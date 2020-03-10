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
import java.text.SimpleDateFormat;
import java.util.Date;

import base.FileUtil;
import cliGui.OutBut;
import commandExec.Commando;
import initialization.TicklerVars;

public class Snapshots {
	
	private Commando commando;
	private FileUtil fileTrans;
	
	public Snapshots() {
		
		this.commando = new Commando();
		this.fileTrans = new FileUtil();
	}

	public void takeSnapshot(){
		String timestamp = new SimpleDateFormat("dd.MM.yy_HH.mm.ss").format(new Date());
		String imgName = TicklerVars.pkgName+"_"+timestamp+".png";
		String path=TicklerVars.sdCardPath+imgName;
		
		//In case of no package
		this.fileTrans.createDirOnDevice(TicklerVars.sdCardPath);
		
		this.executeSnapshot(path);
		
		String imgDir=TicklerVars.imageDir;
		this.fileTrans.createDirOnHost(imgDir);
		this.fileTrans.pullFromSDcard(path, imgDir);
		
		if (new File(TicklerVars.imageDir+imgName).exists())
			OutBut.printStep("Screenshot taken succesfully and saved at "+TicklerVars.imageDir+imgName);
		
	}
	
	public void executeSnapshot(String path) {
		String command = "screencap -p "+path;
		this.commando.execADB(command);
	}
	
	public void getBackGroundSnapshots(){
		//quick and dirty: try both locations
//		String path = "/data/system/recent_images";
		String[] paths = {"/data/system_ce/0/recent_images","/data/system/recent_images", "/data/system_ce/0/snapshots/" };
		this.fileTrans.createDirOnHost(TicklerVars.bgSnapshotsDir);
		OutBut.printH1("Collecting stored Background screenshots");
		OutBut.printH2("Screenshots are stored in only one of the following location. So ignore \"Directory does not exist\" errors if 1 location does not report any error");
		System.out.println();
		System.out.println("Copying Background snapshots to host...");
		for (String path : paths)
			this.fileTrans.copyDirToHost(path, TicklerVars.bgSnapshotsDir,false);
				
	}
	
}
