import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import edu.mit.jwi.item.POS;
import edu.stanford.nlp.trees.TypedDependency;


public class Controller {

	
	/**
	   * This method is used to initialize all the needed Environments 
	   * (e.g., WordNEt, Prolog, Stanford and Ontology)
	   */
	public Controller() throws IOException{
		Matching.intialize();
		WordNet.intialize();
		POSTransoformation.intialize();
		StanfordPOS.intialize();
		Ontology.intialize();
		GrammerMatching.intialize();
	}
	
	/**
	   * This method is used to parse Strings contains rules to objects of Rule.
	   * @param plainRules This is the rule set of type String to be processed
	   * @return ArrayList<Rule> this return contains the Rules that are parsed.
	   */
	public ArrayList<Rule> parseRules(ArrayList<String> plainRules){
		return Parser.Parse(plainRules);
	}
	
	/**
	   * This method is used to parse arithmetic expressions as well as domain expressions. 
	   * @param requirements this is the requirement set to be processed
	   * @return ArrayList<String> this return contains the requirement set after processing
	   */
	public ArrayList<String> doPreProcessing(ArrayList<String> requirements){
		return Manager.doPreProcessing(requirements);
	}
	
	public HashMap<String, String> generateIRtable(String requirement){
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
		String word, stem = "", entryDetails, ontologyres, cond;
		ArrayList<String> matchingOutput;
		POS pos;
		
		//step:1
		tdl = Manager.getTypeDependency(requirement);
		//System.out.println(tdl);
			
		//step:2
		//creating up Mention Table
		mentionTable = Manager.createMentionTable(tdl);
		//get POS of the requirement
		posTags = Manager.getPOS(requirement);
			
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
				stem = Manager.getStemmer(word, pos);
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
					cond = Parser.adjustMentionIdPosition("entity("+mentionId+")");
					additionalCondList.add(cond);
				}
				
				//(e) Else if word is marked as predicate in the ontology, set its IR type to pred.
				else if(ontologyres.equals("predicate")){
					entryDetails += " | predicate";
					cond = Parser.adjustMentionIdPosition("pred("+mentionId+")");
					additionalCondList.add(cond);
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
				cond = Parser.adjustMentionIdPosition("entity("+mentionId+")");
				additionalCondList.add(cond);
				if(stem == null)
					entryDetails += " | unique";
			}
				
			//(h) Else if word has a verb POS tag, set its type to event.
    		else if(pos != null && pos == POS.VERB){
				entryDetails += " | event";
				cond = Parser.adjustMentionIdPosition("event("+mentionId+")");
				additionalCondList.add(cond);
			}
				
			IR.put(mentionId, entryDetails);
		}
			
			
		//Step:5 Execute the type rules. 	
		allRelations = Manager.getAllMentionsMerged(Manager.getArraylistOfMetions(tdl), additionalCondList);
		//mng.printIR(IR);
		for (String mentionId : mentionTable.keySet()){
				
			//(a) Match the type rule with the TD, producing TD.
			matchingOutput = Manager.getMatchedRelationsByTypeRules(mentionTable.get(mentionId), allRelations);
			//System.out.println(matchingOutput);
				
			//(b) If step 5(a) was successful, execute the right-hand-side of TD
			if(matchingOutput != null)
				IR = Manager.executeRelations(matchingOutput, IR, mentionId);
		}
		
		
		return IR;
	}
	
	public ArrayList<String> generateSALModel(ArrayList<String> transformedSALRules, ArrayList<String> statVars){
		
		ArrayList<String> model;
		
		model = SALTransformation.generateSALModel(statVars, transformedSALRules);
		
		return model;
	}
	
	/**
	   * This method is used to print IR entries.
	   * @param IR This is the word to be processed.
	   */
	public void printIR(HashMap<String, String> IR){
		Manager.printIR(IR);
	}
	
	public String convertIRToSALRule(HashMap<String, String> IR){
		
		String root = Manager.getIRRoot(IR);
		//System.out.println(root);
		return SALTransformation.transformFunction(root, IR).replace("zz", "");
	}
	
	/**
	   * This method is used to read lines of file and store it in ArrayLis of strings.
	   * @param filePath This is the file path of the file to be read.
	   * @return ArrayList<String> this return contains the read lines.
	   */
	public ArrayList<String> readFile(String filePath) throws IOException{
		String readString = null;
		BufferedReader br = null;
		ArrayList<String> text = new ArrayList<String>();
		try{
	    br = new BufferedReader( new FileReader(filePath) ) ;
	    while((readString = br.readLine())!= null)
	    	text.add(readString.toLowerCase());
		}
		catch(FileNotFoundException e){
			br.close();
			return null;
		}
	    br.close();
		return text;
	}
	
	/**
	   * This method is used to write strings to file.
	   * @param filePath This is the file path of the file to be read.
	   * @param l This is the list of strings that would be written
	   * @return ArrayList<String> this return contains the read lines.
	   */
	public void writeToFile(String filePath, ArrayList<String> l) throws IOException{
		
		PrintWriter pw = null;
		
		pw = new PrintWriter ( new BufferedWriter ( new FileWriter(filePath) )) ;
		for(int i = 0; i < l.size(); i++)
				pw.println(l.get(i));
	    pw.close();
	}
	
	}