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
package manifest;

import java.io.File;
import java.util.ArrayList;
import apk.ApkToolClass;
import apk.ApkToolDude;
import apk.Decompiler;
import base.DOMXMLReader;
import base.FileUtil;
import base.XMLReader;
import cliGui.OutBut;
import commandExec.Commando;
import components.Activity;
import components.IComponent;
import components.Manifest;
import exceptions.TNotFoundEx;
import initialization.TicklerConst;
import initialization.TicklerVars;

/**
 * Dunno how to call this class. 
 * !!!!!!!!!!!! So far, it's the class that gives back Tickler its Manifest !!!!!!!!!!!!!!
 * @author aabolhadid
 *
 */
public class ManifestDealer {
	private String manifestPath,pkgName,appTickDir;
//	private XMLReader xmlreader;
	private DOMXMLReader domXmlreader;
	public ManifestAnalyzer manAn;
	private FileUtil fileT;
	private ApkToolDude apkTool;
	private boolean apkExisted = true;

	/////////////////////// Package option /////////////////////////
	public void meetThePackage(String pkgName){
		this.pkgName = pkgName;
		fileT = new FileUtil();
		this.appTickDir = TicklerVars.appTickDir;
		// New ApkClass
//		this.apkTool = new ApkToolClass();
		this.apkTool = new ApkToolDude();
		
		//1- create apk tickler directory if it does not exist
		fileT.createDirOnHost(this.appTickDir);
		
		//2- Copy APK from device if it does not exist in the app tickler directory
		this.copyApkFromDevice(); 
		

		//3- Extract Manifest from APK if Manifest file does not exist
		if (!new File(this.appTickDir+"/AndroidManifest.xml").exists())
		{ 
			String apkName = this.getApkName();
			String x = this.appTickDir+apkName;
			try {
				this.apkToolDecode(x);
				OutBut.printStep("Extracting Manifest file from APK");
			}
			catch(TNotFoundEx e){
				OutBut.printError("Cannot extract Manifest file from APK");
			}
		}
		
		//4- Compile the apk using Dex2Jar
		this.dex2Jar();
		
	}
	
	/**
	 * Decompile the APK using dex2jar tool
	 */
	private void dex2Jar(){
		if (!new File(TicklerVars.jClassDir).exists()){
			Decompiler d2j = new Decompiler();
			d2j.decompile();
		}

	}
	
	
	/**
	 * If APK does not exist in the package directory on the host, then Ticker fetches the apk
	 */
	private void copyApkFromDevice() {
		if (!this.isApkExist()){
			OutBut.printStep("Copying APK file from the device");
			fileT.copyDirToHost(this.getPackageApkPath(), this.appTickDir,true);
			this.apkExisted = false;
		}

	}
	
	private boolean isApkExist(){
		if (TicklerVars.isOffline) {
			String [] apkArray = {"apk"};
			if (this.fileT.listFilesInDirContain(TicklerVars.appTickDir, apkArray )!= null)
				return true;
		}
		String apkPath = this.getPackageApkPath();
		String apkName = this.getFileNameFromPath(apkPath);
		File apk = new File(TicklerVars.appTickDir+apkName);
		return apk.exists();
	}
	
	///////////////////// APK tool Encapsulation ///////////////////////
	private void apkToolDecode(String apkPath) throws TNotFoundEx{
		OutBut.printStep("Decompiling the APK file using APKTool");
		this.apkTool.apkToolDecode(apkPath);
	}
		
	
	private String getPackageApkPath(){
		String command = "pm path "+this.pkgName;
		Commando c = new Commando();
		String result = c.execADB(command);
		return result.substring(result.indexOf(":")+1,result.indexOf("\n"));
	}
	
	public String getApkName(){
		String apkName2;
		if (TicklerVars.isOffline) {
			//Name will always be base.apk
			apkName2 = "base.apk";
			
		}
		else {
			String apkName = (new File(this.getPackageApkPath())).getName();
			 apkName2 = apkName.substring(0,apkName.indexOf(".apk")+4);
			String filePath = this.appTickDir+"/"+apkName2+".apk";
		}
		return apkName2;
	}
	
	/////////////////////////////// Common: For all ////////////////////////////////
	
	public void analyzeManifest(String manifestPath){
		Manifest theManifest = this.generateManifestFromXML(manifestPath);
		this.setPkgName(TicklerVars.pkgName);
		this.manAn = new ManifestAnalyzer(theManifest);
	}
	
	// Modified : replacing xmlreader with DOMXmlreader
	public Manifest generateManifestFromXML(String path){
//		this.xmlreader = new XMLReader(path);
		this.domXmlreader = new DOMXMLReader(path);
		Manifest theManifest = this.domXmlreader.parselManifest();
		return theManifest;
//		return this.xmlreader.getManifest();
	}

	private String getFileNameFromPath(String path){
		File absPath = new File(path);
		String theName = absPath.getName();
		if (absPath.isDirectory())
			theName=theName+"/";
		
		return theName;
		
	}
	
	
	/////////////// Components //////////////
	public ArrayList<IComponent> getComponentsOfType(int i, boolean exp) {
		this.analyzeManifest(TicklerVars.tickManifestFile);
		if (i == TicklerConst.ALLCOMPS)
			return this.manAn.getComponents(exp);
		return this.manAn.getComponentsOfType(i,exp);
	}
	
	/**
	 * Checks if a component with this name exists
	 * @param compName
	 * @return
	 */
	public boolean isComponentExist(String compName){
		if (this.manAn.getComponentByName(compName) != null)
			return true;
		else
			return false;
	}
	
	/**
	 * Returns a component of a given name, should be used if isComponentExist = true
	 * @param compName
	 * @return
	 */
	public IComponent getComponentByName(String compName){
		return this.manAn.getComponentByName(compName);
	}
	
	
	
	////////////////////// Getters and setters
	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		if (TicklerVars.pkgName!=null && !TicklerVars.pkgName.equals(pkgName)){
			System.out.println("WARNING: Changing package name from "+TicklerVars.pkgName+" to "+pkgName);		
		}
		this.pkgName = pkgName;
	}
	
	public String getManifestPath() {
		return this.manifestPath;
	}

	public void setManifestPath(String path) {
		this.manifestPath = path;
	}

	public boolean wasApkExist() {
		return apkExisted;
	}


}
