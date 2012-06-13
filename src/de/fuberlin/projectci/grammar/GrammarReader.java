package de.fuberlin.projectci.grammar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GrammarReader {
	
	/**
	 * Liest von einem Reader-Objekt die BNF-Grammatik ein und erstellt daraus ein neues
	 * Grammar-Objekt.
	 * @param r Reader-Objekt der BNF-Grammatik. Produktionen müssen mit Zeilenumbrüchen getrennt sein.
	 * @return Grammatik-Object mit eingelesenen Produktionen.
	 * @throws BNFParsingErrorException Falls das Reader-Objekt keine gültige BNF-Grammatik
	 * einlesen konnte.
	 */
	public static Grammar readGrammar(Reader r) throws BNFParsingErrorException {
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
						// Annahme: erste Produktion enthält Startsymbol (TODO evtl. ändern)
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
			System.out.println("GrammarReader: WARNING Created grammar contains no productions!");

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
	public static Grammar readGrammar(File file) throws BNFParsingErrorException{
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
	
	// XXX Warum sind readGrammar und parseProduction statisch?
	/* XXX Vorschlag: Mehrere überladene Signaturen für readGrammar: Implementierung in readGrammar(Reader reader) und Aufruf in readGrammar(String), readGrammar(File) etc
	 * 	-->	Dann kann man den GrammarReader zum Testen auch mit einem StringReader aufrufen
	*/
	public static Grammar readGrammar(String filename) throws BNFParsingErrorException {
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
	private static List<Production> parseProduction(String string, Grammar grammar) throws BNFParsingErrorException{
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
					} else { // "|" gematched, lege neue Produktion an
						// System.out.print("LHS: "+leftHandSiteName+" RHS: "); // TODO DEBUG
						//for(Symbol s : rightHandSite) System.out.print(s.getName()); // TODO DEBUG
						//System.out.print("\n"); // TODO DEBUG
						
						productions.add(new Production(leftHandSite, rightHandSite));
						rightHandSite = new LinkedList<Symbol>();
					}
				}
				//System.out.print("LHS: "+leftHandSiteName+" RHS: "); // TODO DEBUG
				//for(Symbol s : rightHandSite) System.out.print(s.getName()); // TODO DEBUG
				//System.out.print("\n"); // TODO DEBUG
				
				productions.add(new Production(leftHandSite, rightHandSite));
			} 
		} else {
			throw new BNFParsingErrorException("Illegal production!");
		}
		
		return productions;
	}
	 
}
 