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

public class CompilerMain {

	static final String LLC_EXE = "llc-3.0";
	static final String GCC_EXE = "gcc";
	
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out
					.println("Reading from stdin. Exit with new line and Ctrl+D.");
			ICharStream stream = StringUtils.readFromStdin();
			run(stream);
		} else if (args.length == 1) {
			final String path = args[0];
			FileCharStream stream = StringUtils.readFromFile(path);
			run(stream);
		} else {
			System.out.println("Wrong number of parameters.");
		}
	}

	public static String execute(ICharStream stream) {
		final String code = FrontendMain.genCode(stream);
		if (code == null) {
			System.err.println("Code generation failed.");
			return null;
		}

		System.err.println("Generated code:");
		System.err.println(code);

		try {
			// write LLVM code to file
			File llvmFile = File.createTempFile("code", ".ll");
			BufferedWriter writer = new BufferedWriter(new FileWriter(llvmFile));
			writer.write(code);
			writer.close();

			// call "llc"
			File assemblyFile = File.createTempFile("code", ".s");
			{
				String[] command = {LLC_EXE,
						llvmFile.getCanonicalPath(),
						"-o", assemblyFile.getCanonicalPath()
				};
				Process p = Runtime.getRuntime().exec(command);
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
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
				String[] command = { GCC_EXE,
						assemblyFile.getCanonicalPath(),
						"-o", executableFile.getCanonicalPath() 
				};
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
				System.err.println("Running " + executableFile.getCanonicalPath());
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

	static void run(ICharStream stream) {
		String output = execute(stream);
		System.out.println(output);
	}

	

}
