package parser.nodes;

import parser.ISyntaxTree;

/**
 * Deprecated. Should not be used atm.
 */
public abstract class Terminal extends Tree {

	public Terminal(String name) {
		super(name);
	}

	/**
	 * Should a leaf really implement this method?
	 */
	public void addTree(ISyntaxTree tree) {
		throw new UnsupportedOperationException("Can't extend a leaf!");
	}

}
