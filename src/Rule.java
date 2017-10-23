import java.util.ArrayList;

/**
 * This class contains two attributes: a list of conditions is the LHS and the relation is the RHS 
 */

public class Rule {

	//LHS
	ArrayList<Predicate> conditions;
	//RHS
	Predicate relation;
	
	/**
	 * constructor for initializing the Rule with its both sides
	 * @param condList This is the LHS
	 * @param r This is the RHS
	 */
	public Rule(ArrayList<Predicate> condList, Predicate r) {
		conditions = condList;
		relation = r;
	}
	
}
