import java.util.ArrayList;


public class TemplateChecking {
	
	public static enum TemplateType {BoilerPlateTemplate, ArsenalTemplate, None}

	static public TemplateType checkTemplate (String req){
		boolean res = false;
		RequirementTemplate arsenalTemplate, boilerPlateTemplate;
		arsenalTemplate = new ArsenalTemplate();
		boilerPlateTemplate = new BoilerPlateTemplate();
		
		res = arsenalTemplate.checkTemplate(req);
		if(res == true)
			return TemplateType.ArsenalTemplate;
		
		res = boilerPlateTemplate.checkTemplate(req);
		if(res == true)
			return TemplateType.BoilerPlateTemplate;
		
		return TemplateType.None;
	}
	/*private static boolean matchPrepPhrase(String txt){
		//PP => P + NP
		String PREPOSITONS = "in on of by to into with from for";
		if(existFirstWordInStringList(txt, PREPOSITONS))
			txt = removeFristWord(txt);
		else
			return false;
		return GrammerMatching.matchNounPhrase(txt);
	}*/
	
	
}
