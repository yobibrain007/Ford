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


public class Manager {

	private Parser ruleParser;
	
	
	public Manager() throws IOException{
		ruleParser = new Parser();
		Matching.intialize();
		WordNet.intialize();
		POSTransoformation.intialize();
		StanfordPOS.intialize();
	}
	
	public Parser parseRules(ArrayList<String> plainRules){
		ruleParser.setFlattenRules(plainRules);
		ruleParser.Parse();
		return ruleParser;
	}
	
	public ArrayList<String> doPreProcessing(ArrayList<String> text){
		ArrayList<String> processedTexet = PreProcesing.parseDomainExpressions(text);
		return PreProcesing.parseArithematicExpressions(processedTexet);
	}
	
	public Collection<TypedDependency> getTypeDependency (String rule){
		return Dependency.getTypeDependency(rule);
	}

	public HashMap<String, ArrayList<String>> createMentionTable(Collection<TypedDependency> td){
		
		return Dependency.createMentionTable(td);
	}
	
	public static ArrayList<String> getArraylistOfRelations (Collection<TypedDependency> td){
		return Dependency.getArraylistOfRelations(td);
	}
	
	public String getMatchedRelationsByTypeRules(ArrayList<String> mentionIdRelatedRelations, ArrayList<String> allRelations){
		return Matching.getMatchedRelationsByTypeRules(mentionIdRelatedRelations, allRelations);
	}
	
	public ArrayList<String> getAllRelationsMerged(ArrayList<String> tdRelations,
			ArrayList<String> additionalCondList) {
		tdRelations.addAll(additionalCondList);
		return tdRelations;
	}

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
	
	public void writeToFile(String filePath, ArrayList<String> l) throws IOException{
		
		PrintWriter pw = null;
		
		pw = new PrintWriter ( new BufferedWriter ( new FileWriter(filePath) )) ;
		for(int i = 0; i < l.size(); i++)
				pw.println(l.get(i));
	    pw.close();
	}
	
	public HashMap<String, POS> getPOS(String text){
		return StanfordPOS.getPOS(text);
	}
	
	public String getStemmer(String word, POS p){
		return WordNet.getStemmer(word, p);
	}
}
