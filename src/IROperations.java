import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;


/**
 * This class consists of static methods that operate on IR table 
 */

public class IROperations{

	/**
	   * This method is used to print IR entries.
	   * the method print the entry iff it has any other tags regardless stemmer.
	   * @param IR This is the word to be processed.
	   */
	public static void printIR(HashMap<String, String> IR){
		System.out.println("---IR Table---");
		System.out.println("--Terms--");
		for(Entry<String, String> e: IR.entrySet())
			if(e.getValue().contains("|"))
				System.out.println(e.getKey() + "   : " + e.getValue());
	}
	
	/**
	   * This method is used to execute relations of the matching output for the IR Entry of the key = mentionId.
	   * the method makes use of executeRelations method for getting the executions of the relations.
	   * Then, it appends the executions of the mentionId by iterating over the whole executions set and comparing each 
	   * execution entry key with the mentionId.
	   * Note that, mentionId = "word" + "-POS" and key = "word".
	   * Finally, the method makes use of adjustIREnrty method for updating the corresponding IR entry.
	   * @param relations This is the relations to be executed
	   * @param IR This is the IR table that would be updated
	   * @param mentionId This is the key of the IR entry that would be updated
	   * @return HashMap<String, ArrayList<String>> This return contains the IR table after update.
	   */
	public static HashMap<String, String> executeRelations(ArrayList<String> relations, HashMap<String, String> IR, String mentionId){
	
		String arg = "", str, property = "", originalEntry;
		ArrayList<String> tempList;
		HashMap<String, ArrayList<String>> executedRel = executeRelations(relations);
		for(Entry<String, ArrayList<String>> e : executedRel.entrySet()){
			if(!mentionId.contains(e.getKey()))
				continue;
			originalEntry = IR.get(mentionId);
			tempList = e.getValue();
			for (int i = 0; i < tempList.size(); i++) {
				str = tempList.get(i);
				if(originalEntry.contains(str))
					continue;
				if(str.contains("="))
					arg = arg + ", " + str;
				else
					property =  " | " + str + property;
			}
			if(!arg.equals(""))
				arg = " | " + arg.substring(1, arg.length()-1);
			IR = adjustIREnrty(mentionId, originalEntry + arg + property, IR);
			
			arg = "";
			property = "";
		}
		
		return IR;
	}

	/**
	   * This method is used to update IR entry with the key = mentionId.
	   * the method remove the old entry and add a new entry with the new value.
	   * @param key This is the key of the IR entry that would be updated
	   * @param newVal This is the new value that would be added
	   * @param IR This is the IR table that would be updated
	   * @return HashMap<String, ArrayList<String>> This return contains the IR table after update.
	   */
	private static HashMap<String, String> adjustIREnrty(String key, String newVal,
			HashMap<String, String> IR) {
		
		IR.remove(key);
		IR.put(key, newVal);
		
		return IR;
	}
	
	/**
	   * This method is used to execute relations of the matching output
	   * first it make use of parseRelation method to get the arguments of the relation
	   * then it make use of executeRelation to try to execute it 
	   * if the relation has execution then add the execution to a specific mentionId based on the execution as entry in the output Map
	   * it replaces the stanford name with the type rule name
	   * @param relations This is the relations to be executed
	   * @return HashMap<String, ArrayList<String>> in this return, each entry contains the execution as value and
	   * a specific mentionId based on the execution as key
	   */
	private static HashMap<String, ArrayList<String>> executeRelations(ArrayList<String> relations){
		HashMap<String, ArrayList<String>> output = new HashMap<String, ArrayList<String>>();
		Entry<String, ArrayList<String>> e;
		ArrayList<String>parsedItems;
		String key;
		for(String rel: relations){
			parsedItems = parseRelation(rel);
			e = executeRelation(parsedItems);
			//have no thing to do 
			if(e == null)
				continue;
			key = e.getKey();
			if(!output.keySet().contains(key))
				output.put(e.getKey(), e.getValue());
			else
				output.get(key).addAll(e.getValue());
		}
		return output;
	}

	/**
	   * This method is used to pars relation to terms
	   * the method remove any spaces then extract the relation name from the relation 
	   * after that it extract the arguments of the relations
	   * finally convert the array of terms to array list
	   * @param rel This is the relations to be parsed
	   * @return ArrayList<String>  this return contains the relation name at index = 0 followed by the relation arguments
	   */
	private static ArrayList<String> parseRelation(String rel) {
		//the relation pattern relName(arg1,arg2,arg3,..)
		
		ArrayList<String> out = new ArrayList<String>();
		 String [] terms;
		 rel = rel.replace(" ", "");
		 terms = rel.split("\\(");
		 //add relation name
		 out.add(terms[0]);
		 //spit arguments
		 terms[1] = terms[1].replace(")","");
		 terms = terms[1].split(",");
		 
		 for(String s: terms)
			 out.add(s);
		return out;
	}

