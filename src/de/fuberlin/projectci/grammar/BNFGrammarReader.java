package de.fuberlin.projectci.grammar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.fuberlin.commons.util.LogFactory;

/**
 * 
 * Implementierung eines GrammarReaders für Grammatiken in einer simplen Backus-Naur-Form.<br />
 * Zu beachten:<br />
 * 		<ul>
 * 		<li> jede Produktion in einer Zeile</li>
 * 		<li> Zeichenfolge ::= als Definitionssymbol</li>
 * 		<li> vertikaler Strich | als Zeichen für eine Alternative</li>
 * 		<li> Nichtterminale in spitzen Klammern eingeschlossen <...></li>
 * 		<li> Terminale in doppelten Anführungszeichen "..."</li>
 * 		<li> Startsymbol der Grammatik ist das Nichtterminal der linken Regelseite(LHS) der 1. eingelesenen Produktion</li>
 * 		<li> Das leere Wort wird mit dem Terminal "@" dargestellt.</li>
 * 		<li> Leerzeilen zwischen den Produktionen möglich</li>
 *  	<li> Leerzeichen innerhalb von Produktionen möglich</li>
 * 		</ul>
 * @see <a href="http://de.wikipedia.org/wiki/Backus-Naur-Form">Wikipedia: Backus-Naur-Form</a>
 */
public class BNFGrammarReader implements GrammarReader{
	private static Logger logger=LogFactory.getLogger(BNFGrammarReader.class);
	
	/**
	 * Liest von einem Reader-Objekt die BNF-Grammatik ein und erstellt daraus ein neues
	 * Grammar-Objekt.
	 * @param r Reader-Objekt der BNF-Grammatik. Produktionen müssen mit Zeilenumbrüchen getrennt sein.
	 * @return Grammatik-Object mit eingelesenen Produktionen.
	 * @throws BNFParsingErrorException Falls das Reader-Objekt keine gültige BNF-Grammatik
	 * einlesen konnte.
	 */
	public Grammar readGrammar(Reader r) throws BNFParsingErrorException {
		logger.fine("Reading grammar file...");
		Grammar grammar = new Grammar();
		int foundProductions = 0;
		int lineNumber = 0;
		try {
			// Textdatei zeilenweise einlesen
			BufferedReader reader = new BufferedReader(r);
			String line = null;
			
			while((line = reader.readLine()) != null) {
				++lineNumber;
				if(line.length() > 0) { // Leerzeilen ignorieren
					// pro Zeile genau eine Produktion parsen
					List<Production> productions = parseProduction(line, grammar);
					for(Production production : productions){
						grammar.addProduction(production);
						++foundProductions;
						// Annahme: erste Produktion enthält Startsymbol
						if(foundProductions == 1)
							grammar.setStartSymbol(production.getLhs());
					}
				}
			}
		} catch (FileNotFoundException e) {
			// Textdatei nicht gefunden oder nicht zugreifbar
			throw new BNFParsingErrorException(e);
		} catch (IOException e) {
			// Lesefehler z.B. durch Interrupt
			throw new BNFParsingErrorException(e);
		} catch (BNFParsingErrorException e) {
			// falls beim Parsen einer Zeile ein Fehler auftritt, wird Zeilennummer mit angegeben
			throw new BNFParsingErrorException(e.getMessage()+" (Line "+lineNumber+")");
		}
		
		if(foundProductions == 0)
			logger.info("WARNING Created grammar contains no productions!");
		
		logger.info("Grammar successfully built.");
		return grammar;
	}
	
	/**
	 * Erstellt einen Reader für ein gegebenes File-Objekt, welches die BNF-Grammatik enthält
	 * und erstellt daraus ein neues Grammar-Objekt.
	 * @param file File-Objekt, das auf eine BNF-Grammatik-Datei verweist.
	 * @return Grammatik-Object mit eingelesenen Produktionen.
	 * @throws BNFParsingErrorException Falls das File-Objekt keine gültige BNF enthält 
	 * oder nicht gelesen werden konnte.
	 */
	public Grammar readGrammar(File file) throws BNFParsingErrorException{
		FileReader reader = null;
		
		try {
			reader = new FileReader(file);
		} catch (FileNotFoundException e) {
			// Textdatei nicht gefunden oder nicht zugreifbar
			throw new BNFParsingErrorException(e);
		}

		return readGrammar(reader);
	}
 
