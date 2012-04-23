package lexer.io;

public interface ICharStream {

	/**
	 * Get next bunch of characters as String
	 *
	 * @param numberOfChars
	 *            Number of chars to fetch
	 * @return Next chars
	 */
	public String getNextChars(int numberOfChars);

	/**
	 * Removes an amount of characters at the beginning
	 *
	 * @param numberOfChars
	 *            how many characters should be removed
	 * @return how many characters were actually removed
	 */
	public int consumeChars(int numberOfChars);

	/**
	 * Get the offset of the lookahead relativ to beginning of the line
	 *
	 * @note The user of this class has to take care of resetting the counter on
	 *       newline
	 * @see resetCounter
	 * @return Offset
	 */
	public int getOffset();

	/**
	 * Reset the offset counter
	 */
	public void resetOffset();

	/**
	 * @return True if no more bytes available to read, else False
	 */
	public boolean isEmpty();

}