package de.fuberlin.optimierung;

import java.util.*;

import de.fuberlin.optimierung.commands.*;

public class LLVM_Block{
	
	// Funktion, zu der der Block gehoert
	private LLVM_Function function = null;
	
	// Erster und letzter Befehl des Blockes
	private LLVM_GenericCommand firstCommand = null;
	private LLVM_GenericCommand lastCommand = null;

	// Ursprüngliches Label des Blockes
	private String label = "";
	
	// Vorgaenger- und Nachfolgerbloecke
	// Hieraus entsteht der Flussgraph zwischen den Bloecken
	private LinkedList<LLVM_Block> nextBlocks = new LinkedList<LLVM_Block>();
	private LinkedList<LLVM_Block> previousBlocks = new LinkedList<LLVM_Block>();
	
	// def- und usemenge an Speicheradressen fuer globale Lebendigkeitsanalyse
	private LinkedList<String> def = new LinkedList<String>();
	private LinkedList<String> use = new LinkedList<String>();
	// IN und OUT Mengen fuer globale Lebendigkeitsanalyse auf Speicherzellen
	private LinkedList<String> inLive = new LinkedList<String>();
	private LinkedList<String> outLive = new LinkedList<String>();
	
	// gen- und killmengen fuer globale Reaching Analyse
	private LinkedList<LLVM_GenericCommand> gen = new LinkedList<LLVM_GenericCommand>();
	private LinkedList<LLVM_GenericCommand> kill = new LinkedList<LLVM_GenericCommand>();
	// IN und OUT Mengen fuer globale Reachinganalyse
	private LinkedList<LLVM_GenericCommand> inReaching = new LinkedList<LLVM_GenericCommand>();
	private LinkedList<LLVM_GenericCommand> outReaching = new LinkedList<LLVM_GenericCommand>();
	
	// Kompletter Code des Blocks als String
	private String blockCode;

	public LLVM_Block(String blockCode, LLVM_Function function) {
		
		this.function = function;
		this.blockCode = blockCode;		
		this.createCommands();
		this.optimizeBlock();
	}

	public void optimizeBlock() {
	}
	
	/**
	 * Löscht alle doppelten Befehle in einem Block
	 * Bei Änderungen wird ConstantPropagation aufgerufen
	 * Doppelte Befehle werden nur überprüft, falls in Whitelist
	 */
	public void removeCommonExpressions() {
		List<String> whitelist = new ArrayList<String>();
		LinkedList<LLVM_GenericCommand> changed = new LinkedList<LLVM_GenericCommand>();
		HashMap<String, LinkedList<LLVM_GenericCommand>> commonex = new HashMap<String, LinkedList<LLVM_GenericCommand>>();
		whitelist.add(LLVM_Operation.ADD.toString());
		whitelist.add(LLVM_Operation.MUL.toString());
		whitelist.add(LLVM_Operation.DIV.toString());
		whitelist.add(LLVM_Operation.SUB.toString());
		whitelist.add(LLVM_Operation.LOAD.toString());
		whitelist.add(LLVM_Operation.GETELEMENTPTR.toString());
		
		for (LLVM_GenericCommand i = this.firstCommand; i != null; i=i.getSuccessor()){
			// Nur Kommandos aus der Whitelist optimieren
			if (!whitelist.contains(i.getOperation().name())) continue;
			
			if (commonex.containsKey(i.getOperation().name())){
				// Kommando-Hash existiert
				boolean matched = false;
				LinkedList<LLVM_GenericCommand> commands = commonex.get(i.getOperation().name());
				for (LLVM_GenericCommand command : commands){
					if (matchCommands(i, command)){
						// gleiches Kommando gefunden
						// ersetze aktuelles Kommando mit Bestehendem
						matched = true;
						if (LLVM_Optimization.DEBUG) System.out.println("same command at " + command.getTarget().getName() + ", command replaced : " + i.toString());
						this.function.getRegisterMap().deleteCommand(i);
						// neues Kommando generieren mit Parametern
						LLVM_GenericCommand replacement = new LLVM_BinaryCommand();
						replacement.setOperation(LLVM_Operation.ADD);
						replacement.setTarget(new LLVM_Parameter(i.getTarget().getName(), i.getTarget().getTypeString()));
						LinkedList<LLVM_Parameter> params = new LinkedList<LLVM_Parameter>();
						params.add(new LLVM_Parameter(command.getTarget().getName(), command.getTarget().getTypeString()));
						params.add(new LLVM_Parameter("0", command.getTarget().getTypeString()));
						replacement.setOperands(params);
						// Kommando ersetzen
						i.replaceCommand(replacement);
						this.function.getRegisterMap().addCommand(replacement);
						changed.add(replacement);
						//changed.add(i);
					}
				}
				if (!matched){
					// Kein übereinstimmendes Kommando gefunden
					// füge aktuelles Kommando zum Kommando-Hash hinzu
					commands.add(i);
					commonex.put(i.getOperation().name(), commands);
				}
			}
			else{
				// Kommando-Hash existiert nicht
				LinkedList<LLVM_GenericCommand> tmp = new LinkedList<LLVM_GenericCommand>();
				tmp.add(i);
				commonex.put(i.getOperation().name(), tmp);
			}
		}
		if (changed.size() > 0){
			this.function.constantPropagation(changed);
			removeCommonExpressions();
		}		
	}
	
