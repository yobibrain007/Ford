import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import org.jpl7.Variable;
import org.jpl7.fli.predicate_t;


public class SALTransformation {
	
	public static enum ExecutionType {e, NMOD, NEG, G, F, NEV, UNT, AND, OR, IMP, EQ, EXC, DOM, ATOM,
		SET, SET1, CLR, INIT, SEND, TRAN, REC, IN, BOOL, NUM, DOT, VALUE, ARITH}

	private static HashMap<String, String> variables = new HashMap<String, String>();
	private static HashMap<String, String> attributes = new HashMap<String, String>();
	private static HashMap<String, String> definedTypes = new HashMap<String, String>();
	private static HashMap<String, String> definedVariables = new HashMap<String, String>();
	private static HashMap<String, String> definedTypesBeforeUpdate = new HashMap<String, String>();
	
	private static HashMap<String, String> definition = new HashMap<String, String>();
	private static HashMap<String, String> intialization = new HashMap<String, String>();
	private static HashMap<String, String> transition = new HashMap<String, String>();
	private static ArrayList<String> theorem = new ArrayList<String>();

	private static int newTypeIndex;
	
	public static String transformFunction(String text, HashMap<String, String> IR){
		
		String res;
		String [] parts = separateExecutionParts(text);
		ExecutionType type = getExecutionType(parts[0], parts[1]);
		
		res = execute(type, parts[0], parts[1], IR);
		if(type.equals(ExecutionType.AND) || type.equals(ExecutionType.OR) || type.equals(ExecutionType.IMP))
			res = "(" + res + ")";
		
		return res;
	}

	private static String[] separateExecutionParts(String text) {
		
		String []out = new String[2];
		// this is an input need to be expanded
		if(!text.contains("|")){
			out[0] = text;
			out [1]= "";
			return out;
		}
		//String [] last = getLastPart(text);
		//if(last.equals("entity") || last.equals("unique") || last.equals("predicate"))
		
		//her I am assuming that all arguments are corresponding to only one execution type
		int indexOfLastPar = text.lastIndexOf("|");
		out[0] = text.substring(indexOfLastPar + 1, text.length());
		out[1] = text.substring(0, indexOfLastPar);
		
		return out;
	}

	private static ExecutionType getExecutionType(String nxtExcPart, String restOfText)  {
		// kol el types et3mlet ma3ada el bool wel row bta3 el ATOM wel arith wel num wel predicate hshofhom b3den
		ExecutionType type = null;
		if(!nxtExcPart.contains("|") && restOfText.equals(""))
			type = ExecutionType.e;
		else if(nxtExcPart.equals("entity") || nxtExcPart.equals("unique"))
			type = ExecutionType.NMOD;
		else if(nxtExcPart.contains("neg"))
			type = ExecutionType.NEG;
		else if(nxtExcPart.contains("Talways"))
			type = ExecutionType.G;
		else if(nxtExcPart.contains("Teventually"))
			type = ExecutionType.F;
		else if(nxtExcPart.contains("Tnever"))
			type = ExecutionType.NEV;
		else if(nxtExcPart.contains("Tuntil"))
			type = ExecutionType.UNT;
		else if(nxtExcPart.contains("and:"))
			type = ExecutionType.AND;
		else if(nxtExcPart.contains("or:"))
			type = ExecutionType.OR;
		else if(nxtExcPart.contains("impliedby:"))
			type = ExecutionType.IMP;
		
		else if(restOfText.contains("equal") && nxtExcPart.contains("arg1"))
			type = ExecutionType.EQ;
		else if(restOfText.contains("exceed") && nxtExcPart.contains("arg1"))
			type = ExecutionType.EXC;
		else if(restOfText.contains("dominate") && nxtExcPart.contains("arg1"))
			type = ExecutionType.DOM;
		
		else if(restOfText.contains("set") && nxtExcPart.contains("object=") && nxtExcPart.contains("to="))
			type = ExecutionType.SET;
		else if(restOfText.contains("set") && nxtExcPart.contains("obj"))
			type = ExecutionType.SET1;
			
		else if(restOfText.contains("clear") && nxtExcPart.contains("obj"))
			type = ExecutionType.CLR;
		else if(restOfText.contains("initialize") && nxtExcPart.contains("obj") && nxtExcPart.contains("to="))
			type = ExecutionType.INIT;
		else if(restOfText.contains("send") && nxtExcPart.contains("obj"))
			type = ExecutionType.SEND;
		else if(restOfText.contains("transmit") && nxtExcPart.contains("obj"))
			type = ExecutionType.TRAN;
		else if(restOfText.contains("receive") && nxtExcPart.contains("obj"))
			type = ExecutionType.REC;
		else if(restOfText.contains("be") && nxtExcPart.contains("agent") && nxtExcPart.contains("in"))
			type = ExecutionType.IN;
			
		else if(nxtExcPart.equals("bool"))
			type = ExecutionType.BOOL;
		else if(nxtExcPart.equals("num"))
			type = ExecutionType.NUM;
		else if(nxtExcPart.equals("arithmetic"))
			type = ExecutionType.ARITH;
		
		else if(restOfText.contains("value") && nxtExcPart.contains("of"))
			type = ExecutionType.VALUE;
		else if(restOfText.contains("entity") && nxtExcPart.contains("of"))
			type = ExecutionType.DOT;
		
		return type;
	}

