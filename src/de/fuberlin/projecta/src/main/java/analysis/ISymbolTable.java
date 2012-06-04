package analysis;

import java.util.List;

import analysis.ast.nodes.Id;
import analysis.ast.nodes.Type;

/**
 * @author Christian Cikryt
 */
public interface ISymbolTable {

	/**
	 * Inserts the given entry into the symboltable. If it is already defined an
	 * IllegalStateException is thrown.
	 * 
	 * @param entry
	 *            the entry to insert.
	 * @throws IllegalStateException
	 */
	public void insertEntry(EntryType entry) throws IllegalStateException;

	/**
	 * Generates a new entry from the given parameters and inserts it. If an
	 * equal entry is already defined an IllegalStateException is thrown.
	 * 
	 * 
	 * @param id
	 * @param type
	 * @param params
	 * @throws IllegalStateException
	 */
	public void insertEntry(Id id, Type type, List<EntryType> params)
			throws IllegalStateException;

	/**
	 * Generates a new entry from the given parameters and inserts it. If an
	 * equal entry is already defined an IllegalStateException is thrown.
	 * 
	 * @param id
	 * @param type
	 * @throws IllegalStateException
	 */
	public void insertEntry(Id id, Type type) throws IllegalStateException;

	/**
	 * Updates the entry associated with name. If it is not contained yet it
	 * gets added, otherwise the current value will be overridden.
	 * 
	 * @param name
	 * @param entry
	 */
	public void updateEntry(EntryType value) throws IllegalStateException;

	/**
	 * Find value for a specific key
	 * 
	 * @param name
	 *            Key
	 * @return Value, may be null
	 */
	public EntryType lookup(String name);

	/**
	 * This is basically the method to look for function definitions.
	 * 
	 * @param name
	 *            function name
	 * @param params
	 *            function parameter
	 * @return The Entry for this method, null if not existing
	 */
	public EntryType lookup(String name, List<EntryType> params);

	/**
	 * 
	 * @return A list of all entries of this SymbolTable
	 */
	public List<EntryType> getEntries();

}
