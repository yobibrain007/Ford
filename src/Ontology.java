import java.util.HashMap;

/**
 * This class consists of static methods that operate on Ontology 
 * Either by initializing its environment or getting an ontology using it.
 */

public class Ontology {

	static HashMap<String, String> map;
	
	/**
	   * This method is used to initialize Ontology Environment
	   * Actually there it no Ontology environment and this part is made hard coded
	   */
	public static void intialize(){
		map = new HashMap<String, String>();
		map.put("equals", "predicate");
		map.put("true", "bool");
		map.put("false", "bool");
		map.put("invalid", "uniqe entity");
	}
	
	/**
	   * This method is used to getting the ontology of a word.
	   * @param word This is the word to be processed
	   * @return String this return contains the word ontology.
	   */
	public static String getOntology(String word){
		return map.get(word);
	}
}
