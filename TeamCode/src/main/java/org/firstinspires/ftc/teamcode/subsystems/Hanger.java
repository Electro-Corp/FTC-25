package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Hanger {
    private DcMotorEx stage1Motor = null;
    private Servo littleArmThing = null;

    public static final String HARDWARE_NAME_LITTLEARMHANG = "littleArmHang";
    public static final String HARDWARE_NAME_STAGE1HANG = "stage1Hang";
    public static final int stage1MotorMax = 3477;
    public static final int stage1MotorMin = 0;
    public static final int stage1MotorEngaged = 2455;
    public static final double littleArmRetracted = 0.6825;
    public static final double littleArmDeployed = 0.3735;

    public Hanger(HardwareMap hMap){
        stage1Motor = hMap.get(DcMotorEx.class, HARDWARE_NAME_STAGE1HANG);
        littleArmThing = hMap.get(Servo.class, HARDWARE_NAME_LITTLEARMHANG);

        stage1Motor.setTargetPosition(0);
        stage1Motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        stage1Motor.setPower(1.0);

        retractLittleArm();
    }

    public void setLittleArmPos(double pos){
        if(pos < 0.0f) pos = 0.0f;
        if(pos > 1.0f) pos = 1.0f;
        littleArmThing.setPosition(pos);
    }

    public void retractLittleArm() {
        setLittleArmPos(littleArmRetracted);
    }

    public void deployLittleArm() {
        setLittleArmPos(littleArmDeployed);
    }

    public void setHangerPos(int pos){
        if (pos > stage1MotorMax) pos = stage1MotorMax;
        if (pos < stage1MotorMin) pos = stage1MotorMin;
        stage1Motor.setTargetPosition(pos);
    }

    public void appendHangerDist(int dist) {
        int pos = stage1Motor.getCurrentPosition() + dist;
        setHangerPos(pos);
    }


}
