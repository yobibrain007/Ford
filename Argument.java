
/**
 * This class consists of two attributes: flag for identifying whether it is a constant or not 
 * and its value in case it is a constant argument
 */
public class Argument {

	
	boolean constArg;
	String value;
	/**
	 * this constructor for intializing the class attributes.
	 * If flag is false then the argument is a variable value and no need for writing its name
	 * otherwise the argument is constant and we should maintain its value
	 * @param flag
	 * @param val
	 */
	public Argument(boolean flag, String val){
		constArg = flag;
		if(flag == true)
			value = val;
	}
}
