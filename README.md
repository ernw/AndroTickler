**AndroTickler**
=================

A java tool that helps to pentest Android apps faster, more easily and more efficiently. AndroTickler offers many features of information gathering, static and dynamic checks that cover most of the aspects of Android apps pentesting. It also offers several features that pentesters need during their pentests. AndroTickler also integrates with Frida to provide method manipulation and pinning circumvention. It was previously published under the name of Tickler.

AndroTickler requires a linux host and a rooted Android device connected to its USB port. The tool does not install anything on the Android device, it only creates a *Tickler* directory on /sdcard . AndroTickler depends on Android SDK to run commands on the device and copy app's data to *TicklerWorkspace* directory on the host for further analysis. *TicklerWorkspace* is the working directory of AndroTickler and each app has a separate subdirectory in *TicklerWorkspace* which can contain the following (depending on user actions):
- DataDir directory: a copy of the data directory of the app
- extracted directory: Output of apktool on the app, contains smali code, resources, libraries...etc.
- bgSnapshots directory: Contains background snapshots copied from the device.
- images directory: contains any screenshots taken for the app.
- JavaCode directory: Contains app's Java code decompiled by dex2jar and JD tools
- logs directory: contains log files produced by -t -log, as explained below
- transfers: files and directories copied from the device to the host using -copy2host
- AndroidManifest.xml: The manifest file of the app as per apktool
- base.apk: the APK file of the app, installed on the device
- debuggable.apk: a debuggable version of the app, produced by -dbg

*libs* directory and *Tickler.conf* configuration file exist in the same directory of the jar file. The configuration file sets the location of *TicklerDir* directory on the host and *Tickler* on /sdcard of the android device. If the configuration file does not exist or these 2 directories are not set, then default values will be used (Tickler\_workspace on the current directory and /sdcard/Tickler respectively). *Tickler_lib* directory contains some Java libraries and external tools used by AndroTickler such as apktool and dex2jar. 

AndroTickler highly depends on the following tools, so they should exist on your machine before using it:
- Java 7 or higher
- Android SDK tools (adb and friends) 
- sqlite3

Other tools are required for some features, but AndroTickler can still run without them:
- Frida
- jarsigner

How to use it
=============
1) Build tool from code
2) Make sure Tickler.jar is in the same directory as Tickler_lib directory and Tickler.conf file
3) Connect your Android device with the application-to-test installed on   


The current version does the following:

Command help
=============

    java -jar Tickler.jar -h

Information gathering/Static analysis:
======================================

    java -jar Tickler.jar -pkgs
Lists installed Apps on the device	


    java -jar Tickler.jar -findPkg <searchKey>
Searches for an app (package) installed on the device, whose package name contains the searchKey


    java -jar Tickler.jar -pkg <package> [other options]
Any command with a -pkg option (whether used with any of the following options or not), does the following actions if they have not been done before:
- Copies the app from the device
- Extracts the Manifest file of the app
- Decompiles the app to Java code using dex2jar and JD tools

.

    java -jar Tickler.jar -pkg <package> -info

Returns the following information:
- App's user ID
- App's Directories path
- If the app's code indicate usage of external storage
- App's directories that already exist in External storage
- Content URIs in the code
- If the app is backable
- If the app is debuggable
- Data schemes (like iOS IPC)
- The permissions it uses

.

    java -jar Tickler.jar -pkg <package> -squeeze

Fetches the following from the decompiled Java code of the app:
- Log messages
- Any indication of possible user credentials
- Java comments
- All strings in the code

Unsurprisingly, its output is usually huge, so it is recommended to redirect the command's output to a file


    java -jar Tickler.jar -pkg <package> -l [-exp]
Lists all components of the app 

-exp: show only exported components


    java -jar Tickler.jar -pkg <package> -l [-act | -ser | -rec | -prov ] [-exp] [-v]

Lists any kind of components
- -act : activities
- -ser : services
- -rec: broadcast receivers
- -prov: Content providers
- -exp: show only exported components of any of the above type 
- -v: more detailed information for each component:
 - Component type
 - Whether exported or not
 - Its intent filters 
 - The tool checks the corresponding Java class to each component and returns all possible intent extras 

.

    java -jar Tickler.jar -pkg <package> -db [ |e]
	
Tests whether the databases of the app are encrypted. It is the default action in case no option is given after -db flag.

By default, all -db commands update the app's data directory on the host before running the check.


    java -jar Tickler.jar -pkg <package> -db [l]
Lists all databases of the app. Encrypted databases might not be detected.

    java -jar Tickler.jar -pkg <package> -db [d]
	
Takes a sqlite dump of any of the unencrypted databases.

    java -jar Tickler.jar -pkg <package> -db [|e|l|d] [nu|noUpdate]
    
nu or noUpdate runs any of the above options without updating the app's data directory on the host.

    java -jar Tickler.jar -pkg <package> -diff
	
Copies the data directory of the app (to DataDirOld) then asks the user to do the action he wants and to press Enter when he's done. Then it copies the data directory again (to DataDir) and runs diff between them to show which files got added, deleted or modified. 

    java -jar Tickler.jar -pkg <package> -diff [d|detailed]
Does the same as the normal -diff command, also shows what exactly changed in text files and unencrypted databases.

    java -jar Tickler.jar -pkg <package> -sc <key>