	private static String execute(ExecutionType type, String crrExcPart,
			String restOfText, HashMap<String, String> IR) {
		// kol el types et3mlet ma3ada el bool wel row bta3 el ATOM wel arith wel num wel predicate hshofhom b3den
		String executionOut = "", val;
		String [] values;
		crrExcPart = filterExecutionPart(crrExcPart);
		switch (type){
			case e:
				executionOut = transformFunction(Parser.filterSpaces(IR.get(crrExcPart)), IR);
				break;
				
			case NMOD:
				if(restOfText.contains("|"))
					executionOut = getOneval("@" + restOfText, "@", "|");
				else
					executionOut = getOneval("@" + restOfText, "@", null);
				break;
				
			case DOT:
				val = getOneval(crrExcPart, "of=", null);
				//adjustIR();
				executionOut = transformFunction(val, IR) + "." + getOneval("@" + restOfText, "@", "|");
				break;
				
			case NEG:
				executionOut = "NOT( " + transformFunction(restOfText, IR) + " )";
				break;
				
			case G:
				executionOut = "G( " + transformFunction(restOfText, IR) + " )";
				break;
				
			case F:
				executionOut = "F( " + transformFunction(restOfText, IR) + " )";
				break;
				
			case NEV:
				executionOut = "NOT(F ( " + transformFunction(restOfText, IR) + " ))";
				break;
				
			case UNT:
				val = getOneval(crrExcPart, "=", null);
				executionOut = transformFunction(restOfText, IR) + " U " + transformFunction(val, IR);
				break;
				
			case AND:
				val = getOneval(crrExcPart, ":", null);
				executionOut = executeListOfArgs(val, " AND ", restOfText, IR);
				break;
				
			case OR:
				val = getOneval(crrExcPart, ":", null);
				executionOut = executeListOfArgs(val, " OR ", restOfText, IR);
				break;
			
			case IMP:
				val = getOneval(crrExcPart, ":", null);
				executionOut = transformFunction(val, IR) + " => " + transformFunction(restOfText, IR);
				break;
				
			case EQ:
				values = getValues(crrExcPart, "arg1=", "arg2=");
				executionOut = transformFunction(values[0], IR) + "=" + transformFunction(values[1], IR);
				break;
				
			case EXC:
				values = getValues(crrExcPart, "arg1=", "arg2=");
				executionOut = transformFunction(values[0], IR) + ">" + transformFunction(values[1], IR);
				break;
			
			case DOM:
				values = getValues(crrExcPart, "arg1=", "arg2=");
				executionOut = transformFunction(values[0], IR) + ">=" + transformFunction(values[1], IR);
				break;
				
			case SET:
				values = getValues(crrExcPart, "object=", "to=");
				executionOut = transformFunction(values[0], IR) + "=" + transformFunction(values[1], IR);
				break;
				
			case SET1:
				val = getOneval(crrExcPart, "object=", null);
				executionOut = transformFunction(val, IR) + "=1";
				break;
				
			case CLR:
				val = getOneval(crrExcPart, "object=", null);
				executionOut = transformFunction(val, IR) + "=0";
				break;
				
			case INIT:
				values = getValues(crrExcPart, "object=", "to=");
				executionOut = transformFunction(values[0], IR) + "=" + transformFunction(values[1], IR);
				break;
				
			case SEND:
				val = getOneval(crrExcPart, "object=", null);
				executionOut = "out_channel=" + transformFunction(val, IR);
				break;
				
			case TRAN:
				val = getOneval(crrExcPart, "object=", null);
				executionOut = "out_channel=" + transformFunction(val, IR);
				break;
				
			case REC:
				val = getOneval(crrExcPart, "object=", null);
				executionOut = "in_channel=" + transformFunction(val, IR);
				break;
				
			case IN:
				values = getValues(crrExcPart, "agent=", "in=");
				executionOut = transformFunction(values[0], IR) + "=" + transformFunction(values[1], IR);
				break;
				
			case BOOL:
				executionOut = getOneval("@" + restOfText, "@", null);
				break;
				
			case NUM:
				executionOut = getOneval("@" + restOfText, "@", null);
				break;
				
			case ARITH:
				executionOut = getOneval("@" + restOfText, "@", null);
				break;
				
			case VALUE:
				executionOut = getOneval(crrExcPart, "of=", null);
				break;
				
			default:
				executionOut = "";
				break;
		}
		return executionOut;
	}

