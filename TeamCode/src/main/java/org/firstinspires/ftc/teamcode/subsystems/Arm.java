package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Arm {
    public static final String HARDWARE_NAME_SLIDE = "ArmSlide";
    public static final String HARDWARE_NAME_PITCHSERVO = "PitchServo";
    // Claw def goes here later

    DcMotorEx armExtender;
    Servo upDown;

    public Arm(HardwareMap hardwareMap){
        this.armExtender = hardwareMap.get(DcMotorEx.class, HARDWARE_NAME_SLIDE);
        this.upDown = hardwareMap.get(Servo.class, HARDWARE_NAME_PITCHSERVO);


    }

    public void armForward(int x){
        armExtender.setTargetPosition(x);
    }
}
