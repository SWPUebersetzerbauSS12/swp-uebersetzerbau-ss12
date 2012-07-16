package de.fuberlin.projecta.utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * Python-like list comprehension for Java
 * 
 * @see http
 *      ://stackoverflow.com/questions/899138/python-like-list-comprehension
 *      -in-java
 */
public class ListComprehension {

	public interface Func<In, Out> {
		public Out apply(In in);
	}

	public static <T> void applyToListInPlace(List<T> list, Func<T, T> f) {
		ListIterator<T> itr = list.listIterator();
		while (itr.hasNext()) {
			T output = f.apply(itr.next());
			itr.set(output);
		}
	}

	public static <In, Out> List<Out> map(List<In> in, Func<In, Out> f) {
		List<Out> out = new ArrayList<Out>(in.size());
		for (In inObj : in) {
			out.add(f.apply(inObj));
		}
		return out;
	}

	public static <In, Out> Set<Out> map(Set<In> in, Func<In, Out> f) {
		Set<Out> out = new LinkedHashSet<Out>(in.size());
		for (In inObj : in) {
			out.add(f.apply(inObj));
		}
		return out;
	}
}
