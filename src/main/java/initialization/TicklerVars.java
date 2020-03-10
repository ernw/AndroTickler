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
package initialization;

public abstract class TicklerVars {

	public static String ticklerDir;
	public static String pkgName;
	public static boolean isLib,isOffline;
	public static String jarPath, configPath;
	public static String appTickDir,sdCardPath, tickManifestFile,extractedDir,dataDir
	,smaliDir, imageDir, logDir, bgSnapshotsDir,transferDir, libDir, libJarDir, libNotJarLib, dex2jarPath, apktoolPath,
	dex2jarDir,jClassDir,keyStore,newApkTempDir,mitmXmlName, fridaServerLoc, fridaScriptsDir,extDataDir, squeezeFile;
	public static String version="2.2";
		
	public static void setPkgName(String pName){
		if (TicklerVars.pkgName!=null && !TicklerVars.pkgName.equals(pName)){
//			System.out.println("WARNING: Changing package name from "+TicklerVars.pkgName+" to "+pName);		
		}
		TicklerVars.pkgName = pName;
	}
	
	public static void updateVars(String pName){
		setPkgName(pName);
		appTickDir = ticklerDir+pkgName+"/";
		tickManifestFile = appTickDir+TicklerConst.MANIFEST_NAME;
		sdCardPath = TicklerConst.sdCardPathDefault+pkgName +"/";
		dataDir = appTickDir+TicklerConst.DATA_DIR_NAME;
		extractedDir = appTickDir+TicklerConst.EXTRACTED_NAME;
		smaliDir = extractedDir+TicklerConst.SMALI_DIR_NAME;
		imageDir= appTickDir+TicklerConst.IMAGES_DIR_NAME;
		logDir = appTickDir+TicklerConst.LOGS_DIR_NAME;
		bgSnapshotsDir = appTickDir+TicklerConst.BG_DIR_NAME;
		transferDir = appTickDir+TicklerConst.TRANSFER_DIR_NAME;
		extDataDir = appTickDir+TicklerConst.EXTERNAL_STORAGE_Dir; 
		
		libDir=jarPath+TicklerConst.generalLibName;
		libJarDir=libDir+TicklerConst.jarsLibName;
		libNotJarLib=libDir+TicklerConst.notJarsLibName;
		
		// Will be set anyway whether they exist or not
		
		dex2jarPath = libNotJarLib+TicklerConst.DEX2JAR_EXEC;
		dex2jarDir = appTickDir+TicklerConst.DEX2JAR_OP_DIR_NAME;
		apktoolPath = libJarDir+TicklerConst.APKTOOL_FILE_NAME;
		
		// Output of Java classes
		jClassDir = appTickDir+TicklerConst.JAVA_CODE_DIR_NAME;
		keyStore = libNotJarLib+TicklerConst.KEY_STORE_DIR_NAME;
		newApkTempDir = appTickDir+TicklerConst.newAppTempDir;
		
		fridaScriptsDir = appTickDir+TicklerConst.FRIDA_SCRIPTS_DIR_NAME;
		squeezeFile = appTickDir+TicklerConst.SQUEEZE_FILE_NAME;
		
	}
	
	public static void setTicklerDir(String dir){
		ticklerDir = dir;
	}
	
	public static String replaceSpace(String s){
		return s.replaceAll("\\s", "\\\\s");
	}

}
