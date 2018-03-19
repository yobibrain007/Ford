import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import org.jpl7.Variable;
import org.jpl7.fli.predicate_t;


public class SALTransformation {
	
	public static enum ExecutionType {e, NMOD, NEG, G, F, NEV, UNT, AND, OR, IMP, EQ, EXC, LT, DOM, ATOM,
		SET, SET1, CLR, INIT, SEND, TRAN, REC, IN, BOOL, NUM, DOT, VALUE, ARITH}

	private static HashMap<String, String> definedVariables = new HashMap<String, String>();
	
	private static HashMap<String, String> definition = new HashMap<String, String>();
	private static HashMap<String, String> intialization = new HashMap<String, String>();
	private static HashMap<String, String> transition = new HashMap<String, String>();
	
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
		
		else if(restOfText.contains("exceed") && nxtExcPart.contains("arg1"))
			type = ExecutionType.EXC;
		else if(restOfText.contains("is_less_than_or_equal")&& nxtExcPart.contains("arg1"))
			type = ExecutionType.LT;
		else if(restOfText.contains("equal") && nxtExcPart.contains("arg1"))
			type = ExecutionType.EQ;
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
			
			case LT:
				values = getValues(crrExcPart, "arg1=", "arg2=");
				executionOut = transformFunction(values[0], IR) + "<=" + transformFunction(values[1], IR);
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
		
	//	model.addAll(addDefinedTypes("  "));
		
		model.add("  main : MODULE =");
		model.add("  BEGIN");
		
	//	model.addAll(addDefinedVars("    "));
		
		model.add("    DEFINITION");
		
		model.addAll(addDefinitions("      "));
		
		model.add("    INITIALIZATION");
		
		model.addAll(addIntitialization("      "));
		
		model.add("  TRANSITION");
		
		model.addAll(addTransition("        "));
		
		model.add("  END;");
		
	//	model.addAll(addTheorem("  theorem"));
		
		model.add("END");
		
		return model;
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

}