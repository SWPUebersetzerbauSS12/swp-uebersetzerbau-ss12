package lexer;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import lombok.Getter;

public class InputStream {

	ArrayList<Character> text = new ArrayList<Character>();

	@Getter
	private int offset;

	public InputStream(String file) {
		try {
			this.offset = 0;
			FileReader fr = new FileReader(file);
			char[] peek = new char[1];
			while (fr.read(peek) > -1) {
				text.add(peek[0]);
			}
		} catch (IOException e) {
			System.out.println("WRONG");
		}
	}

	public String getNextChars(int numberOfChars) {
		String result = "";
		int count = Math.min(numberOfChars, text.size());
		for (int i = 0; i < count; i++) {
			result += text.get(i).toString();
		}
		return result;
	}

	/**
	 * removes an amount of characters at the beginning
	 *
	 * @param numberOfChars
	 *            how many characters should be removed
	 * @return how many characters were actually removed
	 */
	public int removeChars(int numberOfChars) {
		int result = 0;
		this.offset += numberOfChars;
		while (!text.isEmpty() && result < numberOfChars) {
			text.remove(0);
			result++;
		}
		return result;
	}

	public boolean isEmpty() {
		return text.isEmpty();
	}

	public void resetOffset() {
		this.offset = 0;
	}

}
