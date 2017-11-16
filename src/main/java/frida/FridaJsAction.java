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

public class FridaJsAction {
	protected FridaJsScript script;
	protected String code;
	
	public void execute(String finalCode){
		this.script.writeCodeInScript(finalCode);
		this.script.prepareCommand();
		this.script.run();
	
		
	}
	
	public void executeNoSpawn(String finalCode){
		this.script.writeCodeInScript(finalCode);
		this.script.prepareCommandNoSpawning();
		this.script.run();
	
		
	}
	
	protected ArrayList<String> getMethodArguments(int num){
		ArrayList<String> methodArgs = new ArrayList<>();
		for (int i=0;i<num;i++){
			methodArgs.add("arg"+i);
		}
		return methodArgs;
	}

}
