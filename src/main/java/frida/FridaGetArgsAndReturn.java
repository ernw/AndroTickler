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
import commandExec.Commando;
import initialization.TicklerVars;

/**
 * GEt input and output of a method, provided the following:
 * 1) the method is not overloaded: only one method of this name
 * 2) inputs and outputs are of primitive data types or String
 * @author aabolhadid
 *
 */
public class FridaGetArgsAndReturn extends FridaJsAction{

	
	public FridaGetArgsAndReturn(boolean reuseScript) {
		this.script = new FridaJsScript(FridaVars.GET_VALS_JS);
		if (reuseScript)
			this.code = this.script.getCodeFromScript();
		else
			this.code = FridaVars.GET_VALS_CODE;
	}
	
	public void run(ArrayList<String> args){
		String finalCode = this.prepareCode(args);
		this.execute(finalCode);

	}
	

	private String prepareCode(ArrayList<String> args){
		String tempCode = this.code.replaceAll("\\$className", args.get(1)).replaceAll("\\$method_name", args.get(2));
		int numberOfArgs = new Integer(args.get(3));
		ArrayList<String> methodArgs = this.getMethodArguments(numberOfArgs);
		
		String methodArguments="";
		String consoleLogArgs = "";
		for (String s: methodArgs){
			methodArguments+=s+", ";
			consoleLogArgs+="console.log(\"Input: \"+"+s+");\n"; 
		}
		
		methodArguments = methodArguments.substring(0, methodArguments.length()-2);
		
		tempCode = tempCode.replaceAll("\\$args", methodArguments);
		tempCode = tempCode.replaceAll("\\$console_log_inputs", consoleLogArgs);
		
		return tempCode;
	}
}
