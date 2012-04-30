

package lexergen;

import regextodfaconverter.ConvertExecption;
import utils.Notification;



/**
 * Hello world!
 * 
 */
public class Lexer {




	public static void main( String[] args) {

		Notification.enableDebugPrinting();

		try {
			Test.runTest();
		} catch ( ConvertExecption e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
