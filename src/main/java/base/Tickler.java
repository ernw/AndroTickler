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
package base;


import java.io.File;
import java.util.ArrayList;

import org.apache.commons.codec.binary.StringUtils;

import actions.Comparer;
import actions.Searcher;
import actions.Snapshots;
import apk.Decompiler;
import apk.newApks.CreateApk;
import attacks.ActivityStarter;
import attacks.Broadcaster;
import attacks.StartAttack;
import cliGui.OutBut;
import code.JavaSqueezer;
import components.IActivityService;
import components.IComponent;
import components.Manifest;
import components.Receiver;
import db.DatabaseTester;
import device.Packagez;
import exceptions.TNotFoundEx;
import frida.FridaCli;
import info.InfoGathering;
import info.InfoGatheringReporting;
import info.ListComponents;
import initialization.TicklerChecks;
import initialization.TicklerConst;
import initialization.TicklerVars;
import manifest.ManifestDealer;

public class Tickler {
	private Manifest manifest;
	private ManifestDealer dealer;
	private ActivityStarter actAttacker; 
	private boolean isLog;
	private StartAttack startAttack;
	private DatabaseTester dbTest;
	private CreateApk newApk;
	private Searcher searcher;
	private CopyUtil copyz;
	private Snapshots snaps; 
	private Comparer comps;
	
	// If offline not compatible --> raise exception when initalizeTicklerNoDevice runs (not to fuck up the whoile code)
	private boolean isOfflineCompatible = true;
	private boolean isDev=true;

	/**
	 * 
	 * @param mode: Tickler's operation mode
	 * 		- pkg:	Package mode (app) and device needed
	 * 		- offline:	Package mode and device is not needed
	 * 		- noPkg:	App not-related ops, such as generic snapshot
	 * @param pkgName
	 */
	public Tickler(String mode, String pkgName) {
		this.inits();
		
		
//		if(mode.equals("pkg")){
//			this.ticklerPackageInit(pkgName);
//		}
//		else if (TicklerVars.pkgName == null){
//				TicklerChecks tc = new TicklerChecks();
//				tc.loadConfiguration();
//				TicklerVars.updateVars("NoPackage");								
//		}
		
		if (mode.equals("noPkg")) {
			TicklerChecks tc = new TicklerChecks();
			tc.loadConfiguration();
			TicklerVars.updateVars("NoPackage");	
			return;
		}
		
		else if (mode.equals("offline")) {
			this.isDev= false;
			
		}
		//else
			this.ticklerPackageInit(pkgName, this.isDev);
			
	}
	/**
	 * Instantiates necessary classes to start Tickler
	 */
	private void inits() {
		this.copyz = new CopyUtil();
		this.snaps = new Snapshots();
		this.comps = new Comparer();
	}
	
	/**
	 * Initiates Tickler in Package mode, whether online or offline
	 * @param pkgName
	 * @param isDev
	 */
// For now isDev = true --> work online mode: needs a device alwasys connected
	
	private void ticklerPackageInit(String pkgName,boolean isDev){
		
		try{
			this.runTicklerChecks(pkgName, isDev);
			this.dealer = new ManifestDealer();
			this.dealer.meetThePackage(pkgName);
			
			if (!this.dealer.wasApkExist())
				this.copyDataDir(null);
		}
		catch(TNotFoundEx e)
		{
			OutBut.printError(e.getMessage());
			System.exit(0);
		}
	}


	private void runTicklerChecks(String pkgName, boolean isDev) throws TNotFoundEx {
		TicklerChecks tc = new TicklerChecks();
		if (isDev)
			tc.initiaizeTickler(pkgName);
		else
			tc.initalizeTicklerNoDevice(pkgName);
	}
	
//////// Start components //////////
	

	
	/**
	 * Start components (All vs. comp type, exported vs. all)
	 * @param compType
	 * @param exported
	 */
	public void start(int compType, boolean exported){
		
		this.prepareComponentAttack();
		this.triggerGroup(compType, exported);
	}
	
	/**
	 * Triggers groups of components (all or just one type, exported or not)
	 * @param compType
	 * @param exported
	 */
	private void triggerGroup(int compType, boolean exported) {
		//1- Get components based on type and exported values
		ArrayList<IComponent> components = this.dealer.getComponentsOfType(compType, exported);
		
		//2- Get commands strings
		ArrayList<String> commands = this.startAttack.attackComponents(components);
		
		//3- Add Prov commands
		if ((compType == TicklerConst.PROVIDER || compType == TicklerConst.ALLCOMPS) && !exported){
			commands.addAll(this.startAttack.queryUrisFromSmali());
		}
		
		//4- Start commands
		this.startAttack.executeTriggerCommands(commands, exported);
		
		//5- Stop logger
		this.startAttack.stopLogging();
	}
	
		/**
		 * Trigger a component by name
		 * @param compName
		 */
	public void attackComponent(String compName){
		this.prepareComponentAttack();
		
		if (this.dealer.isComponentExist(compName))
		{
			IComponent comp = this.dealer.getComponentByName(compName);
			ArrayList<String> commands = this.startAttack.attackComponent(comp);
			
			this.startAttack.executeTriggerCommands(commands, false);
			
		}
		else
		{
			OutBut.printError("No component of name "+compName+" found in the Manifest file\n"
					+ "Use -l command to display all components of this app");
		}
			this.startAttack.stopLogging();
	}
	
