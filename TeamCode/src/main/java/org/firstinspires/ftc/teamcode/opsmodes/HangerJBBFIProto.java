package org.firstinspires.ftc.teamcode.opsmodes;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.jbbfi.JBBFI;
import org.firstinspires.ftc.teamcode.jbbfi.ScriptingWebPortal;
import org.firstinspires.ftc.teamcode.jbbfi.exceptions.JBBFIClassNotFoundException;
import org.firstinspires.ftc.teamcode.jbbfi.exceptions.JBBFIInvalidFunctionException;
import org.firstinspires.ftc.teamcode.jbbfi.exceptions.JBBFIUnknownKeywordException;
import org.firstinspires.ftc.teamcode.pipelines.AutoPipeLine;
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.Arm;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Hanger;
import org.firstinspires.ftc.teamcode.subsystems.Marker;
import org.firstinspires.ftc.teamcode.subsystems.OpenCVManager;
import org.firstinspires.ftc.teamcode.subsystems.RoadRunnerHelper;
import org.opencv.core.Point;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
@TeleOp(name="Hanger JBBFI")
public class HangerJBBFIProto extends LinearOpMode {

    private Arm arm;
    private Claw claw;
    private Hanger hanger;

    String error;

    JBBFI jbbfi;
    ScriptingWebPortal scriptingWebPortal;


    RoadRunnerHelper roadRunnerHelper;


    int neg = -1;





    boolean alreadyLaunched = false;

    public void pause(double milli){
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
            hanger = new Hanger(hardwareMap);


            claw.closeTheClaw();



            try {
                jbbfi = new JBBFI("/sdcard/scripting/hanger.jbbfi", hardwareMap);
                jbbfi.addGlobal(roadRunnerHelper, "driveHelper");
                jbbfi.addGlobal(arm, "arm");
                jbbfi.addGlobal(claw, "claw");
                jbbfi.addGlobal(hanger, "hanger");
                jbbfi.addGlobal(this, "me");
                jbbfi.addGlobal(neg, "neg");
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
                telemetry.addLine("ScriptPortal is alive " + scriptingWebPortal.getState().toString());
            }else{
                telemetry.addLine("ScriptPortal is dead " + scriptingWebPortal.getState().toString());
            }
            telemetry.addData("PORT", scriptingWebPortal.PORT_FINAL);
            telemetry.addData("Error", error);
            telemetry.addLine("======= JBBFI TRACE =======");
            telemetry.addData("Last/Current line parsed", jbbfi.parseLineCur);
            telemetry.addData("Last function run", jbbfi.funcRunCur);
            telemetry.addLine("Loaded objects: " + objectsList);
            telemetry.addLine("==== MEMORY DEBUG ====");
            telemetry.addData("Total Memory", Runtime.getRuntime().totalMemory());
            telemetry.addData("Free Memory", Runtime.getRuntime().freeMemory());
            telemetry.addData("Current memory usage", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
            telemetry.update();
        }

        try {
//            runFunc("dropInBucket");
//            runFunc("goToTape");
//            runFunc("pickUpPiece");
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        scriptingWebPortal.stopRunning();

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


}
