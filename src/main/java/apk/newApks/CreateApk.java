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

import java.io.File;

import apk.ApkSigner;
import apk.ApkToolClass;
import apk.ApkToolDude;
import apk.AppBroker;
import base.FileUtil;
import cliGui.OutBut;
import exceptions.TNotFoundEx;
import initialization.TicklerVars;
import initialization.TicklerConst;

/**
 * Creates new APK versions of the original APK
 * MITM: a modification to network secrutiy configuration of the app in order to accept user added certificates to trust store
 * @author aabolhadid
 *
 */
public class CreateApk {
	private ApkToolDude apktool;
	private FileUtil ft;
	private ApkSigner signer;
	private String newAppDir,netSecConfFileName,netSecConfFilePath,apk;
	private boolean isMitm;
	private INewApk newApk;
	
	public CreateApk(int apkID){
		// Ahmad: replacing ApkToolClass
		this.apktool = new ApkToolDude();
		this.ft = new FileUtil(); 
		this.signer = new ApkSigner();
		this.newAppDir = TicklerVars.appTickDir+TicklerConst.newAppTempDir;
		this.netSecConfFileName = TicklerConst.mitmXmlName; 
		this.netSecConfFilePath = newAppDir+"res/xml/"+netSecConfFileName;	
		
		this.initNewApp(apkID);
		
		
	}
	
	private void initNewApp(int apkID){
		
		switch(apkID){
		case TicklerConst.debuggable:
			newApk = new Debuggable();
			break;
			
		case TicklerConst.mitm:
			newApk = new NougatMitM();
			break;
			
		}
		
	}
	
	/**
	 * Create a new APK, debuggable or mitm compatible
	 * @param isInstall whether to install the apk after creation 
	 */
	public void createNewApk(){
		boolean successfulSign;
		try {
			OutBut.printWarning("Decompiling and Recompiling of the APK might have some errors, which might lead to incorrect behavior of the modified app");
			this.apk = this.newApk.getNewApkName();
			
			this.ft.copyOnHost(TicklerVars.extractedDir, TicklerVars.newApkTempDir, true);
			this.newApk.changeManifest();
			
			this.apktool.apkToolCompile(this.newAppDir,this.apk);
			this.afterCompilation(false);
			successfulSign =this.signer.signApk(this.apk);
			if (successfulSign)
				this.reinstallNewApk();
		}
		catch(TNotFoundEx e){
			OutBut.printError(e.getMessage());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void createAnyApk(String decompiledDir, String name) {
		
		this.apk = TicklerVars.appTickDir+name;
		OutBut.printStep("Creating a new APK at: "+this.apk);
		
		this.apktool.apkToolCompile(decompiledDir,this.apk);
		this.afterCompilation(true);
		boolean successfulSign =this.signer.signApk(this.apk);
		if (successfulSign)
			this.reinstallNewApk();
		
	}
	
	/**
	 * 
	 * @param isCustom boolean if it's creating a custom APK (not dbg nor mitm) then don't delete the source directory
	 */
	private void afterCompilation(boolean isCustom) {
		if (!isCustom)
			this.ft.deleteFromHost(TicklerVars.newApkTempDir);
		
		if ( this.ft.isExist(this.apk)){
			System.out.println("\n\n");
			OutBut.printStep("App is created successfully at "+this.apk+" \n");
		}
	}

	private void reinstallNewApk(){
		AppBroker broker = new AppBroker(TicklerVars.pkgName);
		broker.reinstall(this.apk);
	}
	
	
}