Searches the decompiled Java code of the app for the given key

    java -jar Tickler.jar -pkg <package> -sd <key>
Searches the Data directory of the app for the given key

    java -jar Tickler.jar [-pkg <package>] [-bg|--bgSnapshots]
Copies the background snapshots taken by the device (works with and without -pkg option)

Tickling
=========
Triggers components of the app, by all possible combinations of intents. For example, if an activity has an intent-filter of 2 possible actions and 3 data URI schemes, then AndroTickler will trigger this activity with all possible combinations of this intent. Additionally, AndroTickler captures the intent extras mentioned in the Java class corresponding to the component, assign them dummy values and add them to the possible intent combinations. Only extras of type boolean, string, int and float are supported. 
 
if the -exp option is used, then the components will be triggered without root privileges or any special permissions. If not, then the components will be trigged with root privileges. This helps to test the app in 2 different scenarios: against normal-privileged or high-privileged attackers.

Before triggering components, AndroTickler prints all the commands to be executed. Then for each command, it triggers the component, prints the command then waits for the user. This gives the user enough time to do any extra checks after the command's execution. Before the user moves on to the next command, he's given the option to capture a screenshot of the device for PoC documentation.

    java -jar Tickler.jar -pkg <package> -t [-all | -exp] [target] [-log]
Triggers the targets as explained above.

[target] as in list command, the targets can be:

- -act : activities. starts the (activity/activities) with all intent combinations as explained above
- -ser : services. starts the service(s) with all intent combinations as explained above
- -rec: broadcast receivers: sends all possible broadcast messages that would match the broadcast receiver(s)
- -prov: Content providers:  queries the content provider(s)

if no value, then the target is all of the above

[-comp] <component_name>: Specifies one component only. -comp flag is optional

[-exp]: AndroTickler uses normal privileges to trigger only the exported targets

[-all]: The default option. AndroTickler uses root privileges to trigger the exported targets

[-log]: Captures all logcat messages generated during the triggering session. Log file is saved in logs subdirectory.


Frida:
======
Frida should be installed on your host machine. Also the location of Frida server on the Android device should be added to *Tickler.conf* file.

    java -jar Tickler.jar -pkg <package> -frida enum
Enumerates loaded classes

    java -jar Tickler.jar -pkg <package> -frida vals <ClassName> <MethodName> <NumberOfArgs> [-reuse]
Displays arguments and return value of this method (only primitive datatypes and String)

    java -jar Tickler.jar -pkg <package> -frida set <ClassName> <MethodName> <NumberOfArgs> <NumberOfArgToModify> <newValue>[-reuse]
Sets the argument number <NumberOfArgToModify> to <newValue> (only primitive datatypes and String)
If <NumberOfArgToModify> > <NumberOfArgs>: sets the return value 

    java -jar Tickler.jar -pkg <package> -frida unpin <CertificateLocation>
SSL pinning circumvention as in https://codeshare.frida.re/@pcipolloni/universal-android-ssl-pinning-bypass-with-frida/
<CertificateLocation> is the location of your Certificate on your host

<<<<<<< HEAD
    java -jar Tickler.jar -pkg <package> -frida script <scriptPath> <arguments>
Run custom frida JS script 
=======
    java -jar Tickler.jar -pkg <package> -frida script <scriptPath>
Runs a frida JS script located at <scriptPath> on your host
>>>>>>> b6c650e4cb53a65a05a50a95ecd128751aa56f91
 
In case of vals and set options, Frida creates/updates a Frida script of that functionality. You can modify the created script as you want, then if you want to run it through AndroTickler, then use *-reuse* option so that it doesn't get overridden.

Other Features
================= 

    java -jar Tickler.jar -pkg <package> -dbg
Creates a debuggable version of the app, which can be installed on the device and debugged using any external tool.

AndroTickler comes with a keystore to sign the debuggable apk, but it requires *jarsigner* tool on the host.

    java -jar Tickler.jar -pkg <package> -apk <decompiledDirectory>
Builds an apk file from a directory, signs it and installs it.

    java -jar Tickler.jar -pkg <package> -mitm
Modifies Network security configuration of the app to circumvent MitM restrictions on Android Nougat (not related to pinning) 

    java -jar Tickler.jar -pkg <package> -cp2host <source_path> [dest]
Copies files / directories from the android devices. 
- source_path is the absolute location of what you want to copy from the android device
- dest: optional name of the destination directory, which will be located anyway at *transfers* sudirectory. 

If dest option is not given then the directory's name will be the timestamp of the transaction. 

    java -jar Tickler.jar [-pkg <package>] -screen
- Captures the current screenshot of the device and saves them in *images* subdirectory
- Works with or without the package flag

.

    java -jar Tickler.jar [-pkg <package>] [-bg |--bgSnapshots]
- Copies all background snapshots saved on the device (related to the app or not) to *bgSnapshots* subdirectory.

Note
----
For the options that do not require -pkg option, their data will be saved at  *Tickler_Dir*/NoPackage



Examples:
---------

    java -jar Tickler.jar -pkg <package> -t  -act -exp
Triggers exported activities

    java -jar Tickler.jar -pkg <package> -t -prov -log
Queries all content providers and saves logcat messages until the tool stops execution

    java -jar Tickler.jar -pkg <package> -t <component_name> 
Triggers the component, type of triggering depends on the type of the component





