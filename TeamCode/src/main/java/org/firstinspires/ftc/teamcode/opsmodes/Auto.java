package org.firstinspires.ftc.teamcode.opsmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.pipelines.AutoPipeLine;
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.Arm;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Marker;
import org.firstinspires.ftc.teamcode.subsystems.OpenCVManager;
import org.firstinspires.ftc.teamcode.subsystems.RoadRunnerHelper;
import org.firstinspires.ftc.teamcode.tests.AutoColorPos;
import org.opencv.core.Point;

///@Autonomous(group="drive", name="Autonomous DONT CLICK THIS")
public class Auto extends LinearOpMode {
    private static final double TARGET_X = 320;
    private static final double TARGET_Y = 240;
    private static final int ARM_DOWN_CONST = 5;
    private OpenCVManager cam;
    private Arm arm;
    private Claw claw;

    private enum AutoState {
        GO_TO_TANK,
        SEEK,
        ALIGN_X,
        ALIGN_Y,
        GRAB
    }

    private AutoState autoState = AutoState.GO_TO_TANK;

    AutoPipeLine autoPipeLine;

    RoadRunnerHelper roadRunnerHelper;

    int posX, posY;


    @Override
    public void runOpMode() throws InterruptedException {
        arm = new Arm(hardwareMap);
        claw = new Claw(hardwareMap);
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        roadRunnerHelper = new RoadRunnerHelper(drive);

        cam = new OpenCVManager(hardwareMap);

        autoPipeLine = new AutoPipeLine(Marker.RED, new Point(TARGET_X, TARGET_Y));
        cam.setPipeline(autoPipeLine);


        while (!isStarted()) {



        }
        while(!isStopRequested()) {
            switch (autoState) {
                case GO_TO_TANK:
                    roadRunnerHelper.forward(RoadRunnerHelper.TILE_SIZE_IN * 1.5)
                            .strafeLeft(RoadRunnerHelper.TILE_SIZE_IN)
                            .forward(RoadRunnerHelper.TILE_SIZE_IN)
                            .turn(90)
                            .forward(RoadRunnerHelper.TILE_SIZE_IN / 2);
                    arm.setArmPos(Arm.SLIDE_MAX / 2);
                    autoState = AutoState.SEEK;
                    break;
                case SEEK:
                     posX = autoPipeLine.getX();
                     posY = autoPipeLine.getY();
                     autoPipeLine.lockOnPoint(new Point(posX, posY));
                     autoState = AutoState.ALIGN_X;
                    break;
                case ALIGN_X:
                    Point newLock = autoPipeLine.getClosestToLockOn();
                    Point alX = new Point(newLock.x , 0);
                    double dist = AutoPipeLine.getDist(alX, new Point(TARGET_X, 0));
                    if(dist < 10.0){
                        autoState = AutoState.ALIGN_Y;
                    }else{
                        if(TARGET_X > alX.x){
                            roadRunnerHelper.strafeLeft(0.5);
                        }else if(TARGET_X < alX.x){
                            roadRunnerHelper.strafeLeft(-0.5);
                        }
                        autoPipeLine.lockOnPoint(newLock);
                    }
                    break;
                case ALIGN_Y:
                    Point newLockY = autoPipeLine.getClosestToLockOn();
                    Point alY = new Point(0 , newLockY.y);
                    double distY = AutoPipeLine.getDist(alY, new Point(0, TARGET_Y));
                    if(distY < 10.0){
                        autoState = AutoState.GRAB;
                    }else{
                        if(TARGET_X > alY.y){
                            roadRunnerHelper.forward(0.5);
                        }else if(TARGET_X < alY.y){
                            roadRunnerHelper.reverse(-0.5);
                        }
                        autoPipeLine.lockOnPoint(newLockY);
                    }
                    break;
                case GRAB:
                    arm.armAppendDist(ARM_DOWN_CONST);
                    claw.openClaw();
                    arm.pitchGoToGrab();
                    claw.closeClaw();
                    arm.pitchGoToPitchSeek();
                    break;
            }
        }
    }
}
