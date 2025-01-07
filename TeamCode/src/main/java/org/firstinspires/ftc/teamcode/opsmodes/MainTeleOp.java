package org.firstinspires.ftc.teamcode.opsmodes;
import static org.firstinspires.ftc.teamcode.subsystems.Arm.SLIDE_MIN;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.pipelines.AutoPipeLine;
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.Arm;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Hanger;
import org.firstinspires.ftc.teamcode.subsystems.Marker;
import org.firstinspires.ftc.teamcode.subsystems.OpenCVManager;
import org.firstinspires.ftc.teamcode.subsystems.RoadRunnerHelper;
import org.opencv.core.Point;

@TeleOp(name= "TeleOp")
public class MainTeleOp extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();

    private DcMotorEx leftFrontDrive = null;
    private DcMotorEx rightFrontDrive = null;
    private DcMotorEx leftBackDrive = null;
    private DcMotorEx rightBackDrive = null;

    private Arm arm;
    private Claw claw;
    private Hanger hanger;

    private boolean armChangerHeld = false;
    private boolean clawChangerHeld = false;
    private boolean littleArmDeployed = false;

    RoadRunnerHelper roadRunnerHelper;
    SampleMecanumDrive mecanumDrive;
    OpenCVManager cam;
    AutoPipeLine autoPipeLine;

    private static final double TARGET_X = 320;
    private static final double TARGET_Y = 240;

    private static final double ITS_OK_TOLERANCE = 0.0005;

    boolean assistDriver = false;

    private enum AutoState {
        SEEK,
        ALIGN_X,
        ALIGN_Y
    }

    private AutoState currentAssistStage = AutoState.SEEK;

    int posX, posY;

    private void initHardware() {
        leftFrontDrive = hardwareMap.get(DcMotorEx.class,"leftFront");
        rightFrontDrive = hardwareMap.get(DcMotorEx.class, "rightFront");
        leftBackDrive = hardwareMap.get(DcMotorEx.class, "leftRear");
        rightBackDrive = hardwareMap.get(DcMotorEx.class, "rightRear");

        leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

        mecanumDrive = new SampleMecanumDrive(hardwareMap);

        roadRunnerHelper = new RoadRunnerHelper(mecanumDrive);

        cam = new OpenCVManager(hardwareMap);

        autoPipeLine = new AutoPipeLine(Marker.YELLOW, new Point(TARGET_X, TARGET_Y));
        cam.setPipeline(autoPipeLine);

        arm = new Arm(hardwareMap);

        claw = new Claw(hardwareMap);

        hanger = new Hanger(hardwareMap);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initHardware();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()){
            updateHanger();
            updateArm();
            updateClaw();
            updateDriveMotors();

            telemetry.addData("POSX", autoPipeLine.getX());
            telemetry.addData("POSY", autoPipeLine.getY());

            if(!assistDriver) {
                updateHanger();
                updateArm();
                updateClaw();
                updateDriveMotors();
            }else{
                switch(currentAssistStage){
                    case SEEK:
                        telemetry.addLine("==== CHOOSING PIECE TO LOCK ON TO ====");
                        posX = autoPipeLine.getX();
                        posY = autoPipeLine.getY();
                        autoPipeLine.lockOnPoint(new Point(posX, posY));
                        currentAssistStage = AutoState.ALIGN_X;
                        break;
                    case ALIGN_X:
                        telemetry.addLine("=== SPLINE TO ===");
                        Point lockON = new Point(autoPipeLine.getX(), autoPipeLine.getY());
                        double dist = AutoPipeLine.getDist(lockON, new Point(TARGET_X, TARGET_Y));
                        if(dist < ITS_OK_TOLERANCE){
                            claw.openClaw();
                            arm.pitchGoToPitchSeek();
                            assistDriver = false;
                            currentAssistStage = AutoState.ALIGN_Y;
                        }else {
                            roadRunnerHelper.resetPath();
                            int xConst = 88 * (lockON.x > TARGET_X ? 1 : -1);
                            int yConst = 64 * (lockON.y > TARGET_Y ? -1 : 1);
                            double distX = lockON.x - TARGET_X;
                            double distY = TARGET_Y - lockON.y;
                            roadRunnerHelper.splineToLinearHeading(lockON.x / xConst, lockON.y / yConst, 0);
                        }
                }
            }
            driverAssist();
            telemetry.update();
        }
    }

    boolean assistButtonPressed = false;
    private void driverAssist() {
        if(gamepad1.a && !assistButtonPressed){
            assistButtonPressed = true;
            roadRunnerHelper.resetPath();
            assistDriver = true;
            currentAssistStage = AutoState.SEEK;
        }else if(!gamepad1.a){
            assistButtonPressed = false;
            assistDriver = false;
            currentAssistStage = AutoState.SEEK;
        }
    }

    boolean x1Pressed = false;

    private void updateHanger() {
        if (gamepad1.left_trigger > 0.1) hanger.appendHangerDist((int) -(gamepad1.left_trigger * 2.5f));
        if (gamepad1.right_trigger > 0.1) hanger.appendHangerDist((int) (gamepad1.right_trigger * 2.5f));

        if (!littleArmDeployed && gamepad1.x && !x1Pressed) {
            hanger.deployLittleArm();
            littleArmDeployed = true;
            x1Pressed = true;
        }

        if (littleArmDeployed && gamepad1.x && !x1Pressed) {
            hanger.retractLittleArm();
            littleArmDeployed = false;
            x1Pressed = true;
        }

        if (x1Pressed && !gamepad1.x) {
            x1Pressed = false;
        }
    }

    boolean extend = false, lf = false;
    boolean clipOnYesNo = false;
    boolean yPressed = false, aPressed = false;
    boolean extendSlide = false, fish = false;
    boolean bucketYesNo = false;

    private void updateArm(){
        arm.armAppendDist((int) -(gamepad2.right_stick_y * 2.5f));

        if(gamepad2.right_bumper && !lf) {
            lf = true;
            arm.clipOn();
        } else if(!gamepad2.right_bumper) {
            lf = false;
        }

       if(gamepad2.left_bumper && !armChangerHeld){
            armChangerHeld = true;
            arm.moveToBucket();
        } else if(!gamepad2.left_bumper) {
            armChangerHeld = false;
        }

        if(gamepad2.y && !yPressed){
            yPressed = true;
            arm.wallGrab();
            claw.wristRight();
            claw.openTeleOp();
        } else if(!gamepad2.y) {
            yPressed = false;
        }

        if(gamepad2.a && !aPressed){
            aPressed = true;
            if(!fish) {
                fish = true;
                arm.pitchGrabSeek();
            }else{
                fish = false;
                arm.pitchGoToPitchSeek();
            }

            if(!extendSlide) {
                arm.setArmPos(arm.SLIDE_MAX / 2);
                extendSlide = true;
            } else {
                arm.setArmPos(0);
                extendSlide = false;
            }
        }else if(!gamepad2.a){
            aPressed = false;
        }

        arm.pitchAppend(gamepad2.left_stick_y / 7000);

        /*if(gamepad2.a){
            arm.pitchAppend(0.01f);
        }else if(gamepad2.y){
            arm.pitchAppend(-0.01f);
        }*/

    }

    boolean yPressed2 = false;
    boolean bNotHeld = false;

    private void updateClaw(){
        if(gamepad2.b && !bNotHeld){
            bNotHeld = true;
            claw.toggleWrist();
        }else if(!gamepad2.b){
            bNotHeld = false;
        }

        claw.appendWristPos(gamepad2.right_trigger / 400);
        claw.appendWristPos(-gamepad2.left_trigger / 400);

        if(gamepad2.x && !clawChangerHeld){
            clawChangerHeld = true;
            switch(claw.getClawState()){
                case OPEN:
                    claw.closeTheClaw();
                    break;
                case CLOSE:
                    claw.openTeleOp();
                    break;
            }

        } else if(!gamepad2.x){
            clawChangerHeld = false;
        }
    }

    private void updateDriveMotors() {
        double max = 0.0;
        double axial = 0;
        double lateral = 0;
        double yaw = 0;

        if (Math.abs(gamepad1.left_stick_y) > 0.1)
            axial = -gamepad1.left_stick_y;
        if (Math.abs(gamepad1.left_stick_x) > 0.1)
            lateral = gamepad1.left_stick_x;
        if (Math.abs(gamepad1.right_stick_x) > 0.1)
            yaw = gamepad1.right_stick_x;

        if (gamepad1.dpad_up || gamepad2.dpad_up)
            axial += 0.3;
        if (gamepad1.dpad_down || gamepad2.dpad_down)
            axial -= 0.3;
        if (gamepad1.dpad_left || gamepad2.dpad_left)
            lateral -= 0.3;
        if (gamepad1.dpad_right || gamepad2.dpad_right)
            lateral += 0.3;

        if (gamepad1.right_bumper)
            yaw -= 0.3 * -1;
        if (gamepad1.left_bumper) {
            yaw += 0.3 * -1;
        }

        double leftFrontPower = axial + lateral + yaw;
        double rightFrontPower = axial - lateral - yaw;
        double leftBackPower = axial - lateral + yaw;
        double rightBackPower = axial + lateral - yaw;

        max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        telemetry.addData("LF", leftFrontPower);
        telemetry.addData("RF", rightFrontPower);
        telemetry.addData("LB", leftBackPower);
        telemetry.addData("RF", rightBackPower);


        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);

    }

}
