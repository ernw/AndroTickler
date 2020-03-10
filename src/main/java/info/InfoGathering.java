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

import java.util.List;
import java.io.File;
import java.util.AbstractMap.SimpleEntry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashSet;

import base.Base64Util;
import base.FileUtil;
import base.SearchUtil;
import cliGui.OutBut;
import code.JavaSqueezer;
//import code.SmaliAnalyzer;
import commandExec.Commando;
import db.DatabaseTester;
import initialization.TicklerVars;

public class InfoGathering {
	
	private SearchUtil searcher;
	private String resXml;
	
	public InfoGathering(){
		this.searcher = new SearchUtil();
		this.resXml = "res/xml/";
		
	}
	
	//////////////////// SD card /////////////////////
	
	public String getSdcardDirectory(){
		return this.searcher.searchOnDevice("/sdcard/Android", TicklerVars.pkgName);
		
	}
	
	/**
	 * Searches in Java and Smali code if the app uses external storage
	 * @return
	 */
	public boolean isExternalStorage(){
		JavaSqueezer jCA = new JavaSqueezer();
		boolean result = jCA.isExternalStorage();
		if (!result)
			result = this.isExternalStorageDirectory(TicklerVars.smaliDir);
		
		return result;
	}
	
	/**
	 * Checks in a directory for the keywords of external storage
	 */
	private boolean isExternalStorageDirectory(String loc){
		
		ArrayList<String> foundArray = searcher.search4KeyInDir(loc, "getExternal");
		
		for (String s:foundArray)
		{
			if (s.contains("getExternalFilesDir") || s.contains("getExternalStoragePublicDirectory")){
				return true;
			}
		}
		
		return false;
		
	}
	
	

	
	//////////////////////////// Interesting things in APK ////////////////////////////
	
	public void getCertificatesInApkDirectory(){
		String[] certExtensions = {".cer",".crt",".der",".csr",".pfx",".p12",".pem",".p7b", ".p7r", ".spc"};
		List<File> result = this.searcher.search4FileInDir(TicklerVars.extractedDir, certExtensions );
		boolean found = false;
		
		for (File f: result){
			found = true;
			OutBut.printNormal(f.getAbsolutePath().replaceAll(TicklerVars.extractedDir, "[ExtractedDir]"));
		}
		
		if (found)
			OutBut.printStep("Where [ExtractedDir] is "+ TicklerVars.extractedDir);
		else
			OutBut.printNormal("None is found");
	}
	
	/**
	 * Checking if the app already patched to work in mitm mode on Android 7
	 * I think I will skip it for now
	 */

	
	public boolean checkTicklerMitmModification(){
		
		File ticklerNSC = new File(TicklerVars.appTickDir+this.resXml+TicklerVars.mitmXmlName);
		if (ticklerNSC.exists()){
			String searchKey="<application android:networkSecurityConfig=\"@xml/"+TicklerVars.mitmXmlName+"\"";
			ArrayList<String> searchResult = this.searcher.findInFile(new File(TicklerVars.tickManifestFile), searchKey);
			if (!searchResult.isEmpty())
				return true;
		}
		return false;
	}
}
