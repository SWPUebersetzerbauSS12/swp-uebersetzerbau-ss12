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
