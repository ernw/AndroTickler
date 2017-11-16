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
package info;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import attacks.StartAttack;
import base.OtherUtil;
import base.SearchUtil;
import cliGui.OutBut;
import device.Packagez;
import initialization.TicklerVars;
import manifest.ManifestDealer;

public class InfoGatheringReporting {
	private ManifestDealer dealer;
	private StartAttack startAttack;
	private InfoGathering info;
	private ArrayList<String> infoArrayList;
	private Packagez pkgz;
	
	public InfoGatheringReporting(){
		this.dealer = new ManifestDealer();
		this.startAttack = new StartAttack();
		this.infoArrayList = new ArrayList<String>();
		this.info = new InfoGathering();
		this.pkgz = new Packagez();
	}

	public void report() {
		
		OutBut.printH1("Package Information");		
		this.printArrayList(this.pkgz.dumpInfo());	

		
		////////////////////////// SD card ///////////////////////////////
		
		this.sdcardChecks();
		

		///////////////////////// Manifest Analysis //////////////////////////////////
		this.manifestAnalysis();
		
		OutBut.printH2("Certificates and Keys found in the APK zipped file");
		this.info.getCertificatesInApkDirectory();
		
		
	}
	

	/**
	 * Checks if the app uses external storage
	 */
	private void sdcardChecks(){
		this.infoArrayList.clear();
		
		if (info.isExternalStorage()){
			System.out.println("Code shows that the app uses External storage");
		}
		
		// search in SD card /sdcard/Android
		String sdCardPaths =info.getSdcardDirectory();
		if (sdCardPaths.isEmpty()){
			this.infoArrayList.add("No Directories found for the app in external storage");
		}
		else{
			this.infoArrayList.add("Directories found for the app in external storage:");
			for (String s:sdCardPaths.split("\n")){
				this.infoArrayList.add("\t"+s);
			}
		}
		
		this.printArrayList(infoArrayList);
		
	}
	
	////////////////////////// Manifest Analysis /////////////////////////7
	private void manifestAnalysis(){
		this.infoArrayList.clear();
		
		this.printDebuggableBackable();
		
		this.isTicklerMitM();
		
		OutBut.printH2("Uses Permissions");
		this.printArrayList(this.dealer.manAn.getUsesPermissions());
		
		OutBut.printH2("Content URIs in Code");
		this.printArrayList(this.fetchContentUris());
		
		this.printSchemes();
		
		this.getMetaData();

	}
	
	private void printDebuggableBackable(){
		this.dealer.analyzeManifest(TicklerVars.tickManifestFile);
		infoArrayList.add("Backable: "+this.dealer.manAn.getManifest().getApplication().isAllowBackup());
		infoArrayList.add("Debuggable: "+this.dealer.manAn.getManifest().getApplication().isDebuggable());
		
		this.printArrayList(this.infoArrayList);
	}
	
	public ArrayList<String> fetchContentUris(){
		this.startAttack.provAtt.getContentURIsFromSmali(TicklerVars.smaliDir);
		return this.startAttack.provAtt.getContentURIs();
	}
	
	private void printSchemes(){
		ArrayList<String> schemes = this.dealer.manAn.getSchemes();
		if (!schemes.isEmpty()){
			OutBut.printH2("Data Schemes");
			schemes = OtherUtil.removeDuplicates(schemes);
			this.printArrayList(schemes);
		}
	}
	
	private void isTicklerMitM(){
		if (this.info.checkTicklerMitmModification()){
			OutBut.printNormal("\nThe app is already patched by Tickler to accept Certificates added by users to Trust store. \n"
					+ "You can use Burp as MitM with the app if installed on Android 7.x");
		}
		//else
			//OutBut.printNormal("\nThe app is NOT patched by Tickler to work with Burp if installed on Android 7.x device\n"
			//		+ "You can run Tickler with -mitm flag to patch the app, if you're using an Android 7.x device");
	}
	
	private void getMetaData(){
		SearchUtil sU = new SearchUtil();
		ArrayList<String> result = sU.findInFile(new File(TicklerVars.tickManifestFile), "meta-data");
		
		if (!result.isEmpty()){
			OutBut.printH2("Meta-data in Manifest file");
			this.printArrayList(result);
		}
	}

	////////////////////////////////////////
	
	
	private void printArrayList(ArrayList<String> list){
		for (String s:list)
			System.out.println(s);
	}
	
	
}
