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

import apk.AppBroker;
import cliGui.OutBut;
import commandExec.Commando;
import initialization.TicklerVars;

public class FridaBase {
	
	public FridaBase() {
		
	}
	
	public void fridaEnumerateClasses(boolean reuse){
		FridaEnumerateClasses enumClasses = new FridaEnumerateClasses(reuse);
		enumClasses.run();
	}
	
	public void fridaScript(ArrayList<String> args){
//		FridaPythonScript script = new FridaPythonScript(args);
//		script.execute();
		FridaJsScript script = new FridaJsScript(args.get(1));
		script.prepareCommandNoSpawning();
		OutBut.printNormal("\nPlease start the app before running this command\n");
		
		script.run();
	}
	
	public void fridaGetInputAndOutput(ArrayList<String> args, boolean reuse){
		FridaGetArgsAndReturn action = new FridaGetArgsAndReturn(reuse);
		action.run(args);
	}
	
	public void fridaSetValue(ArrayList<String> args, boolean reuse){
		FridaSetValue action = new FridaSetValue(reuse);
		action.run(args);
	}
	
	public void fridaUnpin(ArrayList<String> args, boolean reuse) {
		FridaUnpinSslContext action = new FridaUnpinSslContext(reuse);
		action.run(args);
	}
	

	
}
