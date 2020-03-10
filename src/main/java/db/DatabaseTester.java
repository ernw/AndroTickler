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
package db;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import base.FileUtil;
import base.OtherUtil;
import base.SearchUtil;
import cliGui.OutBut;
import commandExec.Commando;
import initialization.TicklerVars;

public class DatabaseTester {
	private Commando commando;
	private FileUtil fileTrans;
	
	public DatabaseTester() {
		this.commando = new Commando();
		this.fileTrans = new FileUtil();
	}
	
	public ArrayList<File> fetchAllDBs(String dir){
		SearchUtil searcher = new SearchUtil();
		List<File> files = searcher.search4FileInDir(dir, null);
		ArrayList<File> dbs = new ArrayList<File>();
		
		for (File f:files){
			if (this.isFileDB(f))
				dbs.add(f);
		}
		
		return dbs;
	}
	
	public void dbOption(String param){
		if (param == null ||param.equals("e") || param.equals("encryption")){
			OutBut.printH1("Database encryption");
			this.testDBAllFiles(TicklerVars.dataDir);
			}
		
		else if (param.equals("l") || param.equals("list")){
			this.listDatabases(TicklerVars.dataDir);
		}
		else if (param.equals("d") || param.equals("dump")){
			this.chooseDBToDump(TicklerVars.dataDir);
		} 
		else {
			OutBut.printError("Unknown option "+param);
		}
	}
	
	/**
	 * Checks if the file is a database file
	 * @param f File file under test
	 * @return true: if file is DB
	 */
	public boolean isFileDB(File f){
		String path = f.getAbsolutePath();
		String command = "file "+ path+"";
		String result = commando.executeProcessString(command).get(0);
		if (result.matches("(?s).*\\bdatabase\\b.*")){
			return true;
		}
		return false;
	}
	
	/**
	 * Lists all databases in a specific directory
	 * @param dir
	 */
	public ArrayList<File> listDatabases(String dir){
		ArrayList<File> allDB = this.fetchAllDBs(dir);
		OutBut.printH1("List of Databases of the app");
		System.out.println("!!!! WARNING: Encrypted databases might not be detected !!!!\n\n");
		for (int i=0;i<allDB.size();i++){
			File f = allDB.get(i);
			System.out.println((i+1)+"-"+f.getName());
			System.out.println("	Location: "+f.getAbsolutePath().replace(TicklerVars.appTickDir, "[Tickler_App_Dir]/"));
		}
		
		if (allDB.size()>1){
			System.out.println("\n...Where [Tickler_App_Dir] is "+TicklerVars.appTickDir);
		}
		return allDB;
	}
	
	//////////////////////////// DB Encryption ///////////////////////////
	
	/**
	 * Tests the encryption of all databases in a specific directory
	 * @param dir
	 */
	public void testDBAllFiles(String dir) {
		
		ArrayList<File> dbs= this.fetchAllDBs(dir);
		for (File f:dbs){
			this.testDBEncryption(f);
		}
		System.out.println("!!! NOTE: Any path that contains a space is replaced by two underscores in the DataDir folder"
				+ " on your machine");
	}
	
	/**
	 * Runs the encryption Tests on a single DB
	 * @param f
	 */
	public void testDBEncryption(File f){
		
		String result="";
			if (this.isDBEncrypted(TicklerVars.replaceSpace(f.getAbsolutePath())) )
				result="encrypted";
			else
				result = "NOT encrypted";
			
			System.out.println("Database:"+f.getName()+" is "+result);
	}
	
	/**
	 * Tests if a single DB is encrypted
	 * @param f
	 */
	public boolean isDBEncrypted(String dbName) {
		String command = "sqlite3 "+dbName+" .tables";
		String output = this.commando.executeCommand(command);
		if (output.contains("encrypted or is not a database"))
			return true;
		return false;
	}
	
	
	///////////////////// Database Dump /////////////////////////////
	
