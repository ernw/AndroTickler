	        setTimeout(function(){	
	        Java.perform(function () {	
	            var className = Java.use("java.lang.String");	
			            className.equals.implementation = function (arg0) {	
	  var returnValue = this.equals(arg0);	
	console.log("Input: "+arg0);
	
	                			if (returnValue != null ) {console.log("Output: "+returnValue.toString());}
			return returnValue;	
			            };	
			        });	
			    },0);	
