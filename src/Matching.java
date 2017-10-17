import java.util.ArrayList;

import org.jpl7.Query;


public class Matching {

	public static void intialize(){
		Query q = new Query("consult('src/test.pl')");
		System.out.println((q.hasSolution() ? "succeeded" : "failed"));	
	}
	
	public static String getMatchedRelationsByTypeRules(ArrayList<String> mentionIdRelatedRelations, ArrayList<String> allRelations){
		String query = "findMatchedMentions("+ normalizeArrayList(mentionIdRelatedRelations) + ", "+ normalizeArrayList(allRelations) + ",R)";
		Query q = new Query(query);
		return q.oneSolution().get("R").toString();
	}
	
	private static String normalizeArrayList(ArrayList<String> list){
		String temp;
		int firstDashIndex, lastDashindex, commaIndex;
		String normalizedList = "[";
		for (String s : list) {
			if(s.contains("event") || s.contains("entity") || s.contains("pred")){
				normalizedList += s.toLowerCase();
				normalizedList += ",";
				continue;
			}
			s = s.replace(":", "_");
			firstDashIndex = s.indexOf("-");
			lastDashindex = s.lastIndexOf("-");
			commaIndex = s.indexOf(",");
			temp = s.substring(0, firstDashIndex) + s.substring(commaIndex, lastDashindex) +")";
			normalizedList += temp.toLowerCase();
			normalizedList += ",";
		}
		//removing the last comma
		normalizedList = normalizedList.substring(0, normalizedList.length() - 1);
		normalizedList +="]";
		return normalizedList;
	}
}
