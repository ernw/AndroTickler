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
import javax.xml.bind.annotation.XmlElements;

public class Application {
	
	boolean isAllowBackup,isDebuggable;
	List<Activity> activites;
	List<Service> services;
	List<Receiver> receivers;
	List<Provider> providers;
	
	public Application() {
		this.isAllowBackup = false;
	}
	
	public boolean isAllowBackup() {
		return isAllowBackup;
	}
	@XmlAttribute(name="allowBackup",namespace="http://schemas.android.com/apk/res/android")
	public void setAllowBackup(boolean isAllowBackup) {
		this.isAllowBackup = isAllowBackup;
	}
	public List<Activity> getActivites() {
		return activites;
	}
	@XmlElements({
			@XmlElement(name="activity"),
			@XmlElement(name="activity-alias")
	})
	public void setActivites(List<Activity> activites) {
		this.activites = activites;
	}
	public List<Service> getServices() {
		return services;
	}
	@XmlElement(name="service")
	public void setServices(List<Service> services) {
		this.services = services;
	}
	
	public List<Receiver> getReceivers() {
		return receivers;
	}
	@XmlElement(name="receiver")
	public void setReceivers(List<Receiver> receivers) {
		this.receivers = receivers;
	}

	public List<Provider> getProviders() {
		return providers;
	}
	@XmlElement(name="provider")
	public void setProviders(List<Provider> providers) {
		this.providers = providers;
	}
	
	public boolean isDebuggable() {
		return isDebuggable;
	}
	@XmlAttribute(name="debuggable",namespace="http://schemas.android.com/apk/res/android")
	public void setDebuggable(boolean isDebuggable) {
		this.isDebuggable = isDebuggable;
	}

	
}
