package de.fuberlin.projecta.analysis.ast.nodes;




public abstract class Statement extends AbstractSyntaxTree {
	private int label;

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}
	
}
