package de.fuberlin.projectF.CodeGenerator;

public class Debuginfo {
	boolean debug;
	public Debuginfo(boolean debug) {
		this.debug = debug;
	}
	
	public void print(String info) {
		if(debug)
			System.out.print(info);
	}
	
	public void println(String info) {
		if(debug)
			System.out.println(info);
	}
	
	public boolean getDebugflag() {
		return debug;
	}
}
