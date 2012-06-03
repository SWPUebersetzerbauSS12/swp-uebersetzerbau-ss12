package analysis;

import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Christian Cikryt
 */
public interface ISymbolTable {

	/**
	 * Updates the entry associated with name. If it is not contained yet it
	 * gets added, otherwise the current value will be overridden.
	 * 
	 * @param name
	 * @param entry
	 */
	public void updateEntry(String name, EntryType value);

	public void insertEntry(String name, EntryType value);

	/**
	 * Find value for a specific key
	 * 
	 * @param name
	 *            Key
	 * @return Value, may be null
	 */
	public EntryType lookup(String name);
	
	/**
	 * 
	 * @return
	 * 		A set of all entries of this SymbolTable
	 */
	public Set<Entry<String, EntryType>> getEntries();
}
