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
 * Authors: Johannes Dahlke
 * 
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */

package de.fuberlin.bii.utils;

/**
 * Die Klasse stellt vorgefertigte Meldungen zur Verfügung.
 * 
 * @author Johannes
 */
public class Notification {

	// error message handling

	private static boolean printErrorMessages = true;


	public static void enableErrorPrinting() {
		printErrorMessages = true;
	}


	public static void disableErrorPrinting() {
		printErrorMessages = false;
	}


	public static void printErrorMessage( String message) {
		if ( printErrorMessages)
			System.err.println( "NotificationService(Error): " + message);
	}
	
	public static void printMismatchMessage( String message) {
		System.err.println( "NotificationService(Mismatch): " + message);
	}

	// debugmessage handling

	private static boolean printDebugMessages = false;


	public static void enableDebugPrinting() {
		printDebugMessages = true;
	}


	public static void disableDebugPrinting() {
		printDebugMessages = false;
	}


	public static void printDebugMessage( String message) {
		if ( printDebugMessages)
			System.err.println( "NotificationService(Debug): " + message);
	}


	public static void printDebugException( Exception e) {
		if ( printDebugMessages) {
			System.err.println( "NotificationService(Debug): ");
			e.printStackTrace();
		}
	}

	// info message handling

	private static boolean printInfoMessages = false;


	public static void enableInfoPrinting() {
		printInfoMessages = true;
	}


	public static void disableInfoPrinting() {
		printInfoMessages = false;
	}


	public static void printInfoMessage( String message) {
		if ( printInfoMessages)
			System.out.println( "NotificationService(Info): " + message);
	}
	
  //info message handling

	private static boolean printDebugInfoMessages = false;


	public static void enableDebugInfoPrinting() {
		printDebugInfoMessages = true;
	}


	public static void disableDebugInfoPrinting() {
		printDebugInfoMessages = false;
	}


	public static void printDebugInfoMessage( String message) {
		if ( printDebugInfoMessages)
			System.out.println( "NotificationService(DebugInfo): " + message);
	}

}
