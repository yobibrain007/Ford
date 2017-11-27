import java.util.ArrayList;


public class BoilerPlateTemplate extends RequirementTemplate{
	
	
	private final ArrayList<String> BOLER_TEMPLATE_MODAL = new ArrayList<String>();
	private final ArrayList<String> BOILERPLAE_CONDITIONS_KEYWORDS = new ArrayList<String>();
	
	public BoilerPlateTemplate(){
		inetializeBoilerPlateModal();
		inetializeBoilerPlateConditionKeywords();
	}
	
	private  void inetializeBoilerPlateModal(){
		BOLER_TEMPLATE_MODAL.add("shall");
		BOLER_TEMPLATE_MODAL.add("should");
		BOLER_TEMPLATE_MODAL.add("would");
	}

	private  void inetializeBoilerPlateConditionKeywords(){
		BOILERPLAE_CONDITIONS_KEYWORDS.add("as long as ");
		BOILERPLAE_CONDITIONS_KEYWORDS.add("as soon as ");
		BOILERPLAE_CONDITIONS_KEYWORDS.add("after ");
		BOILERPLAE_CONDITIONS_KEYWORDS.add("if ");
	}
	
	@Override
	public boolean checkTemplate(String req) {
		req = filterReq(req);
		int id = getPatternId(req);
		return matchPattern(id, req);
	}

	@Override
	protected String filterReq(String req) {
		req = req.substring(0, req.length()-2);
		req = req.replace(",", "");
		req = req.replace("  ", " ");
		return removeBoilerConditionWord(req);
	}

	@Override
	protected int getPatternId(String req) {
		//<opt-Condition> <NP> <modal> "PROVIDE" <NP> "WITH THE ABILITY" <infinitive-VP> <NP> <opt-Details>
		if(req.contains("provide") && req.contains("with the ability"))
			return 1;
		//<opt-Condition> <NP> <modal> "BE ABLE" <infinitive-VP> <NP> <opt-Details>
		else if(req.contains("be able "))
			return 2;
		//<opt-Condition> <NP> <VP-starting-with-modal> <NP <opt-Details>
		return 3;
	}

	@Override
	protected boolean matchPattern(int id, String req) {
		boolean matchedFFragment, matchedLFragment, matchedNP;
		String []reqParts;
		switch(id){
		
		case 1:
			reqParts = req.split(" provide ");
			matchedFFragment = matchFirstFragment(reqParts[0]);
			
			reqParts = reqParts[1].split(" with the ability ");
			
			matchedNP = GrammerMatching.matchNounPhrase(reqParts[0]);
			matchedLFragment = matchLastFragment(reqParts[1], "to");
			
			if(matchedFFragment && matchedNP && matchedLFragment)
				return true;
			break;
		case 2:
			reqParts = req.split(" be able ");
			matchedFFragment = matchFirstFragment(reqParts[0]);
			matchedLFragment = matchLastFragment(reqParts[1], "to");
			
			if(matchedFFragment && matchedLFragment)
				return true;
			break;
		case 3:
			String modal = getModal(req);
			reqParts = splitAtModal(req, modal);
			matchedNP = GrammerMatching.matchNounPhrase(reqParts[0]);
			matchedLFragment = matchLastFragment(reqParts[1], modal);
			if(matchedNP && matchedLFragment)
				return true;
			break;				
		}
		
		return false;
	}

	private static String[] splitAtModal(String req, String modal) {
		String [] parts = new String [2];
		int index = req.indexOf(" " + modal + " ");
		parts[0] = req.substring(0, index);
		parts[1] = req.substring(index + 1, req.length());
		
		return parts;
	}

	private String getModal(String req) {
		for (String modal : BOLER_TEMPLATE_MODAL)
			if(req.contains(modal))
				return modal;
		return "";
	}

	
	private String removeBoilerConditionWord(String req) {
		for (String keyword : BOILERPLAE_CONDITIONS_KEYWORDS)
			if(req.contains(keyword) && req.indexOf(keyword) == 0){
				req = req.replace(keyword, "");
				break;
			}
		return req;
	}

	private boolean matchFirstFragment(String txt) {		
		String lWord = GrammerMatching.getLastWord(txt);
		if(BOLER_TEMPLATE_MODAL.contains(lWord))
			txt = GrammerMatching.removeLastWord(txt);
		return GrammerMatching.matchNounPhrase(txt);
	}

	private boolean matchLastFragment(String txt, String constVal) {
		String matchedVP, matchedNP, temp;
		
		matchedVP = GrammerMatching.getMatchedVerbPhrase(txt, constVal);
		temp = txt.substring(matchedVP.length(), txt.length());
		
		matchedNP = GrammerMatching.getMatchedNounPhrase(temp);
		temp = temp.substring(matchedNP.length(), temp.length());
		
		if(matchOpDetails(temp))
			return true;
		return false;
	}

	private boolean matchOpDetails(String txt) {
		return GrammerMatching.matchSubordinator(txt);
	}

}
