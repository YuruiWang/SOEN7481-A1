package package_1;

public class wrong_switch {
	public static void func(String[] args) {
	char grade = 'C';
	switch(grade)
	{
	   case 'A' :
	      System.out.println("����"); 
	      break;
	   case 'B' :
	   case 'C' :
	      System.out.println("����");
	      break;
	   case 'D' :
	      System.out.println("����");
	      break;
	   case 'F' :
	      System.out.println("����Ҫ��Ŭ��Ŭ��");
	      break;
	   default :
	      System.out.println("δ֪�ȼ�");}
	}
}
