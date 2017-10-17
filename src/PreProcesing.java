import java.util.ArrayList;


public class PreProcesing {

	public static ArrayList<String> parseArithematicExpressions(ArrayList<String> text){
		
		ArrayList<String> parsedText = new ArrayList<String>();
		
		for(String statement: text)
		{
			if(statement.equals(""))
				continue;
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

	private static String addPrefix(String statement) {
		String adjustedStatement = "";
		String[] words = statement.split(" ");
	    for (String word: words) {
	    	adjustedStatement += " ";
	    	if(word.contains("_zzminuszz_")|| word.contains("_zzpluszz_") || word.contains("_zzmulzz_") ||
	    			word.contains("_zzdivzz_") || word.contains("zzopzz_") || word.contains("_zzequalzz_"))
	    		adjustedStatement += "zzarithzz_";
	    	adjustedStatement += word;
	    }
	    
		return adjustedStatement.substring(1, adjustedStatement.length());
	}

	public static ArrayList<String> parseDomainExpressions(
			ArrayList<String> text) {
		// TODO Auto-generated method stub
		return text;
	}
}
