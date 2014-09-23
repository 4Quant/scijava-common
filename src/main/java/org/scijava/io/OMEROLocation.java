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

import org.scijava.plugin.Plugin;

/**
 * {@link Location} pointing at an OMERO server.
 *
 * @author Curtis Rueden
 */
@Plugin(type = Location.class)
public class OMEROLocation extends AbstractLocation {

	// -- OMEROLocation methods --

	public String getServer() {
		return getURI().getHost();
	}

	public int getPort() {
		return getURI().getPort();
	}

	public String getSessionID() {
		return sessionID;
	}

	public String getUser() {
		return getURI().getUserInfo();
	}

	public String getPassword() {
		return password;
	}

	public boolean isEncrypted() {
		return encrypted;
	}

	public void setServer(final String server) {
		this.server = server;
	}

	public void setPort(final int port) {
		this.port = port;
	}

	public void setSessionID(final String sessionID) {
		this.sessionID = sessionID;
		if (sessionID != null) {
			// NB: Drop username & password from memory when we have a session ID.
			user = password = null;
		}
	}

	public void setUser(final String user) {
		this.user = user;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public void setEncrypted(final boolean encrypted) {
		this.encrypted = encrypted;
	}


	// -- Location methods --

	@Override
	public String getPath() {
		return uri.toString();
	}

	// -- WrapperPlugin methods --

	@Override
	public void set(final String data) {
		try {
			uri = new URI(data);
		}
		catch (URISyntaxException exc) {
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

}
