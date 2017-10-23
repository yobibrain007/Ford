import java.util.ArrayList;

/**
 * This class consists of static methods that parse Strings to a meaningful data
 */

public class Parser {
	
	
	/**
	   * This method is used to parse Strings contains rules to objects of Rule.
	   * the method iterate over strings contains rules and neglect the both of commented rules and spare lines.
	   * it make use of the filterRule method to filter string before parsing it.
	   * then it make use of parseRule method to parse it.
	   * @param flattenRules This is the rule set of type String to be processed
	   * @return ArrayList<Rule> this return contains the Rules that are parsed.
	   */
	public static ArrayList<Rule> Parse(ArrayList<String> flattenRules){
		ArrayList<Rule> ruleSet = new ArrayList<Rule>();
		Rule r;
		String filteredRule = "";
		for (String rule : flattenRules) {
			if(rule.contains("%")|| rule.equals(""))
				continue;
			filteredRule = filterRule(rule);
			r = parseRule(filteredRule);
			ruleSet.add(r);
		}
		
		return ruleSet;
	}

	/**
	   * This method is used to remove any space in the string before parsing.
	   * @param text This is the string to be processed
	   * @return String this return contains the filtered string.
	   */
	private static String filterRule(String text) {
		return text.replace(" ", "");
	}

	/**
	   * This method is used to parse one string to object of type rule.
	   * it decompose the string to parts: LHS and RHS.
	   * the method make use of parseLHS and parsePredicate for parsing LHS and RHS respectively.
	   * LHS contains set of Predicates, however, the RHS contains only one predicate
	   * @param rule This is the string to be processed
	   * @return Rule this return contains the parsed string.
	   */
	private static Rule parseRule(String rule) {
		//0_index represent LHS "conditions"
		//1_index represent RHS "relation"
		String []ruleSides;
		ruleSides = rule.split(":");
		ArrayList<Predicate> condList = parseLHS(ruleSides[0]);
		Predicate relation = parsePredicate(ruleSides[1]);
		Rule r = new Rule(condList, relation);
		return r;
	}

	/**
	   * This method is used to parse LHS of a rule.
	   * it decompose the string to set of predicate and iterate over them.
	   * the method make use of parsePredicate for parsing each predicate.
	   * @param lHS This is the string to be processed
	   * @return ArrayList<Predicate> this return contains the parsed string.
	   */
	private static ArrayList<Predicate> parseLHS(String lHS) {
		ArrayList<Predicate> parsedConditions = new ArrayList<Predicate>();
		String []conditions;
		conditions = lHS.split("&");
		for(int i = 0; i < conditions.length; i++)
			parsedConditions.add(parsePredicate(conditions[i]));
		
		return parsedConditions;
	}
	
	/**
	   * This method is used to parse one predicate.
	   * the method make use of getPredicateTerms method to get the name of the predicate and the arguments.
	   * it also defines whether the argument is constant or variable.
	   * finally, it creates object of type Rule and initialize it with the obtained data
	   * @param predicate This is the string to be processed
	   * @return Predicate this return contains the parsed string.
	   */
	private static Predicate parsePredicate(String predicate) {  
	    ArrayList<String> terms = getPredicateTerms(predicate);
	    
	    //add predicate name
	    Predicate p = new Predicate(terms.get(0));
	    
	    //add predicate arguments
	    for(int i = 1; i < terms.size(); i++){
	    	if(terms.get(i).contains("?")) //variable argument
	    		p.addArgument(new Argument(false, null));
	    	else//constant argument
	    		p.addArgument(new Argument(true, terms.get(i)));
	    }
		return p;
	}
	
	/**
	   * This method is used to get the name of the predicate and the arguments.
	   * the method make use of filterRule method for filtering it before processing.
	   * then extract the predicate name. 
	   * after that it extract the arguments of the predicate.
	   * finally convert the array of terms to array list.
	   * @param predicate This is the predicate to be parsed
	   * @return ArrayList<String>  this return contains the predicate name at index = 0 followed by the predicate arguments
	   */
	public static ArrayList<String> getPredicateTerms(String predicate){
		 String [] arguments;
		 ArrayList<String> terms = new ArrayList<String>();
		 predicate = filterRule(predicate);
		 arguments = predicate.split("\\(");
		 
		 terms.add(arguments[0]);
		 
		 arguments[1] = arguments[1].replace(")","");
		 arguments = arguments[1].split(",");
		 
		 for(String s: arguments)
			 terms.add(s);
		 
		 return terms;
	}

	
	
}
