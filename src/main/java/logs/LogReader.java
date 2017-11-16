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
package logs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import base.FileUtil;
import commandExec.Commando;
import initialization.TicklerVars;

public class LogReader implements Runnable {
	String logFileName;
	Commando commando;
	FileWriter writer;
	private LogReaderController controller;
	
	public LogReader(LogReaderController controller){
		this.commando = new Commando();
		this.controller = controller;
		this.logFileName = this.controller.getLogFileName();
	}

	@Override
	public void run() {
		this.readWriteLogCat();
	}
		
	public void readWriteLogCat(){
		File logFile = new File(this.logFileName);
		String command = "adb logcat";
		this.executeLogcat(command, logFile);
	}
	
	
	public void executeLogcat(String command, File logFile){
		Process process;
		try {
			process=Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader( process.getInputStream()));
			this.writer = new FileWriter(logFile,true);
			
			String line = "";			
			while (((line = reader.readLine())!= null) && !this.controller.isStop()) {
				synchronized(this){
					this.writer.append(line+"\n");
					this.writer.flush();
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if (this.controller.isStop())
			this.closeWriter();
		
	}
	
	public void closeWriter(){
		try{
			this.writer.close();
		}
		catch(IOException e)
		{}
	}

	public String getLogFileName() {
		return logFileName;
	}

	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}

}
