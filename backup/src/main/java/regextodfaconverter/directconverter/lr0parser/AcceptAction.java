package regextodfaconverter.directconverter.lr0parser;


public class AcceptAction<Element extends Comparable<Element>> extends Action<Element> implements EventHandler {

	public AcceptAction(ItemAutomataInterior<Element> itemAutomata) {
		super( itemAutomata);
	}
	
	public Object handle(Object sender) throws Exception {
		return true;
	}
	
	@Override
	public String toString() {
		return "Accept";
	}

}
