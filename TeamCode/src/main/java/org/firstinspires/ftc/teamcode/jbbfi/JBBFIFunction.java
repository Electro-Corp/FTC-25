package org.firstinspires.ftc.teamcode.jbbfi;

import java.util.ArrayList;

/**
 * Very simple, just an ArrayList of Strings.
 */
public class JBBFIFunction {
    private ArrayList<String> commands;

    private String functionName;

    public JBBFIFunction(String funcName){
        functionName = funcName;
        commands = new ArrayList<>();
    }

    public void addLine(String line){
        commands.add(line);
    }

    public ArrayList<String> getCommands(){
        return commands;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
}
