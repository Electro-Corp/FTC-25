package org.firstinspires.ftc.teamcode.opsmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pipelines.AutoPipeLine;
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.Marker;
import org.firstinspires.ftc.teamcode.subsystems.OpenCVManager;
import org.firstinspires.ftc.teamcode.subsystems.RoadRunnerHelper;
import org.opencv.core.Point;

@TeleOp(name= "Alignment Test")
public class AlignmentTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        OpenCVManager manager = new OpenCVManager(hardwareMap);
        AutoPipeLine autoPipeLine = new AutoPipeLine(Marker.BLUE, new Point(320, 240));
        manager.setPipeline(autoPipeLine);

        SampleMecanumDrive sampleMecanumDrive = new SampleMecanumDrive(hardwareMap);
        RoadRunnerHelper roadRunnerHelper = new RoadRunnerHelper(sampleMecanumDrive);

        waitForStart();

        int tolerance = 10;
        double moveDistance = 5.0;

        boolean upKeyPressed = false, downKeyPressed = false;

        while (opModeIsActive()) {
            //
            telemetry.addData("POSX (important)", autoPipeLine.getX());
            telemetry.addData("POSY (not so important)", autoPipeLine.getY());
            telemetry.addLine("================================");
            telemetry.addLine("Use GamePad1 DPAD to change tolerance, or hold A to change the distance we move");
            telemetry.addData("Tolerance", tolerance);
            telemetry.addData("Distance", moveDistance);
            telemetry.addLine("================================");

            if(!upKeyPressed && gamepad1.dpad_up){
                upKeyPressed = true;
                if(gamepad1.a){
                    moveDistance+=0.5;
                }else {
                    tolerance++;
                }
            }else if(!gamepad1.dpad_up){
                upKeyPressed = false;
            }

            if(!downKeyPressed && gamepad1.dpad_down){
                downKeyPressed = true;
                if(gamepad1.a){
                    moveDistance-=0.5;
                }else {
                    tolerance--;
                }
            }else if(!gamepad1.dpad_down){
                downKeyPressed = false;
            }

            // Decisions decisions

            // Check distance
            telemetry.addLine("Hold B to move");
            if (gamepad1.b) {
                int distance = autoPipeLine.getX() - (OpenCVManager.WIDTH / 2);
                telemetry.addData("Distance", distance);

                if (Math.abs(distance) < tolerance) {
                    telemetry.addLine("Distance is within tolerance.");
                } else {
                    double scaleFactor = Math.max(0.2, Math.min(1.0, Math.abs((double) distance / (OpenCVManager.WIDTH / 2))));
                    double adjustedMoveDistance = moveDistance * scaleFactor;

                    if (distance > 0) {
                        // Target is to the right
                        telemetry.addLine("We're moving right.");
                        roadRunnerHelper.strafeRight(adjustedMoveDistance);
                    } else {
                        // Target is to the left
                        telemetry.addLine("We're moving left.");
                        roadRunnerHelper.strafeRight(-adjustedMoveDistance);
                    }
                }
            }


            telemetry.update();
        }

    }
}
