package org.firstinspires.ftc.teamcode.opsmodes.autoReal;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(group="drive", name="AUTO_FAR")
public class Auto_FAR extends AutoBase{
    @Override
    public void moveToBucketInit() {
        runFunc("moveToBuckIniFAR");
    }

    // We might need this later for camera stuff
    //public abstract void getSideColor();
}
