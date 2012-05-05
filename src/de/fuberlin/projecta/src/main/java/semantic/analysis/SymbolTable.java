package semantic.analysis;

import java.util.HashMap;

/**
 * @author Christian Cikryt
 */
public class SymbolTable {

	private HashMap<String, SymbolTableEntry> hashMap;

	public SymbolTable() {
		hashMap = new HashMap<String, SymbolTableEntry>();
	}

	/**
	 * Updates the entry associated with name. If it is not contained yet it
	 * gets added, otherwise the current value will be overridden.
	 * 
	 * @param name
	 * @param entry
	 */
	public void updateEntry(String name, SymbolTableEntry entry) {
		hashMap.put(name, entry);
	}

	public SymbolTableEntry getEntryByName(String name) {
		return hashMap.get(name);
	}
}
