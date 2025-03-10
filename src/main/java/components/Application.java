/*******************************************************************************
 * Copyright 2019 Ahmad Abolhadid
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


public class Application {
	
	boolean isAllowBackup,isDebuggable;
	List<Activity> activites;
	List<Service> services;
	List<Receiver> receivers;
	List<Provider> providers;
	String name;
	
	public Application() {
		this.isAllowBackup = false;
	}
	
	public boolean isAllowBackup() {
		return isAllowBackup;
	}
	public void setAllowBackup(boolean isAllowBackup) {
		this.isAllowBackup = isAllowBackup;
	}
	
	public List<Activity> getActivites() {
		return activites;
	}
	
	public void setActivites(List<Activity> activites) {
		this.activites = activites;
	}
	public List<Service> getServices() {
		return services;
	}
	
	public void setServices(List<Service> services) {
		this.services = services;
	}
	
	public List<Receiver> getReceivers() {
		return receivers;
	}
	
	public void setReceivers(List<Receiver> receivers) {
		this.receivers = receivers;
	}

	public List<Provider> getProviders() {
		return providers;
	}

	public void setProviders(List<Provider> providers) {
		this.providers = providers;
	}
	
	public boolean isDebuggable() {
		return isDebuggable;
	}

	public void setDebuggable(boolean isDebuggable) {
		this.isDebuggable = isDebuggable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
}
