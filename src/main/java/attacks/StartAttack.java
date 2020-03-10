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
package attacks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import actions.Snapshots;
import apk.AppBroker;
import base.OtherUtil;
import cliGui.OutBut;
import commandExec.Commando;
import initialization.TicklerVars;
import logs.LogReader;
import logs.LogReaderController;

/**
 * Migrating the start components functionality from Tickler class without missing up with Starter class
 * @author aabolhadid
 *
 */
public class StartAttack extends Starter {
	private AppBroker broker;
	private Commando commando;
	private ArrayList<String> commands;
	private boolean isLogger;
	private Runnable log;
	private Thread th1;
	private String logFileName;
	private LogReaderController logController;
	private Snapshots snaps;

	
	public StartAttack(){
		super();
		this.broker = new AppBroker(TicklerVars.pkgName);
		this.commando = new Commando();
		this.snaps = new Snapshots();
		
	}
	
	/**
	 * Replaces executeAttackCommand. 
	 * @param origCommands
	 * @param exported
	 * @param output
	 */
	public void executeTriggerCommands(ArrayList<String> origCommands, boolean exported){
		String anyKey="";

		ArrayList<String> commands = this.removeDuplicates(origCommands);
		this.printCommandsToBeExecuted(commands);
		
		//Executing commands
		for (String command : commands)
		{
			OutBut.printH2("Attacking Component");
			System.out.println(command);
			
			if (this.isLogger()){
				this.writeCommandInLogFile(command);
			}

			
			// Quick and dirty: don't start the app before bcast receiver and content provider attacks
			
			if (command.contains("content") || command.contains("am broadcast")){
				
			}
			if (!exported)
				this.commando.execRootPrintOP(command);
			else
				this.commando.execADBPrintOP(command);
			
			anyKey=OtherUtil.pressAnykey();
			
			if (anyKey.equals("snapshot")){
				this.snaps.takeSnapshot();
			}
			
			 
			this.commando.execADB(this.broker.forceStopApp());
			
		}

		
	}
	
	/**
	 * Print in the beginning the list of commands to be executed
	 */
	private void printCommandsToBeExecuted(ArrayList<String> origCommands){
		if (this.isLogger){
			OutBut.printH2("Logcat messages are saved in the following file: ");
			OutBut.printNormal(this.logFileName);
		}
		OutBut.printH2("Commands to be executed");
		ArrayList<String> commands = this.removeDuplicates(origCommands);
		for (String c:commands)
			System.out.println(c);
	}
	
	private ArrayList<String> removeDuplicates(ArrayList<String> orig){
		return new ArrayList<String>(new LinkedHashSet<String>(orig));
	}
	
	

	public ArrayList<String> getCommands() {
		return commands;
	}


	public void setCommands(ArrayList<String> commands) {
		this.commands = commands;
	}



	public boolean isLogger() {
		return isLogger;
	}



	public void setLogger(boolean isLogger) {
		this.isLogger = isLogger;
		
		if (isLogger){
			
			this.prepareLoggerThread();
		}
			
	}
	
	////////////////////// Logging ////////////////////////////////////
	/**
	 * Set and start the logger thread
	 */
	private void prepareLoggerThread(){
		this.makeLogFileName();
		this.logController = new LogReaderController();
		this.logController.setLogFileName(this.logFileName);
		this.logController.setStop(false);
		this.log = new LogReader(this.logController);
		this.th1 = new Thread(this.log);
		th1.start();
		
	}
	
	private void makeLogFileName(){
		String timestamp = new SimpleDateFormat("dd.MM.yy_HH.mm.ss").format(new Date());
		this.logFileName=TicklerVars.logDir+TicklerVars.pkgName+"_"+timestamp+".log";
	}
	
	private synchronized void writeCommandInLogFile(String command){

		File logFile = new File(this.logFileName);
		String line="\n\n************************************ Tickler: Executing Command ************************************************\n"
		+command+"\n	 *******************************************************************************************************************\n\n";
		try{
			FileWriter w = new FileWriter(logFile,true);
			w.append(line);
			w.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
		
	}
	
	public void stopLogging(){
		if (this.logController != null)
		this.logController.setStop(true);
	}
	
	
}
