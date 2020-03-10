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
package actions;

import java.util.ArrayList;
import java.io.File;
import java.util.AbstractMap.SimpleEntry;

import base.Base64Util;
import base.CopyUtil;
import base.FileUtil;
import base.OtherUtil;
import base.SearchUtil;
import cliGui.OutBut;
import db.DatabaseTester;
import info.InfoGathering;
import initialization.TicklerConst;
import initialization.TicklerVars;

public class Searcher {
	
	private SearchUtil searchUtil;
	private String codeLoc;
	private CopyUtil copyUtil;
	private FileUtil fileUtil;
	
	public Searcher() {
		this.searchUtil = new SearchUtil();
		this.codeLoc = TicklerVars.jClassDir;
		this.copyUtil = new CopyUtil();
		this.fileUtil = new FileUtil();
	}

	
	/**
	 * Searches for a key in decompiled java code and in strings.xml
	 * @param key
	 */
	public void sC(String key,boolean all){
		
		ArrayList<SimpleEntry> hits = this.searchInCodeWithOption(key, all);
		OtherUtil.printSimpleEntryArray(hits, this.codeLoc, "[Java_Code_Dir]");
		
		//Search in Manifest
		ArrayList<SimpleEntry> result = new ArrayList<>();
		
		result = this.searchUtil.search4KeyInDirFName(TicklerVars.tickManifestFile, key);
		if (!result.isEmpty()) {
			OutBut.printH2("Search results in Manifest File");
			OtherUtil.printSimpleEntryArray(result, TicklerVars.tickManifestFile, "[res]");
		}
		
		// Search for a key in res directory and print the results
		this.searchInRes(key);
		
		
	}
		
		private ArrayList<String> sCInFile(File file, String fileName, String key){
			ArrayList<String> result = new ArrayList<>();
			if (file.exists()){
				OutBut.printH2("Searching for "+key+" in "+fileName);
				result = this.searchUtil.findInFile(file, key);
			}
			return result;
		}
		
		/**
		 * Search for a key in the res directory and print the results
		 * @param key
		 * @return
		 */
		private void searchInRes(String key){
			ArrayList<SimpleEntry> result = new ArrayList<>();
			String resDir= TicklerVars.extractedDir+"res/";
			
			result = this.searchUtil.search4KeyInDirFName(resDir, key);
			if (!result.isEmpty()) {
				OutBut.printH2("Search results in res directory");
				OtherUtil.printSimpleEntryArray(result, resDir, "[res]");
			}
			
		}
		
		/**
		 * Search fo rkey in strings and arrays.xml but I don't all it as it is replaced by searchinRes()
		 * @param key
		 */
		private void searchInStringsArrays(String key) {
			File stringsXml = new File(TicklerVars.extractedDir+"res/values/strings.xml");
			File arraysXml = new File(TicklerVars.extractedDir+"res/values/arrays.xml");
			
			ArrayList<String> hits2 = this.sCInFile(stringsXml, "strings.xml", key);
			hits2.addAll(this.sCInFile(arraysXml, "arrays.xml", key));
			
			
			if (!hits2.isEmpty())
				for (String s: hits2)
					OutBut.printNormal(" "+s+"\n");
			
		}
	
	/**
	 * Searches in source code in a custom location.
	 * @param key
	 * @param loc
	 */
	public void scCustomCodeLoc(String key, String codeRoot){
		if (codeRoot != null){
			String codeRootNotHome=codeRoot.replace("~", System.getProperty("user.home"));
			File cR = new File(codeRootNotHome);
			if (cR.exists()){
				this.codeLoc = codeRootNotHome;
				
			}
		
			else
			{
				OutBut.printError("The code location you entered "+codeRoot+" does not exist");
				OutBut.printStep("Using decompiled Java code from the APK....");
			}
		}
		
		this.sC(key,false);
	}
	
