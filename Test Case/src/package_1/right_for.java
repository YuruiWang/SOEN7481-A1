package package_1;

public class right_for {
	public static void func(String[] args) {
		  int [] numbers = {10, 20, 30, 40, 50};
		  
		  for(int x : numbers ){
		     System.out.print( x );
		     System.out.print(",");
		  }
		  System.out.print("\n");
		  String [] names ={"James", "Larry", "Tom", "Lacy"};
		  for( String name : names ) {
		     System.out.print( name );
		     System.out.print(",");
		  }
	}
}
