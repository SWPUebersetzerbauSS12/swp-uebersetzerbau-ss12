package de.fuberlin.commons.util;

import java.lang.reflect.Array;

/**
 * UtilityClass zum einfachen Implementieren von equals und hashCode.
 * Ableitende Klassen implementieren die Methode getSignificantFields auf deren Basis dann equals und hashCode berechnet werden.
 * Aus Performance-Gründen wird der Hashcode nur einmal berechnet und dann gecacht. 
 * Die ableitende Klasse ist dafür verantwortlich super.resetHashCode aufzurufen, wenn sich eines der ignificantFields ändert!!!
 *  
 * 
 * Idee entnommen aus den beiden Beispielen auf http://www.javapractices.com/topic/TopicAction.do?Id=28
 *
 */
public abstract class EasyComparableObject {

	private int fHashCode;

	/**
	 * Definiert die Felder die für die Berechnung von equals und hashCode berücksichtigt werden sollen.
	 * Z.B.: return new Object[]{field1, field2}; , wenn zwei Objekte als gleich gelten sollen, wenn field1 und field2 gleich sind.
	 * Falls zusätzlich noch der Typ berücksichtigt werden soll: return new Object[]{field1, field2, getClass()};
	 */
	protected abstract Object[] getSignificantFields();

	/**
	 * Setzt den gecachten Hashcode zurück, so dass dieser beim nächsten Aufruf von hashCode erneut berechnet wird.
	 * Diese Methode MUSS von der ableitenden Klasse aufgerufen werden, sobald sich eines der Felder aus getSignificantFields geändert hat.
	 */
	protected void resetHashCode(){
		fHashCode=0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj==null || !(obj instanceof EasyComparableObject)){
			return false;
		}
		EasyComparableObject other=(EasyComparableObject) obj;
		
		Object[] mySignificantFields=getSignificantFields();
		Object[] otherSignificantFields=other.getSignificantFields();
		
		if (mySignificantFields.length!=otherSignificantFields.length){
			return false;
		}
		
		for (int i = 0; i < mySignificantFields.length; i++) {
			Object aField = mySignificantFields[i];
			Object anotherField=otherSignificantFields[i];
			if (aField==null && anotherField!=null){
				return false;				
			}
			if (aField!=null && anotherField==null){
				return false;				
			}
			if (aField!=null && anotherField!=null){
				boolean equal=aField.equals(anotherField);
				if (!equal){
					return false;
				}
			}
		}
		return true;
	}
	
	
	@Override
	public int hashCode() {
		if ( fHashCode == 0) {
			int result = HashCodeUtil.SEED;			
			for (int i = 0; i < getSignificantFields().length; i++) {
				Object aField = getSignificantFields()[i];
				result = HashCodeUtil.hash( result, aField );
			}				
			fHashCode = result;
		}
		return fHashCode;
	}
	
	
	private static final class HashCodeUtil {

		  /**
		  * An initial value for a <code>hashCode</code>, to which is added contributions
		  * from fields. Using a non-zero value decreases collisons of <code>hashCode</code>
		  * values.
		  */
		  public static final int SEED = 23;

		  /**
		  * booleans.
		  */
		  public static int hash( int aSeed, boolean aBoolean ) {
//		    System.out.println("boolean...");
		    return firstTerm( aSeed ) + ( aBoolean ? 1 : 0 );
		  }

		  /**
		  * chars.
		  */
		  public static int hash( int aSeed, char aChar ) {
//		    System.out.println("char...");
		    return firstTerm( aSeed ) + (int)aChar;
		  }

		  /**
		  * ints.
		  */
		  public static int hash( int aSeed , int aInt ) {
		    /*
		    * Implementation Note
		    * Note that byte and short are handled by this method, through
		    * implicit conversion.
		    */
//		    System.out.println("int...");
		    return firstTerm( aSeed ) + aInt;
		  }

		  /**
		  * longs.
		  */
		  public static int hash( int aSeed , long aLong ) {
//		    System.out.println("long...");
		    return firstTerm(aSeed)  + (int)( aLong ^ (aLong >>> 32) );
		  }

		  /**
		  * floats.
		  */
		  public static int hash( int aSeed , float aFloat ) {
		    return hash( aSeed, Float.floatToIntBits(aFloat) );
		  }

		  /**
		  * doubles.
		  */
		  public static int hash( int aSeed , double aDouble ) {
		    return hash( aSeed, Double.doubleToLongBits(aDouble) );
		  }

		  /**
		  * <code>aObject</code> is a possibly-null object field, and possibly an array.
		  *
		  * If <code>aObject</code> is an array, then each element may be a primitive
		  * or a possibly-null object.
		  */
		  public static int hash( int aSeed , Object aObject ) {
		    int result = aSeed;
		    if ( aObject == null) {
		      result = hash(result, 0);
		    }
		    else if ( ! isArray(aObject) ) {
		      result = hash(result, aObject.hashCode());
		    }
		    else {
		      int length = Array.getLength(aObject);
		      for ( int idx = 0; idx < length; ++idx ) {
		        Object item = Array.get(aObject, idx);
		        //recursive call!
		        result = hash(result, item);
		      }
		    }
		    return result;
		  }


		  /// PRIVATE ///
		  private static final int fODD_PRIME_NUMBER = 37;

		  private static int firstTerm( int aSeed ){
		    return fODD_PRIME_NUMBER * aSeed;
		  }

		  private static boolean isArray(Object aObject){
		    return aObject.getClass().isArray();
		  }
		} 
}