	private static String filterExecutionPart(String crrExcPart) {
		crrExcPart = crrExcPart.replace("[", "");
		crrExcPart = crrExcPart.replace("]", "");
		return crrExcPart;
	}

	private static String executeListOfArgs(String crrExcPart, String op, String restOfText, HashMap<String, String> IR) {
		
		String [] values = crrExcPart.split(",");
		
		String executionOut = transformFunction(restOfText, IR);
		for (String s : values) {
			executionOut += op;
			executionOut += transformFunction(s, IR);
		}
		return executionOut;
	}

	private static String getOneval(String crrExcPart, String start,
			String endDelimeter) {
		
		int sIndex, endIndex;
		
		sIndex = crrExcPart.indexOf(start) + start.length();
		
		if (endDelimeter == null)
			endIndex = crrExcPart.length();
		else
			endIndex = crrExcPart.indexOf(endDelimeter);
		
		return crrExcPart.substring(sIndex, endIndex);
	}

	private static String[] getValues(String crrExcPart, String val1, String val2) {
		String [] values = new String[2];
		
		if(crrExcPart.indexOf(val1) < crrExcPart.indexOf(val2)){
			values[0] = getOneval(crrExcPart, val1,  ",");
			values[1] = getOneval(crrExcPart, val2,  null);
		}else{
			values[0] = getOneval(crrExcPart, val1,  null);
			values[1] = getOneval(crrExcPart, val2,  ",");
		}
		return values;
	}
	
	public static void idntifyVariables(String req){
		String [] items = req.split(" ");
		ArrayList<String> data;
		String TypeVal;
		for (String s : items){
			data = getSeparatedData(s);
			if(data != null)
				updateVariables(data);
		}
	}

	private static void updateVariables(ArrayList<String> data) {
		String equalityVal, temp;
		if(data.get(3) != null){
			addVariable(data.get(2), data.get(3));
			addAttribute(data.get(3), "");
			equalityVal = data.get(3);
		}
		else
			equalityVal = data.get(2);
			
		if(data.get(1) != null){
			addVariable(data.get(0), data.get(1));
			addAttribute(data.get(1), equalityVal);
		}
		else
			addVariable(data.get(0), equalityVal);
	}

	private static void addAttribute(String key, String val) {
		addMapEntry(key, val, attributes);
	}


	private static void addVariable(String key, String val) {
		addMapEntry(key, val, variables);
	}

	private static void addMapEntry(String key, String val, HashMap<String, String> map){
		String temp;
		
		if(key.equals(val))
			return;
		if(!map.keySet().contains(key))
			map.put(key, " "+val);
		else{
			temp = map.get(key);
			if(!temp.contains(" " + val))
				temp = temp + ", " + val;
			map.remove(key);
			map.put(key, temp);
		}
	}
	// provides all variables and values separated
	private static ArrayList<String> getSeparatedData(String s) {
		String [] data;
		ArrayList<String> separatedData = new ArrayList<String>();
		
		s = s.replace(")", "");
		s = s.replace("(", "");
		
		//means implication sign
		if(s.contains("=>"))
			return null;
		else if(s.contains("="))
			data = s.split("=");
		else if( s.contains(">"))
			data = s.split(">");
		else if( s.contains("<"))
			data = s.split("<");
		else if( s.contains(">="))
			data = s.split(">=");
		else if( s.contains("<="))
			data = s.split("<=");
		else 
			return null;
		
		separatedData.addAll(Arrays.asList(getDecomposedData(data[0])));
		separatedData.addAll(Arrays.asList(getDecomposedData(data[1])));
		
		return separatedData;
	}

