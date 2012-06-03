package analysis;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import analysis.ast.nodes.BasicType;
import analysis.ast.nodes.Id;
import analysis.ast.nodes.Type;

/**
 * @author Christian Cikryt
 */
public class EntryType {

	public EntryType(Id id, Type type, List<EntryType> params) {
		this.id = id.getValue();
		this.type = type;
		this.params = params;
	}

	public EntryType(Id id, Type type) {
		this.id = id.getValue();
		this.type = type;
		params = new ArrayList<EntryType>();
	}

	@Getter
	private String id;

	@Getter
	private Type type;

	/**
	 * This may be an empty list of parameters (for declaring functions with
	 * same name and return type, but different arguments)
	 */
	@Getter
	private List<EntryType> params;

	/**
	 * @return true if id, type and parameters equal one another, false
	 *         otherwise. TODO: please make this much more pretty!
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof EntryType) {
			EntryType ot = (EntryType) o;

			if (id.getClass() == ot.getId().getClass()
					&& type.getClass() == ot.getType().getClass()) {
				if (type instanceof BasicType) {
					if (((BasicType) type).getType() == ((BasicType) ot
							.getType()).getType()) {
						for (int i = 0; i < params.size(); i++) {
							if (!params.get(i).equals(ot.getParams().get(i))) {
								return false;
							}
						}
					} else {
						return false;
					}
				} else {
					for (int i = 0; i < params.size(); i++) {
						if (!params.get(i).equals(ot.getParams().get(i))) {
							return false;
						}
					}
				}
				return true;
			}
		}
		System.out.println("equal failed: " + this.getClass() + ", "
				+ o.getClass());
		return false;
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
}
