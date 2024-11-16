package org.firstinspires.ftc.teamcode.jbbfi;

public class JBBFIArg<T> {
    private T arg;

    public JBBFIArg(T arg){
        this.arg = arg;
    }

    T getArg(){
        return arg;
    }
}