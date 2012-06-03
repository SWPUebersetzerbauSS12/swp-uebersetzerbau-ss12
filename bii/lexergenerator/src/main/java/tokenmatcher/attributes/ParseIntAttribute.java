package tokenmatcher.attributes;


public class ParseIntAttribute implements Attribute {

	private String value;
	
	public ParseIntAttribute() {
	  super();
	}
	
	public Object lexemToValue( String lexem) {
		value = lexem;
	  return Integer.parseInt( lexem);	
	}

	public Class getValueType() {
		return Integer.class;
	}

	@Override
	public String toString() {
		return value;
	}
	
}
