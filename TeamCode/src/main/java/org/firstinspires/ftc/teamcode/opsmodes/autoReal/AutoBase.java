package org.firstinspires.ftc.teamcode.opsmodes.autoReal;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.jbbfi.JBBFI;
import org.firstinspires.ftc.teamcode.jbbfi.ScriptingWebPortal;
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.Arm;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.RoadRunnerHelper;

import java.util.Arrays;

public abstract class AutoBase extends LinearOpMode {
    protected AutoBase() {
    }

    enum SideColor {
        RED,
        BLUE
    }

    private Arm arm;
    private Claw claw;

    String error;

    JBBFI jbbfi;
    ScriptingWebPortal scriptingWebPortal;

    boolean alreadyLaunched = false;

    public void waitTime(double milli){
        sleep((long) milli);
    }

    public void close(){
        claw.closeTheClaw();
    }

    @Override
    public void runOpMode() throws InterruptedException {
        if(!alreadyLaunched) {
            Thread.UncaughtExceptionHandler caught = new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                    error = e.toString();

                }

            };
            error = "NONE";

            scriptingWebPortal = new ScriptingWebPortal(hardwareMap.appContext);
            scriptingWebPortal.setUncaughtExceptionHandler(caught);
            scriptingWebPortal.start();


            // Create drivehelper
            SampleMecanumDrive sampleMecanumDrive = new SampleMecanumDrive(hardwareMap);
            RoadRunnerHelper roadRunnerHelper = new RoadRunnerHelper(sampleMecanumDrive);

            Arm arm = new Arm(hardwareMap);
            Claw claw = new Claw(hardwareMap);


            try {
                jbbfi = new JBBFI("/sdcard/scripting/test.jbbfi", hardwareMap);
                jbbfi.addGlobal(roadRunnerHelper, "driveHelper");
                jbbfi.addGlobal(arm, "arm");
                jbbfi.addGlobal(claw, "claw");
                jbbfi.addGlobal(this, "me");
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            scriptingWebPortal.setJbbfi(jbbfi);

            alreadyLaunched = true;

        }

        // Get modules
        String[] loadedObjects = jbbfi.getObjectNames();

        String objectsList = String.format("%s", Arrays.toString(loadedObjects));

        while(!isStarted() && !isStopRequested()) {
            if(scriptingWebPortal.isAlive()){
                telemetry.addLine("ScriptPortal is alive\n");
            }else{
                telemetry.addLine("ScriptPortal is dead\n");
            }
            telemetry.addData("State", scriptingWebPortal.getState().toString());
            telemetry.addData("PORT", scriptingWebPortal.PORT_FINAL);
            telemetry.addData("Error", error);
            telemetry.addLine("======= JBBFI TRACE =======");
            telemetry.addData("Last/Current line parsed", jbbfi.parseLineCur);
            telemetry.addData("Loaded objects", objectsList);
            telemetry.update();
        }

        try {
            moveToBucketInit();
            runFunc("dropInBucket");
            runFunc("goToTape");
            runFunc("pickUpPiece");
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        // Relaunch everything
        //runOpMode();
    }

    public void runFunc(String name){
        try {
            if(opModeIsActive())
                jbbfi.runFunction(name);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    public abstract void moveToBucketInit();


}
