import java.util.ArrayList;


public class Rule {

	
	ArrayList<Predicate> conditions;
	Predicate relation;
	
	public Rule(ArrayList<Predicate> condList, Predicate r) {
		conditions = condList;
		relation = r;
	}
	
}
