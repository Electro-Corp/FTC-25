package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

//import org.firstinspires.ftc.teamcode.objects.Robot;
import org.firstinspires.ftc.teamcode.subsystems.Arm;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Hanger;
//import org.firstinspires.ftc.teamcode.subsystems.Claw;

@TeleOp(name="Servo Calib")
public class ServoCalib extends LinearOpMode {
    //Robot robot;

    // Float
    float wristPos = 0.45f, clawPos = 0.0f, leftPitchPos = 0.421f, rightPitchPos = 0.5f, littleArmPos = 0.5f;
    int servoNumber = 2;
    Servo clawServo;
    Servo wristServo;
    Servo pitchServoLeft;
    Servo pitchServoRight;
    Servo littleArmThing;

    boolean changeDown = false;

    float diff = 0.0005f;

    @Override
    public void runOpMode() throws InterruptedException {
        pitchServoLeft = this.hardwareMap.get(Servo.class, Arm.HARDWARE_NAME_LEFTPITCHSERVO);
        pitchServoRight = this.hardwareMap.get(Servo.class, Arm.HARDWARE_NAME_RIGHTPITCHSERVO);
        clawServo = this.hardwareMap.get(Servo.class, Claw.SERVO_CLAW);
        //bucketServo = this.hardwareMap.get(Servo.class, Bucket.HARDWARE_NAME);
        wristServo = this.hardwareMap.get(Servo.class, Claw.SERVO_WRIST);
        littleArmThing = this.hardwareMap.get(Servo.class, Hanger.HARDWARE_NAME_LITTLEARMHANG);

        pitchServoRight.setPosition(rightPitchPos);
        pitchServoLeft.setPosition(leftPitchPos);
        clawServo.setPosition(clawPos);
        wristServo.setPosition(wristPos);
        littleArmThing.setPosition(littleArmPos);

        waitForStart();

        //robot = new Robot(Auto_RED_FAR_SIDE.StartPos.BACKSTAGE, hardwareMap);
        while (opModeIsActive()) {

            // Switch type
            if (gamepad1.dpad_right) {
                if (changeDown == false) {
                    servoNumber++;
                    if (servoNumber > 4)
                        servoNumber = 0;
                }
                changeDown = true;
            } else {
                changeDown = false;
            }

            if(clawPos < 0.0f)clawPos = 0.0f;
            if(clawPos > 1.0f)clawPos = 1.0f;
            /*if(bucketPos < 0.0f)bucketPos = 0.0f;
            if(bucketPos > 1.0f)bucketPos = 1.0f;

             */
            if(leftPitchPos < 0.0f) leftPitchPos = 0.0f;
            if(leftPitchPos > 1.0f) leftPitchPos = 1.0f;

            if(rightPitchPos < 0.0f) rightPitchPos = 0.0f;
            if(rightPitchPos > 1.0f) rightPitchPos = 1.0f;

            if(littleArmPos < 0.0f) littleArmPos = 0.0f;
            if(littleArmPos > 1.0f) littleArmPos = 1.0f;


            telemetry.addData("WRIST", "%f", wristPos);
            telemetry.addData("CLAW", "%f", clawPos);
            telemetry.addData("LEFT PITCH", "%f", leftPitchPos);
            telemetry.addData("RIGHT PITCH", "%f", rightPitchPos);
            telemetry.addData("LITTLE ARM", "%f", littleArmPos);
            telemetry.addLine("=======================");
            switch (servoNumber) {
                case 0: //WRIST
                    if (gamepad1.dpad_up) wristPos += diff;
                    if (gamepad1.dpad_down) wristPos -= diff;
                    wristServo.setPosition(wristPos);
                    telemetry.addLine("CURRENT IS WRIST");
                    break;
                case 1:
                    if (gamepad1.dpad_up) clawPos += diff;
                    if (gamepad1.dpad_down) clawPos -= diff;
                    clawServo.setPosition(clawPos);
                    telemetry.addLine("CURRENT IS CLAW");
                    break;
                case 2:
                    if (gamepad1.dpad_up) leftPitchPos += diff;
                    if (gamepad1.dpad_down) leftPitchPos -= diff;
                    pitchServoLeft.setPosition(leftPitchPos);
                    telemetry.addLine("CURRENT IS LEFT PITCH");
                    break;
                case 3:
                    if (gamepad1.dpad_up) rightPitchPos += diff;
                    if (gamepad1.dpad_down) rightPitchPos -= diff;
                    pitchServoRight.setPosition(rightPitchPos);
                    telemetry.addLine("CURRENT IS RIGHT PITCH");
                    break;
                case 4:
                    if (gamepad1.dpad_up) littleArmPos += diff;
                    if (gamepad1.dpad_down) littleArmPos -= diff;
                    littleArmThing.setPosition(littleArmPos);
                    telemetry.addLine("CURRENT IS LITTLE ARM");
                    break;
            }
            telemetry.update();
        }
    }
}
