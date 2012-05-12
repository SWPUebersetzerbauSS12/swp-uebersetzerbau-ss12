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
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import regextodfaconverter.MinimalDfa;
import tokenmatcher.StatePayload;
import utils.Notification;

/**
 * Stellt einen minimalen DFA-Provider dar, der minimale DFA's, auf Basis (von
 * mindestens) einer regulären Definitionsdatei und einem
 * {@link MinimalDfaBuilder}, erzeugt.
 * 
 * Dabei können zusätlich folgende optionale Parameter angegeben - DFA-Laden -
 * DFA-Speichern - Speicherpfad der Serialisierung
 * 
 * @author Maximilian Schröder
 * @author Daniel Rotar
 * 
 */
public class MinimalDfaProvider {

	/**
	 * Gibt einen minimalen DFA aus, der auf Basis der regulären
	 * Definitionsdatei und dem minimalen DFA-Builder erzeugt wurde.
	 * 
	 * @param rdFile
	 *            reguläre Definitionsdatei die dem minimalen DFA-Builder zur
	 *            DFA-Erzeugung übergeben wird
	 * @param builder
	 *            minimaler DFA-Builder der, mittels der übergebenen regulären
	 *            Definitionsdatei, einen minimalen DFA erzeugt
	 * @return minimaler DFA der auf Basis der regulären Definitionsdatei und
	 *         des minimalen DFA-Builders erzeugt und gegebenenfalls
	 *         deserialisiert und/oder abgespeichert wurde
	 * @throws MinimalDfaProviderException
	 *             Wenn ein Fehler beim Erstellen des DFA's auftritt.
	 */
	public static MinimalDfa<Character, StatePayload> getMinimalDfa(
			File rdFile, MinimalDfaBuilder builder)
			throws MinimalDfaProviderException {
		return getMinimalDfa(rdFile, builder, false);
	}

	/**
	 * Gibt einen minimalen DFA aus, der auf Basis der regulären
	 * Definitionsdatei und dem minimalen DFA-Builder erzeugt wurde.
	 * 
	 * @param rdFile
	 *            reguläre Definitionsdatei die dem minimalen DFA-Builder zur
	 *            DFA-Erzeugung übergeben wird
	 * @param builder
	 *            minimaler DFA-Builder der, mittels der übergebenen regulären
	 *            Definitionsdatei, einen minimalen DFA erzeugt
	 * @param skipDeserialization
	 *            Angabe, ob das Laden des minimalen DFA übersprungen werden
	 *            soll oder nicht (Standard = false)
	 * @return minimaler DFA der auf Basis der regulären Definitionsdatei und
	 *         des minimalen DFA-Builders erzeugt und gegebenenfalls
	 *         deserialisiert und/oder abgespeichert wurde
	 * @throws MinimalDfaProviderException
	 *             Wenn ein Fehler beim Erstellen des DFA's auftritt.
	 */
	public static MinimalDfa<Character, StatePayload> getMinimalDfa(
			File rdFile, MinimalDfaBuilder builder, boolean skipDeserialization)
			throws MinimalDfaProviderException {
		return getMinimalDfa(rdFile, builder, skipDeserialization, false);
	}

	/**
	 * Gibt einen minimalen DFA aus, der auf Basis der regulären
	 * Definitionsdatei und dem minimalen DFA-Builder erzeugt wurde.
	 * 
	 * @param rdFile
	 *            reguläre Definitionsdatei die dem minimalen DFA-Builder zur
	 *            DFA-Erzeugung übergeben wird
	 * @param builder
	 *            minimaler DFA-Builder der, mittels der übergebenen regulären
	 *            Definitionsdatei, einen minimalen DFA erzeugt
	 * @param skipDeserialization
	 *            Angabe, ob das Laden des minimalen DFA übersprungen werden
	 *            soll oder nicht (Standard = false)
	 * @param skipSerialization
	 *            Angabe, ob das Speichern des minimalen DFA übersprungen werden
	 *            soll oder nicht (Standard = false)
	 * @return minimaler DFA der auf Basis der regulären Definitionsdatei und
	 *         des minimalen DFA-Builders erzeugt und gegebenenfalls
	 *         deserialisiert und/oder abgespeichert wurde
	 * @throws MinimalDfaProviderException
	 *             Wenn ein Fehler beim Erstellen des DFA's auftritt.
	 */
	public static MinimalDfa<Character, StatePayload> getMinimalDfa(
			File rdFile, MinimalDfaBuilder builder,
			boolean skipDeserialization, boolean skipSerialization)
			throws MinimalDfaProviderException {
		File dfaFile = new File(rdFile.getAbsolutePath() + ".dfa");

		return getMinimalDfa(rdFile, builder, skipDeserialization,
				skipSerialization, dfaFile);
	}

