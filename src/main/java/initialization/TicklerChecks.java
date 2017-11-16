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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.CodeSource;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apk.Decompiler;
import base.FileUtil;
import base.OtherUtil;
import base.SearchUtil;
import base.Tickler;
import cliGui.OutBut;
import commandExec.Commando;
import device.Packagez;
import exceptions.TNotFoundEx;
import info.InfoGathering;

public class TicklerChecks {

	private Commando commando; 
	private String PKG_NOT_FOUND="Package $P does not exist on the connected device. Run -pkgs to see all installed packages";
	
	public TicklerChecks(){
		this.commando = new Commando();
	}
	
	
	////////////////////////Initiatlization ///////////////////////
	/**
	 * Initializazion:
	 * 1) check devices if isDevice is true
	 * 2)host requirements (tools exist)
	 * 3) load configurations from conf file
	 * 4) Update TicklerVars
	 * 5) create essential dirs
	 * 6) check external library directory
	 * @param pkgName
	 * @param isDev
	 * @throws TNotFoundEx
	 */
	public void initiaizeTickler(String pkgName,boolean isDev) throws TNotFoundEx{

		Packagez pkg = new Packagez();
		if (isDev){
			this.checkDevices();
		}
		
		this.checkRequirements();
		
		this.loadConfiguration();
	
		if (!pkg.isPackageExist(pkgName)){
			throw new TNotFoundEx(this.PKG_NOT_FOUND.replaceAll("\\$P", pkgName));
		}
		
		TicklerVars.updateVars(pkgName);
		
		this.createEssentialDirs();	
		this.checkExternalLibDir();
		this.isDex2Jar();
	
	}
	
	
	public void initalizeTicklerNoDevice(String pkgName) throws TNotFoundEx{
	this.loadConfiguration();
	TicklerVars.updateVars(pkgName);
	this.checkExternalLibDir();
}

	/**
	 * Check if only one device is connected to the host
	 * @throws TNotFoundEx
	 */
	public void checkDevices() throws TNotFoundEx{
		String command = "adb devices -l";
		Commando commando = new Commando();
		String op = commando.executeCommand(command);
		
		OtherUtil oU = new OtherUtil();
		ArrayList<String> devices = oU.getRegexFromString(op, "(model:.*?device:.+$?)");
		int eligDevices = devices.size();
		
		if (eligDevices>1)
			throw new TNotFoundEx("ERROR: 2 or more Android devices are connected to the host, please connect only one Android device.");
		if (eligDevices == 0)
			throw new TNotFoundEx("ERROR: No Android devices detected by the host. Execute adb devices -l to check the connected devices");
			
	}
	
	/**
	 * Check if the host has the following tools: adb, sqlite3, strings
	 * @throws TNotFoundEx
	 */
	public void checkRequirements() throws TNotFoundEx{
		
		ArrayList<SimpleEntry> toolsCheck = new ArrayList<>();
		
		//adb
		toolsCheck.add(new SimpleEntry<String, String>("adb", "adb devices"));
		//sqlite3
		toolsCheck.add(new SimpleEntry<String, String>("sqlite3", "sqlite3 -version"));
		//strings
		toolsCheck.add(new SimpleEntry<String, String>("strings", "strings -v"));
		
		boolean pass = this.checkToolOnHost(toolsCheck);
		
		if (!pass)
			throw new TNotFoundEx("Missing tools on host: Please install the missing tools and rerun the command");
	}
	
	/**
	 * Executes a sample command of each tool to detect if it exists
	 * ToDo: it prints stack trace which is not cool
	 * @param toolCheck
	 * @return false if any required tool doesn't exist, otehrwise true
	 */
	private boolean checkToolOnHost(ArrayList<SimpleEntry> toolCheck) throws TNotFoundEx{
		int result=13;
		String cmd="";
		boolean pass=true;
		for(SimpleEntry s: toolCheck){
			cmd = s.getValue().toString();
			result = this.commando.executeProcessListPrintOP(cmd, false);

				if (result != 0){
					OutBut.printError("Tool "+s.getKey().toString()+" does not exist on the host, please install it first");
					pass=false;
				}
			}
		return pass;
	}
	
	private void isDex2Jar() throws TNotFoundEx {
		Decompiler dec = new Decompiler();
		if (!dec.isDex2Jar())
			throw new TNotFoundEx("Cannot run Dex2Jar tool. Make sure it exists in "+TicklerVars.libNotJarLib+" directory and that d2j-dex2jar.sh and d2j_invoke.sh are executable");
	}
	
