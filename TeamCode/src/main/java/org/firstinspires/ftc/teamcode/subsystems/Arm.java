package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
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


    private final double PitchStop = 0.197;
    private final double PitchSeek = 0.4;
    private final double PitchGrabSeek = 0.20;
    private final double Grab = 0.5;


    public static final int SLIDE_MIN = 0;
    public static final int SLIDE_MAX = 2300;

    private double pitchPos = 0.0f;



    public enum ArmState{
        TOP,
        MID,
        BOT
    }

    private ArmState armState = ArmState.TOP;


    public Arm(HardwareMap hardwareMap) {
        this.armExtender = hardwareMap.get(DcMotorEx.class, HARDWARE_NAME_SLIDE);

        //armExtender.setDirection(DcMotorSimple.Direction.REVERSE);
        armExtender.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armPos = armExtender.getCurrentPosition();

        this.upDown = hardwareMap.get(Servo.class, HARDWARE_NAME_PITCHSERVO);

        setArmPos(SLIDE_MIN);
        armPos = SLIDE_MIN;

        pitchGoToPitchSeek();

    }

    public void pitchAppend(float pos) {
        pitchPos += pos;
        pitchSet(pitchPos);
    }

    public void pitchSet(double pos){
        if(pos > PitchStop){
            upDown.setPosition(pos);
        }
    }

    public void pitchGoToPitchSeek(){
        armState = ArmState.MID;
        pitchPos = PitchGrabSeek;
        pitchSet(PitchGrabSeek);
    }

    public void pitchGrabSeek(){
        armState = ArmState.BOT;
        pitchPos = PitchSeek;
        pitchSet(PitchSeek);
    }

    public void pitchGoToGrab(){
        armState = ArmState.TOP;
        pitchPos = Grab;
        pitchSet(Grab);
    }

    public void moveToGround() {
        upDown.setPosition(POSITION_GROUND);
    }

    public void moveToBucket() {
        upDown.setPosition(POSITION_BUCKET);
    }

    public ArmState getArmState(){
        return armState;
    }

    public void setArmPos(int x) {
        armPos = x;
        armExtender.setTargetPosition(x);
        armExtender.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armExtender.setPower(0.7);
    }

    public int getArmPos(){
        return armExtender.getCurrentPosition();
    }

    public int getTargetPos(){
        return armPos;
    }

    public void armAppendDist(int dist){
        armPos += dist;
        if(armPos > SLIDE_MAX){
            armPos = SLIDE_MAX;
        }else if(armPos < SLIDE_MIN){
            armPos = SLIDE_MIN;
        }
        setArmPos(armPos);
    }

    public void zeroSlidePower(){
        armExtender.setPower(0.0);
    }

}
