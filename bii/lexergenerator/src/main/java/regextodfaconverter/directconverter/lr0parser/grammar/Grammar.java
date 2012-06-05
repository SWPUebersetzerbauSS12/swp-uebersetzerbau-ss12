package regextodfaconverter.directconverter.lr0parser.grammar;

import java.util.HashMap;
import java.util.Set;


public interface Grammar {
	
	public Nonterminal getStartSymbol();
	
	public void setStartSymbol( Nonterminal startSymbol);

	boolean addProduction(ProductionRule productionRule);
	
	Set<Terminal> getTerminals();

	Set<Nonterminal> getNonterminals();
	
	HashMap<Nonterminal, Set<Terminal>> getFirstSets();
	
	HashMap<Nonterminal, Set<Terminal>> getFollowSets();
	
}
