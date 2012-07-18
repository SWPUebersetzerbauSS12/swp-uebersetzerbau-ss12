package de.fuberlin.projectF.CodeGenerator;

public abstract class Assembler {

	public StringBuffer sectionHead;
	public StringBuffer sectionData;
	public StringBuffer sectionText;
	
	public Assembler() {
		
	}
	
	public StringBuffer getSectionHead() {
		return sectionHead;
	}
	
	public StringBuffer getSectionData() {
		return sectionData;
	}
	
	public StringBuffer getSectionText() {
		return sectionText;
	}
	
	protected abstract String translate(String op);
	
	protected abstract void createEP();
	
	protected abstract void data(String label, String type, String value);

	protected abstract void declare(String name);
	
	protected abstract void funcDec(String name, String operand1, String operand2);
	
	protected abstract void funcEnd();
	
	protected abstract void je(String label);

	protected abstract void jne(String label);
	
	protected abstract void jl(String label);

	protected abstract void jg(String label);

	protected abstract void jle(String label);

	protected abstract void jge(String label);

	protected abstract void jmp(String label);

	protected abstract void cmp(String source, String target);

	protected abstract void label(String name);

	protected abstract void call(String name);

	protected abstract void push(String operand, String comment);
	
	protected abstract void lea(String source, String target, String string);

	protected abstract void mov(String source, String target, String comment);
	
	protected abstract void movsd(String source, String target, String comment);
	
	protected abstract void movss(String source, String target, String comment);
	
	protected abstract void cvtsi2sd(String source, String target, String comment);
	
	protected abstract void cvtsd2ss(String source, String target, String comment);
	
	protected abstract void cvttss2si(String source, String target, String comment);

	protected abstract void or(String source, String target, String comment);
	
	protected abstract void and(String source, String target, String comment);
	
	protected abstract void xor(String source, String target, String comment);
	
	protected abstract void idiv(String source);

	protected abstract void imul(String source, String target, String comment);

	protected abstract void sub(String source, String target, String comment);

	protected abstract void add(String source, String target, String comment);
	
	protected abstract void addsd(String source, String target, String comment);
	
	protected abstract void subsd(String source, String target, String comment);
	
	protected abstract void mulsd(String source, String target, String comment);
	
	protected abstract void divsd(String source, String target, String comment);
	
}