	/**
	 * Liest eine Textdatei mit enthaltener BNF-Grammatik und erstellt daraus ein neues
	 * Grammar-Objekt.
	 * Die Textdatei darf auch Leerzeilen enthalten, welche beim Parsen ignoriert werden.
	 * @param filename Pfad zur Textdatei, welche die Eingabegrammatik in BNF enthält.
	 * @return Grammatik-Object mit eingelesenen Produktionen.
	 * @throws BNFParsingErrorException Falls die Textdatei keine gültige BNF ist.
	 */
	public Grammar readGrammar(String filename) throws BNFParsingErrorException {
		FileReader reader = null;
		
		try {
			reader = new FileReader(filename);
		} catch (FileNotFoundException e) {
			// Textdatei nicht gefunden oder nicht zugreifbar
			throw new BNFParsingErrorException(e);
		}

		return readGrammar(reader);
	}
	
	/**
	 * Untersucht einen String nach einer gültigen Produktion.
	 * @param string Eingabestring
	 * @param grammar Eingabegrammatik
	 * @return Produktions-Objekt
	 * @throws BNFParsingErrorException Falls der String keine gültige Produkton ist.
	 */
	private List<Production> parseProduction(String string, Grammar grammar) throws BNFParsingErrorException{
		// alle Whitespaces entfernen
		string = string.replaceAll("\\s", "");
		
		// Reguläre Ausdrücke
		String regexNonterminal = "(<[^<>]+>)"; // mind. 1 Zeichen außer "< und >" muss innerhalb <...> liegen
		String regexSymbol = "("+regexNonterminal+"|(\"[^\"]+\"))"; // Nichterminale und Terminale
		String regexRightHandSite = "("+regexSymbol+"+\\|)*"+regexSymbol+"+";
		Pattern patternNonterminal = Pattern.compile(regexNonterminal);
		Pattern patternSymbol = Pattern.compile(regexSymbol+"|\\|"); // Nichterminale, Terminale und "|"
		Pattern patternProduction = Pattern.compile(regexNonterminal+"::="+regexRightHandSite); // auf generelles Schema prüfen
		
		// Variablen für zu findende LHS, RHS und Liste für Produktionen
		NonTerminalSymbol leftHandSite = null;
		List<Symbol> rightHandSite = null;
		List<Production> productions = new LinkedList<Production>();
		
		// Parsen
		Matcher matcher = patternProduction.matcher(string);
		if(matcher.matches()){ // Produktion gültig? (LHS::=RHS1|RHS2...)
			
			// Linke Regelseite bestimmen (muss 1 Nonterminal enthalten)
			matcher = patternNonterminal.matcher(string);
			
			if(matcher.find()){ // Nonterminal gefunden
				// zu LHS Variable hinzufügen
				String leftHandSiteName = matcher.group();
				leftHandSiteName = leftHandSiteName.replaceAll("[<>]", "");
				leftHandSite = grammar.createNonTerminalSymbol(leftHandSiteName);
				
				// Rechte Regelseite abarbeiten
				rightHandSite = new LinkedList<Symbol>(); // lege Symbolliste an
				string = string.substring(leftHandSiteName.length()+5); // verkürze String ab Postition nach "::="

				// einzelne Symbole parsen und bei "|" neue Produktionen erstellen
				matcher = patternSymbol.matcher(string);
				while(matcher.find()){
					String symbol = matcher.group();
					if(symbol.startsWith("<")){ // Nonterminal zu RHS hinzufügen
						symbol = symbol.replaceAll("[<>]", "");
						// lasse von Grammar entweder Neues erstellen oder Referenz auf Vorhandenes zurückgeben
						rightHandSite.add(grammar.createNonTerminalSymbol(symbol));
						
					} else if(symbol.startsWith("\"")) { // Terminal zu RHS hinzufügen
						symbol = symbol.replaceAll("\"", "");
						rightHandSite.add(grammar.createTerminalSymbol(symbol));
						
					} else { // Alternative "|" gematched, lege neue Produktion an						
						Production p = new Production(leftHandSite, rightHandSite);
						productions.add(p);
						rightHandSite = new LinkedList<Symbol>();
						
						logger.finer("Produktion hinzugefügt: " +p);
					}
				}

				Production p = new Production(leftHandSite, rightHandSite);
				productions.add(p);
				
				logger.finer("Produktion hinzugefügt: " +p);
			} 
		} else {
			throw new BNFParsingErrorException("Illegal production!");
		}
		
		return productions;
	}
	 
}
 