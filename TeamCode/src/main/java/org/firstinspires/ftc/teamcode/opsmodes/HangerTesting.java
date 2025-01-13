package org.firstinspires.ftc.teamcode.opsmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.Arm;
import org.firstinspires.ftc.teamcode.subsystems.Hanger;

@TeleOp(name = "HangerTesting")
public class HangerTesting extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();

    private Hanger hanger;
    private Arm arm;

    private boolean littleArmDeployed = false;
    private int hangerDiff = 50;

    private boolean xPressed = false;
    private boolean isLittleArmDeployed = false;

    private void initHardware() {
        hanger = new Hanger(hardwareMap);
        arm = new Arm(hardwareMap);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initHardware();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            arm.pitchAppend(gamepad1.left_stick_y / 5000);
            arm.armAppendDist((int) -(gamepad2.right_stick_y * 5.5f));

            if (gamepad1.dpad_up) {
                hanger.appendHangerDist(hangerDiff);
            } else if (gamepad1.dpad_down) {
                hanger.appendHangerDist(-1 * hangerDiff);
            }

            if (gamepad1.x && !xPressed) {
                if (!isLittleArmDeployed) {
                    hanger.deployLittleArm();
                    isLittleArmDeployed = true;
                } else {
                    hanger.retractLittleArm();
                    isLittleArmDeployed = false;
                }
                xPressed = true;
            } else if (!gamepad1.x && xPressed) {
                xPressed = false;
            }
        }

    }
}
