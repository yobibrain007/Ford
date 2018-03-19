import java.util.HashMap;

import edu.mit.jwi.item.POS;

/**
 * This class consists of static methods for POS data type conversion
 */

public class POSTransoformation {

	public static HashMap<String, POS> map= new HashMap<String, POS>();
	
	/**
	   * This method is used to initialize map for converting from 
	   * Stanford POS data type to WordNet POS data type manually
	   */
	public static void intialize(){
		
		//The POS of the JWI contain (Noun, Verb, Adverb and adjectives only). Therefore, I transformed these parts only 
		//any word outside these parts (e.g. the, if...) has no stemmer
		
		map.put("NN", POS.NOUN);
		map.put("NNS", POS.NOUN);
		map.put("NNP", POS.NOUN);
		map.put("NNPS", POS.NOUN);
		
		map.put("VB", POS.VERB);
		map.put("VBD", POS.VERB);
		map.put("VBG", POS.VERB);
		map.put("VBN", POS.VERB);
		map.put("VBP", POS.VERB);
		map.put("VBZ", POS.VERB);

		map.put("RB", POS.ADVERB);
		map.put("RBR", POS.ADVERB);
		map.put("RBS", POS.ADVERB);
		
		map.put("JJ", POS.ADJECTIVE);
		map.put("JJR", POS.ADJECTIVE);
		map.put("JJS", POS.ADJECTIVE);
		
	}
}
