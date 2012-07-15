package de.fuberlin.projecta.analysis.ast.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import de.fuberlin.commons.parser.ISyntaxTree;

/**
 * unknown count of declaration children !
 * 
 * @author sh4ke
 */
public class Record extends Type {
	
	@Override
	public String genCode(){
		String ret = "%struct." + getId().getValue();
		return ret;
	}

	@Override
	public String toTypeString() {
		ArrayList<Declaration> decls = new ArrayList<Declaration>();
		for (ISyntaxTree child : this.getChildren()) {
			decls.add((Declaration) child);
		}
		Collections.sort(decls, new Comparator<Declaration>() {
			@Override
			public int compare(Declaration child1, Declaration child2) {
				String id1, id2;
				id1 = ((Id) child1.getChild(1)).getValue();
				id2 = ((Id) child2.getChild(1)).getValue();
				return id1.compareTo(id2);
			}
		});
		String typeString = "record(";
		ArrayList<String> subStrings = new ArrayList<String>();
		for (Declaration decl : decls) {
			Type t = (Type) decl.getChild(0);
			Id id = (Id) decl.getChild(1);
			subStrings.add("(" + id.getValue() + "," + t.toTypeString() + ")");
		}
		for (String subStr : subStrings) {
			typeString += subStr + ";";
		}
		typeString += ")";
		return typeString;
	}

	@Override
	public String genStruct() {
		String ret = "%struct." + getId().getValue() + " = type { ";
		boolean tmp = false;
		for (int i = 0; i < getChildrenCount(); i++) {
			String t = ((Type) (getChild(i).getChild(0))).genCode();
			if (!t.equals("")) {
				t += ", ";
				tmp = true;
			}
			ret += t;
		}
		if (tmp) {
			ret = ret.substring(0, ret.length() - 2);
		}
		ret +=  " }";
		return ret + "\n";
	}

	/**
	 * This is hopefully working all the time, since no checking on
	 * classCastException is done.
	 * 
	 * @return
	 */
	private Id getId() {
		Declaration decl = (Declaration) this.getParent();
		return (Id) decl.getChild(1);
	}
}
