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

	@Override
	public String getNextChars(int numberOfChars) {
		final int count = Math.min(numberOfChars, data.length());
		return data.substring(0, count);
	}

	@Override
	public int consumeChars(int numberOfChars) {
		final int count = Math.min(numberOfChars, data.length());
		data = data.substring(count, data.length());
		this.offset += count;
		return count;
	}

	@Override
	public void resetOffset() {
		this.offset = -1;
	}

	@Override
	public boolean isEmpty() {
		return data.length() == 0;
	}

}
