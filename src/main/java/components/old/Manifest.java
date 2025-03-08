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
package components.old;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


public class Manifest {
	
	private ArrayList<Intent> intents;
	private ArrayList<UsesPermission> usesPermissions;
	private ArrayList<Permission> Permissions;
	private Application application;
	private String pkgName;

	public ArrayList<Intent> getIntents() {
		return intents;
	}

	public void setIntents(ArrayList<Intent> intents) {
		this.intents = intents;
	}

	public ArrayList<UsesPermission> getUsesPermissions() {
		return usesPermissions;
	}
	
	public void setUsesPermissions(ArrayList<UsesPermission> usesPermissions) {
		this.usesPermissions = usesPermissions;
	}

	public ArrayList<Permission> getPermissions() {
		return Permissions;
	}

	public void setPermissions(ArrayList<Permission> permissions) {
		Permissions = permissions;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}
	
	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	
	
	

}
