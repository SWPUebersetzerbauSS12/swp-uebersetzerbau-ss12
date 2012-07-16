package de.fuberlin.projecta.analysis;

import java.util.List;

import de.fuberlin.projecta.analysis.ast.AbstractSyntaxTree;

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