	/**
	 * Load configurations from the config file
	 */
	public void loadConfiguration(){
		
		String jarLoc = this.getJarLocation();
		TicklerVars.jarPath = jarLoc;
		TicklerVars.configPath=TicklerVars.jarPath+TicklerConst.configFileName;
		
		//Read configs from conf file
		if (new File(TicklerVars.configPath).exists()){
			try {
				String line;
				BufferedReader reader = new BufferedReader(new FileReader(TicklerVars.configPath));
				while ((line =reader.readLine())!= null) {
					if (line.contains("Tickler_local_directory")){
						String loc = line.substring(line.indexOf("=")+1, line.length());
						TicklerVars.ticklerDir = this.correctJarLoc(loc);
					}
					else if (line.contains("Tickler_sdcard_directory")){
						String loc = line.substring(line.indexOf("=")+1, line.length()-1);
						TicklerVars.sdCardPath = this.correctJarLoc(loc);
					}
					else if (line.contains("Frida_server_path")){
						String loc = line.substring(line.indexOf("=")+1, line.length());
						TicklerVars.fridaServerLoc = loc;
					}
				
				}
				reader.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		//Config path does not exist
		
		else
		{
			System.out.println("WARNING...... Configuration file does not exist!!!!\nThe following default configurations are set:\n");
			TicklerVars.ticklerDir = TicklerVars.jarPath+TicklerConst.defaultTicklerDirName;
			System.out.println("Tickler Workspace directory on host: "+TicklerVars.ticklerDir);
			System.out.println("Tickler temporary directory on device: "+TicklerConst.sdCardPathDefault);
		}
		
		String x = TicklerVars.ticklerDir;
		if (TicklerVars.ticklerDir == null || TicklerVars.ticklerDir.matches("\\s*/") ){
			TicklerVars.ticklerDir = TicklerVars.jarPath+TicklerConst.defaultTicklerDirName;
//			OutBut.printWarning("Configuration File "+TicklerVars.configPath+ " doesn't specify Tickler_local_directory. Workspace is set at "+ TicklerVars.ticklerDir);
			OutBut.printStep("Tickler Workspace directory on host: "+TicklerVars.ticklerDir);
		}
		
		if (TicklerVars.sdCardPath == null || TicklerVars.sdCardPath.matches("\\s*/")) {
			TicklerVars.sdCardPath = TicklerConst.sdCardPathDefault;	
//			OutBut.printWarning("Configuration File "+TicklerVars.configPath+ " doesn't specify Tickler's temp directory on the device. It is set to "+ TicklerVars.sdCardPath);
			OutBut.printStep("Tickler temporary directory on device: "+TicklerConst.sdCardPathDefault);
		}
			
	}
	
	public String getJarLocation(){
		File myJar;
		try{
			
			File myJar1 = new File(System.getProperty("java.class.path"));
			myJar = myJar1.getAbsoluteFile().getParentFile();
		}
		catch(Exception e){
			myJar = new File(".");
		}
		
		String jarLoc = this.correctJarLoc(myJar.getAbsolutePath());
		return jarLoc;
	}
	
	public void getLibName(){
		SearchUtil searcher = new SearchUtil();
		String[] ext = {"jar"};
		String x = TicklerVars.jarPath;
		String JarName = searcher.search4FileInDir(TicklerVars.jarPath, ext ).get(0).getName();
		
	}
	
	
	/**
	 * Checks if the tool has external Lib directory or a stand alone Jar file
	 * @return
	 */
	private void checkExternalLibDir() throws TNotFoundEx{
		String jarLoc = this.getJarLocation();
		String libDirLoc=jarLoc+TicklerConst.generalLibName;
		File tickLib = new File (libDirLoc);
		if (tickLib.exists()){
			TicklerVars.isLib = true;
			TicklerVars.libDir = libDirLoc;
		}
		//Lib directory not found
		else
		{
			throw new TNotFoundEx("Lib directory not found. \nMake sure that "+TicklerConst.generalLibName+" directory exists in the same directory as Tickler.jar");
		}
		
	}
	
	public String correctJarLoc(String jarLoc){
		String finalLoc=jarLoc;
		if (jarLoc.contains(":"))
			finalLoc = jarLoc.substring(0, jarLoc.indexOf(":"));
		
		Matcher m = Pattern.compile("\\s+(.+)").matcher(jarLoc);
		if (m.find())
			finalLoc = m.group(1);
		if (finalLoc.matches(".+\\n$")){
			finalLoc = finalLoc.substring(0, jarLoc.length()-1);
		}
		if (finalLoc.matches(".+\\.$")){
			finalLoc = finalLoc.substring(0, jarLoc.length()-1);
		}
		if (!finalLoc.matches(".+/$")){
			finalLoc = finalLoc+"/";
		}
		
		return finalLoc;
	}
	
	
	/**
	 * Create sdCardPath and App directory in TicklerDir if they don't exist. 
	 */
	private void createEssentialDirs() throws TNotFoundEx{
		FileUtil fT = new FileUtil();
		fT.createDirOnDevice(TicklerVars.sdCardPath);
		if (!fT.isExistOnDevice(TicklerVars.sdCardPath))
			throw new TNotFoundEx("Cannot create Tickler directory "+TicklerVars.sdCardPath+". Check your configurations in Tickler.conf file");
		fT.createDirOnHost(TicklerVars.appTickDir);
		if (!fT.isExist(TicklerVars.appTickDir))
			throw new TNotFoundEx("Cannot create Tickler directory "+TicklerVars.appTickDir+". Check your configurations in Tickler.conf file");
		
	}
	
	private boolean isPkgNameExist(String pkgName){

		Packagez pkgz = new Packagez();
		return pkgz.isPackageExist(pkgName);

	}
	
	
}
