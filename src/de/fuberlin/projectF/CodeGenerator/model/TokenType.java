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
	CompareInteger,
	CompareDouble,
	Label,
	Branch,
	ExpressionInt,
	ExpressionDouble,
	Cast,
	Getelementptr,
	Declare
}
