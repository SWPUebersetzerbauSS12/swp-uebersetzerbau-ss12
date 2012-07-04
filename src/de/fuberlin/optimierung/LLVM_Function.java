package de.fuberlin.optimierung;

import java.util.*;
import de.fuberlin.optimierung.commands.*;


public class LLVM_Function {

	String func_define = "";
	String afterFunc = "";
	
	private LLVM_Block startBlock;
	private LLVM_Block endBlock;
	private ArrayList<LLVM_Block> blocks;
	private int numberBlocks;
	
	private HashMap<String,Integer> labelToBlock = new HashMap<String,Integer>();
	
	private LLVM_RegisterMap registerMap = new LLVM_RegisterMap();
	
	public LLVM_Function(String code) {
		func_define = "define " + code.substring(0, code.indexOf("\n"));
		code = code.substring(code.indexOf("\n")+1);
		
		afterFunc = code.substring(code.lastIndexOf("}")+1);
		code = code.substring(0, code.lastIndexOf("}"));
		
		String codeBlocks[] = code.split("\n\n"); 
		this.numberBlocks = codeBlocks.length;
		this.blocks = new ArrayList<LLVM_Block>(this.numberBlocks);
		for(int i = 0; i < this.numberBlocks; i++) {
			this.blocks.add(new LLVM_Block(codeBlocks[i],this));
		}
		this.startBlock = this.blocks.get(0);
		this.endBlock = this.blocks.get(this.numberBlocks-1);
	}
	
	/*
	 * *********************************************************
	 * *********** Flussgraph - Erstellung *********************
	 * *********************************************************
	 */

	/**
	 * Erstellt den Flussgraphen zwischen den Bloecken, d.h. die Attribute
	 * nextBlocks und previousBlocks der Bloecke werden gesetzt.
	 * Labels werden gegebenenfalls erstellt.
	 */
	public void createFlowGraph() {
		
		// Teste, ob Labels gesetzt sind
		// Wenn nein, dann erstelle die Labels
		/*if(this.numberBlocks>1) {
			if(this.blocks.get(1).getLabel().equals("")) {
				this.createNewLabels();
			}
		}*/
		this.createNewLabels();
			
		// Erstelle Label zu Block Mapping
		this.mapLabelsToBlocks();
		
		// Setze Zeiger
		for(LLVM_Block block : this.blocks) {	// Durchlaufe Bloecke
			
			LLVM_GenericCommand branchCommand = block.getLastCommand();
			if (branchCommand == null) continue;
			LinkedList<LLVM_Parameter> operands = branchCommand.getOperands();
			
			if(branchCommand.getOperation()==LLVM_Operation.RET) {
				// void-return
				// Aktueller Block ist this.endBlock
			}
			else if(branchCommand.getOperation()==LLVM_Operation.RET_CODE) {
				// Return mit Rueckgabewert
				// Aktueller Block ist this.endBlock
				
			}
			else if(branchCommand.getOperation()==LLVM_Operation.BR) {
				// Unbedingter Sprung
				String label = operands.getFirst().getName();
				Integer blockPosition = this.labelToBlock.get(label);
				if(blockPosition!=null) {
					block.appendToNextBlocks(this.blocks.get(blockPosition));
					this.blocks.get(blockPosition).appendToPreviousBlocks(block);
				}
				
			}
			else if(branchCommand.getOperation()==LLVM_Operation.BR_CON) {
				// Bedingter Sprung
				String label1 = operands.get(1).getName();
				String label2 = operands.get(2).getName();
				Integer blockPosition1 = this.labelToBlock.get(label1);
				Integer blockPosition2 = this.labelToBlock.get(label2);
				if(blockPosition1!=null) {
					block.appendToNextBlocks(this.blocks.get(blockPosition1));
					this.blocks.get(blockPosition1).appendToPreviousBlocks(block);
				}
				if(blockPosition2!=null) {
					block.appendToNextBlocks(this.blocks.get(blockPosition2));
					this.blocks.get(blockPosition2).appendToPreviousBlocks(block);
				}
				
			}
				
		}
		
	}
	
	/**
	 * Setze Labels fuer Bloecke in denen sie noch nicht gesetzt sind
	 * Neue Labels haben die Form wie z.B. %4
	 */
	private void createNewLabels() {
		String nextUnnamed = "%1";
		int nextNumber = 1;
		// Erster Block muss nicht betrachtet werden
		for(int i=0; i<this.numberBlocks; i++) {
			LLVM_Block block = this.blocks.get(i);
			// Setze ab zweitem Block das Label
			// Nur falls kein Label gesetzt ist
			if(i>0 && block.getLabel().equals("")) {
				block.setLabel(nextUnnamed);
				nextNumber++;
				nextUnnamed = "%" + nextNumber;
			}
			
			if(!block.isEmpty()) {
				LLVM_GenericCommand c = block.getFirstCommand();
				while(c!=null) {
					LLVM_Parameter p = c.getTarget();
					if(p!=null) {
						String name = p.getName();
						if(name!=null && name.equals(nextUnnamed)) {
							nextNumber++;
							nextUnnamed = "%" + nextNumber;
						}
					}
					c = c.getSuccessor();
				}
				
			}	// if block not empty
		}	// for
	}
	
