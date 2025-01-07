package org.firstinspires.ftc.teamcode.opsmodes.autoReal;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.opsmodes.HangerJBBFIProto;

@Autonomous(group="drive", name="AUTO_RIGHT")
public class Auto_FAR extends HangerJBBFIProto {
    @Override
    public void moveToBucketInit() {
        runFunc("moveToClipPositionRight");
    }

    // We might need this later for camera stuff
    //public abstract void getSideColor();
}
