
public class Argument {

	boolean constArg;
	String value;
	
	public Argument(boolean flag, String val){
		constArg = flag;
		if(flag == true)
			value = val;
	}
}
