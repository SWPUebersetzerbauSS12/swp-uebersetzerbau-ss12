package parser;

public enum NonTerminal {
	program, funcs, func,
	func_, optparams, params, params_, block, decls,
	decl, type, type_, stmts, stmt, stmt_, stmt__, loc,
	loc_, loc__, assign, assign_, bool, bool_, join,
	join_, equality, equality_, rel, rel_, expr, expr_,
	term, term_, unary, factor, factor_, optargs, args,
	args_, basic,
	EPSILON
};