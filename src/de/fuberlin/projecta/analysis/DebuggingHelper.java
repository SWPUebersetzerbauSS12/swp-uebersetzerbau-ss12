package de.fuberlin.projecta.analysis;

import de.fuberlin.commons.lexer.IToken;
import de.fuberlin.projecta.analysis.ast.AbstractSyntaxTree;

public class DebuggingHelper {

	/**
	 * Try to find nearest Token of an AST node
	 * (recursively searches parent nodes to find next attached Token)
	 * 
	 * @param node AST node
	 * @return  Token
	 */
	public static IToken extractPosition(AbstractSyntaxTree node) {
		while (node != null) {
			IToken token = node.getToken();
			if (token != null)
				return token;

			node = (AbstractSyntaxTree)node.getParent();
		}
		return null;
	}

}
