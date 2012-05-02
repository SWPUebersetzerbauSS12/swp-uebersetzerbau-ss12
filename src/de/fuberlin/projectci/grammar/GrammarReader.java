package de.fuberlin.projectci.grammar;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GrammarReader {
 
	/**
	 * Liest eine Textdatei mit enthaltener BNF-Grammatik und erstellt daraus ein neues
	 * Grammar Object.
	 * Die Textdatei darf auch Leerzeilen enthalten, welche beim Parsen ignoriert werden.
	 * @param filename Pfad zur Textdatei, welche die Eingabegrammatik in BNF enthält.
	 * @return Grammatik-Object mit eingelesenen Produktionen.
	 * @throws BNFParsingErrorException Falls die Textdatei keine gültige BNF
	 */
	public static Grammar readGrammar(String filename) throws BNFParsingErrorException {
		Grammar grammar = new Grammar();
		int foundedProductions = 0;
		try {
			// Textdatei zeilenweise einlesen
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line = null;
			
			while((line = reader.readLine()) != null) {
				if(line.length() > 0) { // Leerzeilen ignorieren
					//System.out.println("Parsing line..."); // TODO DEBUG (Markierung, um diese hinterher wieder leicht entfernen zu können)
					// pro Zeile genau eine Produktion parsen
					List<Production> productions = parseProduction(line, grammar);
					for(Production production : productions){
						grammar.addProduction(production);
						++foundedProductions;
					}
				}
			}
		} catch (FileNotFoundException e) {
			// Textdatei nicht gefunden oder nicht zugreifbar
			throw new BNFParsingErrorException(e.getStackTrace());
		} catch (IOException e) {
			// Lesefehler z.B. durch Interrupt
			throw new BNFParsingErrorException(e.getStackTrace());
		}
		if(foundedProductions == 0)
			System.out.println("GrammarReader: WARNING Created Grammar contains no Productions!");
		return grammar;
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
		System.out.println(string); // TODO DEBUG
		
		// Reguläre Ausdrücke
		String regexNonterminal = "(<[^>]+>)";
		String regexSymbol = "("+regexNonterminal+"|(\"[^\"]+\"))";
		String regexRightHandSite = "("+regexSymbol+"+\\|)*"+regexSymbol+"+";
		Pattern patternNonterminal = Pattern.compile(regexNonterminal); // mind. 1 Zeichen außer ">" muss innerhalb <...> liegen
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
						System.out.print("LHS: "+leftHandSiteName+" RHS: "); // TODO DEBUG
						for(Symbol s : rightHandSite) System.out.print(s.getName()); // TODO DEBUG
						System.out.print("\n"); // TODO DEBUG
						
						productions.add(new Production(leftHandSite, rightHandSite));
						rightHandSite = new LinkedList<Symbol>();
					}
				}
				System.out.print("LHS: "+leftHandSiteName+" RHS: "); // TODO DEBUG
				for(Symbol s : rightHandSite) System.out.print(s.getName()); // TODO DEBUG
				System.out.print("\n"); // TODO DEBUG
				
				productions.add(new Production(leftHandSite, rightHandSite));
			}
		} else {
			throw new BNFParsingErrorException("No guilty Production!");
		}
		
		return productions;
	}
	 
}
 
