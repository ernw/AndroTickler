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
import cliGui.OutBut;
import code.ExtrasUtil;
import components.IActivityService;
import components.IComponent;
import components.Provider;
import components.Receiver;
import initialization.TicklerVars;

/**
 * I mean component attacker :) so it triggers attacks of Activities, services, content providers and B/C receivers
 * @author aabolhadid
 *
 */
public class Starter {

	private ActivityStarter actStarter;
	private Broadcaster broadcaster;
	public ProviderAttacker provAtt;
	public String manifestPath;
	
	public Starter() {
		String pkgName = TicklerVars.pkgName;
		this.actStarter = new ActivityStarter(pkgName);
		this.broadcaster = new Broadcaster(pkgName);
		this.provAtt = new ProviderAttacker();
	}
	
	
	/**
	 * Collects commands of starting the selected components
	 * @param components
	 * @return
	 */
	public ArrayList<String> attackComponents(ArrayList<IComponent> components){
		ArrayList<String> commands = new ArrayList<String>();
		for (IComponent c:components){
			commands.addAll(this.attackComponent(c));
		}
		
		return commands;
	}
	
	/**
	 * Collect the commands to start a specific component, based on its type
	 * @param comp
	 * @return
	 */
	public ArrayList<String> attackComponent(IComponent comp) {
		ArrayList<String> commands = new ArrayList<String>();
		
		if (comp instanceof IActivityService) {
			commands.addAll(this.actStarter.startActivityfully((IActivityService)comp));
		}
		else if (comp instanceof Receiver) {
			commands.addAll(broadcaster.generateBroadcast((Receiver) comp));
		}
		
		else if (comp instanceof Provider) {

			//Query only the URI from Authority
			String uri = this.provAtt.prepareContentFromAuthority((Provider)comp);
			commands.add(this.provAtt.queryContent(uri));			
		}
		
		ExtrasUtil cU = new ExtrasUtil();
		if (cU.isJClassDir()){
			ArrayList<String> commandsWithExtras = this.addExtrasOfComp(comp, commands);
			return commandsWithExtras;
		}
		else {
			OutBut.printWarning("Extras cannot be obtained. PLease make sure that the lib directory lies in the same directory as Tickler Jar file and contains dex2jar-2.0 folder");
			return commands;
		}
		
	}
	
	public ArrayList<String> queryUrisFromSmali(){
		return this.provAtt.queryUrisFromSmali(TicklerVars.extractedDir);
	}
	
	public ArrayList<String> getContentUriFromDex(String dexPath) {
		provAtt.getContentURIsFromSmali(dexPath);
		return provAtt.queryContents();
	}
	
	public void prepareProviderAttacks(String dexPath) {
		this.setManifestPath(dexPath);
		this.provAtt.getContentURIsFromSmali(dexPath);
	}
	

	public String getManifestPath() {
		return manifestPath;
	}

	public void setManifestPath(String manifestPath) {
		this.manifestPath = manifestPath;
	}



	////// Get Extras //////
	private String getExtrasOfComp(IComponent comp){
		String extrasLine = " ";
		ExtrasUtil cU = new ExtrasUtil();
		extrasLine = cU.getExtras(comp.getName());
		
		return extrasLine;
	}
	
	private ArrayList<String> addExtrasToCommands(String extrasLine, ArrayList<String> commands ) {
		ArrayList<String> additionalCommands = new ArrayList<>();
		for (String cmd : commands){
			additionalCommands.add(cmd + extrasLine);
		}
		
		commands.addAll(additionalCommands);
		return commands;
	}
	
	/**
	 * Adds extras commands if exist
	 * @param comp
	 * @param commands
	 * @return
	 */
	private ArrayList<String> addExtrasOfComp(IComponent comp,ArrayList<String> commands) {
		String extrasLine = this.getExtrasOfComp(comp);
		if (!extrasLine.equals(" "))
			return this.addExtrasToCommands(extrasLine, commands);
		return commands;
	}
	
	
}
