package de.fuberlin.optimierung;

public enum LLVMOperation {
	ALLOCA, STORE, LOAD, ADD, SUB, MUL, DIV, UDIV, SDIV, UREM, SREM,
	BR, BR_IL, AND, OR, XOR, ICMP_EQ, ICMP_NE, ICMP_UGT, ICMP_UGE,
	ICMP_ULT, ICMP_ULE, ICMP_SGT, ICMP_SGE, ICMP_SLT, ICMP_SLE
}

//DONE: ALLOCA, ADD, SUB, MUL, DIV, UREM, SREM, AND, OR, XOR, ICMP_EQ, ICMP_NE, ICMP_UGT, ICMP_UGE,
//ICMP_ULT, ICMP_ULE, ICMP_SGT, ICMP_SGE, ICMP_SLT, ICMP_SLE