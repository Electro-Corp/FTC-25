package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import java.util.Arrays;
import java.util.List;

@TeleOp(name = "WheelCalibration")
public class EncoderTest extends LinearOpMode {
    private DcMotorEx leftFront;
    private DcMotorEx leftBack;
    private DcMotorEx rightFront;
    private DcMotorEx rightBack;
    @Override
    public void runOpMode() {
        leftFront = hardwareMap.get(DcMotorEx.class, "leftFront");
        leftBack = hardwareMap.get(DcMotorEx.class, "leftBack");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");
        rightBack = hardwareMap.get(DcMotorEx.class, "rightBack");
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        waitForStart();
        while (opModeIsActive()) {
            telemetry.addData("leftFront", leftFront.getCurrentPosition());
            telemetry.addData("leftBack", leftBack.getCurrentPosition());
            telemetry.addData("rightFront", rightFront.getCurrentPosition());
            telemetry.addData("rightBack", rightBack.getCurrentPosition());
            telemetry.update();
        }
    }
}