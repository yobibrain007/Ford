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


public class Dependency {

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
	
	private static List<? extends HasWord> prepareString(String text){
		return SentenceUtils.toWordList(text.split(" "));
	}
    
	public static HashMap<String, ArrayList<String>> createMentionTable(Collection<TypedDependency> td){
		
		HashMap<String, ArrayList<String>> mentionTable = new HashMap<String, ArrayList<String>>();
		String[] mentionIDs;
		for (TypedDependency typedDependency : td) {
			mentionIDs = parsePredicate(typedDependency.toString());
			for(int i=0; i < 2; i++){
				if(mentionTable.keySet().contains(mentionIDs[i]))
					continue;
				mentionTable.put(mentionIDs[i], getMentionIdRelatedRelations(mentionIDs[i], td));
			}
		}
		return mentionTable;
	}
	
	private static ArrayList<String> getMentionIdRelatedRelations(String menId, Collection<TypedDependency> td) {
		ArrayList<String> relatedRelations = new ArrayList<String>();
		for (TypedDependency typedDependency : td) {
			if(typedDependency.toString().contains(menId))
				relatedRelations.add(typedDependency.toString());
		}
		return relatedRelations;
	}

	private static String[] parsePredicate(String predicate) {  
		
	    String [] terms, arguments;
	   // String pattern = "(\\s+)|(\\()|([(?=\\?)\\s+,?]+)|(\\))";
	   // terms = Predicate.split(pattern);
	    predicate = predicate.replace(" ", "");
	    terms = predicate.split("\\(");

	    //spit arguments
	    terms[1] = terms[1].replace(")","");
	    arguments = terms[1].split(",");
	    
		return arguments;
	}
	
	public static ArrayList<String> getArraylistOfRelations (Collection<TypedDependency> td){
		ArrayList<String> relations = new ArrayList<String>();
		for (TypedDependency typedDependency : td)
			relations.add(typedDependency.toString());
		return relations;
		
	}
	

}
