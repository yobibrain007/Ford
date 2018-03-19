import java.util.ArrayList;

/**
 * This class consists of static methods that do preprocessing operations on the requirements
 */

public class PreProcesing {
	
	public static final String ARITH_PREFIX = "zzarithzz_";
	
	/**
	   * This method is used to parse arithmetic expressions with one long term started with word arith 
	   * and concatenated by "_". 
	   * arithmetic symbols replaced with words  and the expression is concatenated 
	   * finally add the "arith" prefix
	   * @param requirements this is the requirement set to be processed
	   * @return ArrayList<String> this return contains the requirement set after processing
	   */

	public static ArrayList<String> parseArithematicExpressions(ArrayList<String> requirements){
		
		ArrayList<String> parsedText = new ArrayList<String>();
		
		for(String statement: requirements)
		{
			if(statement.equals(""))
				continue;
			//all possible cases of any symbol that may be existed
			statement = statement.replace(" - ", "_zzminuszz_");
			statement = statement.replace("- ", "_zzminuszz_");
			statement = statement.replace(" -", "_zzminuszz_");
			statement = statement.replace("-", "_zzminuszz_");
			
			statement = statement.replace(" + ", "_zzpluszz_");
			statement = statement.replace("+ ", "_zzpluszz_");
			statement = statement.replace(" +", "_zzpluszz_");
			statement = statement.replace("+", "_zzpluszz_");
			
			statement = statement.replace(" * ", "_zzmulzz_");
			statement = statement.replace("* ", "_zzmulzz_");
			statement = statement.replace(" *", "_zzmulzz_");
			statement = statement.replace("*", "_zzmulzz_");
			
			statement = statement.replace(" / ", "_zzdivzz_");
			statement = statement.replace("/ ", "_zzdivzz_");
			statement = statement.replace(" /", "_zzdivzz_");
			statement = statement.replace("/", "_zzdivzz_");
			
			statement = statement.replace("( ", "zzopzz_");
			statement = statement.replace("(", "zzopzz_");
			
			statement = statement.replace(" )", "_zzcpzz");
			statement = statement.replace(")", "_zzcpzz");
			
			statement = statement.replace(" = ", "_zzequalzz_");
			statement = statement.replace("= ", "_zzequalzz_");
			statement = statement.replace(" =", "_zzequalzz_");
			statement = statement.replace("=", "_zzequalzz_");
			
			statement = addPrefix(statement);
			parsedText.add(statement);
		}
		
		return parsedText;
	}

	/**
	   * This method is used to check if a word in the requirement is an arithmetic expression 
	   * then add "zzarithzz_" as a prefix to it 
	   * @param requirement this is the requirement to be processed
	   * @return ArrayList<String> this return contains the requirement after processing
	   */
	private static String addPrefix(String requirement) {
		String adjustedStatement = "";
		String[] words = requirement.split(" ");
	    for (String word: words) {
	    	adjustedStatement += " ";
	    	if(word.contains("_zzminuszz_")|| word.contains("_zzpluszz_") || word.contains("_zzmulzz_") ||
	    			word.contains("_zzdivzz_") || word.contains("zzopzz_") || word.contains("_zzequalzz_"))
	    		adjustedStatement += ARITH_PREFIX;
	    	adjustedStatement += word;
	    }
	    
		return adjustedStatement.substring(1, adjustedStatement.length());
	}

	/**
	   * This method is used to parse domain expressions to one long term 
	   * @param requirements this is the requirement set to be processed
	   * @return ArrayList<String> this return contains the requirement set after processing
	   */
	public static ArrayList<String> parseDomainExpressions(
			ArrayList<String> text) {
		// TODO Auto-generated method stub
		return text;
	}
}
