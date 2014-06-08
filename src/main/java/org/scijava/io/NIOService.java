
package org.scijava.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import org.scijava.service.SciJavaService;

/**
 * Interface for services that work with the {@link java.nio} package,
 * particularly NIO {@link ByteBuffer} objects.
 * 
 * @author Chris Allan
 * @author Curtis Rueden
 */
public interface NIOService extends SciJavaService {

	/**
	 * Allocates or maps the desired file data into memory.
	 * <p>
	 * This method provides a facade to byte buffer allocation that enables
	 * <code>FileChannel.map()</code> usage on platforms where it's unlikely to
	 * give us problems and heap allocation where it is.
	 * </p>
	 * 
	 * @param channel File channel to allocate or map byte buffers from.
	 * @param mapMode The map mode. Required but only used if memory mapped I/O is
	 *          to occur.
	 * @param bufferStartPosition The absolute position of the start of the
	 *          buffer.
	 * @param newSize The buffer size.
	 * @return A newly allocated or mapped NIO byte buffer.
	 * @see "http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5092131"
	 * @see "http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6417205"
	 * @throws IOException If there is an issue mapping, aligning or allocating
	 *           the buffer.
	 */
	ByteBuffer allocate(FileChannel channel, MapMode mapMode,
		long bufferStartPosition, int newSize) throws IOException;

}
