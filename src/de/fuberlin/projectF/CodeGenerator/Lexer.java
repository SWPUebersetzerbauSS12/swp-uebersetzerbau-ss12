package de.fuberlin.projectF.CodeGenerator;

import java.io.*;
import java.lang.Number;

import de.fuberlin.projectF.CodeGenerator.model.Token;
import de.fuberlin.projectF.CodeGenerator.model.TokenType;

/**
 *         Dies ist ein Lexer für einen LLVM Code. Grobe Funktionsweise: Der
 *         LLVM Code wird zeilenweise durchgeparst und es wird pro geparste
 *         Zeile ein Token erzeugt und zurückgegeben.
 * 
 *         Es gibt 3 wesentliche Funktionen: open: öffnet die LLVM-Code Datei
 *         close: schließt die LLVM-Code Datei getNextToken: liesst eine Zeile
 *         der Eingabe Datei und parst diese dann auf relevante Informationen
 *         Diese Informationen werden dann in einem Token gespeichert. Der Token
 *         wird dann zurückgegeben.
 * 
 *         Ist aber noch nicht fertig!!!!!!
 * 
 */

public class Lexer {

	FileInputStream fstream;
	DataInputStream in;
	BufferedReader br;

	public Lexer(String filename) {
		this.open(filename);
	}

