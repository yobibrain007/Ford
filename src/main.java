import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;




import org.jpl7.*;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.logging.Redwood;
import edu.stanford.nlp.ling.SentenceUtils;


public class main {

	
	public static void main(String[] args) throws IOException{
	
		Manager M = new Manager();
		
		//rule set to be parsed "for testing only"
		ArrayList<String> plainRules = M.getInputText("bin/NLP.txt");
		M.parseRules(plainRules);
		
		//this part is to test running the prolog query from java using static Query
		Query q1 = new Query("findMatchedMentions([prep_upon(entering_17, set_4),neg(test, never),advmod(k,when),advcl(k, m)], [prep_upon(entering_17, set_4),neg(test, never),advmod(k,when),advcl(k, m)],R)");
		System.out.print(q1.oneSolution().get("R"));
		
		//rule to get type dependency of it "for testing only"
		String sent = "If the Status_attribute of the Lower_Desired_Temperature or the Upper_Desired_Temperature equals Invalid , the Regulator_Interface_Failure shall be set to True .";
		Collection<TypedDependency> tdl = M.getTypeDependency(sent);
		//System.out.println(tdl);
		
		//creating Mention Table
		HashMap<String, ArrayList<String>> mentionTables = M.CreateMentionTable(tdl);
		
		//find matches only testing on mention Id
	//	String s = M.getMatchedRuleOfTheMentionId(mentionTables.get("Status_attribute-3"), M.getArraylistOfRelations(tdl));
	//	System.out.println(s);

		
		
	}
}
