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

import cliGui.OutBut;
import initialization.TicklerVars;

public class FridaEnumerateClasses extends FridaJsAction{

	
	public FridaEnumerateClasses(boolean reuseScript) {
		this.script = new FridaJsScript(FridaVars.ENUM_LOC);
		if (reuseScript)
			this.code = this.script.getCodeFromScript();
		else
			this.code = FridaVars.ENUM_CODE;
	}
	
	public void run(){
		OutBut.printNormal("\nPlease start the app before running this command\n");
		this.executeNoSpawn(this.code);
	}
	
}