	// öffnen der Datei
	public int open(String filename) {
		try {
			fstream = new FileInputStream(filename);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return 0;
	}

	public int close() {
		// schließen der Datei
		try {
			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return 0;
	}

	public Token getNextToken() {
		String strLine;
		String[] splitLine;

		try {
			// Einlesen der nächsten Zeile
			while ((strLine = br.readLine()) != null) {

				splitLine = splitInformation(strLine);
				if (splitLine.length == 0)
					continue;

				return fillToken(splitLine);
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());

		}

		Token endToken = new Token();
		endToken.setType(TokenType.EOF);
		return endToken;
	}

	private String[] splitInformation(String line) {
		line = line.trim();

		String[] tmpSplitLine;
		String[] splitLine;

		line = line.replace("(", " ( ");
		line = line.replace(")", " ) ");
		line = line.replace("[", " [ ");
		line = line.replace("]", " ] ");
		line = line.replace("{", " { ");
		line = line.replace("}", " } ");
		line = line.replace(",", " , ");
		line = line.replace(":", " : ");

		int p1 = line.lastIndexOf('(');
		int p2 = line.indexOf('(', p1);
		if(line.indexOf(')',p2+1) != -1) {
			p2 = line.indexOf(')', p2+1);
		}
		line = replaceBetween(line, p1, p2, ' ', (char) 1);

		p1 = line.indexOf(" [ ") + 1;
		p2 = line.lastIndexOf(" ] ");
		line = replaceBetween(line, p1, p2, ' ', (char) 1);

		p1 = line.lastIndexOf('{');
		p2 = line.indexOf('}', p1);
		line = replaceBetween(line, p1, p2, ' ', (char) 1);

		p1 = line.indexOf('"');
		p2 = line.indexOf('"', p1 + 1);
		line = replaceBetween(line, p1, p2, ' ', (char) 1);
		
		System.out.println("Bevor:");
		System.out.println(line);
		
		tmpSplitLine = line.split(" ");

		int count = 0;
		for (int i = 0; i < tmpSplitLine.length; i++) {
			if (tmpSplitLine[i].isEmpty()) {
			} else if (tmpSplitLine[i].contentEquals(",")) {
			} else if (tmpSplitLine[i].contentEquals("*")) {
			} else if (tmpSplitLine[i].contentEquals("i8*")) {
			} else if (tmpSplitLine[i].contentEquals("...")) {
			} else if (tmpSplitLine[i].contentEquals("(")) {
			} else if (tmpSplitLine[i].contentEquals(")")) {
			} else if (tmpSplitLine[i].contentEquals("inbounds")) {
			} else if (tmpSplitLine[i].contentEquals("nounwind")) {
			} else if (tmpSplitLine[i].contentEquals("nsw")) {
			} else if (tmpSplitLine[i].contentEquals("tail")) {
			} else if (tmpSplitLine[i].contentEquals("noreturn")) {
			} else if (tmpSplitLine[i].contentEquals("private")) {
			} else if (tmpSplitLine[i].contentEquals("unnamed_addr")) {
			} else if (tmpSplitLine[i].contentEquals("constant")) {
			} else if (tmpSplitLine[i].contentEquals("align")) {
				break;
			}

			else {
				count++;
			}
		}

		splitLine = new String[count];

		count = 0;
		for (int i = 0; i < tmpSplitLine.length; i++) {
			if (tmpSplitLine[i].isEmpty()) {
			} else if (tmpSplitLine[i].contentEquals(",")) {
			} else if (tmpSplitLine[i].contentEquals("*")) {
			} else if (tmpSplitLine[i].contentEquals("i8*")) {
			} else if (tmpSplitLine[i].contentEquals("...")) {
			} else if (tmpSplitLine[i].contentEquals("(")) {
			} else if (tmpSplitLine[i].contentEquals(")")) {
			} else if (tmpSplitLine[i].contentEquals("inbounds")) {
			} else if (tmpSplitLine[i].contentEquals("nounwind")) {
			} else if (tmpSplitLine[i].contentEquals("nsw")) {
			} else if (tmpSplitLine[i].contentEquals("tail")) {
			} else if (tmpSplitLine[i].contentEquals("noreturn")) {
			} else if (tmpSplitLine[i].contentEquals("private")) {
			} else if (tmpSplitLine[i].contentEquals("unnamed_addr")) {
			} else if (tmpSplitLine[i].contentEquals("constant")) {
			} else if (tmpSplitLine[i].contentEquals("align")) {
				break;
			}

			else {
				splitLine[count++] = new String(tmpSplitLine[i]);
			}
		}
		
		for(int i = 0; i < splitLine.length; i++)
			System.out.print(splitLine[i] + " ");
		System.out.println();
		
		return splitLine;
	}

	private String replaceBetween(String line, int startpoint, int endpoint,
			char oldChar, char newChar) {

		if (startpoint == -1)
			return line;
		if (endpoint == -1)
			return line;

		String tmpLine1, tmpLine2;
		tmpLine1 = line.substring(startpoint, endpoint + 1);
		tmpLine2 = line.substring(endpoint + 1);
		line = line.substring(0, startpoint);
		line = line.concat(tmpLine1.replace(oldChar, newChar));
		line = line.concat(tmpLine2);
		return line;
	}

	private Token fillToken(String[] line) {
		Token newToken = new Token();

		// Definitionen (bei Methodendeklarationen)
		if (line[0].contentEquals("define")) {
			newToken.setType(TokenType.Definition);
			newToken.setTarget(line[2]);
			newToken.setTypeTarget(line[1]);
			fillParameter(newToken, line[3].replace((char) 1, ' '));
		}

		// Wertzuweisungen
		else if (line[0].contentEquals("store")) {
			newToken.setType(TokenType.Assignment);
			newToken.setTarget(line[4]);
			newToken.setTypeTarget(line[3]);
			
			if(line[1].equals("double"))
				newToken.setOp1(transformInIEEE(line[2]));
			else
				newToken.setOp1(line[2]);
			
			newToken.setTypeOp1(line[1]);
		}

		else if (line[0].contentEquals("call")) {
			newToken.setType(TokenType.Call);
			newToken.setOp1(line[2]);
			newToken.setTypeTarget(line[1]);
			fillParameter(newToken, line[3].replace((char) 1, ' '));
		}

		else if (line[0].contentEquals("br")) {
			newToken.setType(TokenType.Branch);
			if(line[1].equals("label")) {
				newToken.setOp2(line[2]);
				newToken.setTypeOp2(line[1]);
			}
			else if(line[3].equals("label")) {
				newToken.setTarget(line[2]);
				
				newToken.setOp1(line[4]);
				newToken.setTypeOp1(line[3]);
				
				newToken.setOp2(line[6]);
				newToken.setTypeOp2(line[5]);
				
			}
			newToken.setTarget(line[2]);
		}

		// Return anweisungen
		else if (line[0].contentEquals("ret")) {
			newToken.setType(TokenType.Return);
			newToken.setTypeOp1(line[1]);
			if (line.length > 2) {
				newToken.setOp1(line[2]);
			}
		}

		// Ende einer Definition
		else if (line[0].contentEquals("}")) {
			newToken.setType(TokenType.DefinitionEnd);
		}

		else if (line[1].contentEquals("<label>")) {
			newToken.setType(TokenType.Label);
			newToken.setTarget(line[3]);
		}

		else if (line[1].contentEquals("=")) {

			// Typ-Definition (STRUCT, RECORD)
			if (line[2].contentEquals("type")) {
				newToken.setType(TokenType.TypeDefinition);
				newToken.setTarget(line[0]);
				fillParameter(newToken, line[3].replace((char) 1, ' '));
			}

			else if (line[0].startsWith("@.str")) {
				newToken.setType(TokenType.String);
				newToken.setTarget(line[0]);
				newToken.setTypeTarget(line[2].replace((char) 1, ' '));
				newToken.setOp1(requote(line[3].substring(1).replace((char) 1, ' ')));
				newToken.setOp2("" + line[3].length());
			}

			// Expression Int
			else if (line[2].contentEquals("add")
					|| line[2].contentEquals("sub")
					|| line[2].contentEquals("mul")
					|| line[2].contentEquals("sdiv")
					|| line[2].contentEquals("or")
					|| line[2].contentEquals("and")
					|| line[2].contentEquals("xor")) {
				// Type: ADDITION
				newToken.setType(TokenType.ExpressionInt);
				newToken.setTarget(line[0]);
				newToken.setTypeTarget(line[2]);
				newToken.setOp1(line[4]);
				newToken.setOp2(line[5]);
			}
			
			// Expression Double
			else if (line[2].contentEquals("fadd")
					|| line[2].contentEquals("fsub")
					|| line[2].contentEquals("fmul")
					|| line[2].contentEquals("fdiv")) {
				// Type: ADDITION
				newToken.setType(TokenType.ExpressionDouble);
				newToken.setTarget(line[0]);
				newToken.setTypeTarget(line[2]);
				newToken.setOp1(line[4]);
				newToken.setOp2(line[5]);
			}
			
			else if (line[2].contentEquals("sitofp")
					|| line[2].contentEquals("fptosi")) {
				
				newToken.setType(TokenType.Cast);
				newToken.setTarget(line[0]);
				newToken.setTypeTarget(line[6]);
				newToken.setOp1(line[4]);
				newToken.setTypeOp1(line[3]);
			}
			
			else if (line[2].contentEquals("getelementptr")) {
				newToken.setType(TokenType.Getelementptr);
				newToken.setTarget(line[0]);
				newToken.setTypeTarget(line[3].replace((char) 1, ' '));
				
				newToken.setOp1(line[4]);
				newToken.setOp2(line[8]);
				newToken.setTypeOp2(line[7]);
			}
	
			// Wert aus Speicher lesen
			else if (line[2].contentEquals("load")) {
				newToken.setType(TokenType.Load);
				newToken.setTarget(line[0]);
				newToken.setOp1(line[4]);
				newToken.setTypeTarget(line[3]);
			}

			// Speicher Allocierungen
			else if (line[2].contentEquals("alloca")) {
				newToken.setType(TokenType.Allocation);
				newToken.setTarget(line[0]);
				newToken.setTypeTarget(line[3].replace((char) 1, ' '));
			} else if (line[2].contentEquals("call")) {
				newToken.setTarget(line[0]);
				newToken.setTypeTarget(line[3]);
				
				newToken.setType(TokenType.Call);
				newToken.setOp1(line[4]);
				
				if(line[4].equals("@printf")) {
					fillParameter(newToken, line[6].replace((char) 1, ' '));
					newToken.removeParameters(1);
					newToken.removeParameters(1);
				} else {
					fillParameter(newToken, line[5].replace((char) 1, ' '));
				}
			}

			else if (line[2].contentEquals("icmp")) {
				newToken.setType(TokenType.Compare);
				newToken.setTarget(line[0]);
				newToken.setTypeTarget(line[3]);
				newToken.setOp1(line[5]);
				newToken.setTypeOp1(line[4]);
				newToken.setOp2(line[6]);

			} else
				newToken.setType(TokenType.Undefined);
		} else
			newToken.setType(TokenType.Undefined);

		return newToken;
	}

	private String transformInIEEE(String string) {
		System.out.println("String: " + string);
		String[] sString = string.split("e");
		
		System.out.println(sString[0]);
		System.out.println(sString[1]);
		double result = Double.parseDouble(sString[0]);
		if(sString[1].charAt(0) == '-') {
			sString[1] = sString[1].substring(1);
			for(int i = Integer.parseInt(sString[1]); i > 0; i--) {
				result = result / 10;
			}
			
		} else {
			sString[1] = sString[1].substring(1);
			for(int i = Integer.parseInt(sString[1]); i > 0; i--) {
				result = result * 10;
			}
		}
		//TODO
		
		long tmp = Double.doubleToLongBits(result);
		String tmp2 = Long.toHexString(tmp);
		return new String("0x" + tmp2);
	}

	private String requote(String string) {
		//TODO
		string = string.replace("\\0A", "\\n");
		string = string.replace("\\09", "\\t");
		string = string.replace("\\00", "\\0");
		
		return string;
	}

	private void fillParameter(Token newToken, String line) {
		line = line.replace('(', ' ');
		line = line.replace(')', ' ');
		line = line.replace('{', ' ');
		line = line.replace('}', ' ');
		
		line = line.trim();
		if(line.isEmpty())
			return;
		
		String[] pair = line.split(",");
		for(int i = 0; i < pair.length; i++) {
			pair[i] = pair[i].trim();
			int p1 = pair[i].indexOf('[');
			int p2 = pair[i].indexOf(']');
			pair[i] = replaceBetween(pair[i], p1, p2, ' ', (char) 1);
		}
		
		for(String p : pair)
			System.out.println(p);
		
		for(String p : pair) {
			String[] pairValue = p.split(" ");
			pairValue[0] = pairValue[0].replace((char)1, ' ');
			if(pairValue.length > 2)
				newToken.addParameter(pairValue[2], pairValue[0]);
			else if(pairValue.length > 1)
				newToken.addParameter(pairValue[1], pairValue[0]);
			else
				newToken.addParameter("", pairValue[0]);
		}
	}
}