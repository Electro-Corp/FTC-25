package org.firstinspires.ftc.teamcode.jbbfi;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.jbbfi.exceptions.JBBFIClassNotFoundException;
import org.firstinspires.ftc.teamcode.jbbfi.exceptions.JBBFIInvalidFunctionException;
import org.firstinspires.ftc.teamcode.jbbfi.exceptions.JBBFIScriptNotFoundException;
import org.firstinspires.ftc.teamcode.jbbfi.exceptions.JBBFIUnknownKeywordException;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Scanner;



/**
 Manages scripting
 */
public class JBBFI {
    ArrayList<JBBFIObject> objects = new ArrayList<>();
    ArrayList<JBBFIFunction> functions = new ArrayList<>();

    ArrayList<String> fileData = new ArrayList<>();

    public static HardwareMap hardwareMap;

    public String parseLineCur = "No Line Loaded";
    public String funcRunCur = "No function run";

    int currentLine = 0;
    volatile boolean allowParseNextLine = true;

    JBBFIExecLayer jbbfiExecLayer;


    public JBBFI(String fileName) throws JBBFIScriptNotFoundException, JBBFIClassNotFoundException, FileNotFoundException, JBBFIInvalidFunctionException, JBBFIUnknownKeywordException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        this.jbbfiExecLayer = new JBBFIExecLayer();
        try {
            readFile(fileName);
        } catch (FileNotFoundException e){
            throw new JBBFIScriptNotFoundException(fileName);
        }

        // Add math as a global
        addGlobal(new JBBFIMath(), "Math");

