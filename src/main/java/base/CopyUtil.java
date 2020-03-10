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
import java.text.SimpleDateFormat;
import java.util.Date;

import cliGui.OutBut;
import commandExec.Commando;
import info.InfoGathering;
import initialization.TicklerConst;
import initialization.TicklerVars;

public class CopyUtil {

	private FileUtil fileTrans;
	
	public CopyUtil() {
		this.fileTrans = new FileUtil();
	}
	
/////////////////////////////////// Copy Data ////////////////////////////////////////////////

	/**
	 * Updates local and external data directory
	 */
	public void copyStorage() {
		this.copyDataDir();
		this.copyExtDir(TicklerVars.extDataDir);
	}
	/**
	 * Copy local and external storage directories to their usual directories 
	 * OR copy them to Transfers directory
	 * @param dest
	 */
	public void copyStorage(String dest) {
		if (dest == null) {
			this.copyStorage();
		}
		else {
			String mainDest =TicklerVars.transferDir+dest+"/";
			this.copyDataDir(mainDest+TicklerConst.DATA_DIR_NAME);
			this.copyExtDir(mainDest+TicklerConst.EXTERNAL_STORAGE_Dir);
		}
	}
	
	/**
	* Copies DAta directory from the device, replaces any space in any file or dir name with __
	*/
	public void copyDataDir() {
		this.copyDataDir(TicklerVars.dataDir);
	}
	
	/**
	 * Copy local Data Directory to a specific destination
	 * @param dest
	 */
	public void copyDataDir(String dest){
		
		String src = "/data/data/"+TicklerVars.pkgName;
		if (this.fileTrans.isExistOnDevice(src)) {
			this.fileTrans.warnOverrideAndDelete(dest);
			System.out.println("\n!!! NOTE: Space in Files' and Directories' names are replaced by two underscores __ !!!\n");
			this.fileTrans.copyDirToHost(src, dest,false);
			
			//Escape space in file names
			
			this.fileTrans.escapeSpaceInDir(new File(dest));
		}
		else {
			OutBut.printError("Data Directory does not exist on the device");
		}
	
	}
	
	/**
	 * Copy local data storage directory to a specific destination
	 * I think it's duplicated and the code changed anyway to copy both local and ext storage
	 * @param name
	 */
	/*
	public void copyDataDirName(String name){
		String dest;
		if (name==null){
			dest = TicklerVars.dataDir;
		}
		else
		{
			dest =TicklerVars.transferDir+name;
//			FileUtil fU = new FileUtil();
			this.fileTrans.createDirOnHost(TicklerVars.transferDir);
		}
		
		this.copyDataDir(dest);
	
	}
	*/
	// Copy any file or directory from the device to the host
	//Create a new directory for each transfer
	public void copyToHost(String src, String dest){
	
		String timestamp = new SimpleDateFormat("dd-MM-yy_HH.mm.ss").format(new Date());
		String srcName =  fileTrans.getFileNameFromPath(src);
		String dstDirName;
		
		if (dest == null){
			dstDirName = srcName+"_"+timestamp;
		}
		else {
			dstDirName = dest;
		}
		
		String destDir = TicklerVars.transferDir+dstDirName;
		
		this.fileTrans.copyDirToHost(src, destDir,false);
		
		//Check
		if (new File(destDir).exists())
		System.out.println(src+" has been copied successfully to "+destDir);
	}

	/**
	 * Copy External storage directory to a certain destination
	 * @param destExtDir
	 */
	public void copyExtDir(String destExtDir) {
		
		InfoGathering info = new InfoGathering();
//		FileUtil fU = new FileUtil();
		String extDir = info.getSdcardDirectory().replaceAll("\\n", "");
//		String destExtDir=TicklerVars.transferDir+TicklerConst.COPIED_EXTERNAL_STORAGE_NAME;
		if (!extDir.isEmpty()){
			OutBut.printStep("Copying External Storage Directory: "+extDir+"\n");
			this.fileTrans.copyDirToHost(extDir, destExtDir,false);
			System.out.println("");
		}
		
	}

	
}
