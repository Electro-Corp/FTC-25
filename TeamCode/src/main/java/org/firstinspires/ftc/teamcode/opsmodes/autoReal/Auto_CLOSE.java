package org.firstinspires.ftc.teamcode.opsmodes.autoReal;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(group="drive", name="AUTO_LEFT")
public class Auto_CLOSE extends HangerJBBFIProto {
    @Override
    public void moveToBucketInit()  {
        runFunc("moveToClipPositionClose");
    }

    //public abstract void getSideColor();
}
