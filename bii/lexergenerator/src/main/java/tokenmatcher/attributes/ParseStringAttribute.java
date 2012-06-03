package tokenmatcher.attributes;


public class ParseStringAttribute implements Attribute {

	private String value;
	
	public ParseStringAttribute() {
	  super();
	}
	
	public Object lexemToValue( String lexem) {
		value = lexem;
	  return value;	
	}

	public Class getValueType() {
		return String.class;
	}

	@Override
	public String toString() {
		return value;
	}
	
}
