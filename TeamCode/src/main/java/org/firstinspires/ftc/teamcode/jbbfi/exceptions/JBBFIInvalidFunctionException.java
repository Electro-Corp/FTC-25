package org.firstinspires.ftc.teamcode.jbbfi.exceptions;

public class JBBFIInvalidFunctionException extends Exception{
    public JBBFIInvalidFunctionException(String function){
        super("Either " + function +
                " does not exist, or you have the wrong arguments.");
    }
}
