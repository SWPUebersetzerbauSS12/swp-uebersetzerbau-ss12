package main.model;

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
}
