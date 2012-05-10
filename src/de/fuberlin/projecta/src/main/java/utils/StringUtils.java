package utils;

public class StringUtils {

	public static String repeat(char c, int times) {
		StringBuffer b = new StringBuffer();

		for (int i = 0; i < times; i++) {
			b.append(c);
		}

		return b.toString();
	}

}
