package de.fuberlin.projectF.CodeGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import de.fuberlin.projectF.CodeGenerator.model.Token.Parameter;

import de.fuberlin.projectF.CodeGenerator.model.Array;
import de.fuberlin.projectF.CodeGenerator.model.ArrayPointer;
import de.fuberlin.projectF.CodeGenerator.model.MMXRegisterAddress;
import de.fuberlin.projectF.CodeGenerator.model.Record;
import de.fuberlin.projectF.CodeGenerator.model.Reference;
import de.fuberlin.projectF.CodeGenerator.model.RegisterAddress;
import de.fuberlin.projectF.CodeGenerator.model.Token;
import de.fuberlin.projectF.CodeGenerator.model.TokenType;
import de.fuberlin.projectF.CodeGenerator.model.Variable;

public class Translator {
	
	private MemoryManager mem;
	protected Assembler asm;
	ArrayList<Token> code;

	public Translator(String assembler) {
		mem = new MemoryManager();
		if(assembler.equals("gnu"))
			asm = new GNUAssembler();
		else if(assembler.equals("intel"))
			asm = new IntelAssembler();
		else
			return;
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
			
			case Declare:
				asm.declare(tok.getTarget().substring(1));
				break;
			
			case Definition:
				// Neuen Variablenkontext anlegen
				String name = tok.getTarget().substring(1);
				mem.newContext(name);
				mem.setContext(name);
				// Deklaration
				
				asm.funcDec(name, "0", "0");
				
				// Parameterbehandlung
				if (tok.getParameterCount() > 0) {
					int stack = 8;
					for (int i = 0; i < tok.getParameterCount(); i++) {
						Parameter p = tok.getParameter(i);
						int size = getSize(p.getType());
						mem.addStackVar(p.getOperand(), p.getType(), stack + size);
						stack += size;
					}
				}
				
				break;

			case Return:
				// Kein Rückgabewert oder bereits in %eax
				if (tok.getTypeOp1().equals("void")
						|| mem.inReg(tok.getOp1(), 0))
					break;
				// Variable zurückgeben
				if (tok.getOp1().startsWith("%")) {
					if(tok.getTypeOp1().equals("double"))
						asm.movsd(mem.getAddress(tok.getOp1()), new MMXRegisterAddress(0).getFullName(), "Return value");
					else
						asm.mov(mem.getAddress(tok.getOp1()), new RegisterAddress(0).getFullName(), "Return value");
					
				}
				// Fester Wert
				else {
					if(tok.getTypeOp1().equals("double"))
						asm.mov(tok.getOp1(), new MMXRegisterAddress(0).getFullName(), "Return Value");
					else
						asm.mov(tok.getOp1(), new RegisterAddress(0).getFullName(), "Return Value");
				}
				break;

			case DefinitionEnd:
				asm.funcEnd();
				break;

			case Call:
				String function = tok.getOp1().substring(1);
				
				// Variablen, die nur in Registern sind, auf dem Stack speichern
				// TODO: MMX-Register sichern
				List<Variable> regVars = mem.getRegVariables(true);
				for (Variable var : regVars) {
					//TODO musste ich auskommentieren NULLPOINTEREXCEPTION bei mathStruct.llvm 
					//System.out.println("Var to stack: " + var.getName());
					//mem.regToStack(var);
					// Stackpointer verschieben
					//asm.sub(String.valueOf(var.getSize()), "esp", "Move var to stack");
				}
				// Alle Register sind nun frei und werden möglicherweise in der
				// Aufgerufenen Funktion verwendet.
				// Parameter auf den Stack legen
				if (tok.getParameterCount() > 0) {
					for (int i = tok.getParameterCount() - 1; i >= 0; i--) {
						Parameter p = tok.getParameter(i);
						String operand;
						if (p.getOperand().startsWith("%"))
							operand = mem.getAddress(p.getOperand());
						else
							operand = p.getOperand();
						
						if (operand.charAt(0) == '@')
							operand = mem.getContextName() + "." + operand.substring(2);
						if(p.getType().equals("double")) {
							if(p.getOperand().startsWith("%")) {
								asm.push(mem.getAddress(p.getOperand(), 4), "Parameter " + p.getOperand());
							} else {
								asm.push(p.getOperand().substring(0,10),"Parameter " + p.getOperand());
								operand = "0x" + p.getOperand().substring(10);
							}
						}
						asm.push(operand, "Parameter " + p.getOperand());
					}
				}
				// Funktionsaufruf
				asm.call(function);

				// Parameter löschen
				for (int i = 0; i < tok.getParameterCount(); i++) {
					Parameter p = tok.getParameter(i);
					asm.add(String.valueOf(getSize(p.getType())), "esp", "Dismiss Parameter");
				}
				
				// Rückgabe speichern
				if (tok.getTypeTarget().equals("i32")) {
					mem.addRegVar(tok.getTarget(), tok.getTypeTarget(), mem.getFreeRegister(0));
				}
				else if (tok.getTypeTarget().equals("double")) {
					mmxRes = new MMXRegisterAddress(0);
					mem.addMMXRegVar(tok.getTarget(), tok.getTypeTarget(), mmxRes);
					Variable tmp = mem.newStackVar(tok.getTarget(), tok.getTypeTarget());
					
					String addr = mem.getAddress(tmp.getName());
					asm.sub(String.valueOf(tmp.getSize()), "esp", "save return on stack");
					asm.movsd(mmxRes.getFullName(), addr, "save a copy");
					
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
					p = Pattern.compile("(i\\d+)|double");
					m = p.matcher(tT);
					m.find();
					String type = m.group();
					
					// Länge berechnen
					int length = 1;
					for (Integer i : numbers) {
						length *= i;
					}
					Array newArr = mem.newArray(tok.getTarget(), type, length);
					asm.sub(String.valueOf(newArr.getSize()), "esp",
							"Allocation " + tok.getTarget());
				}
				//Record
				else if(tT.startsWith("%")) {
					System.out.println("Allocation of a record");
					int result;
					
					result = findToken(0, false, TokenType.TypeDefinition, tok.getTypeTarget(), null, null);
					
					Record rec = createRecord(tok.getTarget(), code.get(result));
					
					asm.sub(String.valueOf(rec.getSize()), "esp",
							"Allocation " + tok.getTarget());
				}
				// Kein Array kein Record
				else{
					// Neue Variable anlegen
					Variable newVar = mem.newStackVar(tok.getTarget(),
							tT);
					// Stackpointer verschieben
					asm.sub(String.valueOf(newVar.getSize()), "esp",
							"Allocation " + tok.getTarget());
				}
				break;

			case Assignment:
				System.out.println("Target: " + tok.getTarget());
				String target = mem.getAddress(tok.getTarget());
				String source;
				// Zuweisung Variable
					// Zuweisung Zahl
					if (!tok.getOp1().startsWith("%")) {
						if(tok.getTypeTarget().equals("i32*"))
							asm.mov(tok.getOp1(), target, "Assignment " + tok.getTarget());
						else if(tok.getTypeTarget().equals("double*")) {
							System.out.println("Target: " + tok.getTarget());
							String target2 = mem.getAddress(tok.getTarget(), +4);
							System.out.println("Address" + target2);
							asm.mov(tok.getOp1().substring(0,10), target2, "Assignment " + tok.getTarget());
							asm.mov("0x" + tok.getOp1().substring(10), target, "Assignment " + tok.getTarget());
						}
					}
					
					// Variable (Stack -> Stack)
					else if (mem.onStack(tok.getOp1()) && mem.onStack(tok.getTarget())) {
						
						if(tok.getTypeOp1().equals("double")) {
							MMXRegisterAddress tmp = mem.getFreeMMXRegister();
							asm.movsd(mem.getAddress(tok.getOp1()), tmp.getFullName(), "Copy assignment");
							asm.movsd(tmp.getFullName(), target, tok.getTarget() + tok.getOp1());
						} else {
							RegisterAddress tmp = mem.getFreeRegister();
							asm.mov(mem.getAddress(tok.getOp1()), tmp.getFullName(), "Copy assignment");
							asm.mov(tmp.getFullName(), target, tok.getTarget() + tok.getOp1());
							mem.freeRegister(tmp);
						}
						// Variable
					} else {
						source = mem.getAddress(tok.getOp1());
						if(tok.getTypeTarget().equals("double*"))
							asm.movsd(source, target, "Assignment double " + tok.getTarget());
						else
							asm.mov(source, target, "Assignment i32 " + tok.getTarget());
					}

				break;

			case Load:
				mem.newReference(tok.getTarget(), tok.getOp1());
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
					op1 = tok.getOp1();
				if (tok.getOp2().startsWith("%"))
					op2 = mem.getAddress(tok.getOp2());
				else
					op2 = tok.getOp2();

				asm.mov(op1, res.getFullName(), "Expression");
				if (tok.getTypeTarget().equals("or"))
					asm.or(op2, res.getFullName(), tok.getOp1() + " + " + tok.getOp2());
				
				else if (tok.getTypeTarget().equals("and"))
					asm.and(op2, res.getFullName(), tok.getOp1() + " + " + tok.getOp2());
				
				else if (tok.getTypeTarget().equals("xor"))
					asm.xor(op2, res.getFullName(), tok.getOp1() + " + " + tok.getOp2());
				
				else if (tok.getTypeTarget().equals("add"))
					asm.add(op2, res.getFullName(), tok.getOp1() + " + " + tok.getOp2());
				
				else if (tok.getTypeTarget().equals("sub"))
					asm.sub(op2, res.getFullName(), tok.getOp1() + " - " + tok.getOp2());
				
				else if (tok.getTypeTarget().equals("mul"))
					asm.imul(op2, res.getFullName(), tok.getOp1() + " * " + tok.getOp2());
				
				else if (tok.getTypeTarget().equals("sdiv")) {
					if (!isRegisterFree(new RegisterAddress(0))) {
						System.out.println("Register eax is not free");
						saveRegisterValue(new RegisterAddress(0));
					}
					if (!isRegisterFree(new RegisterAddress(3))) {
						System.out.println("Register edx is not free");
						saveRegisterValue(new RegisterAddress(3));
					}
					asm.mov(op1, new RegisterAddress(0).getFullName(), "");
					asm.mov(new String("0"),
							new RegisterAddress(3).getFullName(), "");

					asm.idiv(op2);
					res = new RegisterAddress(0);
				}
				mem.addRegVar(tok.getTarget(), "i32*", res);
				break;

			case ExpressionDouble:

				if (tok.getOp1().startsWith("%"))
					op1 = mem.getAddress(tok.getOp1());
				else {
					op1 = tok.getOp1();
					Variable tmp = mem.newStackVar(tok.getTarget(), "double");
					String addr = mem.getAddress(tmp.getName());
					asm.sub(String.valueOf(tmp.getSize()), "esp", "");
					asm.mov("0x" + op1.substring(10), addr, "save a copy");
					addr = mem.getAddress(tmp.getName(),4);
					asm.mov(op1.substring(0,10), addr, "save a copy");
					op1 = mem.getAddress(tmp.getName());
					
				}
				if (tok.getOp2().startsWith("%"))
					op2 = mem.getAddress(tok.getOp2());
				else {
					op2 = tok.getOp2();
					Variable tmp = mem.newStackVar(tok.getTarget(), "double");
					String addr = mem.getAddress(tmp.getName());
					asm.sub(String.valueOf(tmp.getSize()), "esp", "");
					asm.mov("0x" + op2.substring(10), addr, "save a copy");
					addr = mem.getAddress(tmp.getName(),4);
					asm.mov(op2.substring(0,10), addr, "save a copy");
					op2 = mem.getAddress(tmp.getName());
				}

				mmxRes = mem.getFreeMMXRegister();
				if (mmxRes == null) {
					if (!freeUnusedMMXRegister(tokenNumber)) {
						System.out.println("Could'nt free register");
					}
					mmxRes = mem.getFreeMMXRegister();
				}
				mem.addMMXRegVar(tok.getOp1(), tok.getTypeOp1(), mmxRes);
				asm.movsd(op1, mmxRes.getFullName(), "Expression");
				
				mmxRes2 = mem.getFreeMMXRegister();
				if (mmxRes2 == null) {
					if (!freeUnusedMMXRegister(tokenNumber)) {
						System.out.println("Could'nt free register");
					}
					mmxRes2 = mem.getFreeMMXRegister();
				}
				asm.movsd(op2, mmxRes2.getFullName(), "Expression");
				mem.addMMXRegVar(tok.getOp2(), tok.getTypeOp2(), mmxRes2);
				
				if (tok.getTypeTarget().equals("fadd"))
					asm.addsd(mmxRes2.getFullName(), mmxRes.getFullName(), tok.getOp1() + " + " + tok.getOp2());
				else if (tok.getTypeTarget().equals("fsub"))
					asm.subsd(mmxRes2.getFullName(), mmxRes.getFullName(), tok.getOp1() + " - " + tok.getOp2());
				else if (tok.getTypeTarget().equals("fmul"))
					asm.mulsd(mmxRes2.getFullName(), mmxRes.getFullName(), tok.getOp1() + " * " + tok.getOp2());
				else if (tok.getTypeTarget().equals("fdiv")) {
					asm.divsd(mmxRes2.getFullName(), mmxRes.getFullName(), tok.getOp1() + " * " + tok.getOp2());
				}
				
				mem.addMMXRegVar(tok.getTarget(), "double*", mmxRes);
				
				break;

			case Cast:
				if(tok.getTypeTarget().equals("i32") && tok.getTypeOp1().equals("double")) {
					op1 = mem.getAddress(tok.getOp1());
					
					if(!mem.inMMXReg(tok.getOp1())) {
						mmxRes = mem.getFreeMMXRegister();
						asm.movss(op1, mmxRes.getFullName(), "Convert to single precision");
						op1 = mmxRes.getFullName();
					} else {
						asm.cvtsd2ss(op1,op1, "Convert to single precision");
					}
					
					res = mem.getFreeRegister();
					if (res == null) {
						if (!freeUnusedRegister(tokenNumber)) {
							System.out.println("Could'nt free register");
						}
						res = mem.getFreeRegister();
					}
					
					asm.cvttss2si(op1,res.getFullName(), "Convert to integer");
					
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
					asm.cvtsi2sd(op1, mmxRes.getFullName(), "Cast");
					mem.addMMXRegVar(tok.getTarget(), "double*", mmxRes);
				} else {
					System.out.println("Cast Error");
				}
					
				break;
			case Label:
				asm.label(tok.getTarget());
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
					op1 = tok.getOp1();
				if (tok.getOp2().startsWith("%"))
					op2 = mem.getAddress(tok.getOp2());
				else
					op2 = tok.getOp2();

				asm.mov(op1, res.getFullName(), "Compare");
				asm.cmp(op2, res.getFullName());

				mem.addRegVar(tok.getOp1(), tok.getTypeOp1(), res);
				break;

			case Branch:
				if (tok.getOp1().isEmpty())
					asm.jmp("label_" + tok.getOp2().substring(1));
				else {
					int result;
					result = findToken(tokenNumber, true, TokenType.Compare,
							null, null, null);

					if (code.get(result).getTypeTarget().equals("eq")) {
						asm.je("label_" + tok.getOp1().substring(1));
						asm.jmp("label_" + tok.getOp2().substring(1));
					}
					if (code.get(result).getTypeTarget().equals("ne")) {
						asm.jne("label_" + tok.getOp1().substring(1));
						asm.jmp("label_" + tok.getOp2().substring(1));
					}
					if (code.get(result).getTypeTarget().equals("slt")) {
						asm.jl("label_" + tok.getOp1().substring(1));
						asm.jmp("label_" + tok.getOp2().substring(1));
					}
					if (code.get(result).getTypeTarget().equals("sgt")) {
						asm.jg("label_" + tok.getOp1().substring(1));
						asm.jmp("label_" + tok.getOp2().substring(1));
					}
					if (code.get(result).getTypeTarget().equals("sle")) {
						asm.jle("label_" + tok.getOp1().substring(1));
						asm.jmp("label_" + tok.getOp2().substring(1));
					}
					if (code.get(result).getTypeTarget().equals("sge")) {
						asm.jge("label_" + tok.getOp1().substring(1));
						asm.jmp("label_" + tok.getOp2().substring(1));
					}
				}
				break;

			case String:
				mem.addGlobalVar(tok.getTarget().substring(2));
				String tmp = mem.getAddress(tok.getTarget().substring(2));
				asm.data(tmp, ".ascii", tok.getOp1());
				break;
				
			case Getelementptr:
				if(tok.getTypeTarget().charAt(0) == '%') {
					//TODO :-)
					System.out.println("New RecordPointer: " + tok.getTarget() + " " + tok.getOp1() + " " + tok.getOp2());
					mem.newRecordPtr(tok.getTarget(), tok.getOp1(), tok.getOp2());
				} else if(tok.getOp1().charAt(0) == '@') {
					mem.newReference(tok.getTarget(), tok.getOp1().substring(2));
				} else {
					// Continue array pointer
					if (tok.getOp1().matches("%\\d+")) {
						String offset = tok.getOp2();
						ArrayPointer newPtr = mem.contArrayPtr(tok.getTarget(), tok.getOp1(), tok.getOp2(), tok.getTypeTarget());
						RegisterAddress temp = mem.getFreeRegister();
						
						if (offset.startsWith("%")){
							offset = mem.getAddress(offset);
						}
						
						asm.mov("" + newPtr.getValue(), temp.getFullName(), "ArrayPointer " + tok.getTarget());
						asm.imul("" + newPtr.getArray().getTypeSize(), temp.getFullName(), "Calculating offset");
						asm.imul(offset, temp.getFullName(), "");
						
						asm.add(temp.getFullName(), newPtr.getPtrAddress(), "Adding to last pointer");
						
						mem.freeRegister(temp);
					}
					// New array pointer
					else{
						RegisterAddress reg = mem.getFreeRegister();
						String offset = tok.getOp2();
						
						ArrayPointer newPtr = mem.newArrayPtr(tok.getTarget(), tok.getOp1(), tok.getTypeTarget(), reg);
						
						if (offset.startsWith("%")){
							offset = mem.getAddress(offset);
						}
						Array array = mem.getArray(tok.getOp1());
						String ptrName = tok.getTarget();
						
						asm.mov("" + newPtr.getValue(), reg.getFullName(), "ArrayPointer " + ptrName);
						asm.imul("" + array.getTypeSize(), reg.getFullName(), "Calculating offset");
						asm.imul(offset, reg.getFullName(), "");
						
						RegisterAddress temp = mem.getFreeRegister();
						asm.lea(array.getAddress(), temp.getFullName(), "Load array address");
						asm.add(temp.getFullName(), reg.getFullName(), "Add array address");
						mem.freeRegister(temp);						
					}
				}
				break;

			default:
				break;
			}
			tokenNumber++;
		}

		//erstelle Einstiegspunkt
		asm.createEP();
	}

	

	private Record createRecord(String name, Token tok) {
		Record rec = new Record(name);
		System.out.println("Create Record: " + name);
		for( int i = 0; i < tok.getParameterCount(); i++) {
			String type = tok.getParameter(i).getType();
			if(type.charAt(0) == '%') {
				int result = findToken(0, false, TokenType.TypeDefinition, type, null, null);
				Record tmp = createRecord(String.valueOf(i), code.get(result));
				rec.add(tmp);
			} else {
				rec.add(mem.newStackVar(String.valueOf(i),type));
			}
		}
		
		mem.newRecord(rec);
		return rec;
	}

	private boolean freeUnusedRegister(int tokenNumber) {
		boolean result = false;
		HashMap<RegisterAddress, Reference> usedRegisters = mem.getUsedRegisters();
		for (RegisterAddress k : usedRegisters.keySet()) {
			Reference v = usedRegisters.get(k);
			if (findToken(tokenNumber, false, null, null, v.getName(), v.getName()) == 0) {
				mem.freeRegister(k);
				result = true;
			}
		}
		return result;
	}
	
	private boolean freeUnusedMMXRegister(int tokenNumber) {
		boolean result = false;
		for (int i = 0; i < 6; i++) {
			Variable tmp = mem.getVarFromMMXReg(i);
			if (findToken(tokenNumber, false, null, null, tmp.getName(), tmp.getName()) == 0) {
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
			if (code.get(i).getType() == type || type == null)
				if (code.get(i).getTarget().equals(target) || target == null)
					if (code.get(i).getOp1().equals(op1) || op1 == null)
						if (code.get(i).getOp2().equals(op2) || op2 == null)
							return i;
			if (backwards)
				i--;
			else
				i++;
		}
		return 0;
	}

	public String getCode() {
		return (asm.getSectionHead() + asm.getSectionData().toString() + asm.getSectionText().toString());
	}

	public void print() {
		System.out.println("\nGenerated Code:");
		System.out.print(asm.getSectionHead());
		System.out.print(asm.getSectionData());
		System.out.print(asm.getSectionText());
		System.out.println();
	}

	private static int getSize(String type) {
		if (type.equals("double"))
			return 8;
		else
			return 4;
	}

}
