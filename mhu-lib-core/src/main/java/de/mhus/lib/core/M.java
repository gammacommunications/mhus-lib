package de.mhus.lib.core;

import java.util.function.Function;

import de.mhus.lib.core.pojo.MPojo;

/**
 * This is a shortcut class to call methods without
 * obfuscating the source code. For some reasons this 
 * makes sense.
 * 
 * @author mikehummel
 *
 */
public class M {

	/**
	 * Return a string cascading the names of the getters (without 'get' prefix).
	 * and joined with underscore.
	 * 
	 * This is used to create identifiers for MForm or Adb.
	 * 
	 * @param getters
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> String n(Function<T,?> ... getters ) {
		StringBuilder out = new StringBuilder();
		for (Function<T, ?> getter : getters) {
			if (out.length() > 0) out.append('_');
			out.append(MPojo.toAttributeName(getter));
		}
		return out.toString();
	}

	/**
	 * Truncate the string by length characters.
	 * 
	 * @param in String to truncate
	 * @param length Max length
	 * @return Same or truncated string
	 */
	public static String trunc(String in, int length) {
		return MString.truncate(in, length);
	}
	
	/**
	 * Cast to default type
	 * @param in
	 * @param def
	 * @return Integer
	 */
	public static int c(Object in, int def) {
		return MCast.toint(in, def);
	}
	
	public static long c(Object in, long def) {
		return MCast.tolong(in, def);
	}

	public static double c(Object in, double def) {
		return MCast.todouble(in, def);
	}

	public static boolean c(Object in, boolean def) {
		return MCast.toboolean(in, def);
	}
	
}
