package base;

import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import cliGui.OutBut;

public class JsonParser {
	
	

	private JSONParser parser;
	private String squeezeFileLoc;
	
	public static void main(String[] args) {
		JsonParser jp = new JsonParser();
//		jp.parseJsonFile();
		
		String objo = "{ \"squeeze\": [{\"Title\":\"World accessible files\",\"Values\":[ \"MODE_WORLD_READABLE\", \"MODE_WORLD_WRITABLE\"]},"
				+ "{\"Title\":\"WebView\",\"Values\":[ \"addJavascriptInterface\", \"setAllowContentAccess\", \"setAllowFileAccess\", \"setAllowUniversalAccess\" ]}]}";
		ArrayList<SimpleEntry<String,ArrayList<String>>> arr = jp.parseJsonString(objo);
		
//		System.out.println("--"+title+"--");
//		for (int j=0;j<valuesJA.size();j++) {
//			System.out.println(valuesJA.get(j).toString());
//		}
		
		for (SimpleEntry<String,ArrayList<String>> e : arr) {
			System.out.println(e.getKey());
			OtherUtil.printStringArray(e.getValue());
		}

		
	}
	
	public JsonParser() {
		parser = new JSONParser();
		//For now
		squeezeFileLoc="/home/a7mad/eclipse-workspace/TicklerV2.1/bin/Squeeze.conf";
	}
	
	
	private void parseJsonFile() {
		try {
//			String objo = "\"squeeze\":[{\"a\":\"aa\",\"b\":\"bb\"}]}";
			String objo = "{ \"squeeze\": [{\"Title\":\"World accessible files\",\"values\":[ \"MODE_WORLD_READABLE\", \"MODE_WORLD_WRITABLE\"]},"
					+ "{\"Title\":\"WebView\",\"values\":[ \"addJavascriptInterface\", \"setAllowContentAccess\", \"setAllowFileAccess\", \"setAllowUniversalAccess\" ]}]}";
//			Object objy = parser.parse(new FileReader(this.squeezeFileLoc));
			Object objy = parser.parse(objo);
			JSONObject jsonObj = (JSONObject) objy;
			
//			JSONArray arr = (JSONArray) objy; 
			JSONArray arr = (JSONArray)jsonObj.get("squeeze");
			
			System.out.println(arr.size());
			
			Iterator itr1 = arr.iterator();
			Iterator itr2 = arr.iterator(); 
			
//	        while (itr2.hasNext())  
//	        { 
	        	
//	            itr1 = ((Map) itr2.next()).entrySet().iterator(); 
//	            while (itr1.hasNext()) { 
//	                Map.Entry pair = itr1.next(); 
//	                System.out.println(pair.getKey() + " : " + pair.getValue()); 
//	            } 
//	        } 
			String test="";
			for (int i=0;i<arr.size();i++) {
				test= arr.get(i).toString();
				System.out.println(test);
			}
			
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<SimpleEntry<String,ArrayList<String>>> parseJsonString(String jsonString) {

//			String jsonString = "{ \"squeeze\": [{\"Title\":\"World accessible files\",\"Values\":[ \"MODE_WORLD_READABLE\", \"MODE_WORLD_WRITABLE\"]},"
//					+ "{\"Title\":\"WebView\",\"Values\":[ \"addJavascriptInterface\", \"setAllowContentAccess\", \"setAllowFileAccess\", \"setAllowUniversalAccess\" ]}]}";
	
		String jsonStr="",title="";
		JSONObject jsObj2;
		JSONArray valuesJA;
		SimpleEntry<String,ArrayList<String>> sEnt;
//		ArrayList<SimpleEntry<String,ArrayList<String>>> squeezeParams= new ArrayList<>();
		ArrayList<SimpleEntry<String,ArrayList<String>>> squeezeParams= new ArrayList<>();
			
		JSONObject rootJO = this.parseStringToObj(jsonString);
		JSONArray squeezeJA = (JSONArray)rootJO.get("squeeze");
		
		for (int i=0;i<squeezeJA.size();i++) {
			
			jsonStr= squeezeJA.get(i).toString();
			jsObj2 = this.parseStringToObj(jsonStr);
			title = (String)jsObj2.get("Title");
		
			valuesJA= (JSONArray) jsObj2.get("Values");
			ArrayList<String> aL= (ArrayList<String>)valuesJA;
			sEnt=new SimpleEntry<>(title,aL);
			squeezeParams.add(sEnt);
			
		}
		
		return squeezeParams;
		
		
		
	}

	
	
	private JSONObject parseStringToObj(String jsonStr) {
		
		try {
			Object objy = parser.parse(jsonStr);
			JSONObject jsonObj = (JSONObject) objy;
			return jsonObj;
		}
		catch(Exception e) {
			OutBut.printError("Error while Parsing Squeeze JSON Configuration");
			e.printStackTrace();
			return null;
		}
		
//		return jsonObj;
	}
	
}
