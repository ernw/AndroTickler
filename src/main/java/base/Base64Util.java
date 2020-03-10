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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import commandExec.Commando;
import initialization.TicklerVars;

/**
 * Find Base64 values in a directory
 * @author aabolhadid
 *
 */
public class Base64Util {
	private FileUtil fU;
	private SearchUtil searcher;

	public Base64Util(){
		this.searcher = new SearchUtil();
		this.fU = new FileUtil();
	}
	
	/**
	 * @param dir
	 * @param key
	 * @return
	 */
	public ArrayList<String> searchB64inDir(String dir, String key){
		String keyLower = key.toLowerCase();
		ArrayList<String> results = new ArrayList<>();
		List<File> files = this.searcher.search4FileInDir(dir, null);
		
		for (File f : files){
			String decoded = this.fileToBase64(f.getAbsolutePath());
			if (decoded.toLowerCase().contains(keyLower)){
				results.add(f.getAbsolutePath());
			}
		}
		
		return results;
	}
	
	/**
	 * Searches for a key in the DataDir of an app
	 * @param key
	 * @return	Names of the files that contain the base64 value of the key
	 */
	public ArrayList<String> searchB64DataDir(String key){
		return this.searchB64inDir(TicklerVars.dataDir, key);

	}
	
	
	
	/**
	 * Check first if the file is not empty!!!, then read the file and base64 Dec every line
	 * @param filePath
	 * @return
	 */
	public String fileToBase64(String filePath){
		String theFile="", decLine, returnString="";
		String ls = System.getProperty("line.separator");
		
		try{
			theFile=this.fU.readFile(filePath);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		if (!theFile.isEmpty()){
			String[] fileArray= theFile.split(ls);
			
			for (String s:fileArray){
				decLine= this.breakLineBase64Dec(s);
				if (!decLine.isEmpty()|| !decLine.matches("^\\s*$"))
					returnString+= decLine+ls;
			}
		}
		
		return returnString;
	}
	
	/**
	 * Break every line with delimiter of non-char or non-digit (except for = ??) then decode every part
	 * @param line
	 * @return
	 */
	private String breakLineBase64Dec(String line){
		String[] broken = line.split("[^\\w\\d=]");
		String dec="", returnString="", asciiDec="";
		for (String s: broken){
			dec = this.getBase64Dec(s);
			asciiDec = this.getAsciiFromString(dec);
			if (!asciiDec.isEmpty())
				returnString+=asciiDec+" ";
		}
		return returnString;
	}
	
	
	
	public String getAsciiFromString(String complex){

		String pure = complex.replaceAll("[^\\x20-\\x7F]", "");
		return pure;		
	}
	
	/**
	 * Base64 decodes a String
	 * @param orig
	 * @return
	 */
	public String getBase64Dec(String orig){
		byte[] dec = org.apache.commons.codec.binary.Base64.decodeBase64(orig);
		return new String(dec);
	}
	
}
