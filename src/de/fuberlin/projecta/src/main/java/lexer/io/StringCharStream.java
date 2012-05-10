package lexer.io;

import lombok.Getter;

public class StringCharStream implements ICharStream {

	private String data;

	@Getter
	private int offset;

	public StringCharStream(String data) {
		this.data = data;
		this.offset = 0;
	}


	public String getNextChars(int numberOfChars) {
		final int count = Math.min(numberOfChars, data.length());
		return data.substring(0, count);
	}


	public int consumeChars(int numberOfChars) {
		final int count = Math.min(numberOfChars, data.length());
		data = data.substring(count, data.length());
		this.offset += count;
		return count;
	}


	public void resetOffset() {
		this.offset = -1;
	}


	public boolean isEmpty() {
		return data.length() == 0;
	}

}
