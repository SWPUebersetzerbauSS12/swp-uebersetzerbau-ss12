import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import de.fuberlin.Main;

/**
 * Not a real unit test at the moment,
 * but useful for quickly testing parameters to main()
 */
public class MainTest {

	static String readFile(String file) throws FileNotFoundException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String content = "";
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				content += line + '\n';
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		return content;
	}

	// Helper
	static String createEmptyFile() {
		File file;
		try {
			file = File.createTempFile("test", ".tmp");
		} catch (IOException e) {
			System.err.println("Failed to create temporary file: " + e);
			return "";
		}
		return file.getAbsolutePath();
	}

	// Helper
	static String createFile(String content) {
		File file = null;
		try {
			file = File.createTempFile("test", ".ll");
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			System.err.println("Failed to create temporary file: " + e);
			return "";
		}
		assert(file != null);
		return file.getAbsolutePath();
	}

	@Test
	public void testAllProjecta() throws Exception {
		final String inputFile = createFile("def int main() { return 0; }");
		final String outputFile = createEmptyFile();
		String[] args = {"-f", inputFile, "-o", outputFile};
		Main.main(args);

		// check output file
		String output = readFile(outputFile);
		System.out.println(outputFile);
		assertFalse(output.isEmpty());
		System.out.println(output);
	}

	@Test
	public void testLlvmToMachineCode() throws Exception {
		final String inputFile = createFile(
				"declare i32 @puts(i8*) nounwind\n" + 
				"declare i32 @printf(i8*, ...) nounwind\n" +
				"define i32 @main() nounwind {\n" +
				"ret i32 0;\n" +
				"<label>:1\n" +
				"}\n"
		);
		final String outputFile = createEmptyFile();
		String[] args = {"-llvm", inputFile, "-o", outputFile};
		Main.main(args);

		// check output file
		String output = readFile(outputFile);
		System.out.println(outputFile);
		assertFalse(output.isEmpty());
		System.out.println(output);
	}

}
