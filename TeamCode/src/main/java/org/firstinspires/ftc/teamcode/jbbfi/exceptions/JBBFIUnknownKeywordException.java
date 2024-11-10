package org.firstinspires.ftc.teamcode.jbbfi.exceptions;

public class JBBFIUnknownKeywordException extends Exception{
    public JBBFIUnknownKeywordException(String name) {
        super(name + " is undefined!");
    }
}