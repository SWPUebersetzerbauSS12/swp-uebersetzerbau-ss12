/*
 * 
 * Copyright 2012 lexergen.
 * This file is part of lexergen.
 * 
 * lexergen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * lexergen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with lexergen.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * lexergen:
 * A tool to chunk source code into tokens for further processing in a compiler chain.
 * 
 * Projectgroup: bi, bii
 * 
 * Authors:  Alexander Niemeier, Benjamin Weißenfels, Daniel Rotar, Johannes Dahlke, Maximilian Schröder, Philipp Schröter, Wojciech Lukasiewicz, Yanlei Li
 * 
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */


package de.fuberlin.bii;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.sound.midi.SysexMessage;

import de.fuberlin.bii.lexergen.BuilderType;
import de.fuberlin.bii.lexergen.Lexergen;
import de.fuberlin.bii.parser.IToken;
import de.fuberlin.bii.tokenmatcher.Token;
import de.fuberlin.bii.tokenmatcher.errorhandler.ErrorCorrector.CorrectionMode;
import de.fuberlin.bii.utils.ApplicationArgument;
import de.fuberlin.bii.utils.ApplicationArgumentProcessor;
import de.fuberlin.bii.utils.InvalidArgumentException;
import de.fuberlin.bii.utils.Notification;
import de.fuberlin.bii.utils.StrUtils;
import de.fuberlin.bii.utils.Test;
import de.fuberlin.commons.lexer.ILexer;
import de.fuberlin.commons.lexer.TokenType;


/**
 * 
 * @author Alexander Niemeier
 * @author Benjamin Weißenfels
 * @author Daniel Rotar
 * @author Johannes Dahlke
 * @author Maximilian Schröder
 * @author Philipp Schröter
 * @author Wojciech Lukasiewicz
 * @author Yanlei Li
 *
 */
public class Lexer {

	private static ApplicationArgumentProcessor argumentProcessor;
	
	private static ILexer lexer;
	
	private static File definitionFile;
	
	private static File sourceFile;
	
	private static BuilderType builderType = BuilderType.directBuilder;
	
	private static boolean rebuildDfa = false;
	
	private static CorrectionMode correctionMode = CorrectionMode.PANIC_MODE;
	
	private static boolean lineBreak = false;
	
	private static boolean noHeader = false;
	
	
	/**
	 * Liest die übergebenen Argumente ein. Analysiert sie auf Syntax und filtert
	 * die akzeptierten Parameter heraus
	 * 
	 * @param args
	 * @throws InvalidArgumentException
	 */
	private static void initArgumentProcessor() throws InvalidArgumentException {

		// param verbose
		final ApplicationArgument beVerboseArgument = new ApplicationArgument( "v", "verbose") {
	  	protected void execute() {
	  		Notification.enableInfoPrinting();
	  	};
	  	@Override
	  	protected boolean acceptParam( String param) {
	  		return false;
	  	}
	  };
		
		// param errors
		final ApplicationArgument notifyErrorsArgument = new ApplicationArgument( "e", "errors") {
	  	protected void execute() {
	  		if ( getParamList().contains( "suppress"))
	  		  Notification.disableErrorPrinting();
	  		else
	  			Notification.enableErrorPrinting();
	  	};
	  	@Override
	  	protected boolean acceptParam( String param) {
	  		return param.equals( "suppress");
	  	}
	  };		
	  
		// param debug
		final ApplicationArgument debugArgument = new ApplicationArgument( "db", "debug") {
	  	protected void execute() {
	  		Notification.enableDebugPrinting();
	  	};
	  	@Override
	  	protected boolean acceptParam( String param) {
	  		return false;
	  	}
	  };

	  // param tokendefinition file 
	  final ApplicationArgument tokendefFileArgument = new ApplicationArgument( "d", "token-definition") {
	  	protected void execute() {
	  		if ( getParamList().size() < 1) {
	  			System.err.println( "No definition file specified.");
	  			System.exit( 1);
	  		}
	  		else
	  		  definitionFile = new File( getParamList().get( 0));
	  	};
	  	@Override
	  	protected boolean acceptParam( String param) {
	  		return getParamList().size() == 0;
	  	}
	  };

	  // param rebuild dfa 
	  final ApplicationArgument rebuildDfaArgument = new ApplicationArgument( "rb", "rebuild-dfa") {
	  	protected void execute() {
	  		rebuildDfa = true;
	  		builderType =  ( getParamList().contains( "bi")) ? BuilderType.indirectBuilder : BuilderType.directBuilder; 		
	  	};
	  	@Override
	  	protected boolean acceptParam( String param) {
	  		return param.equals( "bi") || // indirect
	  				   param.equals( "bii");  // direct
	  	}
	  };
	  

	  
	  // param source file
	  final ApplicationArgument sourceFileArgument = new ApplicationArgument( "f", "source-file") {
	  	protected void execute() {
	  		if ( getParamList().size() < 1) {
	  			System.err.println( "No source file specified.");
	  		  System.exit( 1);
	  		}
	  		else
	  		  sourceFile = new File( getParamList().get( 0));
	  	};
	  	@Override
	  	protected boolean acceptParam( String param) {
	  		return getParamList().size() == 0;
	  	}
	  };		
	  
	  
	  // param linebreak
	  final ApplicationArgument lineBreakArgument = new ApplicationArgument( "lb", "line-break") {
	  	protected void execute() {
	  			lineBreak = true;
	  	};
	  	@Override
	  	protected boolean acceptParam( String param) {
	  		return false;
	  	}
	  };		

	  // param usage
	  final ApplicationArgument helpArgument = new ApplicationArgument( "h", "help") {
	  	protected void execute() {
	  		printUsage();
	  		System.exit( 0);
	  	};
	  	@Override
	  	protected boolean acceptParam( String param) {
	  		return false;
	  	}
	  };		

	  // param usage
	  final ApplicationArgument bareArgument = new ApplicationArgument( "b", "bare") {
	  	protected void execute() {
	  		noHeader = true;
	  	};
	  	@Override
	  	protected boolean acceptParam( String param) {
	  		return false;
	  	}
	  };	
	  
		argumentProcessor = new ApplicationArgumentProcessor( beVerboseArgument, 
																													notifyErrorsArgument, 
																													debugArgument,
																													tokendefFileArgument,
																													rebuildDfaArgument,
																													sourceFileArgument,
																													lineBreakArgument,
																													helpArgument,
																													bareArgument
																													);
	}
	
