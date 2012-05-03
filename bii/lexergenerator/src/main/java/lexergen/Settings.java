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
 * Authors: Alexander Niemeier, Benjamin Weißenfels, Daniel Rotar, Johannes Dahlke, 
 *          Maximilian Schröder, Lukasiewicz Wojciech, Philipp Schröter, yanlei li
 * 
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */

package lexergen;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import tokenmatcher.errorhandler.ErrorCorrector.CorrectionMode;

/**
 * Ermöglicht den Zugriff auf Programmübergreifende Einstellungen.
 * 
 * @author Daniel Rotar
 * @author Johannes Dahlke
 * 
 * 
 */
public class Settings {

	/**
	 * Der Mode, in dem die Fehlerbehandlung erfolgt. Panic mode or phrase level
	 * mode.
	 */
	private static CorrectionMode errorCorrectionMode = CorrectionMode.PANIC_MODE;

	/**
	 * Der Pfad, in dem sich alle programmrelevanten Dateien befinden.
	 */
	private static String _workingDirectory = "D:\\TEMP\\";

	/**
	 * Der Dateiname (inkl. Dateiendung) der Datei, die die regulären
	 * Definitionen enthält.
	 */
	private static String _regularDefinitionFileName = "test.rd";

	/**
	 * Der Dateiname (inkl. Dateiendung) der Datei, die das Quellprogramm
	 * enthält.
	 */
	private static String _sourceProgramFileName = "test.fun";

	/**
	 * Die aktuelle Version von lexergen.
	 */
	private static final String _VERSION = "0.1";

	/**
	 * Gibt den Pfad, in dem sich alle programmrelevanten Dateien befinden
	 * zurück.
	 * 
	 * @return Der Pfad, in dem sich alle programmrelevanten Dateien befinden.
	 *         Die Ausgabe endet immer mit einem "\".
	 */
	public static String getWorkingDirectory() {
		if (_workingDirectory.endsWith("\\")) {
			return _workingDirectory;
		} else {
			return _workingDirectory + "\\";
		}
	}

	/**
	 * Setzte den Pfad, in dem sich alle programmrelevanten Dateien befinden
	 * fest.
	 * 
	 * @param workingDirectory
	 *            Der Pfad, in dem sich alle programmrelevanten Dateien
	 *            befinden.
	 */
	public static void setWorkingDirectory(String workingDirectory) {
		_workingDirectory = workingDirectory;
	}

	/**
	 * Gibt den Dateiname der Datei, die die regulären Definitionen enthält
	 * zurück.
	 * 
	 * @return Der Dateiname (inkl. Dateiendung) der Datei, die die regulären
	 *         Definitionen enthält.
	 */
	public static String getRegularDefinitionFileName() {
		return _regularDefinitionFileName;
	}

	/**
	 * Setzt den Dateiname der Datei, die die regulären Definitionen enthält
	 * fest.
	 * 
	 * @param regularDefinitionFileName
	 *            Der Dateiname (inkl. Dateiendung) der Datei, die die regulären
	 *            Definitionen enthält.
	 */
	public static void setRegularDefinitionFileName(
			String regularDefinitionFileName) {
		_regularDefinitionFileName = regularDefinitionFileName;
	}

	/**
	 * Gibt Dateiname (inkl. Dateiendung) der Datei, die das Quellprogramm
	 * enthält zurück.
	 * 
	 * @return Der Dateiname (inkl. Dateiendung) der Datei, die das
	 *         Quellprogramm enthält.
	 */
	public static String getSourceProgramFileName() {
		return _sourceProgramFileName;
	}

	/**
	 * Setzt Dateiname (inkl. Dateiendung) der Datei, die das Quellprogramm
	 * fest.
	 * 
	 * @param regularDefinitionFileName
	 *            Der Dateiname (inkl. Dateiendung) der Datei, die das
	 *            Quellprogramm enthält.
	 */
	public static void setSourceProgramFileName(String sourceProgramFileName) {
		_sourceProgramFileName = sourceProgramFileName;
	}

	/**
	 * Gibt die aktuelle Version von lexergen zurück.
	 * 
	 * @return Die aktuelle Version von lexergen.
	 */
	public static String getVersion() {
		return _VERSION;
	}

	/**
	 * @return Liefert den Modus für die Fehlerbehandlung
	 */
	public static CorrectionMode getErrorCorrectionMode() {
		return errorCorrectionMode;
	}

	/**
	 * Setzt den Modus für die Fehlerbehandlung.
	 * 
	 * @param errorCorrectionMode
	 *            der Modus der Fehlerbehandlung
	 */
	public static void setErrorCorrectionMode(CorrectionMode errorCorrectionMode) {
		Settings.errorCorrectionMode = errorCorrectionMode;
	}

	/**
	 * Should be work under Windows and Unix based systems
	 * 
	 * @return path to the directory, where the pom.xml is located
	 */
	public static String getApplicationPath() {
		String path = null;
		String pattern = "target(/|\\\\)classes(/|\\\\)lexergen";

		try {
			path = new java.io.File(".").getCanonicalPath().replaceFirst(
					pattern, "");
			System.out.println(path);
		} catch (IOException ex) {
			Logger.getLogger(Settings.class.getName())
					.log(Level.SEVERE, null, ex);
		}
		return path;
	}

	/**
	 * Gets the default token definition for testing purposes.
	 * 
	 * @return path to the token definition file
	 */
	public static String getDefaultTokenDef() {
		String path = getApplicationPath();
		return path + "/src/main/resources/def/tokendefinition";
	}

}
