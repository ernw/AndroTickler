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
package manifest;

import java.util.ArrayList;
import java.util.List;
import components.Activity;
import components.DataUri;
import components.IComponent;
import components.Intent;
import components.Manifest;
import components.Provider;
import components.Receiver;
import components.Service;
import components.UsesPermission;
import initialization.TicklerConst;
import initialization.TicklerVars;
import manifest.handlers.IntentHandler;

/**
 * This class analyzes the Manifest file and extracts information from it
 * @author aabolhadid
 *
 */
public class ManifestAnalyzer {

	private Manifest manifest;
	private ArrayList<IComponent> components,exported,hidden;
	
	public ManifestAnalyzer(Manifest manifest) {
		this.components = new ArrayList<IComponent>();
		this.manifest = manifest;
		this.collectAllComponents();
		this.collectExportedComponents();
	}
	
	public void collectAllComponents() {
		this.components.addAll(this.manifest.getApplication().getActivites());
		if (this.manifest.getApplication().getServices() != null)
			this.components.addAll(this.manifest.getApplication().getServices());
		if (this.manifest.getApplication().getProviders() != null)
			this.components.addAll(this.manifest.getApplication().getProviders());
		if (this.manifest.getApplication().getReceivers() != null)
			this.components.addAll(this.manifest.getApplication().getReceivers());
	}
	
	/**
	 * Divides all components over exported and hidden lists
	 */
	public void collectExportedComponents() {
		this.exported = new ArrayList<IComponent>();
		this.hidden = new ArrayList<IComponent>();
		
		this.decideExportedStatus();
		
		for(IComponent c : this.components) {
			if (c.isExported())
				this.exported.add(c);
			else
				this.hidden.add(c);
		}
		
	}
	
	/**
	 * Sets isExported, based on exported field in manifest and existence of implicit intents
	 */
	public void decideExportedStatus() {
		for(IComponent c : this.components) {
			if ( c.isExported()) 
				c.setExported(true);
			else if (c.getExp() != null && c.getExp().equals("true"))
				c.setExported(true);
			else if (c.getExp()==null && c.getIntent()!=null)
				c.setExported(true);
			else
				c.setExported(false);
		}
	}
		
	public IComponent getComponentByName(String compName){
		String name;
		
		for (IComponent c:this.components){
			name = c.getName();
			if (name.equals(compName))
				return c;
		}
		return null;
	}

	public ArrayList<IComponent> getComponentsOfType(int i, boolean exp) {
		ArrayList<IComponent> list = new ArrayList<IComponent>();
		
		for (IComponent c:this.components){
			if (!exp || (exp && c.isExported())){
				if ( i ==TicklerConst.ACTIVITY&& c instanceof Activity)
					list.add(c);
				else if (i ==TicklerConst.SERVICE && c instanceof Service)
					list.add(c);
				else if (i == TicklerConst.PROVIDER && c instanceof Provider)
					list.add(c);
				else if (i == TicklerConst.RECEIVER && c instanceof Receiver)
					list.add(c);
			}
		}
		return list;
	}
	
	public ArrayList<String> getUsesPermissions(){
		ArrayList<String> result = new ArrayList<>();
		
		for (UsesPermission p: this.getManifest().getUsesPermissions()){
			result.add(p.getName());
		}
		
		return result;	
	}
	
	
	public ArrayList<String> getSchemes(){
		List<Intent> intents;
		ArrayList<String> schemes = new ArrayList<String>();
		ArrayList<DataUri> data = new ArrayList<>();
		
		for (IComponent c : this.components){
			if ((intents =c.getIntent()) != null){
				for (Intent i:intents){
					if (i.getData()!=null)
						data.addAll(i.getData());
				}
			}
		}
		
		for(DataUri d:data){
			schemes.add(d.getScheme());
		}
		
		return schemes;
		
	}
	
	
	
	//////////////////////// Getters and Setters ////////////////////////
	public Manifest getManifest() {
		return manifest;
	}

	public void setManifest(Manifest manifest) {
		this.manifest = manifest;
	}

	public ArrayList<IComponent> getComponents() {
		return components;
	}
	

	public void setComponents(ArrayList<IComponent> components) {
		this.components = components;
	}

	public ArrayList<IComponent> getExported() {
		return exported;
	}

	public void setExported(ArrayList<IComponent> exported) {
		this.exported = exported;
	}
	
	public ArrayList<IComponent> getComponents(boolean exp) {
		if (exp){
			return this.getExported();
		}
		return components;
	}
	
	public ArrayList<IComponent> getHidden() {
		return hidden;
	}

	public void setHidden(ArrayList<IComponent> hidden) {
		this.hidden = hidden;
	}
	
	
}
