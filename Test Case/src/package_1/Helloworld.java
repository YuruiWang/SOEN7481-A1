package package_1;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Helloworld {
	String keyword;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length == 0)
			System.out.println("Hello world!");
		/*String fileName = "abc";
		
		try 
		{
			InputStream file = new FileInputStream(fileName);
		    byte x = (byte) file.read();
		} 
		catch(FileNotFoundException f) { // Not valid!
			System.out.println("Connection to AMQP service is lost.");
		} 
		catch(IOException i) {
			System.out.println("Connection to AMQP service is lost.");
		}
		
		char grade = 'C';
  
		switch(grade)
		{
		   case 'A' :
		      System.out.println("优秀"); 
		      break;
		   case 'B' :
		   case 'C' :
		      System.out.println("良好");
		      break;
		   case 'D' :
		      System.out.println("及格");
		      break;
		   case 'F' :
		      System.out.println("你需要再努力努力");
		      break;
		   default :
		      System.out.println("未知等级");}
		}
	
    public int hashCode(String args[]) {
    	String keyword = new String("www.runoob.com");
        System.out.println("Hashcode is :" + keyword.hashCode() );  //s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]
        return keyword.hashCode();
    }
    
    public String equals(String args[]) {
        return keyword;
    }*/
	}

}