package tokenmatcher.attributes;


public class StringAttribute implements Attribute {

	private String value;
	
	public StringAttribute( String value) {
	  super();
	  this.value = value;
	}
	
	public Object lexemToValue( String lexem) {
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
