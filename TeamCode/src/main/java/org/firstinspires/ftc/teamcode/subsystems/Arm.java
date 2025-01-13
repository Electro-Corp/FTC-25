package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Arm {
    public static final String HARDWARE_NAME_SLIDE = "ArmSlide";
    public static final String HARDWARE_NAME_LEFTPITCHSERVO = "LeftPitchServo";
    public static final String HARDWARE_NAME_RIGHTPITCHSERVO = "RightPitchServo";
    // Claw def goes here later

    private final DcMotorEx armExtender;
    private final Servo pitchLeft;
    private final Servo pitchRight;

    public int MAX_HORIZONTAL_EXTENSION = 5360 ;
    public double ARM_HORIZONTAL_MAX = 0.224499;
    private static final float POSITION_GROUND = 0;
    private static final float POSITION_BUCKET = 1;

    private int armPos;

    private final double pitchStop = 0.423;
    private final double pitchWallGrab = 0.661;
    private final double pitchSeek = 0.375;
    private final double pitchGrabSeek = 0.72;//0.23;

    private final double pitchInit = 0.7845;
    private final double pitchGrab = 0.5;

    // Auto Pos for Clipon
    private final double pitchClipPos = 0.5735;
    private final int slidePosClipOn = 5219;

    // Auto pos for Bucket

    private final double pitchBucketPos = 0.456;
    //private final int slidePosBucket;

    public static final int SLIDE_MIN = 0;
    public static final int SLIDE_MAX = 7900;
    public static final int slideWallGrab = 5326;
    public static final int slideBucketPos = 6657;

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

        this.pitchLeft = hardwareMap.get(Servo.class, HARDWARE_NAME_LEFTPITCHSERVO);
        this.pitchRight = hardwareMap.get(Servo.class, HARDWARE_NAME_RIGHTPITCHSERVO);

        setArmPos(SLIDE_MIN);
        armPos = SLIDE_MIN;

        pitchSet(pitchInit);
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
        if(pos > pitchStop){
            if(pos < ARM_HORIZONTAL_MAX){
                if(armPos > MAX_HORIZONTAL_EXTENSION)
                    setArmPos(MAX_HORIZONTAL_EXTENSION);
            }
            pitchLeft.setPosition(pos);
            pitchRight.setPosition(pos);
        }
    }

    public void pitchGoToPitchSeek(){
        armState = ArmState.MID;
        pitchPos = pitchGrabSeek;
        pitchSet(pitchGrabSeek);
    }

    public void pitchGrabSeek(){
        armState = ArmState.BOT;
        pitchPos = pitchGrabSeek;
        pitchSet(pitchGrabSeek);
    }

    public void pitchGoToGrab(){
        armState = ArmState.TOP;
        pitchPos = pitchGrab;
        pitchSet(pitchGrab);
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
        setArmPos(slidePosClipOn, 0.7);
    }

    public void clipOn(double speed){
        pitchPos = pitchClipPos;
        pitchSet(pitchPos);


        armPos = slidePosClipOn;
        setArmPos(slidePosClipOn, speed);
    }

    public void moveToGround() {
        pitchLeft.setPosition(POSITION_GROUND);
    }

    public void moveToBucket() {
        //upDown.setPosition(POSITION_BUCKET);
        pitchPos = pitchBucketPos;
        pitchSet(pitchPos);


        setArmPos(slideBucketPos, 0.7);
    }

    public ArmState getArmState(){
        return armState;
    }

    public void setArmPos(int x) {
        setArmPos(x, 0.7);
    }

    public void setArmPosFast(int x){setArmPos(x, 1.0);}

    public void setArmPos(int x, double speed) {
        if(x > MAX_HORIZONTAL_EXTENSION && pitchPos > ARM_HORIZONTAL_MAX && armPos < x){
            return;
        }

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
        if(armPos + dist > MAX_HORIZONTAL_EXTENSION && pitchPos > ARM_HORIZONTAL_MAX && (armPos + dist) > armPos){
            return;
        }
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
