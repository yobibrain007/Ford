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
import java.util.Map.Entry;

import edu.mit.jwi.item.POS;
import edu.stanford.nlp.trees.TypedDependency;

/**
 * This class is a layer to allow the IRGenerationAlgo communicate with the rest of classes through it 
 */

public class Manager {

	private ArrayList<Rule> parsedRules;
	
	
	/**
	   * This method is used to initialize all the needed Environments 
	   * (e.g., WordNEt, Prolog, Stanford and Ontology)
	   */
	public Manager() throws IOException{
		Matching.intialize();
		WordNet.intialize();
		POSTransoformation.intialize();
		StanfordPOS.intialize();
		Ontology.intialize();
	}
	
	/**
	   * This method is used to parse Strings contains rules to objects of Rule.
	   * @param plainRules This is the rule set of type String to be processed
	   * @return ArrayList<Rule> this return contains the Rules that are parsed.
	   */
	public ArrayList<Rule> parseRules(ArrayList<String> plainRules){
		parsedRules = Parser.Parse(plainRules);
		return parsedRules;
	}
	
	/**
	   * This method is used to parse arithmetic expressions as well as domain expressions. 
	   * @param requirements this is the requirement set to be processed
	   * @return ArrayList<String> this return contains the requirement set after processing
	   */
	public ArrayList<String> doPreProcessing(ArrayList<String> requirements){
		ArrayList<String> processedRequirements = PreProcesing.parseDomainExpressions(requirements);
		return PreProcesing.parseArithematicExpressions(processedRequirements);
	}
	
	/**
	   * This method is used to getting the typed dependency of a sentence.
	   * @param req This is the sentence to be processed
	   * @return Collection<TypedDependency> this return contains the typed dependency "mentions".
	   */
	public Collection<TypedDependency> getTypeDependency (String req){
		return Dependency.getTypeDependency(req);
	}

	/**
	   * This method is used to create mention table from typed dependency.
	   * @param td This is the type dependency of a sentence
	   * @return HashMap<String, ArrayList<String>> this return contains in each entry 
	   * mentionId as a key and its related mentions as value
	   */
	public HashMap<String, ArrayList<String>> createMentionTable(Collection<TypedDependency> td){
		
		return Dependency.createMentionTable(td);
	}
	
	/**
	   * This method is used to convert typed dependencies of a sentences to ArrayList.
	   * @param td This is the type dependency of a sentence
	   * @return ArrayList<String> this return contains all mentions as strings.
	   */
	public static ArrayList<String> getArraylistOfMetions (Collection<TypedDependency> td){
		return Dependency.getArraylistOfMetions(td);
	}
	
	/**
	   * This method is used to get matched relations of a specific mentionID by type Rules.
	   * @param mentionIdRelatedMentions This is the mentions of a specific mentionId
	   * @param allMentions This is the mentions of the whole sentence
	   * @return ArrayList<String> this return contains the matched relations by the type rules
	   */
	public ArrayList<String> getMatchedRelationsByTypeRules(ArrayList<String> mentionIdRelatedRelations, ArrayList<String> allRelations){
		return Matching.getMatchedRelationsByTypeRules(mentionIdRelatedRelations, allRelations);
	}
	
	/**
	   * This method is used to merge to ArrayLists into only one ArrayList.
	   * @param list_1 This is the first list to be merged
	   * @param list_2 This is the second list to be merged
	   * @return ArrayList<String> this return contains the read lines.
	   */
	public ArrayList<String> getAllMentionsMerged(ArrayList<String> list_1,
			ArrayList<String> list_2) {
		list_1.addAll(list_2);
		return list_1;
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
	
	/**
	   * This method is used to getting the POS of a sentence.
	   * @param text This is sentence to be processed
	   * @return HashMap this return contains in each entry a word of the sentences as a key
	   * accompanied by its POS as entry.
	   */
	public HashMap<String, POS> getPOS(String text){
		return StanfordPOS.getPOS(text);
	}
	
	/**
	   * This method is used to getting the stemmer of a word.
	   * @param word This is the word to be processed
	   * @param p This is the tagger of the word (e.g. noun, verb, adj and adv) 
	   * @return String this return contains the word stemmer if it exited.
	   */
	public String getStemmer(String word, POS p){
		return WordNet.getStemmer(word, p);
	}
	
	/**
	   * This method is used to execute relations of the matching output for the IR Entry of the key = mentionId.
	   * @param relations This is the relations to be executed
	   * @param IR This is the IR table that would be updated
	   * @param mentionId This is the key of the IR entry that would be updated
	   * @return HashMap<String, ArrayList<String>> This return contains the IR table after update.
	   */
	public static HashMap<String, String> executeRelations(ArrayList<String> relations, HashMap<String, String> IR, String mentionId){
		return IROperations.executeRelations(relations, IR, mentionId);
	}
	/**
	   * This method is used to print IR entries.
	   * @param IR This is the word to be processed.
	   */
	public static void printIR(HashMap<String, String> IR){
		IROperations.printIR(IR);
	}

}

