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

package org.scijava.script;

import org.scijava.util.LastRecentlyUsed;
import org.scijava.util.Prefs;

/**
 * Container for a script language's interpreter history.
 * 
 * @author Johannes Schindelin
 */
class History {

	protected static final long serialVersionUID = 1L;

	private static final String PREFIX = History.class.getClass().getName() + ".";
	private final int MAX_ENTRIES = 1000;

	private final String name;
	private final LastRecentlyUsed<String> entries = new LastRecentlyUsed<String>(MAX_ENTRIES);
	private int position = -1;

	/**
	 * Constructs a history object for a given scripting language.
	 * 
	 * @param name the name of the scripting language
	 */
	public History(final String name) {
		this.name = name;
	}

	/**
	 * Read back a persisted history.
	 */
	public void read() {
		entries.clear();
		for (final String item : Prefs.getIterable(PREFIX + name)) {
			entries.addToEnd(item);
		};
	}

	/**
	 * Persist the history.
	 * 
	 * @see {@link Prefs}
	 */
	public void write() {
		Prefs.putIterable(entries, PREFIX + name);
	}

	/**
	 * Adds the most recently issued command.
	 * 
	 * @param command the most recent command to add to the history
	 */
	public void add(final String command) {
		entries.add(command);
		position = -1;
	}

	public boolean replace(final String currentCommand) {
		if (position < 0) return false;
		return entries.replace(position, currentCommand);
	}

	/**
	 * Navigates to the next (more recent) command.
	 * <p>
	 * This method wraps around, i.e. it returns {@code null} when there is no
	 * more-recent command in the history.
	 * </p>
	 * 
	 * @return the next command
	 */
	public String next() {
		position = entries.next(position);
		return position < 0 ? null : entries.get(position);
	}

	/**
	 * Navigates to the previous (i.e less recent) command.
	 * <p>
	 * This method wraps around, i.e. it returns {@code null} when there is no
	 * less-recent command in the history.
	 * </p>
	 * 
	 * @return the previous command
	 */
	public String previous() {
		position = entries.previous(position);
		return position < 0 ? null : entries.get(position);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		int position = -1;
		for (;;) {
			position = entries.previous(position);
			if (position < 0) break;
			if (builder.length() > 0) builder.append(" -> ");
			if (this.position == position) builder.append("[");
			builder.append(entries.get(position));
			if (this.position == position) builder.append("]");
		}
		return builder.toString();
	}
}
