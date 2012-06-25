package de.fuberlin.projectF.CodeGenerator;

public class GNUAssembler extends Assembler {
	
	public GNUAssembler() {
		super();
		sectionHead = new StringBuffer().append("");
		sectionData = new StringBuffer().append(".section .data\n");
		sectionText = new StringBuffer().append(".section .text\n");
	}
	
	protected void createEP() {
	sectionText.append(".globl _start\n_start:\n\tcall main\n" + "\tpushl "
			+ "%eax" + "\n\tcall exit\n\n");
	}
	
	protected void data(String label, String type, String value) {
		value= requote(value);
		
		sectionData.append(label).append(":\t").append(type).append(" ")
				.append(value).append("\n");
		
	}
	
	protected void declare(String name) {}

	protected void funcDec(String name, String operand1, String operand2) {
		operand1 = translate(operand1);
		operand2 = translate(operand2);
		sectionText.append(".type ").append(name)
		.append(", @function\n").append(name).append(":\n");
		sectionText.append("\tenter " + operand1 + ", " + operand2 + "\n");
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

	protected void cmp(String source, String target) {
		source = translate(source);
		target = translate(target);
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

	protected void push(String operand, String comment) {
		operand = translate(operand);
		sectionText.append("\tpushl ").append(operand).append("\t#")
				.append(comment).append("\n");
	}

	protected void mov(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\tmovl ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void movsd(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\tmovsd ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void movss(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\tmovss ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void cvtsi2sd(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\tcvtsi2sd ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void cvtsd2ss(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\tcvtsd2ss ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void cvttss2si(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\tcvttss2si ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}

	protected void or(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\torl ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void and(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\tandl ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void xor(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\txorl ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void idiv(String source) {
		source = translate(source);
		sectionText.append("\tidivl ").append(source).append("\n");
	}

	protected void imul(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\timull ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}

	protected void sub(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\tsubl ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}

	protected void add(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\taddl ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void addsd(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\taddsd ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void subsd(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\tsubsd ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void mulsd(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\tmulsd ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected void divsd(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\tdivsd ").append(source).append(", ")
				.append(target).append("\t#").append(comment).append("\n");
	}
	
	protected String translate(String op) {
		//Wenn Stackaddresse
		if(op.indexOf(' ') != -1) {
			String[] pair = op.split(" ");
			op = pair[0] + "(%" + pair[1] + ")";
		} 
		//Wenn Register
		else if(op.equals("eax")) op = "%" + op;
		else if(op.equals("ebx")) op = "%" + op;
		else if(op.equals("ecx")) op = "%" + op;
		else if(op.equals("edx")) op = "%" + op;
		else if(op.equals("edi")) op = "%" + op;
		else if(op.equals("esi")) op = "%" + op;
		else if(op.equals("ebp")) op = "%" + op;
		else if(op.equals("esp")) op = "%" + op;
		else if(op.equals("xmm0")) op = "%" + op;
		else if(op.equals("xmm1")) op = "%" + op;
		else if(op.equals("xmm2")) op = "%" + op;
		else if(op.equals("xmm3")) op = "%" + op;
		else if(op.equals("xmm4")) op = "%" + op;
		else if(op.equals("xmm5")) op = "%" + op;
		else if(op.equals("xmm6")) op = "%" + op;
		else if(op.equals("xmm7")) op = "%" + op;
		//Wenn Hexadezimalzeahl
		else if(op.startsWith("0x")) op = "$" + op;
		//Wenn Dezimalzahl oder irgendetwas anderes
		else op = "$" + op;
			
		return op;
	}
	protected String requote(String string) {
		
		string = string.replace("\\0A", "\\n");
		string = string.replace("\\09", "\\t");
		string = string.replace("\\00", "\\0");
		string = string.replace("'", "\\'");
		string = string.replace("\\22", "\\\"");
		string = string.replace("?", "\\?");
		string = string.replace("\\5C", "\\\\");
		string = string.replace("\\07", "\\a");
		string = string.replace("\\08", "\\b");
		string = string.replace("\\0C", "\\f");
		string = string.replace("\\0D", "\\r");
		string = string.replace("\\0B", "\\v");
		
		return string;
	}
	
}