	private boolean matchCommands(LLVM_GenericCommand com1, LLVM_GenericCommand com2){
		int i = 0;
		// Gleichviele Parameter?
		if (com1.getOperands().size() != com2.getOperands().size()) return false;
		// Gleiche Operation?
		if (com1.getOperation() != com2.getOperation()) return false;
		// Gleiche Parameter?
		for (LLVM_Parameter para1 : com1.getOperands()){
			LLVM_Parameter para2 = com2.getOperands().get(i);
			if (para1.getType() == para2.getType() && para1.getName().equals(para2.getName())){
				// Nothing
			}else{
				// Parameter matchen nicht
				return false;
			}
			i++;
		}
		return true;
	}
	
	/*
	 * *********************************************************
	 * *********** Live Variable Analysis **********************
	 * *********************************************************
	 */
	
	/**
	 * Entferne ueberfluessige Stores
	 * Vorraussetzung: IN und OUT mengen der globalen lebendigkeitsanalyse sind gesetzt
	 */
	public void deleteDeadStores() {
		LinkedList<String> active = (LinkedList<String>) this.outLive.clone();
		LinkedList<LLVM_GenericCommand> deletedCommands = new LinkedList<LLVM_GenericCommand>();
		
		// Gehe Befehle von hinten durch
		LLVM_GenericCommand c = this.lastCommand;
		for(;c!=null; c = c.getPredecessor()) {
			if(c.getOperation()==LLVM_Operation.STORE) {
				String registerName = c.getOperands().get(1).getName();
				if(!active.contains(registerName)) {
					
					LLVM_GenericCommand def = this.function.getRegisterMap().
							getDefinition(registerName);
					
					if(def!=null && def.getOperation()!=LLVM_Operation.GETELEMENTPTR 
							&& !(def.getOperation()==LLVM_Operation.ALLOCA
							&& def.getTarget().getTypeString().startsWith("["))) {
						
						// c kann geloescht werden
						this.function.getRegisterMap().deleteCommand(c);
						c.deleteCommand("deleteDeadStores");
						deletedCommands.add(c);
					}
			
				}
				else {
					// jetzt ist es nicht mehr aktiv
					active.remove(registerName);
				}
			}
			if(c.getOperation()==LLVM_Operation.LOAD) {
				active.add(c.getOperands().getFirst().getName());
			}
		}
		
		// Teste, ob geloeschter Befehl Operanden hatte, der nun keine Verwendung mehr hat
		// Dann kann die Definition entfernt werden
		while(!deletedCommands.isEmpty()) {
			
			deletedCommands = this.function.eliminateDeadRegistersFromList(deletedCommands);
			
		}
	}
	
