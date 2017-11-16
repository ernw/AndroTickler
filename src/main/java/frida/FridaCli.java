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
import java.util.Arrays;
import java.util.List;

import cliGui.OutBut;

public class FridaCli {

	private FridaBase base;
	private FridaInit init;
	
	public FridaCli(){
		this.base = new FridaBase();
		this.init = new FridaInit();
		
		this.init.initFrida();
	}
	
	/**
	 * arg0: Name of function
	 * arg1- : args of the function
	 * @param args
	 */
	public void fridaThis(String [] args, boolean reuse){
		String functionName = args[0];
		ArrayList<String> scriptArgs = new ArrayList<>(Arrays.asList(args)); 
		
		switch(functionName){
		case "enum":
			this.base.fridaEnumerateClasses(reuse);
			break;
			
		case "script":
			this.base.fridaScript(scriptArgs);
//		
		
		case "vals":
			this.base.fridaGetInputAndOutput(scriptArgs, reuse);
			break;
			
		case "set":
			this.base.fridaSetValue(scriptArgs, reuse);
			break;
			
		case "unpin":
			this.base.fridaUnpin(scriptArgs, reuse);
			break;
			
		default:
				OutBut.printError("Unknown option "+functionName);
				break;
		
		}
		
		
	}
}
