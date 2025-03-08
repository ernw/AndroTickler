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
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Service implements IComponent,IActivityService{
	String name,exp, permission;
	boolean isExported;
	ArrayList<Intent> intent;
	
	public Service() {
		this.isExported = false;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getExp() {
		return exp;
	}
	public void setExp(String exp) {
		this.exp = exp;
	}

	public boolean isExported() {
		return isExported;
	}
	
	public void setExported(boolean isExported) {
		this.isExported = isExported;
	}
	
	public ArrayList<Intent> getIntent() {
		return intent;
	}
	public void setIntent(ArrayList<Intent> intent) {
		this.intent = intent;
	}
	
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}

}
