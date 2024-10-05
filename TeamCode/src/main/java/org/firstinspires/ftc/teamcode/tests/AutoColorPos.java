package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.checkerframework.checker.units.qual.A;
import org.firstinspires.ftc.teamcode.pipelines.AutoPipeLine;
import org.firstinspires.ftc.teamcode.pipelines.CalibratePipeline;
import org.firstinspires.ftc.teamcode.subsystems.Arm;
import org.firstinspires.ftc.teamcode.subsystems.Marker;
import org.firstinspires.ftc.teamcode.subsystems.OpenCVManager;
import org.opencv.core.Point;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

@Autonomous(group="drive", name="hii")
public class AutoColorPos extends LinearOpMode  {

    private final int TARGET_X = 240;
    private final int TARGET_Y = 500;

    private OpenCVManager cam;
    private Arm arm;

    @Override
    public void runOpMode() throws InterruptedException {

        arm = new Arm(hardwareMap);


        cam = new OpenCVManager(hardwareMap);
        CalibratePipeline calibratePipeline = new CalibratePipeline();

        AutoPipeLine autoPipeLine = new AutoPipeLine(Marker.RED, new Point(TARGET_X, TARGET_Y));
        cam.setPipeline(autoPipeLine);

        while(!isStarted() && !isStopRequested()) {

            // Get target's screenspace coords
            int posX = autoPipeLine.getX(), posY = autoPipeLine.getY();
            // Do something with that Ig....
            if(TARGET_Y > posY){
                // Slide arm forwards
            }else if(TARGET_Y < posY){
                // Slide arm backwards
            }

            if(TARGET_X > posX){
                // Rotate claw ->
            }else if(TARGET_X < posX) {
                // Rotate claw <-
            }

            // Do something else to grab the distance and then decide whether or not to grab the
            // ... specimen or whatever the hell its called
        }
    }
}
