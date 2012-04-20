package utils;

/**
 * Die Klasse stellt vorgefertigte Meldungen zur Verfügung.
 * 
 * @author Johannes
 */
public class Notification {

	// error message handling

	private static boolean printErrorMessages = true;


	public static void enableErrorPrinting() {
		printErrorMessages = true;
	}


	public static void disableErrorPrinting() {
		printErrorMessages = false;
	}


	public static void printErrorMessage( String message) {
		if ( printErrorMessages)
			System.err.println( "NotificationService(Error): " + message);
	}

	// debugmessage handling

	private static boolean printDebugMessages = false;


	public static void enableDebugPrinting() {
		printDebugMessages = true;
	}


	public static void disableDebugPrinting() {
		printDebugMessages = false;
	}


	public static void printDebugMessage( String message) {
		if ( printDebugMessages)
			System.err.println( "NotificationService(Debug): " + message);
	}


	public static void printDebugException( Exception e) {
		if ( printDebugMessages) {
			System.err.println( "NotificationService(Debug): ");
			e.printStackTrace();
		}
	}

	// info message handling

	private static boolean printInfoMessages = false;


	public static void enableInfoPrinting() {
		printInfoMessages = true;
	}


	public static void disableInfoPrinting() {
		printInfoMessages = false;
	}


	public static void printInfoMessage( String message) {
		if ( printInfoMessages)
			System.out.println( "NotificationService(Info): " + message);
	}
	
  //info message handling

	private static boolean printDebugInfoMessages = false;


	public static void enableDebugInfoPrinting() {
		printDebugInfoMessages = true;
	}


	public static void disableDebugInfoPrinting() {
		printDebugInfoMessages = false;
	}


	public static void printDebugInfoMessage( String message) {
		if ( printDebugInfoMessages)
			System.out.println( "NotificationService(DebugInfo): " + message);
	}

}
