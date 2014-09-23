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

package org.scijava.io;

import java.net.URI;
import java.net.URISyntaxException;

import org.scijava.plugin.AbstractWrapperPlugin;

/**
 * Abstract base class for {@link Location} plugins.
 *
 * @author Curtis Rueden
 */
public abstract class AbstractLocation extends AbstractWrapperPlugin<String>
	implements Location
{

	/** The URI backing this location, if any. */
	private URI uri;

	// -- Location methods --

	@Override
	public String getPath() {
		return uri == null ? null : uri.toString();
	}

	@Override
	public URI getURI() {
		return uri;
	}

	// -- WrapperPlugin methods --

	@Override
	public void set(final String data) {
		try {
			uri = new URI(data);
		}
		catch (final URISyntaxException exc) {
			throw new IllegalArgumentException(exc);
		}
	}

	// -- Typed methods --

	@Override
	public boolean supports(final String data) {
		try {
			new URI(data);
			return true;
		}
		catch (final URISyntaxException exc) {
			return false;
		}
	}

	@Override
	public Class<String> getType() {
		return String.class;
	}

}
