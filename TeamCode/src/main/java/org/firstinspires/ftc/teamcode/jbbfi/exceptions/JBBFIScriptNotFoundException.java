package org.firstinspires.ftc.teamcode.jbbfi.exceptions;

public class JBBFIScriptNotFoundException extends Exception{
    public JBBFIScriptNotFoundException(String name) {
        super("Could not find script at "+ name + "!");
    }
}
