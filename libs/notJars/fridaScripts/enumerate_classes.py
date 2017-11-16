#!/usr/bin/python
import frida
import sys
appName= sys.argv[1]
print(appName)
# put your javascript-code here
jscode= """
Java.perform(
	function(){
		var grepped = "ssl";
		Java.enumerateLoadedClasses(
  			{
			  "onMatch": function(className){ 
			  //if (className.toLowerCase().includes(grepped))
		   	  console.log(className) 
		        	},
			  "onComplete":function(){}
  			}
		)
	})



"""

# startup frida and attach to com.android.chrome process on a usb device
session = frida.get_usb_device().attach(appName)

# create a script for frida of jsccode
script = session.create_script(jscode)

# and load the script
script.load()

session.detach()

