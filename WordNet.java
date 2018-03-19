import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;

/**
 * This class consists of static methods that operate on WordNet 
 * Either by initializing its environment or getting a stemmer using it.
 */

public class WordNet {

	private static IDictionary dict;
	
	/**
	   * This method is used to initialize WordNet Environment
	   * getting the path of the dictionary using system environment variable
	   * of the WordNet installation folder
	   * opening it for use
	   */
	public static void intialize() throws IOException{
		String wnHome = System.getenv("WNHOME");
		String path = wnHome + File.separator + "dict";
		URL url = new URL("file", null , path );
		
		// construct the dictionary object and open it
		dict = new Dictionary (url);
		dict.open();
	}
	
	/**
	   * This method is used to getting the stemmer of a word.
	   * @param word This is the word to be processed
	   * @param p This is the tagger of the word (e.g. noun, verb, adj and adv) 
	   * @return String this return contains the word stemmer if it exited.
	   */
	public static String getStemmer(String word, POS p){
		WordnetStemmer stemmer = new WordnetStemmer(dict);
		//return the first match
		if(stemmer.findStems(word, p).size() != 0)
			return stemmer.findStems(word, p).get(0);
		return null;
	}
}
