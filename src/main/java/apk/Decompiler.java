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
package apk;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

import base.FileUtil;
import base.SearchUtil;
import cliGui.OutBut;
import commandExec.Commando;
import initialization.TicklerVars;

public class Decompiler {
	private String jarLoc;
	
	private boolean checkD2jExists() {
		if (new File(TicklerVars.dex2jarPath).exists()){
			return true;
		}
		System.out.println("!!!!!ERROR: The stand alone version does not support dex2jar");
		return false;
		
	}
	
	private File getApkFromTicklerDir(){
		File apkFile;
		SearchUtil searcher = new SearchUtil();
		String[] keys ={"apk"};  
		List<File> apkList = searcher.search4FileInDir(TicklerVars.appTickDir, keys);
		apkFile = apkList.get(0);
		if (apkFile.getName().equals("debuggable.apk"))
			apkFile = apkList.get(1);
		
		return apkFile;
	}

	public void decompile(){
		this.dex2jar();
		this.jdCore();
	}
	
	public void dex2jar()
	{
		//If dex2jar scripts exist and executable
		if (this.checkD2jExists() && this.checkDex2JarExecutable()){
			
			FileUtil ft = new FileUtil();
			Commando command = new Commando();
			ft.createDirOnHost(TicklerVars.dex2jarDir);
			this.jarLoc = TicklerVars.dex2jarDir+"app.jar";
			String cmd = TicklerVars.dex2jarPath + " "+ this.getApkFromTicklerDir() +" -o "+TicklerVars.jClassDir+".zip";
			OutBut.printStep("Decompiling the app using Dex2Jar tool......");
			command.executeProcessString(cmd);
			
		}
	}
	
	
	private void jdCore(){
		try{
			OutBut.printStep("Obtaining Java code using JDCore tool. This might take some time ......");
			new jd.core.Decompiler().decompile(TicklerVars.jClassDir+".zip", TicklerVars.jClassDir);
		}
		catch(Exception e){
//			e.printStackTrace();
		}
	}
	
	/**
	 * If required dex2jar files are executable 
	 * @param path
	 */
	private boolean checkDex2JarExecutable(){
		boolean isExec = true;
		FileUtil ft = new FileUtil();
		Commando command = new Commando();
		String d2jInvokeFile = TicklerVars.dex2jarPath.replace("d2j-dex2jar.sh", "d2j_invoke.sh");
		String[] execs = {TicklerVars.dex2jarPath, d2jInvokeFile};
		for (String exec : execs){
			if (!ft.isExecutable(exec)){
				isExec = false;
				OutBut.printError("Please change mode of "+exec+" to executable and rerun the command" );
			}
		}
		return isExec;
	}
	
	/**
	 * return if dex2jar exists and executable
	 * @return
	 */
	public boolean isDex2Jar() {
		return this.checkD2jExists() && this.checkDex2JarExecutable();
	}
	
}
