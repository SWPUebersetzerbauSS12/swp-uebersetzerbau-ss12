package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.projecta.analysis.EntryType;
import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.analysis.SymbolTableHelper;
import de.fuberlin.projecta.analysis.TypeErrorException;
import de.fuberlin.projecta.codegen.LLVM;

public class BinaryOp extends Type {

	TokenType op;

	public TokenType getOp() {
		return op;
	}

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
			if (this.getChild(1) instanceof BinaryOp
					&& (((BinaryOp) this.getChild(1)).getOp() == TokenType.OP_ASSIGN)) {
				throw new SemanticException(
						"Left side of an assignment cannot be an assignment.");
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
		// int regs[] = new int[5];
		Id id1 = null, id2 = null;
		Type t1 = null, t2 = null;

		if (getChild(0) instanceof Id) {
			id1 = (Id) getChild(0);
		} else {
			t1 = (Type) getChild(0);
		}

		if (getChild(1) instanceof Id) {
			id2 = (Id) getChild(1);
		} else {
			t2 = (Type) getChild(1);
		}

		if (op == TokenType.OP_EQ || op == TokenType.OP_NE
				|| op == TokenType.OP_LT || op == TokenType.OP_LE
				|| op == TokenType.OP_GT || op == TokenType.OP_GE) {
			// load value of id1 if it is an id!!!
			if (id1 != null) {
				ret += LLVM.loadVar(id1);
			}
			// load value of id2 if it is an id!!!
			if (id2 != null) {
				ret += LLVM.loadVar(id2);
			}
			if (id1 != null && id2 != null) {
				int tmp = block.getNewVar();
				ret += "%" + tmp + " = " + getIntOrReal(id1, t1) + " "
						+ getOpName(id1, t1) + " ";
				ret += SymbolTableHelper.lookup(id1.getValue(), this).getType()
						.genCode()
						+ " %";
				ret += LLVM.getMem(id1) + ", %";
				ret += LLVM.getMem(id2) + "\n";
			}
		} else if (op == TokenType.OP_ADD || op == TokenType.OP_MINUS
				|| op == TokenType.OP_DIV || op == TokenType.OP_MUL) {
			String type = "";
			String mathOp = "";
			int val1 = 0, val2 = 0; // used to store both values
			if (getChild(0) instanceof Id) {
				if (SymbolTableHelper.lookup(id1.getValue(), this).getType()
						.toTypeString().equals("double"))
					mathOp = "f"; // append f in front of math_op
			} else if (getChild(0) instanceof Type) {
				if (((Type) getChild(0)).toTypeString().equals("double"))
					mathOp = "f"; // append f in front of math_op
			} else {
				throw new SemanticException("type couldn't be figured out in: "
						+ getChild(0));
			}

			switch (op) {
			case OP_ADD:
				mathOp += "add";
				break;
			case OP_MINUS:
				mathOp += "sub";
				break;
			case OP_MUL:
				mathOp += "mul";
				break;
			case OP_DIV:
				mathOp += "div";
				break;
			}

			if (id1 != null) {
				// the value must only be loaded if it is instanceof Id and if
				// it is not parameter!
				ret += LLVM.loadVar(id1);

				if (SymbolTableHelper.lookup(id1.getValue(), this).getType()
						.toTypeString().equals("int")) {
					type = "i32";
				} else if (SymbolTableHelper.lookup(id1.getValue(), this)
						.getType().toTypeString().equals("double")) {
					type = "double";
				}

			} else if (getChild(0) instanceof Type) {
				if (((Type) getChild(0)).toTypeString().equals("int"))
					type = "i32";
				else if (((Type) getChild(0)).toTypeString().equals("double"))
					type = "double";
			} else {
				throw new SemanticException("No type could be made in: "
						+ this.getClass());
			}
			// load value of id2 if it is an id and not in params
			if (getChild(1) instanceof Id) {
				ret += LLVM.loadVar(id2);
			}

			int val = block.getNewVar();
			this.setValMemory(val); // save currents computation in this node
			if (id1 != null)
				val1 = id1.getVar();
			if (id2 != null)
				val2 = id2.getVar();
			if (id1 != null && id2 != null) {
				ret += "%" + val + " = " + mathOp + " " + type + " %"
						+ LLVM.getMem(id1) + ", %" + LLVM.getMem(id2) + "\n";
			} else if (t1 != null && t2 != null) {
				// both are types?
				String s1 = t1.genCode(), s2 = t2.genCode();
				String[] tmp1 = s1.split(" ");
				String[] tmp2 = s2.split(" ");
				type = tmp1[0];
				ret += "%" + val + " = " + mathOp + " " + type + " " + tmp1[1]
						+ ", " + tmp2[1] + "\n";
			} else if (t1 != null) {
				String s1 = t1.genCode();
				String[] tmp1 = s1.split(" ");
				type = tmp1[0];
				ret += "%" + val + " = " + mathOp + " " + type + " " + tmp1[1]
						+ ", %" + LLVM.getMem(id2) + "\n";
			} else {
				String s2 = t2.genCode();
				String[] tmp2 = s2.split(" ");
				type = tmp2[0];
				ret += "%" + val + " = " + mathOp + " " + type + " %"
						+ LLVM.getMem(id1) + ", " + tmp2[1] + "\n";
			}

		} else if (op == TokenType.OP_ASSIGN) {
			EntryType eA = null;
			eA = SymbolTableHelper.lookup(id1.getValue(), this);
			if (getChild(1) instanceof StringLiteral) {
				StringLiteral str = (StringLiteral) getChild(1);
				/*
				 * if you look below, this is what is implemented (it's kind of
				 * sick) ;basic principle for saving a string to a char pointer
				 * %str3 %r3 = alloca [9 x i8] store [9 x i8] c"test1234\00", [9
				 * x i8]* %r3 %firstEl = getelementptr [9 x i8]* %r3, i8 0, i8 0
				 * store i8* %firstEl, i8** %str3
				 */
				int tempReg = block.getNewVar();
				int tempReg2 = block.getNewVar();
				int strLength = (str.getValue().length() + 1);
				ret = "%" + tempReg + " = alloca [" + strLength + " x i8]\n";
				ret += "store [" + strLength + " x i8] c\"" + str.getValue()
						+ "\\00\", [" + strLength + " x i8]* %" + tempReg
						+ "\n";
				ret += "%" + tempReg2 + " = getelementptr [" + strLength
						+ " x i8]* %" + tempReg + ", i8 0, i8 0 \n";
				ret += "store i8* %" + tempReg2 + ", i8** %" + id1.getValue();
			} else if (getChild(1) instanceof FuncCall) {

				// id1 must exist!!!

				// load parameters of this function first
				if (getChild(1).getChildrenCount() > 1
						&& getChild(1).getChild(1).getChildrenCount() != 0) {
					ret += LLVM.loadParams((Args) getChild(1).getChild(1));
				}
				int reg = block.getNewVar();
				String type = SymbolTableHelper.lookup(id1.getValue(), this)
						.getType().genCode();
				ret += "%" + reg + " = " + ((FuncCall) getChild(1)).genCode()
						+ "\n";

				ret += "store " + type + " %" + reg + ", " + type + "* %"
						+ id1.getValue();
			} else if (getChild(1) instanceof Id) {
				String type = SymbolTableHelper.lookup(id1.getValue(), this)
						.getType().genCode();
				ret += LLVM.loadVar(id2);
				ret += "store " + type + " %" + LLVM.getMem(id2) + ", "
						+ eA.getType().genCode() + "* %" + id1.getValue();
			} else if (getChild(1) instanceof BinaryOp) {
				// First execute operations, then save the result
				ret += ((BinaryOp) getChild(1)).genCode();
				// int result = block.getCurrentRegister();
				// ret += "%" + block.getNewMemory() + " = load "
				// + eA.getType().genCode() + "* %" + result + "\n";
				ret += "store " + eA.getType().genCode() + " %"
						+ ((BinaryOp) getChild(1)).getVar() + ", "
						+ eA.getType().genCode() + "* %" + id1.getValue();
			} else {
				ret = "store " + ((AbstractSyntaxTree) getChild(1)).genCode()
						+ ", " + eA.getType().genCode() + "* %"
						+ id1.getValue();
			}
		} else {
			System.out.println("Unknown Binary OP: " + op);
		}
		return ret;
	}

