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
import java.util.LinkedHashSet;
import java.util.AbstractMap.SimpleEntry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import base.SearchUtil;
import components.Provider;

/**
 * 1- Get content URIs from provider authorities
 * 2- Get content URIs from DEX
 * 3- Add content URIs from 2 if do not exist
 * @author aabolhadid
 *
 */

public class ProviderAttacker {
	private ArrayList<String> contentURIs;
	private Provider provider;
	
	
	/////////////////// SMALI //////////////////////
	
	public ArrayList<String> queryUrisFromSmali(String smaliPath){
		ArrayList<String> commands = new ArrayList<String>();
		this.getContentURIsFromSmali(smaliPath);
		for (String uri:this.contentURIs)
			commands.add(this.queryContent(uri));
		
		return commands;
	}
	
	public void getContentURIsFromSmali(String smaliPath){
		
		this.contentURIs = new ArrayList<String>();
		ArrayList<String> contents = new ArrayList<String>();
		ArrayList<SimpleEntry> contentsReturn =new ArrayList<SimpleEntry>(); 
		SearchUtil searcher = new SearchUtil();
		String contentUri;
		
		contents = searcher.search4KeyInDir(smaliPath, "content://");
		
		for (String c:contents){
			if (!"".equals(contentUri = this.correctContentUri(c)))
				this.contentURIs.add(contentUri);
		}
		this.contentURIs = this.removeDuplicates(this.contentURIs);
	}
	
	private String correctContentUri(String line) {
		String contentUri="";
		//Content URI = content://+any_number_of(non space char) until the first occurrence of " or ' or a space character (space or new line)
		Matcher m = Pattern.compile("content://(\\S+?)[\"'\\s]").matcher(line);
		if (m.find()) {
			contentUri = line.substring(m.start(0),m.end(0)-1);
		}
		return contentUri;
	}
	
	
	private void checkAndAddUri(String uri) {
		if (!uri.equals("content://")){
			this.contentURIs.add(uri);
		}
		if (!uri.endsWith("/")){
			this.contentURIs.add(uri+"/");
		}
	}
	
	///////////////////////////// From manifest ///////////////////////////
	
	public String prepareContentFromAuthority(Provider prov){
		String uri= "content://"+prov.getAuthorities();
		return uri;
	}
	
	////////////////////////// Query /////////////////////////////
	public String queryContent(String contentURI) {
		return "content query --uri "+contentURI;
	}
	
	public ArrayList<String> queryContents(){
		ArrayList<String> queryCommands = new ArrayList<String>();
		for (String cont:this.contentURIs){
			queryCommands.add(this.queryContent(cont));
		}
		
		return queryCommands;
			
	}
	
	/**
	 * 1- get content URI from authorities
	 * 2- add related Content URIs
	 * 3- query content
	 * 4- sql injection 
	 * @param prov
	 * @return
	 */
	public ArrayList<String> attackProvider(Provider prov){
		ArrayList<String> relatedUris = new ArrayList<String>();
		ArrayList<String> commands = new ArrayList<String>();
		String uriFromAuth = this.prepareContentFromAuthority(prov);
		relatedUris.add(uriFromAuth);
		commands.add(this.queryContent(uriFromAuth));

		for (String uri:this.contentURIs){
			if (uri.contains(prov.getAuthorities()) && !uri.equals(uriFromAuth)){
				relatedUris.add(uri);
				commands.add(this.queryContent(uri));
			}
		}
		
		return commands;
		
	}

	public ArrayList<String> getContentURIs() {
		return contentURIs;
	}

	public void setContentURIs(ArrayList<String> contentURIs) {
		this.contentURIs = contentURIs;
	}
	
	private ArrayList<String> removeDuplicates(ArrayList<String> orig){
		return new ArrayList<String>(new LinkedHashSet<String>(orig));
		
	}

	
	}
