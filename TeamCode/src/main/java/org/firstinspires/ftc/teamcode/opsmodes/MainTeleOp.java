package org.firstinspires.ftc.teamcode.opsmodes;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.linearOpMode;

import com.fasterxml.jackson.databind.node.TextNode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.subsystems.Arm;
import org.firstinspires.ftc.teamcode.subsystems.Claw;

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

    private void initHardware() {
        leftFrontDrive = hardwareMap.get(DcMotorEx.class,"leftFront");
        rightFrontDrive = hardwareMap.get(DcMotorEx.class, "rightFront");
        leftBackDrive = hardwareMap.get(DcMotorEx.class, "leftRear");
        rightBackDrive = hardwareMap.get(DcMotorEx.class, "rightRear");

        leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

        arm = new Arm(hardwareMap);

        claw = new Claw(hardwareMap);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initHardware();

        arm.pitchGoToPitchSeek();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()){
            updateArm();
            updateClaw();
            updateSlide();
            updateDriveMotors();
        }
    }

    boolean extend = false, lf = false;
    private void updateSlide(){
        //if(gamepad2.left_stick_y != 0.0)
            arm.armAppendDist((int) -(gamepad2.left_stick_y * 1.7f));
        //else{
        //    if(arm.getTargetPos() - 10 < arm.getArmPos() && arm.getArmPos() < arm.getTargetPos() + 10){
        //        arm.zeroSlidePower();
        //    }
        //}
        if(gamepad2.right_bumper && !lf){
            lf = true;
            if(extend){
                arm.setArmPos(Arm.SLIDE_MAX);
                extend = false;
            }else{
                arm.setArmPos(Arm.SLIDE_MIN);
                extend = true;
            }
        }else if(!gamepad2.right_bumper){
            lf = false;
        }
    }

    private void updateArm(){
        if(gamepad2.left_bumper && !armChangerHeld){
            armChangerHeld = true;
            switch(arm.getArmState()){
                case TOP:
                    arm.pitchGoToPitchSeek();
                    break;
                case MID:
                    arm.pitchGrabSeek();
                    break;
                case BOT:
                    arm.pitchGoToGrab();
                    break;
            }
        }else if(!gamepad2.left_bumper){
            armChangerHeld = false;
        }

        arm.pitchAppend(gamepad2.right_stick_y / 10000);

        if(gamepad2.a){
            arm.pitchAppend(0.01f);
        }else if(gamepad2.y){
            arm.pitchAppend(-0.01f);
        }
    }

    private void updateClaw(){
        if(gamepad2.x && !clawChangerHeld){
            clawChangerHeld = true;
            switch(claw.getClawState()){
                case OPEN:
                    claw.closeClaw();
                    break;
                case CLOSE:
                    claw.openClaw();
                    break;
            }

        } else if(!gamepad2.x){
            clawChangerHeld = false;
        }

    }

    private void updateDriveMotors() {
        double max;
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

        if (gamepad1.left_bumper)
            yaw += 0.1 * -1;
        if (gamepad1.right_bumper)
            yaw -= 0.1 * -1;

        double leftFrontPower = axial + lateral + yaw;
        double rightFrontPower = axial - lateral - yaw;
        double leftBackPower = axial - lateral + yaw;
        double rightBackPower = axial + lateral - yaw;

        max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(leftBackPower));

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);

    }

}
