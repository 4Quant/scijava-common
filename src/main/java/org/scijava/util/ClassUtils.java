/*
 * #%L
 * SciJava Common shared library for SciJava software.
 * %%
 * Copyright (C) 2009 - 2014 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package org.scijava.util;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Useful methods for working with {@link Class} objects and primitive types.
 * 
 * @author Curtis Rueden
 */
public final class ClassUtils {

	private ClassUtils() {
		// prevent instantiation of utility class
	}

	// -- Class loading, querying and reflection --

	/**
	 * Loads the class with the given name, using the current thread's context
	 * class loader, or null if it cannot be loaded.
	 * 
	 * @see #loadClass(String, ClassLoader)
	 */
	public static Class<?> loadClass(final String className) {
		return loadClass(className, null);
	}

	/**
	 * Loads the class with the given name, using the specified
	 * {@link ClassLoader}, or null if it cannot be loaded.
	 * <p>
	 * This method is capable of parsing several different class name syntaxes.
	 * In particular, array classes (including primitives) represented using
	 * either square brackets or internal Java array name syntax are supported.
	 * Examples:
	 * </p>
	 * <ul>
	 * <li>{@code boolean} is loaded as {@code boolean.class}</li>
	 * <li>{@code Z} is loaded as {@code boolean.class}</li>
	 * <li>{@code double[]} is loaded as {@code double[].class}</li>
	 * <li>{@code string[]} is loaded as {@code java.lang.String.class}</li>
	 * <li>{@code [F} is loaded as {@code float[].class}</li>
	 * </ul>
	 * 
	 * @param name The name of the class to load.
	 * @param classLoader The class loader with which to load the class; if null,
	 *          the current thread's context class loader will be used.
	 */
	public static Class<?> loadClass(final String name,
		final ClassLoader classLoader)
	{
		// handle primitive types
		if (name.equals("Z") || name.equals("boolean")) return boolean.class;
		if (name.equals("B") || name.equals("byte")) return byte.class;
		if (name.equals("C") || name.equals("char")) return char.class;
		if (name.equals("D") || name.equals("double")) return double.class;
		if (name.equals("F") || name.equals("float")) return float.class;
		if (name.equals("I") || name.equals("int")) return int.class;
		if (name.equals("J") || name.equals("long")) return long.class;
		if (name.equals("S") || name.equals("short")) return short.class;
		if (name.equals("V") || name.equals("void")) return void.class;

		// handle built-in class shortcuts
		final String className;
		if (name.equals("string")) className = "java.lang.String";
		else className = name;

		// handle source style arrays (e.g.: "java.lang.String[]")
		if (name.endsWith("[]")) {
			final String elementClassName = name.substring(0, name.length() - 2);
			return getArrayClass(loadClass(elementClassName, classLoader));
		}

		// handle non-primitive internal arrays (e.g.: "[Ljava.lang.String;")
		if (name.startsWith("[L") && name.endsWith(";")) {
			final String elementClassName = name.substring(2, name.length() - 1);
			return getArrayClass(loadClass(elementClassName, classLoader));
		}

		// handle other internal arrays (e.g.: "[I", "[[I", "[[Ljava.lang.String;")
		if (name.startsWith("[")) {
			final String elementClassName = name.substring(1);
			return getArrayClass(loadClass(elementClassName, classLoader));
		}

		// load the class!
		try {
			final ClassLoader cl =
				classLoader == null ? Thread.currentThread().getContextClassLoader()
					: classLoader;
			return cl.loadClass(className);
		}
		catch (final ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * Gets the array class corresponding to the given element type.
	 * <p>
	 * For example, {@code getArrayClass(double.class)} returns
	 * {@code double[].class}.
	 * </p>
	 */
	public static Class<?> getArrayClass(final Class<?> elementClass) {
		if (elementClass == null) return null;
		// NB: It appears the reflection API has no built-in way to do this.
		// So unfortunately, we must allocate a new object and then inspect it.
		try {
			return Array.newInstance(elementClass, 0).getClass();
		}
		catch (final IllegalArgumentException exc) {
			return null;
		}
	}

	/** Checks whether a class with the given name exists. */
	public static boolean hasClass(final String className) {
		return hasClass(className, null);
	}

	/** Checks whether a class with the given name exists. */
	public static boolean hasClass(final String className,
		final ClassLoader classLoader)
	{
		return loadClass(className, classLoader) != null;
	}

	/**
	 * Gets the base location of the given class.
	 * <p>
	 * If the class is directly on the file system (e.g.,
	 * "/path/to/my/package/MyClass.class") then it will return the base directory
	 * (e.g., "/path/to").
	 * </p>
	 * <p>
	 * If the class is within a JAR file (e.g.,
	 * "/path/to/my-jar.jar!/my/package/MyClass.class") then it will return the
	 * path to the JAR (e.g., "/path/to/my-jar.jar").
	 * </p>
	 * 
	 * @param className The name of the class whose location is desired.
	 * @see FileUtils#urlToFile(URL) to convert the result to a {@link File}.
	 */
	public static URL getLocation(final String className) {
		return getLocation(className, null);
	}

	/**
	 * Gets the base location of the given class.
	 * <p>
	 * If the class is directly on the file system (e.g.,
	 * "/path/to/my/package/MyClass.class") then it will return the base directory
	 * (e.g., "/path/to").
	 * </p>
	 * <p>
	 * If the class is within a JAR file (e.g.,
	 * "/path/to/my-jar.jar!/my/package/MyClass.class") then it will return the
	 * path to the JAR (e.g., "/path/to/my-jar.jar").
	 * </p>
	 * 
	 * @param className The name of the class whose location is desired.
	 * @param classLoader The class loader to use when loading the class.
	 * @see FileUtils#urlToFile(URL) to convert the result to a {@link File}.
	 */
	public static URL getLocation(final String className,
		final ClassLoader classLoader)
	{
		final Class<?> c = loadClass(className, classLoader);
		return getLocation(c);
	}

	/**
	 * Gets the base location of the given class.
	 * <p>
	 * If the class is directly on the file system (e.g.,
	 * "/path/to/my/package/MyClass.class") then it will return the base directory
	 * (e.g., "file:/path/to").
	 * </p>
	 * <p>
	 * If the class is within a JAR file (e.g.,
	 * "/path/to/my-jar.jar!/my/package/MyClass.class") then it will return the
	 * path to the JAR (e.g., "file:/path/to/my-jar.jar").
	 * </p>
	 * 
	 * @param c The class whose location is desired.
	 * @see FileUtils#urlToFile(URL) to convert the result to a {@link File}.
	 */
	public static URL getLocation(final Class<?> c) {
		if (c == null) return null; // could not load the class

		// try the easy way first
		try {
			final URL codeSourceLocation =
				c.getProtectionDomain().getCodeSource().getLocation();
			if (codeSourceLocation != null) return codeSourceLocation;
		}
		catch (final SecurityException e) {
			// NB: Cannot access protection domain.
		}
		catch (final NullPointerException e) {
			// NB: Protection domain or code source is null.
		}

		// NB: The easy way failed, so we try the hard way. We ask for the class
		// itself as a resource, then strip the class's path from the URL string,
		// leaving the base path.

		// get the class's raw resource path
		final URL classResource = c.getResource(c.getSimpleName() + ".class");
		if (classResource == null) return null; // cannot find class resource

		final String url = classResource.toString();
		final String suffix = c.getCanonicalName().replace('.', '/') + ".class";
		if (!url.endsWith(suffix)) return null; // weird URL

		// strip the class's path from the URL string
		final String base = url.substring(0, url.length() - suffix.length());

		String path = base;

		// remove the "jar:" prefix and "!/" suffix, if present
		if (path.startsWith("jar:")) path = path.substring(4, path.length() - 2);

		try {
			return new URL(path);
		}
		catch (final MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets the given class's {@link Method}s marked with the annotation of the
	 * specified class.
	 * <p>
	 * Unlike {@link Class#getMethods()}, the result will include any non-public
	 * methods, including methods defined in supertypes of the given class.
	 * </p>
	 *
	 * @param c The class to scan for annotated methods.
	 * @param annotationClass The type of annotation for which to scan.
	 * @return A new list containing all methods with the requested annotation.
	 */
	public static <A extends Annotation> List<Method> getAnnotatedMethods(
		final Class<?> c, final Class<A> annotationClass)
	{
		final ArrayList<Method> methods = new ArrayList<Method>();
		getAnnotatedMethods(c, annotationClass, methods);
		return methods;
	}

	/**
	 * Gets the given class's {@link Method}s marked with the annotation of the
	 * specified class.
	 * <p>
	 * Unlike {@link Class#getMethods()}, the result will include any non-public
	 * methods, including methods defined in supertypes of the given class.
	 * </p>
	 *
	 * @param c The class to scan for annotated methods.
	 * @param annotationClass The type of annotation for which to scan.
	 * @param methods The list to which matching methods will be added.
	 */
	public static <A extends Annotation> void
		getAnnotatedMethods(final Class<?> c, final Class<A> annotationClass,
			final List<Method> methods)
	{
		// NB: The java.lang.Object class does not have any annotated methods.
		// And even if it did, it definitely does not have any methods annotated
		// with SciJava annotations such as org.scijava.event.EventHandler, which
		// are the main sorts of methods we are interested in.
		if (c == null || c == Object.class) return;

		// check supertypes for annotated methods first
		getAnnotatedMethods(c.getSuperclass(), annotationClass, methods);
		// NB: In some cases, we may not need to recursively scan interfaces.
		// In particular, for the @EventHandler annotation, we only care about
		// concrete methods, not interface method declarations. So we could have
		// additional method signatures with a boolean toggle indicating whether
		// to include interfaces in the recursive scan. But initial benchmarks
		// suggest that the performance difference, even when creating a
		// full-blown Context with a large classpath, is negligible.
		for (final Class<?> iface : c.getInterfaces()) {
			getAnnotatedMethods(iface, annotationClass, methods);
		}

		for (final Method m : c.getDeclaredMethods()) {
			final A ann = m.getAnnotation(annotationClass);
			if (ann != null) methods.add(m);
		}
	}

	/**
	 * Gets the given class's {@link Field}s marked with the annotation of the
	 * specified class.
	 * <p>
	 * Unlike {@link Class#getFields()}, the result will include any non-public
	 * fields, including fields defined in supertypes of the given class.
	 * </p>
	 * 
	 * @param c The class to scan for annotated fields.
	 * @param annotationClass The type of annotation for which to scan.
	 * @return A new list containing all fields with the requested annotation.
	 */
	public static <A extends Annotation> List<Field> getAnnotatedFields(
		final Class<?> c, final Class<A> annotationClass)
	{
		final ArrayList<Field> fields = new ArrayList<Field>();
		getAnnotatedFields(c, annotationClass, fields);
		return fields;
	}

	/**
	 * Gets the given class's {@link Field}s marked with the annotation of the
	 * specified class.
	 * <p>
	 * Unlike {@link Class#getFields()}, the result will include any non-public
	 * fields, including fields defined in supertypes of the given class.
	 * </p>
	 * 
	 * @param c The class to scan for annotated fields.
	 * @param annotationClass The type of annotation for which to scan.
	 * @param fields The list to which matching fields will be added.
	 */
	public static <A extends Annotation> void getAnnotatedFields(
		final Class<?> c, final Class<A> annotationClass, final List<Field> fields)
	{
		// NB: The java.lang.Object class does not have any annotated fields.
		// And even if it did, it definitely does not have any fields annotated
		// with SciJava annotations such as org.scijava.plugin.Parameter, which
		// are the main sorts of fields we are interested in.
		if (c == null || c == Object.class) return;

		// check supertypes for annotated fields first
		getAnnotatedFields(c.getSuperclass(), annotationClass, fields);
		for (final Class<?> iface : c.getInterfaces()) {
			getAnnotatedFields(iface, annotationClass, fields);
		}

		for (final Field f : c.getDeclaredFields()) {
			final A ann = f.getAnnotation(annotationClass);
			if (ann != null) fields.add(f);
		}
	}

	/**
	 * Gets the specified field of the given class, or null if it does not exist.
	 */
	public static Field getField(final String className, final String fieldName) {
		return getField(loadClass(className), fieldName);
	}

	/**
	 * Gets the specified field of the given class, or null if it does not exist.
	 */
	public static Field getField(final Class<?> c, final String fieldName) {
		if (c == null) return null;
		try {
			return c.getDeclaredField(fieldName);
		}
		catch (final NoSuchFieldException e) {
			return null;
		}
	}

	/**
	 * Gets the given field's value of the specified object instance, or null if
	 * the value cannot be obtained.
	 */
	public static Object getValue(final Field field, final Object instance) {
		try {
			field.setAccessible(true);
			return field.get(instance);
		}
		catch (final IllegalAccessException e) {
			return null;
		}
	}

	/**
	 * Sets the given field's value of the specified object instance.
	 * 
	 * @throws IllegalArgumentException if the value cannot be set.
	 */
	// FIXME: Move to ConvertService and deprecate this signature.
	public static void setValue(final Field field, final Object instance,
		final Object value)
	{
		try {
			field.setAccessible(true);
			final Object compatibleValue;
			if (value == null || field.getType().isInstance(value)) {
				// the given value is compatible with the field
				compatibleValue = value;
			}
			else {
				// the given value needs to be converted to a compatible type
				final Type fieldType =
						GenericUtils.getFieldType(field, instance.getClass());
				compatibleValue = ConversionUtils.convert(value, fieldType);
			}
			field.set(instance, compatibleValue);
		}
		catch (final IllegalAccessException e) {
			throw new IllegalArgumentException("No access to field: " +
				field.getName(), e);
		}
	}

	// -- Type querying --

	public static boolean isBoolean(final Class<?> type) {
		return type == boolean.class || Boolean.class.isAssignableFrom(type);
	}

	public static boolean isByte(final Class<?> type) {
		return type == byte.class || Byte.class.isAssignableFrom(type);
	}

	public static boolean isCharacter(final Class<?> type) {
		return type == char.class || Character.class.isAssignableFrom(type);
	}

	public static boolean isDouble(final Class<?> type) {
		return type == double.class || Double.class.isAssignableFrom(type);
	}

	public static boolean isFloat(final Class<?> type) {
		return type == float.class || Float.class.isAssignableFrom(type);
	}

	public static boolean isInteger(final Class<?> type) {
		return type == int.class || Integer.class.isAssignableFrom(type);
	}

	public static boolean isLong(final Class<?> type) {
		return type == long.class || Long.class.isAssignableFrom(type);
	}

	public static boolean isShort(final Class<?> type) {
		return type == short.class || Short.class.isAssignableFrom(type);
	}

	public static boolean isNumber(final Class<?> type) {
		return Number.class.isAssignableFrom(type) || type == byte.class ||
			type == double.class || type == float.class || type == int.class ||
			type == long.class || type == short.class;
	}

	public static boolean isText(final Class<?> type) {
		return String.class.isAssignableFrom(type) || isCharacter(type);
	}

	// -- Comparison --

	/**
	 * Compares two {@link Class} objects using their fully qualified names.
	 * <p>
	 * Note: this method provides a natural ordering that may be inconsistent with
	 * equals. Specifically, two unequal classes may return 0 when compared in
	 * this fashion if they represent the same class loaded using two different
	 * {@link ClassLoader}s. Hence, if this method is used as a basis for
	 * implementing {@link Comparable#compareTo} or
	 * {@link java.util.Comparator#compare}, that implementation may want to
	 * impose logic beyond that of this method, for breaking ties, if a total
	 * ordering consistent with equals is always required.
	 * </p>
	 * 
	 * @see org.scijava.Priority#compare(org.scijava.Prioritized,
	 *      org.scijava.Prioritized)
	 */
	public static int compare(final Class<?> c1, final Class<?> c2) {
		if (c1 == c2) return 0;
		final String name1 = c1 == null ? null : c1.getName();
		final String name2 = c2 == null ? null : c2.getName();
		return MiscUtils.compare(name1, name2);
	}

	// -- Deprecated methods --

	/** @deprecated use {@link ConversionUtils#convert(Object, Class)} */
	@Deprecated
	public static <T> T convert(final Object value, final Class<T> type) {
		return ConversionUtils.convert(value, type);
	}

	/** @deprecated use {@link ConversionUtils#canConvert(Class, Class)} */
	@Deprecated
	public static boolean canConvert(final Class<?> c, final Class<?> type) {
		return ConversionUtils.canConvert(c, type);
	}

	/** @deprecated use {@link ConversionUtils#canConvert(Object, Class)} */
	@Deprecated
	public static boolean canConvert(final Object value, final Class<?> type) {
		return ConversionUtils.canConvert(value, type);
	}

	/** @deprecated use {@link ConversionUtils#cast(Object, Class)} */
	@Deprecated
	public static <T> T cast(final Object obj, final Class<T> type) {
		return ConversionUtils.cast(obj, type);
	}

	/** @deprecated use {@link ConversionUtils#canCast(Class, Class)} */
	@Deprecated
	public static boolean canCast(final Class<?> c, final Class<?> type) {
		return ConversionUtils.canCast(c, type);
	}

	/** @deprecated use {@link ConversionUtils#canCast(Object, Class)} */
	@Deprecated
	public static boolean canCast(final Object obj, final Class<?> type) {
		return ConversionUtils.canCast(obj, type);
	}

	/** @deprecated use {@link ConversionUtils#getNonprimitiveType(Class)} */
	@Deprecated
	public static <T> Class<T> getNonprimitiveType(final Class<T> type) {
		return ConversionUtils.getNonprimitiveType(type);
	}

	/** @deprecated use {@link ConversionUtils#getNullValue(Class)} */
	@Deprecated
	public static <T> T getNullValue(final Class<T> type) {
		return ConversionUtils.getNullValue(type);
	}

	/** @deprecated use {@link GenericUtils#getFieldClasses(Field, Class)} */
	@Deprecated
	public static List<Class<?>> getTypes(final Field field, final Class<?> type)
	{
		return GenericUtils.getFieldClasses(field, type);
	}

	/** @deprecated use {@link GenericUtils#getFieldType(Field, Class)} */
	@Deprecated
	public static Type getGenericType(final Field field, final Class<?> type) {
		return GenericUtils.getFieldType(field, type);
	}

}
