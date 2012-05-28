package main;

import java.util.ArrayList;

import main.model.Token.Parameter;

import main.model.RegisterAddress;
import main.model.Token;
import main.model.TokenType;
import main.model.Variable;

public class Translator {
	private StringBuffer sectionData;
	private StringBuffer sectionText;
	private MemoryManager mem;
	ArrayList<Token> code;

	public Translator() {
		mem = new MemoryManager();
		sectionData = new StringBuffer().append(".section .data\n");
		sectionText = new StringBuffer().append(".section .text\n");
	}

	public void translate(ArrayList<Token> code) {
		this.code = code;
		int tokenNumber = 0;
		for (Token tok : code) {
			String op1, op2;
			RegisterAddress res;
			
			switch (tok.getType()) {
			case Definition:
				// Neuen Variablenkontext anlegen
				String name = tok.getTarget().substring(1);
				mem.newContext(name);
				mem.setContext(name);
				// Deklaration
				sectionText.append(".type ").append(name)
						.append(", @function\n").append(name).append(":\n");
				// Parameterbehandlung
				if (tok.getParameterCount() > 0) {
					for (int i = 0; i < tok.getParameterCount(); i++) {
						Parameter p = tok.getParameter(i);
						mem.addStackVar(p.getOperand(), p.getType(), 8 + i
								* getSize(p.getType()));
					}
				}
				sectionText.append("\tenter $0, $0\n");
				break;

			case Return:
				// Kein Rückgabewert
				if (tok.getTypeOp1().equals("void"))
					break;
				// Variable zurückgeben
				res = mem.getFreeRegister();
				if(res == null) {
					if(!freeUnusedRegister(tokenNumber)) {
						System.out.println("Could'nt free register");
					}
					res = mem.getFreeRegister();
				}
				mem.setReturnRegister(res);
				if (tok.getOp1().startsWith("%")) {
					
					movl(mem.getAddress(tok.getOp1()), res.getFullName(),
							"Return value");	
				}
				// Fester Wert
				else
					movl("$" + tok.getOp1(), res.getFullName(), "Return Value");
				break;

			case DefinitionEnd:
				sectionText.append("\tleave\n");
				sectionText.append("\tret\n\n");
				break;

			case Call:
				String function = tok.getOp1().substring(1);
				// Parameter auf den Stack legen
				if (tok.getParameterCount() > 0) {
					for (int i = 0; i < tok.getParameterCount(); i++) {
						Parameter p = tok.getParameter(i);
						String operand;
						if (p.getOperand().startsWith("%"))
							operand = mem.getAddress(p.getOperand());
						else
							operand = "$" + p.getOperand();
						pushl(operand, "Parameter " + p.getOperand());
					}
				}
				// Funktionsaufruf
				call(function);

				// Rückgabe speichern
				mem.addRegVar(tok.getTarget(), tok.getTypeTarget(),
						mem.getReturnRegister(function));
				// Parameter löschen
				for (int i = 0; i < tok.getParameterCount(); i++) {
					Parameter p = tok.getParameter(i);
					addl("$" + String.valueOf(getSize(p.getType())), "%esp",
							"Dismiss Parameter");
				}
				break;

			case Allocation:
				// Neue Variable anlegen
				Variable newVar = mem.newStackVar(tok.getTarget(),
						tok.getTypeTarget());
				// Stackpointer verschieben
				subl("$" + String.valueOf(newVar.getSize()), "%esp",
						"Allocation " + tok.getTarget());
				break;

			case Assignment:
				String target = mem.getAddress(tok.getTarget());
				String source;
				// Zuweisung Variable
				if (mem.onStack(tok.getOp1()) && mem.onStack(tok.getTarget())) {
					RegisterAddress tmp = mem.getFreeRegister();
					movl(mem.getAddress(tok.getOp1()), tmp.getFullName(),
							"Copy assignment");
					movl(tmp.getFullName(), target,
							tok.getTarget() + tok.getOp1());
					mem.freeRegister(tmp);
				} else if (tok.getOp1().startsWith("%")) {
					source = mem.getAddress(tok.getOp1());
					movl(source, target, "Assignment " + tok.getTarget());
				} else
					movl("$" + tok.getOp1(), target,
							"Assignment " + tok.getTarget());

				break;

			case Load:
				mem.newVirtualVar(tok.getTarget(), tok.getOp1());
				break;

			case Addition:
				res = mem.getFreeRegister();
				if(res == null) {
					if(!freeUnusedRegister(tokenNumber)) {
						System.out.println("Could'nt free register");
					}
					res = mem.getFreeRegister();
				}
				
				if (tok.getOp1().startsWith("%"))
					op1 = mem.getAddress(tok.getOp1());
				else
					op1 = "$" + tok.getOp1();
				if (tok.getOp2().startsWith("%"))
					op2 = mem.getAddress(tok.getOp2());
				else
					op2 = "$" + tok.getOp2();

				movl(op1, res.getFullName(), "Addition");
				addl(op2, res.getFullName(),
						tok.getOp1() + " + " + tok.getOp2());

				mem.addRegVar(tok.getTarget(), tok.getTypeTarget(), res);

				break;
				
			case Subtraction:
				res = mem.getFreeRegister();
				if(res == null) {
					if(!freeUnusedRegister(tokenNumber)) {
						System.out.println("Could'nt free register");
					}
					res = mem.getFreeRegister();
				}

				if (tok.getOp1().startsWith("%"))
					op1 = mem.getAddress(tok.getOp1());
				else
					op1 = "$" + tok.getOp1();
				if (tok.getOp2().startsWith("%"))
					op2 = mem.getAddress(tok.getOp2());
				else
					op2 = "$" + tok.getOp2();

				movl(op1, res.getFullName(), "Subtraction");
				subl(op2, res.getFullName(),
						tok.getOp1() + " + " + tok.getOp2());

				mem.addRegVar(tok.getTarget(), tok.getTypeTarget(), res);

				break;
				
			case Label:
				label(tok.getTarget());
				break;
			case Compare:
				res = mem.getFreeRegister();
				if(res == null) {
					if(!freeUnusedRegister(tokenNumber)) {
						System.out.println("Could'nt free register");
					}
					res = mem.getFreeRegister();
				}
				
				if (tok.getOp1().startsWith("%"))
					op1 = mem.getAddress(tok.getOp1());
				else
					op1 = "$" + tok.getOp1();
				if (tok.getOp2().startsWith("%"))
					op2 = mem.getAddress(tok.getOp2());
				else
					op2 = "$" + tok.getOp2();
				
				movl(op1, res.getFullName(), "Compare");
				cmpl(op2, res.getFullName());
				
				mem.addRegVar(tok.getOp1(), tok.getTypeOp1(), res);
				break;
			
			case Branch:
				if(tok.getOp1().isEmpty()) 
					jmp("label_" + tok.getOp2().substring(1));
				else {
					int result;
					result = findToken(tokenNumber, true, TokenType.Compare, null, null, null);
					System.out.println("last compare was in token #" + result + " -> " + code.get(result).getTypeTarget());
					if(code.get(result).getTypeTarget().equals("eq")) {
						je("label_" + tok.getOp1().substring(1));
						jmp("label_" + tok.getOp2().substring(1));
					}
					if(code.get(result).getTypeTarget().equals("ne")) {
						jne("label_" + tok.getOp1().substring(1));
						jmp("label_" + tok.getOp2().substring(1));
					}
					if(code.get(result).getTypeTarget().equals("slt")) {
						jl("label_" + tok.getOp1().substring(1));
						jmp("label_" + tok.getOp2().substring(1));
					}
					if(code.get(result).getTypeTarget().equals("sgt")) {
						jg("label_" + tok.getOp1().substring(1));
						jmp("label_" + tok.getOp2().substring(1));
					}
					if(code.get(result).getTypeTarget().equals("sle")) {
						jle("label_" + tok.getOp1().substring(1));
						jmp("label_" + tok.getOp2().substring(1));
					}
					if(code.get(result).getTypeTarget().equals("sge")) {
						jge("label_" + tok.getOp1().substring(1));
						jmp("label_" + tok.getOp2().substring(1));
					}
				}
				break;

			default:
				break;
			}
			tokenNumber++;
		}

		sectionText.append(".globl _start\n_start:\n\tcall main\n" + "\tpushl "
				+ mem.getReturnRegister("main").getFullName()
				+ "\n\tcall exit\n\n");
	}

