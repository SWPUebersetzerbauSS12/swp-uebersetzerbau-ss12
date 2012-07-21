package de.fuberlin.projecta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import de.fuberlin.projecta.lexer.io.FileCharStream;
import de.fuberlin.projecta.lexer.io.ICharStream;
import de.fuberlin.projecta.utils.StringUtils;

/**
 * Runnable class for executing the complete frontend + LLC + GCC
 * 
 * Main testing tool for projecta!
 * 
 * Results in a runnable binary, which is executed immediately
 * @note You need to have 'llc' + 'gcc' in PATH
 */
public class CompilerMain {

	static final String LLC_EXE = "llc";
	static final String GCC_EXE = "gcc";

	public static void main(String[] args) {
		// action
		boolean printHelp = false;
		boolean readFromStdin = true; // read from stdin by default
		// parameters
		boolean verbose = false;
		String filePath = "";

		for(int i = 0; i < args.length; i++) {
			if (args[i].equals("-f")) {
				filePath = args[i++];
			} else if (args[i].equals("-v")) {
				verbose = true;
			} else if (args[i].equals("-h")) {
				printHelp = true;
			}
		}

		// run
		if (printHelp) {
			printHelp();
			return;
		} else if (readFromStdin) {
			System.out
					.println("Reading from stdin. Exit with new line and Ctrl+D.");
			ICharStream stream = StringUtils.readFromStdin();
			run(stream, verbose);
		} else if (filePath.isEmpty()) {
			FileCharStream stream = StringUtils.readFromFile(filePath);
			run(stream, verbose);
		} else {
			System.out.println("Wrong parameters.");
		}
	}

	private static void printHelp() {
		System.out.println("CompilerMain:");
		System.out.println("  -h        Show this help");
		System.out.println("  -v        Turn on debugging");
		System.out.println("  -f FILE   Read from file FILE");
	}

	/**
	 * Run the compiler frontend + backend
	 * @param stream Character stream
	 * @param verbose If true, print out debugging output
	 * @return Output from running the binary
	 */
	public static String execute(ICharStream stream, boolean verbose) {
		final String code = FrontendMain.genCode(stream, verbose);
		if (code == null) {
			System.err.println("Code generation failed.");
			return null;
		}

		if (verbose) {
			System.err.println("\nGenerated code:");
			System.err.println(code);
		}

		try {
			// write LLVM code to file
			File llvmFile = File.createTempFile("code", ".ll");
			BufferedWriter writer = new BufferedWriter(new FileWriter(llvmFile));
			writer.write(code);
			writer.close();

			// call "llc"
			File assemblyFile = File.createTempFile("code", ".s");
			{
				String[] command = { LLC_EXE, llvmFile.getCanonicalPath(),
						"-o", assemblyFile.getCanonicalPath() };
				Process p = Runtime.getRuntime().exec(command);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						p.getErrorStream()));
				String text;
				while ((text = in.readLine()) != null) {
					System.out.println(text);
					System.out.flush();
				}
			}

			// call "gcc"
			File executableFile = File.createTempFile("code", ".bin");
			executableFile.setExecutable(true);
			{
				String[] command = { GCC_EXE, assemblyFile.getCanonicalPath(),
						"-o", executableFile.getCanonicalPath() };
				Process p = Runtime.getRuntime().exec(command);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						p.getErrorStream()));
				String text;
				while ((text = in.readLine()) != null) {
					System.out.println(text);
					System.out.flush();
				}
			}

			// call generated executable
			{
				String[] command = { executableFile.getCanonicalPath() };
				System.err.println("Running "
						+ executableFile.getCanonicalPath());
				Process p = Runtime.getRuntime().exec(command);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				String result = "";
				String text;
				while ((text = in.readLine()) != null) {
					result += text;
				}
				return result;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	static void run(ICharStream stream, boolean verbose) {
		String output = execute(stream, verbose);
		
		if (output != null) {
			System.out.println("\nOutput:");
			System.out.println(output);
		}
	}

}
