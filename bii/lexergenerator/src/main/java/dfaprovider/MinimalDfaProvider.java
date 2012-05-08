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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lexergen.Settings;

import regextodfaconverter.MinimalDfa;
import tokenmatcher.StatePayload;

/**
 * Stellt einen DFA-Provider dar, der minimale DFA's, auf Basis einer regulären
 * Definitionsdatei, erstellt.
 * 
 * Dabei wird der minimale DFA entweder aus der serialisierten Form
 * (<dateiname>.dfa) geladen, oder ein neuer minimaler DFA erstellt (sowie
 * serialisiert).
 * 
 * @author Maximilian Schröder
 * 
 */
public class MinimalDfaProvider {

	/**
	 * Gibt den minimalen DFA für die angegebenen regulären Definitionen zurück.
	 * Dabei wird im "Workingdirectory" nach einem serialisierten DFA gesucht,
	 * um diesen (unter gewissen Umständen), zu deserialisieren. Wenn kein
	 * serialisierter DFA vorliegt, wird ein neu-erzeugter minimaler DFA (zur
	 * übergebenen regulären Definitionsdatei) zurückgegeben.
	 * 
	 * @param file
	 *            : Der absolute Pfad zu der Datei, die die regulären
	 *            Definitionen (<dateiname>.rd) enthalten.
	 * @return: Der minimale DFA für die angegebenen regulären Definitionen.
	 */
	@SuppressWarnings("unchecked")
	public static MinimalDfa<Character, StatePayload> getMinimalDfa(
			String rdFileName) throws MinimalDfaProviderException {
		// Anfängliche Überprüfungen, welche die Weiterverarbeitung (inkl.
		// (De-)Serialisierung) ermöglicht
		if (!rdFileName.endsWith(".rd")) {
			throw new MinimalDfaProviderException("Die angegebene Datei "
					+ rdFileName + " hat nicht das Format <dateiname>.rd");
		}
		File rdFile = new File(Settings.getWorkingDirectory() + rdFileName);
		if (!rdFile.exists()) {
			throw new MinimalDfaProviderException("Die angegebene Datei "
					+ rdFileName + " existiert nicht");
		}

		/** Workflow: dfa provider */
		MinimalDfa<Character, StatePayload> mDfa = null;

		// dfa-Datei holen für die Überprüfung (auf Basis der angegebenen
		// rd-Datei)
		int fileEndingIndex = rdFileName.lastIndexOf(".rd");
		String dfaFileName = rdFileName.substring(0, fileEndingIndex + 1)
				+ "dfa";
		File dfaToLoad = new File(Settings.getWorkingDirectory() + dfaFileName);

		// existiert <dateiname>.dfa
		if (dfaToLoad.exists()) {
			try {
				FileInputStream file = new FileInputStream(
						Settings.getWorkingDirectory() + dfaFileName);
				ObjectInputStream o = new ObjectInputStream(file);

				// Zeile_1 vom Header überprüfen (lexergen)
				if (((String) o.readObject()).equals("lexergen")) {

					// Zeile_2 vom Header überprüfen (z.B. 0.1)
					if (((String) o.readObject()).equals(Settings.getVersion())) {

						// sha von regulärer Definitionsdatei bilden und
						// mit Zeile_3 vom Header überprüfen vergleichen
						String currentSHA = getHashedRDFileAsString(Settings
								.getWorkingDirectory() + rdFileName);

						// header korrekt abgearbeitet, nutze ausgelesenen dfa
						if (((String) o.readObject()).equals(currentSHA)) {
							mDfa = (MinimalDfa<Character, StatePayload>) o
									.readObject();
							o.close();
							checkDeserialization(mDfa);
							return mDfa;
						} else {
							// <dateiname>.dfa löschen, da sha1-hash-Konflikt
							dfaToLoad.delete();
							o.close();
							return buildMinimalDfa(rdFileName);
						}
					} else {
						// <dateiname>.dfa löschen, da Versionskonflikt
						dfaToLoad.delete();
						o.close();
						return buildMinimalDfa(rdFileName);
					}
				} else {
					o.close();
					return buildMinimalDfa(rdFileName);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			return buildMinimalDfa(rdFileName);
		}
		checkDeserialization(mDfa);
		return mDfa;
	}

	private static void checkDeserialization(
			MinimalDfa<Character, StatePayload> mDfa) {
		if (mDfa != null) {
			System.out.println("Deserialization finished");
		} else {
			System.out.println("Deserialization failed");
		}
	}

	/**
	 * Serialisiert den übergebenen DFA, unter Berücksichtigung der übergebenen
	 * regulären Definitionsdatei, und speichert diesen als Ausgabedatei
	 * <dateiname>.dfa ab.
	 * 
	 * @param outputFileName
	 *            : Dateiname (inkl. Pfad) für die Datei <dateiname>.dfa
	 * @param rdFileName
	 *            : Der absolute Pfad zu der Datei, die die regulären
	 *            Definitionen (<dateiname>.rd) enthält.
	 * @param dfaToSerialize
	 *            : minimaler DFA, der serialisiert und abgespeichert werden
	 *            soll
	 */
	private static void serialize(String outputFileName, String rdFileName,
			MinimalDfa<Character, StatePayload> dfaToSerialize) {
		try {
			FileOutputStream file = new FileOutputStream(outputFileName);
			ObjectOutputStream oOS = new ObjectOutputStream(file);

			oOS.writeObject("lexergen"); // header1
			oOS.writeObject(Settings.getVersion()); // header2

			String shaString = getHashedRDFileAsString(rdFileName);
			oOS.writeObject(shaString); // header3

			oOS.writeObject(dfaToSerialize);
			oOS.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Berechnet für eine angegebene reguläre Definitionsdatei die Checksumme
	 * (mit SHA1-Algorithmus), wandelt diese in Hexadezimalformat um und gibt
	 * diese als Zeichenkette zurück.
	 * 
	 * @param fileName
	 *            : Der absolute Pfad zu der Datei, die die regulären
	 *            Definitionen (<dateiname>.rd) enthält.
	 * @return: Checksumme der regulären Definitionsdatei, die im
	 *          Hexadezimalformat als Zeichenkette zurückgegeben wird
	 */
	public static String getHashedRDFileAsString(String fileName) {
		String shaToString = "";
		try {
			// Algo festlegen und Datei lesen
			MessageDigest md = MessageDigest.getInstance("SHA");
			FileInputStream fis = new FileInputStream(fileName);
			byte[] fileBytes = new byte[5120];
			int readbytes = 0;

			while ((readbytes = fis.read(fileBytes)) != -1) {
				md.update(fileBytes, 0, readbytes);
			}
			;

			byte[] digest = md.digest();

			// Umwandlung von Byte in Hexadezimalformat
			StringBuffer sb = new StringBuffer("");
			for (int i = 0; i < digest.length; i++) {
				sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16)
						.substring(1));
			}

			shaToString = sb.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return shaToString;
	}

	/**
	 * Erzeugt auf Basis einer regulären Definitionsdatei einen neuen minimalen
	 * DFA und gibt diesen zurück.
	 * 
	 * @param reDeFi
	 *            : Der absolute Pfad zu der Datei, die die regulären
	 *            Definitionen (<dateiname>.rd) enthält.
	 * @return: minimaler DFA, der aus der übergebenen Datei (@param: reDeFi),
	 *          erzeugt wurde.
	 */
	private static MinimalDfa<Character, StatePayload> buildMinimalDfa(
			String reDeFi) {
		MinimalDfa<Character, StatePayload> buildedMDfa = null;
		File rdFile = new File(Settings.getWorkingDirectory() + reDeFi);

		int fileEndingIndex = reDeFi.lastIndexOf(".rd");
		String dfaFileName = reDeFi.substring(0, fileEndingIndex + 1) + "dfa";

		try {
			MinimalDfaBuilder builder = new IndirectMinimalDfaBuilder();
			buildedMDfa = builder.buildMinimalDfa(rdFile);
		} catch (MinimalDfaBuilderException e) {
			e.printStackTrace();
		}
		File dfaToSave = new File(Settings.getWorkingDirectory() + dfaFileName);
		if (dfaToSave.exists()) {
			return buildedMDfa;
		} else {
			serialize(
					Settings.getWorkingDirectory() + dfaFileName,
					Settings.getWorkingDirectory() + reDeFi,
					buildedMDfa);
			return buildedMDfa;
		}
	}

}
