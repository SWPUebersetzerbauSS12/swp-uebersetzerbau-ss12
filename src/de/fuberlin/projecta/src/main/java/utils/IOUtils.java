package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class IOUtils {

	public static String readMultilineStringFromStdin() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String data = "";
		String line;
		try {
			while ((line = in.readLine()) != null && line.length() != 0) {
				data += line + '\n';
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		return data;
	}

}
