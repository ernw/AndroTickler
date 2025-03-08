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

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
/**
 * TODO: assume multiple mime-types, pathPrefixes
 * @author aabolhadid
 *
 */
public class DataUri {
	private String scheme,host,port,path,pathPrefix,pathPattern,mimeType;
	private Map<String,String>dataMap;

	public String getScheme() {
		return scheme;
	}
	@XmlAttribute(name="scheme",namespace="http://schemas.android.com/apk/res/android")
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	@XmlAttribute(name="host",namespace="http://schemas.android.com/apk/res/android")
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	@XmlAttribute(name="port",namespace="http://schemas.android.com/apk/res/android")
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	@XmlAttribute(name="path",namespace="http://schemas.android.com/apk/res/android")
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	@XmlAttribute(name="pathPrefix",namespace="http://schemas.android.com/apk/res/android")
	public String getPathPrefix() {
		return pathPrefix;
	}
	public void setPathPrefix(String pathPrefix) {
		this.pathPrefix = pathPrefix;
	}
	@XmlAttribute(name="pathPattern",namespace="http://schemas.android.com/apk/res/android")
	public String getPathPattern() {
		return pathPattern;
	}
	public void setPathPattern(String pathPattern) {
		this.pathPattern = pathPattern;
	}
	@XmlAttribute(name="mimeType",namespace="http://schemas.android.com/apk/res/android")
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public Map<String,String> getDataMap(){
		dataMap =new HashMap<String,String>();
		this.fillScheme();

		this.fillRest("host", host);
		this.fillRest("path", path);
		this.fillRest("port", port);
		this.fillRest("pathPrefix", pathPrefix);
		this.fillRest("pathPattern", pathPattern);
		this.fillRest("mimeType", mimeType);
		
		return dataMap;
	}
	
	/**
	 * Fill scheme in the map.
	 * If empty then use content and file
	 */
	private void fillScheme(){
		if (scheme !=null)
			dataMap.put("scheme", this.getScheme());
	}
	
	private void fillRest(String key, String value){
		if (value != null)
			dataMap.put(key,value);
	}
	
	
	
}
