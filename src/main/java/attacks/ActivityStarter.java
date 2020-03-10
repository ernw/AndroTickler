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
import commandExec.Commando;
import components.Activity;
import components.IActivityService;
import components.Intent;
import manifest.handlers.IntentHandler;

/**
 * 
 * @author aabolhadid
 * Starts Activities
 */
public class ActivityStarter {

	String pkgName;
	String actStartCommand;
	IActivityService activity;
	ArrayList<String> commands;
	Commando c;
	
	public ActivityStarter(String pkgname) {
		this.pkgName = pkgname;
		this.commands = new ArrayList<String>();
	}
	
	public ArrayList<String> startActivityfully(IActivityService actSer) {
		
		ArrayList<String> commands=new ArrayList<String>();
		String startCommand = this.startActivity(actSer);
		commands.add(startCommand);
		
		IntentHandler iHandler;
		
		if (actSer.getIntent()!= null) {
			for (Intent i:actSer.getIntent())
			{
				iHandler= new IntentHandler(startCommand,i);
				commands.addAll(iHandler.fullIntent());
			
			}
		}
		return commands;
	}
	
	/**
	 * Starts activity without intents
	 * @return Command to start
	 */
	public String startActivity() {
		
		String amCommand = this.createAmCommand(this.activity);
		return amCommand+this.getPkgName()+"/" + this.getActivity().getName();
	}
	
	public String startActivity(IActivityService actSer){
		this.setActivity(actSer);
		return this.startActivity();
	}
	
	
	
	private String createAmCommand(IActivityService comp) {
		if (comp.getClass().equals(Activity.class)){
			return "am start -n ";
		}
		return "am startservice -n ";
		
		
	}

	public String getPkgName() {
		return this.pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public IActivityService getActivity() {
		return activity;
	}

	public void setActivity(IActivityService a) {
		this.activity = a;
	}

	public Commando getC() {
		return c;
	}

	public void setC(Commando c) {
		this.c = c;
	}
	
}
