package de.fuberlin.commons.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * 
 * Fabrik zum Erzeugen von java.util.logging.Logger mit SingleLineFormatter
 */
public class LogFactory{
	private static Level DEFAULT_LEVEL=Level.INFO;

	static{
		init(DEFAULT_LEVEL, null, null); // Default-Konfiguration setzen
	}
	
	/**
	 * Erzeugt ein Logger-Objekt für die übergebene Klasse.
	 * <p>
	 * Tipp: Man kann Logger-Deklarationen einfach via ContentAssist einfügen, wenn man in den Eclipse-Preferences unter Java/Editor/Templates folgendes Template einfügt:
	 * <code>
	 * Name: Logger
	 * Context: Java
	 * AutoInsert: True
	 * Description: Erzeugt eine Logger-Deklaration
	 * Pattern: private static Logger logger=LogFactory.getLogger(${enclosing_type}.class);
	 * </code>
	 * In einer Java-Klasse braucht man dann nur noch 'Logger' eingeben, Strg+Space (ContentAssist) klicken und einmal Strg-Shift+O (OrganizeImports) aufrufen. 
	 * 
	 * </p>
	 */
	public static Logger getLogger(Class<?extends Object> clazz){
		return Logger.getLogger(clazz.getName());
	}
	
	/**
	 * Erlaubt es die Lgging-Grundeinstellungen zu setzen
	 * @param logLevelConsole LogLevel für die Konsole (oder null, falls auf der Konsole keine Log-Messages ausgeben werden sollen)
	 * @param logLevelFile LogLevel für die Log-Datei
	 * @param logFile Pfad zur Log-Datei
	 */
	public static void init(Level logLevelConsole, Level logLevelFile, String logFile){
		// java.util.logging.Logger verwendet einen SimpleFormatter, der für jeden Eintrag zwei Zeilen verschwendet
		Logger rootLogger = Logger.getLogger("");
		
		for (int i = 0; i < rootLogger.getHandlers().length; i++) {
			Handler aHandler = rootLogger.getHandlers()[i];
			rootLogger.removeHandler(aHandler);
		}
		
		Level minLevel=null;
		if (logLevelConsole!=null){
			ConsoleHandler consoleHandler=new ConsoleHandler();
			consoleHandler.setFormatter(new SingleLineFormatter(false));
			consoleHandler.setLevel(logLevelConsole);
			rootLogger.addHandler(consoleHandler);
			minLevel=logLevelConsole;
		}
		if (logLevelFile!=null && logFile!=null){
			try {
				FileHandler fileHandler=new FileHandler(logFile);
				fileHandler.setFormatter(new SingleLineFormatter(true));
				fileHandler.setLevel(logLevelFile);		
				rootLogger.addHandler(fileHandler);
				if (minLevel==null || minLevel.intValue()>logLevelFile.intValue()){
					minLevel=logLevelFile;
				}
			} catch (Exception e) {
			}
		}		
		Logger.getLogger("de.fuberlin").setLevel(minLevel); // LogLevel für unsere Klassen setzen
		rootLogger.setLevel(Level.OFF); // Logging für Fremd-Bibliotheken deaktivieren.
	}
	
	/**
	 * Kopiert von: http://stackoverflow.com/questions/194765/how-do-i-get-java-logging-output-to-appear-on-a-single-line
	 */
	private static class SingleLineFormatter extends Formatter {
		
		Date dat = new Date();
		private String format = null;
		private MessageFormat formatter;
		private Object args[] = new Object[1];

		// Line separator string.  This is the value of the line.separator
		// property at the moment that the SimpleFormatter was created.
		//private String lineSeparator = (String) java.security.AccessController.doPrivileged(
		//        new sun.security.action.GetPropertyAction("line.separator"));
		private String lineSeparator = "\n";

		
		private SingleLineFormatter(boolean displayDate){
			format=displayDate?"{0,date} {0,time}":"{0,time}";
		}
		
		/**
		 * Format the given LogRecord.
		 * @param record the log record to be formatted.
		 * @return a formatted log record
		 */
		public synchronized String format(LogRecord record) {

			StringBuilder sb = new StringBuilder();

			// Minimize memory allocations here.
			dat.setTime(record.getMillis());    
			args[0] = dat;


			// Date and time 
			StringBuffer text = new StringBuffer();
			if (formatter == null) {
				formatter = new MessageFormat(format);
			}
			formatter.format(args, text, null);
			sb.append(text);
			sb.append(" ");


			// Class name 
			if (record.getSourceClassName() != null) {
				sb.append(record.getSourceClassName());
			} else {
				sb.append(record.getLoggerName());
			}

			// Method name 
			if (record.getSourceMethodName() != null) {
				sb.append(" ");
				sb.append(record.getSourceMethodName());
			}
			sb.append(" - "); // lineSeparator



			String message = formatMessage(record);

			// Level
			sb.append(record.getLevel().getName());
			sb.append(": ");

			// Indent - the more serious, the more indented.
			//sb.append( String.format("% ""s") );
			//		    int iOffset = (1000 - record.getLevel().intValue()) / 100;
			//		    for( int i = 0; i < iOffset;  i++ ){
			//		      sb.append(" ");
			//		    }


			sb.append(message);
			sb.append(lineSeparator);
			if (record.getThrown() != null) {
				try {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					record.getThrown().printStackTrace(pw);
					pw.close();
					sb.append(sw.toString());
				} catch (Exception ex) {
				}
			}
			return sb.toString();
		}
	}

}