	/**
	 * Untersucht die Argumente, welche der ArgumentProcessor eingelesen hat.
	 * 
	 * @param args
	 * @throws NoSuchElementException
	 * @throws IllegalArgumentException
	 * 
	 */
	private static void processArguments(String[] args) throws NoSuchElementException {

		argumentProcessor.processArguments( args);
	}
	
	
	
	//force-rebuild
	
	
	/**
	 * Erstellt ein neues Lexergen Objekt.
	 * 
	 * @param regularDefinitionFile
	 *            Die Datei, die die regulären Definitionen enthält.
	 * @param sourceProgramFile
	 *            Die Datei, die das Quellprogramm enthält.
	 * @param builderType
	 *            Der Typ der DFA-Erstellung.
	 * @param errorCorrectionMode
	 *            Der Modus der Fehlerbehandlung.
	 * @param forceRebuild
	 *            Erzwingt die Neuerstellung des DFAs.
	 */
	
	
	private static void runApplication() throws Exception {
		if ( !noHeader)
			printEnvInfo();
		
		if ( Test.isUnassigned( definitionFile) ||
				 Test.isUnassigned( sourceFile)) {
			System.err.println( "Insufficient parameterization");
			System.exit( 1);
		}
		
			
	  lexer = new Lexergen( definitionFile, sourceFile, builderType, correctionMode, rebuildDfa);
		Token token;
		String postFix = lineBreak ? "\n" : "";
		do {
			token = (Token) lexer.getNextToken();
			System.out.print( token.toString()+postFix);
		} while ( !token.isEofToken());
			
	}
	
	private static void printEnvInfo() {
		System.out.println( "-------------------------------------------------------------------------------");
		System.out.println( "token definition file: \t" + definitionFile);
		System.out.println( "source file: \t\t" + sourceFile);
		System.out.println( "dfa builder type: \t" + builderType.toString());
		System.out.println( "force dfa rebuild: \t"+ rebuildDfa);
		System.out.println( "correction mode: \t" + correctionMode.toString());
		System.out.println( "insert line breaks: \t" + lineBreak);
		System.out.println( "-------------------------------------------------------------------------------");
		System.out.println();
	}
	
	private static void printUsage() {
		System.out.println( "Usage: java -jar lexer.jar -d <path> -f <path>");
    System.out.println();
		System.out.println( "Expected parameters:");
		System.out.println( "-d, --token-definition <path> \t Specifies the token definition file.");
		System.out.println( "-f, --source-file <path> \t Specifies the source file to lex.");
		System.out.println();
		System.out.println( "Optional parameters:");
		System.out.println( "-b, --bare \t\t\t Suppress information header.");
		System.out.println( "-db, --debug \t\t\t Enables output of debug informations.");
		System.out.println( "-e, --errors [suppress] \t Enables or suppress error printing.");
		System.out.println( "-h, --help  \t\t\t Shows this help.");
		System.out.println( "-lb, --line-break  \t\t Appends a line break after each token.");
		System.out.println( "-rb, --rebuild-dfa [bi|bii] \t Forces the rebuild of the normally persitent DFA.");
    System.out.println( "-v, --verbose \t\t\t Prints additional information while lex.");
    System.out.println(); 
    System.out.println( "Authors: Alexander Niemeier, Benjamin Weißenfels, Daniel Rotar, Johannes Dahlke, " +
    		                         "Maximilian Schröder, Philipp Schröter, Wojciech Lukasiewicz, Yanlei Li");
	}
	
	
	public static void main( String[] args) {

		Notification.enableErrorPrinting();
		
		try {	
			initArgumentProcessor();
      processArguments( args);
		} catch ( InvalidArgumentException e) {
			// Notification Service ist hier möglicherweise noch nicht aufgesetzt.
			System.err.println( e);
		}  

		
		try {	
			runApplication();
		} catch ( Exception e) {
			Notification.printDebugException( e);
			Notification.printErrorMessage( e.getMessage());
		}
	}	


}
