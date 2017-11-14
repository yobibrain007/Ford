import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.jpl7.fli.term_t;

import edu.mit.jwi.item.POS;
import edu.stanford.nlp.io.EncodingPrintWriter.out;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

/**
 * This class consists of static methods that operate on Stanford typed dependency 
 */

public class Dependency {
	/**
	   * This method is used to getting the typed dependency of a sentence.
	   * @param text This is the sentence to be processed
	   * @return Collection<TypedDependency> this return contains the typed dependency "mentions".
	   */
	public static Collection<TypedDependency> getTypeDependency(String text){
		LexicalizedParser lp = LexicalizedParser.loadModel(
				"edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz",
				"-maxLength", "80", "-retainTmpSubcategories");
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		// Uncomment the following line to obtain original Stanford Dependencies
		// tlp.setGenerateOriginalDependencies(true);
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		Tree parse = lp.apply(prepareString(text));
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		Collection<TypedDependency> tdl = gs.typedDependenciesEnhancedPlusPlus();
		return tdl;
	}
	
	/**
	   * This method is used to pars sentence to word set.
	   * @param text This is the sentence to be processed
	   * @return List<? extends HasWord> this return contains words.
	   */
	private static List<? extends HasWord> prepareString(String text){
		return SentenceUtils.toWordList(text.split(" "));
	}
    
	/**
	   * This method is used to create mention table from typed dependency.
	   * the method iterate over the mentions of the type dependency
	   * make the use of parsePredicate method to get  mentionIds of the mention
	   * make the use of getMentionIdRelatedMentions method to get all related mentions of each mentionId
	   * for each mentionId put in the map an entry contains mentionId and its related mentions
	   * @param td This is the type dependency of a sentence
	   * @return HashMap<String, ArrayList<String>> this return contains in each entry 
	   * mentionId as a key and its related mentions as value
	   */
	public static HashMap<String, ArrayList<String>> createMentionTable(Collection<TypedDependency> td){
		
		HashMap<String, ArrayList<String>> mentionTable = new HashMap<String, ArrayList<String>>();
		 ArrayList<String> mentionIDs;
		for (TypedDependency typedDependency : td) {
			mentionIDs = getMentionIds(typedDependency.toString());
			for(String id: mentionIDs){
				if(mentionTable.keySet().contains(id))
					continue;
				mentionTable.put(id, getMentionIdRelatedMentions(id, td));
			}
		}
		return mentionTable;
	}
	
	/**
	   * This method is used to get all mentions of a specific mentionIDd.
	   * it iterate over the type dependencies
	   * if a type dependency contains the mentionId then add it as a related mentions
	   * @param td This is the type dependency of a sentence
	   * @return ArrayList<String> this return contains all related mentions.
	   */
	private static ArrayList<String> getMentionIdRelatedMentions(String menId, Collection<TypedDependency> td) {
		ArrayList<String> relatedMentions = new ArrayList<String>();
		for (TypedDependency typedDependency : td) {
			if(typedDependency.toString().contains(menId))
				relatedMentions.add(typedDependency.toString());
		}
		return relatedMentions;
	}

	/**
	   * This method is used to get mentionIds of a mention.
	   * it remove any spaces before splitting as a preprocessing filter to return the mentionId only
	   * isolate mentionIds from the mention
	   * finally split them
	   * @param mention This is the mention to be processed
	   * @return String[] this return contains two mentionIds.
	   */
	private static ArrayList<String> getMentionIds(String mention) {  
		
	    ArrayList<String> arguments = Parser.getPredicateTerms(mention);
	    arguments.remove(0);
	    return arguments;
	}
	
	/**
	   * This method is used to convert typed dependencies of a sentences to arrayList.
	   * @param td This is the type dependency of a sentence
	   * @return ArrayList<String> this return contains all mentions as strings.
	   */
	public static ArrayList<String> getArraylistOfMetions (Collection<TypedDependency> td){
		ArrayList<String> mentions = new ArrayList<String>();
		for (TypedDependency typedDependency : td)
			mentions.add(typedDependency.toString());
		return mentions;
		
	}
	

}
