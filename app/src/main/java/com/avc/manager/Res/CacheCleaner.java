package com.avc.manager.Res;
import java.io.*;

public class CacheCleaner
{
	String re = null;
	
	public CacheCleaner()
	{
		try {

			// Executes the command.

			Process process = Runtime.getRuntime().exec("/system/bin/ls /sdcard");

			// Reads stdout.
			// NOTE: You can write to stdin of the command using
			//       process.getOutputStream().
			BufferedReader reader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));
			int read;
			char[] buffer = new char[4096];
			StringBuffer output = new StringBuffer();
			while ((read = reader.read(buffer)) > 0) {
				output.append(buffer, 0, read);
			}
			reader.close();

			// Waits for the command to finish.
			process.waitFor();

			re = output.toString();
		} catch (IOException e) {

			throw new RuntimeException(e);

		} catch (InterruptedException e) {

			throw new RuntimeException(e);
		}
	}
}
