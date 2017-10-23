import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import edu.mit.jwi.item.POS;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

/**
 * This class consists of static methods that operate on Stanford CoreNLP
 * Either by initializing its environment for Part of speech "POS", getting a POS for a statement.
 */

public class StanfordPOS {

	private static StanfordCoreNLP stanfordCNLP;

	/**
	   * This method is used to initialize Stanford Environment 
	   * using Part of speech "POS" properties
	   */
	public static void intialize(){
		
		Properties properties = new Properties();
	    properties.put("annotators", "tokenize, ssplit, pos");
	    stanfordCNLP = new StanfordCoreNLP(properties);
	    
	}
	
	/**
	   * This method is used to getting the POS of a sentence.
	   * process the text using stanford 
	   * make the use of convertTokensToMap to put the output in map format
	   * @param text This is sentence to be processed
	   * @return HashMap this return contains in each entry a word of the sentences as a key
	   * accompanied by its POS as entry.
	   * Note that, the returned POS is of the WordNet data type not the stanford data type
	   */
	public static HashMap<String, POS> getPOS(String text){
	    Annotation annotation = stanfordCNLP.process(text);
	    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
	    
	    return convertTokensToMap(sentences.get(0).get(CoreAnnotations.TokensAnnotation.class));
	}
	
	/**
	   * This method is used to convert tokkens of a sentence to POS of the WordNet .
	   * iterate on tokens then get the word  and the stanford POS
	   * after that make a use of POSTransoformation class for converting 
	   * from Stanford POS to WordNet POS
	   * finally add in the map the word and its WordNet POS 
	   * @param tokens tokens of processed sentence
	   * @return HashMap this return contains in each entry word and its WordNet POS
	   */
	private static HashMap<String, POS> convertTokensToMap(List<CoreLabel> tokens) {
		HashMap<String , POS> posMap = new HashMap<String, POS>();
		 String word, pos;
		 for (CoreLabel token: tokens) {
 		    // this is the text of the token
 		    word = token.get(TextAnnotation.class);
 		    // this is the POS tag of the token
 		    pos = token.get(PartOfSpeechAnnotation.class);
 		    if(!posMap.containsKey(word))
 		    	posMap.put(word, POSTransoformation.map.get(pos));
     }
		return posMap;
	}

}
