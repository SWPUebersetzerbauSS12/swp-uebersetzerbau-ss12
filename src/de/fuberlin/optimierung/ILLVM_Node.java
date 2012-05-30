package de.fuberlin.optimierung;

import java.util.LinkedList;

public interface ILLVM_Node {
	public LinkedList<ILLVM_Node> getChildren();
	public boolean addParent(ILLVM_Node parent);
	public ILLVM_Command getCommand();
}
