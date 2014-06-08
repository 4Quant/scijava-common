
package org.scijava.io;

import java.io.IOException;

/**
 * A data handle using buffered NIO logic.
 * 
 * @author Chris Allan
 */
public interface NIOHandle<L extends Location> extends DataHandle<L> {

	/**
	 * Ensures that the file mode is either "r" or "rw".
	 * 
	 * @param mode Mode to validate.
	 * @throws IllegalArgumentException If an illegal mode is passed.
	 */
	void validateMode(final String mode);

	/**
	 * Ensures that the handle has the correct length to be written to and extends
	 * it as required.
	 * 
	 * @param writeLength Number of bytes to write.
	 * @return <code>true</code> if the buffer has not required an extension.
	 *         <code>false</code> otherwise.
	 * @throws IOException If there is an error changing the handle's length.
	 */
	boolean validateLength(final int writeLength) throws IOException;

	/**
	 * Sets the new length of the handle.
	 * 
	 * @param length New length.
	 * @throws IOException If there is an error changing the handle's length.
	 */
	void setLength(long length) throws IOException;

}
