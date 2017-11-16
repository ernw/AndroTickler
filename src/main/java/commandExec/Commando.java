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
package commandExec;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import base.FileUtil;
import cliGui.OutBut;
import initialization.TicklerVars;
import java.io.File;
import java.io.FileWriter;

public class Commando {
	
	private StringBuffer output;


	//////////////////// Process Commands //////////////////////
	/**
	 * Execute a command as a process
	 * @param command
	 * @return ArrayList of 2 strings:
	 * 		1) The output of the command
	 * 		2) The error code of command execution
	 */
	public ArrayList<String> executeProcessString(String command){
		int errCode=999;
		ArrayList<String> returnArr = new ArrayList();
		List<String> args = Arrays.asList(command.split(" "));
		String output="!!! ERROR: command "+command+" failed to execute successfully :(";
		try{
			ProcessBuilder build = new ProcessBuilder(args);
			Process p = build.start();
			errCode = p.waitFor();
			output= this.getProcessOp(p);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		returnArr.add(output);
		returnArr.add(new Integer(errCode).toString());
		return returnArr;
	}
	
	/**
	 * Execute a command as a list of arguments and prints its output if "output" is true
	 * @param args
	 * @return return code, errCode 999 is a default value in case an exception occurs before errCode is set 
	 */
	public int executeProcessListPrintOP(String command,boolean output){
		return this.executeProcessListMain(command, output,false,false);
	}
	
	public int executeProcessListPrintOPError(String command){
		return this.executeProcessListMain(command, true, true,false);
	}
	
	public int executeProcessForAdbPull(String command){
		return this.executeProcessListMain(command, false, true, true);
	}
	
	/**
	 * Main method for executing a command as a list
	 * @param args
	 * @param output boolean: if true then print output
	 * @param error	boolean: if true then print output / save output
	 * @param file	boolean: if true then save the output in a temporary file (used with adb pull)
	 * @return
	 */
	private int executeProcessListMain(String command,boolean output,boolean error,boolean file){	
		List<String> args = Arrays.asList(command.split(" "));
		
		int errCode=999;
		String tempPullLog = TicklerVars.logDir+".pullLog.log";
		FileUtil fU = new FileUtil();
		
		try{
			ProcessBuilder build = new ProcessBuilder(args);
			build.redirectErrorStream(error);
			Process p = build.start();
			
			//Print output?
			if (output){
				System.out.println();
				OutBut.printH3("Command Output");
				this.printProcessOp(p);
			}
			
			//Writes output in a file
			else if (file)
			{
				fU.createDirOnHost(TicklerVars.logDir);
				new File(tempPullLog).createNewFile();
				this.saveProcessOp(p, tempPullLog);
			}
			errCode = p.waitFor();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		//If command is successful then delete temp log file of the command execution
		if (errCode == 0 && file){
			fU.deleteFromHost(tempPullLog);
		}
		
		return errCode;
	}
	
	/**
	 *Print stout of a command
	 * @param process
	 */
	private void printProcessOp(Process process) {
		BufferedReader reader = new BufferedReader(new InputStreamReader( process.getInputStream()));
		
		String line = "";	
		
		try {
			while ((line = reader.readLine())!= null) {
				System.out.println(line);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Used with adb pull command
	 * @param process
	 * @param fileName
	 */
	private void saveProcessOp(Process process, String fileName){
		
		BufferedReader reader = new BufferedReader(new InputStreamReader( process.getInputStream()));
		
		String line = "";	
		
		try {
			BufferedWriter writer = new  BufferedWriter(new FileWriter(fileName));
			while ((line = reader.readLine())!= null) {
				writer.write(line+"\n");
			}
			writer.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retruns the output of the command 
	 * @param process
	 * @return
	 */
	private String getProcessOp(Process process){
		BufferedReader reader = new BufferedReader(new InputStreamReader( process.getInputStream()));
		
		String line = "";	
		StringBuffer outputBuff = new StringBuffer();
		try {
			while ((line = reader.readLine())!= null) {
				outputBuff.append(line + "\n");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return outputBuff.toString();
	}
	
	//////////////// Runtime execution ////////////////////
	public String executeCommand(String command)
	{
		return this.executeCommand(command, true);
	}
	
	/**
	 * Executes a command with an option not to wait.
	 * @param command
	 * @param wait
	 * @return
	 */
	public String executeCommand(String command, boolean wait)
	{
		this.output = new StringBuffer(	);
		Process process;
		try {
			process=Runtime.getRuntime().exec(command);
			if (wait)
				process.waitFor();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader( process.getInputStream()));
			
			String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}
			
			if (output.toString().isEmpty()) {
				reader = new BufferedReader(new InputStreamReader( process.getErrorStream()));
				line = "";			
				while ((line = reader.readLine())!= null) {
					output.append(line + "\n");
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return output.toString();
	}
	
	public String executePythonScript(String command)
	{
//		command = "frida -U wana.notenaf.intenttest -l /home/aabolhadid/tools/Frida/myScripts/get_values.js";
		this.output = new StringBuffer(	);
		Process process;
		try {
			process=Runtime.getRuntime().exec(command);

			BufferedReader reader = new BufferedReader(new InputStreamReader( process.getErrorStream()));
			
			String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
				OutBut.printNormal(line);
			}
			
			if (output.toString().isEmpty()) {
				reader = new BufferedReader(new InputStreamReader( process.getErrorStream()));
				line = "";			
				while ((line = reader.readLine())!= null) {
//					output.append(line + "\n");
					OutBut.printNormal(line);
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return output.toString();
	}
	
	//////////////////////////////////// execute ADB or Root Commands //////////////////////////
	public String execADB(String command, boolean wait) {
		return this.executeCommand("adb shell "+command, wait);
	}
	
	public String execADB(String command) {
		return this.executeCommand("adb shell "+command);
	}
	
	public String execRoot(String command) {
		return this.executeCommand("adb shell su -c "+command);
//		ArrayList<String> result = this.executeProcessString("adb shell su -c "+command);
//		return result.get(0);
	}
	
	/**
	 * Currently used to print OP of Start commands
	 * @param command
	 * @return
	 */
	public int execADBPrintOP(String command){
		String fullCommand = "adb shell "+command;
		return this.executeProcessListPrintOP(fullCommand,true);
	}
	
	/**
	 * Currently used to print OP of Start commands
	 * @param command
	 * @return
	 */
	public int execRootPrintOP(String command){
		String fullCommand = "adb shell su -c "+command;
		return this.executeProcessListPrintOP(fullCommand,true);
	}
	
	
}
