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
 * Authors: Johannes Dahlke
 * 
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */


package de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar;

import java.util.HashMap;
import java.util.Set;


/**
 * Schnittstelle für eine Grammatik. 
 * 
 * @author Johannes Dahlke
 *
 */
public interface Grammar {
	
	/**
	 * Liefert das Startsymbol.
	 *  
	 */
	public Nonterminal getStartSymbol();
	
	/**
	 * Legt das Startsymbol fest.
	 * 
	 * @param startSymbol
	 */
	public void setStartSymbol( Nonterminal startSymbol);

	/**
	 * Fügt eine Produktionsregel zur Grammtik hinzu.
	 * 
	 * @param productionRule
	 * @return
	 */
	boolean addProduction(ProductionRule productionRule);
	
	/**
	 * Gibt die Menge der Terminale zurück.
	 * @return
	 */
	TerminalSet getTerminals();

	/**
	 * Gibt die Menge der Nichterminale zurück.
	 * @return
	 */
	Set<Nonterminal> getNonterminals();
	
	/**
	 * Gibt eine Abbildung der Nichtterminale auf die dazugehörigen FIRST-Mengen zurück.
	 * @return
	 */
	HashMap<Nonterminal, TerminalSet> getFirstSets();

	/**
	 * Gibt eine Abbildung der Nichtterminale auf die dazugehörigen FOLLOW-Mengen zurück.
	 * @return
	 */
	HashMap<Nonterminal, TerminalSet> getFollowSets();
	
}
