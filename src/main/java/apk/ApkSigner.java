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

package apk;

import java.io.File;

import cliGui.OutBut;
import commandExec.Commando;
import initialization.TicklerVars;

/**
 * Signs an APK if Jarsigner exists on the device
 * @author aabolhadid
 *
 */
public class ApkSigner {

	private boolean checkJarsigner(){
		//check if jarsigner exists
		Commando commando = new Commando();
		String cmd = "jarsigner -h";
		int result = commando.executeProcessListPrintOP(cmd, false);
		if (result == 0)
			return true;
		
		return false;
	}
	
	private boolean isKeyStoreExist(){
		File keystore = new File(TicklerVars.keyStore);
		return keystore.exists();
	}
	
	
	public boolean signApk(String apkPath){
		
		boolean ok = false;
		if (!this.checkJarsigner()){
			OutBut.printWarning("Cannot sign the new apk because jarsigner is not found on the host");
			OutBut.printNormal("\nThe debuggable apk needs to be signed, in order to be installed on the device.\njarsigner can be installed by installing Java JDK\n");
			
		}
			//jarsigner exists
		else if (!this.isKeyStoreExist()){
			OutBut.printError("Keystore needed to sign the new apk is not found at "+TicklerVars.keyStore);
		}
			
		else{
			OutBut.printStep("Signing the app using Tickler keystore");
			String cmd = "jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore "+TicklerVars.keyStore+" -storepass itsalright "+apkPath+" Tickler";
			Commando commando = new Commando();
			int result = commando.executeProcessListPrintOP(cmd, true);
			if (result == 0)
				ok = true;
		}
		
		return ok;
	}
}
