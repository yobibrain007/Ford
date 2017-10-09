import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.jpl7.fli.term_t;

import edu.stanford.nlp.io.EncodingPrintWriter.out;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;


public class DependencyParsing {
	
	public static Collection<TypedDependency> getTypeDependency(String text){
		LexicalizedParser lp = LexicalizedParser.loadModel(
				"edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz",
				"-maxLength", "80", "-retainTmpSubcategories");
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		// Uncomment the following line to obtain the original Stanford Dependencies
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
    
	public static HashMap<String, ArrayList<String>> CreateMentionTable(Collection<TypedDependency> td){
		
		HashMap<String, ArrayList<String>> MentionTable = new HashMap<String, ArrayList<String>>();
		String[] mentionIDs;
		for (TypedDependency typedDependency : td) {
			mentionIDs = parsePredicate(typedDependency.toString());
			for(int i=0; i < 2; i++){
				if(MentionTable.keySet().contains(mentionIDs[i]))
					continue;
				MentionTable.put(mentionIDs[i], getRelatedRelations(mentionIDs[i], td));
			}
		}
		return MentionTable;
	}
	
	private static ArrayList<String> getRelatedRelations(String menId,
			Collection<TypedDependency> td) {
		ArrayList<String> relatedRelations = new ArrayList<String>();
		for (TypedDependency typedDependency : td) {
			if(typedDependency.toString().contains(menId))
				relatedRelations.add(typedDependency.toString());
		}
		return relatedRelations;
	}

	private static String[] parsePredicate(String Predicate) {  
		
	    String [] terms, arguments;
	   // String pattern = "(\\s+)|(\\()|([(?=\\?)\\s+,?]+)|(\\))";
	   // terms = Predicate.split(pattern);
	    Predicate = Predicate.replace(" ", "");
	    terms = Predicate.split("\\(");

	    //spit arguments
	    terms[1] = terms[1].replace(")","");
	    arguments = terms[1].split(",");
	    
		return arguments;
	}
	
	public static ArrayList<String> getArraylistOfRelations (Collection<TypedDependency> td){
		ArrayList<String> Relations = new ArrayList<String>();
		for (TypedDependency typedDependency : td)
			Relations.add(typedDependency.toString());
		return Relations;
		
	}
	

}
