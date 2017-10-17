import java.util.HashMap;

import edu.mit.jwi.item.POS;


public class POSTransoformation {

	public static HashMap<String, POS> map= new HashMap<String, POS>();
	
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
