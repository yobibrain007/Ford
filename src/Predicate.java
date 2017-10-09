import java.util.ArrayList;


public class Predicate {
	
	String name;
	ArrayList<Argument> Arguments;
	
	public  Predicate(String n) {
		Arguments = new ArrayList<Argument>();
		name= n;
	}
	public void addArgument(Argument a){
		Arguments.add(a);
	}
}
