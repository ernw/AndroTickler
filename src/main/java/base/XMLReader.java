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
package base;

import java.awt.List;
import java.io.File;
import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import attacks.ActivityStarter;
import attacks.Broadcaster;
//import brut.androlib.ApktoolProperties;
import components.Manifest;

/**
 * Marshal and Unmarshal Manifest file
 * Unused since v2.3
 * @author aabolhadid
 *
 */
public class XMLReader{
	private String manifestFile;
	private Manifest manifest;
	
	public XMLReader(String manifestFile) {
		this.manifestFile = manifestFile;
		this.unmarshalManifest();
	}

	public void unmarshalManifest() {
		Manifest man=new Manifest();
		File manifest = new File(this.manifestFile);
		
		try {
	        
			JAXBContext context = JAXBContext.newInstance(Manifest.class);
			Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
			man = (Manifest) jaxbUnmarshaller.unmarshal(manifest);
		}
		catch(Exception e)
		{
			System.out.println("ERROR: Manifest cannot be parsed");
			e.printStackTrace();
		}
		
		this.manifest = man;
	}
	
	public Manifest getManifest() {
		return this.manifest;
		
	}

}
