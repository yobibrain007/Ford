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


public class WordNet {

	private static IDictionary dict;
	public static void intialize() throws IOException{
		String wnHome = System.getenv("WNHOME");
		String path = wnHome + File.separator + "dict";
		URL url = new URL("file", null , path );
		
		// construct the dictionary object and open it
		dict = new Dictionary (url);
		dict.open();
	}
	
	public static String getStemmer(String word, POS p){
		WordnetStemmer stemmer = new WordnetStemmer(dict);
		//return the first match
		if(stemmer.findStems(word, p).size() != 0)
			return stemmer.findStems(word, p).get(0);
		return null;
	}
}
