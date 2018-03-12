import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

















import java.util.Map;

import org.jpl7.*;

import edu.mit.jwi.*;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;
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


public class SystemMain {

	static final String REQ_FILE_PATH = "src/requirements.txt";
	static final String SAL_MODEL_FILE_PATH = "src/SALModel.txt";
	static final String STATE_VARIABLES_FILE_PATH = "src/statVars.txt";

	public static void main(String[] args) throws IOException{
		
		
		//object of the manager class to enable the interaction between this class and the rest of classes
		Controller ctl = new Controller();
		
		HashMap<String, String> IR ;
		
		ArrayList<String> transformedSALRules = new ArrayList<String>();
		
		//read the requirement File
		ArrayList<String> requirementList = ctl.readFile(REQ_FILE_PATH);
		//apply pre-processing preparations
		requirementList = ctl.doPreProcessing(requirementList);
		
		//rule set to be parsed "for testing only"
		//ArrayList<String> plainRules = mng.readFile("src/NLP.txt");
		//ArrayList<Rule> parsedRules = mng.parseRules(plainRules);
	
		String rule;
		for(String requirement: requirementList){
		
			System.out.println("================================================================");
			System.out.println("-Req is {" + requirement + "}");
			TemplateChecking.TemplateType type = TemplateChecking.checkTemplate(requirement);
			System.out.println("-Req Template Type = " + type);
			
			if(!type.equals(TemplateChecking.TemplateType.ArsenalTemplate))
				continue;
			IR = ctl.generateIRtable(requirement);
			//print the IR table of this requirement statement
			ctl.printIR(IR);
			
			rule = ctl.convertIRToSALRule(IR);
			transformedSALRules.add(rule);
			
			System.out.println("---------- SAL-GENERATION ---------");
			
			System.out.println(rule);
			
		}
		
		transformedSALRules = ctl.readFile("src/temp.txt");
		ArrayList<String> statVars = ctl.readFile(STATE_VARIABLES_FILE_PATH);
		
		ArrayList<String> model = ctl.generateSALModel(transformedSALRules, statVars);
		ctl.writeToFile(SAL_MODEL_FILE_PATH, model);
		
		System.out.println("SAL model is constructed");
	}
}
