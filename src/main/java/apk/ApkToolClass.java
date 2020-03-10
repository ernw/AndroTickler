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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;

import base.FileUtil;
import brut.androlib.Androlib;
import brut.androlib.ApkDecoder;
import brut.androlib.ApkOptions;
import cliGui.OutBut;
import commandExec.Commando;
import exceptions.TNotFoundEx;
import initialization.TicklerVars;

/**
 * Responsible for APKtool actions
 * @author aabolhadid
 *
 */
public class ApkToolClass {

	String debuggableApk;
	public ApkToolClass(){
		this.debuggableApk = TicklerVars.appTickDir+"debuggable.apk";
	}
	
	/**
	 * APK tool decompiles the apk into Manifest, res dir and Smali files,
	 * @throws TNotFoundEx if apktool fails to get the manifest file
	 * @param apkPath
	 */
	public void apkToolDecode(String apkPath) throws TNotFoundEx{
		FileUtil fileT = new FileUtil(); 
		brut.androlib.ApkDecoder dec = new ApkDecoder();
		File apkPathFile = new File(apkPath);

		dec.setApkFile(apkPathFile);
		this.deleteExistingDecodedDir();
		
		try {
			File file = new File("/dev/null");
			PrintStream ps = new PrintStream(new FileOutputStream(file));
			System.setErr(ps);
			dec.setOutDir(new File (TicklerVars.extractedDir));
			dec.decode();
			System.setErr(System.err);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			fileT.copyOnHost(TicklerVars.extractedDir+"AndroidManifest.xml", TicklerVars.tickManifestFile,true);
		}
		catch(Exception e){
			OutBut.printError("Decompilation failed and Manifest cannot be obtained");
			TNotFoundEx eNotFound = new TNotFoundEx("Decompilation failed and Manifest cannot be obtained");
			throw eNotFound;
		}
	}
	
	/**
	 * Compile a customized app to an apk
	 * @param dirPath the directory of the modififed app
	 * @param apkPath output apk
	 */
	public void apkToolCompile(String dirPath, String apkPath){
		String tempApktoolLog = TicklerVars.logDir+"debuggableCompile.log";
		ApkOptions apkOptions = new ApkOptions();
		
		try{
			File file = new File(tempApktoolLog);
			OutBut.printH3("APKTool output:");
			new Androlib(apkOptions).build(new File(dirPath), new File(apkPath));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	private void deleteExistingDecodedDir(){
		File decodedDir = new File (TicklerVars.extractedDir);
		if (decodedDir.exists()){
			try{
				FileUtils.deleteDirectory(decodedDir);
				System.out.println("Existing SMALI directory will be deleted....");
			}
			catch(IOException e){
				System.out.println("Cannot delete old extracted directory "+decodedDir.getAbsolutePath()+"\nPlease delete it manually and repeat");;
			}
		}
		
	}
}
