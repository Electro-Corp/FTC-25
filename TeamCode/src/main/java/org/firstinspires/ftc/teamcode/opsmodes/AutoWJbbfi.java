package org.firstinspires.ftc.teamcode.opsmodes;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.jbbfi.JBBFI;
import org.firstinspires.ftc.teamcode.jbbfi.ScriptingWebPortal;
import org.firstinspires.ftc.teamcode.jbbfi.exceptions.JBBFIClassNotFoundException;
import org.firstinspires.ftc.teamcode.jbbfi.exceptions.JBBFIInvalidFunctionException;
import org.firstinspires.ftc.teamcode.jbbfi.exceptions.JBBFIScriptNotFoundException;
import org.firstinspires.ftc.teamcode.jbbfi.exceptions.JBBFIUnknownKeywordException;
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.Arm;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.RoadRunnerHelper;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

@Autonomous(group="drive", name="Autonomous JBBFI")
public class AutoWJbbfi extends LinearOpMode {

    JBBFI jbbfi;
    ScriptingWebPortal scriptingWebPortal;

    String error;

    @Override
    public void runOpMode() throws InterruptedException {
        Thread.UncaughtExceptionHandler caught = new Thread.UncaughtExceptionHandler(){
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                error = e.toString();
            }

        };
        error = "NONE";

        ScriptingWebPortal scriptingWebPortal = new ScriptingWebPortal(hardwareMap.appContext);
        scriptingWebPortal.setUncaughtExceptionHandler(caught);
        scriptingWebPortal.start();


        //ScriptWebPortal scriptWebPortal = new ScriptWebPortal();


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
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        scriptingWebPortal.setJbbfi(jbbfi);

        while(!isStarted() && !isStopRequested()) {
            if(scriptingWebPortal.isAlive()){
                telemetry.addLine("ScriptPortal is alive\n");
            }else{
                telemetry.addLine("ScriptPortal is dead\n");
            }
            telemetry.addData("State", scriptingWebPortal.getState().toString());
            telemetry.addData("PORT", scriptingWebPortal.PORT_FINAL);
            telemetry.addData("Error", error);
            telemetry.update();
        }


        try {
            jbbfi.runFunction("clipOn");
        } catch (Exception e){
            throw new RuntimeException(e);
        }


    }
}
