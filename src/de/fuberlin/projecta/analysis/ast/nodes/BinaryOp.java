package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.projecta.analysis.EntryType;
import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.analysis.SymbolTableHelper;

public class BinaryOp extends AbstractSyntaxTree {

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
				return false;
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
		Id a = null, b = null;
		if (op == TokenType.OP_EQ || op == TokenType.OP_NE
				|| op == TokenType.OP_LT || op == TokenType.OP_LE
				|| op == TokenType.OP_GT || op == TokenType.OP_GE) {
			if (getChild(0) instanceof Id && getChild(1) instanceof Id) {
				a = ((Id) getChild(0));
				b = ((Id) getChild(1));

				String int_or_real = "";
				String cmp_op = "";
				if (checkType(a, b).equals("double")) {
					int_or_real = "fcmp";
				} else {
					int_or_real = "icmp";
				}

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
				ret = int_or_real + " " + cmp_op + " " + checkType(a, b)
						+ "* %" + a.getValue() + ", %" + b.getValue();

			}
		}
		if (op == TokenType.OP_ASSIGN) {
			EntryType eA = null;
			SymbolTableHelper helper = new SymbolTableHelper();
			a = ((Id) getChild(0));
			eA = helper.lookup(a.getValue(), this);
			if (getChild(1) instanceof StringLiteral) {
				StringLiteral str = (StringLiteral) getChild(1);
				/* if you look below, this is what is implemented (it's kind of sick)
 ;basic principle for saving a string to a char pointer %str3
  %r3 = alloca [9 x i8] 
  store [9 x i8] c"test1234\00", [9 x i8]* %r3 
  %firstEl = getelementptr [9 x i8]* %r3, i8 0, i8 0
  store i8* %firstEl, i8** %str3
				 */
				Block block = ((Block) getParent()).getHighestBlock();
				int tempReg = block.getNewRegister();
				int tempReg2 = block.getNewRegister();
				ret  = "%"+ tempReg +" = alloca ["+ str.getValue().length() + " x i8]\n";
				ret += "store ["+ str.getValue().length() + " x i8] c\""+ str.getValue() +"\\00\", ["+ str.getValue().length() +"x i8]* %"+tempReg + "\n";
				ret += "%"+tempReg2+" = getelementptr ["+str.getValue().length()+" x i8]* %"+tempReg+", i8 0, i8 0 \n";
				ret += "store i8* %"+tempReg2+", i8** %"+a.getValue();
			} else {
				ret = "store " + ((AbstractSyntaxTree) getChild(1)).genCode() + ", "
						+ eA.getType().genCode() + "* %" + a.getValue();
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
	private String checkType(Id a, Id b) {
		String ret = "";

		EntryType eA = null, eB = null;
		SymbolTableHelper helper = new SymbolTableHelper();
		eA = helper.lookup(a.getValue(), this);
		eB = helper.lookup(b.getValue(), this);

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
