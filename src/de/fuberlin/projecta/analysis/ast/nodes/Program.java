package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.SymbolTableStack;


public class Program extends AbstractSyntaxTree {
	
	@Override
	public void buildSymbolTable(SymbolTableStack stack){
		stack.push();
		for(int i = 0; i < this.getChildrenCount(); i++){
			((AbstractSyntaxTree)this.getChild(i)).buildSymbolTable(stack);
		}
		table = stack.pop();	
		
	}

	@Override
	public boolean checkSemantics() {
		boolean mainExists = false;
		for(int i = 0; i < this.getChildrenCount(); i++){
			AbstractSyntaxTree child = (AbstractSyntaxTree)this.getChild(i);
			if(!child.checkSemantics()){
				return false;
			}
			if(child instanceof FuncDef){
				String name = ((Id)child.getChild(1)).getValue();
				if(name.equals("main")){
					mainExists = true;
				}
			}
		}
		return mainExists;
	}

	@Override
	public boolean checkTypes() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/*
	 * GenCode already implemented by AbstractSyntaxTree
	 * - i don't care, we need some declarations
	 */
	public String genCode(){
		//we use puts to print to screen
		String out = "declare i32 @puts(i8*) nounwind\n";
		out += super.genCode();
		return out;
	}
}
