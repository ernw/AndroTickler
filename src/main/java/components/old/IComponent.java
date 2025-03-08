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
package components.old;

import java.util.ArrayList;

public interface IComponent {

	public boolean isExported();
	public void setExported(boolean isExported);
	public ArrayList<Intent> getIntent();
	public String getName();
	public String getPermission();
	
	public void setName(String name);
	public void setPermission(String permission);
	public void setIntent(ArrayList<Intent> intentFilters);

}