	/**
	 * Erstelle def und use Mengen dieses Blockes fuer globale Lebendigkeitsanalyse
	 * def : store i32 1, i32* %a -> %a wird hinzugefuegt, falls es keine vorherige
	 * Verwendung von a in diesem Block gibt
	 * use : %5 = load i32* %a -> %a wird hinzugefuegt, falls es keine vorherige
	 * Definition von a in diesem Block gibt
	 */
	public void createDefUseSets() {
		if(!this.isEmpty()) {
			LLVM_GenericCommand c = this.firstCommand;
			while(c!=null) {
				if(LLVM_Operation.STORE==c.getOperation()) {
					// Register mit Speicheradresse steht in zweitem Operanden
					LLVM_Parameter p = c.getOperands().get(1);
					String registerName = p.getName();
					
					// registerName muss in this.def, falls es keine vorherige Verwendung
					// gab, also falls registerName nicht in this.use enthalten ist
					if(!this.use.contains(registerName)) {
						this.def.add(registerName);
					}
				}
				else if(LLVM_Operation.LOAD==c.getOperation()) {
					// Register mit Speicheradresse steht in erstem Operanden
					LLVM_Parameter p = c.getOperands().getFirst();
					String registerName = p.getName();
					
					// registerName muss in this.use, falls es keine vorherige Definition
					// gab, also falls registerName nicht in this.def enthalten ist
					if(!this.def.contains(registerName)) {
						this.use.add(registerName);
					}
				}
				c = c.getSuccessor();
			}
		}
		
	}
	
	/**
	 * Aktualisiere IN und OUT Mengen fuer globale Lebendigkeitsanalyse
	 * Voraussetzung: def und use sind gesetzt
	 * @return true, falls IN veraendert wurde
	 */
	public boolean updateInOutLiveVariables() {
		
		// this.out = in-Mengen aller Nachfolger zusammenfuegen
		this.outLive.clear();
		for(LLVM_Block b : this.nextBlocks) {
			LinkedList<String> inNextBlock = b.getInLive();
			for(String s : inNextBlock) {
				if(!this.outLive.contains(s)) {
					this.outLive.add(s);
				}
			}
		}
		
		// this.in = this.use + (this.out - this.def)
		//this.inLive.clear();
		LinkedList<String> inLiveOld = this.inLive;
		this.inLive = (LinkedList<String>) this.outLive.clone();	// gibt doch neues obj zurueck?
		for(String s : this.def) {
			this.inLive.remove(s);
		}
		for(String s : this.use) {
			if(!this.inLive.contains(s)) {
				this.inLive.add(s);
			}
		}
		
		return !(this.compareLists(inLiveOld, this.inLive));
				
	}
	
	
	/*
	 * *********************************************************
	 * *********** Reaching Analysis ***************************
	 * *********************************************************
	 */
	
	/**
	 * Load-Befehle, die nur von einem Store erreicht werden koennen,
	 * werden zu Registerzuweisung.
	 * Diese wird hier weiterpropagiert.
	 * Koennen tote Stores entstehen.
	 */
	public void foldStoreLoad() {
		
		HashMap<String,LinkedList<LLVM_GenericCommand>> reaching = 
				new HashMap<String,LinkedList<LLVM_GenericCommand>>();
		//LinkedList<LLVM_GenericCommand> reaching = (LinkedList<LLVM_GenericCommand>) this.inReaching.clone();
		LinkedList<LLVM_GenericCommand> changed = new LinkedList<LLVM_GenericCommand>();
		
		for(LLVM_GenericCommand c : this.inReaching) {
			
			String registerName = c.getOperands().get(1).getName();
			LinkedList<LLVM_GenericCommand> stores = reaching.get(registerName);
			
			if(stores==null) {
				stores = new LinkedList<LLVM_GenericCommand>();
			}
			// Fuege ein, falls der Befehl noch nicht enthalten ist
			if(!stores.contains(c)) {
				stores.add(c);
			}
			
			reaching.put(registerName, stores);
			
		}
		
		// Gehe Befehle von vorne durch
		LLVM_GenericCommand c = this.firstCommand;
		for(;c!=null; c = c.getSuccessor()) {
			
			// falls store, fuege zu liste hinzu
			if(c.getOperation()==LLVM_Operation.STORE) {
				
				String registerName = c.getOperands().get(1).getName();
				LinkedList<LLVM_GenericCommand> stores = reaching.get(registerName);
				
				if(stores==null) {
					stores = new LinkedList<LLVM_GenericCommand>();
				}
				// Fuege ein, falls der Befehl noch nicht enthalten ist
				if(!stores.contains(c)) {
					stores.add(c);
				}
				
				reaching.put(registerName, stores);
			}
			
			// falls load, teste ob es nur von einer definition erreicht werden kann
			// dann ersetze load befehl
			// store koennte danach tot sein
			if(c.getOperation()==LLVM_Operation.LOAD) {
				String registerName = c.getOperands().getFirst().getName();
				LinkedList<LLVM_GenericCommand> stores = reaching.get(registerName);
				if(stores!=null) {
					if(stores.size()==1) {
						LLVM_GenericCommand store = stores.getFirst();
						// Veraendere Load Befehl, store ist einzige Definition, die Load erreicht
						this.function.getRegisterMap().deleteCommand(c);
						
						// Erstelle  neuen Befehl
						LLVM_GenericCommand newCommand = new LLVM_BinaryCommand();
						newCommand.setOperation(LLVM_Operation.ADD);
						LinkedList<LLVM_Parameter> parameterList = new LinkedList<LLVM_Parameter>();
						LLVM_Parameter newParameter = store.getOperands().getFirst();
						parameterList.add(new LLVM_Parameter(newParameter.getName(),
								newParameter.getTypeString()));
						parameterList.add(new LLVM_Parameter("0",newParameter.getTypeString()));
						newCommand.setOperands(parameterList);
						newCommand.setTarget(c.getTarget());
						newCommand.setBlock(c.getBlock());
						newCommand.setPredecessor(c.getPredecessor());
						newCommand.setSuccessor(c.getSuccessor());
						c.replaceCommand(newCommand);
						
						this.function.getRegisterMap().addCommand(newCommand);
						
						changed.add(newCommand);
						
					}
				}
			}
		}
		
		this.function.constantPropagation(changed);
	}
	
