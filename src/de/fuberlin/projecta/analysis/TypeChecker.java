package de.fuberlin.projecta.analysis;

import de.fuberlin.projecta.analysis.ast.nodes.Type;

public class TypeChecker {

	public static boolean isNumeric(String type) {
		return type.equals(Type.TYPE_INT_STRING) || type.equals(Type.TYPE_REAL_STRING);
	}

	public static boolean isBoolean(String type) {
		return type.equals(Type.TYPE_BOOL_STRING);
	}

	public static boolean isString(String type) {
		return type.equals(Type.TYPE_STRING_STRING);
	}

}