	/**
	 * Initialisiert die Hashmap labelToBlock, die Labelnamen dem jeweiligen Block zuordnet
	 * (als Index in this.blocks)
	 * Wird bei Erstellung des Flussgraphen zwischen den Bloecken benoetigt
	 */
	private void mapLabelsToBlocks() {
		
		this.labelToBlock.clear();
		
		// Durchlaufe alle Bloecke
		for(int i=0; i<this.numberBlocks; i++) {
			String label = this.blocks.get(i).getLabel();		
			this.labelToBlock.put(label, i);
		}
		
	}
	
	/*
	 * *********************************************************
	 * *********** Registermaps initialisieren *****************
	 * *********************************************************
	 */
	
	/**
	 * Definition und Verwendungen der Register werden in registerMap abgelegt
	 * Alte Informationen werden entfernt, aktuelle gesetzt
	 */
	public void createRegisterMaps() {
		
		// Loesche alte Werte
		this.registerMap.clean();
		
		// Setze neue Werte
		for(LLVM_Block block : this.blocks) {	// Gehe Bloecke durch
			
			// Ist Block leer?
			if(!block.isEmpty()) {
				
				// Gehe Befehle des Blockes durch
				LLVM_GenericCommand c = block.getFirstCommand();
				while(c!=null) {
					
					// Fuege c in Register Maps ein
					this.registerMap.addCommand(c);
					c = c.getSuccessor();
					
				}
				
			}
	
		}
	}
	
	/*
	 * *********************************************************
	 * *********** Globale Lebendigkeit ************************
	 * *********************************************************
	 */
	
	/**
	 * Erstelle IN und OUT Mengen fuer globale Lebendigkeitsanalyse
	 * Arbeitet nicht auf Registern, sondern auf Speicheradressen
	 * Dannach ist zwischen den Bloecken bekannt, ob eine Speicheradresse lebendig ist
	 * Dient dazu, spaeter ueberfluessige stores und loads entfernen zu koennen
	 * Vorraussetzung: def und use Mengen der Bloecke sind gesetzt
	 */
	private void createInOutLiveVariables() {
		// Algorithmus siehe Seite 610 Drachenbuch
		boolean changes = true;
		while(changes) {
			changes = false;
			for(LLVM_Block b : this.blocks) {
				if(b.updateInOutLiveVariables()) {
					changes = true;
				}
			}
		}
		
	}
	
	/**
	 * Erstelle IN OUT Mengen pro Block fuer Lebendigkeitsanalyse
	 * Entferne anschliessend ueberfluessige Stores
	 */
	public void globalLiveVariableAnalysis() {
		for(LLVM_Block b : this.blocks) {
			b.createDefUseSets();
		}
		this.createInOutLiveVariables();
		for(LLVM_Block b : this.blocks) {
			b.deleteDeadStores();
		}
	}
	
	/*
	 * *********************************************************
	 * *********** Reaching Analysis ***************************
	 * *********************************************************
	 */
	
	/**
	 * Erstelle IN und OUT Mengen fuer Reaching Analyse
	 * Arbeitet nicht auf Registern, sondern auf Speicheradressen
	 * Dient dazu, spaeter store/load-paare zusammenfuegen zu koennen
	 * Vorraussetzung: gen und kill Mengen der Bloecke sind gesetzt
	 */
	private void createInOutReaching() {
		// Algorithmus siehe Seite 607 Drachenbuch
		boolean changes = true;
		while(changes) {
			changes = false;
			for(LLVM_Block b : this.blocks) {
				if(b.updateInOutReaching()) {
					changes = true;
				}
			}
		}
		
	}
	
	/**
	 * Erstelle IN OUT Mengen pro Block fuer Lebendigkeitsanalyse
	 * Entferne anschliessend ueberfluessige Stores
	 */
	public void reachingAnalysis() {
		for(LLVM_Block b : this.blocks) {
			b.createGenKillSets();
		}
		this.createInOutReaching();
		for(LLVM_Block b : this.blocks) {
			b.foldStoreLoad();
		}
	}
	
	/*
	 * *********************************************************
	 * *********** CommonExpressions ***************************
	 * *********************************************************
	 */
	
