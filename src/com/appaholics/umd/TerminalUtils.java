package com.appaholics.umd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.os.Environment;
import android.util.Log;

public class TerminalUtils {

	public static String TAG = "UM";
	
	public static void runAsRoot(String[] cmds){
        Process p;
		try {
			p = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());  
			BufferedReader bf = new BufferedReader(new InputStreamReader(p.getInputStream()));
			for (String tmpCmd : cmds) {
                os.writeBytes(tmpCmd+"\n");
                String test;
                while((test = bf.readLine()) != null)
                {
                	Log.i(TAG, test);
                }
			}
                
			//os.writeBytes("exit\n");  
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void createUser(String name)
	{
		Process p;
		try {
			p = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());  
			
            os.writeBytes("pm create-user \"" + name + "\"\n");
            os.writeBytes("exit\n");
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteUser(String id)
	{
		Process p;
		try {
			p = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());  
			
            os.writeBytes("pm remove-user " + id + "\n");
            os.writeBytes("exit\n");
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void switchUser(String id)
	{
		Process p;
		try {
			p = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());  
			
            os.writeBytes("am switch-user " + id + "\n");
            os.writeBytes("exit\n");
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String[] renameUser(String id, String newName)
	{
        Process p;
		try {
			p = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());  
			BufferedReader bf = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
                os.writeBytes("cat /data/system/users/" + id + ".xml\n");
                os.writeBytes("exit\n"); 
                ArrayList<String> users = new ArrayList<String>();
                String test;
                int ctr = 1;
                while((test = bf.readLine()) != null)
                {
                	
                	Log.i(TAG, test);
                	users.add(test);
                	Log.i(TAG, "" + (ctr++));
                	
                }
                Log.i(TAG, "Loop done.");

                String[] content = (String[]) users.toArray(new String[users.size()]);
                
                for(ctr = 0;ctr<content.length;ctr++)
                {
                	int begin = content[ctr].indexOf("<name>");
                	if(begin!=-1)
                	{
                		int end = content[ctr].indexOf("</name>");
                		begin = begin + 6;
                		String oldName = content[ctr].substring(begin, end);
                		content[ctr] = content[ctr].replaceAll(oldName, newName);
                		Log.i(TAG, content[ctr]);
                	}
                }
                
                String savedFile = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + id + ".xml";
                
                FileWriter fw = new FileWriter(new File(savedFile));
                BufferedWriter bw = new BufferedWriter(fw);
                
                for(ctr = 0; ctr<content.length;ctr++)
                {
                	bw.write(content[ctr]);
                	bw.write("\n");
                }
                
                bw.close();
                
                copyFile(id, savedFile);
			 
			os.flush();
			return content;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void copyFile(String id, String savedFile) {
		Process p;
		try {
			p = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());  
			
            os.writeBytes("cat " + savedFile + " > /data/system/users/" + id + ".xml\n");
            os.writeBytes("rm -f " + savedFile);
            os.writeBytes("exit\n");
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void reboot() {
		
		Process p;
		try {
			p = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());  
			
            os.writeBytes("reboot\n");
            os.writeBytes("exit\n");
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String[] getUserList()
	{
        Process p;
		try {
			p = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());  
			BufferedReader bf = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
                os.writeBytes("pm list-users"+"\n");
                os.writeBytes("exit\n"); 
                ArrayList<String> users = new ArrayList<String>();
                String test;
                bf.readLine();
                while((test = bf.readLine()) != null)
                {
                	users.add(test);
                }

                String[] userList = (String[]) users.toArray(new String[users.size()]);
                

			 
			os.flush();
			return userList;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
