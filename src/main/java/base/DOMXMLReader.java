package base;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.crypto.Data;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cliGui.OutBut;
import components.Manifest;
import components.Permission;
import components.Provider;
import components.Receiver;
import components.Service;
import components.UsesPermission;
import components.Action;
import components.Activity;
import components.Application;
import components.Category;
import components.DataUri;
import components.IComponent;
import components.Intent;

// Following https://www.journaldev.com/898/read-xml-file-java-dom-parser

public class DOMXMLReader {
	
	private String manifestPath;
	private Manifest manifest;
	
	 public static void main(String[] args) {
		 DOMXMLReader r = new DOMXMLReader("/home/a7mad/Documents/Mobiles/Android/AndroTickler/eclipseTickler/insecManTest.xml");
		 r.parselManifest();
	 }
	
	public DOMXMLReader(String manifestFile) {
		this.manifestPath = manifestFile;
		this.manifest = new Manifest();
		
	}
	
	/**
	 * Parse the Manifest File in general
	 */
	public Manifest parselManifest() {
		
		File manifestFile= new File(this.manifestPath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(manifestFile);
	        doc.getDocumentElement().normalize();
	        this.parseManDoc(doc);
	        
	        return this.manifest;
	        
	        
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	private void parseManDoc(Document doc) {
		ArrayList<UsesPermission> usesPerm = new ArrayList<UsesPermission>();
		ArrayList<Permission> perms = new ArrayList<Permission>();
		Application app = new Application();
	
		NodeList nl;
				
		//Application, components and Intent filters
		nl = doc.getElementsByTagName("application");
		for (int i=0;i<nl.getLength();i++) {
			app = this.parseApplication(nl.item(i));
		}
		
		
		nl = doc.getElementsByTagName("uses-permission");
		for (int i=0;i<nl.getLength();i++) {
			Element e = (Element) nl.item(i);
			UsesPermission p = new UsesPermission();
			p.setName( e.getAttribute("android:name"));
			usesPerm.add(p);
		}
		
		nl = doc.getElementsByTagName("permission");
		for (int i=0;i<nl.getLength();i++) {
			Element e = (Element) nl.item(i);
			Permission p = new Permission();
			p.setName( e.getAttribute("android:name"));
			p.setProtectionLevel(e.getAttribute("android:protectionLevel"));
			perms.add(p);
		}
		

		this.manifest.setPkgName(doc.getDocumentElement().getAttribute("package"));
		this.manifest.setApplication(app);
		this.manifest.setUsesPermissions(usesPerm);
		this.manifest.setPermissions(perms);
		
		
	}
	
	

/////////////////////////////////////// Application /////////////////////////////////
	
	private Application parseApplication(Node node) {
		ArrayList<Activity> actList = new ArrayList<Activity>();
		ArrayList<Service> serList = new ArrayList<>();
		ArrayList<Provider> provList = new ArrayList<>();
		ArrayList<Receiver> recList = new ArrayList<Receiver>();
		
		Application app = new Application();
		NodeList nl;
		app.setAllowBackup(true);
		app.setDebuggable(false);
		
		
		if (node.getNodeType()==Node.ELEMENT_NODE) {
			Element el = (Element) node;
			if (el.getAttribute("android:allowBackup").toLowerCase().equals("false"))
				app.setAllowBackup(false);
			
			if (el.getAttribute("android:debuggable").toLowerCase().equals("true"))
				app.setDebuggable(true);
			
			app.setName(el.getAttribute("android:name"));
		
		//Components
		
				nl = el.getElementsByTagName("activity");
				for (int i=0;i<nl.getLength();i++) {
					actList.add(this.parseActivity(nl.item(i)) );
				}
				
				nl = el.getElementsByTagName("activity-alias");
				for (int i=0;i<nl.getLength();i++) {
					actList.add(this.parseActivity(nl.item(i)) );
				}
				
				nl = el.getElementsByTagName("service");
				for (int i=0;i<nl.getLength();i++) {
					serList.add(this.parseService(nl.item(i)) );
				}
				
				nl = el.getElementsByTagName("provider");
				for (int i=0;i<nl.getLength();i++) {
					provList.add( this.parseProvider(nl.item(i)) );
				}
				
				nl = el.getElementsByTagName("receiver");
				for (int i=0;i<nl.getLength();i++) {
					recList.add( this.parseReceiver(nl.item(i)) );
				}
		}
		
		app.setActivites(actList);
		app.setServices(serList);
		app.setProviders(provList);
		app.setReceivers(recList);
		
		return app; 
	}
	

//////////////////////////////////////////// Components ///////////////////////////////////
	
	// IComponent
	
	private IComponent parseIComponent(Node node,IComponent compy) {
		NodeList nl;
		ArrayList<Intent> intFilList;
		
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) node;
			
    		compy.setName(element.getAttribute("android:name"));
    		compy.setPermission(element.getAttribute("android:permission"));
    		
    		if(element.hasAttribute("android:exported") && element.getAttribute("android:exported").toLowerCase().equals("true")) {
    			compy.setExported(true);
    		}
    		else
    			compy.setExported(false);
			
    		
    		
			//Intent Filters

            nl = element.getElementsByTagName("intent-filter");
            intFilList = new ArrayList();
            
    		for (int i=0;i<nl.getLength();i++) {
    			intFilList.add( this.parseIntentFilter(nl.item(i)) ) ;
    		}
    		compy.setIntent(intFilList);
    		

		}

		return compy;
	}
	
