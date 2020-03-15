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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.io.FileUtils;
import cliGui.OutBut;
import commandExec.Commando;
import exceptions.TNotFoundEx;
import initialization.TicklerVars;
/**
 * All File operations (copy, delete ...) on host or device
 * @author aabolhadid
 *
 */
public class FileUtil {
	
	private Commando commando;

	public FileUtil(){
		this.commando = new Commando();
	}
		
	
	///////// HOST commands ////////////////
	/**
	 * 
	 * @param src
	 * @param dest
	 * @param silent If true then it will not print the Copying message
	 */
	public void copyOnHost(String src, String dest,boolean silent){
		if (!silent)
			System.out.println("Copying "+src+" to "+dest+".....");
		String command = "cp -fr "+src+" "+dest;
		this.commando.executeCommand(command);
	}
	
	
	/**
	 * Checks subdirectories of each directory, escapes space in their files' names then change the subdirectories' names
	 * @param dir
	 */
	public void escapeSpaceInDir(File dir){
		try {
			for (File f : dir.listFiles())
			{
				if (f.isDirectory())
					this.escapeSpaceInDir(f);
	
				this.replaceSpace(f);
			}
		}
		catch (java.lang.NullPointerException nullEx) {
			OutBut.printNormal(".... Directory is empty");
		}

	}
	
	/**
	 * Since Java cannot handle files names or directories containing a space, this is to replace the space with two underscores
	 * @param loc
	 */
	private void replaceSpace(File f){
		String fName = f.getName();
		if (fName.contains(" ")){
			String newPath = f.getParentFile().getAbsolutePath()+"/"+fName.replace(" ", "__");
			File newName = new File(newPath);
			f.renameTo(newName);
			
		}
	}
	
	public void deleteFromHost(String filename){
		File file = new File(filename);
		try{
			if (file.isDirectory())
				FileUtils.deleteDirectory(file);
			else
				FileUtils.forceDelete(file);
		}
		catch(IOException e){
			System.out.println("!!!!!! ERROR: Cannot delete "+file.getAbsolutePath()+". Please delete the "
					+ "file / directory manually and rerun the command");
		}
	}
	
	// If a directory does not exist, it creates it. 
	public void createDirOnHost(String path) {
		if (!this.isExist(path)) {
			File dir = new File(path);
			dir.mkdirs();
		}
	}
	
	/**
	 * If Path exists on host, a warning message is printed and path is deleted
	 * @param path
	 */
	public void warnOverrideAndDelete(String path){
		if (this.isExist(path)){
			System.out.println("WARNING... "+path+" already exists with this same, !!OVERRIDING!! ");
			this.deleteFromHost(path);
		}
	}
	
	
	public boolean isExist(String path){
		File file = new File(path);
		return file.exists();
	}
	
	/**
	 * Returns whether a file is executable, used with dex2jar and other external tools
	 * @param path
	 * @return
	 */
	public boolean isExecutable(String path){
		File file = new File(path);
		return file.canExecute();
	}
	
	/**
	 * Checks the file type by executing the file command
	 * @param f: file
	 */
	public String fileType(File f){
		String path = f.getAbsolutePath().replaceAll("\\s", "\\ ");
		String command = "file "+ path;
		String result = commando.executeCommand(command);
		
		return result;
	}
	
	public String getFileNameFromPath(String path){
		File absPath = new File(path);
		String theName = absPath.getName();
		if (absPath.isDirectory())
			theName=theName+"/";
		
		return theName;
		
	}
	
