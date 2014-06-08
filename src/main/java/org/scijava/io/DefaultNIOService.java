
package org.scijava.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

/**
 * Default service for working with the {@link java.nio} package, particularly
 * NIO {@link ByteBuffer} objects.
 * 
 * @author Chris Allan
 * @author Curtis Rueden
 */
@Plugin(type = Service.class)
public class DefaultNIOService extends AbstractService implements NIOService {

	// -- Fields --

	@Parameter
	private LogService log;

	/** Whether or not we are to use memory mapped I/O. */
	private final boolean useMappedByteBuffer = Boolean.parseBoolean(System
		.getProperty("mappedBuffers"));

	// -- NIOService API methods --

	@Override
	public ByteBuffer allocate(final FileChannel channel, final MapMode mapMode,
		final long bufferStartPosition, final int newSize) throws IOException
	{
		log.debug("NIO: allocate: mapped=" + useMappedByteBuffer + ", start=" +
			bufferStartPosition + ", size=" + newSize);
		if (useMappedByteBuffer) {
			return allocateMappedByteBuffer(channel, mapMode, bufferStartPosition,
				newSize);
		}
		return allocateDirect(channel, bufferStartPosition, newSize);
	}

	// -- Helper methods --

	/**
	 * Allocates memory and copies the desired file data into it.
	 * 
	 * @param channel File channel to allocate or map byte buffers from.
	 * @param bufferStartPosition The absolute position of the start of the
	 *          buffer.
	 * @param newSize The buffer size.
	 * @return A newly allocated NIO byte buffer.
	 * @throws IOException If there is an issue aligning or allocating the buffer.
	 */
	private ByteBuffer allocateDirect(final FileChannel channel,
		final long bufferStartPosition, final int newSize) throws IOException
	{
		final ByteBuffer buffer = ByteBuffer.allocate(newSize);
		channel.read(buffer, bufferStartPosition);
		return buffer;
	}

	/**
	 * Memory maps the desired file data into memory.
	 * 
	 * @param channel File channel to allocate or map byte buffers from.
	 * @param mapMode The map mode. Required but only used if memory mapped I/O is
	 *          to occur.
	 * @param bufferStartPosition The absolute position of the start of the
	 *          buffer.
	 * @param newSize The buffer size.
	 * @return A newly mapped NIO byte buffer.
	 * @throws IOException If there is an issue mapping, aligning or allocating
	 *           the buffer.
	 */
	private ByteBuffer allocateMappedByteBuffer(final FileChannel channel,
		final MapMode mapMode, final long bufferStartPosition, final int newSize)
		throws IOException
	{
		return channel.map(mapMode, bufferStartPosition, newSize);
	}

}
