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
		
		//This would contains the type dependency of a requirement 
		Collection<TypedDependency> tdl;
		HashMap<String, ArrayList<String>> mentionTable;
		//Each entry of this HashMap contains the word as a key and its tagger as the value
		HashMap<String, POS> posTags;
		//This hash map contains the IR information where the key set contains the mentionIds
		HashMap<String, String> IR;
		//This list contains all mentions of a requirement
		ArrayList<String> allRelations;
		
		//object of the manager class to enable the interaction between this class and the rest of classes
		Manager mng = new Manager();
		
		//read the requirement File
		ArrayList<String> requirementList = mng.readFile("src/requirements.txt");
		//apply pre-processing preparations
		requirementList = mng.doPreProcessing(requirementList);
		
		//rule set to be parsed "for testing only"
		//ArrayList<String> plainRules = mng.readFile("src/NLP.txt");
		//ArrayList<Rule> parsedRules = mng.parseRules(plainRules);
	
		
		String word, stem = "", entryDetails, ontologyres;
		ArrayList<String> matchingOutput;
		POS pos;
		
		for(String requirement: requirementList){
				
			//step:1
			tdl = mng.getTypeDependency(requirement);
			System.out.println(tdl);
			
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
				//the position starts with "-"
				word = mentionId.substring(0, mentionId.indexOf("-"));
				
				//(a)Get the POS tag for the MentionId
				pos = posTags.get(word);

				//(b) Set word to the stem of the word, using the WordNet stemmer, and add an IR entry for word.
				
				//means that the word has no stemmer in the WordNet
				if(pos == null)
					entryDetails = word;
				else{
					stem = mng.getStemmer(word, pos);
					if(stem != null)
						entryDetails = stem;
					else
						entryDetails = word;
				}
				
				//get the ontology of the word
				ontologyres = Ontology.getOntology(word);
				
				//(c) If word is a math expression encoded by the math preprocessor, set its IR type to arithmetic.
				if(word.contains(PreProcesing.ARITH_PREFIX))
					entryDetails += " | arithmetic";
				
				
				else if(ontologyres != null){
					//(d) Else if word is marked as a unique entity in the ontology, set its IR type to entity and its quantifier to unique
					if(ontologyres.equals("uniqe entity")){
						entryDetails += " | entity";
						additionalCondList.add("entity("+word+")");
					}
				
					//(e) Else if word is marked as predicate in the ontology, set its IR type to pred.
					else if(ontologyres.equals("predicate")){
						entryDetails += " | predicate";
						additionalCondList.add("pred("+word+")");
					}
					
					//(f) Else if word is a number, set its IR type to num
					else if(ontologyres.equals("Number"))
						entryDetails += " | num";
					
					else if(ontologyres.equals("bool"))
						entryDetails += " | bool";
				}
				
				//(g) Else if word has a noun POS tag, set its IR type to entity. In addition, if the
				//word is not found in WordNet, set its quantifier to unique (as it is presumably a proper name).
				else if(pos != null && pos == POS.NOUN){
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
			allRelations = mng.getAllMentionsMerged(mng.getArraylistOfMetions(tdl), additionalCondList);
			//mng.printIR(IR);
			for (String mentionId : mentionTable.keySet()){
				
				//(a) Match the type rule with the TD, producing TD.
				matchingOutput = mng.getMatchedRelationsByTypeRules(mentionTable.get(mentionId), allRelations);
				//System.out.println(matchingOutput);
				
				//(b) If step 5(a) was successful, execute the right-hand-side of TD
				if(matchingOutput != null)
					IR = mng.executeRelations(matchingOutput, IR, mentionId);
			}
			
			//print the IR table of this requirement statement
			mng.printIR(IR);
			
			additionalCondList.clear();
		}

	}
}
