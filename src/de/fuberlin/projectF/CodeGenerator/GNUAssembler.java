package de.fuberlin.projectF.CodeGenerator;

public class GNUAssembler extends Assembler {
	
	public GNUAssembler() {
		super();
		sectionData = new StringBuffer().append(".section .data\n");
		sectionText = new StringBuffer().append(".section .text\n");
	}
	
	protected void createEP() {
	sectionText.append(".globl _start\n_start:\n\tcall main\n" + "\tpushl "
			+ "%eax" + "\n\tcall exit\n\n");
	}
	
	protected void data(String label, String type, String value) {
		sectionData.append(label).append(":\t").append(type).append(" ")
				.append(value).append("\n");
	}

	protected void funcDec(String name) {
		sectionText.append(".type ").append(name)
		.append(", @function\n").append(name).append(":\n");
		sectionText.append("\tenter $0, $0\n");
	}
	protected void funcEnd() {
		sectionText.append("\tleave\n");
		sectionText.append("\tret\n\n");
	}
	
	protected void je(String label) {
		sectionText.append("\tje ").append(label).append("\n");
	}

	protected void jne(String label) {
		sectionText.append("\tjne ").append(label).append("\n");
	}

	protected void jl(String label) {
		sectionText.append("\tjl ").append(label).append("\n");
	}

	protected void jg(String label) {
		sectionText.append("\tjg ").append(label).append("\n");
	}

	protected void jle(String label) {
		sectionText.append("\tjle ").append(label).append("\n");
	}

	protected void jge(String label) {
		sectionText.append("\tjge ").append(label).append("\n");
	}

	protected void jmp(String label) {
		sectionText.append("\tjmp ").append(label).append("\n");
	}

	protected void cmpl(String source, String target) {
		sectionText.append("\tcmpl ").append(source).append(", ")
				.append(target).append("\t\t\t#Label ").append("\n");
	}

	protected void label(String name) {
		sectionText.append("label_").append(name).append(":\t\t\t#Label ")
				.append(name).append("\n");
	}

	protected void call(String name) {
		sectionText.append("\tcall ").append(name).append("\t#Call ")
				.append(name).append("\n");
	}

	protected void pushl(String operand, String comment) {
		sectionText.append("\tpushl ").append(operand).append("\t#")
				.append(comment).append("\n");
	}

	protected void movl(String source, String target, String comment) {
		sectionText.append("\tmovl ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void movsd(String source, String target, String comment) {
		sectionText.append("\tmovsd ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void movss(String source, String target, String comment) {
		sectionText.append("\tmovss ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void cvtsi2sd(String source, String target, String comment) {
		sectionText.append("\tcvtsi2sd ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void cvtsd2ss(String source, String target, String comment) {
		sectionText.append("\tcvtsd2ss ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void cvttss2si(String source, String target, String comment) {
		sectionText.append("\tcvttss2si ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}

	protected void orl(String source, String target, String comment) {
		sectionText.append("\torl ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void andl(String source, String target, String comment) {
		sectionText.append("\tandl ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void xorl(String source, String target, String comment) {
		sectionText.append("\txorl ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void idivl(String source) {
		sectionText.append("\tidivl ").append(source).append("\n");
	}

	protected void imull(String source, String target, String comment) {
		sectionText.append("\timull ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}

	protected void subl(String source, String target, String comment) {
		sectionText.append("\tsubl ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}

	protected void addl(String source, String target, String comment) {
		sectionText.append("\taddl ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void addsd(String source, String target, String comment) {
		sectionText.append("\taddsd ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void subsd(String source, String target, String comment) {
		sectionText.append("\tsubsd ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void mulsd(String source, String target, String comment) {
		sectionText.append("\tmulsd ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void divsd(String source, String target, String comment) {
		sectionText.append("\tdivsd ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
}