	private void je(String label) {
		sectionText.append("\tje ").append(label).append("\n");
	}
	
	private void jne(String label) {
		sectionText.append("\tjne ").append(label).append("\n");
	}
	
	private void jl(String label) {
		sectionText.append("\tjl ").append(label).append("\n");
	}
	
	private void jg(String label) {
		sectionText.append("\tjg ").append(label).append("\n");
	}
	
	private void jle(String label) {
		sectionText.append("\tjle ").append(label).append("\n");
	}
	
	private void jge(String label) {
		sectionText.append("\tjge ").append(label).append("\n");
	}
	
	private void jmp(String label) {
		sectionText.append("\tjmp ").append(label).append("\n");
	}
	
	private void cmpl(String source, String target) {
		sectionText.append("\tcmpl ").append(source).append(", ").append(target).append("\t\t\t#Label ")
				.append("\n");
	}
	private void label(String name) {
		sectionText.append("label_").append(name).append(":\t\t\t#Label ")
				.append(name).append("\n");
	}
	
	private void call(String name) {
		sectionText.append("\tcall ").append(name).append("\t#Call ")
				.append(name).append("\n");
	}

	private void pushl(String operand, String comment) {
		sectionText.append("\tpushl ").append(operand).append("\t#")
				.append(comment).append("\n");
	}

	private void movl(String source, String target, String comment) {
		sectionText.append("\tmovl ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}

	private void subl(String source, String target, String comment) {
		sectionText.append("\tsubl ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}

	private void addl(String source, String target, String comment) {
		sectionText.append("\taddl ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	private boolean freeUnusedRegister(int tokenNumber) {
		boolean result = false;
		for(int i = 0; i < 6; i++) {
			Variable tmp = mem.getVarFromReg(i);
			if(findToken(tokenNumber, false, null, null, tmp.name, tmp.name) == 0) {
				mem.freeRegister(new RegisterAddress(i));
				result = true;
			}
		}
		return result;
	}
	
	private int findToken(int start, boolean backwards, TokenType type, String target, String op1, String op2) {
		for(int i = start; i < code.size() && i >= 0;) {
			if(type != null)
				if(code.get(i).getType() == type)
					return i;
			if(target != null)
				if(code.get(i).getTarget().equals(target))
					return i;
			if(op1 != null)
				if(code.get(i).getOp1().equals(op1))
					return i;
			if(op2 != null)
				if(code.get(i).getOp2().equals(op2))
					return i;
			if(backwards)
				i--;
			else
				i++;
		}
		return 0;
	}

	public String getCode() {
		return (sectionData.toString() + sectionText.toString());
	}

	public void print() {
		System.out.println("\nGenerated Code:");
		System.out.print(sectionData);
		System.out.print(sectionText);
		System.out.println();
	}

	public void addCode(String code) {
		sectionText.append(code + "\t#added extra\n");
	}

	private static int getSize(String type) {
		if (type.equals("i32"))
			return 4;
		else
			return 4;
	}

}
