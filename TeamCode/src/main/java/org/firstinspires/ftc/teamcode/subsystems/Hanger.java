package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Hanger {
    private DcMotorEx stage1Motor = null;
    private Servo littleArmThing = null;

    public Hanger(HardwareMap hMap){
        stage1Motor = hMap.get(DcMotorEx.class, "stage1Hang");
        littleArmThing = hMap.get(Servo.class, "littleArmHang");

        stage1Motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        stage1Motor.setPower(1.0);
    }

    public void setLittleArmPos(double pos){
        littleArmThing.setPosition(pos);
    }

    public void setStage1MotorPos(int pos){
        stage1Motor.setTargetPosition(pos);
    }


}
