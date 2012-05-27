package main.model;

public enum TokenType {
	TypeDefinition,
	Addition,
	Subtraction,
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
	CompareLower,
	CompareGreater,
	CompareLowerEqual,
	CompareGreaterEqual,
	CompareEqual,
	CompareNotEqual,
	Label,
	Branch;
}
