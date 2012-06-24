package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.analysis.SymbolTableStack;
import de.fuberlin.commons.parser.ISyntaxTree;


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
		int mains = 0;
		for (int i = 0; i < this.getChildrenCount(); i++) {
			AbstractSyntaxTree child = (AbstractSyntaxTree) this.getChild(i);
			if (!child.checkSemantics()) {
				return false;
			}
			if (child instanceof FuncDef) {
				String name = ((Id) child.getChild(1)).getValue();
				if (name.equals("main")) {
					mains++;
				}
			}
		}
		if (mains == 1) {
			return true;
		} else {
			throw new SemanticException(
					"Program needs exactly one main method! Program contains "
							+ mains + " main methods.");
		}
	}

	@Override
	public boolean checkTypes() {
		// check children and we are good.
		for(ISyntaxTree child : this.getChildren()){
			if(!((AbstractSyntaxTree)child).checkTypes()){
				return false;
			}
		}
		return true;
	}
	
	/*
	 * GenCode already implemented by AbstractSyntaxTree
	 * - i don't care, we need some declarations
	 */
	public String genCode(){
		//we use puts to print to screen
		String out = "declare i32 @puts(i8*) nounwind\n";
		out += "declare i32 @printf(i8*, ...) nounwind\n";
		out += super.genCode();
		return out;
	}
}
