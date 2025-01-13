package org.firstinspires.ftc.teamcode.jbbfi;

/**
 * Class to manage execution of code
 */
public class JBBFIExecLayer {
    boolean executeOnHardware = true;

    public JBBFIExecLayer(){

    }


    public boolean isExecuteOnHardware() {
        return executeOnHardware;
    }

    public void setExecuteOnHardware(boolean executeOnHardware) {
        this.executeOnHardware = executeOnHardware;
    }
}