	/**
	 * Erstelle gen und kill Mengen dieses Blockes fuer globale Lebendigkeitsanalyse
	 * gen : store i32 1, i32* %a -> Befehl wird hinzugefuegt, falls es kein spaeteres
	 * store auf a gibt (in diesem Block)
	 * kill : store i32 1, i32* %a -> alle anderen stores auf a werden hinzugefuegt
	 * (aus allen Bloecken)
	 */
	public void createGenKillSets() {
		if(!this.isEmpty()) {
			LLVM_GenericCommand c = this.lastCommand;
			while(c!=null) {
				if(LLVM_Operation.STORE==c.getOperation()) {
					
					// Register mit Speicheradresse steht in zweitem Operanden
					LLVM_Parameter p = c.getOperands().get(1);
					String registerName = p.getName();
					
					// Falls es vorheriges Store auf diesem Register gab, so ist der
					// aktuelle Befehl in der kill-Menge diese Blockes enthalten
					if(!this.kill.contains(c)) {
						this.gen.add(c);
					}
					
					// Suche alle anderen Stores auf diesem Register und fuege diese
					// Befehle der kill-Menge hinzu
					LinkedList<LLVM_GenericCommand> uses = this.function.getRegisterMap().
							getUses(registerName);
					if(uses != null){
						for(LLVM_GenericCommand u : uses) {
							if(LLVM_Operation.STORE==u.getOperation() && u!=c) {
								this.kill.add(u);
							}
						}
					}
				}

				c = c.getPredecessor();
			}
		}
		
	}
	
	/**
	 * Aktualisiere IN und OUT Mengen fuer Reachinganalyse
	 * Voraussetzung: gen und kill sind gesetzt
	 * @return true, falls OUT veraendert wurde
	 * TODO: not ready
	 */
	public boolean updateInOutReaching() {
		
		// this.in = out-Mengen aller Vorgaenger zusammenfuegen
		this.inReaching.clear();
		for(LLVM_Block b : this.previousBlocks) {
			LinkedList<LLVM_GenericCommand> outPreviousBlock = b.getOutReaching();
			for(LLVM_GenericCommand c : outPreviousBlock) {
				if(!this.inReaching.contains(c)) {
					this.inReaching.add(c);
				}
			}
		}
		
		// this.out = this.gen + (this.in - this.kill)
		LinkedList<LLVM_GenericCommand> outReachingOld = this.outReaching;
		this.outReaching = (LinkedList<LLVM_GenericCommand>) this.inReaching.clone();	// gibt doch neues obj zurueck?
		for(LLVM_GenericCommand c : this.kill) {
			this.outReaching.remove(c);
		}
		for(LLVM_GenericCommand c : this.gen) {
			if(!this.outReaching.contains(c)) {
				this.outReaching.add(c);
			}
		}
		
		return !(this.compareLists(outReachingOld, this.outReaching));
				
	}
	
