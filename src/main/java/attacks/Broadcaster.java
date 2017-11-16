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

import java.util.ArrayList;

import components.Intent;
import components.Receiver;
import manifest.handlers.IntentHandler;

public class Broadcaster {

	String pkgName;
	Receiver rec;
	IntentHandler iHandler;
	
	public Broadcaster(String pkgName) {
		this.pkgName = pkgName;
	}

	public Broadcaster(String pkgName,Receiver rec) {
		this.pkgName = pkgName;
		this.setRec(rec);
	}
	
	public Receiver getRec() {
		return rec;
	}

	public void setRec(Receiver rec) {
		this.rec = rec;
	}
	
	public ArrayList<String> generateBroadcast(Receiver rec) {
		ArrayList<String> commands = new ArrayList<String>();
		String baseCommand = "am broadcast -n "+this.pkgName+"/"+rec.getName();
		commands.add(baseCommand);
		
		if (rec.getIntent() != null)
			for (Intent i : rec.getIntent()){
				iHandler = new IntentHandler(baseCommand,i);
				commands.addAll(iHandler.fullIntent());
			}
		return commands;
	}
	
}
