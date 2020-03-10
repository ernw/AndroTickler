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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import base.OtherUtil;
import cliGui.OutBut;
import commandExec.Commando;
import components.Activity;
import components.Manifest;
import initialization.TicklerVars;

/**
 * Starts, stops, (un)install apps, backs up data dir before installation of an enhanced app
 * @author aabolhadid
 *
 */
public class AppBroker {
	
	private String pkgName,outputPath,sdCardPath;
	private Commando commando;
	
	
	/**
	 * @param laucher
	 * @param pkgName
	 */
	public AppBroker(Activity laucher,String pkgName) {
		super();
		this.pkgName = pkgName;
		this.commando = new Commando();
		this.sdCardPath=TicklerVars.sdCardPath;

	}
	
	public AppBroker(String pkgName) {
		super();
		this.pkgName = pkgName;
		this.commando = new Commando();
		
		
	}

	
	public String forceStopApp() {
		
		return "am force-stop "+this.getPkgName();
	}
	
	///////////////////////// Install and Uninstall
	
	
	public void installApk(String apk){
		String command = "adb install "+apk;
		this.commando.executeProcessListPrintOP(command, true);
	}
	
	public void uninstallPackage(String pkgName){
		String command = "pm uninstall "+pkgName;
		this.commando.execRootPrintOP(command);
	}
	
	//////////////////////Backup Data Directory 
	
	public void backupDataDir(String bkpLoc){
		
		File destLoc;
		File appDir = new File(TicklerVars.appTickDir);

		if (appDir.exists())
		{	
			if (bkpLoc == null){
				String timestamp = new SimpleDateFormat("dd-MM-yy_HH.mm.ss").format(new Date());
				destLoc=new File(TicklerVars.ticklerDir+TicklerVars.pkgName+"_bkp_"+timestamp);
			}
			else
				destLoc = new File(bkpLoc);
			
			try{
				
//				FileUtils.moveDirectory(appDir, destLoc);
				Files.move(appDir.toPath(), destLoc.toPath(), StandardCopyOption.REPLACE_EXISTING);
				
//				FileUtils.moveDirectoryToDirectory(destLoc, appDir, true);
				
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	private boolean isReinstallQuestion(){
		OutBut.printWarning("\nDo you want to uninstall the original app and install the modified one?");
		OutBut.printNormal("If yes, the following steps are executed:");
		OutBut.printNormal("\t1- Uninstall the original app");
		OutBut.printNormal("\t2- Backup Tickler working directory, if it exists ");
		OutBut.printNormal("\t3- Install the modified app");
		OutBut.printNormal("\nIf agree, enter yes or y");
		
		String choice = OtherUtil.pressAnyKeySilent();
		if (choice.toLowerCase().equals("yes") || choice.toLowerCase().equals("y"))
			return true;
		
		return false;
	}
	
	/**
	 * After creation of debuggable or MitM, the user can uninstall the original APK and install the modified one
	 * @param apk
	 */
	public void reinstall(String apk){
		if (this.isReinstallQuestion()){
			this.uninstallPackage(TicklerVars.pkgName);
			this.installApk(apk);
			this.backupDataDir(null);
		}
	}
	
	////////////// Getters and Setters 

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public String getSdCardPath() {
		return sdCardPath;
	}


	

}