	//split properties from variable (e.g., X.y ... output ls {X, Y} )
	private static String [] getDecomposedData(String s) {
		String [] decomposedData = {s, null};
		if(s.contains("."))
			decomposedData = s.split("\\.");
		return decomposedData;
	}
	
	public static void print (){
		System.out.println(variables);
		System.out.println(attributes);
		System.out.println(definedTypes);
		System.out.println(definedVariables);
	}
	
	public static void defineVariables(){
		newTypeIndex = 0;
		for (Entry<String, String> e : variables.entrySet()) {
			defineOneVariable(e.getKey(), e.getValue());
		}
	}

	private static void defineOneVariable(String var, String val) {
		boolean succeded;
		val = val.replace(" ", "");
		String type = getPrimitiveType(val);
		if(type != null)
			definedVariables.put(var, type);
		else {
			succeded = checkIfEquivelentToAnotherVariable(var, val);
			if(!succeded)
				succeded = checkIfCompositeAttributes(var, val);
			if(!succeded){
				type = defineNewType("TYPE = {" + val + "}");
				definedTypesBeforeUpdate.put(val, type);
				definedVariables.put(var, type);
				newTypeIndex++;
			}
		}
		
	}
	private static String getPrimitiveType(String val){
		String type= null;
		if(val.contains("true") || val.contains("false"))
			return "BOOLEAN";
		else if(val.equals(""))
			return "Integer";
			
		type = getPreDefinedType(val);
		return type;
	}


	private static String getPreDefinedType(String val) {
		for (String key : definedTypesBeforeUpdate.keySet()) 
			if(equivelent(key, val))
				return definedTypesBeforeUpdate.get(key);
		
		return null;
	}

	private static boolean equivelent(String key, String val) {
		String [] data = val.split(",");
		
		for (String s : data)
			if(!key.contains(s))
				return false;
		return true;
	}

	private static String defineNewType(String val) {
		String type = "type"+String.valueOf(newTypeIndex);
		definedTypes.put(val, type);
		newTypeIndex++;
		return type;
	}

	private static boolean checkIfEquivelentToAnotherVariable(String var, String val) {
		String newVal = null;
		String [] data;
		if(val.contains(","))
			data = val.split(",");
		else{
			data = new String[1];
			data[0] = val;
		}
		
		for (String s : data) 
			if(variables.keySet().contains(s)){
				newVal = s;
				break;
			}
			
		if(newVal == null)
			return false;
		
		if(!definedVariables.keySet().contains(val))
			defineOneVariable(newVal, variables.get(newVal));
		
		definedVariables.put(var, definedVariables.get(newVal));
		
		return true;
	}
	
	private static boolean checkIfCompositeAttributes(String var, String val) {
		String type, tempVal, temp = "TYPE = [# ";
		String [] attr;
		if(val.contains(","))
			attr = val.split(",");
		else{
			attr = new String[1];
			attr[0] = val;
		}
		
		if(!attributes.keySet().contains(attr[0]))
			return false;
		for (String s : attr) {
			tempVal = attributes.get(s).replace(" ", "");
			type = getPrimitiveType(tempVal);
			if(type == null){
				type = defineNewType("TYPE = {" + tempVal + "}");
				definedTypesBeforeUpdate.put(tempVal, type);
			}
			temp = temp + s + " : " + type + " , ";
		}
		temp = temp.substring(0, temp.length()-3);
		temp += " #]";
		
		type = defineNewType(temp);
		definedTypesBeforeUpdate.put(val, type);
		definedVariables.put(var, type);
		
		return true;
	}
	
	public static ArrayList<String> generateSALModel(ArrayList<String> statVars, ArrayList<String> modelRules){
		ArrayList<String> model;
		
		isolateModelRuls(statVars, modelRules);
		model = prepareSALModel();
		return model;
	}

	private static void isolateModelRuls(ArrayList<String> statVars, ArrayList<String> modelRules) {
		for (String r : modelRules) 
			assignRule(r, statVars);
		
	}

