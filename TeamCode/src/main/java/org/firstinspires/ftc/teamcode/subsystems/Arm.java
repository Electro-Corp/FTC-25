package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Arm {
    public static final String HARDWARE_NAME_SLIDE = "ArmSlide";
    public static final String HARDWARE_NAME_PITCHSERVO = "PitchServo";
    // Claw def goes here later

    private final DcMotorEx armExtender;
    private final Servo upDown;



    private static final float POSITION_GROUND = 0;
    private static final float POSITION_BUCKET = 1;

    private int armPos;



    public Arm(HardwareMap hardwareMap) {
        this.armExtender = hardwareMap.get(DcMotorEx.class, HARDWARE_NAME_SLIDE);

        armPos = armExtender.getCurrentPosition();



        this.upDown = hardwareMap.get(Servo.class, HARDWARE_NAME_PITCHSERVO);

    }

    public void moveToGround() {
        upDown.setPosition(POSITION_GROUND);
    }

    public void moveToBucket() {
        upDown.setPosition(POSITION_BUCKET);
    }

    public void setArmPos(int x) {
        armExtender.setTargetPosition(x);
    }

    public void armAppendDist(int dist){
        armPos += dist;
        setArmPos(armPos);
    }

}
