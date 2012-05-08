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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import tokenmatcher.errorhandler.ErrorCorrector.CorrectionMode;

/**
 * Ermöglicht den Zugriff auf programmübergreifende Einstellungen.
 * 
 * @author Daniel Rotar
 * @author Johannes Dahlke
 * @author Benjamin Weißenfels
 * 
 */
public class Settings {

	private static Properties properties;

	public static void readSettings() {

		String path = getApplicationPath()
				+ "/src/main/resources/conf/program.properties";
		properties = new Properties();

		try {
			properties.load(new FileInputStream(path));
		} catch (FileNotFoundException e) {
			System.err.println("Program settings are not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("problems with reading settings file in: "
					+ path);
			e.printStackTrace();
		}
	}

	/**
	 * Gibt den Pfad, in dem sich alle programmrelevanten Dateien befinden
	 * zurück. Methode macht nur auf windows Rechner was sie soll.
	 * 
	 * @return Der Pfad, in dem sich alle programmrelevanten Dateien befinden.
	 *         Die Ausgabe endet immer mit einem "\".
	 */
	@Deprecated
	public static String getWorkingDirectory() {
		return properties.getProperty("workingDirectory");
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
		properties.setProperty("workingDirectory", workingDirectory);
	}

	/**
	 * Gibt den Dateiname der Datei, die die regulären Definitionen enthält
	 * zurück.
	 * 
	 * @return Der Dateiname (inkl. Dateiendung) der Datei, die die regulären
	 *         Definitionen enthält.
	 */
	public static String getRegularDefinitionFileName() {
		return getApplicationPath()
				+ properties.getProperty("regularDefinitionFile");
	}

	/**
	 * Setzt den Dateiname der Datei, die die regulären Definitionen enthält
	 * fest.
	 * 
	 * @param fileName
	 *            Der Dateiname (inkl. Dateiendung) der Datei, die die regulären
	 *            Definitionen enthält.
	 */
	public static void setRegularDefinitionFileName(String fileName) {
		properties.setProperty("regularDefinitionFileName", fileName);
	}

	/**
	 * Gibt Dateipfad (inkl. Dateiendung) der Datei, die das Quellprogramm
	 * enthält zurück.
	 * 
	 * @return Der vollständige Dateipfad (inkl. Dateiendung) der Datei, die das
	 *         Quellprogramm enthält.
	 */
	public static String getSourceProgramFile() {
		return properties.getProperty("sourceProgramFile");
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
		properties.setProperty("sourceProgramFileName", sourceProgramFileName);
	}

	/**
	 * Gibt die aktuelle Version von lexergen zurück.
	 * 
	 * @return Die aktuelle Version von lexergen.
	 */
	public static String getVersion() {
		return properties.getProperty("_VERSION");
	}

	/**
	 * @return Liefert den Modus für die Fehlerbehandlung
	 */
	public static CorrectionMode getErrorCorrectionMode() {
		String getErrCorrectionMode = properties
				.getProperty("errorCorrectionMode");
		return CorrectionMode.valueOf(getErrCorrectionMode);
	}

	/**
	 * Setzt den Modus für die Fehlerbehandlung.
	 * 
	 * @param errorCorrectionMode
	 *            der Modus der Fehlerbehandlung
	 */
	public static void setErrorCorrectionMode(CorrectionMode errorCorrectionMode) {
		properties.setProperty("errorCorrectionMode",
				errorCorrectionMode.toString());
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
			Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null,
					ex);
		}
		return path;
	}

	/**
	 * Gets the default token definition for testing purposes.
	 * 
	 * @return path to the token definition file
	 */
	public static String getDefaultTokenDef() {
		return getApplicationPath() + getRegularDefinitionFileName();
	}

	public static String getConfigFilePath() {
		return getApplicationPath()
				+ "/src/main/resources/conf/program.properties";
	}
}
