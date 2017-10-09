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

import edu.stanford.nlp.trees.TypedDependency;


public class Manager {

	private Parser ruleParser;
	
	
	public Manager(){
		ruleParser = new Parser();
		Match.Intialize();
	}
	
	public ArrayList<String> readfile(String path){
	
		return null;
	}
	
	public Parser parseRules(ArrayList<String> plainRules){
		ruleParser.setFlattenRules(plainRules);
		ruleParser.Parse();
		return ruleParser;
	}
	
	public Collection<TypedDependency> getTypeDependency (String rule){
		return DependencyParsing.getTypeDependency(rule);
	}

	public HashMap<String, ArrayList<String>> CreateMentionTable(Collection<TypedDependency> td){
		
		return DependencyParsing.CreateMentionTable(td);
	}
	
	public static ArrayList<String> getArraylistOfRelations (Collection<TypedDependency> td){
		return DependencyParsing.getArraylistOfRelations(td);
	}
	
	public String getMatchedRuleOfTheMentionId(ArrayList<String> MentionIdRelatedRelations, ArrayList<String> AllRelations){
		return Match.getMatchedRuleOfTheMentionId(MentionIdRelatedRelations, AllRelations);
	}
	
	public ArrayList<String> getInputText(String filePath) throws IOException{
		String readString = null;
		BufferedReader br = null;
		ArrayList<String> text = new ArrayList<String>();
		try{
	    br = new BufferedReader( new FileReader(filePath) ) ;
	    while((readString = br.readLine())!= null)
	    	text.add(readString);
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
	
	
	
}
