package utils;

public class Test {

	public static boolean isAssigned( Object o) {
		return o != null;
	}


	public static boolean isUnassigned( Object o) {
		return o == null;
	}


	public static boolean isInteger( String s) {
		try {
			Integer.valueOf( s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean isNumber( String s) {
		try {
			Double.valueOf( s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isNan( String s) {
		return !isNumber(s);
	}
	

}
