package regextodfaconverter.directconverter.lr0parser.itemset;

import java.util.HashMap;
import java.util.List;

import regextodfaconverter.directconverter.lr0parser.grammar.ProductionMap;
import regextodfaconverter.directconverter.lr0parser.grammar.ProductionRule;
import regextodfaconverter.directconverter.lr0parser.grammar.RuleElement;
import utils.Test;


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
	
	public ItemSet getItemSet() {
		ItemSet itemSet = new ItemSet();
		itemSet.addAll( this.keySet());	
		return itemSet;
	}
	

	public ProductionMap toProductionMap() {
		ProductionMap result = new ProductionMap();
		for ( Item item : this.keySet()) {
			result.addProduction( item);
		}
		return result;
	}
	
	@Override
	public boolean equals( Object theOtherObject) {
		
		if ( Test.isUnassigned( theOtherObject))
			return false;
		
		if ( !( theOtherObject instanceof Closure))
			return false;
		
		
		Closure theOtherClosure = (Closure) theOtherObject;
		
		if ( !theOtherClosure.entrySet().equals( this.entrySet()))
			return false;
				
		return true;
	}
	

}
