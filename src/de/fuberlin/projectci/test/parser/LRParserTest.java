package de.fuberlin.projectci.test.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import de.fuberlin.bii.lexergen.BuilderType;
import de.fuberlin.bii.lexergen.Lexergen;
import de.fuberlin.bii.tokenmatcher.errorhandler.ErrorCorrector.CorrectionMode;
import de.fuberlin.commons.lexer.ILexer;
import de.fuberlin.commons.parser.IParser;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.commons.util.LogFactory;
import de.fuberlin.optimierung.LLVM_Optimization;
import de.fuberlin.projecta.analysis.SemanticAnalyzer;
import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projectci.lrparser.LRParser;
import de.fuberlin.projectci.lrparser.LRParserException;

public class LRParserTest {
	private static Logger logger = LogFactory.getLogger(LRParserTest.class);
	private static final String testSourceFilesDirPath="input/de/fuberlin/projectci/quellprogrammdateien";
	private static final String DEFAULT_GRAMMAR_FILE = "input/de/fuberlin/projectci/non-ambigous.txt";
	private static final String DEFAULT_TOKEN_DEFINITION_FILE = "input/de/fuberlin/bii/def/tokendefinition.rd";
	
	@Before
	public void setUp() throws Exception {
		// Disable System.out to avoid spamming the console output
		//PrintStream printStreamOriginal=System.out;
		System.setOut(new PrintStream(new OutputStream(){
			public void write(int b) {
			}
		}));
		LogFactory.init(Level.INFO, null, null);
	}

	@Test
	public void testParse() {
		boolean rebuildDFA=false;
		
		File[] sourceFiles = null;
		try {
			File testSourceFilesDir = new File(testSourceFilesDirPath);
			sourceFiles = testSourceFilesDir.listFiles();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to access example source files from "+testSourceFilesDirPath, e);
			fail("Failed to access example source files from "+testSourceFilesDirPath);
		}
		if (sourceFiles==null){
			logger.log(Level.SEVERE, "Failed to access any example source files from "+testSourceFilesDirPath);
			fail("Failed to access any example source files from "+testSourceFilesDirPath);
		}
		
		if (sourceFiles.length==0){
			logger.info("No source file found in "+testSourceFilesDirPath);
			return;
		}
		
		IParser parser=new LRParser();
		int numberOfTests=0;
		int numberOfPassedTests=0;
		
		for (int i = 0; i < sourceFiles.length; i++) {
			File aSourceFile = sourceFiles[i];
						
			List<ILexer> lexers=new ArrayList<ILexer>();
			try {
				lexers.add(new Lexergen(new File(DEFAULT_TOKEN_DEFINITION_FILE), aSourceFile, BuilderType.directBuilder, CorrectionMode.PANIC_MODE, rebuildDFA));
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Failed to create Lexer "+BuilderType.directBuilder+" for tokendefinition="+DEFAULT_TOKEN_DEFINITION_FILE+" and sourceFile="+aSourceFile.getAbsolutePath(), e);
			}
			try {
				lexers.add(new Lexergen(new File(DEFAULT_TOKEN_DEFINITION_FILE), aSourceFile, BuilderType.indirectBuilder, CorrectionMode.PANIC_MODE, rebuildDFA));
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Failed to create Lexer "+BuilderType.indirectBuilder+" for tokendefinition="+DEFAULT_TOKEN_DEFINITION_FILE+" and sourceFile="+aSourceFile.getAbsolutePath(), e);
			}
			
			for (ILexer aLexer : lexers) {
				numberOfTests++;
				try {
					logger.info("Testing sourceFile: "+aSourceFile.getAbsolutePath()+" with lexer="+((Lexergen)aLexer).getBuilderType()+", tokenDefintion="+DEFAULT_TOKEN_DEFINITION_FILE+" and grammar="+DEFAULT_GRAMMAR_FILE);
					ISyntaxTree syntaxTree = null;
					try {
						syntaxTree = parser.parse(aLexer, DEFAULT_GRAMMAR_FILE);
						if (syntaxTree==null){
							logger.log(Level.SEVERE, "Parser failed to create SyntaxTree.");
							continue;
						}
					} catch (RuntimeException e) {
						logger.log(Level.SEVERE, "Parser failed to create SyntaxTree.", e);
						continue;
					}
					
					SemanticAnalyzer semanticAnalyzer= new SemanticAnalyzer(syntaxTree);
					try {
						semanticAnalyzer.analyze();
						try {
							semanticAnalyzer.getAST().checkSemantics();
							try {
								String generatedLLVMCode = semanticAnalyzer.getAST().genCode();
								String optimized_llvm_code = null;
								
								try {
									LLVM_Optimization llvm_optimizer = new LLVM_Optimization();
									optimized_llvm_code = llvm_optimizer.optimizeCodeFromString(generatedLLVMCode);
								} catch (Exception e) {
									logger.log(Level.WARNING,"LLVM Optimization failed. Use unoptimized code!",e);
									// Nutze nicht optimierten Code
									optimized_llvm_code = generatedLLVMCode;
								}								
								
								try {
									// TODO CodeGenerator kann nicht getestet werden, da Exception in CodeGenerator.generateCode2 gefangen und nicht wieder geworfen wird.
//									boolean debug = false;
//									boolean guiFlag = false;								
//									boolean exec = false;
//									String asmType = "gnu";
//										
//									String machineCode = CodeGenerator.generateCode(optimized_llvm_code, asmType, debug, guiFlag);
//									// TODO Gibt es eine einfache Möglichkeit die Güte des LLVM Codes zu beurteilen?
//									if (machineCode==null){
//										logger.log(Level.SEVERE, "CodeGenerator failed to generate Machine code.");
//										continue;
//									}
//									
//									
////									if (outputFile != null) {
////										CodeGenerator.writeFile(exec, outputFile, machineCode);
////									}
////									
////									if (exec) {
////										CodeGenerator.exec(outputFile);
////									}
									
									numberOfPassedTests++;
									logger.info("Succeed to generate LLVM code.");
//									logger.info("Succeed to generate Machine code.");
								} catch (RuntimeException e) {
									logger.log(Level.WARNING,"Machine Code Generation failed.",e);
								}																								
								
							} catch (Exception e) {
								logger.log(Level.SEVERE, "SemanticAnalyzer failed to generate LLVM code on SyntaxTree.", e);
							}
						} catch (RuntimeException e) {
							logger.log(Level.WARNING, "SemanticAnalyzer failed to check semantics on SyntaxTree.", e);
						}
					} catch (SemanticException e) {
						logger.log(Level.SEVERE, "SemanticAnalyzer failed to analyze SyntaxTree.", e);
					}

				} catch (LRParserException e) {
					logger.log(Level.SEVERE, "LRParser failed to create SyntaxTree", e);
				}
			}
		}
		int numberOfFaildTests=numberOfTests-numberOfPassedTests;
		assertEquals(numberOfFaildTests+" out of "+numberOfTests+" tests failed", numberOfTests,numberOfPassedTests);
		
	}

	
	
}