	//Activity
	
	private Activity parseActivity(Node node) {
		Activity activity = new Activity();
		
		activity = (Activity)this.parseIComponent(node, activity);
		return activity;
	}
	
	
	private Service parseService(Node node) {
		Service service = new Service();
		
		service = (Service)this.parseIComponent(node, service);
		return service;
	}
	
	private Provider parseProvider(Node node) {
		Provider prov = new Provider();
		prov = (Provider)this.parseIComponent(node, prov);
		
		 if (node.getNodeType() == Node.ELEMENT_NODE) {
	            Element element = (Element) node;
	            prov.setAuthorities(element.getAttribute("android:authorities"));
		 }
		 
		 return prov;
	}
	
	private Receiver parseReceiver(Node node) {
		Receiver rec = new Receiver();
		rec = (Receiver) this.parseIComponent(node, rec);
		
		return rec;
	}
	
	
///////////////////////////////////////////////////////// Intent Filters /////////////////////////////////////////	
	
	private Intent parseIntentFilter(Node node) {
		Intent inFl = new Intent();
		NodeList nl;
		ArrayList<Action> actionList;
		ArrayList<Category> catList;
		ArrayList<DataUri> dataList;
		
		 if (node.getNodeType() == Node.ELEMENT_NODE) {
	            Element element = (Element) node;
	             
	            //Actions
	            nl = element.getElementsByTagName("action");
	            actionList = new ArrayList();
	            for (int i=0;i<nl.getLength();i++) {
	            	actionList.add(this.parseAction(nl.item(i)));
	    		}
	            
	            //Categories
	            nl = element.getElementsByTagName("category");
	    		catList = new ArrayList<Category>();
	    		for (int i=0;i<nl.getLength();i++) {
	    			catList.add(this.parseCategory(nl.item(i)));
	    		}
	    		
	    		//Data 
	            nl = element.getElementsByTagName("data");
	    		dataList = new ArrayList<DataUri>();
	    		for (int i=0;i<nl.getLength();i++) {
	    			dataList.add(this.parseDataUri(nl.item(i)));
//	    			
	    		}
	    		
	    		inFl.setAction(actionList);
	    		inFl.setCategory(catList);
	    		inFl.setData(dataList);
		 }

		
		return inFl;
	}
	
	
	private Action parseAction(Node node) {
			
			Action action = new Action();
			
	        if (node.getNodeType() == Node.ELEMENT_NODE) {
	            Element element = (Element) node;
	            action.setName(element.getAttribute("android:name"));
	        }
			
			return action;
		}
	
	private Category parseCategory(Node node) {
		
		Category cat = new Category();
		
	    if (node.getNodeType() == Node.ELEMENT_NODE) {
	        Element element = (Element) node;
	        cat.setName(element.getAttribute("android:name"));
	        
	    }
		
		return cat;
	}
	
	private DataUri parseDataUri(Node node) {
		DataUri data = new DataUri();
		
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element e = (Element) node;
			
			data.setHost(e.getAttribute("android:host"));
			data.setScheme(e.getAttribute("android:scheme"));
			data.setPort(e.getAttribute("android:port"));
			data.setPath(e.getAttribute("android:path"));
			data.setPathPrefix(e.getAttribute("android:pathPrefix"));
			data.setPathPattern(e.getAttribute("android:pathPattern"));
			data.setMimeType(e.getAttribute("android:mimeType"));
		}
		

		
		return data;
	}


	///////////////////////////////////// Util ////////////////////////////////////////
	
	  private String getTagValue(String tag, Element element) {
	        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
	        Node node = (Node) nodeList.item(0);
	        return node.getNodeValue();
	    }

}
