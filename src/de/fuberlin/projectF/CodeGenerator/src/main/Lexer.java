package main;
import java.io.*;

import main.model.Token;
import main.model.TokenType;

/**
 * 
 * @author Peter Hirschfeld
 * 
 * Dies ist ein Lexer für einen LLVM Code.
 * Grobe Funktionsweise:
 * 		Der LLVM Code wird zeilenweise durchgeparst und es wird pro geparste Zeile ein Token erzeugt
 * 		und zurückgegeben.
 * 
 * 		Es gibt 3 wesentliche Funktionen:
 * 			open: öffnet die LLVM-Code Datei
 * 			close: schließt die LLVM-Code Datei
 * 			getNextToken: liesst eine Zeile der Eingabe Datei und parst diese dann auf relevante Informationen
 * 			Diese Informationen werden dann in einem Token gespeichert. Der Token wird dann zurückgegeben.
 *
 * Ist aber noch nicht fertig!!!!!! 
 *
 */

public class Lexer {

	FileInputStream fstream;
	DataInputStream in;
	BufferedReader br;
	
	public Lexer() {}
	
	//öffnen der Datei
	public int open(String filename) {
		try{
			fstream = new FileInputStream(filename);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));

			}catch (Exception e){
			  System.err.println("Error: " + e.getMessage());
			}
		return 0;
	}
	
	public int close() {
		//schließen der Datei
		try{
		 in.close();
		}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
		return 0;
	}
	
	public Token getNextToken() {
		String strLine;
		
		try {
			//Einlesen der nächsten Zeile
			while((strLine = br.readLine()) != null) {
				
				strLine = filterLine(strLine);
				
				//Wenn Zeile "leer" dann neue Zeile lesen
				if(strLine.length() == 0)
					continue;
				
				//Wenn Zeile nicht "leer" dann Token erzeugen und zurückgeben
				
				return fillToken(strLine);
			}
		} catch(Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		
		Token endToken = new Token();
		endToken.setType(TokenType.EOF);
		return endToken;
	}

	private String filterLine(String strLine) {
		strLine = strLine.trim();
		
		String[] strings;
		
		strings = strLine.split(" ");
		strLine = "";
		
		for(int i = 0; i < strings.length; i++) {
			if(strings[i].isEmpty())
				continue;
			strLine = strLine.concat(strings[i]);
			if(i != strings.length - 1)
				strLine = strLine.concat(" ");
		}
		
		return strLine;
	}

	private Token fillToken(String strLine) {
		Token newToken = new Token();
		
		// Zuweisungszeilen
		if(strLine.contains(" = ")) {
			//Typ-Definition (STRUCT, RECORD)
			if(strLine.contains(" type ")) {
				newToken.setType(TokenType.TypeDefinition);
				newToken.setTarget(strLine.substring(0, strLine.indexOf(' ')));
				fillParameter(newToken, strLine);

			}
	
			//Additionen
			else if(strLine.contains(" add ") || strLine.contains(" fadd ") ) {
				//Type: ADDITION
				newToken.setType(TokenType.Addition);
				newToken.setTarget(strLine.substring(0, strLine.indexOf(' ')));
				
				int p1 = strLine.indexOf('%', 1);
				int p2 = strLine.indexOf('%', p1 + 1);
				int komma = strLine.indexOf(',', p1);
				
				newToken.setOp1(strLine.substring(p1, komma));
				newToken.setOp2(strLine.substring(p2));
				
				p2 = strLine.substring(0, p1).lastIndexOf(' ');
				p1 = strLine.substring(0, p2 - 1).lastIndexOf(' ');
				
				newToken.setTypeTarget(strLine.substring(p1 + 1, p2));
			}
			//Subtraktionen
			else if(strLine.contains(" sub ")) {
				newToken.setType(TokenType.Subtraction);
				newToken.setTarget(strLine.substring(0, strLine.indexOf(' ')));
				
				int p1 = strLine.indexOf('%', 1);
				int p2 = strLine.indexOf('%', p1 + 1);
				int komma = strLine.indexOf(',', p1);
				
				newToken.setOp1(strLine.substring(p1, komma));
				newToken.setOp2(strLine.substring(p2));
				
				p2 = strLine.substring(0, p1).lastIndexOf(' ');
				p1 = strLine.substring(0, p2 - 1).lastIndexOf(' ');
				
				newToken.setTypeTarget(strLine.substring(p1 + 1, p2));
			}
			//Wert aus Speicher lesen
			else if(strLine.contains(" load ")) {
				newToken.setType(TokenType.Load);
				newToken.setTarget(strLine.substring(0, strLine.indexOf(' ')));
				
				int p1 = strLine.indexOf('%', 1);
				int p2 = strLine.indexOf(',', p1 + 1);
				
				if(p2 == -1)
					p2 = strLine.length() - 1;
					
				newToken.setOp1(strLine.substring(p1, p2));
				
				p2 = strLine.substring(0, p1).lastIndexOf(' ');
				p1 = strLine.substring(0, p2 - 1).lastIndexOf(' ');
				
				newToken.setTypeOp1(strLine.substring(p1 + 1, p2));
			}
			
			//Speicher Allocierungen
			//TODO
			else if(strLine.contains(" alloca ") ) {
				newToken.setType(TokenType.Allocation);
				newToken.setTarget(strLine.substring(0, strLine.indexOf(' ')));
				
				int p1 = strLine.indexOf("alloca");
				p1 = strLine.indexOf(' ',p1) + 1;
				
				int p2 = strLine.indexOf(',',p1+1);
				if(p2 == -1)
					p2 = strLine.length();
				
				newToken.setTypeTarget(strLine.substring(p1, p2));
			}
			else
				newToken.setType(TokenType.Undefined);
		}
		
		//Definitionen (bei Methodendeklarationen)
		else if(strLine.contains("define ") && strLine.charAt(0) != '%') {
			newToken.setType(TokenType.Definition);
			
			int p1 = strLine.indexOf('@') + 1;
			int p2 = strLine.indexOf('(', p1);
			
			newToken.setTarget(strLine.substring(p1, p2));
			
			p2 = strLine.substring(0, p1).lastIndexOf(' ');
			p1 = strLine.substring(0, p2 - 1).lastIndexOf(' ');
			
			newToken.setTypeTarget(strLine.substring(p1 + 1, p2));
			
			fillParameter(newToken, strLine);
		}
		
		// Wertzuweisungen
		else if(strLine.contains("store ") && strLine.charAt(0) != '%') {
			newToken.setType(TokenType.Assignment);
			
			int p1 = strLine.indexOf(' ', 1) + 1;
			int p2 = strLine.indexOf(' ', p1);
			
			newToken.setTypeOp1(strLine.substring(p1, p2));
			
			p1 = strLine.indexOf(',', p2);
			
			newToken.setOp1(strLine.substring(++p2, p1));
			
			p2 = strLine.indexOf(' ', p1 + 2);
			
			newToken.setTypeTarget(strLine.substring(p1 + 2, p2));
			
			p1 = strLine.indexOf(',', ++p2);
			if(p1 == -1)
				p1 = strLine.length();
			
			newToken.setTarget(strLine.substring(p2, p1));
		}
		
		//Return anweisungen
		else if(strLine.contains("ret ") && strLine.charAt(0) != '%') {
			newToken.setType(TokenType.Return);
			int p1 = strLine.indexOf(' ', 1) + 1;
			int p2 = strLine.indexOf(' ', p1);
			
			if(p2 == -1) {
				newToken.setOp1(strLine.substring(p1, strLine.length()));
				return newToken;
			}
			newToken.setTypeOp1(strLine.substring(p1, p2));
			
			p1 = strLine.length();
			
			newToken.setOp1(strLine.substring(++p2, p1));
		}
		
		//Ende einer Definition
		else if(strLine.compareTo("}") == 0 )
			newToken.setType(TokenType.DefinitionEnd);
		else
			newToken.setType(TokenType.Undefined);
		
		return newToken;
	}

	private void fillParameter(Token newToken, String strLine) {
		int p1 = strLine.indexOf('(');
		String pair;
		String[] split;
		
		if (p1==-1)
			p1=strLine.indexOf('{');
		int p2 = p1;

		//TODO: unschön aber funktioniert erstmal
		while(true) {
			do {
				p2++;
			}while(strLine.charAt(p2) != ')' && strLine.charAt(p2) != ',' && strLine.charAt(p2) != '}');
			p1++;
			pair = strLine.substring(p1, p2);
			pair = pair.trim();
			if(pair.isEmpty())
				break;
			
			split=pair.split(" ");
			
			if (split.length>1)
				newToken.addParameter(split[1], split[0]);
			else 
				newToken.addParameter("", split[0]);

			p1 = p2;
			if(strLine.charAt(p2) == ')' || strLine.charAt(p2) == '}')
				break;
			while(strLine.charAt(p1++) == ' ');
		}
	}
}
