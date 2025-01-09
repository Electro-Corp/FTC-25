package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;

public class OpenCVManager implements OpenCvCamera.AsyncCameraOpenListener {

    private OpenCvCamera camera;

    // if anyone tells me to make getters and setters for these im gonna lose it
    public static int WIDTH = 640, HEIGHT = 480;


    public OpenCVManager(HardwareMap hardwareMap){
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        WebcamName webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");

        // With live preview
        camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName, cameraMonitorViewId);
        camera.openCameraDeviceAsync(this);
    }



    public void setPipeline(OpenCvPipeline pipeline){
        camera.setPipeline(pipeline);
    }


    @Override
    public void onOpened() {
        camera.startStreaming(WIDTH, HEIGHT, OpenCvCameraRotation.UPSIDE_DOWN);
    }

    @Override
    public void onError(int i) {

    }
}