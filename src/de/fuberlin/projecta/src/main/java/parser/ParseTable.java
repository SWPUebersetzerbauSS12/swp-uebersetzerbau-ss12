package parser;


public class ParseTable {
	
	private static final char DELIM = '#';
	
	String nonTerminals, terminals;
	
	String[][] table;
	
	public ParseTable(String nonTerminals, String terminals){
		this.nonTerminals = nonTerminals;
		this.terminals = terminals;
		
		table = new String[nonTerminals.length()][terminals.length()];
	}
	
	public void setEntry(char nonT, char t, String entry){
		for(int i = 0; i < nonTerminals.length(); i++){
			if(nonTerminals.charAt(i) == nonT){
				for(int j = 0; j < terminals.length(); j++){
					if(terminals.charAt(i) == t){
						table[i][j] += entry + DELIM;
					}
				}
			}
		}
	}
	
	public String getEntry(char nonT, char t){
		for(int i = 0; i < nonTerminals.length(); i++){
			if(nonTerminals.charAt(i) == nonT){
				for(int j = 0; j < terminals.length(); j++){
					if(terminals.charAt(i) == t){
						return table[i][j];
					}
				}
			}
		}
		return null;
	}

}
