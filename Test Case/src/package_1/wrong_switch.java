package package_1;

public class wrong_switch {
	public static void func(String[] args) {
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
}
