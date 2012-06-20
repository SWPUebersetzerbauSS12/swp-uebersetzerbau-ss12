package de.fuberlin.projectF.CodeGenerator;

import de.fuberlin.projectF.CodeGenerator.model.Token;

public interface ILexer {
	
	public int close();
	public Token getNextToken();

}
