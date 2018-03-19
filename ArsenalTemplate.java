import java.util.ArrayList;


public class ArsenalTemplate extends RequirementTemplate {

	private final String ARSENAL_CONDITION_WORDS = "if when";
	private final ArrayList<String> ARSENAL_CONDITION_VERBS = new ArrayList<String>();
	private final ArrayList<String> ARSENAL_ACTION_VERBS = new ArrayList<String>();
	
	public ArsenalTemplate(){
		intializeCondVerbs();
		intializeActionVerbs();
	}
	
	private void intializeCondVerbs(){
		ARSENAL_CONDITION_VERBS.add("equals");
		ARSENAL_CONDITION_VERBS.add("exceeds");
		ARSENAL_CONDITION_VERBS.add("does not exceed");
		ARSENAL_CONDITION_VERBS.add("is greater than");
		ARSENAL_CONDITION_VERBS.add("is_less_than_or_equal");
		ARSENAL_CONDITION_VERBS.add("is set to");
		ARSENAL_CONDITION_VERBS.add("is not set to");
	}
	private void intializeActionVerbs(){
	
		ARSENAL_ACTION_VERBS.add("shall be set to");
		ARSENAL_ACTION_VERBS.add("shall be initialized to");
		ARSENAL_ACTION_VERBS.add("shall never be set to");
		ARSENAL_ACTION_VERBS.add("shall eventually not be set to");
		ARSENAL_ACTION_VERBS.add("shall eventually be set to");
	}
	
	@Override
	public boolean checkTemplate(String req) {
		req = filterReq(req);
		int id = getPatternId(req);
		return matchPattern(id, req);
	}

	@Override
	protected String filterReq(String req) {
		return req.substring(0, req.length()-2);
	}

	@Override
	protected int getPatternId(String req) {
		//<condition> [<NP> <condtion-VP> (<NP>|<JJ>) ((op_and)|(op_or)) ]+ ,  <NP> <action-VP> (<NP>|<JJ>)
		if(req.contains(" , "))
			return 1;
		//<NP> <action-VP> (<NP>|<JJ>)
		return 2;
	}

	@Override
	protected boolean matchPattern(int id, String req) {
		boolean matchedCond, matchedAction;
		switch(id){
		
		case 1:
			String []reqParts = req.split(" , ");
			matchedCond = matchCondition(reqParts[0]);
			matchedAction = matchAction(reqParts[1]);
			if(matchedAction && matchedCond)
				return true;
			break;
		case 2:
			matchedAction = matchAction(req);
			if(matchedAction)
				return true;
			break;				
		}
		
		return false;
	}

	private boolean matchCondition(String txt) {
		String fWord = GrammerMatching.getFristWord(txt);
		if(ARSENAL_CONDITION_WORDS.contains(fWord))
			txt = GrammerMatching.removeFristWord(txt);
		else
			return false;
		
		String [] multiCond = splitMultiCond(txt);
		ArrayList<String> remainingParts;
		for (String cond : multiCond) {
			remainingParts = matchCondVerb(cond);
			if(remainingParts.isEmpty())
				return false;
		
			if(!matchFragments(remainingParts))
				return false;
		}
		
		return true;
	}

	private String[] splitMultiCond(String txt) {
		if(txt.contains(" or "))
			return txt.split(" or ");
		else if(txt.contains(" and "))
			return txt.split(" and ");
		else {
			String[] s = {txt};
			return s;
		}
	}

	private ArrayList<String> matchCondVerb(String txt) {
		ArrayList<String> out = getRemainingPartsOfVerb(txt, ARSENAL_CONDITION_VERBS);
		return out;
	}

	private boolean matchAction(String txt) {
		ArrayList<String> remainingParts = matchActionVerb(txt);
		if(remainingParts.isEmpty())
			return false;
		return matchFragments(remainingParts);
	}

	private boolean matchFragments(ArrayList<String> remainingParts){
		if(GrammerMatching.matchNounPhrase(remainingParts.get(0)) && 
				(GrammerMatching.matchNounPhrase(remainingParts.get(1)) || GrammerMatching.matchAdj(remainingParts.get(1)) ))
						return true;
		
		return false;
	}
	private ArrayList<String> matchActionVerb(String txt) {
		
		ArrayList<String> out = getRemainingPartsOfVerb(txt, ARSENAL_ACTION_VERBS);
		return out;
	}
	

	private ArrayList<String> getRemainingPartsOfVerb(String txt,
			ArrayList<String> verbs) {
		int sIndex, eIndex;
		ArrayList<String> out = new ArrayList<String>();
		for (String v : verbs) {
			if(txt.contains(v)){
				sIndex = txt.indexOf(v);
				eIndex = sIndex + v.length();
				out.add(txt.substring(0, sIndex - 1));
				out.add(txt.substring(eIndex +1, txt.length()));
			}
		}
		return out;
	}
	
}
