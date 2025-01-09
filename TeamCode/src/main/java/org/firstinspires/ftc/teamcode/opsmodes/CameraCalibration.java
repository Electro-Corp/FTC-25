package org.firstinspires.ftc.teamcode.opsmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.pipelines.AutoPipeLine;
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.Arm;
import org.firstinspires.ftc.teamcode.subsystems.Marker;
import org.firstinspires.ftc.teamcode.subsystems.OpenCVManager;
import org.firstinspires.ftc.teamcode.subsystems.RoadRunnerHelper;
import org.opencv.core.Point;

@TeleOp(group="drive", name="Camera Calibration")
public class CameraCalibration extends LinearOpMode {

    private enum CalibStage{
        NOTHING,
        MEASURING,
        INPUT_DISTANCE_X,
        INPUT_DISTANCE_Y,
        CALCULATION,
        AM_I_RIGHT,
        LETS_TRY_AGAIN
    }

    CalibStage calibStage = CalibStage.NOTHING;

    Servo pitchServoLeft;

    @Override
    public void runOpMode() throws InterruptedException {
        OpenCVManager manager = new OpenCVManager(hardwareMap);
        AutoPipeLine autoPipeLine = new AutoPipeLine(Marker.BLUE, new Point(320, 240));
        manager.setPipeline(autoPipeLine);

        SampleMecanumDrive sampleMecanumDrive = new SampleMecanumDrive(hardwareMap);
        RoadRunnerHelper roadRunnerHelper = new RoadRunnerHelper(sampleMecanumDrive);

        pitchServoLeft = this.hardwareMap.get(Servo.class, Arm.HARDWARE_NAME_LEFTPITCHSERVO);

        waitForStart();

        double time = getRuntime();

        double measuredX = 0, measuredY = 0;

        double finalConstX = 0.0, finalConstY = 0.0;

        float leftPitchPos = 0.421f;
        float diff = 0.0005f;



        boolean upKeyPressed = false, downKeyPressed = false;

        while (opModeIsActive()){
            telemetry.addData("POSX", autoPipeLine.getX());
            telemetry.addData("POSY", autoPipeLine.getY());
            telemetry.addData("ARM POS", leftPitchPos);
            telemetry.addLine("== CAMERA DISTANCE CALIBRATION ==");

            if(leftPitchPos < 0.0f) leftPitchPos = 0.0f;
            if(leftPitchPos > 1.0f) leftPitchPos = 1.0f;

            if (gamepad1.right_bumper) leftPitchPos += diff;
            if (gamepad1.left_bumper) leftPitchPos -= diff;

            pitchServoLeft.setPosition(leftPitchPos);

            switch(calibStage){
                case NOTHING:
                    telemetry.addLine("Press A on GamePad 1 to begin calibration.");
                    if(isAValid()){
                        calibStage = CalibStage.MEASURING;
                    }
                    break;
                case MEASURING:
                    telemetry.addLine("Go measure how much we need to move in order to be in the center.");
                    telemetry.addLine("Then press A on GamePad1.");
                    if(isAValid()){
                        calibStage = CalibStage.INPUT_DISTANCE_X;
                    }
                    break;
                case INPUT_DISTANCE_X:
                    telemetry.addLine("Use the D-PAD to input how many inches we need to move in the X DIRECTION");
                    telemetry.addLine("And then press A.");

                    if(!upKeyPressed && gamepad1.dpad_up){
                        upKeyPressed = true;
                        measuredX+=0.25;
                    }else if(!gamepad1.dpad_up){
                        upKeyPressed = false;
                    }

                    if(!downKeyPressed && gamepad1.dpad_down){
                        downKeyPressed = true;
                        measuredX-=0.25;
                    }else if(!gamepad1.dpad_down){
                        downKeyPressed = false;
                    }

                    telemetry.addData("Measured X", measuredX);

                    if(isAValid()){
                        calibStage = CalibStage.INPUT_DISTANCE_Y;
                    }
                    break;
                case INPUT_DISTANCE_Y:
                    telemetry.addLine("Use the D-PAD to input how many inches we need to move in the Y DIRECTION");
                    telemetry.addLine("And then press A.");

                    telemetry.addData("[LOCKED] Measured X", measuredX);
                    telemetry.addData("Measured Y", measuredY);

                    if(!upKeyPressed && gamepad1.dpad_up){
                        upKeyPressed = true;
                        measuredY+=0.25;
                    }else if(!gamepad1.dpad_up){
                        upKeyPressed = false;
                    }

                    if(!downKeyPressed && gamepad1.dpad_down){
                        downKeyPressed = true;
                        measuredY-=0.25;
                    }else if(!gamepad1.dpad_down){
                        downKeyPressed = false;
                    }

                    if(isAValid()){
                        calibStage = CalibStage.CALCULATION;
                    }
                    break;
                case CALCULATION:
                    finalConstX = autoPipeLine.getX() / measuredX;
                    finalConstY = autoPipeLine.getY() / measuredY;

                    telemetry.addData("Calculated X constant", finalConstX);
                    telemetry.addData("Calculated Y constant", finalConstY);
                    telemetry.addLine("Press A to test.");

                    if(isAValid()){
                        calibStage = CalibStage.AM_I_RIGHT;
                    }

                    break;
                case AM_I_RIGHT:
                    telemetry.addLine("Testing...");
                    telemetry.addData("Calculated X constant", finalConstX);
                    telemetry.addData("Calculated Y constant", finalConstY);
                    roadRunnerHelper.resetPath();
                    roadRunnerHelper.splineToLinearHeading(autoPipeLine.getX() / finalConstX, autoPipeLine.getY() / finalConstY, 0);
                    if(!sampleMecanumDrive.isBusy()){
                        calibStage = CalibStage.LETS_TRY_AGAIN;
                    }
                    break;
                case LETS_TRY_AGAIN:
                    telemetry.addLine("Move the piece to a new location and press A to test again.");
                    telemetry.addData("Calculated X constant", finalConstX);
                    telemetry.addData("Calculated Y constant", finalConstY);
                    if(isAValid()){
                        calibStage = CalibStage.AM_I_RIGHT;
                    }
                    break;
            }
            telemetry.update();
        }
    }
    boolean isAPressed = false;
    boolean isAValid(){
        if(!isAPressed && gamepad1.a){
            isAPressed = true;
            return true;
        }else if(!gamepad1.a){
            isAPressed = false;
        }
        return false;
    }
}
