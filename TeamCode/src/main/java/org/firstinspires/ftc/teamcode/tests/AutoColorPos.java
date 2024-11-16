package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.checkerframework.checker.units.qual.A;
import org.firstinspires.ftc.teamcode.pipelines.AutoPipeLine;
import org.firstinspires.ftc.teamcode.pipelines.CalibratePipeline;
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.Arm;
import org.firstinspires.ftc.teamcode.subsystems.Marker;
import org.firstinspires.ftc.teamcode.subsystems.OpenCVManager;
import org.firstinspires.ftc.teamcode.subsystems.RoadRunnerHelper;
import org.opencv.core.Point;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

@Autonomous(group="drive", name="AUTODRIVE")
public class AutoColorPos extends LinearOpMode  {

    private enum AutoState{
        SEEK,
        GRAB
    }

    private enum GrabProc{
        ARM_LOWER,
        POLL,
        CLAW_OPEN,
        ARM_LOWER_MORE,
        CLAW_CLOSE,
        ARM_UP
    }

    private final int TARGET_X = 320;
    private final int TARGET_Y = 240;

    private final double GRAB_TOLERANCE = 100.0;

    private OpenCVManager cam;
    private Arm arm;

    private AutoState currentState = AutoState.SEEK;
    private GrabProc currentGrabState = GrabProc.ARM_LOWER;

    AutoPipeLine autoPipeLine;

    RoadRunnerHelper roadRunnerHelper;

    @Override
    public void runOpMode() throws InterruptedException {

        arm = new Arm(hardwareMap);
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        roadRunnerHelper = new RoadRunnerHelper(drive);

        cam = new OpenCVManager(hardwareMap);

        autoPipeLine = new AutoPipeLine(Marker.RED, new Point(TARGET_X, TARGET_Y));
        cam.setPipeline(autoPipeLine);

        while(!isStarted()){

            String whereTo = "";

            // Get target's screenspace coords
            int posX = autoPipeLine.getX(), posY = autoPipeLine.getY();
            // Do something with that Ig....
            if(TARGET_Y > posY){
                // Slide arm forwards
                whereTo += "Arm Slide Forwards";
            }else if(TARGET_Y < posY){
                // Slide arm backwards
                whereTo += "Arm Slide backwards";
            }
            whereTo += " | ";

            if(TARGET_X > posX){
                // Rotate claw ->
                whereTo += "Rotate Bot ->";
            }else if(TARGET_X < posX) {
                // Rotate claw <-
                whereTo += "Rotate Bot <-";
            }

            autoPipeLine.setPrevLocText(whereTo);

            telemetry.addLine(whereTo);

            telemetry.update();
        }

        while(!isStopRequested()) {
            String whereTo = "";

            switch(currentState) {
                case SEEK:

                    arm.pitchGoToPitchSeek();

                    telemetry.addLine("Current State: SEEK");



                    // Get target's screenspace coords
                    int posX = autoPipeLine.getX(), posY = autoPipeLine.getY();
                    // Do something with that Ig....
                    if (TARGET_Y > posY) {
                        // Slide arm forwards
                        whereTo += "Arm Slide Forwards";
                        arm.armAppendDist(10);
                    } else if (TARGET_Y < posY) {
                        // Slide arm backwards
                        whereTo += "Arm Slide backwards";
                        arm.armAppendDist(-10);
                    }
                    whereTo += " | ";


                    double distY = AutoPipeLine.getDist(new Point(320, posY), new Point(320, 240));

                    if(distY > 50.0) {

                        if (TARGET_X > posX) {
                            // Rotate claw ->
                            whereTo += "Strafe Bot ->";
                            roadRunnerHelper.strafeLeft(0.1);
                        } else if (TARGET_X < posX) {
                            // Rotate claw <-
                            whereTo += "Strafe Bot <-";
                            roadRunnerHelper.strafeRight(0.1);
                        }
                    }else{
                        whereTo += "No rotation.";
                    }

                    telemetry.addLine(whereTo);

                    // Do something else to grab the distance and then decide whether or not to grab the
                    // ... specimen or whatever the hell its called

                    //                                  these are inefficient, store them as points already in the future
                    double dist = AutoPipeLine.getDist(new Point(320, 240), new Point(posX, posY));

                    telemetry.addLine(Integer.toString(posX));
                    telemetry.addLine(Integer.toString(posY));
                    telemetry.addLine(Double.toString(dist));
                    telemetry.addLine((Integer.toString(arm.getArmPos())));


                    if(dist < GRAB_TOLERANCE){
                        currentState = AutoState.GRAB;
                    }
                    break;

                case GRAB:
                    telemetry.addLine("Current State: GRAB");

                    arm.pitchGrabSeek();

                    // Get target's screenspace coords
                    posY = autoPipeLine.getY();
                    // Do something with that Ig....
                    if (TARGET_Y > posY) {
                        // Slide arm forwards
                        whereTo += "Arm Slide Forwards";
                        arm.armAppendDist(10);
                    } else if (TARGET_Y < posY) {
                        // Slide arm backwards
                        whereTo += "Arm Slide backwards";
                        arm.armAppendDist(-10);
                    }
                    whereTo += " | ";

                    telemetry.addLine(whereTo);

                    /*switch(currentGrabState){
                        case ARM_LOWER:
                            arm.moveToGround();
                            break;
                        case POLL:
                            break;
                    }*/


                    break;
                default:
                    telemetry.addLine("How did we get here?");
            }




            telemetry.update();
        }
    }

}
