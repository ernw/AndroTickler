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
package manifest.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import components.DataUri;
import components.Intent;

public class DataUriHandler {
	private ArrayList<DataUri> totalDU, outOfMishMash;
	private Intent intent;
	private ArrayList<Map<String,String>> collection;
	
	public DataUriHandler(Intent intent){
		this.totalDU = new ArrayList<>();
		this.outOfMishMash = new ArrayList<>();
		this.intent = intent;
		this.collection = new ArrayList<>();
	}
	
	public void doIt(){
		this.collection = this.collectMap();
		this.totalDU = this.createDUsForSchemes();
		this.addDataUriComp("host");
		this.addDataUriComp("port");
		this.addDataUriComp("path");
		this.addDataUriComp("pathPattern");
		this.addDataUriComp("pathPrefix");
		this.addDataUriComp("mimeType");
		// If there are only dataURI tags with only MimeType 
		this.addMimeTypeOnly();
		this.addCompleteToMishmash();
		
	}
	
	private ArrayList<Map<String,String>> collectMap(){
		ArrayList<Map<String,String>> collection = new ArrayList<>();
		for (DataUri d:this.intent.getData()){
			Map<String,String> h = d.getDataMap();
			collection.add(h);
		}
		
		return collection;
	}
	
	/**
	 * Creates an array of DataUris, one for each scheme
	 * Problem: takes only the scheme !!!
	 * @return
	 */
	private ArrayList<DataUri> createDUsForSchemes(){
		ArrayList<DataUri> arr = new ArrayList<>();
		for (Map<String,String> h: this.collection){
			if (h.get("scheme")!= null){			
				DataUri d = this.getDataUriFromMap(h);

				if (this.isMapComplete(h))
					this.outOfMishMash.add(d);
				else
					arr.add(d);
				
			}
				
		}
		return arr;
	}
	
	/**
	 * If the map has enough components to be independent : scheme & host, it will be excluded from other Data maps mishmash
	 * @param map
	 * @return
	 */
	private boolean isMapComplete(Map<String,String> map){
		if (map.get("scheme")!=null && map.get("host")!=null)
			return true;
		return false;
		
	}
	
	
	private void addCompleteToMishmash(){
		this.totalDU.addAll(outOfMishMash);
	}
	
	private void addMimeTypeOnly(){
		if (this.isMimeTypeOnly()){
			DataUri dTypeOnly = new DataUri();
			this.totalDU.add(dTypeOnly);
			this.addDataUriComp("mimeType");			
		}
	}
	
	private boolean isMimeTypeOnly(){
		for (Map<String,String> h: this.collection){
			if (!(h.containsKey("mimeType") && h.size()==1))
				return false;
		}
		return true;
	}
	
	private void addDataUriComp(String compName){
		for (Map<String,String> h: this.collection){
			if (h.get(compName)!= null){
				this.updateTotalDU(compName, h);
			}
		}
		
	}
	
	private void updateTotalDU(String compName, Map<String,String> h){
		ArrayList<DataUri> tempDataUri = new ArrayList<>();
		
		for (DataUri d:this.totalDU)
		{
			DataUri d2 = this.copyDataUri(d);
			
			
			if (!this.isDataUriCompEmpty(compName, d)){
				tempDataUri.add(d);
			
				if (this.isPathConflict(compName,d)){
					this.clearPathValuesCuzConflict(d2);
				}
			}
			
			d2 = this.addCompByType(compName,h.get(compName),d);
			tempDataUri.add(d2);
			
		}
		this.totalDU = tempDataUri;
	}
	
	/**
	 * If there is a path conflict, delete all path properties in order to add the new one later
	 * @param d
	 */
	private void clearPathValuesCuzConflict(DataUri d){
		d.setPath(null);
		d.setPathPattern(null);
		d.setPathPrefix(null);
	}
	
	/**
	 * Added the occupied check to make sure that if multiple properties are defined in a single Data Tag, they will not be replaced (Not a solution asasan)
	 * @param name
	 * @param value
	 * @param d
	 * @return
	 */
	private DataUri addCompByType(String name,String value,DataUri d){
		DataUri dTemp = this.copyDataUri(d);
		switch(name)
			{
				case "scheme":
					dTemp.setScheme(value);
					break;
					
				case "host":
						dTemp.setHost(value);
					break;
					
				case "port":
						dTemp.setPort(value);
					break;
					
				case "path":
						dTemp.setPath(value);
					break;
					
				case "pathPattern":
						dTemp.setPathPattern(value);
					break;
					
				case "pathPrefix":
						dTemp.setPathPrefix(value);
					break;
					
				case "mimeType":
						dTemp.setMimeType(value);
					break;
			}
			return dTemp;		
	}
	
