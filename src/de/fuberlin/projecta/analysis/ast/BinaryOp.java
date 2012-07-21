package de.fuberlin.projecta.analysis.ast;

import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.analysis.TypeChecker;
import de.fuberlin.projecta.codegen.LLVM;

public class BinaryOp extends Expression {

	TokenType op;

	public TokenType getOp() {
		return op;
	}

	public BinaryOp(TokenType op) {
		this.op = op;
	}

	@Override
	public void checkSemantics() {
		switch (this.getOp()) {
		// TODO: think if you can find other cases, where semantics can be
		// wrong/ambiguous
		case OP_ASSIGN:
			if (!((this.getChild(0) instanceof Id) || this.getChild(0) instanceof RecordVarCall || this.getChild(0) instanceof ArrayCall)) {
				throw new SemanticException(
						"Left side of an assignment has to be an identifier, but is "
								+ this.getChild(0).toString(), this);
			}
			if (this.getChild(1) instanceof BinaryOp
					&& (((BinaryOp) this.getChild(1)).getOp() == TokenType.OP_ASSIGN)) {
				throw new SemanticException(
						"Left side of an assignment cannot be an assignment.", this);
			}
			break;
		case OP_DIV:
			if (this.getChild(1) instanceof IntLiteral) {
				int v = ((IntLiteral) this.getChild(1)).getValue();
				if (v == 0)
					throw new SemanticException("Division by Zero!", this);
			}
			else if (this.getChild(1) instanceof RealLiteral) {
				double v = ((RealLiteral) this.getChild(1)).getValue();
				if (v == 0.0)
					throw new SemanticException("Division by Zero!", this);
			}
		}

		// check all children, may throw
		for (int i = 0; i < this.getChildrenCount(); i++) {
			((AbstractSyntaxTree)this.getChild(i)).checkSemantics();
		}
	}

	@Override
	public String genCode() {
		String ret = "";
		Block block = getHighestBlock();
		Expression t1 = (Expression) getLeftSide();
		Expression t2 = (Expression) getRightSide();

		// comparison operators
		if (op == TokenType.OP_EQ || op == TokenType.OP_NE
				|| op == TokenType.OP_LT || op == TokenType.OP_LE
				|| op == TokenType.OP_GT || op == TokenType.OP_GE) {

			// load both values into new memory addresses
			ret += LLVM.loadType(t1);
			ret += LLVM.loadType(t2);

			int mem = block.getNewVar();
			this.setValMemory(mem);
			ret += "%" + mem + " = " + getIntOrReal(t1) + " " + getOpName(t1)
					+ " " + t1.fromTypeStringToLLVMType() + " %";
			ret += LLVM.getMem(t1) + ", %";
			ret += LLVM.getMem(t2) + "\n";
		}
		// arithmetic operators
		else if (op == TokenType.OP_ADD || op == TokenType.OP_MINUS
				|| op == TokenType.OP_DIV || op == TokenType.OP_MUL) {
			String type = "";
			String mathOp = getMathOpName(t1);

			// load both values into new memory addresses
			ret += LLVM.loadType(t1);
			ret += LLVM.loadType(t2);

			int val = block.getNewVar();
			this.setValMemory(val); // save currents computation in this node

			type = t1.fromTypeStringToLLVMType();
			ret += "%" + val + " = " + mathOp + " " + type + " %"
					+ LLVM.getMem(t1) + ", %" + LLVM.getMem(t2) + "\n";
		}
		// assignment operator
		else if (op == TokenType.OP_ASSIGN) {
			Id id1 = null;
			if (t1 instanceof RecordVarCall)
				id1 = ((RecordVarCall) t1).getVarId();
			else if(t1 instanceof ArrayCall)
				id1 = ((ArrayCall) t1).getVarId();
			else
				id1 = (Id)t1;

			if (getChild(1) instanceof StringLiteral) {
				
				StringLiteral str = (StringLiteral) getChild(1);
				/*
				 * if you look below, this is what is implemented (it's kind of
				 * sick) ;basic principle for saving a string to a char pointer
				 * %str3 %r3 = alloca [9 x i8] store [9 x i8] c"test1234\00", [9
				 * x i8]* %r3 %firstEl = getelementptr [9 x i8]* %r3, i8 0, i8 0
				 * store i8* %firstEl, i8** %str3
				 */
				int tmp1 = block.getNewVar();
				int tmp2 = block.getNewVar();
				int strLength = (str.getValue().length() + 1);
				ret = "%" + tmp1 + " = alloca [" + strLength + " x i8]\n";
				ret += "store [" + strLength + " x i8] c\"" + str.getValue()
						+ "\\00\", [" + strLength + " x i8]* %" + tmp1 + "\n";
				ret += "%" + tmp2 + " = getelementptr [" + strLength
						+ " x i8]* %" + tmp1 + ", i8 0, i8 0 \n";
				ret += "store i8* %" + tmp2 + ", i8** %" + id1.getValue();
			} else if (t1 instanceof RecordVarCall) {
				ret += LLVM.getRecordVarCallPointer((RecordVarCall)t1);
				ret += LLVM.loadType(t2);
				String t = t2.fromTypeStringToLLVMType();
				ret += "store " + t + " %" + LLVM.getMem(t2) + ", " + t + "* "
						+ "%" + LLVM.getMem(t1);
			} else if(t1 instanceof ArrayCall){
				ret += LLVM.getArrayCallPointer((ArrayCall) t1);
				ret += LLVM.loadType(t2);
				String t = t2.fromTypeStringToLLVMType();
				ret += "store " + t + " %" + LLVM.getMem(t2) + ", " + t + "* %" + LLVM.getMem(t1);
			} else {
				ret += LLVM.loadType(t2);
				String t = t2.fromTypeStringToLLVMType();
				ret += "store " + t + " %" + LLVM.getMem(t2) + ", " + t + "* "
						+ "%" + id1.getValue();
			}
		} else {
			System.out.println("Unknown Binary OP: " + op);
		}
		return ret;
	}

