package tokenmatcher;

/**
 * The TokenAttributor yields the corresponding attribute of an token in addiction 
 * to the token type and the readed lexem.
 * 
 * @author Yanlei Li
 *         Johannes Dahlke
 *
 */
public class TokenAttributor {
	
	/**
	 * Converts a lexem depending of the token type from string type to the target type of the token.
	 * e.g. the lexem "123" with token type INT will be convertet to an int value 123.
	 *  
	 * @param lexem the chunked string from the input source 
	 * @param tokenType the identified type
	 * @return an value with common type
	 */
	public Object convertLexemToAttributeForTokenWithType( String lexem, TokenType tokenType) {
		switch ( tokenType) {
			/** handle relational operators <(LT), <=(LE), ==(EQ), !=(NE), >(GT), >=(GE) */
			case OP_LT: 
			case OP_LE: 
			case OP_EQ:
			case OP_NE:
			case OP_GT:
			case OP_GE: 
				return null;
			/** handle logical operators ||(OR), &&(AND), !(NOT) */
			case OP_OR :
			case OP_AND :
			case OP_NOT :
				return null;
		  // etc 
			// ...
			// for all types in TokenType
			// some of them like INT needs a special conversion
			case INT:
				return Integer.valueOf( lexem);
			// but aware: In an INT we can store just as well an lexem in HEX-format like 0xA0F4 or 
			// tip: use Integer.valueOf( lexem, 16);
			// and so on for all defined token types
			default:
			  return null;
		}
	}

}