	/**
	   * This method is used to execute relation based on the relation name
	   * each relation name has its own execution 
	   * in each relation we add the execution "string contain information" to array list 
	   * then add a specific menttionId based on the relation name as a key in the output map accompanied by the execution
	   * @param parsedTerms This is the relations to be parsed terms
	   * @return Entry<String, ArrayList<String>>  in this return, each entry contains the execution as value and
	   * a specific mentionId based on the execution as key
	   */
	private static Entry<String, ArrayList<String>> executeRelation(ArrayList<String> parsedTerms) {
		Entry<String, ArrayList<String>> e;
		ArrayList<String> arrList = new ArrayList<String>();
		switch(parsedTerms.get(0)){
			case "rel":
				//rel(const, ?g, ?d) ....    execution ==> const = ?d .... mentionId = ?g
				arrList.add(parsedTerms.get(1)+ "= "+ parsedTerms.get(3));
				e = new AbstractMap.SimpleEntry<String, ArrayList<String>>(parsedTerms.get(2), arrList);
				break;
			case "trel":
				//trel(const, ?g, ?d) ....    execution ==> T+ const = ?d .... mentionId = ?g
				arrList.add("T" +parsedTerms.get(1)+ "= "+ parsedTerms.get(3));
				e = new AbstractMap.SimpleEntry<String, ArrayList<String>>(parsedTerms.get(2), arrList);
				break;
			case "implies":
				//implies(?d,?g) ....    execution ==> implied by= [?d] .... mentionId = ?g
				arrList.add("implied by: ["+ parsedTerms.get(1) +"]");
				e = new AbstractMap.SimpleEntry<String, ArrayList<String>>(parsedTerms.get(2), arrList);
				break;
			case "unique":
				//unique(?g) ....    execution ==> entity | unique .... mentionId = ?g
				arrList.add("entity");
				arrList.add("unique");
				e = new AbstractMap.SimpleEntry<String, ArrayList<String>>(parsedTerms.get(1), arrList);
				break;
			case "exists":
				// exists(?g) ....    execution ==>  exist .... mentionId = ?g
				arrList.add("exist");
				e = new AbstractMap.SimpleEntry<String, ArrayList<String>>(parsedTerms.get(1), arrList);
				break;
			case "forall":
				// forall(?g) ....    execution ==>  all .... mentionId = ?g
				arrList.add("all");
				e = new AbstractMap.SimpleEntry<String, ArrayList<String>>(parsedTerms.get(1), arrList);
				break;
			case "and_":
				//and(?d,?g) ....    execution ==> and:[?g].... mentionId = ?d
				arrList.add("and: ["+ parsedTerms.get(2) +"]");
				e = new AbstractMap.SimpleEntry<String, ArrayList<String>>(parsedTerms.get(1), arrList);
				break;
			case "or_":
				//or(?d,?g) ....    execution ==> and:[?g].... mentionId = ?d
				arrList.add("or: ["+ parsedTerms.get(2) +"]");
				e = new AbstractMap.SimpleEntry<String, ArrayList<String>>(parsedTerms.get(1), arrList);
				break;
			case "not_":
				//not(?g) ....    execution ==> neg .... mentionId = ?g
				arrList.add("neg");
				e = new AbstractMap.SimpleEntry<String, ArrayList<String>>(parsedTerms.get(1), arrList);
				break;
			case "tattr":
				//tattr(const, ?g) ....    execution ==> T+const .... mentionId = ?g
				arrList.add("T" + parsedTerms.get(1));
				e = new AbstractMap.SimpleEntry<String, ArrayList<String>>(parsedTerms.get(2), arrList);
				break;
			case "bigram":
				//bigram(?d,?g) ....    execution ==> [?d] .... mentionId = ?g
				arrList.add("["+ parsedTerms.get(1) +"]");
				e = new AbstractMap.SimpleEntry<String, ArrayList<String>>(parsedTerms.get(2), arrList);
				break;
			case "attr":
				//attr(?g,?d) I have no Idea how to transfer
			case "top":
				//do no thing
			default:
				e = null;
				break;
		}
		return  e;
	}
}
