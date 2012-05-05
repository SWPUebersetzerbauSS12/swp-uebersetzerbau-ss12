package semantic.analysis;

import java.util.HashMap;

/**
 * @author Christian Cikryt
 */
public class SymbolTable {

	private HashMap<String, Object> hashMap;

	public SymbolTable() {
		hashMap = new HashMap<String, Object>();
	}

	/**
	 * Updates the entry associated with name. If it is not contained yet it
	 * gets added, otherwise the current value will be overridden.
	 * 
	 * @param name
	 * @param entry
	 */
	public void updateEntry(String name, Object value) {
		if (!hashMap.containsKey(name))
			throw new IllegalStateException("No such symbol: " + name);
		
		hashMap.put(name, value);
	}
	
	public void insertEntry(String name, Object value) {
		if (hashMap.containsKey(name))
			throw new IllegalStateException("Symbol already defined:" + name);
		
		hashMap.put(name, value);
	}

	public Object lookup(String name) {
		return hashMap.get(name);
	}
}
