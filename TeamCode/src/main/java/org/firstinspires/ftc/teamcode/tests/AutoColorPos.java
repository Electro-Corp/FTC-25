package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.checkerframework.checker.units.qual.A;
import org.firstinspires.ftc.teamcode.pipelines.AutoPipeLine;
import org.firstinspires.ftc.teamcode.pipelines.CalibratePipeline;
import org.firstinspires.ftc.teamcode.subsystems.Marker;
import org.firstinspires.ftc.teamcode.subsystems.OpenCVManager;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

@Autonomous(group="drive", name="hii")
public class AutoColorPos extends LinearOpMode  {

    private OpenCVManager cam;

    @Override
    public void runOpMode() throws InterruptedException {



        cam = new OpenCVManager(hardwareMap);
        CalibratePipeline calibratePipeline = new CalibratePipeline();

        AutoPipeLine autoPipeLine = new AutoPipeLine(Marker.RED);
        cam.setPipeline(autoPipeLine);
        while(!isStarted() && !isStopRequested()) {
            /*double[] hsvValues = calibratePipeline.getHSVValues();
            telemetry.addData("HSV: ",  "%.3f, %.3f, %.3f", hsvValues[0], hsvValues[1], hsvValues[2]);
            if (calibratePipeline.getMat() != null) {
                telemetry.addData("Info on Mat: %d", calibratePipeline.getMat().type());
            }
            telemetry.update();*/

        }
    }
}
