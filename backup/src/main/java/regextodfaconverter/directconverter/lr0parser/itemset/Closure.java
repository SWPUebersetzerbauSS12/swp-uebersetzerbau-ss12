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
	
	private int number = -1;
	
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
	
	@Override
	public boolean containsKey(Object key) {
		if ( key == null)
			return false;
		if ( !( key instanceof Item))
			return false;
		for (Item item : this.keySet()) {
			if (item.equals( (Item) key))
			  return true;
		}
		return false;
	}
	
	public ProductionMap toProductionMap() {
		ProductionMap result = new ProductionMap();
		for ( Item item : this.keySet()) {
			result.addProduction( item.toProduction());
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
		
		if ( theOtherClosure.size() != this.size())
			return false;
		
		if ( !theOtherClosure.keySet().equals( this.keySet()))
			return false;
		
		for ( Item thisItem : this.keySet()) {
			for ( Item theOtherItem : theOtherClosure.keySet()) {
				if ( thisItem.equals(theOtherItem) && 
					 !this.get( thisItem).equals( theOtherClosure.get( theOtherItem)))			
				return false;
		    }
		}
		
		// theOtherClosure equals this 
		if ( this.number == -1)
			this.number = theOtherClosure.number; 
		else if ( theOtherClosure.number == -1)
			theOtherClosure.number = this.number;
			
		
		return true;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	
	public Integer getNumber() {
		return number;
	}
	
	public String getName() {
		return "I" + number;
	}
	
	@Override
	public String toString() {
		return number > -1 ? getName() : toItemsString();
	}

	public String toItemsString() {
		return number > -1 ? getName() + ": " + super.toString() : super.toString();
	}
	
}
