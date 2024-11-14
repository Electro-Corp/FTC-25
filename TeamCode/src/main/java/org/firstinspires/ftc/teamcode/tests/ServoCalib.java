package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

//import org.firstinspires.ftc.teamcode.objects.Robot;
import org.firstinspires.ftc.teamcode.subsystems.Arm;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
//import org.firstinspires.ftc.teamcode.subsystems.Claw;

@TeleOp(name="Servo Calib")
public class ServoCalib extends LinearOpMode {
    //Robot robot;

    // Float
    float wristPos = 0.45f, clawPos = 0.0f, doorPos = 0.0f;
    int servoNumber = 2;
    Servo clawServo;
    Servo wristServo;
    Servo pitchSero;

    boolean changeDown = false;

    float diff = 0.0005f;

    @Override
    public void runOpMode() throws InterruptedException {
        pitchSero = this.hardwareMap.get(Servo.class, Arm.HARDWARE_NAME_PITCHSERVO);
        clawServo  = this.hardwareMap.get(Servo.class, Claw.SERVO_CLAW);
        //bucketServo = this.hardwareMap.get(Servo.class, Bucket.HARDWARE_NAME);
        wristServo = this.hardwareMap.get(Servo.class, Claw.SERVO_WRIST);

        waitForStart();


        //robot = new Robot(Auto_RED_FAR_SIDE.StartPos.BACKSTAGE, hardwareMap);
        while (opModeIsActive()) {

            // Switch type
            if (gamepad1.dpad_right) {
                if (changeDown == false) {
                    servoNumber++;
                    if (servoNumber > 2)
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
            if(doorPos < 0.0f)doorPos = 0.0f;
            if(doorPos > 1.0f)doorPos = 1.0f;


            telemetry.addData("WRIST", "%f", wristPos);
            telemetry.addData("CLAW", "%f", clawPos);
            telemetry.addData("DOOR", "%f", doorPos);
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
                    if (gamepad1.dpad_up) doorPos += diff;
                    if (gamepad1.dpad_down) doorPos -= diff;
                    pitchSero.setPosition(doorPos);
                    telemetry.addLine("CURRENT IS ARM");
                    break;

            }
            telemetry.update();
        }
    }
}
