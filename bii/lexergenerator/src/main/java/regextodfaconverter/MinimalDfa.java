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

package regextodfaconverter;

import regextodfaconverter.fsm.FiniteStateMachine;
import tokenmatcher.DeterministicFiniteAutomata;
import tokenmatcher.State;


/**
 * Adapter zur Anpassung an das DFA Interface des TokenMatchers. 
 * Garantiert, dass der gekapselte FSA auch sicher ein DFA ist.
 * @author workstation
 *
 * @param <E>
 * @param <Payload>
 */
public class MinimalDfa<E extends Comparable<E>, Payload> implements DeterministicFiniteAutomata<E, Payload> {
	
	private FiniteStateMachine finiteStateMachine;
	
  
	public MinimalDfa( FiniteStateMachine<E, Payload> finiteStateMachine) throws ConvertExecption {
		super();
		this.finiteStateMachine = finiteStateMachine;
		try {
			if ( !finiteStateMachine.isDeterministic())
				finiteStateMachine = NfaToDfaConverter.convertToDfa( finiteStateMachine);
			finiteStateMachine = DfaMinimizer.convertToMimimumDfa( finiteStateMachine);
		} catch( Exception e) {
			throw new ConvertExecption( "Cannot convert given fsa to minimal dfa.");
		}
	}
	

	public State<Payload> changeStateByElement( E element) {
		return finiteStateMachine.changeState( element);
	}

	public boolean canChangeStateByElement( E element) {
		return finiteStateMachine.canChangeState( element);
	}

	public State<Payload> getCurrentState() {
		return finiteStateMachine.getCurrentState();
	}

	public void resetToInitialState() {
		finiteStateMachine.resetToInitialState();
	}

}
