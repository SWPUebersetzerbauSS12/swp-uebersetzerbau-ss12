package de.fuberlin.projecta.analysis;

import de.fuberlin.projecta.analysis.ast.AbstractSyntaxTree;

public class SemanticException extends IllegalStateException {

	private static final long serialVersionUID = -265889433702118104L;

	private AbstractSyntaxTree node;

	/**
	 * Create a semantic exception
	 * @param message Human-readable message of this error
	 * @param node Node that caused this exception, may be null
	 */
	public SemanticException(String message, AbstractSyntaxTree node) {
		super(message);
		this.node = node;
	}

	/**
	 * Pointer to the node that caused this exception
	 * @return AST node, may be null
	 */
	public AbstractSyntaxTree getNode() {
		return node;
	}

}
