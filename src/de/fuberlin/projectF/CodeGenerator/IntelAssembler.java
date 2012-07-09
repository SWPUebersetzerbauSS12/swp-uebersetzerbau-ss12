package de.fuberlin.projectF.CodeGenerator;

public class IntelAssembler extends Assembler{

	public IntelAssembler() {
		super();
		sectionHead = new StringBuffer().append("");
		sectionData = new StringBuffer().append("section .data\n");
		sectionText = new StringBuffer().append("section .text\n");
	}
	
	protected void createEP() {
		if(System.getProperty("os.name").toLowerCase().indexOf("linux") >= 0) {
			sectionHead.append("extern exit\n");
		}
		else if(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
			sectionHead.append("extern _exit\n");
		}
			sectionText.append("global _start\n_start:\n\tcall main\n" + "\tpush "
				+ "eax" + "\n\tcall exit\n\n");
	
		
		}
	
	protected void data(String label, String type, String value) {
		value=requote(value);
		sectionData.append(label).append(":\t").append("db").append(" ")
				.append(value).append("\n");
	}
	
	protected void declare(String name) {
		if(System.getProperty("os.name").toLowerCase().indexOf("linux") >= 0) {
			sectionHead.append("extern ").append(name).append("\n");
		}
		else if(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
			sectionHead.append("extern _").append(name).append("\n");
		}
		
	}
	
	protected void funcDec(String name, String operand1, String operand2) {
		operand1 = translate(operand1);
		operand2 = translate(operand2);
		sectionText.append(name).append(":\n");
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
		sectionText.append("\tcmp ").append(target).append(", ")
				.append(source).append("\t\t\t;Label ").append("\n");
	}

	protected void label(String name) {
		sectionText.append("label_").append(name).append(":\t\t\t;Label ")
				.append(name).append("\n");
	}

	protected void call(String name) {
		sectionText.append("\tcall ").append(name).append("\t;Call ")
				.append(name).append("\n");
	}

	protected void push(String operand, String comment) {
		operand = translate(operand);
		sectionText.append("\tpush ").append(operand).append("\t;")
				.append(comment).append("\n");
	}

	protected void mov(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\tmov ").append(target).append(", ")
				.append(source).append("\t;").append(comment).append("\n");
	}
	
	protected void movsd(String source, String target, String comment) {
		source = translate(source).replace("dword ", "");
		target = translate(target).replace("dword ", "");
		sectionText.append("\tmovsd ").append(target).append(", ")
				.append(source).append("\t;").append(comment).append("\n");
	}
	
	protected void movss(String source, String target, String comment) {
		source = translate(source).replace("dword ", "");;
		target = translate(target).replace("dword ", "");;
		sectionText.append("\tmovss ").append(target).append(", ")
				.append(source).append("\t;").append(comment).append("\n");
	}
	
	protected void cvtsi2sd(String source, String target, String comment) {
		source = translate(source).replace("dword ", "");;
		target = translate(target).replace("dword ", "");;
		sectionText.append("\tcvtsi2sd ").append(target).append(", ")
				.append(source).append("\t;").append(comment).append("\n");
	}
	
	protected void cvtsd2ss(String source, String target, String comment) {
		source = translate(source).replace("dword ", "");;
		target = translate(target).replace("dword ", "");;
		sectionText.append("\tcvtsd2ss ").append(target).append(", ")
				.append(source).append("\t;").append(comment).append("\n");
	}
	
	protected void cvttss2si(String source, String target, String comment) {
		source = translate(source).replace("dword ", "");;
		target = translate(target).replace("dword ", "");;
		sectionText.append("\tcvttss2si ").append(target).append(", ")
				.append(source).append("\t;").append(comment).append("\n");
	}

	protected void or(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\tor ").append(target).append(", ")
				.append(source).append("\t;").append(comment).append("\n");
	}
	
	protected void and(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\tand ").append(target).append(", ")
				.append(source).append("\t;").append(comment).append("\n");
	}
	
	protected void xor(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\txor ").append(target).append(", ")
				.append(source).append("\t;").append(comment).append("\n");
	}
	
	protected void idiv(String source) {
		source = translate(source);
		sectionText.append("\tidiv ").append(source).append("\n");
	}

	protected void imul(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\timul ").append(target).append(", ")
				.append(source).append("\t;").append(comment).append("\n");
	}

	protected void sub(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\tsub ").append(target).append(", ")
				.append(source).append("\t;").append(comment).append("\n");
	}

	protected void add(String source, String target, String comment) {
		source = translate(source);
		target = translate(target);
		sectionText.append("\tadd ").append(target).append(", ")
				.append(source).append("\t;").append(comment).append("\n");
	}
	
	protected void addsd(String source, String target, String comment) {
		source = translate(source).replace("dword ", "");;
		target = translate(target).replace("dword ", "");;
		sectionText.append("\taddsd ").append(target).append(", ")
				.append(source).append("\t;").append(comment).append("\n");
	}
	
	protected void subsd(String source, String target, String comment) {
		source = translate(source).replace("dword ", "");;
		target = translate(target).replace("dword ", "");;
		sectionText.append("\tsubsd ").append(target).append(", ")
				.append(source).append("\t;").append(comment).append("\n");
	}
	
	protected void mulsd(String source, String target, String comment) {
		source = translate(source).replace("dword ", "");;
		target = translate(target).replace("dword ", "");;
		sectionText.append("\tmulsd ").append(target).append(", ")
				.append(source).append("\t;").append(comment).append("\n");
	}
	
	protected void divsd(String source, String target, String comment) {
		source = translate(source).replace("dword ", "");;
		target = translate(target).replace("dword ", "");;
		sectionText.append("\tdivsd ").append(target).append(", ")
				.append(source).append("\t;").append(comment).append("\n");
	}
	
	protected String translate(String op) {
		//Wenn Stackaddresse
				if(op.indexOf(' ') != -1) {
					String[] pair = op.split(" ");
					op = "dword [" + pair[1] + " + " + pair[0] + "]";
				} 
				
				return op;
	}
	private String requote(String string) {
		
		string = string.replace("\\0A", "\",10,\"");
		string = string.replace("\\09", "\",09,\"");
		string = string.replace("\\00\"", "\",0");
		string = string.replace("\\22", "\",0x22,\"");
		string = string.replace("\\5C", "\",0x5C,\"");
		string = string.replace("\\08", "\",08,\"");
		string = string.replace("\\0C", "\",0x0C,\"");
		string = string.replace("\\0D", "\",0x0D,\"");
		string = string.replace("\\0B", "\",0x0B,\"");
		
		return string;
	}
	
}
