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
package code;

import java.util.ArrayList;

import base.FileUtil;
import initialization.TicklerVars;

/**
 * Extras in Java code
 * @author aabolhadid
 *
 */
public class ExtrasUtil {
	private FileUtil fT;
	
	public ExtrasUtil(){
		this.fT = new FileUtil();
	}
	
	
	/**
	 * Checks if the App is already decompiled in the proper directory
	 * @return
	 */
	public boolean isJClassDir() {
		if (this.fT.isExist(TicklerVars.jClassDir))
			return true;
		
		System.out.println("!! ERROR: Decompiled code does not exist");
		return false;
	}
	
	/**
	 * Gets extras of a component
	 * @param className
	 */
	public String getExtras(String className){
		if (this.isJClassDir())
			return this.getExtrasIfCodeExists(className);
		return "!! ERROR: run dex2jar first";
	}
	
	/**
	 * Gets the extras syntax of a class in one string (e.g. -e Var1 Val1 --ez Var2 Val2)
	 * @param className
	 */
	private String getExtrasIfCodeExists(String className){
		
		String cName = this.getClassNameFromCompName(className);
		ArrayList<String> extrasCommands = this.prepareExtrasCommands(cName);
		
		return this.getExtrasCommandLine(extrasCommands);
	}
	
	private String getClassNameFromCompName(String className){
		String cName = className;
		
		if (className.contains(".") && className.lastIndexOf(".") != 0){
			String[] cNames = className.split("\\.");
			cName = cNames[cNames.length -1];
		}
		
		return cName;
	}
	
	/**
	 * Convert an array of extra parameters into one line
	 * @param extrasCommands
	 * @return
	 */
	private String getExtrasCommandLine(ArrayList<String> extrasCommands) {
		String line =" ";
		
		for (String cmd : extrasCommands){
			line = line+cmd+" ";
		}
		
		return line;
	}
	
	/**
	 * 1- Gets Extras Commands array from ClassExtra class
	 * 2- Check for duplicates
	 * 3- removes L and F from default values
	 * 4- propose default values if there aren't any
	 * @param cName
	 * @return
	 */
	private ArrayList<String> prepareExtrasCommands(String cName){
		ClassExtras cExtra = new ClassExtras(cName);
		cExtra.process();
		ArrayList<String> allExtras = cExtra.getExtrasCommands();
		
		return allExtras;

	}
	
	/**
	 * Gets Info about Extras for -l command
	 * @return
	 */
	public ArrayList<String> getExtrasInfo(String className){
		ArrayList<String> extrasInfo = new ArrayList<>();
		
		if (this.isJClassDir()){
			String cName = this.getClassNameFromCompName(className);
			ClassExtras cExtra = new ClassExtras(cName);
			cExtra.process();
			extrasInfo = cExtra.getExtrasInfo();
		}
			
		
		return extrasInfo;
	}

}
	
