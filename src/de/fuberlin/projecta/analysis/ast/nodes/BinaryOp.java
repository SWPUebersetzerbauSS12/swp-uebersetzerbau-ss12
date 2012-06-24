package de.fuberlin.projecta.analysis.ast.nodes;

import sun.org.mozilla.javascript.Token;
import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.projecta.analysis.EntryType;
import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.analysis.SymbolTableHelper;

public class BinaryOp extends Statement {

	TokenType op;

	public BinaryOp(TokenType op) {
		this.op = op;
	}

	@Override
	public boolean checkSemantics() {
		switch (this.getOp()) {
		// TODO: think if you can find other cases, where semantics can be
		// wrong/ambiguous
		case OP_ASSIGN:
			if (!(this.getChild(0) instanceof Id)) {
				throw new SemanticException(
						"Left side of an assignment has to be an identifier, but is "
								+ this.getChild(0).getClass().toString());
			}
			if (this.getChild(1) instanceof BinaryOp && (((BinaryOp)this.getChild(1)).getOp() == TokenType.OP_ASSIGN)){
				throw new SemanticException("Left side of an assignment cannot be an assignment.");
			}
			break;
		case OP_DIV:
			if (this.getChild(1) instanceof IntLiteral) {
				int v = ((IntLiteral) this.getChild(1)).getValue();
				if (v == 0)
					throw new SemanticException("Division by Zero!");
				else
					return true;
			}
			if (this.getChild(1) instanceof RealLiteral) {
				double v = ((RealLiteral) this.getChild(1)).getValue();
				if (v == 0.0)
					throw new SemanticException("Division by Zero!");
				else
					return true;
			}
		}
		for (int i = 0; i < this.getChildrenCount(); i++) {
			if (!((AbstractSyntaxTree) this.getChild(i)).checkSemantics()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String genCode() {
		String ret = "";
		Block block = getHighestBlock();
		int regs[] = new int[5];
		Id a = null, b = null;
		if (op == TokenType.OP_EQ || op == TokenType.OP_NE
				|| op == TokenType.OP_LT || op == TokenType.OP_LE
				|| op == TokenType.OP_GT || op == TokenType.OP_GE) {
			// load value of id1 if it is an id!!!
			if (getChild(0) instanceof Id) {
				Id id = (Id) getChild(0);
				regs[3] = block.getNewRegister();
				ret += "%"
						+ regs[3]
						+ " = load "
						+ (SymbolTableHelper.lookup(id.getValue(), this))
								.getType().genCode() + "* %" + id.getValue()
						+ "\n";
			}
			// load value of id2 if it is an id!!!
			if (getChild(1) instanceof Id) {
				Id id = (Id) getChild(1);
				regs[4] = block.getNewRegister();
				ret += "%"
						+ regs[4]
						+ " = load "
						+ (SymbolTableHelper.lookup(id.getValue(), this))
								.getType().genCode() + "* %" + id.getValue()
						+ "\n";
			}
			if (getChild(0) instanceof Id && getChild(1) instanceof Id) {
				a = ((Id) getChild(0));
				b = ((Id) getChild(1));

				String int_or_real = "";
				String cmp_op = "";
				if (checkTypeOnEqual(a, b).equals("double")) {
					int_or_real = "fcmp";
					switch (op) {
					case OP_LT:
						cmp_op = "olt";
						break;
					case OP_LE:
						cmp_op = "ole";
						break;
					case OP_GT:
						cmp_op = "ogt";
						break;
					case OP_GE:
						cmp_op = "oge";
						break;
					case OP_EQ:
						cmp_op = "eq";
						break;
					case OP_NE:
						cmp_op = "ne";
						break;
					}
				} else {
					int_or_real = "icmp";
					switch (op) {
					case OP_LT:
						cmp_op = "slt";
						break;
					case OP_LE:
						cmp_op = "sle";
						break;
					case OP_GT:
						cmp_op = "sgt";
						break;
					case OP_GE:
						cmp_op = "sge";
						break;
					case OP_EQ:
						cmp_op = "eq";
						break;
					case OP_NE:
						cmp_op = "ne";
						break;
					}
				}
				int tmp = block.getNewRegister();
				ret += "%" + tmp + " = " + int_or_real + " " + cmp_op + " "
						+ checkTypeOnEqual(a, b) + " %";
				if (regs[3] == 0)
					ret += a.getValue() + ", %";
				else
					ret += regs[3] + ", %";
				if (regs[4] == 0)
					ret += b.getValue() + "\n";
				else
					ret += regs[4] + "\n";

			}
		}
		if (op == TokenType.OP_ASSIGN) {
			EntryType eA = null;
			a = ((Id) getChild(0));
			eA = SymbolTableHelper.lookup(a.getValue(), this);
			if (getChild(1) instanceof StringLiteral) {
				StringLiteral str = (StringLiteral) getChild(1);
				/*
				 * if you look below, this is what is implemented (it's kind of
				 * sick) ;basic principle for saving a string to a char pointer
				 * %str3 %r3 = alloca [9 x i8] store [9 x i8] c"test1234\00", [9
				 * x i8]* %r3 %firstEl = getelementptr [9 x i8]* %r3, i8 0, i8 0
				 * store i8* %firstEl, i8** %str3
				 */
				int tempReg = block.getNewRegister();
				int tempReg2 = block.getNewRegister();
				int strLength = (str.getValue().length() + 1);
				ret = "%" + tempReg + " = alloca [" + strLength + " x i8]\n";
				ret += "store [" + strLength + " x i8] c\"" + str.getValue()
						+ "\\00\", [" + strLength + " x i8]* %" + tempReg
						+ "\n";
				ret += "%" + tempReg2 + " = getelementptr [" + strLength
						+ " x i8]* %" + tempReg + ", i8 0, i8 0 \n";
				ret += "store i8* %" + tempReg2 + ", i8** %" + a.getValue();
			} else if (getChild(1) instanceof FuncCall) {
				int reg = block.getNewRegister();
				String id = ((Id) getChild(0)).getValue();
				String type = SymbolTableHelper
						.lookup(((Id) getChild(0)).getValue(), this).getType()
						.genCode();
				ret = "%" + reg + " = " + ((FuncCall) getChild(1)).genCode() + "\n";

				ret += "store " + type + " %" + reg + ", " + type + "* %"
						+ id;
			} else if (getChild(1) instanceof Id) {
				String type1 = SymbolTableHelper
						.lookup(((Id) getChild(1)).getValue(), this).getType()
						.genCode();
				int reg = block.getNewRegister();
				ret = "%" + reg + " = load " + type1 + "* %" + ((Id) getChild(1)).getValue() + "\n";

				String type0 = SymbolTableHelper
						.lookup(((Id) getChild(0)).getValue(), this).getType()
						.genCode();
				ret += "store " + type0 + " %" + reg
						+ ", " + eA.getType().genCode() + "* %" + a.getValue();
				}
			else {
				ret = "store " + ((AbstractSyntaxTree) getChild(1)).genCode()
						+ ", " + eA.getType().genCode() + "* %" + a.getValue();
			}
		}
		if (op == TokenType.OP_ADD || op == TokenType.OP_MINUS
				|| op == TokenType.OP_DIV || op == TokenType.OP_MUL) {
			String op_name;

			for (int i=0; i < getChildrenCount(); i++){
				switch(op){
				case OP_ADD:
					op_name = "add";
					break;
				case OP_MINUS:
					op_name = "sub";
					break;
				case OP_DIV:
					op_name = "div";
				case OP_MUL:
					op_name = "mul";
				}
				//ret += block.getNewRegister() + op_name + "";
			}
		}

		return ret;
	}

	/**
	 * Searches the symbolTables up to the point where both id's are found and
	 * gives the highest type possible. E.g. for int and real it is double, for
	 * int and int it is i32, for int and string it is i8*. If at least one
	 * parameter is not found in any symbolTable an SemanticException is raised.
	 * 
	 * @param a
	 *            the first id
	 * @param b
	 *            the second id
	 * @return the highest possible basicTokenType of both id's
	 */
	private String checkTypeOnEqual(Id a, Id b) {
		String ret = "";

		EntryType eA = null, eB = null;
		eA = SymbolTableHelper.lookup(a.getValue(), this);
		eB = SymbolTableHelper.lookup(b.getValue(), this);

		if (eA != null && eB != null) {
			Type tA = eA.getType();
			Type tB = eB.getType();
			if (tA.equals(tB)) {
				ret = tA.genCode();
			} else {
				throw new SemanticException("Error! " + eA + " and " + eB
						+ " must be of the same type!");
			}
		} else {
			throw new SemanticException("Error! Id's:" + a.getValue() + ", "
					+ b.getValue() + " not found in symbolTables:");
		}

		return ret;
	}

	public TokenType getOp() {
		return op;
	}

	@Override
	public boolean checkTypes() {
		// TODO Auto-generated method stub
		return false;
	}
}
