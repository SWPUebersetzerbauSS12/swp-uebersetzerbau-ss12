package de.fuberlin.projectF.CodeGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.fuberlin.projectF.CodeGenerator.model.Token.Parameter;

import de.fuberlin.projectF.CodeGenerator.model.MMXRegisterAddress;
import de.fuberlin.projectF.CodeGenerator.model.RegisterAddress;
import de.fuberlin.projectF.CodeGenerator.model.Token;
import de.fuberlin.projectF.CodeGenerator.model.TokenType;
import de.fuberlin.projectF.CodeGenerator.model.Variable;

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
			MMXRegisterAddress mmxRes;
			MMXRegisterAddress mmxRes2;

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
					for (int i = tok.getParameterCount() - 1; i >= 0; i--) {
						Parameter p = tok.getParameter(i);
						mem.addStackVar(p.getOperand(), p.getType(), 8 + i
								* getSize(p.getType()));
					}
				}
				sectionText.append("\tenter $0, $0\n");
				break;

			case Return:
				// Kein Rückgabewert oder bereits in %eax
				if (tok.getTypeOp1().equals("void")
						|| mem.inReg(tok.getOp1(), 0))
					break;
				// Variable zurückgeben
				if (tok.getOp1().startsWith("%")) {
					if(tok.getTypeOp1().equals("double"))
						movsd(mem.getAddress(tok.getOp1()), "%xmm0", "Return value");
					else
						movl(mem.getAddress(tok.getOp1()), "%eax", "Return value");
					
				}
				// Fester Wert
				else {
					if(tok.getTypeOp1().equals("double"))
						movl("$" + tok.getOp1(), "%xmm0", "Return Value");
					else
						movl("$" + tok.getOp1(), "%eax", "Return Value");
				}
				break;

			case DefinitionEnd:
				sectionText.append("\tleave\n");
				sectionText.append("\tret\n\n");
				break;

			case Call:
				String function = tok.getOp1().substring(1);
				
				// Variablen, die nur in Registern sind, auf dem Stack speichern
				List<Variable> regVars = mem.getRegVariables(true);
				for (Variable var : regVars) {
					mem.regToStack(var);
					// Stackpointer verschieben
					subl("$" + String.valueOf(var.getSize()), "%esp", "Move var to stack");
				}
				// Alle Register sind nun frei und werden möglicherweise in der
				// Aufgerufenen Funktion verwendet.
				// Parameter auf den Stack legen
				if (tok.getParameterCount() > 0) {
					for (int i = tok.getParameterCount() - 1; i >= 0; i--) {
						Parameter p = tok.getParameter(i);
						String operand;
						System.out.println(p.getOperand());
						if (p.getOperand().startsWith("%"))
							operand = mem.getAddress(p.getOperand());
						else
							operand = "$" + p.getOperand();
						
						
						if (operand.charAt(1) == '@')
							operand = "$" + operand.substring(3);
						
						if(p.getType().equals("double"))
							pushl(mem.getAddress(p.getOperand(), 4), "Parameter " + p.getOperand());
						pushl(operand, "Parameter " + p.getOperand());
					}
				}
				// Funktionsaufruf
				call(function);

				// Rückgabe speichern
				if (tok.getTypeTarget().equals("i32")) {
					mem.addRegVar(tok.getTarget(), tok.getTypeTarget(), mem.getFreeRegister(0));
				}
				else if (tok.getTypeTarget().equals("double")) {
					System.out.println("Register " + tok.getTarget());
					mmxRes = new MMXRegisterAddress(0);
					System.out.println("To: " + mmxRes.getFullName());
					mem.addMMXRegVar(tok.getTarget(), tok.getTypeTarget(), mmxRes);
				}
				// Parameter löschen
				for (int i = 0; i < tok.getParameterCount(); i++) {
					Parameter p = tok.getParameter(i);
					addl("$" + String.valueOf(getSize(p.getType())), "%esp", "Dismiss Parameter");
				}
				break;

			case Allocation:
				// Array
				String tT = tok.getTypeTarget();
				if  (tT.startsWith("[")){
					// Extrahieren der Array-Größen
					ArrayList<Integer> numbers = new ArrayList<Integer>();
					Pattern p = Pattern.compile("(\\d+)(\\sx)");
					Matcher m = p.matcher(tT); 
					while (m.find()) {
					   numbers.add(new Integer(m.group(1)));
					}
					
					// Extrahieren des Typs
					p = Pattern.compile("(i)(\\d+)");
					m = p.matcher(tT);
					m.find();
					String type = m.group();
					System.out.println(type);
					
					// Länge berechnen
					int length = 1;
					for (Integer i : numbers) {
						length *= i;
					}
					Variable newArr = mem.newArrayVar(tok.getTarget(), type, length);
					subl("$" + String.valueOf(newArr.getSize()), "%esp",
							"Allocation " + tok.getTarget());
				}
				// Kein Arryay
				else{
				// Neue Variable anlegen
				Variable newVar = mem.newStackVar(tok.getTarget(),
						tT);
				// Stackpointer verschieben
				subl("$" + String.valueOf(newVar.getSize()), "%esp",
						"Allocation " + tok.getTarget());
				}
				break;

			case Assignment:
				String target = mem.getAddress(tok.getTarget());
				String source;
				// Zuweisung Variable
					
					// Zuweisung Zahl
					if (!tok.getOp1().startsWith("%")) {
						if(tok.getTypeTarget().equals("i32*"))
							movl("$" + tok.getOp1(), target, "Assignment " + tok.getTarget());
						else if(tok.getTypeTarget().equals("double*")) {
							String target2 = mem.getAddress(tok.getTarget(), +4);
							movl("$" + tok.getOp1().substring(0,10), target2, "Assignment " + tok.getTarget());
							movl("$0x" + tok.getOp1().substring(10), target, "Assignment " + tok.getTarget());
						}
					}
					
					// Variable (Stack -> Stack)
					else if (mem.onStack(tok.getOp1()) && mem.onStack(tok.getTarget())) {
						
						if(tok.getTypeOp1().equals("double")) {
							MMXRegisterAddress tmp = mem.getFreeMMXRegister();
							movsd(mem.getAddress(tok.getOp1()), tmp.getFullName(), "Copy assignment");
							movsd(tmp.getFullName(), target, tok.getTarget() + tok.getOp1());
						} else {
							RegisterAddress tmp = mem.getFreeRegister();
							movl(mem.getAddress(tok.getOp1()), tmp.getFullName(), "Copy assignment");
							movl(tmp.getFullName(), target, tok.getTarget() + tok.getOp1());
							mem.freeRegister(tmp);
						}
						// Variable
					} else {
						source = mem.getAddress(tok.getOp1());
						System.out.println("Source: " + source);
						if(tok.getTypeTarget().equals("double*"))
							movsd(source, target, "Assignment double " + tok.getTarget());
						else
							movl(source, target, "Assignment i32 " + tok.getTarget());
					}

				break;

			case Load:
				mem.newVirtualVar(tok.getTarget(), tok.getOp1());
				break;

			case ExpressionInt:
				res = mem.getFreeRegister();
				if (res == null) {
					if (!freeUnusedRegister(tokenNumber))
						System.out.println("Could'nt free register");
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

				movl(op1, res.getFullName(), "Expression");
				if (tok.getTypeTarget().equals("or"))
					orl(op2, res.getFullName(), tok.getOp1() + " + " + tok.getOp2());
				
				else if (tok.getTypeTarget().equals("and"))
					andl(op2, res.getFullName(), tok.getOp1() + " + " + tok.getOp2());
				
				else if (tok.getTypeTarget().equals("xor"))
					xorl(op2, res.getFullName(), tok.getOp1() + " + " + tok.getOp2());
				
				else if (tok.getTypeTarget().equals("add"))
					addl(op2, res.getFullName(), tok.getOp1() + " + " + tok.getOp2());
				
				else if (tok.getTypeTarget().equals("sub"))
					subl(op2, res.getFullName(), tok.getOp1() + " - " + tok.getOp2());
				
				else if (tok.getTypeTarget().equals("mul"))
					imull(op2, res.getFullName(), tok.getOp1() + " * " + tok.getOp2());
				
				else if (tok.getTypeTarget().equals("sdiv")) {
					if (!isRegisterFree(new RegisterAddress(0))) {
						System.out.println("Register eax is not free");
						saveRegisterValue(new RegisterAddress(0));
					}
					if (!isRegisterFree(new RegisterAddress(3))) {
						System.out.println("Register edx is not free");
						saveRegisterValue(new RegisterAddress(3));
					}
					movl(op1, new RegisterAddress(0).getFullName(), "");
					movl(new String("$0"),
							new RegisterAddress(3).getFullName(), "");

					idivl(op2);
					res = new RegisterAddress(0);
				}
				mem.addRegVar(tok.getTarget(), "i32*", res);
				break;

			case ExpressionDouble:

				if (tok.getOp1().startsWith("%"))
					op1 = mem.getAddress(tok.getOp1());
				else
					op1 = "$" + tok.getOp1();
				if (tok.getOp2().startsWith("%"))
					op2 = mem.getAddress(tok.getOp2());
				else
					op2 = "$" + tok.getOp2();

				//if(!op1.startsWith("%xmm")) {
					mmxRes = mem.getFreeMMXRegister();
					if (mmxRes == null) {
						if (!freeUnusedMMXRegister(tokenNumber)) {
							System.out.println("Could'nt free register");
						}
						mmxRes = mem.getFreeMMXRegister();
					}
					mem.addMMXRegVar(tok.getOp1(), tok.getTypeOp1(), mmxRes);
					movsd(op1, mmxRes.getFullName(), "Expression");
				//}
				
				mmxRes2 = mem.getFreeMMXRegister();
				if (mmxRes2 == null) {
					if (!freeUnusedMMXRegister(tokenNumber)) {
						System.out.println("Could'nt free register");
					}
					mmxRes2 = mem.getFreeMMXRegister();
				}
				movsd(op2, mmxRes2.getFullName(), "Expression");
				mem.addMMXRegVar(tok.getOp2(), tok.getTypeOp2(), mmxRes2);
				
				if (tok.getTypeTarget().equals("fadd"))
					addsd(mmxRes2.getFullName(), mmxRes.getFullName(), tok.getOp1() + " + " + tok.getOp2());
				else if (tok.getTypeTarget().equals("fsub"))
					subsd(mmxRes2.getFullName(), mmxRes.getFullName(), tok.getOp1() + " - " + tok.getOp2());
				else if (tok.getTypeTarget().equals("fmul"))
					mulsd(mmxRes2.getFullName(), mmxRes.getFullName(), tok.getOp1() + " * " + tok.getOp2());
				else if (tok.getTypeTarget().equals("fdiv")) {
					divsd(mmxRes2.getFullName(), mmxRes.getFullName(), tok.getOp1() + " * " + tok.getOp2());
				}
				
				mem.addMMXRegVar(tok.getTarget(), "double*", mmxRes);
				
				break;

			case Cast:
				if(tok.getTypeTarget().equals("i32") && tok.getTypeOp1().equals("double")) {
					System.out.println("Op1: " + tok.getOp1());
					op1 = mem.getAddress(tok.getOp1());
					System.out.println("Address:" + op1);
					System.out.println(op1);
					
					//TODO wenn wert noch nicht in mmx register
					if(!(op1.charAt(0) == '%')) {
						mmxRes = mem.getFreeMMXRegister();
						movss(op1, mmxRes.getFullName(), "Convert to single precision");
						op1 = mmxRes.getFullName();
					} else {
						cvtsd2ss(op1,op1, "Convert to single precision");
					}
					
					res = mem.getFreeRegister();
					if (res == null) {
						if (!freeUnusedRegister(tokenNumber)) {
							System.out.println("Could'nt free register");
						}
						res = mem.getFreeRegister();
					}
					
					cvttss2si(op1,res.getFullName(), "Convert to integer");
					
					mem.addRegVar(tok.getTarget(), "i32*", res);

					
				} else if (tok.getTypeTarget().equals("double") && tok.getTypeOp1().equals("i32")) {
					op1 = mem.getAddress(tok.getOp1());
					mmxRes = mem.getFreeMMXRegister();
					if (mmxRes == null) {
						if (!freeUnusedMMXRegister(tokenNumber)) {
							System.out.println("Could'nt free register");
						}
						mmxRes = mem.getFreeMMXRegister();
					}
					cvtsi2sd(op1, mmxRes.getFullName(), "Cast");
					mem.addMMXRegVar(tok.getTarget(), "double*", mmxRes);
				} else {
					System.out.println("Cast Error");
				}
					
				break;
			case Label:
				label(tok.getTarget());
				break;

			case Compare:
				res = mem.getFreeRegister();
				if (res == null) {
					if (!freeUnusedRegister(tokenNumber)) {
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
				if (tok.getOp1().isEmpty())
					jmp("label_" + tok.getOp2().substring(1));
				else {
					int result;
					result = findToken(tokenNumber, true, TokenType.Compare,
							null, null, null);
					System.out.println("last compare was in token #" + result
							+ " -> " + code.get(result).getTypeTarget());
					if (code.get(result).getTypeTarget().equals("eq")) {
						je("label_" + tok.getOp1().substring(1));
						jmp("label_" + tok.getOp2().substring(1));
					}
					if (code.get(result).getTypeTarget().equals("ne")) {
						jne("label_" + tok.getOp1().substring(1));
						jmp("label_" + tok.getOp2().substring(1));
					}
					if (code.get(result).getTypeTarget().equals("slt")) {
						jl("label_" + tok.getOp1().substring(1));
						jmp("label_" + tok.getOp2().substring(1));
					}
					if (code.get(result).getTypeTarget().equals("sgt")) {
						jg("label_" + tok.getOp1().substring(1));
						jmp("label_" + tok.getOp2().substring(1));
					}
					if (code.get(result).getTypeTarget().equals("sle")) {
						jle("label_" + tok.getOp1().substring(1));
						jmp("label_" + tok.getOp2().substring(1));
					}
					if (code.get(result).getTypeTarget().equals("sge")) {
						jge("label_" + tok.getOp1().substring(1));
						jmp("label_" + tok.getOp2().substring(1));
					}
				}
				break;

			case String:
				data(tok.getTarget().substring(2), ".ascii", tok.getOp1());

				System.out.println("Size: " + tok.getOp2());

				mem.addHeapVar(tok.getTarget(), 5);

			default:
				break;
			}
			tokenNumber++;
		}

		sectionText.append(".globl _start\n_start:\n\tcall main\n" + "\tpushl "
				+ "%eax" + "\n\tcall exit\n\n");
	}

	private void data(String label, String type, String value) {
		sectionData.append(label).append(":\t").append(type).append(" ")
				.append(value).append("\n");
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
		sectionText.append("\tcmpl ").append(source).append(", ")
				.append(target).append("\t\t\t#Label ").append("\n");
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
	
	private void movsd(String source, String target, String comment) {
		sectionText.append("\tmovsd ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	private void movss(String source, String target, String comment) {
		sectionText.append("\tmovss ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	private void cvtsi2sd(String source, String target, String comment) {
		sectionText.append("\tcvtsi2sd ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	private void cvtsd2ss(String source, String target, String comment) {
		sectionText.append("\tcvtsd2ss ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	private void cvttss2si(String source, String target, String comment) {
		sectionText.append("\tcvttss2si ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}

	private void orl(String source, String target, String comment) {
		sectionText.append("\torl ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	private void andl(String source, String target, String comment) {
		sectionText.append("\tandl ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	private void xorl(String source, String target, String comment) {
		sectionText.append("\txorl ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	private void idivl(String source) {
		sectionText.append("\tidivl ").append(source).append("\n");
	}

	private void imull(String source, String target, String comment) {
		sectionText.append("\timull ").append(source).append(", ")
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
	
	private void addsd(String source, String target, String comment) {
		sectionText.append("\taddsd ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	private void subsd(String source, String target, String comment) {
		sectionText.append("\tsubsd ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	private void mulsd(String source, String target, String comment) {
		sectionText.append("\tmulsd ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	private void divsd(String source, String target, String comment) {
		sectionText.append("\tdivsd ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}

	private boolean freeUnusedRegister(int tokenNumber) {
		boolean result = false;
		for (int i = 0; i < 6; i++) {
			Variable tmp = mem.getVarFromReg(i);
			if (findToken(tokenNumber, false, null, null, tmp.name, tmp.name) == 0) {
				mem.freeRegister(new RegisterAddress(i));
				result = true;
			}
		}
		return result;
	}
	
	private boolean freeUnusedMMXRegister(int tokenNumber) {
		boolean result = false;
		for (int i = 0; i < 6; i++) {
			Variable tmp = mem.getVarFromMMXReg(i);
			if (findToken(tokenNumber, false, null, null, tmp.name, tmp.name) == 0) {
				mem.freeMMXRegister(new MMXRegisterAddress(i));
				result = true;
			}
		}
		return result;
	}

	// TODO
	private boolean isRegisterFree(RegisterAddress res) {
		boolean result = true;

		return result;
	}

	// TODO
	private void saveRegisterValue(RegisterAddress res) {

	}

	private int findToken(int start, boolean backwards, TokenType type,
			String target, String op1, String op2) {
		for (int i = start; i < code.size() && i >= 0;) {
			if (type != null)
				if (code.get(i).getType() == type)
					return i;
			if (target != null)
				if (code.get(i).getTarget().equals(target))
					return i;
			if (op1 != null)
				if (code.get(i).getOp1().equals(op1))
					return i;
			if (op2 != null)
				if (code.get(i).getOp2().equals(op2))
					return i;
			if (backwards)
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
		if (type.equals("double"))
			return 8;
		else
			return 4;
	}

}
