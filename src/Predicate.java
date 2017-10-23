import java.util.ArrayList;

/**
 * This class contains two attributes: the name of the predicate and the arguments 
 */

public class Predicate {
	
	String name;
	ArrayList<Argument> arguments;
	
	/**
	 * the constructor initialize the name of the predicate
	 * @param n the name of the predicate
	 */
	public  Predicate(String n) {
		arguments = new ArrayList<Argument>();
		name= n;
	}
	
	/**
	 * This method add argument to the argument list of the predicate
	 * @param a one argument
	 */
	public void addArgument(Argument a){
		arguments.add(a);
	}
}
