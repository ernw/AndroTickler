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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.filefilter.TrueFileFilter;
//import org.apache.commons.io.filefilter.WildcardFileFilter;

import base.FileUtil;
import base.OtherUtil;
import base.SearchUtil;
import initialization.TicklerVars;

/**
 * EXtract extras from Java classes of every component
 * @author aabolhadid
 *
 */
public class ClassExtras {
	
	private String className;
	private Collection <File> files;
	private ArrayList<String> types, names, defaults,commands;

	public ClassExtras(String className){
		this.className = className;
		this.types = new ArrayList<>();
		this.names = new ArrayList<>();
		this.defaults = new ArrayList<>();
		this.commands = new ArrayList<>();
	}
	
	public void process(){
		
		this.getClassFiles();
		for (File f : this.files){		
			this.getExtrasOfClass(f);
		}
	}
	
	/**
	 * Gets the files whose names contain the class name
	 */
	private void getClassFiles(){
		FileUtil fU = new FileUtil();
		String[] classNameArr = {this.className};
		
//		this.files = FileUtils.listFiles( new File(TicklerVars.jClassDir), new WildcardFileFilter("*" + this.className + "*"), TrueFileFilter.TRUE);
		this.files = fU.listFilesInDirContain(TicklerVars.jClassDir, classNameArr);
	}
	
	
	/**
	 * gets class name and searches for it in the Java class dir.
	 * Then searches in each of these files for the names, types and the default values of each extra
	 * regex *? to make the wildcard not gready and end with the first boundary
	 * @param f
	 * @return
	 */
	public void getExtrasOfClass(File file){
		ArrayList<String> extras = this.getExtraLines(file, this.className);
		for (String line : extras){		
			this.types.addAll(OtherUtil.getRegexFromString(line, "get(\\w+?)Extra"));
			this.names.addAll( OtherUtil.getRegexFromString(line, "get\\w+?Extra\\(\"(.*?)\"") );
			this.defaults.addAll( OtherUtil.getRegexFromString(line, ".*Extra\\(.*,(.*?)\\)") );
		}
	}
	
	
	/**
	 * return lines of the file "file" that contain the pattern get*Extra
	 * @param file
	 * @param className
	 * @return
	 */
	private ArrayList<String> getExtraLines(File file, String className){
		SearchUtil sr = new SearchUtil();
		ArrayList<String> extras = sr.findRegexInFile(file, ".*get(.*)Extra.*");
		return extras;
		
	}
	
	
	/**
	 * returns the parts of syntax of all extras, but avoiding any duplicates of extras names
	 * @return
	 */
	public ArrayList<String> getExtrasCommands(){
		ArrayList<String> addedExtras = new ArrayList<>();
		for (int i=0; i<this.names.size();i++ ){
			if (!addedExtras.contains(this.names.get(i)) && this.isValidExtraType(i)){
				this.commands.add(this.getExtraCommand(i));
				addedExtras.add(this.names.get(i));
			}
		}
		
		return this.commands;
	}
	
	/**
	 * Whether the type of the extra is String, number or boolean
	 * @param i
	 * @return
	 */
	private boolean isValidExtraType(int i){
		String type = this.types.get(i);
		
		if (type.equals("String")|| type.equals("Boolean")|| type.equals("Int")|| type.equals("Long") || type.equals("Float") || type.equals("Double"))
			return true;
		
		return false;
	}
	
	/**
	 * Returns the part of syntax to be added to the am command, such as -e Var1 Val1
	 * Also taking care in case the default value is null --> assign a default value
	 * To avoid errors: defaults have been excluded
	 * @return
	 */
	private String getExtraCommand(int i){
		String cmd = this.getE(this.types.get(i));
		String value="";
		
		value = this.getExtraValue(this.types.get(i));
		String command = cmd + " " +this.names.get(i)+" "+value;
		
		return command; 
		
	}
	
	/**
	 * Returns the information about Extras for -l command
	 * @return
	 */
	public ArrayList<String> getExtrasInfo(){
		ArrayList<String> ExtrasInfo = new ArrayList<>();
		ArrayList<String> addedExtras = new ArrayList<>();
		
		
		for (int i=0; i<this.names.size();i++ ){
			if (!addedExtras.contains(this.names.get(i))){
				ExtrasInfo.add("Extra Name: "+this.names.get(i)+"\t\t Extra Type: "+this.types.get(i));
				addedExtras.add(this.names.get(i));
			}
		}
		
		return ExtrasInfo;
	}

	/**
	 * Gets the extra flag according to the extra type
	 * @param type
	 * @return
	 */
	public String getE(String type){
		String result="";
		switch(type){
		
		case "String":
			result="-e";
			break;
			
		case "Int":
			result="--ei";
			break;
			
		case "Boolean":
			result="--ez";
			break;
			
		case "Long":
			result="--el";
			break;
			
		case "Float":
			result="--ef";
			break;
			
		case "Double":
			result="--ef";
			break;
			
		}
		
		return result;
	}
	
	/**
	 * Gets a default value for an extra according to its type
	 * @param type
	 * @return
	 */
	public String getExtraValue(String type){
		String result="";
		switch(type){
		
		case "String":
			result="TiCkLeR";
			break;
			
		case "Int":
			result="9";
			break;
			
		case "Boolean":
			result="true";
			break;
			
		case "Long":
			result="999";
			break;
			
		case "Float":
			result="99";
			break;
		}
		
		return result;
	}
	
	public ArrayList<String> getTypes() {
		return types;
	}

	public void setTypes(ArrayList<String> types) {
		this.types = types;
	}

	public ArrayList<String> getNames() {
		return names;
	}

	public void setNames(ArrayList<String> names) {
		this.names = names;
	}

	public ArrayList<String> getDefaults() {
		return defaults;
	}

	public void setDefaults(ArrayList<String> defaults) {
		this.defaults = defaults;
	}
	
	
	

}
