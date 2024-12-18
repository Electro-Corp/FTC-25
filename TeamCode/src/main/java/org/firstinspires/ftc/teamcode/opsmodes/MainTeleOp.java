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

    private boolean armChangerHeld = false;
    private boolean clawChangerHeld = false;

    RoadRunnerHelper roadRunnerHelper;
    SampleMecanumDrive mecanumDrive;
    OpenCVManager cam;
    AutoPipeLine autoPipeLine;

    private static final double TARGET_X = 320;
    private static final double TARGET_Y = 240;

    private static final double ITS_OK_TOLERANCE = 50.0;

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

        autoPipeLine = new AutoPipeLine(Marker.RED, new Point(TARGET_X, TARGET_Y));
        cam.setPipeline(autoPipeLine);

        arm = new Arm(hardwareMap);

        claw = new Claw(hardwareMap);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initHardware();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()){
            updateArm();
            updateClaw();
            updateDriveMotors();

            telemetry.addData("POSX", autoPipeLine.getX());
            telemetry.addData("POSY", autoPipeLine.getY());

            if(!assistDriver) {
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
                            arm.pitchGoToGrab();
                            assistDriver = false;
                            currentAssistStage = AutoState.ALIGN_Y;
                        }else {
                            roadRunnerHelper.resetPath();
                            double distX = lockON.x - TARGET_X;
                            double distY = TARGET_Y - lockON.y;
                            roadRunnerHelper.splineToLinearHeading(distX, distY, 0);
                        }

                        /*telemetry.addLine("==== ALIGN ON X ====");
                        Point newLock = new Point(autoPipeLine.getX(), autoPipeLine.getY());//autoPipeLine.getClosestToLockOn();
                        telemetry.addLine("--- LOCK ON POS: ");
                        telemetry.addData("POS X", newLock.x);
                        telemetry.addData("POS Y", newLock.y);
                        Point alX = new Point(newLock.x , 0);
                        double dist = AutoPipeLine.getDist(alX, new Point(TARGET_X, 0));
                        if(dist < ITS_OK_TOLERANCE){
                            currentAssistStage = AutoState.ALIGN_Y;
                        }else{
                            if(TARGET_X < alX.x){
                                telemetry.addLine("Target is greater than our pos, so we're");
                                telemetry.addLine("going to [Strafe Left]");
                                roadRunnerHelper.strafeRight(0.3);
                            }else if(TARGET_X > alX.x){
                                telemetry.addLine("Target is less than our pos, so we're");
                                telemetry.addLine("going to [Strafe Right]");
                                roadRunnerHelper.strafeLeft(0.3);
                            }
                            autoPipeLine.lockOnPoint(newLock);
                        }
                        break;
                    case ALIGN_Y:
                        telemetry.addLine("==== ALIGN ON Y ====");
                        Point newLockY = new Point(autoPipeLine.getX(), autoPipeLine.getY()); //autoPipeLine.getClosestToLockOn();
                        telemetry.addLine("--- LOCK ON POS: ");
                        telemetry.addData("POS X", newLockY.x);
                        telemetry.addData("POS Y", newLockY.y);
                        Point alY = new Point(0 , newLockY.y);
                        double distY = AutoPipeLine.getDist(alY, new Point(0, TARGET_Y));
                        if(distY < ITS_OK_TOLERANCE){
                            claw.openClaw();
                            arm.pitchGoToGrab();
                            assistDriver = false;
                        }else{
                            if(TARGET_Y > alY.y){
                                telemetry.addLine("Target is greater than our pos, so we're");
                                telemetry.addLine("going to [Go Back]");
                                roadRunnerHelper.reverse(0.5);
                            }else if(TARGET_Y < alY.y){
                                telemetry.addLine("Target is less than our pos, so we're");
                                telemetry.addLine("going to [Go Forward]");
                                roadRunnerHelper.forward(-0.5);
                            }
                            autoPipeLine.lockOnPoint(newLockY);
                        }
                        break;*/
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