	/**
	 * Check if a map of a dataUri contains a DataUri component
	 * @param name
	 * @param h
	 * @return
	 */
	private boolean isDataUriCompEmpty(String name, DataUri d) {
		Map<String,String> h = d.getDataMap();
		return (h.get(name)== null);
	}
	
	/**
	 * If the component is a path* component and d has another path* component, return true
	 * Bardo not a solution :(
	 * @param compName
	 * @param d
	 * @return
	 */
	private boolean isPathConflict(String compName,DataUri d){
		if (compName.contains("path") && (d.getPath()!= null || d.getPathPattern()!= null || d.getPathPrefix()!= null))
			return true;
		return false;
	}
	
	private DataUri getDataUriFromMap(Map<String,String> h){
		DataUri d = new DataUri();
		String[] comps = {"scheme","host","port","path","pathPattern","pathPrefix","mimeType"};
		
		for(String s:comps){
			d = this.addCompByType(s, h.get(s), d);
		}
		
		return d;
	}
	
	private DataUri copyDataUri(DataUri d){
		DataUri d2 = new DataUri();
		d2.setHost(d.getHost());
		d2.setMimeType(d.getMimeType());
		d2.setPath(d.getPath());
		d2.setPathPattern(d.getPathPattern());
		d2.setPathPrefix(d.getPathPrefix());
		d2.setPort(d.getPort());
		d2.setScheme(d.getScheme());
		
		return d2;
	}
	
	/**
	 * Prints Data URI component of an intent, used with  -l
	 * @param d
	 */
	public void printData(DataUri d) {
		if (d.getScheme()!=null)
			System.out.println("\tScheme: "+d.getScheme());
		
		if (d.getHost()!=null)
			System.out.println("\tHost: "+d.getHost());
		
		if (d.getPort()!=null)
			System.out.println("\tPort: "+d.getPort());
		
		if (d.getPath()!=null)
			System.out.println("\tPath: "+d.getPath());
		
		if (d.getPathPattern()!=null)
			System.out.println("\tPath Pattern: "+d.getPathPattern());
		
		if (d.getPathPrefix()!=null)
			System.out.println("\tPath Prefix: "+d.getPathPrefix());
		
		if (d.getMimeType()!=null)
			System.out.println("\tMime Type: "+d.getMimeType());
	}
	
	/**
	 * Prepare the data URI part in am start command
	 * @param d
	 * @return
	 */
	public String getStartCommand(DataUri d) {
		String cmd="";
		if (d.getScheme()!=null && ! d.getScheme().isEmpty()) {
			cmd = " -d \""+d.getScheme()+"://";
		
		// If a scheme is not specified for the intent filter, all the other URI attributes are ignored.
		// https://developer.android.com/guide/topics/manifest/data-element
			if (d.getHost()!=null && !d.getHost().isEmpty()){
				String newVal=this.replaceAstrexInPathValues(d.getHost());
				cmd = cmd+newVal;
				}
			else {
				cmd=cmd+"TiCkLeR";
			}
			
			if (d.getPort()!=null && !d.getPort().equals("*") && !d.getPort().isEmpty())
				cmd = cmd+ ":"+d.getPort();
			
			if (d.getPath()!=null){
				String newVal = this.replaceAstrexInPathValues(d.getPath());
				cmd = cmd+newVal;
			}
			
			if (d.getPathPattern()!=null){
				String newVal = this.replaceAstrexInPathValues(d.getPathPattern());
				cmd = cmd + newVal;
			}
			
			if (d.getPathPrefix()!=null && !d.getPathPrefix().equals("*"))
				cmd = cmd+ d.getPathPrefix();
		}
		if (!cmd.isEmpty())
			cmd = cmd+"\"";
		
			if (d.getMimeType()!=null && ! d.getMimeType().isEmpty())
				cmd = cmd+" -t " +d.getMimeType();
		
		return cmd;
		
	}
	
	/**
	 * REplace the wildcards in Path values (prefix and pattern) with a dummy value
	 * @param path
	 * @return
	 */
	private String replaceAstrexInPathValues(String path){
		String newPath = path.replace(".*", "TiCkLeR");
		String newPath2 = newPath.replace("\\", "");
		newPath = newPath2.replace("*", "TiCkLeR");
		return newPath;
	}
	
	/**
	 * Gets All Data URI combinations
	 * @return
	 */
	public ArrayList<DataUri> getTotalDU() {
		return totalDU;
	}

	public void setTotalDU(ArrayList<DataUri> totalDU) {
		this.totalDU = totalDU;
	}

}
