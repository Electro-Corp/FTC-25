package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Claw {

    private static final String SERVO_WRIST = "servoWrist";
    private static final String SERVO_CLAW = "servoClaw";

    private static final float POSITION_CLAW_OPENED = 1;
    private static final float POSITION_CLAW_CLOSED = 0;
    private static final float POSITION_WRIST_CENTERED = .5f;

    private final Servo servoWrist;
    private final Servo servoClaw;

    public Claw(HardwareMap hardwareMap){
        this.servoWrist = hardwareMap.get(Servo.class, SERVO_WRIST);
        this.servoClaw = hardwareMap.get(Servo.class, SERVO_CLAW);
    }

    public void openClaw(){
        servoClaw.setPosition(POSITION_CLAW_OPENED);
    }

    public void closeClaw(){
        servoClaw.setPosition(POSITION_CLAW_CLOSED);
    }

    public void centerWrist(){
        servoWrist.setPosition(POSITION_WRIST_CENTERED);
    }

    public void turnWrist(float position){
        servoWrist.setPosition((position+1)/2.0);
    }

}
