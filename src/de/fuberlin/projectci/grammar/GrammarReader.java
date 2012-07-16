package de.fuberlin.projectci.grammar;

import java.io.File;
import java.io.Reader;

/**
 * 
 * Interface für das allgemeine Einlesen einer Grammatik, die in Textform abgespeichert ist.
 *
 */
public interface GrammarReader {
	
	/**
	 * Liest eine Grammatik ein und erstellt daraus ein Grammar-Objekt.
	 * @param reader Reader-Objekt
	 * @return Grammar-Objekt
	 * @throws RuntimeException falls die Grammatik-Datei ein ungültiges Format besitzt oder nicht lesbar ist.
	 */
	public Grammar readGrammar(Reader reader);
	
	/**
	 * Liest eine Grammatik ein und erstellt daraus ein Grammar-Objekt.
	 * @param file File-Objekt
	 * @return Grammar-Objekt
	 * @throws RuntimeException falls die Grammatik-Datei ein ungültiges Format besitzt oder nicht lesbar ist.
	 */
	public Grammar readGrammar(File file);
	
	/**
	 * Liest eine Grammatik ein und erstellt daraus ein Grammar-Objekt.
	 * @param filename Pfad zur Grammatik-Datei
	 * @return Grammar-Objekt
	 * @throws RuntimeException falls die Grammatik-Datei ein ungültiges Format besitzt oder nicht lesbar ist.
	 */
	public Grammar readGrammar(String filename);
	 
}
 