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

import initialization.TicklerVars;

public abstract class FridaVars {

	//script locations
	public static String ENUM_LOC = TicklerVars.fridaScriptsDir+"enumerate_classes.js";
	public static String GET_VALS_JS = TicklerVars.fridaScriptsDir+"get_attributes_output.js";
	public static String SET_VALS_JS = TicklerVars.fridaScriptsDir+"set_value_output.js";
	public static String SSL_CONTEXT_UNPIN_JS = TicklerVars.fridaScriptsDir+"unpin_ssl_context.js";
	
	
	
	/// Frida JS Scripts code
	public static String GET_VALS_CODE= "setTimeout(function(){	\n"	 +
			"	Java.perform(function () {	\n"	 +
			"		var className = Java.use(\"$className\");	\n"	 +
			"	            className.$method_name.implementation = function ($args) {	\n"	 +
			"					console.log(\"$method_name\"+\" method started\");	\n"	 +
			"					var returnValue = this.$method_name($args);	\n"	 +
			"					$console_log_inputs	\n"	 +
//			"	                	"	 +
			"					if (returnValue != null ) {console.log(\"Output: \"+returnValue.toString()+\"\\n\");}\n"	 +
			"					return returnValue;	\n"	 +
//			"		"	 +
			"	            };	\n"	 +
//			"		"	 +
			"	        });	\n"	 +
//			"		"	 +
			"	    },0);	\n";
	
	
	public static String SET_VALS_CODE = "	 setTimeout(function(){	\n	"	 +
			"	        Java.perform(function () {	\n	"	 +
			"	            var className = Java.use(\"$className\");	\n	"	 +
			"	            className.$methodName.implementation = function ($args) {	\n	"	 +
			"					console.log(\"$methodName\"+\" method started\");	\n"	 +
			"			var orig_return = this.$methodName($args);	\n	"	 +
			"			$output_line	\n	"	 +
			"			return $returnValue;	\n	"	 +
			"	            };	\n	"	 +
			"	        });	\n	"	 +
			"	    },0);	\n	";
	
	public static String sslContextUnpin = "	//Originally written by Piergiovanni Cipolloni : https://codeshare.frida.re/@pcipolloni/universal-android-ssl-pinning-bypass-with-frida/	\n	"	 +
			"	Java.perform(function (){	\n	"	 +
			"	     console.log(\"[.] Cert Pinning Bypass/Re-Pinning\");	\n	"	 +
			"	     var CertificateFactory = Java.use(\"java.security.cert.CertificateFactory\");	\n	"	 +
			"	     var FileInputStream = Java.use(\"java.io.FileInputStream\");	\n	"	 +
			"	     var BufferedInputStream = Java.use(\"java.io.BufferedInputStream\");	\n	"	 +
			"	     var X509Certificate = Java.use(\"java.security.cert.X509Certificate\");	\n	"	 +
			"	     var KeyStore = Java.use(\"java.security.KeyStore\");	\n	"	 +
			"	     var TrustManagerFactory = Java.use(\"javax.net.ssl.TrustManagerFactory\");	\n	"	 +
			"	     var SSLContext = Java.use(\"javax.net.ssl.SSLContext\");	\n	"	 +
			"	// Load CAs from an InputStream	\n	"	 +
			"	     console.log(\"[+] Loading our CA...\")	\n	"	 +
			"	     cf = CertificateFactory.getInstance(\"X.509\");	\n	"	 +
			"	     try {	\n	"	 +
			"	      var fileInputStream = FileInputStream.$new(\"$mitmCert\");	\n	"	 +
			"		    }	\n	"	 +
			"	    catch(err) {	\n	"	 +
			"	      console.log(\"[o] \" + err);	\n	"	 +
			"		    }	\n	"	 +
			"		    var bufferedInputStream = BufferedInputStream.$new(fileInputStream);	\n	"	 +
			"		  	var ca = cf.generateCertificate(bufferedInputStream);	\n	"	 +
			"		    bufferedInputStream.close();	\n	"	 +
			"			var certInfo = Java.cast(ca, X509Certificate);	\n	"	 +
			"	     console.log(\"[o] Our CA Info: \" + certInfo.getSubjectDN());	\n	"	 +
			"		    // Create a KeyStore containing our trusted CAs	\n	"	 +
			"	     console.log(\"[+] Creating a KeyStore for our CA...\");	\n	"	 +
			"		    var keyStoreType = KeyStore.getDefaultType();	\n	"	 +
			"		    var keyStore = KeyStore.getInstance(keyStoreType);	\n	"	 +
			"		    keyStore.load(null, null);	\n	"	 +
			"	     keyStore.setCertificateEntry(\"ca\", ca);	\n	"	 +
			"		    // Create a TrustManager that trusts the CAs in our KeyStore	\n	"	 +
			"	     console.log(\"[+] Creating a TrustManager that trusts the CA in our KeyStore...\");	\n	"	 +
			"		    var tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();	\n	"	 +
			"		    var tmf = TrustManagerFactory.getInstance(tmfAlgorithm);	\n	"	 +
			"		    tmf.init(keyStore);	\n	"	 +
			"	     console.log(\"[+] Our TrustManager is ready...\");	\n	"	 +
			"	     console.log(\"[+] Hijacking SSLContext methods now...\")	\n	"	 +
			"	     console.log(\"[-] Waiting for the app to invoke SSLContext.init()...\")	\n	"	 +
			"	     SSLContext.init.overload(\"[Ljavax.net.ssl.KeyManager;\", \"[Ljavax.net.ssl.TrustManager;\", \"java.security.SecureRandom\").implementation = function(a,b,c) {	\n	"	 +
			"		   		console.log(\"[o] App invoked javax.net.ssl.SSLContext.init...\");	\n	"	 +
			"	      SSLContext.init.overload(\"[Ljavax.net.ssl.KeyManager;\", \"[Ljavax.net.ssl.TrustManager;\", \"java.security.SecureRandom\").call(this, a, tmf.getTrustManagers(), c);	\n	"	 +
			"		   		console.log(\"[+] SSLContext initialized with our custom TrustManager!\");	\n	"	 +
			"		   	}	\n	"	 +
			"	    });	\n	"	;
	
	public static String ENUM_CODE="Java.perform(\n"
	+"	function(){\n"
	+"		Java.enumerateLoadedClasses(\n"
  	+"		{\n"
	+"			\"onMatch\": function(className){\n" 
	+"				console.log(className) \n"
	+"			},\n"
	+"			\"onComplete\":function(){}\n"
  	+"		}\n"
	+"	)\n"
	+"})\n";

}
