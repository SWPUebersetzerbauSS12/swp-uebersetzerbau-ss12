package regextodfaconverter;

/**
 * Stellt grundlegende Funktionen zum Arbeiten mit regulären Ausdrücken bereit.
 * @author Daniel Rotar
 *
 */
public class Regex {

	/**
	 * Reduziert den angebenen regulären Ausdruck auf die Grundoperationen (AB, A|B, A*) und klammert den Ausdruck korrekt.
	 * @param regex Der zu reduzierende und zu klammernde Ausdruck.
	 * @return Ein äquivalenter reduzierter und geklammerte Ausdruck.
	 */
	public static String reduceAndAddParenthesis(String regex)
	{
		//Auf Grundoperationen reduzieren.
		regex = reducePlusSigns(regex);
		regex = reduceQuestionMarks(regex);
		regex = reduceBrackets(regex);
		
		//Klammerung einfügen.
		regex = addMissingParenthesis(regex);
		
		return regex;
	}

	private static String reducePlusSigns(String regex)
	{
		//TODO: reducePlusSigns implementieren.
		return regex;
	}
	
	private static String reduceQuestionMarks(String regex)
	{
		//TODO: reduceQuestionMarks implementieren.
		return regex;
	}
	
	private static String reduceBrackets(String regex)
	{
		//TODO: reduceBrackets implementieren.
		return regex;
	}
	
	private static String addMissingParenthesis(String regex)
	{
		//TODO: addMissingParenthesis implementieren.
		return regex;
	}
	
	public static boolean isValid(String regex)
	{
		//TODO: isValid implementieren.
		return false;
	}
	
	public static boolean containsOnlyBasicOperations()
	{
		//TODO: containsOnlyBasicOperations implementieren.
		return false;
	}
}
