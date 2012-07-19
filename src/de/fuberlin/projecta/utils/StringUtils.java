package de.fuberlin.projecta.utils;

import java.io.File;

import de.fuberlin.projecta.lexer.io.FileCharStream;
import de.fuberlin.projecta.lexer.io.StringCharStream;

public class StringUtils {

	public static StringCharStream readFromStdin() {
		String data = IOUtils.readMultilineStringFromStdin();
		return new StringCharStream(data);
	}

	public static FileCharStream readFromFile(String path) {
		File sourceFile = new File(path);
		if (!sourceFile.exists()) {
			System.out.println("File does not exist.");
			return null;
		}

		if (!sourceFile.canRead()) {
			System.out.println("File is not readable");
			return null;
		}

		return new FileCharStream(path);
	}

	/**
	 * Repeat char n times
	 */
	public static String repeat(char c, int times) {
		StringBuilder b = new StringBuilder();

		for (int i = 0; i < times; i++) {
			b.append(c);
		}

		return b.toString();
	}

}
