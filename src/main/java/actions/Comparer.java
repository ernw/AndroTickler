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

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import base.CopyUtil;
import base.FileUtil;
import base.OtherUtil;
import commandExec.Commando;
import db.DatabaseTester;
import initialization.TicklerVars;

public class Comparer {

	private Commando commando;
	private FileUtil fileTrans;
	private CopyUtil copyz;
	
	public Comparer() {
		
		this.copyz = new CopyUtil();
		this.commando = new Commando();
		this.fileTrans = new FileUtil();
	}
	
	public void diff(boolean detailed) {
		this.clearDataDirs();
		
		this.copyz.copyDataDir(TicklerVars.appTickDir+"DataDirOld/");
		System.out.println("\n\n>>>>>>>>>>>>>>>> Go crazy then press Enter to compare data directories....\n");
		OtherUtil.pressAnyKeySilent();
		copyz.copyDataDir();
		
		String command = "diff -rq "+ TicklerVars.appTickDir+"DataDirOld/ "+TicklerVars.appTickDir+"DataDir/";
		String output = this.commando.executeCommand(command);
		System.out.println(output.replace(TicklerVars.appTickDir, "[Tickler_App_Dir]/"));
		if (output.isEmpty())
			System.out.println("No change in the app's Data directory");
		else
			System.out.println("\n...Where [Tickler_App_Dir] is "+TicklerVars.appTickDir);
		
		if (detailed){
			this.diffDetailed(output);
		}
	}	
	
	/**
	 * A detailed output of changed files:
	 * 1) text files: classic diff output
	 * 2) DB files: query and show the difference
	 * @param output: output of undetailed diff 
	 */
	
	public void diffDetailed(String output){
		System.out.println("\nDetailed Comparison result:\n===========================\n");
		
		//1- get file names
		ArrayList<String> diffFileNames = this.getDiffFileNames(output);
		if (!diffFileNames.isEmpty()){
			//2- diff files 
			for (String s:diffFileNames)
				checkFileTypeAndCompare(s);
			}
	}
	
	private ArrayList<String> getDiffFileNames(String output){
		ArrayList<String> filePathsArray = new ArrayList<String>();
		
		String[] opArray=output.split("\n");
		if (opArray.length>0 && opArray[0]!=""){
			for (String s:opArray)
			{
				Matcher m = Pattern.compile("differ$").matcher(s);
				if (m.find()){
					String filePath = s.split(" ")[1];
					filePathsArray.add(filePath);
				}
			}
		}
		return filePathsArray;
	}
	
	private void checkFileTypeAndCompare(String fileName){
		File f = new File(fileName);
		String output,fileTypeCommand;
		fileTypeCommand = this.fileTrans.fileType(f);
		DatabaseTester dbT = new DatabaseTester();
		///File is a text file or human readable
		if (fileTypeCommand.contains("text")){
			//File is a text file
			this.compareTextfiles(fileName);
		}
		//If the file is a database file
		else if (dbT.isFileDB(f)){
			this.compareDB(f);
		}
	}
	private void compareTextfiles(String fileName){
		String command,output,fName;
		
		fName=this.fileTrans.getFileNameFromPath(fileName);
		System.out.println("---------- "+fName+" ----------");
		command="diff "+fileName+" "+fileName.replace("DataDirOld", "DataDir");
		output=this.commando.executeCommand(command);
		System.out.println(output+"\n");
		
	}
	
	/**
	 * Comapres between 2 DBs by string diff their dumps 
	 * @param f
	 */
	private void compareDB(File f){
		DatabaseTester dbT = new DatabaseTester();
		String filePath=f.getAbsolutePath();
		String oldDump = dbT.dumpDBToFile(filePath,null);
		String newDump = dbT.dumpDBToFile(filePath.replace("DataDirOld", "DataDir"),null);
		
		System.out.println("------- Database File: "+f.getName()+" -------");
		
		String command = "diff "+ oldDump+" "+newDump;
		String output = this.commando.executeCommand(command);
		System.out.println(output);
		
		this.fileTrans.deleteFromHost(oldDump);
		this.fileTrans.deleteFromHost(newDump);
	}
	
	private void clearDataDirs(){
		this.fileTrans.warnOverrideAndDelete(TicklerVars.appTickDir+"DataDirOld/");
		this.fileTrans.warnOverrideAndDelete(TicklerVars.dataDir);
		
	}
	
}