	/**
	 * Get files in Folder
	 * Replacing FileUtils.listFiles problem
	 * @param src
	 * @param dest
	 */
	public ArrayList<File> listFilesInDir(String dirLoc){

		ArrayList<File> fileList = new ArrayList<File>();	
		try {
		 Path source = Paths.get(dirLoc);
	     Files.walk(source).filter(Files::isRegularFile).forEach(p -> fileList.add(this.pathToFile(p)));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return fileList;
	}
	
	private File pathToFile(Path p) {
		File f=p.toFile();
		if (f.exists()) {
			return f;
		}
		return null;
	}
	
	/**
	 * Search for files with specific extensions or contain a specific string.
	 * Solving FileUtils issues
	 * @param dirLoc
	 * @param exts
	 * @return
	 */
	public ArrayList<File> listFilesInDirContain(String dirLoc,String[] exts) {
		ArrayList<File> filez = this.listFilesInDir(dirLoc);
		ArrayList<File> returnFilez = new ArrayList<File>();
		
		for(File f : filez) {
			for(String ext:exts)
				if (f.getName().contains(ext)) {
					returnFilez.add(f);
				}
		}
		return returnFilez;
		
	}
	
	////////////// Android Device ////////////////
	
	public void copyOnDevice(String src, String dest) {
		String command = "cp -fr "+src+" "+dest;
		this.commando.execRoot(command);
		
	}
	
	public void copyToDevice(String src, String dest) {
		String fName = this.getFileNameFromPath(src);
//		this.warnOverrideAndDelete(dest+fName);
		String sdCardDestPath = TicklerVars.sdCardPath+fName;
		String command = "adb push "+src+" "+sdCardDestPath;
		int pullResult=this.commando.executeProcessForAdbPull(command);
		
		this.copyOnDevice(sdCardDestPath, dest);
//		this.deleteDirFromDevice(src);
	}
	
	public void pullFromSDcard(String src, String dest) {
		String fName = this.getFileNameFromPath(src);
		this.warnOverrideAndDelete(dest+fName);
		String command = "adb pull "+src+" "+dest;
		int pullResult=this.commando.executeProcessForAdbPull(command);
		this.deleteDirFromDevice(src);
	}
	

	
	public String createDirOnDevice(String path) {
		String command = "mkdir -p "+path;
		return this.commando.execRoot(command);
	}
	
	public String deleteDirFromDevice(String path) {
		String command = "rm -fr "+path;
		return this.commando.execRoot(command);
	}
	
	//Checks if the file exists on the android device
	public boolean isExistOnDevice(String path) {
		String command = "ls "+path;
		String op = this.commando.execRoot(command);
		
		if (op.toLowerCase().contains("no such file or directory"))
			return false;
		return true;
	}
	
	private void b4CopyChecks(String path) throws TNotFoundEx{
		if (!this.isExistOnDevice(path)){
			throw new TNotFoundEx("!!!!!! ERROR: File / Directory does not exist on the device");
		}
	}
	
	/////////////////////////////////////// Between Device and host //////////////////////////////////
	/**
	 * Copies a file / directory from any location on the device to the host (linux) through SDcard
	 * @param silent if true then don't print a message
	 */
	public void copyDirToHost(String src,String dest,boolean silent) {
		
		try{
			if (!silent)
				OutBut.printStep("Copying: "+src+" to ...\n"+dest+" ......");
			this.b4CopyChecks(src);
			String srcName = this.getFileNameFromPath(src);
			this.deleteDirFromDevice(TicklerVars.sdCardPath+srcName);
			this.copyOnDevice(src, TicklerVars.sdCardPath);
			this.prepareDestination(dest);
			this.pullFromSDcard(TicklerVars.sdCardPath+srcName, dest+"/");
			File f = new File(TicklerVars.sdCardPath+srcName);
			//Clean (uncommented)
			this.deleteDirFromDevice(TicklerVars.sdCardPath+srcName);
		}
		catch(TNotFoundEx e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	
	/////////////// I/O ///////////////
	public String readFile(String file) throws IOException {
	    BufferedReader reader = new BufferedReader(new FileReader (file));
	    String line;
	    StringBuilder fileString = new StringBuilder();
	    String ls = System.getProperty("line.separator");

	    try {
	        while((line = reader.readLine()) != null) {
	        	fileString.append(line);
	        	fileString.append(ls);
	        }

	        return fileString.toString();
	    } finally {
	        reader.close();
	    }
	}
	
	public void writeFile(String fileName, String content){
		try {
			BufferedWriter writer = new  BufferedWriter(new FileWriter(fileName));
			writer.write(content);
			writer.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/////// Preparation of Transfer directory
	public String prepareTimestampTransfer(){
		String timestamp = new SimpleDateFormat("dd-MM-yy_HH.mm.ss").format(new Date());
		if (!this.isExist(TicklerVars.transferDir))
			this.createDirOnHost(TicklerVars.transferDir);
		
		return timestamp;
	}
	
	private void prepareDestination(String dst) {
		File destFile = new File(dst);
		this.createDirOnHost(dst);		
	}
	


}
