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


public class IRGenerationAlgo {

	
	public static void main(String[] args) throws IOException{
	
		//this list would contain additional relations to the type dependency for matching (e.g. event(?g), entity(?g), pred(?g) )
		ArrayList<String> additionalCondList = new ArrayList<String>();
		
		Collection<TypedDependency> tdl;
		HashMap<String, ArrayList<String>> mentionTable;
		HashMap<String, POS> posTags;
		HashMap<String, String> IR;
		ArrayList<String> allRelations;
		
		Manager mng = new Manager();
		
		ArrayList<String> requirementList = mng.readFile("bin/requirements.txt");
		requirementList = mng.doPreProcessing(requirementList);
		
		//rule set to be parsed "for testing only"
		//ArrayList<String> plainRules = mng.readFile("bin/NLP.txt");
		//mng.parseRules(plainRules);
	
		
		String word, stem = "", entryDetails, matchingOutput;
		POS pos;
		
		for(String requirement: requirementList){
				
			//step:1
			tdl = mng.getTypeDependency(requirement);
			//System.out.println(tdl);
			
			//step:2
			//creating up Mention Table
			mentionTable = mng.createMentionTable(tdl);
			//get POS of the requirement
			posTags = mng.getPOS(requirement);
			
			//step:3
			IR = new HashMap<String, String>();
			
			//Step:4
			for (String mentionId : mentionTable.keySet()) {
				//get the word without its position
				word = mentionId.substring(0, mentionId.indexOf("-"));
				
				//(a)Get the POS tag for the MentionId
				pos = posTags.get(word);

				//(b) Set word to the stem of the word, using the WordNet stemmer, and add an IR entry for word.
				if(pos == null)
					//means that the word has no stemmer in the WordNet
					entryDetails = word;
				else{
					stem = mng.getStemmer(word, pos);
					entryDetails = stem;
				}
				
				//TODO (d)
				//TODO (e)
				//TODO (f)
				
				//(c) If word is a math expression encoded by the math preprocessor, set its IR type to arithmetic.
				if(word.contains("zzarithzz_"))
					entryDetails += " | arithmetic";
				
				//(g) Else if word has a noun POS tag, set its IR type to entity. In addition, if the
				//word is not found in WordNet, set its quantifier to unique (as it is presumably a proper name).
				if(pos != null && pos == POS.NOUN){
					entryDetails += " | entity";
					additionalCondList.add("entity("+word+")");
					if(stem == null)
						entryDetails += " | unique";
				}
				
				//(h) Else if word has a verb POS tag, set its type to event.
				else if(pos != null && pos == POS.VERB){
					entryDetails += " | event";
					additionalCondList.add("event("+word+")");
				}
				
				IR.put(mentionId, entryDetails);
			}
			
			
			//Step:5 Execute the type rules. 	
			allRelations = mng.getAllRelationsMerged(mng.getArraylistOfRelations(tdl), additionalCondList);
			for (String mentionId : mentionTable.keySet()){
				
				//(a) Match the type rule with the TD, producing TD.
				matchingOutput = mng.getMatchedRelationsByTypeRules(mentionTable.get(mentionId), allRelations);
				System.out.println(matchingOutput);
				
				//TODO (b)
			}
			
			System.out.println("done");
		}

	}
}
