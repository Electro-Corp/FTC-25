# JBBFI
Are you trying to fix some garbage code that I wrote when I'm not there, but half the code is no longer in Java and you can't tell how this ugly new language works?
<br>
You're in the right place.

## Usage
### Connection to web portal
Launch the web portal by running an opmode that initializes it. It will give you a port number on the telemetry. <br>
Connect to the robot's wifi and goto `http://192.168.43.1:[PORT]/scripting`. 

### Usage
Functions are declared as so:
```
function functionName
function_end
```
To use a class that has been defined as a global, do as the following:
```
class::functionName(args)
```
as compared to 
```java
class.functionName(args);
```
in java.

For example:
```
arm::setPos(10)
```

#### Modules
To use modules in args, do the following:
```
class::functionName(arg1, ModuleName:moduleFunction<argMod1;argMod2>)
```

