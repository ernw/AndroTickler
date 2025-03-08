package apk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import base.FileUtil;
import brut.androlib.Androlib;
import brut.androlib.ApkOptions;
import cliGui.OutBut;
import commandExec.Commando;
import exceptions.TNotFoundEx;
import initialization.TicklerVars;

// Replacing ApkToolClass in this version to run apktool.jar ... rewritten in v3
public class ApkToolDude {
	
	String debuggableApk;
	public ApkToolDude(){
		this.debuggableApk = TicklerVars.appTickDir+"debuggable.apk";
	}
	
	/**
	 * Normal apkdecode as pervious versions... decompiling resources
	 * @param apkPath
	 * @throws TNotFoundEx
	 */
	public void apkToolDecode(String apkPath) throws TNotFoundEx {
		String args = " d -o "+TicklerVars.extractedDir+" "+apkPath;
		this.apkToolDecodeGeneral(apkPath, args);
		
	}
	
	private void apkToolDecodeGeneral(String apkPath, String args) throws TNotFoundEx{
		FileUtil fileT = new FileUtil(); 
		File apkPathFile = new File(apkPath);
		

		try {
			File file = new File("/dev/null");
			PrintStream ps = new PrintStream(new FileOutputStream(file));
			System.setErr(ps);
			
			this.executeApktoolCommand(args);
			
			System.setErr(System.err);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			fileT.copyOnHost(TicklerVars.extractedDir+"AndroidManifest.xml", TicklerVars.tickManifestFile,true);
		}
		catch(Exception e){
			OutBut.printError("Decompilation failed and Manifest cannot be obtained");
			TNotFoundEx eNotFound = new TNotFoundEx("Decompilation failed and Manifest cannot be obtained");
			throw eNotFound;
		}
	}
	
	
	public void apkToolCompile(String dirPath, String apkPath){
		String tempApktoolLog = TicklerVars.logDir+"debuggableCompile.log";
		String args = " b "+dirPath+" -o "+apkPath;
//		ApkOptions apkOptions = new ApkOptions();
		
		try{
			File file = new File(tempApktoolLog);
			OutBut.printH3("APKTool output:");
			this.executeApktoolCommand(args);
//			new Androlib(apkOptions).build(new File(dirPath), new File(apkPath));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * Wrapper of apktool.jar
	 * @param args
	 */
	private void executeApktoolCommand(String args) {
		Commando command = new Commando();
		String cmd = "java -jar "+TicklerVars.apktoolPath + args;
		
		command.executeProcessString(cmd);
	}
}
