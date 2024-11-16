package org.firstinspires.ftc.teamcode.jbbfi.exceptions;

public class JBBFIHardwareMapNullException extends Exception{
    public JBBFIHardwareMapNullException(){
        super("A init'ed class needs a hardwareMap, but you didn't provide one!");
    }
}