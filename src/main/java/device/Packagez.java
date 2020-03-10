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
package device;

import java.util.ArrayList;

import cliGui.OutBut;
import commandExec.Commando;
import initialization.TicklerVars;

public class Packagez {

	
	public boolean searchPackage(String key,boolean syso){
		boolean isFound=false;
		ArrayList<String> results = new ArrayList<>();
		
		String output = this.fetchInstalledPkgs();
		String[] opLines = output.split("\n");
		
		
		for (String s:opLines)
			if (s.toLowerCase().contains(key.toLowerCase())){
				isFound = true;
				results.add(s);
			}
		
		if (!results.isEmpty() && syso){
			OutBut.printH2("Possible Package names");
			for (String s:results){
				String res = s.substring(s.indexOf(":")+1);
				System.out.println(res);
			}
		}
		else if (syso)		
			System.out.println("No package matches the search key :(");
		return isFound;
		
	}
	/**
	 * Makes sure that the exact pkgName exists, Case sensitive and full match
	 * @param pkgName
	 */
	public boolean isPackageExist(String pkgName){
		String[] packages = this.fetchInstalledPkgs().split("\n");
		
		for (String pkg:packages){
			String pkg2 = pkg.replace("package:", "");
			if (pkgName.equals(pkg2))
				return true;
		}
		
		return false;
	}
	
	public String fetchInstalledPkgs(){
		String command = "pm list package ";
		Commando commando = new Commando();
		String output = commando.execADB(command);
		
		return output;
	}
	
	public void printInstalledPkgs() {
		String pkgs = this.fetchInstalledPkgs();
		OutBut.printH1("Installed Packages");
		System.out.println(pkgs);
	}

	/**
	 * execute pm dump command to get a package's information
	 * @return
	 */
	public ArrayList<String> dumpInfo(){
		ArrayList<String> matches = new ArrayList<>();
		String command = "pm dump "+TicklerVars.pkgName;
		
		Commando commando = new Commando();
		String dump = commando.execADB(command, false);
		
		String userID = this.getParameterFromDump("userId", dump);
		String codePath = this.getParameterFromDump("codePath", dump);
		String versionName = this.getParameterFromDump("versionName", dump);
		String firstInstallTime = this.getParameterFromDump("firstInstallTime", dump);
		
		matches.add("userID: "+userID);
		matches.add("API path: "+codePath);
		matches.add("Version name: "+versionName);
		matches.add("Installation date: "+firstInstallTime);
		
		return matches;
		
	}
	
	public String getParameterFromDump(String par, String dump) {
		String value="";
		int index = dump.indexOf(par);
		value = dump.substring(dump.indexOf("=", index)+1, dump.indexOf("\n", index));
		return value;
	}
	
}
