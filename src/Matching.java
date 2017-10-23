import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import org.jpl7.Query;
import org.jpl7.Term;

/**
 * This class consists of static methods that execute matching part on SWI prolog
 */

public class Matching {

	/**
	   * This method is used to initialize Prolog Environment
	   * by loading the prolog file
	   */
	public static void intialize(){
		Query q = new Query("consult('src/match.pl')");
		System.out.println((q.hasSolution() ? "succeeded" : "failed"));	
	}
	
	/**
	   * This method is used to get matched relations of a specific mentionID by type Rules.
	   * the method make the use of normalizeArrayListToPrologListFormat to prepare the query that will be executed on prolog
	   * then it get the solution of the query and convert it to array list 
	   * @param mentionIdRelatedMentions This is the mentions of a specific mentionId
	   * @param allMentions This is the mentions of the whole sentence
	   * @return ArrayList<String> this return contains the matched relations by the type rules
	   */
	public static ArrayList<String> getMatchedRelationsByTypeRules(ArrayList<String> mentionIdRelatedMentions, ArrayList<String> allMentions){
		String query = "findMatchedMentions("+ normalizeArrayListToPrologListFormat(mentionIdRelatedMentions) + ", "+ normalizeArrayListToPrologListFormat(allMentions) + ",R)";
		Query q = new Query(query);
		String queryOutpu = q.oneSolution().get("R").toString();
		//query has no solution
		if(queryOutpu.equals("'[]'"))
			return null;
		return convertQueryOutputToarrayList(queryOutpu);
	}
	
	/**
	   * This method is used to convert the prolog output string to arraylist.
	   * it makes use of preProcessQueryOutput method to
	   * preprocess the query before converting relations from string to arraylist
	   * then it split the string output into array list 
	   * @param out This is the prolog output to be processed
	   * @return ArrayList<String> this return contains the relations
	   */
	private static ArrayList<String> convertQueryOutputToarrayList(String out) {
		String filteredOut = preProcessQueryOutput(out);
		String [] relations = filteredOut.split("%");
		return  new ArrayList<String>(Arrays.asList(relations));
	}

	/**
	   * This method is used to do preprocessing on the prolog output string.
	   * it first remove the not needed information from the string 
	   * the it replace the splitter character "," of the relations with "%" 
	   * to differentiate it from each relation argument splitter "," 
	   * this would ensure a correct split later for the relations
	   * @param out This is the prolog output to be processed
	   * @return String this return contains the processed string
	   */
	private static String preProcessQueryOutput(String out) {
		
		out = out.replace("'[|]'(", "");
		int index = out.indexOf(", '[]'");
		out = out.replace("),", ")%");
		out = out.substring(0, index);
		return out;
	}

	/**
	   * This method is used to iterate over a set of mentions to adjust and concatenate them
	   * by separating them with "," to put them on the prolog list format [mention,mention,...,mention]
	   * @param list This is array list of mentions
	   * @return String this return contains the normalized prolog list
	   */
	private static String normalizeArrayListToPrologListFormat(ArrayList<String> list){
		String normalizedList = "[";
		for (String s : list) {
			normalizedList += adjustMention(s);;
			normalizedList += ",";
		}
		//removing the last comma
		normalizedList = normalizedList.substring(0, normalizedList.length() - 1);
		normalizedList +="]";
		return normalizedList;
	}
	
	/**
	   * This method is used to remove the position of the word from each mentionId
	   * mentionId format is word-position so we get the index of the "-" to remove the position
	   * adjust mention name if needed
	   * @param mention This is the mention to be adjusted
	   * @return String this return contains the adjusted mention
	   */
	private static String adjustMention(String mention) {
		String temp;
		int firstDashIndex, lastDashindex, commaIndex;
		
		if(mention.contains("event") || mention.contains("entity") || mention.contains("pred"))
			return mention;
		
		firstDashIndex = mention.indexOf("-");
		lastDashindex = mention.lastIndexOf("-");
		commaIndex = mention.indexOf(",");
		temp = mention.substring(0, firstDashIndex) + mention.substring(commaIndex, lastDashindex) +")";
		temp = adjustMentionNameManually(temp);
		return temp;
	}

	/**
	   * This method is used to adjust the conflict between the returned name from stanford and the 
	   * exist name in the type rule
	   * it replaces the stanford name with the type rule name
	   * @param mention This is the mention to be adjusted
	   * @return String this return contains the adjusted mention
	   */
	private static String adjustMentionNameManually(String mention) {
		mention = mention.replace(":", "_");
		mention = mention.replace("nmod_of(", "prep_of(");
		mention = mention.replace("advcl_if(","advcl(");
		mention = mention.replace("advcl_to(","prep_to(");
		mention = mention.replace("if)", "if_)");
		mention = mention.replace("root(","roott(");
		return mention;
	}

	
}
