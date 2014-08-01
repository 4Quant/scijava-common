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

package org.scijava.convert;

import org.scijava.plugin.AbstractHandlerPlugin;

/**
 * Abstract superclass for {@link Converter} plugins. Performs
 * appropriate dispatching of {@link #canConvert(ConversionRequest)} and
 * {@link #convert(ConversionRequest)} calls based on the actual state of the
 * given {@link ConversionRequest}.
 * <p>
 * Note that the {@link #supports(ConversionRequest)} method is overridden as
 * well, to delegate to the appropriate {@link #canConvert}.
 * </p>
 *
 * @author Mark Hiner
 */
public abstract class AbstractConverter extends
	AbstractHandlerPlugin<ConversionRequest> implements Converter
{

	// -- ConversionHandler methods --

	@Override
	public boolean canConvert(final ConversionRequest request) {
		final Class<?> src = request.sourceClass();
		if (src == null) return true;

		if (request.destClass() != null) return canConvert(src, request.destClass());
		if (request.destType() != null) return canConvert(src, request.destType());

		return false;
	}

	@Override
	public Object convert(final ConversionRequest request) {
		if (request.sourceObject() != null) {
			if (request.destClass() != null) return convert(request.sourceObject(),
				request.destClass());

			if (request.destType() != null) return convert(request.sourceObject(),
				request.destType());
		}
		return null;
	}

	// -- Typed methods --

	@Override
	public boolean supports(final ConversionRequest request) {
		return canConvert(request);
	}

	@Override
	public Class<ConversionRequest> getType() {
		return ConversionRequest.class;
	}
}