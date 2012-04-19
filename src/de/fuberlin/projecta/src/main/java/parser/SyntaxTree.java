package parser;

public interface SyntaxTree {
	
	public void addTree(SyntaxTree tree);
	
	public void removeTree(int i);
	
	public String getName();
	
	/**
	 * @return the number of children
	 */
	public int getChildrenCount();
	
	/** 
	 * @param i		
	 * @return the i'th children of current node, null if none is existing.		
	 */
	public SyntaxTree getChildren(int i);	
	
	/**
	 * 
	 * @return	The desired attribute. Null if none is found.
	 */
	public Attribute getAttribute(String name);
	
	/**
	 * Changes the value of the attribute
	 * @param name	the attribute's name
	 * @param value	the value to change the attribute to.
	 * @return	True if change was successful, False otherwise.
	 */
	public boolean setAttribute(String name, String value);
	
	/**
	 * Adds a new attribute to the nodes attribute collection, if this name is not already inserted.
	 * @param name	the attribute's name to insert.
	 * @return	True if add was successful, False otherwise.
	 */
	public boolean addAttribute(String name);
}
