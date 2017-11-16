/*******************************************************************************
 * Copyright 2017 Ahmad Abolhadid
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package frida;


import java.util.ArrayList;

import base.FileUtil;
import cliGui.OutBut;
import commandExec.Commando;
import initialization.TicklerVars;

/**
 * GEt input and output of a method, provided the following:
 * 1) the method is not overloaded: only one method of this name
 * 2) inputs and outputs are of primitive data types or String
 * @author aabolhadid
 *
 */
public class FridaSetValue {

	private FridaJsScript script;
	private String code;
	
	public FridaSetValue(boolean reuseScript) {
		this.script = new FridaJsScript(FridaVars.SET_VALS_JS);
		
		if (reuseScript)
			this.code = this.script.getCodeFromScript();
		else
			this.code = FridaVars.SET_VALS_CODE;
	}
	
	public void run(ArrayList<String> args){
		String finalCode = this.prepareCode(args);
		this.script.writeCodeInScript(finalCode);
		this.script.prepareCommand();
		this.script.run();
	
	}
	

	// args would be like: set, ClassName, MethodName, number_of_args, valNum, newValue  
	// myArgs : pkgName, ClassName, MethodName, methodArgs(arg0,1...etc) 
	private String prepareCode(ArrayList<String> args){
		
		String tempCode = this.code.replaceAll("\\$className", args.get(1)).replaceAll("\\$methodName", args.get(2));
		int numberOfArgs = new Integer(args.get(3));
		
		ArrayList<String> methodArgs = this.getMethodArguments(numberOfArgs); 
		
		String methodArguments="";
		String consoleLog = "";
		for (String s: methodArgs){
			methodArguments+=s+", ";
		}
		
		methodArguments = methodArguments.substring(0, methodArguments.length()-2);
		
		tempCode = tempCode.replaceAll("\\$args", methodArguments);
		
		int numberOfTarget = new Integer(args.get(4));
		String newValue = this.correctStringsInArgs(args.get(5));
		
		if (numberOfTarget>=numberOfArgs) {
			//Modify return value
			tempCode = tempCode.replaceAll("\\$returnValue", newValue);
			tempCode = tempCode.replaceAll("\\$output_line", "console.log(\"Old return value: \"+orig_return.toString()+ \". New return value: \"+"+newValue+");");
		}
		else {
			//Modify an argument
			String newArgs = this.getNewArgs(numberOfArgs,numberOfTarget,newValue);
			tempCode = tempCode.replaceAll("\\$returnValue", "this."+args.get(2)+newArgs);
			String y ="console.log(\"Arg number "+ numberOfTarget+": old value: \"+"+"arg"+numberOfTarget+ "+\". New value: \"+"+newValue+");"; 
			tempCode = tempCode.replaceAll("\\$output_line", y );
		}
		
		tempCode = tempCode.replaceAll("\\$output_line", consoleLog);
		
		return tempCode;
	}
		
	
	private ArrayList<String> getMethodArguments(int num){
		ArrayList<String> methodArgs = new ArrayList<>();
		for (int i=0;i<num;i++){
			methodArgs.add("arg"+i);
		}
		return methodArgs;
	}
	
	private String correctStringsInArgs(String arg){
		
		if (Integer.getInteger(arg) == null)
			if (!arg.equals("true") && !arg.equals("false"))
				return "\\\""+arg+"\\\"";
		
		return arg;
		
	}
	
	private String getNewArgs(int totalNumOfArgs, int argNum, String value){
		String methodArgs="(";
		
		for (int i=0;i<totalNumOfArgs;i++){
			if (i==argNum)
				methodArgs+=value+", ";
			else
				methodArgs+="arg"+i+", ";
		}
		
		methodArgs = methodArgs.substring(0, methodArgs.length()-2);
		methodArgs +=");";
		
		return methodArgs;
		
	}
	
}
