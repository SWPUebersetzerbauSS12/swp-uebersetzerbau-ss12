package analysis.ast.nodes;

import analysis.SymbolTableStack;

/**
 * first child num
 * second child type' 
 * 
 * @author sh4ke
 */
public class Array extends Type {
	public void buildSymbolTable(SymbolTableStack tables) {

	}
	
	@Override
	public boolean equals(Object object){
		if(object instanceof Array){
//			for()
		}
		return false;
	}
}
