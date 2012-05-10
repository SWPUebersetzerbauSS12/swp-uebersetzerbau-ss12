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
 * Authors: Maximilian Schröder, Daniel Rotar, Johannes Dahlke
 * 
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */

package dfaprovider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import regextodfaconverter.MinimalDfa;
import tokenmatcher.StatePayload;

/**
 * Stellt einen Wrapper für die Parameter<Character, StatePayload> dar, der vom
 * {@link MinimalDfaProvider} serialisiert bzw. deserialisert wird. Dabei sind
 * im Wrapper alle benötigten Informationen für die Rückgabe des minimalen DFA
 * enthalten.
 * 
 * @author Maximilian Schröder
 * 
 */
public class MinimalDfaCharacterStatePayloadWrapper implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6790273782667033324L;
	/**
	 * Version des Lexers (mit dem der minimale DFA assoziiert ist)
	 */
	private String _version;
	/**
	 * Hahswert der regulären Definitionsdatei, mit dem der minimale DFA erzeugt
	 * wurde
	 */
	private String _rdFileHash;
	/**
	 * minimaler DFA, der serialisiert bzw. deserialisiert werden kann
	 */
	private MinimalDfa<Character, StatePayload> _mDfa;

	/**
	 * Erzeugt einen Wrapper, der den minimalen DFA sowie die dazugehörigen
	 * Statusinformationen enthält.
	 * 
	 * @param version
	 *            Version des Lexers, mit dem der minimale DFA assoziiert ist.
	 * @param rdFileHash
	 *            Hahswert der regulären Definitionsdatei, mit dem der minimale
	 *            DFA erzeugt wurde.
	 * @param mDfa
	 *            minimaler DFA, der serialisiert bzw. deserialisiert werden
	 *            kann.
	 */
	public MinimalDfaCharacterStatePayloadWrapper(String version,
			String rdFileHash, MinimalDfa<Character, StatePayload> mDfa) {
		setVersion(version);
		setRdFileHash(rdFileHash);
		setMDfa(mDfa);
	}

	/**
	 * Gibt die Version des Lexers (mit dem der minimale DFA assoziiert ist)
	 * zurück.
	 * 
	 * @return Version des Lexers.
	 */
	public String getVersion() {
		return _version;
	}

	/**
	 * Setzt Version des Lexers (mit dem der minimale DFA assoziiert ist) neu.
	 * 
	 * @param version
	 *            Neue Version des Lexers.
	 */
	public void setVersion(String version) {
		_version = version;
	}

	/**
	 * Gibt den Hahswert der regulären Definitionsdatei, mit dem der minimale
	 * DFA erzeugt wurde, zurück.
	 * 
	 * @return: Hashwert der regulären Definitionsdatei.
	 */
	public String getRdFileHash() {
		return _rdFileHash;
	}

	/**
	 * Setzt den Hahswert der regulären Definitionsdatei, mit dem der minimale
	 * DFA erzeugt wurde, neu.
	 * 
	 * @param rdFileHash
	 *            Neuer Hashwert der regulären Definitionsdatei.
	 */
	public void setRdFileHash(String rdFileHash) {
		_rdFileHash = rdFileHash;
	}

	/**
	 * Gibt den minimalen DFA, der serialisiert bzw. deserialisiert werden kann,
	 * zurück.
	 * 
	 * @return Aktueller minimaler DFA.
	 */
	public MinimalDfa<Character, StatePayload> getMDfa() {
		return _mDfa;
	}

	/**
	 * Setzt den minimalen DFA, der serialisiert bzw. deserialisiert werden
	 * kann, neu.
	 * 
	 * @param mDfa
	 *            Neuer minimaler DFA.
	 */
	public void setMDfa(MinimalDfa<Character, StatePayload> mDfa) {
		_mDfa = mDfa;
	}

	/**
	 * Deserialisiert einen serialisierten
	 * {@link MinimalDfaCharacterStatePayloadWrapper} auf Basis der übergebenen
	 * Datei.
	 * 
	 * @param file 
	 *            Pfad (inkl. Datei) von dem deserialisiert werden soll.
	 * @return deserialisierter Wrapper, der die nötigen Informationen für
	 *         {@link MinimalDfaProvider} enthält.
	 * @throws IOException
	 *             Leseprobleme bzgl. der Input-Datei
	 * @throws MinimalDfaCharacterStatePayloadWrapperException
	 *             Fehlerhafte Input-Datei - keine DFA enthalten
	 */
	public static MinimalDfaCharacterStatePayloadWrapper load(File file)
			throws IOException, MinimalDfaCharacterStatePayloadWrapperException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		try {
			return ((MinimalDfaCharacterStatePayloadWrapper) ois.readObject());
		} catch (ClassNotFoundException e) {
			throw new MinimalDfaCharacterStatePayloadWrapperException(
					"Die angegebene Datei '" + file.getAbsolutePath()
							+ "' enthält keinen gültigen minimalen DFA!");
		}
	}

	/**
	 * Serialisiert die aktuelle Instanz des
	 * {@link MinimalDfaCharacterStatePayloadWrapper} und speichert sie in eine
	 * Datei.
	 * 
	 * @param file
	 *            Pfad (inkl. Datei) in dem serialisiert werden soll.
	 * @throws IOException
	 *             Speicherproblem bzgl. der Ausgabe-Datei
	 */
	public void save(File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
		ObjectOutputStream oOS = new ObjectOutputStream(fos);

		oOS.writeObject(this);

		oOS.close();
	}
}
