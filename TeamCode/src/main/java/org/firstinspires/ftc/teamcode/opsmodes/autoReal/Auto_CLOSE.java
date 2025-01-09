package org.firstinspires.ftc.teamcode.opsmodes.autoReal;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.opsmodes.HangerJBBFIProto;

@Autonomous(group="drive", name="AUTO_LEFT")
public class Auto_CLOSE extends AutoBase {
    @Override
    public void moveToBucketInit()  {
        runFunc("moveToClipPositionClose");
    }

    //public abstract void getSideColor();
}
