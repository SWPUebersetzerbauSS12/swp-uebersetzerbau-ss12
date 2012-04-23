package ast;

import java.util.List;

import parser.IAttribute;

/**
 * Using composite pattern as tree data-structure. Leafs are indicated by a
 * childCount of 0.
 */
public interface ISyntaxTree {

	public void addTree(ISyntaxTree tree);

	public String getName();

	/**
	 * @return the number of children
	 */
	public int getChildrenCount();

	/**
	 * @param i child index
	 * @return the i'th child of current node, null if none is existing.
	 */
	public ISyntaxTree getChild(int i);

	/**
	 * @return The desired attribute. Null if none is found.
	 */
	public IAttribute getAttribute(String name);


	public List<ISyntaxTree> getChildrenByName(String name);

	/**
	 * Changes the value of the attribute
	 *
	 * @param name the attribute's name
	 * @param value the value to change the attribute to.
	 * @return True if change was successful, False otherwise.
	 */
	public boolean setAttribute(String name, String value);

	/**
	 * Adds a new attribute to the nodes attribute collection, if this name is
	 * not already inserted.
	 *
	 * @param name the attribute's name to insert.
	 * @return True if add was successful, False otherwise.
	 */
	public boolean addAttribute(String name);
}
