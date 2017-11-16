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
package frida;

import java.util.ArrayList;

import base.FileUtil;
import commandExec.Commando;

public class FridaUnpinSslContext extends FridaJsAction{
	private FileUtil fU;

	public FridaUnpinSslContext(boolean reuseScript) {
		this.fU = new FileUtil();
		
		this.script = new FridaJsScript(FridaVars.SSL_CONTEXT_UNPIN_JS);
		if (reuseScript)
			this.code = this.script.getCodeFromScript();
		else
			this.code = FridaVars.sslContextUnpin;
	}
	
	public void run(ArrayList<String> args){
		String finalCode = this.prepareCode(args);
		this.executeNoSpawn(finalCode);
	}
	
	private String prepareCode(ArrayList<String> args) {
		Commando commando = new Commando();
		String certLoc = args.get(1);
		String certName= this.fU.getFileNameFromPath(certLoc);
		String certOnDevice="/data/local/tmp/"+certName;
		this.fU.copyToDevice(certLoc, certOnDevice);
		String finalCode = this.code.replaceAll("\\$mitmCert", certOnDevice);
		commando.execRoot("chmod 666 "+certOnDevice);
		return finalCode;
	}
}