	/**
	 * Gibt einen minimalen DFA aus, der auf Basis der regulären
	 * Definitionsdatei und dem minimalen DFA-Builder erzeugt wurde.
	 * 
	 * @param rdFile
	 *            reguläre Definitionsdatei die dem minimalen DFA-Builder zur
	 *            DFA-Erzeugung übergeben wird
	 * @param builder
	 *            minimaler DFA-Builder der, mittels der übergebenen regulären
	 *            Definitionsdatei, einen minimalen DFA erzeugt
	 * @param skipDeserialization
	 *            Angabe, ob das Laden des minimalen DFA übersprungen werden
	 *            soll oder nicht (Standard = false)
	 * @param skipSerialization
	 *            Angabe, ob das Speichern des minimalen DFA übersprungen werden
	 *            soll oder nicht (Standard = false)
	 * @param dfaFile
	 *            Pfad zum Ort, an dem der minimale DFA
	 *            abgespeichert/serialisiert werden soll (Standard =
	 *            rdFile+".dfa")
	 * @return minimaler DFA der auf Basis der regulären Definitionsdatei und
	 *         des minimalen DFA-Builders erzeugt und gegebenenfalls
	 *         deserialisiert und/oder abgespeichert wurde
	 * @throws MinimalDfaProviderException
	 *             Wenn ein Fehler beim Zurückgeben des DFA's auftritt.
	 */
	public static MinimalDfa<Character, StatePayload> getMinimalDfa(
			File rdFile, MinimalDfaBuilder builder,
			boolean skipDeserialization, boolean skipSerialization, File dfaFile)
			throws MinimalDfaProviderException {

		/** Auf Fehleingaben überprüfen */
		if (builder == null) {
			throw new MinimalDfaProviderException(
					"Der Parameter builder darf nicht null sein!");
		}
		if (!rdFile.exists()) {
			throw new MinimalDfaProviderException("Die angegebene Datei '"
					+ rdFile.getAbsolutePath() + "' existiert nicht!");
		}

		/** Logik */
		String version = getVersion();
		String rdFileHash;
		try {
			rdFileHash = getFilehashAsString(rdFile);
		} catch (IOException e) {
			throw new MinimalDfaProviderException("Die angegebene Datei '"
					+ rdFile.getAbsolutePath()
					+ "' konnte nicht verarbeitet werden: " + e.getMessage());
		}
		MinimalDfa<Character, StatePayload> mDfa = null;

		if (!skipDeserialization) {
			MinimalDfaCharacterStatePayloadWrapper wrapper;
			if (dfaFile.exists()) {
				try {
					wrapper = MinimalDfaCharacterStatePayloadWrapper
							.load(dfaFile);

					if (version.equals(wrapper.getVersion())) {
						if (rdFileHash.equals(wrapper.getRdFileHash())) {
							mDfa = wrapper.getMDfa();
						} else {
							Notification
									.printInfoMessage("Der Filehash in der serialisierten dfa-Datei stimmt nicht mit dem Filehash der aktuellen Definitionsdatei überein. Ein neuen DFA wird jetzt erstellt.");
						}
					} else {
						Notification
								.printInfoMessage("Die Versionsnummer in der serialisierten dfa-Datei stimmt nicht mit der aktuellen Version überein. Ein neuen DFA wird jetzt erstellt.");
					}

				} catch (Exception e) {
					Notification
							.printErrorMessage("Error: Fehler beim Deserialisieren des minimalen DFA: "
									+ e.getMessage());
				}
			} else {
				Notification
						.printInfoMessage("Keine dfa-Datei zum deserialisieren gefunden. Ein neuen DFA wird jetzt erstellt.");
			}
		}
		if (mDfa == null) {
			try {
				mDfa = builder.buildMinimalDfa(rdFile);
			} catch (MinimalDfaBuilderException e) {
				throw new MinimalDfaProviderException(
						"Fehler beim Erzeugen des minimalen Dfa: "
								+ e.getMessage());
			}
		}
		if (!skipSerialization) {
			MinimalDfaCharacterStatePayloadWrapper wrapper = new MinimalDfaCharacterStatePayloadWrapper(
					version, rdFileHash, mDfa);
			try {
				wrapper.save(dfaFile);
			} catch (IOException e) {
				Notification
						.printErrorMessage("Error: Fehler beim Serialisieren des minimalen DFA: "
								+ e.getMessage());
			}
		}

		return mDfa;
	}

	/**
	 * Erzeugt den SHA-Hash einer Datei, formt diesen in Hexadezimalformat um
	 * und erzeugt davon den String.
	 * 
	 * @param file
	 *            Datei, von der der SHA-Hash erzeugt werden soll.
	 * @return Hash-String in Hexadezimalformat zur übergebenen Datei
	 * @throws IOException
	 *             Leseproblem bzgl. der Eingabe-Datei
	 */
	private static String getFilehashAsString(File file) throws IOException {
		MessageDigest md = null;
		try {
			// Algo festlegen und Datei lesen
			md = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
			// Dieser Fall kann niemals eintreten, da seit Java 1.5 der
			// SHA-Algorithmus bekannt ist
			e.printStackTrace();
		}
		FileInputStream fis = new FileInputStream(file.getAbsolutePath());
		byte[] fileBytes = new byte[5120];
		int readbytes = 0;

		while ((readbytes = fis.read(fileBytes)) != -1) {
			md.update(fileBytes, 0, readbytes);
		}

		byte[] digest = md.digest();

		// Umwandlung von Byte in Hexadezimalformat
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < digest.length; i++) {
			sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16)
					.substring(1));
		}

		return (sb.toString());
	}
	
	/**
	 * Liest die aktuelle Version aus und gibt diese zurück.
	 * @return Die aktuelle Version.
	 */
	private static String getVersion()
	{
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			return "unknown";
		}

	    Document dom;
		try {
			dom = db.parse("pom.xml");
		} catch (Exception e) {
			return "unknown";
		}

	    Element docEle = dom.getDocumentElement();

	    NodeList nl = docEle.getElementsByTagName("version");
	    
	    return nl.item(0).getFirstChild().getNodeValue();
	}

}
