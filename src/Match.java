import java.util.ArrayList;

import org.jpl7.Query;


public class Match {

	public static void Intialize(){
		Query q = new Query("consult('src/test.pl')");
		System.out.println((q.hasSolution() ? "succeeded" : "failed"));	
	}
	
	public static String getMatchedRuleOfTheMentionId(ArrayList<String> MentionIdRelatedRelations, ArrayList<String> AllRelations){
		String query = "findMatchedMentions("+ NormalizeArraylist(MentionIdRelatedRelations) + ", "+ NormalizeArraylist(AllRelations) + ",R)";
		Query q = new Query(query);
		System.out.print(q.oneSolution().get("R"));
		return q.oneSolution().get("R").toString();
	}
	
	private static String NormalizeArraylist(ArrayList<String> list){
		String temp;
		int firstdashIndex, lastDashindex, commaIndex;
		String normalizedList = "[";
		for (String s : list) {
			s = s.replace(":", "_");
			firstdashIndex = s.indexOf("-");
			lastDashindex = s.lastIndexOf("-");
			commaIndex = s.indexOf(",");
			temp = s.substring(0, firstdashIndex) + s.substring(commaIndex, lastDashindex) +")";
			normalizedList += temp.toLowerCase();
			normalizedList += ",";
		}
		//removing the last comma
		normalizedList = normalizedList.substring(0, normalizedList.length() - 1);
		normalizedList +="]";
		return normalizedList;
	}
}
