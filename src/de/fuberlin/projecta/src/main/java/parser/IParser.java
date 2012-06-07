package parser;


public interface IParser {

	/**
	 * Interface for triggering a parse
	 *
	 * @throws RuntimeException
	 */
	public ISyntaxTree parse();

}