	private static void assignRule(String r, ArrayList<String> statVars) {
		String[] ruleSides;
		String ctlVar;
		if(r.toLowerCase().contains("(f ") || r.toLowerCase().contains("g(") || r.toLowerCase().contains("u(")){
			theorem.add(r);
			return;
		}
		
		ruleSides = r.split("=>");
		if(ruleSides.length == 1)
		{
			ruleSides = r.split("=");
			ctlVar = ruleSides[1].replace(" ", "");
			if(definedVariables.keySet().contains(ctlVar))
				addToDefinition(ctlVar, "", r, definedVariables.get(ctlVar));
			else
				addToIntialization(ctlVar, r);
		}
		else{
			ctlVar = ruleSides[1].split("=")[0].replace(" ", "");
			if(statVars.contains(ctlVar))
				addToTransition(ctlVar, r);
			else
				addToDefinition(ctlVar, ruleSides[0], ruleSides[1],definedVariables.get(ctlVar));
		}
		
	}

	private static void addToIntialization(String ctlVar, String r) {
		intialization.put(ctlVar, r);
	}

	private static void addToDefinition(String ctlVar, String lHS, String rHS, String type) {
		String oldVal, newVal;
		newVal = lHS + " => " + rHS.replace(ctlVar, "Z");
		if(definition.keySet().contains(ctlVar)){
			oldVal = definition.get(ctlVar);
			definition.replace(ctlVar, oldVal, oldVal + " AND " + System.getProperty("line.separator") + newVal);
		}
		else
			definition.put(ctlVar, "IN {Z :" + type + " | " + System.getProperty("line.separator") + newVal);
	}

	private static void addToTransition(String ctlVar, String val) {
		
		String oldVal, newVal;
		newVal = val.replace("=>", "-->");
		if(transition.keySet().contains(ctlVar)){
			oldVal = transition.get(ctlVar);
			transition.replace(ctlVar, oldVal, oldVal + System.getProperty("line.separator") + " [] " + System.getProperty("line.separator") + newVal);
		}
		else 
			transition.put(ctlVar, newVal);
	}
	
	private static ArrayList<String> prepareSALModel() {
		ArrayList<String> model =  new ArrayList<String>();
		
		model.add("faa : CONTEXT =");
		model.add("BEGIN");
		
		model.addAll(addDefinedTypes("  "));
		
		model.add("  main : MODULE =");
		model.add("  BEGIN");
		
		model.addAll(addDefinedVars("    "));
		
		model.add("    DEFINITION");
		
		model.addAll(addDefinitions("      "));
		
		model.add("    INITIALIZATION");
		
		model.addAll(addIntitialization("      "));
		
		model.add("  TRANSITION");
		
		model.addAll(addTransition("        "));
		
		model.add("  END;");
		
		model.addAll(addTheorem("  theorem"));
		
		model.add("END");
		
		return model;
	}

	private static ArrayList<String> addDefinedTypes(String spaces) {
		ArrayList<String> _definedTypes =  new ArrayList<String>();
		
		for (Entry<String, String> e : definedTypes.entrySet())
			_definedTypes.add(spaces + e.getValue() + " : " + e.getKey() + ";");
		
		return _definedTypes;
	}

	private static ArrayList<String> addDefinedVars(String spaces) {
		ArrayList<String> _definedVariables =  new ArrayList<String>();
		
		for (Entry<String, String> e : definedVariables.entrySet())
			_definedVariables.add(spaces + e.getKey() + " : " + e.getValue());
		
		return _definedVariables;
	}

	private static ArrayList<String> addDefinitions(String spaces) {
		ArrayList<String> _definition =  new ArrayList<String>();
		
		for (Entry<String, String> e : definition.entrySet())
			_definition.add(spaces + e.getKey() + " " +  e.getValue() + "};");
		
		return _definition;
	}

	private static ArrayList<String> addIntitialization(String spaces) {
		ArrayList<String> _intialization =  new ArrayList<String>();
		
		for (String s : intialization.values())
			_intialization.add(spaces + s + ";");
		
		return _intialization;
	}

	private static ArrayList<String> addTransition(String spaces) {
		
		ArrayList<String> _transition =  new ArrayList<String>();
		_transition.add("      [");
		
		for (String s : transition.values())
			_transition.add(spaces + s);

		_transition.add(spaces + "[]");
		_transition.add(spaces + "ELSE -->");
		_transition.add("      ]");	
		
		return _transition;
	}

	private static ArrayList<String> addTheorem(String prefix) {
		ArrayList<String> _theorem =  new ArrayList<String>();
		int i = 0;
		for (String s : theorem){
			_theorem.add(prefix + String.valueOf(i) + ": THEOREM main |- G(" + s + ");");
			i++;
		}
		
		return _theorem;
		
	}
	}