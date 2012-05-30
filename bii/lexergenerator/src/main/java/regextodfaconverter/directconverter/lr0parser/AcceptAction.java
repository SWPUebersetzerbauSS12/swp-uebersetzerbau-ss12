package regextodfaconverter.directconverter.lr0parser;


public class AcceptAction<Element> extends Action<Element> implements EventHandler {

	public AcceptAction(ItemAutomata<Element> itemAutomata) {
		super( itemAutomata);
	}
	
	public Object handle(Object sender) throws Exception {
		return true;
	}

}
