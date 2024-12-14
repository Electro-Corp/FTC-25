package org.firstinspires.ftc.teamcode.opsmodes.autoReal;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.jbbfi.exceptions.JBBFIClassNotFoundException;
import org.firstinspires.ftc.teamcode.jbbfi.exceptions.JBBFIInvalidFunctionException;
import org.firstinspires.ftc.teamcode.jbbfi.exceptions.JBBFIUnknownKeywordException;

import java.lang.reflect.InvocationTargetException;

@Autonomous(group="drive", name="AUTO_LEFT")
public class Auto_CLOSE extends AutoBase{
    @Override
    public void moveToBucketInit()  {
        runFunc("moveToClipPositionClose");
    }

    //public abstract void getSideColor();
}
