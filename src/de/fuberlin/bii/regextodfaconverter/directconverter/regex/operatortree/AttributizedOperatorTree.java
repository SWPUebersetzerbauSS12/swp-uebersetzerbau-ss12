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


package de.fuberlin.bii.regextodfaconverter.directconverter.regex.operatortree;

import java.util.HashMap;

import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.TreeNode;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.TreeNodeCollection;

/**
 * Schnittstelle für einen attributierten Operatorbaum.
 * 
 * @author Johannes Dahlke
 *
 */
@SuppressWarnings("rawtypes")
public interface AttributizedOperatorTree {
	
	/**
	 * Liefert eine Abbildung auf die FIRST-Positionen.
	 * @return
	 */
	HashMap<TreeNode, TreeNodeCollection> getFirstPositions();
	
	/**
	 * Liefert eine Abbildung auf die FOLLOW-Positionen.
	 * @return
	 */	
	HashMap<TreeNode, TreeNodeCollection> getFollowPositions();
	
	/**
	 * Liefert eine Abbildung auf die LAST-Positionen.
	 * @return
	 */
	HashMap<TreeNode, TreeNodeCollection> getLastPositions();
	
	/**
	 * Liefert eine Abbildung auf die NULLABLES.
	 * @return
	 */
	HashMap<TreeNode, Boolean> getNullables();

}
