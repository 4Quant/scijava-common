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

import java.net.URLDecoder;
import java.nio.ByteBuffer;

import org.scijava.plugin.Plugin;

/**
 * {@link Location} backed by a {@link ByteBuffer}.
 * 
 * @author Curtis Rueden
 */
@Plugin(type = Location.class)
public class BytesLocation extends AbstractLocation {

	private ByteBuffer bytes;

	// -- BytesLocation methods --

	/** Gets the associated {@link ByteBuffer}. */
	public ByteBuffer getByteBuffer() {
		return bytes;
	}

	// -- Location methods --

	@Override
	public String getPath() {
		return "bytes:" + bytes.position();
	}

	// -- WrapperPlugin methods --

	@Override
	public void set(final String data) {
		// TODO: support a "bytes:capacity=1234&direct=true" notation.
		// This is similar to OMERO and Fake. Let's make a common superclass!
		bytes.allocate(capacity);
		URLDecoder
		throw new UnsupportedOperationException();
	}

	// -- Typed methods --

	@Override
	public boolean supports(final String data) {
		return data != null && data.startsWith("bytes:");
	}

}
