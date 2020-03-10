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
package apk.newApks;

import java.io.IOException;

import base.FileUtil;
import initialization.TicklerConst;
import initialization.TicklerVars;

public class Debuggable implements INewApk {

	private String newAppDir;
	private FileUtil fU;
	
	public Debuggable(){
		this.newAppDir = TicklerVars.appTickDir+TicklerConst.newAppTempDir;
		this.fU = new FileUtil();
	}
	
	@Override
	public String getNewApkName() {
		return TicklerVars.appTickDir+TicklerConst.debuggableName;
		
	}

	@Override
	public void changeManifest() {
		
		try {
			String manString = this.fU.readFile(newAppDir+"AndroidManifest.xml");
			//DEbuggable
			if (manString.contains("android:debuggable"))
				manString = manString.replaceAll("debuggable=\"false\"", "debuggable=\"true\"");
			else
				manString = manString.replaceAll("<application ", "<application android:debuggable=\"true\" ");
			
			this.fU.writeFile(newAppDir+"AndroidManifest.xml", manString);
		}
		catch (IOException e)	{
			e.printStackTrace();
		}
		
		
	}

}
