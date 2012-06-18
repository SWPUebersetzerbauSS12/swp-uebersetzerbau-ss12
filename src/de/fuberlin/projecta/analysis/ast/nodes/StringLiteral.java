package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.SymbolTableStack;

public class StringLiteral extends Statement {

	private String value;

	public StringLiteral(String value) {
		this.value = value;
	}

	public void buildSymbolTable(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		return true;
	}

	/* How to create a string in llvm and output it (it's easy once you get it..)
Using i8 arrays:
  %str = alloca [5 x i8]	; we created an 5 byte string
  store [5 x i8] c"test\00", [5 x i8]* %str
  %1 = getelementptr [5 x i8]* %str, i8 0, i8 0
  tail call i32 (i8*)* @puts(i8* %1)
Using i8 pointer:
  %str2 = alloca i8, i8 9   ;we created an 9 byte string
  %1 = bitcast i8* %str2 to [9 x i8]*
  store [9 x i8] c"test1234\00", [9 x i8]* %1
  tail call i32 (i8*)* @puts(i8* %1)
	 */
	
	@Override
	public String genCode() {
		return "c\"" + this.value  + "\\00" + "\"";
	}

	public int getLength(){
		return value.length();
	}
	
	public String getValue() {
		return this.value;
	}

	@Override
	public boolean checkTypes() {
		// TODO Auto-generated method stub
		return false;
	}
}
