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


public class StanfordPOS {
	
	private static Properties properties;

	public static void intialize(){
		
		properties = new Properties();
	    properties.put("annotators", "tokenize, ssplit, pos");
	    
	}
	public static HashMap<String, POS> getPOS(String text){
		StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
	    Annotation annotation = pipeline.process(text);
	    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
	    
	    return convertTokensToMap(sentences);
	}
	
	private static HashMap<String, POS> convertTokensToMap(
			List<CoreMap> sentences) {
		HashMap<String , POS> posMap = new HashMap<String, POS>();
		 List<CoreLabel> tokens = sentences.get(0).get(CoreAnnotations.TokensAnnotation.class);
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
