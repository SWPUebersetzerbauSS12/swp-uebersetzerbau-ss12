package de.fuberlin.projecta.analysis;

import java.util.List;

import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.ast.AbstractSyntaxTree;
import de.fuberlin.projecta.analysis.ast.Id;
import de.fuberlin.projecta.analysis.ast.Record;
import de.fuberlin.projecta.analysis.ast.RecordVarCall;
import de.fuberlin.projecta.analysis.ast.Type;

/**
 * Wrapper class for symbolTables.
 * 
 * Searches recursively upwards in the AST to find the correct symbol table.
 */
public class SymbolTableHelper {

	public static EntryType lookup(String name, AbstractSyntaxTree node) {
		SymbolTable t = node.getTable();
		EntryType entry = null;
		if (t != null) {
			if (t.lookup(name) != null) {
				entry = t.lookup(name);
			}
		} else {
			AbstractSyntaxTree parent = (AbstractSyntaxTree) (node).getParent();
			while (parent != null) {
				t = parent.getTable();
				if (t != null) {
					entry = t.lookup(name);

					if (entry != null) {
						return entry;
					}
				}
				parent = (AbstractSyntaxTree) parent.getParent();
			}
		}
		return null;
	}
	
	private static Id checkRecordVarCall(Type r, String names) {
		if (names.isEmpty()) {
			return (Id) (r.getParent().getChild(1));
		}
		int index = names.indexOf(".");
		String name = (index >= 0) ? names.substring(0, index) : names;
		for (ISyntaxTree c : r.getChildren()) {
			if (((Id) c.getChild(1)).getValue().equals(name)) {
				if (index >= 0) {
					return checkRecordVarCall((Type) c.getChild(0),
							names.substring(index + 1));
				} else {
					return (Id) c.getChild(1);
				}
			}
		}
		return null;
	}

	/**
	 * This should be used for searching record entries.
	 * 
	 * @param recName
	 *            The record where varName is searched in.
	 * @param node
	 *            The node to start searching from.
	 * @return The corresponding EntryType item for varName
	 */
	public static Type lookupRecordVarCall(RecordVarCall node) {
		// recName is name of outer record (e.g: a.b.c => recName = 'a')
		String recName = node.getRecordId().getValue();
		EntryType entry = lookup(recName, node);
		if (entry == null) {
			throw new TypeErrorException("Record " + recName
					+ " not defined before!");
		}
		String route = "";

		ISyntaxTree child = node;
		do {
			route = "." + ((Id) child.getChild(1)).getValue() + route;
			child = child.getChild(0);
		} while (child instanceof RecordVarCall);
		route = route.substring(1);
		Type returnType = checkRecordVarCall(entry.getType(), route);
		return returnType;
	}

	public static EntryType lookup(String name, List<EntryType> params,
			AbstractSyntaxTree node) {

		SymbolTable t = node.getTable();
		EntryType entry = null;
		if (t != null) {
			if (t.lookup(name, params) != null) {
				entry = t.lookup(name, params);
			}
		} else {
			AbstractSyntaxTree parent = (AbstractSyntaxTree) (node).getParent();
			while (parent != null) {
				t = parent.getTable();
				if (t != null) {
					entry = t.lookup(name, params);

					if (entry != null) {
						return entry;
					}
				}
				parent = (AbstractSyntaxTree) parent.getParent();
			}
		}
		return null;
	}

}
