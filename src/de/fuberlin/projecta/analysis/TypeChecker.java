package de.fuberlin.projecta.analysis;

import de.fuberlin.projecta.analysis.ast.BasicType;

public class TypeChecker {

	public static boolean isNumeric(String type) {
		return type.equals(BasicType.TYPE_INT_STRING) || type.equals(BasicType.TYPE_REAL_STRING);
	}

	public static boolean isBoolean(String type) {
		return type.equals(BasicType.TYPE_BOOL_STRING);
	}

	public static boolean isString(String type) {
		return type.equals(BasicType.TYPE_STRING_STRING);
	}

}
