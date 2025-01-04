package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Autonomous(group="drive", name="ARMCALIB")
public class ArmCalib extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        DcMotorEx armExtender = hardwareMap.get(DcMotorEx.class, "ArmSlide");
        DcMotorEx stage1Hanger = hardwareMap.get(DcMotorEx.class, "stage1Hang");

        while(!isStarted()){
            telemetry.addData("Arm Pos %d", armExtender.getCurrentPosition());
            telemetry.addData("Hanger Pos %d", stage1Hanger.getCurrentPosition());

            telemetry.update();
        }

        while(!isStopRequested()){
            telemetry.addData("Arm Pos %d", armExtender.getCurrentPosition());
            telemetry.addData("Hanger Pos %d", stage1Hanger.getCurrentPosition());

            telemetry.update();
        }


    }
}