	/**
	 * Searches in code based on option: sc_all --> all = true, sc --> all=false
	 * @param key
	 * @param all
	 * @return
	 */
	private ArrayList<SimpleEntry> searchInCodeWithOption(String key, boolean all){
		ArrayList<SimpleEntry> hits = new ArrayList<>();
		
		if (all){
			OutBut.printH2("Searching for "+key+" in Decompiled Java Code");
			hits = this.searchUtil.search4KeyInDirFName(this.codeLoc, key) ;
		}
		else {
			OutBut.printH2("Searching for "+key+" in Decompiled Code of the app");
			hits = this.searchUtil.searchForKeyInJava(key, this.codeLoc);
		}
		
		return hits;
	}
	
	
	
	/**
	 * Search for a key in the Data directory of an App, including files and unencrypted databases.
	 * Also search for the key in clear text and in Base64 format
	 * Also check in External Dir if exists
	 * @param key
	 */
	public void searchForKeyInDataDir(String key, boolean isCopy){
		
		if (isCopy){
			OutBut.printStep("Updating Data Directory");
			CopyUtil copyz = new CopyUtil();
			copyz.copyDataDir();
		}
		else {
			OutBut.printWarning("Data storage directory is not updated");
		}
		//Search in files
		OutBut.printH2("Searching Files in Data Directory of the app");
		ArrayList<SimpleEntry> hits = this.searchUtil.search4KeyInDirFName(TicklerVars.dataDir, key);
		
		OtherUtil.printSimpleEntryArray(hits, TicklerVars.dataDir, "[Data_Dir]");
		
		// Search Base64
		this.base64Search(key);
		
		//Search in DB
		OutBut.printH2("Searching Files in the app's unencrypted databases");
		DatabaseTester db = new DatabaseTester();
		db.searchForKeyInDb(key);

		//search in external memory
		this.searchExternalStorage(key);
	}
	

	
	private void base64Search(String key){
		Base64Util b64 = new Base64Util();
		ArrayList<String> base64Hits = b64.searchB64DataDir(key);
		
		if (!base64Hits.isEmpty()){
			OutBut.printH2("The key is base64 encrypted in the following file(s)");
			for (String s: base64Hits){
				String filePath = s.replaceAll(this.codeLoc, "[Data_Dir]"); 
				System.out.println("#FileName: "+filePath);
			}
			
		}
		
	}
	
	/**
	 * Copy external storage into transfers file
	 * Then saerch in it
	 * Copying code moved to CopyUtil and a new method is created only to search there
	 * @param key
	 */
	/*
	private void searchExternalStorage(String key) {
		InfoGathering info = new InfoGathering();
		FileUtil fU = new FileUtil();
		String extDir = info.getSdcardDirectory().replaceAll("\\n", "");
		String destExtDir=TicklerVars.transferDir+TicklerConst.COPIED_EXTERNAL_STORAGE_NAME;
		if (!extDir.isEmpty()){
			OutBut.printH2("Searching the app's external memory");
			fU.copyDirToHost(extDir, destExtDir,true);
			System.out.println("");
			OutBut.printStep("Copying External Storage Directory: "+extDir+"\n");
			ArrayList<SimpleEntry> hits = this.searchUtil.search4KeyInDirFName(destExtDir, key);
			OtherUtil.printSimpleEntryArray(hits, extDir, "[External_Dir]");
		}
	}*/
	
	/**
	 * Searching for a key in External Directory
	 * @param key
	 */
	private void searchExternalStorage(String key) {
		//OutBut.printStep("Updating External Storage Directory\n");
		if ( fileUtil.isExistOnDevice(TicklerVars.extDataDir)) {
			copyUtil.copyExtDir(TicklerVars.extDataDir);
			ArrayList<SimpleEntry> hits = this.searchUtil.search4KeyInDirFName(TicklerVars.extDataDir, key);
			OtherUtil.printSimpleEntryArray(hits, TicklerVars.extDataDir, "[External_Dir]");
		}
		
	}

}
