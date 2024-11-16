package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.Claw.ClawState.CLOSE;
import static org.firstinspires.ftc.teamcode.subsystems.Claw.ClawState.OPEN;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Claw {
    public enum ClawState{
        OPEN, CLOSE
    }
    private ClawState clawState = ClawState.CLOSE;

    public static final String SERVO_WRIST = "WristServo";
    public static final String SERVO_CLAW = "ClawServo ";

    private static final float POSITION_CLAW_OPENED = 0.60f;
    private static final float POSITION_CLAW_CLOSED = 0.98f;
    private static final float POSITION_WRIST_CENTERED = .5f;

    private static final float POSITION_WRIST_LEFT = 0.84f;
    private static final float POSITION_WRIST_CENTER = 0.5f;
    private static final float POSITION_WRIST_RIGHT = 0.17f;

    private float currentTargetPos = 0.0f;

    //private final Servo servoWrist;
    private final Servo servoClaw;
    private final Servo servoWrist;

    public Claw(HardwareMap hardwareMap){
        //this.servoWrist = hardwareMap.get(Servo.class, SERVO_WRIST);
        this.servoClaw = hardwareMap.get(Servo.class, SERVO_CLAW);
        this.servoWrist = hardwareMap.get(Servo.class, SERVO_WRIST);

        wristCenter();

    }

    private void setClawPos(float pos){
        servoClaw.setPosition(pos);
    }

    private void setWristPos(float pos){
        servoWrist.setPosition(pos);
    }

    public void wristLeft(){
        setWristPos(POSITION_WRIST_LEFT);
    }

    public void wristRight(){
        setWristPos(POSITION_WRIST_RIGHT);
    }

    private void wristCenter(){
        setWristPos(POSITION_WRIST_CENTER);
    }

    public void openClaw(){
        if(clawState == OPEN) {
            currentTargetPos = POSITION_CLAW_CLOSED;
            setClawPos(currentTargetPos);
            clawState = CLOSE;
        }else {
            currentTargetPos = POSITION_CLAW_OPENED;
            setClawPos(currentTargetPos);
            clawState = OPEN;
        }
    }

    public void openTeleOp(){
        currentTargetPos = POSITION_CLAW_CLOSED;
        setClawPos(currentTargetPos);
        clawState = CLOSE;
    }

    public void closeTheClaw(){
        currentTargetPos = POSITION_CLAW_CLOSED;
        setClawPos(currentTargetPos);
        clawState = CLOSE;
    }

    public void appendDist(float dist){
        currentTargetPos += dist;
        setClawPos(currentTargetPos);
    }

    public ClawState getClawState(){
        return clawState;
    }

    //public void centerWrist(){
        //servoWrist.setPosition(POSITION_WRIST_CENTERED);
    //}

    //public void turnWrist(float position){
       // servoWrist.setPosition((position+1)/2.0);
    //}

}