	/**
	 * Lists databases to user and dumps one of them based on his choice
	 * @param dir
	 */
	public void chooseDBToDump(String dir){
		ArrayList<File> allDB = this.listDatabases(dir);
		System.out.println("\nEnter the number of the database to be dumped....");
		String choice = OtherUtil.pressAnyKeySilent();
		Integer i=999;
		try {
			i = new Integer(choice);	
		}
		catch(Exception e){
		e.printStackTrace();
		}
		
		if((i-1)< allDB.size() && i>0){
			String dbFile = allDB.get((i-1)).getAbsolutePath();
			String dbName = allDB.get((i-1)).getName();
			String ts =this.fileTrans.prepareTimestampTransfer();
			String dumpName=TicklerVars.transferDir+dbName+"_"+ts+".dump";
			this.dumpDBToFile(dbFile, dumpName);
			
			if (this.fileTrans.isExist(dumpName)){
				System.out.println("Database is dumped to file: "+dumpName);
			}
		}
		else
		{
			System.out.println("Incorrect choice, please enter a number from the database list");
		}
	}
	
	/**
	 * Dumps the whole database
	 * @param dbName String: path of the database file
	 * @return
	 */
	public String dumpDB(String dbName){
		String command = "sqlite3 "+dbName+" .dump";
		String output = this.commando.executeCommand(command,false);
		
		return output;
	}
	
	/**
	 * Dumps the whole database into a text file
	 * @param dbName String: path of the database file
	 * @return dumpName ; path of the dump file
	 */
	public String dumpDBToFile(String dbName,String dumpFile){
		FileUtil ft = new FileUtil();
		if (dumpFile == null)
			dumpFile= dbName+".dump";
		String output = this.dumpDB(dbName);
		ft.writeFile(dumpFile, output);
		
		return dumpFile;
	}
	

	///////////////////////////// Search in DB /////////////////////////////
	
	
	/**
	 * Search for a key in all databases in Data Dir
	 * @param key
	 */
	public void searchForKeyInDb(String key){
		ArrayList<File> dbs = this.fetchAllDBs(TicklerVars.dataDir);
		boolean found = false;
		
		for(File db: dbs){
			if (this.searchForKeyInSingleDB(key, db))
				found = true;
		}
		
		if(found){
			OutBut.printNormal("");
			OutBut.printStep("Where [Data_Dir] is "+TicklerVars.dataDir);
		}
		
	}
	
	/**
	 * Search for a key in a single database file
	 * @param key
	 * @param db
	 */
	private boolean searchForKeyInSingleDB(String key, File db){
		String dump, dbName, output;
		boolean found = false;
		ArrayList<String> tableNames,noDupTabNames;
		dump = this.dumpDB(db.getAbsolutePath());
		if (dump.contains(key)) {
			found = true;
			dbName= db.getAbsolutePath().replaceAll(TicklerVars.dataDir, "[Data_Dir]/");
			tableNames = this.getKeyInfoFromDBDump(key, dump);
			noDupTabNames = OtherUtil.removeDuplicates(tableNames);
		
			output = "Database Name: "+dbName+"\t\tTable Name(s): ";
			for(String tabName : noDupTabNames){
				output+=tabName+", ";
			}
			
			System.out.println(output.substring(0, output.length()-2));
		}
		
		return found;
	}
	
	
	/**
	 * Searches for a key in the dump of a database
	 * @param key
	 * @param dump
	 * @return
	 */
	private ArrayList<String> getKeyInfoFromDBDump(String key, String dump){
		ArrayList<String> tables = new ArrayList<>();
		List<String> lines= Arrays.asList(dump.split("\n"));
		for (String line: lines){
			if (line.contains(key)){
				tables.addAll(OtherUtil.getRegexFromString(line, "INSERT INTO \"(.*?)\""));
			}
		}
		
		return tables;
		
	}
	
	
}
