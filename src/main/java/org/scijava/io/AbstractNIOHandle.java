
package org.scijava.io;

import java.io.IOException;

/**
 * Abstract base class for NIO data handles.
 * 
 * @author Chris Allan
 */
public abstract class AbstractNIOHandle<L extends Location> extends
	AbstractDataHandle<L> implements NIOHandle<L>
{

	// -- NIOHandle methods --

	@Override
	public void validateMode(final String mode) {
		if (!(mode.equals("r") || mode.equals("rw"))) {
			throw new IllegalArgumentException(String.format(
				"%s mode not in supported modes ('r', 'rw')", mode));
		}
	}

	@Override
	public boolean validateLength(final int writeLength) throws IOException {
		if (getOffset() + writeLength > length()) {
			setLength(getOffset() + writeLength);
			return false;
		}
		return true;
	}

}
