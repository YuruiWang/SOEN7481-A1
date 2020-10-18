package package_1;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class unrelevant_catch {
	public static void func(String[] args) {
	try 
	{
		InputStream file = new FileInputStream("abc");
	    byte x = (byte) file.read();
	    file.close();
	} 
	catch(IOException i) {
		System.out.println("no.");
	}
	}
}
