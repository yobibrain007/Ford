import java.util.ArrayList;


public class Predicate {
	
	String name;
	ArrayList<Argument> arguments;
	
	public  Predicate(String n) {
		arguments = new ArrayList<Argument>();
		name= n;
	}
	public void addArgument(Argument a){
		arguments.add(a);
	}
}
