package org.firstinspires.ftc.teamcode.jbbfi;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.jbbfi.exceptions.JBBFIHardwareMapNullException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
public class JBBFIObject<T> {
    private Object object;

    private String name;

    public JBBFIObject(String objectName, String frontEndName) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, JBBFIHardwareMapNullException {
        java.lang.Class<T> claz = (Class<T>) Class.forName(objectName);

        // Is it in the subsystem folder, or is it RoadRunner Helper?
        if(objectName.contains("subsystems") || objectName.contains("RoadRunnerHelper")){
            if(JBBFI.hardwareMap == null){
                // Well i need a hardwaremap
                throw new JBBFIHardwareMapNullException();
            }
            // Pass a hardwareMap, but we need someway for JBFFIObject to get it...
            object = claz.getDeclaredConstructor(HardwareMap.class).newInstance(JBBFI.hardwareMap);
        }else {
            // No, just assume nothing
            object = claz.newInstance();
        }
        this.name = frontEndName;
    }

    public JBBFIObject(Object object, String frontEndName){
        this.name = frontEndName;
        this.object = object;
    }

    public T executeFunction(String name, JBBFIArg... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Holds the class types for the args
        ArrayList<Class<?>> argTypes = new ArrayList<>();
        // Holds the args themselves
        ArrayList<Object> argsObj = new ArrayList<>();
        // Fill em up
        for (JBBFIArg arg:
                args) {
            argTypes.add(
                    arg.getArg().getClass()
            );
            argsObj.add(arg.getArg());
        }

        Class<?>[] parameterTypes = argTypes.toArray(new Class<?>[0]);
        // Function
        Method function = object.getClass().getMethods()[0];
        int i = 0;
        while(!function.getName().equals(name)){
            function = object.getClass().getMethods()[i++];
        }
        return (T) function.invoke(object, argsObj.toArray());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