	private String getIntOrReal(Id id, Type type) {
		String ret = "";
		if (id != null) {
			if ((id.getType().toTypeString().equals("double"))) {
				ret = "fcmp";
			} else {
				ret = "icmp";
			}
		} else if (type != null) {
			if ((type.toTypeString().equals("double"))) {
				ret = "fcmp";
			} else {
				ret = "icmp";
			}
		}

		return ret;
	}

	private String getOpName(Id id, Type type) {
		String ret = "";
		if (id != null) {
			if ((id.getType().toTypeString().equals("double"))) {
				switch (op) {
				case OP_LT:
					ret = "olt";
					break;
				case OP_LE:
					ret = "ole";
					break;
				case OP_GT:
					ret = "ogt";
					break;
				case OP_GE:
					ret = "oge";
					break;
				case OP_EQ:
					ret = "eq";
					break;
				case OP_NE:
					ret = "ne";
					break;
				}
			} else {
				switch (op) {
				case OP_LT:
					ret = "slt";
					break;
				case OP_LE:
					ret = "sle";
					break;
				case OP_GT:
					ret = "sgt";
					break;
				case OP_GE:
					ret = "sge";
					break;
				case OP_EQ:
					ret = "eq";
					break;
				case OP_NE:
					ret = "ne";
					break;
				}
			}
		} else if (type != null) {
			if ((type.toTypeString().equals("double"))) {
				switch (op) {
				case OP_LT:
					ret = "olt";
					break;
				case OP_LE:
					ret = "ole";
					break;
				case OP_GT:
					ret = "ogt";
					break;
				case OP_GE:
					ret = "oge";
					break;
				case OP_EQ:
					ret = "eq";
					break;
				case OP_NE:
					ret = "ne";
					break;
				}
			} else {
				switch (op) {
				case OP_LT:
					ret = "slt";
					break;
				case OP_LE:
					ret = "sle";
					break;
				case OP_GT:
					ret = "sgt";
					break;
				case OP_GE:
					ret = "sge";
					break;
				case OP_EQ:
					ret = "eq";
					break;
				case OP_NE:
					ret = "ne";
					break;
				}
			}
		}

		return ret;
	}

	@Override
	public boolean checkTypes() {
		Type leftChild = (Type) this.getChild(0);
		Type rightChild = (Type) this.getChild(1);
		if (leftChild.toTypeString().equals(rightChild.toTypeString())) {
			return true;
		}
		throw new TypeErrorException("Operands have to be of same type!");
	}

	@Override
	public String toTypeString() {
		// if both operands are not equal, checkTypes will catch this
		return ((Type) this.getChild(0)).toTypeString();
	}

}
