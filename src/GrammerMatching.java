import java.util.ArrayList;


public class GrammerMatching {

	private static final String PREPOSITONS = "in out on of off by to into with from for";
	private static final ArrayList<String> SUBORDINATOR_CONJUNCTION = new ArrayList<String>();
	private static final ArrayList<String> NOUN_PHRASE_RULES = new ArrayList<String>();
	private static final ArrayList<String> VERB_PHRASE_RULES = new ArrayList<String>();
	private static final ArrayList<String> ADJECTIVE = new ArrayList<String>();
	
	public static void intialize(){
		//Initialize subordinator filter
		SUBORDINATOR_CONJUNCTION.add("[{pos:/IN|WRB/}]+");
		
		//Initialize noun phrase rules filter
		NOUN_PHRASE_RULES.add("[{pos:/DT/}]+[{pos:/JJ|JJR|JJS|VBN/}]*[{pos:/NN|NNS|NNP|XX/}]+");
		NOUN_PHRASE_RULES.add("[{pos:/NN|NNS|NNP|XX/}]+");
		
		//Initialize verb phrase rules filter
		VERB_PHRASE_RULES.add("[{pos:/RB|RBR|RBS/}]*[{pos:/VB/}]+");
		
		//Initialize adjectives filter
		ADJECTIVE.add("[{pos:/JJ|JJR|JJS/}]+");
		
	}
	
	
	public static boolean matchSubordinator(String txt){
		String out = StanfordPOS.matchSeqOfPOS(txt, SUBORDINATOR_CONJUNCTION, "");
		if(out.equals(""))
			return true;
		return false;
	}
	
    private static String matchPhrase(ArrayList<String> PHRASE_RULES, String txt, String constVal) {
		
		String out = StanfordPOS.matchSeqOfPOS(txt, PHRASE_RULES, constVal);
		
		if(!out.equals("")){
			//already Matched
			if(txt.equals(out))
				return txt;
			//false matching
			else if(txt.indexOf(out) != 0)
				return "";
			else //matched with (VP => V + PP) rule so the PP should be processed
				return out + " " + getMatchedPrepPhrase(txt.substring(out.length() +1, txt.length()));
		}
		return "";
	}

	public static String getMatchedNounPhrase(String txt){
		return matchPhrase(NOUN_PHRASE_RULES, txt, "");	
	}

	private static String getMatchedPrepPhrase(String txt) {
		//PP => P + NP
		String fWord = getFristWord(txt);
		if(PREPOSITONS.contains(fWord))
			txt = removeFristWord(txt);
		else
			return "";
		
		return fWord + " " + getMatchedNounPhrase(txt);
	}
	
	public static String getMatchedVerbPhrase(String txt, String constVal) {
		return matchPhrase(VERB_PHRASE_RULES, txt, constVal + " ");
	}
	
	public static boolean matchNounPhrase(String text){
		/*ArrayList<String> NOUN_PHRASE_RULES = new ArrayList<String>();
		NOUN_PHRASE_RULES.add("[{pos:/DT/}]+[{pos:/JJ|JJR|JJS/}]+[{pos:/NN|NNS|NNP/}]+");
		NOUN_PHRASE_RULES.add("[{pos:/DT/}]+[{pos:/NN|NNS|NNP/}]+");
		NOUN_PHRASE_RULES.add("[{pos:/NN|NNS|NNP/}]+");
		
		String out = StanfordPOS.matchSeqOfPOS(text, NOUN_PHRASE_RULES);
		
		if(!out.equals("")){
			//matched with any rule
			if(text.equals(out))
				return true;
			//false matching
			else if(text.indexOf(out) != 0)
				return false;
			else //matched with (NP => DT + N + PP) rule so the PP should be processed
				return matchPrepPhrase(text.substring(out.length() +1, text.length()));
		}
		
		return false;*/
		
		String out = getMatchedNounPhrase(text);
		if(text.equals(out))
			return true;
		return false;
	}
	
	public static boolean matchAdj(String text){
		String out = StanfordPOS.matchSeqOfPOS(text, ADJECTIVE, "");
		if(!out.equals(""))
			return true;
		return false;
	}
	
	public static String getFristWord(String txt){
		return txt.substring(0, txt.indexOf(" "));
	}
	
	public static String getLastWord(String txt){
		return txt.substring(txt.lastIndexOf(" ")+1, txt.length());
	}

	public static String removeFristWord(String txt) {
		int index = txt.indexOf(" ");
		return txt.substring(index + 1, txt.length());
	}
	
	public static String removeLastWord(String txt) {
		int eIndex = txt.lastIndexOf(" ");
		return txt.substring(0, eIndex);
	}
}
