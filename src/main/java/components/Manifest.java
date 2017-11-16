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
package components;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="manifest")
public class Manifest {
	
	private List<Intent> intents;
	private List<UsesPermission> usesPermissions;
	private List<Permission> Permissions;
	private Application application;
	private String pkgName;

	public List<Intent> getIntents() {
		return intents;
	}

	@XmlElement(name="intent-filter")
	public void setIntents(List<Intent> intents) {
		this.intents = intents;
	}

	public List<UsesPermission> getUsesPermissions() {
		return usesPermissions;
	}
	@XmlElement(name="uses-permission")
	public void setUsesPermissions(List<UsesPermission> usesPermissions) {
		this.usesPermissions = usesPermissions;
	}

	public List<Permission> getPermissions() {
		return Permissions;
	}
	@XmlElement(name="permission")
	public void setPermissions(List<Permission> permissions) {
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
	@XmlAttribute(name="package")
	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	
	
	

}
