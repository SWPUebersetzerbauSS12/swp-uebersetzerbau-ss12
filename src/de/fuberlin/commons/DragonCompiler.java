package de.fuberlin.commons;

import de.fuberlin.projectF.CodeGenerator.CodeGenerator;

public class DragonCompiler {
    // project a
    public static String generateLLVMCode(String program) {
        // plug here project bi, bii, ci, cii into main
        return program;
    }

    // project e
    private static String optimizeLLVMCode(String llvmCode) {
        return llvmCode;
    }


    // project f
    private static String generateGASAssembler(String llvmCode) {
    	boolean debug = false;
    	boolean guiFlag = false;
    	String assemblerType="gnu";
        return CodeGenerator.generateCode(llvmCode, assemblerType,  debug, guiFlag);
    }

    // project ci with bi or bii
    private static void genParseTreeCi(String program) {
        return;
    }

    // project cii with bii or bi
    private static void genParseTreeCii(String program) {
        return;
    }


    public static String compile(String program) {
        String llvmCode = generateLLVMCode(program);
        llvmCode = optimizeLLVMCode(llvmCode);
        String gasCode = generateGASAssembler(llvmCode);
        return gasCode;
    }

    public static void main(String[] args) {
        String file = args[0];
        String machineCode = compile(file);
        System.out.println(machineCode);
    }
}
