package regextodfaconverter.directconverter.syntaxtree.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import regextodfaconverter.directconverter.syntaxtree.PrintHandler;
import utils.Test;


public class TreeNode<Value> implements Cloneable {

	protected List<Value> values;
	protected InnerNode parentNode = null;
	private PrintHandler printHandler = null;
	
	public TreeNode( Value value, Value ... values) {
		this( null, value, values);
	}
	
	public TreeNode( InnerNode parentNode, Value value, Value ... values) {
		super();
		this.parentNode = parentNode;
	  this.values = new ArrayList<Value>();
	  addValue( value);
	  addValues( values);
	}
	
	public Value getValue() {
		return values.get( 0);
	}
	
	public void addValue( Value value) {
		this.values.add( value);
	}

	public void addValues( Value ... values) {
		this.values.addAll( Arrays.asList( values));
	}
	
	public void addValues( List<Value> values) {
		this.values.addAll( values);
	}
	
	public void insertValue( int index, Value value) {
		this.values.add( index, value);
	}

	public void insertValues( int index, Value ... values) {
		this.values.addAll( index, Arrays.asList( values));
	}
	
	public void insertValues( int index, List<Value> values) {
		this.values.addAll( index, values);
	}
	
	public List<Value> getValues() {
		return values;
	}
	
	public InnerNode getParentNode() {
		return parentNode;
	}
	
	public void setParentNode( InnerNode newParentNode) {
		if ( Test.isAssigned(  this.parentNode))
			this.parentNode.removeChild( this);
		if ( Test.isAssigned( newParentNode))
			newParentNode.addChild( this);
		else 
			parentNode = null;
	}
	
	public void setParentNode( InnerNode newParentNode, int parentIndex) {
		if ( Test.isAssigned(  this.parentNode))
			this.parentNode.removeChild( this);
		if ( Test.isAssigned( newParentNode))
			newParentNode.insertChild( this, parentIndex);
		else 
			parentNode = null;
	}
	
	public boolean isRootNode() {
		return Test.isUnassigned( parentNode);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		TreeNode<Value> clonedTreeNode = (TreeNode<Value>) super.clone();
		clonedTreeNode.parentNode = this.parentNode;
		clonedTreeNode.values = new ArrayList<Value>( this.values);
		return clonedTreeNode;
	}
	
	@Override
	public String toString() {
		return Test.isAssigned( printHandler) 
				? printHandler.print( values.toArray())
			  : ( values.size() == 1
			        ? values.get( 0).toString()
			        : values.toString()
			    );
	}
	
	
	public void setPrintHandler( PrintHandler printHandler) {
		this.printHandler = printHandler;
	}
	
}