	/*
	 * *********************************************************
	 * *********** Umgang mit Befehlen *************************
	 * *********************************************************
	 */

	private boolean labelCheck(String label) {
		
		if(label.charAt(0) == ';') {
			//String[] splitedLabel = label.split("[:;]");
			//this.label = "%"+splitedLabel[2].trim();
			//this.label_line = label;
			//return true;
			return false;
		}else{
			if (label.contains(":") && label.contains(";") && label.indexOf(';') < label.indexOf(':')) return false;
			String[] splitedLabel = label.split(":");
			
			if(splitedLabel.length >= 2){
				this.label = "%"+splitedLabel[0];
				//this.label_line = label;
				return true;
			}
		}
		
		return false;
	}
	
	private void createCommands() {
		String commandsArray[] = this.blockCode.split("\n");
		
		int i = 0;
		
		if(commandsArray[0].length() == 0){
			i++;
		}
		
		// Checking for label
		if(labelCheck(commandsArray[i])){
			i++;
		}
		
		this.firstCommand = mapCommands(commandsArray[i].trim(), null);
		
		LLVM_GenericCommand predecessor = firstCommand;
		for(i++; i<commandsArray.length; i++) {
			LLVM_GenericCommand c = mapCommands(commandsArray[i].trim(), predecessor);
			if(firstCommand == null){
				firstCommand = c;
				predecessor = c;
			}else{
				predecessor = c;
			}
		}
		this.lastCommand = predecessor;
	}
	
	// Ermittelt Operation und erzeugt Command mit passender Klasse
	//TODO elegante Methode finden, switch funktioniert auf Strings nicht!
	private LLVM_GenericCommand mapCommands(String cmdLine, LLVM_GenericCommand predecessor){
		
		// comment handling
		if (cmdLine.startsWith(";")){
			if (cmdLine.contains("<label>:")) return null;
			return new LLVM_Comment(cmdLine, predecessor, this);
		}
		
		// command handling
		if(cmdLine.startsWith("store ")){
			return new LLVM_StoreCommand(cmdLine, predecessor, this);
		}else if(cmdLine.startsWith("ret ")){
			return new LLVM_ReturnCommand(cmdLine, predecessor, this);
		}else if(cmdLine.startsWith("br ")){
			return new LLVM_BranchCommand(cmdLine, predecessor, this);
		}else if(cmdLine.contains(" = insertvalue ") || cmdLine.contains(" = extractvalue ")){
			return new LLVM_InsertExtractValueCommand(cmdLine, predecessor, this);
		}else if(cmdLine.contains(" = alloca ")){
			return new LLVM_AllocaCommand(cmdLine, predecessor, this);
		}else if(cmdLine.contains(" = and ") ||
				cmdLine.contains(" = or ") ||
				cmdLine.contains(" = xor ") ||
				cmdLine.contains(" = shl ") ||
				cmdLine.contains(" = lshr ") ||
				cmdLine.contains(" = ashr ") ||
				cmdLine.contains(" = add ") ||
				cmdLine.contains(" = fadd ") ||
				cmdLine.contains(" = sub ") ||
				cmdLine.contains(" = fsub ") ||
				cmdLine.contains(" = mul ") ||
				cmdLine.contains(" = fmul ") ||
				cmdLine.contains(" = udiv ") ||
				cmdLine.contains(" = sdiv ") ||
				cmdLine.contains(" = fdiv ") ||
				cmdLine.contains(" = urem ") ||
				cmdLine.contains(" = srem ") ||
				cmdLine.contains(" = frem ")){
			return new LLVM_BinaryCommand(cmdLine, predecessor, this);		
		}else if(cmdLine.contains(" = load ")){
			return new LLVM_LoadCommand(cmdLine, predecessor, this);
		}else if(cmdLine.contains(" = getelementptr ")){
			return new LLVM_GetElementPtrCommand(cmdLine, predecessor, this);
		}else if(cmdLine.contains(" = call ") || cmdLine.contains(" = tail call ") || cmdLine.startsWith("call ")){
			return new LLVM_CallCommand(cmdLine, predecessor, this);
		}else if(cmdLine.contains(" = icmp ")){
			return new LLVM_IcmpCommand(cmdLine, predecessor, this);
		}else if(!cmdLine.isEmpty()){
			return new LLVM_DummyCommand(cmdLine, predecessor, this);
		}else{
			return null;
		}
	}
	
