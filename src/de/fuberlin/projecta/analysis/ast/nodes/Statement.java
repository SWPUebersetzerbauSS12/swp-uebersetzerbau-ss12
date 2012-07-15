package de.fuberlin.projecta.analysis.ast.nodes;

public abstract class Statement extends AbstractSyntaxTree {

	private int beginLabel, endLabel;

	public int getBeginLabel() {
		return beginLabel;
	}

	public void setBeginLabel(int label) {
		this.beginLabel = label;
	}
	
	public int getEndLabel() {
		return endLabel;
	}

	public void setEndLabel(int label) {
		this.endLabel = label;
	}
	
}
