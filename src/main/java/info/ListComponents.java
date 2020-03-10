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
package info;

import java.util.ArrayList;

import cliGui.OutBut;
import code.ExtrasUtil;
import components.Action;
import components.Category;
import components.DataUri;
import components.IComponent;
import components.Intent;
import exceptions.TNotFoundEx;
import initialization.TicklerConst;
import initialization.TicklerVars;
import manifest.ManifestDealer;
import manifest.handlers.DataUriHandler;

public class ListComponents {
	private ManifestDealer dealer;
	
	public ListComponents() {
		this.dealer = new ManifestDealer();
		
	}
	
	/**
	 * Lists any option given from Tickler
	 * @param compType
	 * @param exported
	 * @param details
	 */
	public void listThis(int compType, boolean exported,boolean details){
		
	if (compType==TicklerConst.ALLCOMPS){
		for(int i=1;i<5;i++){
			this.listType(i, exported,details);
		}
	}
	else{
		this.listType(compType, exported,details);
	}
		
}
	/**
	 * Lists all components of a certain type
	 * @param compType	1--> 5 
	 * @param exported
	 * @param details
	 */
	public void listType(int compType, boolean exported,boolean details){		
		String expStr="";
		boolean isProv = (compType == TicklerConst.PROVIDER);
		ArrayList<String> names = this.getNamesOfCompTypes(compType, exported);
		
		if (exported)
			expStr="Exported ";
		else
			expStr="All ";
		OutBut.printH1(expStr+TicklerConst.compNames[compType]);
		
		if (isProv && !exported)
			OutBut.printH2("Content Providers in Manifest File");
		
		for (String n:names){
			if (details){
				this.listComponent(n);
			}
			else
				System.out.println(n);
		}
		
		if (isProv && !exported){
			this.listProvidersInCode();
		}
	}
	
	
	public void listComponent(String compName){
		this.dealer.analyzeManifest(TicklerVars.tickManifestFile);
		if (this.dealer.isComponentExist(compName)){
			IComponent comp = this.dealer.getComponentByName(compName);

			if (comp.getIntent()!= null)
				for (Intent i : comp.getIntent()){
					if (i.getData()!= null){
						DataUriHandler dh = new DataUriHandler(i);
						dh.doIt();
					}
				}
			
			this.printComponent(comp);
			this.printExtrasInfo(comp);
		}
		else{
			OutBut.printError("Component "+compName+" does not exist in the Manifest file");
		}

	}
	
	/**
	 * Prints all Content Provider URIs 
	 */
	private void listProvidersInCode(){
		OutBut.printH2("All Content URIs In Code");
		InfoGatheringReporting info = new InfoGatheringReporting();
		ArrayList<String> prov = info.fetchContentUris();
		
		for (String s : prov){
			System.out.println(s);
		}
	}
	
	
	/**
	 * Print name, type and whether exported or not
	 * @param comp
	 */
	private void printComponent(IComponent comp){
		OutBut.printH2(comp.getName());
		System.out.println("Type: "+this.getCompClassName(comp));
		System.out.println("Exported: "+((comp.isExported())?"True":"False"));
		this.printIntent(comp);
		
	}
	
	///////////////////////////// Intent Filters and Extras ///////////////////////////////
	/**
	 * Printing intents of a component, assuming its intents are not null
	 * @param comp
	 */
	private void printIntent(IComponent comp){
		if (comp.getIntent()!=null) {
			for(Intent i:comp.getIntent()){
				System.out.println("\nIntent-Filter:\n--------------");
				
				if (i.getAction()!=null){
					this.printActions(i);
				}
				
				if (i.getCategory()!= null){
					this.printCategories(i);
				}
				if (i.getData()!= null){
					this.printData(i);
				}
					
			}
		}
		
	}
	private void printActions(Intent i){
		for (Action a:i.getAction())
			System.out.println("Action: "+a.getName());
	}
	
	/**
	 * Print Categories of an intent
	 * @param i Intent
	 */
	private void printCategories(Intent i){
		for (Category c:i.getCategory())
			System.out.println("Category: "+c.getName());
	}
	
	
	/**
	 * Printing the whole data URI (new function)
	 * @param i
	 */
	private void printData(Intent i){
		DataUriHandler dH = new DataUriHandler(i);
		System.out.println("Data URI: ");
		for (DataUri d:i.getData()){
			dH.printData(d);
			System.out.println();
		}
	}
	
	/**
	 * Gets Extras Info from CodeUtil class and print the information
	 * @param c
	 */
	private void printExtrasInfo(IComponent c){
		String cName = c.getName();
		ExtrasUtil cU = new ExtrasUtil();
		ArrayList<String> extras = cU.getExtrasInfo(cName);
		
		
		if (extras.size()>0){
			System.out.println("\nExtras:\n--------");
			for (String s : extras)
				System.out.println(s);
			}
	}
	
	/////////////////// Util
	
	private String getCompClassName(IComponent c){
		String name="";
		String className = c.getClass().getName();
		
		if (className.equals("components.Activity"))
			name="Activity";
		else if (className.equals("components.Service"))
			name="Service";
		else if (className.equals("components.Receiver"))
			name="Broadcast Receiver";
		else if (className.equals("components.Provider"))
			name="Content Provider";
		
		return name;
	}
	
	private ArrayList<String> getNamesOfCompTypes(int type, boolean exported){
		
		ArrayList<IComponent> components = this.dealer.getComponentsOfType(type, exported);
		ArrayList<String> names= new ArrayList<>();
		
		for (IComponent c:components)
			names.add(c.getName());
		
		return names;
	}
	
	

}
