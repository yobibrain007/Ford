
public abstract class RequirementTemplate {
	
	public abstract boolean checkTemplate(String req);
	
	protected abstract String filterReq(String req);
	
	protected abstract int getPatternId(String req);
	
	protected abstract boolean matchPattern(int id, String req);
	
	

}
