package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Arm {
    private static final String HARDWARE_NAME = "servoArm";

    private static final float POSITION_GROUND = 0;
    private static final float POSITION_BUCKET = 1;

    private final Servo servo;

    public Arm(HardwareMap hardwareMap) {
        this.servo = hardwareMap.get(Servo.class, HARDWARE_NAME);
    }

    public void moveToGround() {
        servo.setPosition(POSITION_GROUND);
    }

    public void moveToBucket() {
        servo.setPosition(POSITION_BUCKET);
    }
}
