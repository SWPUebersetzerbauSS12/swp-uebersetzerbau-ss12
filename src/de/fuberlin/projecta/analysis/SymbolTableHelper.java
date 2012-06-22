package de.fuberlin.projecta.analysis;

import java.util.List;

import de.fuberlin.projecta.analysis.ast.nodes.AbstractSyntaxTree;

/**
 * Wrapper class for symbolTables. Searches recursively upwards in the ast to
 * find the correct symbolTable.
 * 
 * @author sh4ke
 */
public class SymbolTableHelper {

	public EntryType lookup(String name, AbstractSyntaxTree node) {

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

	public EntryType lookup(String name, List<EntryType> params,
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
