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

    private static final String SERVO_WRIST = "servoWrist";
    public static final String SERVO_CLAW = "ClawServo ";

    private static final float POSITION_CLAW_OPENED = 0.636f;
    private static final float POSITION_CLAW_CLOSED = 0.863f;
    private static final float POSITION_WRIST_CENTERED = .5f;

    private float currentTargetPos = 0.0f;

    //private final Servo servoWrist;
    private final Servo servoClaw;

    public Claw(HardwareMap hardwareMap){
        //this.servoWrist = hardwareMap.get(Servo.class, SERVO_WRIST);
        this.servoClaw = hardwareMap.get(Servo.class, SERVO_CLAW);

    }

    private void setClawPos(float pos){

        servoClaw.setPosition(pos);
    }

    public void openClaw(){
        currentTargetPos = POSITION_CLAW_OPENED;
        setClawPos(currentTargetPos);
        clawState = OPEN;
    }

    public void closeClaw(){
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
