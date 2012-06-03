package tokenmatcher.attributes;


public interface Attribute {

	Object lexemToValue( String lexem);
	
	Class getValueType();
	
	
}