	/**
	 * Löscht alle doppelten Befehle in einem Block
	 */
	public void removeCommonExpressions (){
		for (LLVM_Block block : blocks){
			block.removeCommonExpressions();
		}
	}
	
	
	/*
	 * *********************************************************
	 * *********** Folding / Propagation ***********************
	 * *********************************************************
	 */
	
	private boolean fold(LLVM_GenericCommand cmd) {
		if(cmd.getClass().equals(LLVM_BinaryCommand.class)){
			LinkedList<LLVM_Parameter> operands = cmd.getOperands();
			LLVM_Parameter op1 = operands.get(0);
			LLVM_Parameter op2 = operands.get(1);
			
			try{
				if(op1.getType() == LLVM_ParameterType.INTEGER && op2.getType() == LLVM_ParameterType.INTEGER){
					int iOP1 = Integer.parseInt(op1.getName());
					int iOP2 = Integer.parseInt(op2.getName());
					int result = 0;
					
					switch(cmd.getOperation()){
					case ADD :
						result = iOP1 + iOP2;
						break;
					case SUB :
						result = iOP1 - iOP2;
						break;
					case MUL :
						result = iOP1 * iOP2;
						break;
					case DIV :
						result = iOP1 / iOP2;
						break;
					case AND :
						result = iOP1 & iOP2;
						break;
					case OR :
						result = iOP1 | iOP2;
						break;
					case XOR :
						result = iOP1 ^ iOP2;
						break;
					}
					
					op1.setName(""+result);
					op2.setName("0");
						
					return true;
				}else if(op1.getType() == LLVM_ParameterType.REGISTER && op2.getType() == LLVM_ParameterType.INTEGER){
				
					int iOP2 = Integer.parseInt(op2.getName());
					if(iOP2 == 0){
						return true;
					}
				}else if(op1.getType() == LLVM_ParameterType.DOUBLE && op2.getType() == LLVM_ParameterType.DOUBLE){
					double iOP1 = Double.parseDouble(op1.getName());
					double iOP2 = Double.parseDouble(op2.getName());
					double result = 0;
					
					switch(cmd.getOperation()){
					case FADD :
						result = iOP1 + iOP2;
						break;
					case FSUB :
						result = iOP1 - iOP2;
						break;
					case FMUL :
						result = iOP1 * iOP2;
						break;
					case FDIV :
						result = iOP1 / iOP2;
						break;
					}
					
					op1.setName(""+result);
					op2.setName("0");
						
					return true;
				}else if(op1.getType() == LLVM_ParameterType.REGISTER && op2.getType() == LLVM_ParameterType.DOUBLE){
				
					double iOP2 = Double.parseDouble(op2.getName());
					if(iOP2 == 0){
						return true;
					}
				}
			}catch(NumberFormatException e){
				// no numbers
			}
		}else if(cmd.getClass().equals(LLVM_IcmpCommand.class)){
		
			LinkedList<LLVM_Parameter> operands = cmd.getOperands();
			LLVM_Parameter op1 = operands.get(0);
			LLVM_Parameter op2 = operands.get(1);
			
			try{
				if(op1.getType() == LLVM_ParameterType.INTEGER && op2.getType() == LLVM_ParameterType.INTEGER){
					int iOP1 = Integer.parseInt(op1.getName());
					int iOP2 = Integer.parseInt(op2.getName());
					boolean result = false;
					
					switch(cmd.getOperation()){
					case ICMP_EQ :
						result = iOP1 == iOP2;
						break;
					case ICMP_NE :
						result = iOP1 != iOP2;
						break;
					case ICMP_UGT :
						result = iOP1 > iOP2;
						break;
					case ICMP_UGE :
						result = iOP1 >= iOP2;
						break;
					case ICMP_ULT :
						result = iOP1 < iOP2;
						break;
					case ICMP_ULE :
						result = iOP1 <= iOP2;
						break;
					case ICMP_SGT :
						result = iOP1 > iOP2;
						break;
					case ICMP_SGE :
						result = iOP1 >= iOP2;
						break;
					case ICMP_SLT :
						result = iOP1 < iOP2;
						break;
					case ICMP_SLE :
						result = iOP1 <= iOP2;
						break;	
					}
					
					op1.setName(result?"1":"0");
					op2.setName("0");
					op1.setTypeString("i1");
					
					return true;
				}else if(op1.getType() == LLVM_ParameterType.DOUBLE && op2.getType() == LLVM_ParameterType.DOUBLE){
					double iOP1 = Double.parseDouble(op1.getName());
					double iOP2 = Double.parseDouble(op2.getName());
					boolean result = false;
					
					switch(cmd.getOperation()){
					case ICMP_EQ :
						result = iOP1 == iOP2;
						break;
					case ICMP_NE :
						result = iOP1 != iOP2;
						break;
					case ICMP_UGT :
						result = iOP1 > iOP2;
						break;
					case ICMP_UGE :
						result = iOP1 >= iOP2;
						break;
					case ICMP_ULT :
						result = iOP1 < iOP2;
						break;
					case ICMP_ULE :
						result = iOP1 <= iOP2;
						break;
					case ICMP_SGT :
						result = iOP1 > iOP2;
						break;
					case ICMP_SGE :
						result = iOP1 >= iOP2;
						break;
					case ICMP_SLT :
						result = iOP1 < iOP2;
						break;
					case ICMP_SLE :
						result = iOP1 <= iOP2;
						break;	
					}
					
					op1.setName(result?"1":"0");
					op2.setName("0");
					op1.setTypeString("i1");
					
					return true;
				}
			}catch(NumberFormatException e){
				// no numbers
			}
		}else if(cmd.getClass().equals(LLVM_BranchCommand.class)){
			LinkedList<LLVM_Parameter> operands = cmd.getOperands();
			LLVM_Parameter op1 = operands.get(0);
			
			if(cmd.getOperation() == LLVM_Operation.BR_CON){
				
				if(op1.getType() == LLVM_ParameterType.INTEGER){
					int iOP = Integer.parseInt(op1.getName());
					
					registerMap.deleteCommand(cmd);
					operands.remove(0);
					
					LLVM_Parameter p;
					if(iOP == 0){
						p = operands.remove(0);
					}else{
						p = operands.remove(1);
					}
					cmd.setOperation(LLVM_Operation.BR);
					registerMap.addCommand(cmd);
					
					LLVM_Block block = cmd.getBlock();
					
					for(LLVM_Block b : block.getNextBlocks()){
						if(p.getName().compareTo(b.getLabel()) == 0){
							b.removeFromPreviousBlocks(block);
							block.removeFromNextBlocks(b);
							
							break;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	
	public void constantFolding() {
		
		LinkedList<LLVM_GenericCommand> changed_cmds = new LinkedList<LLVM_GenericCommand>();
		
		for(LLVM_Block block : blocks){
			LLVM_GenericCommand cmd = block.getFirstCommand();
			
			if (cmd == null) continue;
			while(!cmd.isLastCommand()){
				
				if(fold(cmd)){
					changed_cmds.add(cmd);
				}
				
				// next cmd
				cmd = cmd.getSuccessor();
			}
		}
		
		if(changed_cmds.size() > 0){
			constantPropagation(changed_cmds);
		}
	}
	
	public void constantFolding(LinkedList<LLVM_GenericCommand> cmds) {
		
		LinkedList<LLVM_GenericCommand> changed_cmds = new LinkedList<LLVM_GenericCommand>();
		
		for(LLVM_GenericCommand cmd : cmds){
				
			if(fold(cmd)){
				changed_cmds.add(cmd);
			}
		}
		
		if(changed_cmds.size() > 0){
			constantPropagation(changed_cmds);
		}
	}
	
	public void constantPropagation(LinkedList<LLVM_GenericCommand> cmds) {
		
		LinkedList<LLVM_GenericCommand> changed_cmds = new LinkedList<LLVM_GenericCommand>();
		
		for(LLVM_GenericCommand cmd : cmds) {
			
			LinkedList<LLVM_GenericCommand> uses = registerMap.getUses(cmd.getTarget().getName());
			
			if(uses != null){
				LinkedList<LLVM_GenericCommand> _cmds = (LinkedList<LLVM_GenericCommand>) uses.clone();
				
				for(int j = 0; j < _cmds.size(); j++){
					
					LinkedList<LLVM_Parameter> operands = _cmds.get(j).getOperands();
					for(int k = 0;  k < operands.size(); k++){
						if(cmd.getTarget().getName().equals(operands.get(k).getName())){
							if(!changed_cmds.contains(_cmds.get(j)))
								changed_cmds.add(_cmds.get(j));
							LLVM_Parameter op = cmd.getOperands().get(0);
							registerMap.deleteCommand(_cmds.get(j));
							//registerMap.deleteCommand(_cmds.get(j), cmd.getTarget());
							operands.set(k, new LLVM_Parameter(op.getName(), op.getType(), op.getTypeString()));
							registerMap.addCommand(_cmds.get(j));
						}
					}
				}
			}
			
			cmd.deleteCommand("constantPropagation");
			registerMap.deleteCommand(cmd);
		}
		
		if(changed_cmds.size() > 0){
			constantFolding(changed_cmds);
		}
	}
	
	/*
	 * *********************************************************
	 * *********** Tote Register entfernen *********************
	 * *********************************************************
	 */
	
	/**
	 * Teste, ob gegebenes Register nicht verwendet wird
	 * Wenn es tot ist, so wird die Definition geloescht
	 * @param registerName Name des Registers
	 * @return geloeschte Definition (um spaeter Operanden zu testen) oder null, falls nichts geloescht wurde
	 */
	private LLVM_GenericCommand eliminateDeadRegister(String registerName) {
		// Teste, ob Verwendungen existieren
		if(!this.registerMap.existsUses(registerName) &&
				this.registerMap.existsDefintion(registerName)) {
			
			// keine Verwendungen, aber eine Definition
			LLVM_GenericCommand c = this.registerMap.getDefinition(registerName);
			// Nur entfernen, wenn c kein call-Befehl ist
			if(c.getOperation()!=LLVM_Operation.CALL) {
				this.registerMap.deleteCommand(c);
				c.deleteCommand("eliminateDeadRegister - normal");
				return c;
			}
			return null;

		}
		// Wenn einzige Verwendung ein Store, dann löschen
		if (this.registerMap.existsUses(registerName) &&
				this.registerMap.existsDefintion(registerName)) {
			
			LinkedList<LLVM_GenericCommand> uses = this.registerMap.getUses(registerName);
			LLVM_GenericCommand def = this.registerMap.getDefinition(registerName);
			
			if(uses.size() == 1 && uses.get(0).getOperation() == LLVM_Operation.STORE 
					&& def.getOperation() == LLVM_Operation.ALLOCA){	
				
				LLVM_GenericCommand store = uses.get(0);
				this.registerMap.deleteCommand(store);
				store.deleteCommand("eliminateDeadRegister - store");
				return store;
			}
		}
		return null;
	}
	
	/**
	 * Register in Operanden der uebergebenen Befehle werden auf spaetere Verwendung getestet
	 * Diese Befehle wurden bereits geloescht
	 * Werden sie nich verwendet, so wird die Definition des Registers geloescht
	 * @param list zu testende Befehle
	 * @return geloeschte Definitionen (um Operanden nochmals testen zu koennen)
	 */
	public LinkedList<LLVM_GenericCommand> eliminateDeadRegistersFromList(LinkedList<LLVM_GenericCommand> list) {
		
		LinkedList<LLVM_GenericCommand> deletedCommands = new LinkedList<LLVM_GenericCommand>();
		
		// Teste, ob Operanden aus list geloescht werden koennen
		for(LLVM_GenericCommand c : list) {
			LinkedList<LLVM_Parameter> operands = c.getOperands();
			if(operands!=null) {
				for(LLVM_Parameter op : operands) {
					if(op.getType()==LLVM_ParameterType.REGISTER) {
						LLVM_GenericCommand del = this.eliminateDeadRegister(op.getName());
						if(del!=null)
							deletedCommands.addFirst(del);
					}
				}
			}
		}
		
		return deletedCommands;
	}
	
	/**
	 * Tote Register werden aus Programm entfernt, d.h. ihre Definitionen werden geloescht
	 * Funktion wird einmal komplett durchgegangen, Abhaengigkeiten werden nicht aufgeloest
	 * Geloeschte Befehle werden zurueckgegeben, um spaeter die Operanden testen zu koennen
	 * Die Register-Maps (Definition und Verwendung) muessen aktuell sein
	 * @return Geloeschte Befehle
	 */
	private LinkedList<LLVM_GenericCommand> eliminateDeadRegistersGlobal() {
		
		LinkedList<LLVM_GenericCommand> deletedCommands = new LinkedList<LLVM_GenericCommand>();
		
		String registerNames[] = this.registerMap.getDefinedRegisterNames().
				toArray(new String[0]);
		
		// Iteriere ueber alle definierten Register
		for(String registerName : registerNames) {
			
			// Teste fuer jedes Register r, ob Verwendungen existieren
			// c ist geloeschter Befehl (Definition) oder null, falls
			// es nich geloescht werden kann
			LLVM_GenericCommand c = this.eliminateDeadRegister(registerName);
			if(c!=null)
				deletedCommands.addFirst(c);
			
		}
		
		return deletedCommands;
		
	}
	
	/**
	 * Tote Register werden aus Programm entfernt, d.h. ihre Definitionen werden geloescht
	 * Abhaengigkeiten von toten Registern werden abgearbeitet
	 * eliminateDeadRegistersGlobal und eliminateDeadRegistersFromList werden als
	 * Unterfunktionen verwendet
	 */
	public void eliminateDeadRegisters() {
		
		LinkedList<LLVM_GenericCommand> deletedCommands;
		deletedCommands = this.eliminateDeadRegistersGlobal();
	
		// Bisher geloeschte Befehle koennen Operanden enthalten, die nun keine Verwendung mehr haben
		// Dann koennen diese ebenfalls geloescht werden
		// Teste also geloeschte Befehle durch, bis nichts mehr geloescht werden kann
		while(!deletedCommands.isEmpty()) {
		
			deletedCommands = this.eliminateDeadRegistersFromList(deletedCommands);
			
		}
		
	}
	
	
	/*
	 * *********************************************************
	 * *********** Tote Bloecke entfernen **********************
	 * *********************************************************
	 */
	
	/**
	 * Tote Bloecke, d.h. Bloecke, die im Flussgraph nicht erreichbar sind,
	 * werden entfernt.
	 * Definitionen von Registern, deren einzige Verwendung innerhalb eines geloeschten
	 * Blocks waren, werden geloescht. Dead register elimination ist anschliessend
	 * nicht extra noetig.
	 * @return Bloecke, die geloescht wurden, um anschliessend die Kinder zu testen
	 */
	private LinkedList<LLVM_Block> eliminateDeadBlocksGlobal() {
		
		LinkedList<LLVM_GenericCommand> deletedCommands = new LinkedList<LLVM_GenericCommand>();
		LinkedList<LLVM_Block> deletedBlocks = new LinkedList<LLVM_Block>();
		
		for(int i=1; i<this.numberBlocks; i++) {
			LLVM_Block block = this.blocks.get(i);
			if(!block.hasPreviousBlocks()) {
				// Block hat keine Vorgaenger, kann entfernt werden
				block.deleteBlock();	// aus Flussgraphen entfernen
				deletedBlocks.add(block);
				
				// Befehle des Blocks zu deletedCommands hinzufuegen
				if(!block.isEmpty()) {
					
					// Gehe Befehle des Blockes durch
					LLVM_GenericCommand c = block.getFirstCommand();
					while(c!=null) {
						
						// Fuege c in deletedCommands ein
						deletedCommands.add(c);
						c = c.getSuccessor();
						
					}
					
				}
			}
		}
		
		// Entferne geloeschte Bloecke aus this.blocks
		for(LLVM_Block block : deletedBlocks) {
			this.blocks.remove(block);
			this.numberBlocks--;
		}
		
		// Entferne geloeschte Befehle aus Register-Hashmaps
		// und entferne Definitionen von Registern mit einziger Verwendung in
		// einem geloeschten Block
		for(LLVM_GenericCommand c : deletedCommands) {
			this.registerMap.deleteCommand(c);
		}
		while(!deletedCommands.isEmpty()) {
			
			deletedCommands = this.eliminateDeadRegistersFromList(deletedCommands);
			
		}
		
		return deletedBlocks;
		
	}
	
	/**
	 * Tote Bloecke, d.h. Bloecke, die im Flussgraph nicht erreichbar sind,
	 * werden entfernt.
	 * Es werden nicht alle Bloecke getestet, sondern nur Kindbloecke der uebergebenen Liste.
	 * Definitionen von Registern, deren einzige Verwendung innerhalb eines geloeschten
	 * Blocks waren, werden geloescht. Dead register elimination ist anschliessend
	 * nicht extra noetig.
	 * @return Bloecke, die geloescht wurden, um anschliessend die Kinder zu testen
	 */
	private LinkedList<LLVM_Block> eliminateDeadBlocksFromList(
			LinkedList<LLVM_Block> blocks) {
		
		LinkedList<LLVM_GenericCommand> deletedCommands = new LinkedList<LLVM_GenericCommand>();
		LinkedList<LLVM_Block> deletedBlocks = new LinkedList<LLVM_Block>();
		
		for(LLVM_Block father : blocks) {
			for(LLVM_Block block : father.getNextBlocks()) {
				
				// block wird getestet
				if(!block.hasPreviousBlocks() && this.blocks.contains(block)) {
					// Block hat keine Vorgaenger, kann entfernt werden
					block.deleteBlock();	// aus Flussgraphen entfernen
					deletedBlocks.add(block);
					
					// Befehle des Blocks zu deletedCommands hinzufuegen
					if(!block.isEmpty()) {
						
						// Gehe Befehle des Blockes durch
						LLVM_GenericCommand c = block.getFirstCommand();
						while(c!=null) {
							
							// Fuege c in deletedCommands ein
							deletedCommands.add(c);
							c = c.getSuccessor();
							
						}
						
					}
				}
				
			}
		}
		
		// Entferne geloeschte Bloecke aus this.blocks
		for(LLVM_Block block : deletedBlocks) {
			this.blocks.remove(block);
			this.numberBlocks--;
		}
		
		// Entferne geloeschte Befehle aus Register-Hashmaps
		// und entferne Definitionen von Registern mit einziger Verwendung in
		// einem geloeschten Block
		for(LLVM_GenericCommand c : deletedCommands) {
			this.registerMap.deleteCommand(c);
		}
		while(!deletedCommands.isEmpty()) {
			
			deletedCommands = this.eliminateDeadRegistersFromList(deletedCommands);
			
		}
		
		return deletedBlocks;
		
	}
		
	/**
	 * Tote Bloecke werden aus dem Programm entfernt
	 * Operanden der geloeschten Befehle werden auf weitere Verwendungen getestet
	 * und gegebenenfalls ebenfalls geloescht
	 * Abhaengigkeiten zwischen Bloecken werden aufgeloest
	 */
	public void eliminateDeadBlocks() {
		LinkedList<LLVM_Block> deletedBlocks;
		deletedBlocks = this.eliminateDeadBlocksGlobal();
	
		// Loesche gegebenenfalls Nachfolgebloecke eines schon geloeschten Blocks
		while(!deletedBlocks.isEmpty()) {
		
			deletedBlocks = this.eliminateDeadBlocksFromList(deletedBlocks);
			
		}
	}
	
	/**
	 * Entferne Bloecke, die nur unbedingten Sprungbefehl enthalten
	 */
	public void deleteEmptyBlocks() {
		// Gehe Bloecke durch
		LinkedList<LLVM_Block> toDelete = new LinkedList<LLVM_Block>();
		for(LLVM_Block actualBlock : this.blocks) {
			// Enthaelt Block nur einen unbedingten Sprungbefehl?
			if(!actualBlock.isEmpty() && actualBlock.getFirstCommand().
					getOperation()==LLVM_Operation.BR) {
				// Block kann geloescht werden
				LLVM_Block targetBlock = actualBlock.getNextBlocks().getFirst();
				String targetBlockLabel = targetBlock.getLabel();
				String actualBlockLabel = actualBlock.getLabel();
				
				// Gehe Vorgaengerbloecke durch
				for(LLVM_Block previousBlock : actualBlock.getPreviousBlocks()) {
					// Hole Sprungbefehl des Vorgaengerblocks
					// Dieser muss angepasst werden
					LLVM_GenericCommand branchCommand = previousBlock.getLastCommand();
					
					// Befehl aus Registermap austragen
					this.registerMap.deleteCommand(branchCommand);
					
					// Sprung soll zu targetBlock gehen, statt zu actualBlock
					LLVM_Parameter p = branchCommand.getOperands().getFirst();
					p.setName(targetBlockLabel);
					
					// Setze Registermapeintrag neu
					this.registerMap.addCommand(branchCommand);
					
					// Passe Flussgraph an:
					// previousBlock hat actualBlock nicht mehr als Nachfolger,
					// sondern targetBlock
					// targetBlock hat previousBlock als Vorgaenger
					targetBlock.appendToPreviousBlocks(previousBlock);
					previousBlock.appendToNextBlocks(targetBlock);
					previousBlock.removeFromNextBlocks(actualBlock);
				}
				toDelete.add(actualBlock);
			}else if (!actualBlock.isEmpty() && actualBlock.getPreviousBlocks().size() > 0 && (actualBlock.getFirstCommand().
					getOperation()==LLVM_Operation.RET || actualBlock.getFirstCommand().
					getOperation()==LLVM_Operation.RET_CODE)){
				
				// Gehe Vorgaengerbloecke durch
				for(LLVM_Block previousBlock : actualBlock.getPreviousBlocks()) {
					// Hole Sprungbefehl des Vorgaengerblocks
					// Dieser muss ersetzt werden
					LLVM_GenericCommand branchCommand = previousBlock.getLastCommand();
					// Befehl aus Registermap austragen
					this.registerMap.deleteCommand(branchCommand);
					
					// Clone für Registermap erstellen
					LLVM_GenericCommand tmp = new LLVM_ReturnCommand(actualBlock.getFirstCommand().toString(), branchCommand.getPredecessor(), previousBlock);
					branchCommand.replaceCommand(tmp);
					
					// Setze Registermapeintrag neu
					this.registerMap.addCommand(tmp);
					
					// Passe Flussgraph an:
					// previousBlock hat actualBlock nicht mehr als Nachfolger,
					previousBlock.removeFromNextBlocks(actualBlock);
				}
				toDelete.add(actualBlock);
			}
		}

		// Alle gelöschten Blöcke entfernen
		for (LLVM_Block del : toDelete){
			// Entferne zu loeschenden Block aus Flussgraph
			del.deleteBlock();
			
			// Entferne Befehle aus entferntem Block aus Registermap
			this.registerMap.deleteCommand(del.getFirstCommand());
			
			// Entferne zu loeschenden Block aus this.blocks
			this.blocks.remove(del);
			this.numberBlocks--;
		}
	}
	
	/*
	 * *********************************************************
	 * *********** Labels zur Ausgabe anpassen *****************
	 * *********************************************************
	 */
	
	/**
	 * Aendere den Namen aller Operanden mit Namen oldName in c zu newName.
	 * @param c Befehl, dessen Operanden durchsucht werden
	 * @param oldName zu aendernder Name
	 * @param newName neu zu setzender Name
	 */
	private void changeOperandName(LLVM_GenericCommand c, String oldName, String newName) {
		LinkedList<LLVM_Parameter> operands = c.getOperands();
		for(LLVM_Parameter o : operands) {
			if(o.getName().equals(oldName)) {
				o.setName(newName);
			}
		}
	}
	
	/**
	 * Durch das Loeschen von Befehlen kann nach %2=... ein %4=... folgen.
	 * Dies ist nicht erlaubt, diese Funktion passt die Namen an.
	 * Achtung: nur direkt vor der Ausgabe des Codes nutzen, Hashmaps
	 * werden nicht aktualisiert.
	 */
	public void updateUnnamedLabelNames() {
	
		String nextUnnamed = "%1";
		int nextNumber = 1;
		
		// Erster Block muss nicht betrachtet werden
		for(int i=0; i<this.numberBlocks; i++) {
			
			LLVM_Block block = this.blocks.get(i);
			
			// Teste ab zweitem Block das Label
			if(i>0) {
				String label = block.getLabel();
				// Ist es ein unbenanntes Label?
				if(label!=null && label.matches("%[1-9][0-9]*")) {
					// Ist es nicht der folgende Bezeichner?
					if(!label.equals(nextUnnamed)) {
						// Setze Label auf nextUnnamed
						block.setLabel(nextUnnamed);
						// Setze alle Verwendungen auf nextUnnamed
						LinkedList<LLVM_GenericCommand> uses = this.registerMap.getUses(label);
						if(uses != null){
							for(LLVM_GenericCommand u : uses) {
								this.changeOperandName(u, label, nextUnnamed);
							}
						}
					}
					nextNumber++;
					nextUnnamed = "%" + nextNumber;
				}	
				
			}
			
			// Gehe Befehle des Blocks durch
			if(!block.isEmpty()) {
				LLVM_GenericCommand c = block.getFirstCommand();
				while(c!=null) {
					LLVM_Parameter p = c.getTarget();
					if(p!=null) {
						String name = p.getName();
						// Ist name unbenannenter Bezeichner?
						if(name!=null && name.matches("%[1-9][0-9]*")) {
							
							// Ist es nicht der folgende Bezeichner?
							if(!name.equals(nextUnnamed)) {
								// Ersetze in Definition durch nextUnnamed
								p.setName(nextUnnamed);
								// Ersetze in allen Verwendungen durch nextUnnamed
								LinkedList<LLVM_GenericCommand> uses = this.registerMap.getUses(name);
								if(uses != null) {
									for(LLVM_GenericCommand u : uses) {
										this.changeOperandName(u, name, nextUnnamed);
									}
								}
							}
							
							nextNumber++;
							nextUnnamed = "%" + nextNumber;
							
						}
					}
					c = c.getSuccessor();
				}
				
			}	// if block not empty
		}	// for
		
	}
	
	/*
	 * *********************************************************
	 * *********** Setter / Getter / toString ******************
	 * *********************************************************
	 */
	
	public LLVM_RegisterMap getRegisterMap() {
		return registerMap;
	}
	
	public ArrayList<LLVM_Block> getBlocks() {
		return blocks;
	}
	
	public String toString() {
		String output = func_define + "\n";
		int i = 0;
		for (i = 0; i < this.numberBlocks-1; i++) {
			output += blocks.get(i).toString()+"\n";
		}
		output += blocks.get(i).toString();
		output += "}\n";
		output += this.afterFunc;
		return output;
	}
	
	public String toGraph() {
		
		// Workaround
		blocks.get(0).setLabel("%0");
		
		String graph = "digraph g { graph [fontsize=30 labelloc=\"t\" label=\"\" splines=true overlap=false rankdir = \"TD\"]; ratio = auto;";
		
		for (int i = 0; i < this.numberBlocks; i++) {
			graph += blocks.get(i).toGraph();
		}
		
		LinkedList<LLVM_Block> nxt_Blocks;
		
		for (int i = 0; i < this.numberBlocks; i++) {
			nxt_Blocks = blocks.get(i).getNextBlocks();
			
			for(LLVM_Block b : nxt_Blocks){
				graph += "\""+blocks.get(i).getLabel()+"\" -> \""+b.getLabel()+"\" [ penwidth = 1 fontsize = 14 fontcolor = \"grey28\" label = \"\" ];";
			}
		}
		
		return graph + "}";
	}
}
