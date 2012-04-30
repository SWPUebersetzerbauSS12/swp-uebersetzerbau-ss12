package dfaprovider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lexergen.Settings;

import regextodfaconverter.MinimalDfa;
import regextodfaconverter.fsm.StatePayload;

/**
 * Stellt einen rudimänteren DFA-Provider dar.
 * @author Maximilian Schröder
 *
 */
public class MinimalDfaProvider {
	
	/**
	 * Header aus <dateiname>.dfa, Zeile 1
	 * Sollte der Konstanten "lexergen" entsprechen
	 */
	private static String head_1 = null;
	
	/**
	 * Header aus <dateiname>.dfa, Zeile 2
	 * Sollte der variablen Versionsnummer entsprechen
	 */
	private static String head_2 = null;
	
	/**
	 * Header aus <dateiname>.dfa, Zeile 3
	 * Sollte dem sha1-Hash, der erzeugten Datei, entsprechen
	 */
	private static String head_3 = null;
	
	/**
	 * Gibt den minimalen DFA für die angegebenen regulären Definitionen zurück.
	 * @param file Der absolute Pfad zu der Datei, die die regulären Definitionen enthalten.
	 * @return Den minimalen DFA für die angegebenen regulären Definitionen.
	 */
	public static MinimalDfa<Character,StatePayload> getMinimalDfa(String rdFileName) throws MinimalDfaProviderException
	{
		if(!rdFileName.endsWith(".rd")){
			throw new MinimalDfaProviderException("Die angegebene Datei " + rdFileName + " hat nicht das Format <dateiname>.rd");
		}
		File rdFile = new File(Settings.getWorkingDirectory() + rdFileName);
		if(!rdFile.exists()){
			throw new MinimalDfaProviderException("Die angegebene reguläre Definitionsdatei " + rdFileName + " existiert nicht");
		}
		MinimalDfa<Character,StatePayload> mDfa = null;
		
		/* Workflow: dfa provider */
		// dfa-Datei holen für die Überprüfung (auf Basis der angegebenen rd-Datei)
		int fileEndingIndex = rdFileName.lastIndexOf(".rd");
		String dfaFileName = rdFileName.substring(0, fileEndingIndex+1) + "dfa";
		File dfaToLoad = new File(Settings.getWorkingDirectory() + dfaFileName );
		
		//existiert <dateiname>.dfa
		if(dfaToLoad.exists()){
			//temporäre Headerfiles zurücksetzen
			resetHead();
			//deserialisieren des dfa, ausgehend von der existierenden Datei
			mDfa = deserialize( Settings.getWorkingDirectory() + dfaFileName );
			//header1 korrekt
			if(head_1.equals("lexergen")){
				//header2 korrekt
				if(head_2.equals(Settings.getVersion())){
					//sha von reguläre Definitionsdatei bilden und mit header3 vergleichen
					String currentSHA = getHashedRDFileAsString(rdFileName);
					//header3 korrekt
					if(head_3.equals(currentSHA)){
						//header korrekt abgearbeitet, nutze ausgelesenen dfa
						return mDfa;
					}
					else{
						//<dateiname>.dfa löschen, da sha1-hash-Konflikt
						dfaToLoad.delete();
						//FIXME: TEST-DFA anlegen
						return provideDummyDfa(rdFileName);
					}
				}
				else{
					//<dateiname>.dfa löschen, da Versionskonflikt
					dfaToLoad.delete();
					//FIXME: TEST-DFA anlegen
					return provideDummyDfa(rdFileName);
				}
			}
			else{
				//FIXME: TEST-DFA anlegen
				return provideDummyDfa(rdFileName);
			}
		}
		else{
			//FIXME: TEST-DFA anlegen
			return provideDummyDfa(rdFileName);
		}
	}
	
	
	private static void serialize(String outputFileName, String rdFileName, MinimalDfa<Character, StatePayload> dfaToSerialize){
	    try{
	      FileOutputStream file = new FileOutputStream( outputFileName );
	      ObjectOutputStream oOS = new ObjectOutputStream( file );
	      
	      oOS.writeObject("lexergen");				//header1
	      oOS.writeObject(Settings.getVersion());	//header2
	      
	      String shaString = getHashedRDFileAsString(rdFileName);
	      oOS.writeObject(shaString);				//header3	
	      //minimal dfa, der serialisiert werden soll
	      oOS.writeObject( dfaToSerialize );
	      oOS.close();
	    }
	    catch ( IOException e ){
	    	e.printStackTrace();
    	}
	    System.out.println("Serialization finished");
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	static private MinimalDfa<Character, StatePayload> deserialize( String filename ){
		MinimalDfa<Character, StatePayload> deserializedMDfa = null;
		try{
			FileInputStream file = new FileInputStream( filename );
		    ObjectInputStream o = new ObjectInputStream( file );

		    //Header-Daten abarbeiten
		    String head_1 = (String) o.readObject();	//lexergen
		    String head_2 = (String) o.readObject();	//0.1 (z.B.)
		    String head_3 = (String) o.readObject();	//sha1-hash von <dateiname>.rd
		    
		    //Automaten erhalten
		    deserializedMDfa = (MinimalDfa<Character, StatePayload>) o.readObject();
		     
		    o.close();

		}
		catch ( IOException e ) { System.err.println( e ); }
		catch ( ClassNotFoundException e ) { System.err.println( e ); }
		if(deserializedMDfa != null){
			System.out.println("Deserialization finished");
		}
		else{
			System.out.println("Deserialization failed");
		}
		return deserializedMDfa;
	}
	
	/**
	 * Setzt die temporären head-Variablen zurück
	 * 
	 * etwas unschön, aber pragmatisch
	 */
	private static void resetHead(){
		head_1 = null;
		head_2 = null;
		head_3 = null;
	}
	
	public static String getHashedRDFileAsString(String fileName){
		String shaToString = "";
		try {
			//Algo festlegen und Datei lesen
			MessageDigest md = MessageDigest.getInstance("SHA");
			FileInputStream fis = new FileInputStream(fileName);
			byte[] fileBytes = new byte[5120];
			int readbytes = 0; 
			  
			while ((readbytes = fis.read(fileBytes)) != -1) {
				md.update(fileBytes, 0, readbytes);
			};
			 
			byte[] digest = md.digest();
			  
			//Umwandlung von Byte in Hexadezimalformat
			StringBuffer sb = new StringBuffer("");
			for (int i = 0; i < digest.length; i++) {
				sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
			}
			 
			shaToString = sb.toString();
			
		} catch (NoSuchAlgorithmException e) {
			  e.printStackTrace();
		} catch (FileNotFoundException e) {
			  e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return shaToString;
	}
	
	private static MinimalDfa<Character,StatePayload> provideDummyDfa(String reDeFi){
		MinimalDfa<Character,StatePayload> mDummyDfa = null;
		File rdFile = new File(Settings.getWorkingDirectory() + reDeFi);
		
		int fileEndingIndex = reDeFi.lastIndexOf(".rd");
		String dfaFileName = reDeFi.substring(0, fileEndingIndex+1) + "dfa";
		
		try {
			MinimalDfaBuilder builder = new IndirectMinimalDfaBuilder();
			mDummyDfa = builder.buildMinimalDfa(rdFile);
		} catch (MinimalDfaBuilderException e) {
			e.printStackTrace();
		}
		File dfaToSave = new File(Settings.getWorkingDirectory() + dfaFileName );
		if(dfaToSave.exists()){
			return mDummyDfa;
		}
		else{
			serialize(Settings.getWorkingDirectory() + dfaFileName, 
					Settings.getWorkingDirectory() + Settings.getRegularDefinitionFileName(), mDummyDfa);
			return mDummyDfa;
		}
	}
	
	
}