	/**
	 * Preparations before any kind of component triggering
	 */
	public void prepareComponentAttack() {
		//1- Analyze manifest and get the components
		this.dealer.analyzeManifest(TicklerVars.tickManifestFile);
		
		//2- Instantiate attacker
		this.startAttack = new StartAttack();
		
		//3- Get Launchers but removed
		
		//4- Set logger to True, in order to write the command in the file
		this.startAttack.setLogger(this.isLog);
		
	}
	

///////////// copy  /////////////////
	/**
	 *  Copy local and external data directories of the app to the Tickler folder
	 * @param name
	 */
	
	public void copyDataDir(String name){
		
		copyz.copyStorage(name);
//		copyz.copyDataDirName(name);
//		copyz.copyDataDir(name+"/DataDir");
//		copyz.copyExtDir(name+"/ExtDataDir");
		
	}
	
	//Copy any file / directory from the device to the host
	public void copyToHost(String src, String dest){
	
		copyz.copyToHost(src, dest);
	}
	
	
	///////////////////////////// Databases //////////////////////////
	/**
	 * Handles DB actions (check encryption, list DB, search)
	 * @param param		Database action
	 * @param isCopy	if false, don't update DataDir
	 */
	public void databases(String param,boolean isCopy){
		this.dbTest= new DatabaseTester();
		
		// Update DataDir?
		if (isCopy) {
			// Update DataDir whether it exists or not
			OutBut.printStep("Updating Data Directory");
			this.copyDataDir(null);
		}
		
		this.dbTest.dbOption(param);
	}
	
	//////////////////// Other functions //////////////////
	
	public void snapshot(){
		
		this.snaps.takeSnapshot();
		
	}
	
	
	/**
	 * Create a debuggable version of the app
	 */
	public void createDebuggable(){
		this.newApk = new CreateApk(TicklerConst.debuggable);
		this.newApk.createNewApk();
	}
	
	/**
	 * Solves the MitM issue with Android Nougat (netsecConfiguration)
	 */
	public void createNougatMitM(){
		this.newApk = new CreateApk(TicklerConst.mitm);
		this.newApk.createNewApk();
	}
	
	public void createCustomAPK(String dir, String name){
		this.newApk = new CreateApk(10);
		this.newApk.createAnyApk(dir, name);
	}

	public void decompileApk(){
		Decompiler d2j = new Decompiler();
		d2j.dex2jar();
	}
	
	/**
	 * Copies the directory of background images from the android device to the host
	 */
	public void backgroundSnapshots(){
		
		this.snaps.getBackGroundSnapshots();
	}
	

	//Copy data directory twice, while you can go crazy in between. Then compares the 2 directories
	public void diffDataDir(String detailed){
		OutBut.printH1("Data Directory changes");
		if (detailed == null)
			this.comps.diff(false);
		else if (detailed.equalsIgnoreCase("detailed")|| detailed.equalsIgnoreCase("d"))
			this.comps.diff(true);
		else{
			System.out.println("!!!!!!! WARNING: unknown parameter '"+detailed+"'. \n"
					+ "!!!!!!! Showing only the names of the changed files\n"
					+ "!!!!!!! For detailed comparison use -diff detailed or -diff d flag\n");
			this.comps.diff(false);
		}
	}
	
	/**
	 * Print version number
	 */
	public void version(){
		OutBut.printNormal("Tickler version: "+TicklerConst.version);
	}
	///////////////////// List ////////////////////
	
	
	public void list(int compType, boolean exported,boolean details){
		ListComponents list = new ListComponents();
		list.listThis(compType, exported,details);
	}
	
	
	public void listComponent(String compName){
		ListComponents list = new ListComponents();
		list.listComponent(compName);
	}
	
	///////////// General Information /////////////
	public void informationGathering() {
		InfoGatheringReporting infoRep = new InfoGatheringReporting();
		infoRep.report();
	}

	public void printPackages(){
		Packagez pkgz = new Packagez();
		pkgz.printInstalledPkgs();
	}
	
	public void squeezeCode(String codeLoc){
		JavaSqueezer disc = new JavaSqueezer();
		
		if (codeLoc != null && codeLoc.equals("short")){
			int index1 = TicklerVars.pkgName.indexOf('.')+1;
			String subloc= TicklerVars.pkgName.substring(0,TicklerVars.pkgName.indexOf('.', index1));
			codeLoc = TicklerVars.jClassDir+"/"+subloc.replaceAll("\\.", "/");
		}
		disc.report(codeLoc);
	}
	
	/////////////// Search //////////////////
	public void searchPackage(String key){
		Packagez pkgz = new Packagez();
		pkgz.searchPackage(key,true);
	}
	

	
	public void searchInCode(String key,String codeLoc){
		this.searcher = new Searcher();
		this.searcher.scCustomCodeLoc(key, codeLoc);
//		this.searcher.sC(key,false);
	}
	
	public void searchInCodeAll(String key){
		this.searcher = new Searcher();
		this.searcher.sC(key,true);
	}
	
	public void searchInDataDir(String key, boolean isCopy){
		this.searcher = new Searcher();
		this.searcher.searchForKeyInDataDir(key,isCopy);
	}
	
	public void b64Search(String key)
	{
		Base64Util b64 = new Base64Util();
		ArrayList<String> result = b64.searchB64inDir(TicklerVars.dataDir, key);
		
		for (String s : result)
			OutBut.printNormal(s);
	}
	

	public boolean isLog() {
		return isLog;
	}

	public void setLog(boolean log) {
		this.isLog = log;
		FileUtil ft=new FileUtil();
		ft.createDirOnHost(TicklerVars.logDir);
	}
	
	///////////////////////////// Frida //////////////////////////////
	
	public void frida(String[] args, boolean reuse){
		FridaCli fridaCli = new FridaCli();
		fridaCli.fridaThis(args, reuse);
	}
	
}