        parse();
    }

    /**
     * Create a JBBFI Interpreter that supports classes that need HardwareMap
     * @param fileName
     * @param hardwareMap
     * @throws JBBFIScriptNotFoundException
     * @throws JBBFIClassNotFoundException
     * @throws FileNotFoundException
     * @throws JBBFIInvalidFunctionException
     * @throws JBBFIUnknownKeywordException
     */
    public JBBFI(String fileName, HardwareMap hardwareMap) throws JBBFIScriptNotFoundException, JBBFIClassNotFoundException, FileNotFoundException, JBBFIInvalidFunctionException, JBBFIUnknownKeywordException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        this.hardwareMap = hardwareMap;
        this.jbbfiExecLayer = new JBBFIExecLayer();
        try {
            readFile(fileName);
        } catch (FileNotFoundException e){
            try{
              File swag = new File(fileName);
              //readFile(fileName);
            }catch(Exception g) {
                throw new JBBFIScriptNotFoundException(fileName);
            }
        }

        // Add math as a global
        addGlobal(new JBBFIMath(), "Math");

        parse();
    }

    public void parse() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, JBBFIClassNotFoundException, JBBFIUnknownKeywordException, JBBFIInvalidFunctionException{
        for(currentLine = 0; currentLine < fileData.size(); currentLine++){
            parseLine(fileData.get(currentLine));
        }
    }

    /**
     * Add a global variable\
     * @param global Object of variable
     * @param name Name to be used in scripts
     */
    public void addGlobal(Object global, String name){
        objects.add(
                new JBBFIObject(
                        global,
                        name
                )
        );
    }

    public String[] getObjectNames(){
        return objects.stream()
                .map(JBBFIObject::getName)
                .toArray(String[]::new);
    }



    public void readFile(String fileName) throws FileNotFoundException {
        File scriptFile = new File(fileName);
        Scanner fileScan = new Scanner(scriptFile);
        fileData.clear();
        functions.clear();
        while(fileScan.hasNextLine()){
            fileData.add(fileScan.nextLine());
        }
    }

    /**
     * By default, just execute
     */
    private void parseLine(String line) throws JBBFIClassNotFoundException, JBBFIInvalidFunctionException, JBBFIUnknownKeywordException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        parseLine(line, false);
    }

    private void parseLine(String line, boolean dryRun) throws JBBFIClassNotFoundException, JBBFIUnknownKeywordException, JBBFIInvalidFunctionException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        parseLineCur = line;
        String[] tokens = line.split("\\s+"); // we're looking for spaces

        switch(tokens[0]){
            /*
                Example:
                    init Arm arm
             */
            case "init":
                // Jesse, we need to initialize an object
                try{
                    objects.add(
                            new JBBFIObject(tokens[1], tokens[2])
                    );
                }catch (Exception e){
                    e.printStackTrace();
                    throw new JBBFIClassNotFoundException(tokens[1]);
                }
                break;
            // Oh. Its on.
            case "function":
                // TODO: IF THE NEXT LINE FAILS, CRASH WITH FUNCTIONWITHOUTNAMEEXCPETION
                JBBFIFunction function = new JBBFIFunction(tokens[1]);
                int offSet = currentLine + 1;
                while(offSet < fileData.size() - 1){
                    String l = fileData.get(offSet);
                    if(l.contains("function_end")) break;
                    function.addLine(l);
                    offSet++;
                }
                functions.add(function);
                currentLine += (offSet - currentLine);
                break;
            // If?
            case "if":

                break;
            /*
                Variable types
             */
            case "int":
                objects.add(
                    new JBBFIObject(
                            new Integer(tokens[2]),
                            tokens[1]
                    )
                );
                break;
            case "double":
                objects.add(
                        new JBBFIObject(
                                new Double(tokens[2]),
                                tokens[1]
                        )
                );
                break;
            case "float":
                objects.add(
                        new JBBFIObject(
                                new Float(tokens[2]),
                                tokens[1]
                        )
                );
                break;
            /*
                set varName value
             */
            case "set":

                break;
            /*
                goto functionName
             */
            case "goto":
                runFunction(tokens[1]);
                break;
            default:
                // is it empty?
                if(tokens[0].isEmpty()) return;

                // Is it a comment
                if(tokens[0].startsWith("//")) return;

                // Maybe a function? lets see
                if(runFunction(tokens[0]) == 1) return;

                // Maybe the name of an object? lets try
                // Split at "::" since that is how functions are called
                String[] possible = line.split("\\::");
                boolean found = false;
                for (JBBFIObject obj:
                        objects) {
                    if(obj.getName().equals(possible[0])){
                        found = true;
                        // Start doing stuff
                        for (int i = 1; i < possible.length; i++) {
                            String func = possible[i];
                            // Name of the function
                            String functionName = func.split("\\(")[0];
                            // Get the arguments in the "( )"
                            String argsSTR = func.substring(func.indexOf("(")+1, func.indexOf(")"));
                            // Isolate args
                            String[] argList = argsSTR.split("\\,");
                            // Args
                            ArrayList<JBBFIArg> args = new ArrayList<>();
                            for(String argStr : argList){
                                if(argStr.isEmpty()) continue;
                                // Convert to a JBBFIArg
                                args.add(
                                        getArgFromString(argStr)
                                );
                            }

                            try {
                                obj.executeFunction(functionName, args.toArray(new JBBFIArg[0]));
                            } catch (Exception e){
                                e.printStackTrace();
                                throw new JBBFIInvalidFunctionException(obj.getName() + "::" + functionName + " with error (" + e.getCause() + ")");
                            }
                        }


                    }
                }
                if(!found){
                    throw new JBBFIUnknownKeywordException(possible[0] + "/w line " + tokens[0]);
                }
                break;
        }
    }

    private JBBFIArg getArgFromString(String argStr) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{
        // Is it a primitive
        // Int
        try {
            Integer intTmp = Integer.parseInt(argStr);
            return new JBBFIArg(intTmp);

        }catch (NumberFormatException n){
            // Continue
        }
        // Double
        try {
            Double doubleTmp = Double.parseDouble(argStr);
            return new JBBFIArg(doubleTmp);

        }catch (NumberFormatException n){
            // Continue
        }
        // Float
        try {
            Float floatTmp = Float.parseFloat(argStr);
            return new JBBFIArg(floatTmp);

        }catch (NumberFormatException n){
            // Continue
        }


        // Is it a type of class we know?
        // Look for object
        for (JBBFIObject objKnown:
                objects) {
            if(objKnown.getName().equals(argStr)){
                return new JBBFIArg(objKnown.getObject());
            }
        }

        try{
            // Ok ok what if what if its a JBBFI helper class
            String moduleName = argStr.split("\\:")[0].replaceAll(" ", "");
            String moduleFunc = argStr.split("\\:")[1].substring(0,argStr.split("\\:")[1].indexOf("<"));
            String moduleArgs = argStr.split("\\:")[1];


            for (JBBFIObject module : objects) {
                if(module.getName().equals(moduleName)){
                    ArrayList<JBBFIArg> args = new ArrayList<>();
                    // Get the arguments in the "( )"
                    String argsSTR = moduleArgs.substring(moduleArgs.indexOf("<")+1);
                    // Isolate args
                    String[] argList = argsSTR.split("\\;");
                    for(String arg : argList){
                        arg = arg.split("\\>")[0];
                        if(arg.isEmpty()) continue;
                        // Convert to a JBBFIArg
                        args.add(
                                getArgFromString(arg)
                        );
                    }
                    // Run
                    return new JBBFIArg(module.executeFunction(moduleFunc, args.toArray(new JBBFIArg[0])));
                }
            }
        }catch(Exception e){
            //e.printStackTrace();
            // its not lamfo
        }


        return new JBBFIArg(argStr);
    }

    /**
     * Run a function
     * @param funcName name of the function (shocking)
     * @return 1 for sucesss, 0 for failure
     * @throws JBBFIClassNotFoundException
     * @throws JBBFIInvalidFunctionException
     * @throws JBBFIUnknownKeywordException
     */
    public int runFunction(String funcName) throws JBBFIClassNotFoundException, JBBFIInvalidFunctionException, JBBFIUnknownKeywordException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        funcRunCur = funcName;
        for (JBBFIFunction func:
                functions) {
            if(funcName.split("\\(")[0].contains(func.getFunctionName())) {
                for(String s : func.getCommands()){
                    if(allowParseNextLine) {
                        parseLine(s);
                    }else{
                        // Reset parse condition and finish
                        allowParseNextLine = true;
                        return 0;
                    }
                }
                return 1;
            }
        }
        return 0;
    }


    /**
       Attempt to stop execution
     */
    public void stop(){
        allowParseNextLine = false;
    }

}