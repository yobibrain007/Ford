import java.util.ArrayList;


public class Parser {

	ArrayList<Rule> ruleSet;
	ArrayList<String> flattenRules;
	public Parser(ArrayList<String> r){
		ruleSet = new ArrayList<Rule>();
		flattenRules = r;
	}
	
	public Parser(){
		ruleSet = new ArrayList<Rule>();
	}
	
	public void setFlattenRules(ArrayList<String> r){
		flattenRules = r;
	}
	
	public void Parse(){
		
		Rule r;
		String filteredRule = "";
		for (String rule : flattenRules) {
			if(rule.contains("%")|| rule.equals(""))
				continue;
			filteredRule = filterRule(rule);
			r = parseRule(filteredRule);
			ruleSet.add(r);
		}
	}

	private String filterRule(String rule) {
		return rule.replace(" ", "");
	}

	private Rule parseRule(String rule) {
		//0_index represent LHS "conditions"
		//1_index represent RHS "relation"
		String []ruleSides;
		ruleSides = rule.split(":");
		ArrayList<Predicate> condList = parseLHS(ruleSides[0]);
		Predicate relation = parsePredicate(ruleSides[1]);
		Rule r = new Rule(condList, relation);
		return r;
	}

	private ArrayList<Predicate> parseLHS(String lHS) {
		ArrayList<Predicate> parsedConditions = new ArrayList<Predicate>();
		String []conditions;
		conditions = lHS.split("&");
		for(int i = 0; i < conditions.length; i++)
			parsedConditions.add(parsePredicate(conditions[i]));
		
		return parsedConditions;
	}
	
	private Predicate parsePredicate(String Predicate) {  
	    String [] terms, arguments;
	   // String pattern = "(\\s+)|(\\()|([(?=\\?)\\s+,?]+)|(\\))";
	   // terms = Predicate.split(pattern);
	    terms = Predicate.split("\\(");
	    //add predicate name
	    Predicate p = new Predicate(terms[0]);
	    //spit arguments
	    terms[1] = terms[1].replace(")","");
	    arguments = terms[1].split(",");
	    //add predicate arguments
	    for(int i = 0; i < arguments.length; i++){
	    	if(arguments[i].contains("?")) //variable argument
	    		p.addArgument(new Argument(false, null));
	    	else//constant argument
	    		p.addArgument(new Argument(true, arguments[i]));
	    }
		return p;
	}

	
	
}
