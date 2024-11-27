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


    private final double PitchStop = 0.07;
    private final double pitchWallGrab = 0.308;
    private final double PitchSeek = 0.35;
    private final double PitchGrabSeek = 0.35;//0.23;
    private final double Grab = 0.5;

    // Auto Pos for Clipon
    private final double pitchClipPos = 0.225;
    private final int slidePosClipOn = 1988;

    // Auto pos for Bucket

    private final double pitchBucketPos = 0.087;
    //private final int slidePosBucket;


    public static final int SLIDE_MIN = 0;
    public static final int SLIDE_MAX = 2600;
    public static final int slideWallGrab = 2200;

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



        pitchGrabSeek();

    }

    public void pitchAppend(double pos) {
        pitchPos += pos;
        pitchSet(pitchPos);
    }

    // Bascially only for JBBFI
    public void pitchAppendNeg(double pos) {
        pitchPos -= pos;
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

    public void wallGrab() {
        pitchPos = pitchWallGrab;
        pitchSet(pitchPos);

        armPos = slideWallGrab;
        setArmPos(armPos);
    }

    public void clipOn(){
        pitchPos = pitchClipPos;
        pitchSet(pitchPos);


        armPos = slidePosClipOn;
        setArmPos(slidePosClipOn, 0.3);
    }

    public void clipOn(double speed){
        pitchPos = pitchClipPos;
        pitchSet(pitchPos);


        armPos = slidePosClipOn;
        setArmPos(slidePosClipOn, speed);
    }

    public void moveToGround() {
        upDown.setPosition(POSITION_GROUND);
    }

    public void moveToBucket() {
        //upDown.setPosition(POSITION_BUCKET);
        pitchPos = pitchBucketPos;
        pitchSet(pitchPos);


        setArmPos(SLIDE_MAX, 0.3);
    }

    public ArmState getArmState(){
        return armState;
    }

    public void setArmPos(int x) {
        setArmPos(x, 0.7);
    }

    public void setArmPosFast(int x){setArmPos(x, 1.0);}

    public void setArmPos(int x, double speed) {
        armPos = x;
        armExtender.setTargetPosition(x);
        armExtender.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armExtender.setPower(speed);
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
