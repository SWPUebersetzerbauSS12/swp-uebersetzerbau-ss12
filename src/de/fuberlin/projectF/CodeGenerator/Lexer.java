package de.fuberlin.projectF.CodeGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.fuberlin.projectF.CodeGenerator.model.Token;
import de.fuberlin.projectF.CodeGenerator.model.TokenType;

public abstract class Lexer {
	
	ArrayList<Token> tokenStream;
	HashMap<String,ArrayList<String>> renameCandidate;
	Debuginfo debug;
	
	// Diese Methoden sind in den Unterklassen implementiert
	public abstract int close();
	public abstract Token getNextToken();
	
	public Lexer(Debuginfo debug) {
		this.debug = debug;
	}
	
	protected ArrayList<Token> getTokenStream() {
		tokenStream = new ArrayList<Token>();
		renameCandidate = new HashMap<String,ArrayList<String>> ();
		Token tok;
		while ((tok = getNextToken()).getType() != TokenType.EOF) {
			tokenStream.add(tok);
		}
		
		debug.println("\n\tPostprocessing of token stream -->");
		postprocessing();
		debug.println("\t<-- end of postprocessing");
		
		return tokenStream;
	}
	
	protected String[] splitInformation(String line) {
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
		line = line.replace("\t", " ");

		int p1 = line.lastIndexOf('(');
		int p2 = line.indexOf(')', p1);
		if(line.indexOf(')',p2+1) != -1) {
			p2 = line.indexOf(')', p2+1);
		}
		line = replaceBetween(line, p1, p2, ' ', (char) 1);

		p1 = line.lastIndexOf(" [ ") + 1;
		p2 = p1;
		while(p1 != 0) {
			p2 = line.indexOf(" ] ",p1 + 1);
			line = replaceBetween(line, p1, p2, ' ', (char) 1);
			p1 = line.lastIndexOf(" [ ") + 1;
		}

		p1 = line.lastIndexOf('{');
		p2 = line.indexOf('}', p1);
		line = replaceBetween(line, p1, p2, ' ', (char) 1);
		
		p1 = line.indexOf('"');
		p2 = line.indexOf('"', p1 + 1);
		line = replaceBetween(line, p1, p2, ' ', (char) 1);
		
		tmpSplitLine = line.split(" ");

		int count = 0;
		for (int i = 0; i < tmpSplitLine.length; i++) {
			if (tmpSplitLine[i].isEmpty()) {
			} else if (tmpSplitLine[i].contentEquals(",")) {
			} else if (tmpSplitLine[i].contentEquals("*")) {
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

	protected Token fillToken(String[] line) {
		Token newToken = new Token();

		// Definitionen (bei Methodendeklarationen)
		if (line[0].contentEquals("define")) {
			debug.println("\t\tFound function definition");
			newToken.setType(TokenType.Definition);
			newToken.setTarget(line[2]);
			newToken.setTypeTarget(line[1]);
			fillParameter(newToken, line[3].replace((char) 1, ' '));
		}
		
		// Declaration (bei externer Methodendeklarationen)
		else if (line[0].contentEquals("declare")) {
			debug.println("\t\tFound extern function declaration");
			newToken.setType(TokenType.Declare);
			newToken.setTarget(line[2]);
		}

		// Wertzuweisungen
		else if (line[0].contentEquals("store")) {
			if(line[2].startsWith("c\"")) {
				debug.println("\t\tFound inline string definition");
				newToken.setType(TokenType.String);
				newToken.setOp1(line[2].substring(1));
				newToken.setTarget("@_str" + line[4].substring(1));
				
				renameCandidate.put(newToken.getTarget(),new ArrayList<String>());
				renameCandidate.get(newToken.getTarget()).add(line[4]);
			}	
			else {
				debug.println("\t\tFound Assignment");
				newToken.setType(TokenType.Assignment);
				newToken.setTarget(line[4]);
				newToken.setTypeTarget(line[3].replace((char) 1, ' '));
				
				if(line[1].equals("double"))
					if(line[2].charAt(0) == '%')
						newToken.setOp1(line[2]);
					else
						newToken.setOp1(transformInIEEE(line[2]));
				else
					newToken.setOp1(line[2]);
				
				newToken.setTypeOp1(line[1].replace((char) 1, ' '));
			}
		}

		else if (line[0].contentEquals("call")) {
			debug.print("\t\tFound call of ");
			newToken.setTypeTarget(line[1]);
			
			int i;
			for(i = 0; i < line.length; i++)
				if(line[i].charAt(0) == '@')
					break;
			
			debug.println(line[i]);
			newToken.setType(TokenType.Call);
			
			newToken.setOp1(line[i]);
			
			if(line[i].equals("@printf")) {
				int j;
				for(j = i; j < line.length; j++)
					if(line[j].charAt(0) == '(')
						break;
					
				fillParameter(newToken, line[j].replace((char) 1, ' '));
				
				if(line[j].indexOf(')') != line[j].lastIndexOf(')')) {
					newToken.removeParameters(1);
					newToken.removeParameters(1);
				}
			} else {
				int j;
				for(j = i; j < line.length; j++)
					if(line[j].charAt(0) == '(')
						break;
				fillParameter(newToken, line[j].replace((char) 1, ' '));
			}
		}

		else if (line[0].contentEquals("br")) {
			debug.println("\t\tFound branch ");
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
			debug.println("\t\tFound return ");
			newToken.setType(TokenType.Return);
			newToken.setTypeOp1(line[1]);
			if (line.length > 2) {
				newToken.setOp1(line[2]);
			}
		}

		// Ende einer Definition
		else if (line[0].contentEquals("}")) {
			debug.println("\t\tFound end of current function ");
			newToken.setType(TokenType.DefinitionEnd);
		}
		
		else if (line[1].contentEquals(":")) {
			debug.println("\t\tFound label " + line[0]);
			newToken.setType(TokenType.Label);
			newToken.setTarget(line[0]);
		}

		else if (line[1].contentEquals("=")) {

			// Typ-Definition (STRUCT, RECORD)
			if (line[2].contentEquals("type")) {
				debug.println("\t\tFound a new type definition ");
				newToken.setType(TokenType.TypeDefinition);
				newToken.setTarget(line[0]);
				fillParameter(newToken, line[3].replace((char) 1, ' '));
			}

			else if (line[0].startsWith("@.str")) {
				debug.println("\t\tFound a global string definition ");
				newToken.setType(TokenType.String);
				newToken.setTarget(line[0]);
				newToken.setTypeTarget(line[2].replace((char) 1, ' '));
				newToken.setOp1(line[3].substring(1).replace((char) 1, ' ')
						.replace(" ( ", "(")
						.replace(" ) ", ")")
						.replace(" [ ", "[")
						.replace(" ] ", "]")
						.replace(" { ", "{")
						.replace(" } ", "}")
						.replace(" , ", ",")
						.replace(" : ", ":"));
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
				debug.println("\t\tFound an integer expression");
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
				debug.println("\t\tFound a double expression");
				newToken.setType(TokenType.ExpressionDouble);
				newToken.setTarget(line[0]);
				newToken.setTypeTarget(line[2]);
				if(line[4].contains("e"))
					line[4] = transformInIEEE(line[4]);
				newToken.setOp1(line[4]);
				if(line[5].contains("e"))
					line[5] = transformInIEEE(line[5]);
				newToken.setOp2(line[5]);
			}
			
			else if (line[2].contentEquals("sitofp")
					|| line[2].contentEquals("fptosi")) {
				debug.println("\t\tFound cast from integer to double");
				newToken.setType(TokenType.Cast);
				newToken.setTarget(line[0]);
				newToken.setTypeTarget(line[6]);
				newToken.setOp1(line[4]);
				newToken.setTypeOp1(line[3]);
			}
			
			else if (line[2].contentEquals("getelementptr")) {
				debug.println("\t\tFound a pointer declaration");
				newToken.setType(TokenType.Getelementptr);
				newToken.setTarget(line[0]);
				newToken.setTypeTarget(line[3].replace((char) 1, ' '));
				
				newToken.setOp1(line[4]);
				newToken.setOp2(line[8]);
				newToken.setTypeOp2(line[7]);
			}
	
			// Wert aus Speicher lesen
			else if (line[2].contentEquals("load")) {
				debug.println("\t\tFound load");
				newToken.setType(TokenType.Load);
				newToken.setTarget(line[0]);
				newToken.setOp1(line[4]);
				newToken.setTypeTarget(line[3]);
			}

			// Speicher Allocierungen
			else if (line[2].contentEquals("alloca")) {
				debug.println("\t\tFound an allocation");
				newToken.setType(TokenType.Allocation);
				newToken.setTarget(line[0]);
				newToken.setTypeTarget(line[3].replace((char) 1, ' '));
				
			} else if (line[2].contentEquals("call")) {
				debug.print("\t\tFound a call to ");
				newToken.setTarget(line[0]);
				newToken.setTypeTarget(line[3]);
				
				int i;
				for(i = 0; i < line.length; i++)
					if(line[i].charAt(0) == '@')
						break;
				
				debug.println(line[i]);
				newToken.setType(TokenType.Call);
				
				newToken.setOp1(line[i]);
				
				if(line[i].equals("@printf")) {
					int j;
					for(j = i; j < line.length; j++)
						if(line[j].charAt(0) == '(')
							break;
						
					fillParameter(newToken, line[j].replace((char) 1, ' '));
					
					if(line[j].indexOf(')') != line[j].lastIndexOf(')')) {
						newToken.removeParameters(1);
						newToken.removeParameters(1);
					}
				} else {
					int j;
					for(j = i; j < line.length; j++)
						if(line[j].charAt(0) == '(')
							break;
					fillParameter(newToken, line[j].replace((char) 1, ' '));
				}
			}

			else if (line[2].contentEquals("icmp")) {
				debug.println("\t\tFound a comparism");
				newToken.setType(TokenType.CompareInteger);
				newToken.setTarget(line[0]);
				newToken.setTypeTarget(line[3]);
				newToken.setOp1(line[5]);
				newToken.setTypeOp1(line[4]);
				newToken.setOp2(line[6]);
				
			} else if (line[2].contentEquals("fcmp")) {
				debug.println("\t\tFound a comparism");
				newToken.setType(TokenType.CompareDouble);
				newToken.setTarget(line[0]);
				newToken.setTypeTarget(line[3]);
				newToken.setOp1(line[5]);
				newToken.setTypeOp1(line[4]);
				newToken.setOp2(line[6]);

			} else {
				debug.println("\t\tToken is undefined");
				newToken.setType(TokenType.Undefined);
			}
		} else {
			debug.println("\t\tToken is undefined");
			newToken.setType(TokenType.Undefined);
		}
		return newToken;
	}

	private String transformInIEEE(String string) {
		String[] sString = string.split("e");
		
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
		
		long tmp = Double.doubleToLongBits(result);
		String tmp2 = Long.toHexString(tmp);
		return new String("0x" + tmp2);
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
		
		for(String p : pair) {
			String[] pairValue = p.split(" ");
			pairValue[0] = pairValue[0].replace((char)1, ' ');
			if(pairValue.length > 2)
				newToken.addParameter(pairValue[2], pairValue[0]);
			else if(pairValue.length > 1) {
				if(!pairValue[1].startsWith("%") && pairValue[1].contains("e"))
					pairValue[1] = transformInIEEE(pairValue[1]);
				newToken.addParameter(pairValue[1], pairValue[0]);
			}
			else
				newToken.addParameter("", pairValue[0]);
		}
	}
	
	private void postprocessing() {
		//inline strings umbenennen 
		for (Map.Entry<String, ArrayList<String>> entry : renameCandidate.entrySet()) {
			String key = entry.getKey();
		    // umbenennene
		    for(int i = 0; i < tokenStream.size(); i++)
		    	if(tokenStream.get(i).getType() == TokenType.Getelementptr)
		    		for(int k = 0; k < entry.getValue().size(); k++)
			    		if(tokenStream.get(i).getOp1().equals(entry.getValue().get(k)))
			    			tokenStream.get(i).setOp1(key);
		}
		
		//sprünge handhaben
		HashMap<String,HashMap<Integer, Integer>> contexts = new HashMap<String,HashMap<Integer, Integer>>();
		String currentContext = "";
		int currentBlock = 0;
		int var = 0;
		//in Blöcke unterteilen und größte Variable im Block ermitteln
		for(int i = 0; i < tokenStream.size(); i++) {
			Token tok = tokenStream.get(i);
			if(tok.getType() == TokenType.Definition) {
				currentContext = tok.getTarget();
				currentBlock = 0;
				var = 0;
				debug.println("\t\tnew Context: " + currentContext);
				contexts.put(currentContext, new HashMap<Integer, Integer>());
				debug.println("\t\t\tadd Block: " + currentBlock + " highest variable: " + var);
				contexts.get(currentContext).put(currentBlock, var);
				
			} else if(tok.getType() == TokenType.Branch || tok.getType() == TokenType.Return) {
				currentBlock = ++var;
				Token token = new Token();
				token.setType(TokenType.Label);
				token.setTarget(String.valueOf(var));
				debug.println("\t\t\tinsert Label \"" + token.getTarget() + "\" in token token stream");
				tokenStream.add(i+1, token);
				debug.println("\t\t\tadd Block: " + currentBlock + " highest variable: " + var);
				contexts.get(currentContext).put(currentBlock, var);
			} else if(tok.getType() == TokenType.Label) {
		
			} else {
				if(!tok.getTarget().isEmpty() && tok.getTarget().length() > 1) {
					try {
						int tmp_var = Integer.valueOf(tok.getTarget().substring(1));
						if(tmp_var > var) {
							var = tmp_var;
							debug.println("\t\t\tchange Block: " + currentBlock + " highest variable: " + var);
							contexts.get(currentContext).put(currentBlock, var);
						}
					} catch(NumberFormatException e) {
					}
				}
			}
		}
		
		
		
		
	}
}
