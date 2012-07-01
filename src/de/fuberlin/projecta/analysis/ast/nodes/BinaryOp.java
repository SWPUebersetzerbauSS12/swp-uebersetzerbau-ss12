package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.commons.parser.ISyntaxTree;
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
		int regs[] = new int[5];
		Id a = null, b = null;
		if (op == TokenType.OP_EQ || op == TokenType.OP_NE
				|| op == TokenType.OP_LT || op == TokenType.OP_LE
				|| op == TokenType.OP_GT || op == TokenType.OP_GE) {
			// load value of id1 if it is an id!!!
			if (getChild(0) instanceof Id) {
				Id id = (Id) getChild(0);
				regs[3] = block.getNewMemory();
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
				regs[4] = block.getNewMemory();
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
				if ((a.getType().toTypeString().equals("double") || b.getType()
						.toTypeString().equals("double"))) {
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
				int tmp = block.getNewMemory();
				ret += "%" + tmp + " = " + int_or_real + " " + cmp_op + " ";
				ret += SymbolTableHelper.lookup(a.getValue(), this).getType()
						.genCode()
						+ " %";

				if (regs[3] == 0)
					ret += a.getValue() + ", %";
				else
					ret += regs[3] + ", %";
				if (regs[4] == 0)
					ret += b.getValue() + "\n";
				else
					ret += regs[4] + "\n";

			}
		} else if (op == TokenType.OP_ADD || op == TokenType.OP_MINUS
				|| op == TokenType.OP_DIV || op == TokenType.OP_MUL) {
			String type = "";
			String mathOp = "";
			int val1 = 0, val2 = 0; // used to store both values
			Id id1 = null, id2 = null;

			// the value must only be loaded if it is instanceof Id and if it is
			// not parameter!

			if (getChild(0) instanceof Id) {
				id1 = (Id) getChild(0);
				if (SymbolTableHelper.lookup(id1.getValue(), this).getType()
						.toTypeString().equals("double")) {
					mathOp = "f"; // append f in front of math_op
				}
			} else if (getChild(0) instanceof Type) {
				if (((Type) getChild(0)).toTypeString().equals("double")) {
					mathOp = "f"; // append f in front of math_op
				}
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

			if (getChild(0) instanceof Id) {
				id1 = (Id) getChild(0);
				if (!isInParams(id1)) {
					ret += LLVM.loadVar(id1);
				}

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
				id2 = (Id) getChild(1);
				if (!isInParams(id2)) {
					ret += LLVM.loadVar(id2);
				}
			}

			int val = block.getNewMemory();
			this.setValMemory(val); // save currents computation in this node
			if (id1 != null)
				val1 = id1.getValMemory();
			if (id2 != null)
				val2 = id2.getValMemory();
			if (id1 != null && id2 != null) {
				String v1 = val1 + "";
				String v2 = val2 + "";
				if (isInParams(id1))
					v1 = id1.getValue();
				if (isInParams(id2))
					v2 = id2.getValue();
				ret += "%" + val + " = " + mathOp + " " + type + " %" + v1
						+ ", %" + v2 + "\n";
			} else if (id1 == null && id2 == null) { // TODO!
				// both are types?
				Type t1 = (Type) getChild(0), t2 = (Type) getChild(1);
				String s1 = t1.genCode(), s2 = t2.genCode();
				String[] tmp1 = s1.split(" ");
				String[] tmp2 = s2.split(" ");
				type = tmp1[0];
				ret += "%" + val + " = " + mathOp + " " + type + " " + tmp1[1]
						+ ", " + tmp2[1] + "\n";
			} else if (id1 == null) {
				String v2 = val2 + "";
				if (isInParams(id2))
					v2 = id2.getValue();
				Type t1 = (Type) getChild(0);
				String s1 = t1.genCode();
				String[] tmp1 = s1.split(" ");
				type = tmp1[0];
				ret += "%" + val + " = " + mathOp + " " + type + " " + tmp1[1]
						+ ", %" + v2 + "\n";
			} else {
				// id2 === null
				String v1 = val1 + "";
				if (isInParams(id1))
					v1 = id1.getValue();
				Type t2 = (Type) getChild(1);
				String s2 = t2.genCode();
				String[] tmp2 = s2.split(" ");
				type = tmp2[0];
				ret += "%" + val + " = " + mathOp + " " + type + " %" + v1
						+ ", " + tmp2[1] + "\n";
			}

		} else if (op == TokenType.OP_ASSIGN) {
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
				int tempReg = block.getNewMemory();
				int tempReg2 = block.getNewMemory();
				int strLength = (str.getValue().length() + 1);
				ret = "%" + tempReg + " = alloca [" + strLength + " x i8]\n";
				ret += "store [" + strLength + " x i8] c\"" + str.getValue()
						+ "\\00\", [" + strLength + " x i8]* %" + tempReg
						+ "\n";
				ret += "%" + tempReg2 + " = getelementptr [" + strLength
						+ " x i8]* %" + tempReg + ", i8 0, i8 0 \n";
				ret += "store i8* %" + tempReg2 + ", i8** %" + a.getValue();
			} else if (getChild(1) instanceof FuncCall) {
				// load parameters of this function first
				if (getChild(1).getChildrenCount() > 1
						&& getChild(1).getChild(1).getChildrenCount() != 0) {
					ret += LLVM.loadParams((Args) getChild(1).getChild(1));
				}
				int reg = block.getNewMemory();
				String id = ((Id) getChild(0)).getValue();
				String type = SymbolTableHelper
						.lookup(((Id) getChild(0)).getValue(), this).getType()
						.genCode();
				ret += "%" + reg + " = " + ((FuncCall) getChild(1)).genCode()
						+ "\n";

				ret += "store " + type + " %" + reg + ", " + type + "* %" + id;
			} else if (getChild(1) instanceof Id) {
				String type1 = SymbolTableHelper
						.lookup(((Id) getChild(1)).getValue(), this).getType()
						.genCode();
				int reg = block.getNewMemory();
				ret = "%" + reg + " = load " + type1 + "* %"
						+ ((Id) getChild(1)).getValue() + "\n";

				String type0 = SymbolTableHelper
						.lookup(((Id) getChild(0)).getValue(), this).getType()
						.genCode();
				ret += "store " + type0 + " %" + reg + ", "
						+ eA.getType().genCode() + "* %" + a.getValue();
			} else if (getChild(1) instanceof BinaryOp) {
				// First execute operations, then save the result
				ret += ((BinaryOp) getChild(1)).genCode();
				// int result = block.getCurrentRegister();
				// ret += "%" + block.getNewMemory() + " = load "
				// + eA.getType().genCode() + "* %" + result + "\n";
				ret += "store " + eA.getType().genCode() + " %"
						+ ((BinaryOp) getChild(1)).getValMemory() + ", "
						+ eA.getType().genCode() + "* %" + a.getValue();
			} else {
				ret = "store " + ((AbstractSyntaxTree) getChild(1)).genCode()
						+ ", " + eA.getType().genCode() + "* %" + a.getValue();
			}
		} else {
			System.out.println("Unknown Binary OP: " + op);
		}
		/***********/

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

	private boolean isInParams(Id id) {
		FuncDef fDef = searchUpFuncDef(id);

		if (fDef != null) {
			if (fDef.getChild(2).getChildrenCount() > 0) {
				for (int i = 0; i < fDef.getChild(2).getChildrenCount(); i += 2) {
					Type typeO = (Type) fDef.getChild(2).getChild(i);
					Id idO = (Id) fDef.getChild(2).getChild(i + 1);
					if (idO.getValue().equals(id.getValue())
							&& typeO.equals(id.getType()))
						return true;
				}
			}
		}

		return false;
	}

	private FuncDef searchUpFuncDef(AbstractSyntaxTree node) {
		if (node.getParent() != null) {
			ISyntaxTree parent = node.getParent();
			while (parent != null) {
				if (parent instanceof FuncDef) {
					return (FuncDef) parent;
				}
				parent = parent.getParent();
			}
		}
		return null;
	}
}
