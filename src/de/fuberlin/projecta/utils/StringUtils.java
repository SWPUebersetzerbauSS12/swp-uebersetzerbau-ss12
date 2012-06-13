package de.fuberlin.projecta.utils;

public class StringUtils {

	public static String repeat(char c, int times) {
		StringBuilder b = new StringBuilder();

		for (int i = 0; i < times; i++) {
			b.append(c);
		}

		return b.toString();
	}

}
