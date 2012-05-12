package regextodfaconverter.directconverter.lr0parser.itemset;

import java.util.HashMap;
import java.util.List;


/**
 * Bildet die Items auf ihre Eigenschaft Kernelitem (true) oder Nichtkernelitem /false) ab.
 * 
 * @author Johannes Dahlke
 *
 */
public class Closure extends HashMap<Item, Boolean> {
	
	public boolean isKernelItem( Item item) {
		return this.get( item);
	}
	
	public boolean addAsKernelItem( Item item) {
		return this.put( item, true) == null;
	}
	
	public boolean addAsNonkernelItem( Item item) {
		return this.put( item, false) == null;
	}
	
	public boolean putAsKernelItem( Item item) {
		return this.put( item, true);
	}
	
	public boolean putAsNonkernelItem( Item item) {
		return this.put( item, false);
	}
	

}