	/*
	 * *********************************************************
	 * *********** Hilfsfunktionen *****************************
	 * *********************************************************
	 */

	/**
	 * Hilfsfunktion, um zwei String-Listen zu vergleichen
	 * Gibt true zurueck, wenn sie die gleichen Strings enthalten (Reihenfolge egal),
	 * sonst false
	 * @param <T>
	 * @param l1 Liste 1
	 * @param l2 Liste 2
	 * @return
	 */
	/*private boolean compareLists(LinkedList<String> l1, LinkedList<String> l2) {
		if(l1.size()!=l2.size()) {
			return false;
		}
		for(String s : l1) {
			if(!l2.contains(s)) {
				return false;
			}
		}
		return true;
	}*/
	
	private <T> boolean compareLists(LinkedList<T> l1, LinkedList<T> l2) {
		if(l1.size()!=l2.size()) {
			return false;
		}
		for(T s : l1) {
			if(!l2.contains(s)) {
				return false;
			}
		}
		return true;
	}
	
	public void deleteBlock() {

		for(LLVM_Block nextBlock : this.nextBlocks) {
			nextBlock.removeFromPreviousBlocks(this);
		}
		
	}
	
	public boolean isEmpty() {
		return (this.firstCommand==null);
	}
	
	public boolean hasPreviousBlocks() {
		return !(this.previousBlocks.isEmpty());
	}
	
	public int countCommands() {
		int count = 0;
		
		LLVM_GenericCommand tmp = getFirstCommand();
		
		while(tmp != null){
			count++;
			tmp = tmp.getSuccessor();
		}
		
		return count;
	}
	
	/*
	 * *********************************************************
	 * *********** Setter / Getter / toString ******************
	 * *********************************************************
	 */
	
	public void setFirstCommand(LLVM_GenericCommand first) {
		this.firstCommand = first;
	}

	public void setLastCommand(LLVM_GenericCommand last) {
		this.lastCommand = last;
	}
	
	public LLVM_GenericCommand getFirstCommand() {
		return firstCommand;
	}

	public LLVM_GenericCommand getLastCommand() {
		return lastCommand;
	}
	
	public LinkedList<LLVM_Block> getNextBlocks() {
		return nextBlocks;
	}

	public void appendToNextBlocks(LLVM_Block block) {
		this.nextBlocks.add(block);
	}
	
	public void removeFromNextBlocks(LLVM_Block block) {
		this.nextBlocks.remove(block);
	}

	public LinkedList<LLVM_Block> getPreviousBlocks() {
		return previousBlocks;
	}

	public void appendToPreviousBlocks(LLVM_Block block) {
		this.previousBlocks.add(block);
	}
	
	public void removeFromPreviousBlocks(LLVM_Block block) {
		this.previousBlocks.remove(block);
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public LinkedList<String> getInLive() {
		return inLive;
	}
	
	public LinkedList<LLVM_GenericCommand> getOutReaching() {
		return outReaching;
	}

	public String toString() {
		
		String code = "";
		
		if(!this.label.matches("%[1-9][0-9]*") && !this.label.equals("")) {
			code = label.substring(1)+":\n";
		}
		
		LLVM_GenericCommand tmp = firstCommand;
		while(tmp != null){
			code += "\t"+tmp.toString();
			tmp = tmp.getSuccessor();
		}
		
		return code;
	}
	
	public String toGraph() {
		String graph = "\""+label+"\" [ style = \"filled, bold\" penwidth = 5 fillcolor = \"white\" fontname = \"Courier New\" shape = \"Mrecord\" label =<<table border=\"0\" cellborder=\"0\" cellpadding=\"3\" bgcolor=\"white\"><tr><td bgcolor=\"black\" align=\"center\" colspan=\"2\"><font color=\"white\">"+label+"</font></td></tr>";
		
		LLVM_GenericCommand tmp = firstCommand;
		while(tmp != null){
			graph += "<tr><td align=\"left\">"+ tmp.toString() +"</td></tr>";
			tmp = tmp.getSuccessor();
		}
		
		return graph + "</table>> ];";
	}
}
