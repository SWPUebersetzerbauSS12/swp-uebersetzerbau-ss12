package de.fuberlin.projectF.CodeGenerator.model;

public enum TokenType {
	TypeDefinition,
	Load,
	Allocation,
	Definition,
	Assignment,
	Return,
	DefinitionEnd,
	EOF,
	Undefined,
	Call,
	String,
	Compare,
	Label,
	Branch,
	ExpressionInt,
	ExpressionDouble,
	Cast,
	Getelementptr
}
