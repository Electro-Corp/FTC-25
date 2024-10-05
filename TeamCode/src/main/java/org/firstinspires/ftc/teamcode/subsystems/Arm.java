package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Arm {
    public static final String HARDWARE_NAME_SLIDE = "ArmSlide";
    public static final String HARDWARE_NAME_PITCHSERVO = "PitchServo";
    private static final String HARDWARE_NAME = "servoArm";
    // Claw def goes here later

    private final DcMotorEx armExtender;
    private final Servo upDown;
    private final Servo servo;



    private static final float POSITION_GROUND = 0;
    private static final float POSITION_BUCKET = 1;



    public Arm(HardwareMap hardwareMap) {
        this.servo = hardwareMap.get(Servo.class, HARDWARE_NAME);
        this.armExtender = hardwareMap.get(DcMotorEx.class, HARDWARE_NAME_SLIDE);
        this.upDown = hardwareMap.get(Servo.class, HARDWARE_NAME_PITCHSERVO);

    }

    public void moveToGround() {
        servo.setPosition(POSITION_GROUND);
    }

    public void moveToBucket() {
        servo.setPosition(POSITION_BUCKET);
    }

    public void armForward(int x) {
        armExtender.setTargetPosition(x);
    }
}