	private String getIntOrReal(Expression expr) {
		String ret = "";
		if (expr != null) {
			if ((expr.fromTypeStringToLLVMType()).equals("i64")) {
				ret = "fcmp";
			} else {
				ret = "icmp";
			}
		}

		return ret;
	}

	private AbstractSyntaxTree getLeftSide() {
		return (AbstractSyntaxTree)getChild(0);
	}

	private AbstractSyntaxTree getRightSide() {
		return (AbstractSyntaxTree)getChild(1);
	}

	private String getMathOpName(Expression expr) {
		String mathOp = "";
		if ((expr.fromTypeStringToLLVMType()).equals("i64"))
			mathOp = "f"; // append f in front of math_op

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
		default:
			assert(false); // should never happen!
		}
		if(expr.toTypeString().equals(BasicType.TYPE_REAL_STRING)){
			return "f" + mathOp;
		}
		return mathOp;
	}

	private String getOpName(Expression expr) {
		String ret = "";

		if (expr != null) {
			if ((expr.toTypeString().equals("double"))) {
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
	public void checkTypes() {
		Expression leftChild = (Expression)getLeftSide();
		Expression rightChild = (Expression)getRightSide();
		String leftTypeString = leftChild.toTypeString();
		String rightTypeString = rightChild.toTypeString();
		if (leftTypeString.equals(rightTypeString)) {
			switch (this.op) {
			case OP_ADD:
			case OP_MUL:
			case OP_DIV:
			case OP_MINUS:
				if (!TypeChecker.isNumeric(leftTypeString)
						|| !TypeChecker.isNumeric(rightTypeString)) {
					throw new SemanticException(
							"Can only perform arithmetics operations on numeric types", this);
				}
			case OP_AND:
			case OP_OR:
			case OP_NOT:
			case OP_LT:
			case OP_LE:
			case OP_EQ:
			case OP_GE:
			case OP_GT:
			case OP_NE:
			case OP_ASSIGN:
				return;
			default:
				throw new SemanticException("Undefined error in BinaryOp", this);
			}
		}
		throw new SemanticException(
				"Operands have to be of same type but are:\n Left operand: "
						+ leftTypeString + "\nRight operand: "
						+ rightTypeString, this);
	}

	@Override
	public String toTypeString() {
		// if both operands are not equal, checkTypes will catch this
		switch (this.op) {
		case OP_ADD:
		case OP_MUL:
		case OP_DIV:
		case OP_MINUS:
			return ((Expression) this.getChild(0)).toTypeString();
		case OP_AND:
		case OP_OR:
		case OP_LT:
		case OP_LE:
		case OP_EQ:
		case OP_GE:
		case OP_GT:
		case OP_NE:
		case OP_NOT:
			return BasicType.TYPE_BOOL_STRING;
		default:
			return BasicType.TYPE_VOID_STRING;
		}
	}

}
