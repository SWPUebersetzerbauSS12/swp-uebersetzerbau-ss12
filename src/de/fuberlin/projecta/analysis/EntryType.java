package de.fuberlin.projecta.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fuberlin.projecta.analysis.ast.nodes.Id;
import de.fuberlin.projecta.analysis.ast.nodes.Type;

/**
 * This class represents an entry of a symboltable. It has at least an id and a
 * type. Optionally it has a list of EntryTypes, which correspond to parameters
 * which are defined by declaring functions.
 * 
 */
public class EntryType {

	public EntryType(Id id, Type type, List<EntryType> params) {
		this.id = id.getValue();
		this.type = type;
		this.params = params;
	}

	public EntryType(Id id, Type type) {
		this(id, type, new ArrayList<EntryType>());
	}

	private String id;

	private Type type;

	/**
	 * This may be an empty list of parameters (for declaring functions with
	 * same name and return type, but different arguments)
	 */
	private List<EntryType> params;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof EntryType))
			return false;
		EntryType et = (EntryType) o;
		return this.id.equals(et.id) && this.type.equals(et.type) && Arrays.equals(this.params.toArray(), et.params.toArray());
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (type != null ? type.hashCode() : 0);
		if (params != null)
			for (EntryType t : params)
				result = 31 * result + (t != null ? t.hashCode() : 0);
		else
			result = 31 * result;
		return result;
	}

	@Override
	public String toString() {
		String ret = id + ":" + type.getClass() + ":params(";
		for (EntryType entry : params) {
			ret += entry;
		}
		ret += ")";
		return ret;
	}

	public String getId() {
		return id;
	}

	public Type getType() {
		return type;
	}

	public List<EntryType> getParams() {
		return params;
	}
}
