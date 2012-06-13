package de.fuberlin;

import de.fuberlin.optimierung.LLVM_Optimization;
import de.fuberlin.projectcii.ParserGenerator.src.LL1Parser;

class Main {
	
	public static void main(String args[]) {

		System.out.println("Hier die Code-Schnipsel einfuegen!");

		String llvm_code = "";	// Hier der generierte LLVM-Code


		//--------------------------
		/*
		 *	Optimierung
		 *	input:	String llvm_code
		 *	output:	String optimized_llvm_code
		 */
		LLVM_Optimization llvm_optimizer = new LLVM_Optimization();

		String optimized_llvm_code = llvm_optimizer.optimizeCodeFromString(llvm_code);	// Muss angepasst werden
		//--------------------------
	}	
}
