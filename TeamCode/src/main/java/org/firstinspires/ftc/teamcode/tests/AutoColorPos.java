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

    private final int TARGET_X = 320;
    private final int TARGET_Y = 240;

    private OpenCVManager cam;
    private Arm arm;

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

            // Get target's screenspace coords
            int posX = autoPipeLine.getX(), posY = autoPipeLine.getY();
            // Do something with that Ig....
            if(TARGET_Y > posY){
                // Slide arm forwards
                whereTo += "Arm Slide Forwards";
                arm.armAppendDist(1);
            }else if(TARGET_Y < posY){
                // Slide arm backwards
                whereTo += "Arm Slide backwards";
                arm.armAppendDist(-1);
            }
            whereTo += " | ";

            if(TARGET_X > posX){
                // Rotate claw ->
                whereTo += "Rotate Bot ->";
                roadRunnerHelper.turn(1f);
            }else if(TARGET_X < posX) {
                // Rotate claw <-
                whereTo += "Rotate Bot <-";
                roadRunnerHelper.turn(-1f);
            }

            autoPipeLine.setPrevLocText(whereTo);

            telemetry.addLine(whereTo);

            telemetry.update();

            // Do something else to grab the distance and then decide whether or not to grab the
            // ... specimen or whatever the hell its called


            telemetry.update();
        }
    }

    /*
        Right now the behavior between when its in init and when its running
        aren't different soo....

        it will be later and then this function may be useless
     */
    private void mainLoopForLater(){

    }
}
