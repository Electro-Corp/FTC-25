package org.firstinspires.ftc.teamcode.opsmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.pipelines.AutoPipeLine;
import org.firstinspires.ftc.teamcode.subsystems.Marker;
import org.firstinspires.ftc.teamcode.subsystems.OpenCVManager;
import org.opencv.core.Point;

@Autonomous(group="drive", name="PipeLine Viewer")
public class PipelineViewer extends LinearOpMode {

    private DcMotorEx leftFrontDrive = null;
    private DcMotorEx rightFrontDrive = null;
    private DcMotorEx leftBackDrive = null;
    private DcMotorEx rightBackDrive = null;
    @Override
    public void runOpMode() throws InterruptedException {
        OpenCVManager manager = new OpenCVManager(hardwareMap);
        AutoPipeLine autoPipeLine = new AutoPipeLine(Marker.YELLOW, new Point(320, 240));
        manager.setPipeline(autoPipeLine);

        waitForStart();

        double time = getRuntime();


        while (opModeIsActive()){

        }
    }
}
