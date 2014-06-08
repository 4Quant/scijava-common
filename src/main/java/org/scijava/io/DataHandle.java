
package org.scijava.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.scijava.plugin.WrapperPlugin;

/**
 * Plugin providing access to bytes in a data stream (e.g., files or arrays).
 * 
 * @author Curtis Rueden
 * @see java.io.InputStream
 * @see java.io.OutputStream
 * @see java.io.RandomAccessFile
 */
public interface DataHandle<L> extends WrapperPlugin<Location>, DataInput,
	DataOutput
{

	/** Gets the type of {@link Location} objects supported by the handle. */
	Class<L> getLocationType();

	/** Gets the {@link Location} wrapped by this handle. */
	L getLocation();

	/** Closes this data handle and releases any associated system resources. */
	void close() throws IOException;

	/** Returns the current offset in this stream. */
	long getOffset() throws IOException;

	/** Returns the length of this stream. */
	long length() throws IOException;

	/**
	 * Returns the current order of the stream.
	 * 
	 * @return See above.
	 */
	ByteOrder getOrder();

	/**
	 * Sets the byte order of the stream.
	 * 
	 * @param order Order to set.
	 */
	void setOrder(ByteOrder order);

	/**
	 * Reads up to b.length bytes of data from this stream into an array of bytes.
	 * 
	 * @return the total number of bytes read into the buffer.
	 */
	int read(byte[] b) throws IOException;

	/**
	 * Reads up to len bytes of data from this stream into an array of bytes.
	 * 
	 * @return the total number of bytes read into the buffer.
	 */
	int read(byte[] b, int off, int len) throws IOException;

	/**
	 * Reads up to buffer.capacity() bytes of data from this stream into a
	 * ByteBuffer.
	 */
	int read(ByteBuffer buffer) throws IOException;

	/**
	 * Reads up to len bytes of data from this stream into a ByteBuffer.
	 * 
	 * @return the total number of bytes read into the buffer.
	 */
	int read(ByteBuffer buffer, int offset, int len) throws IOException;

	/**
	 * Sets the stream pointer offset, measured from the beginning of this stream,
	 * at which the next read or write occurs.
	 */
	void seek(long pos) throws IOException;

	/**
	 * Writes up to buffer.capacity() bytes of data from the given ByteBuffer to
	 * this stream.
	 */
	void write(ByteBuffer buf) throws IOException;

	/**
	 * Writes up to len bytes of data from the given ByteBuffer to this stream.
	 */
	void write(ByteBuffer buf, int off, int len) throws IOException;

	/**
	 * Close and reopen the stream; the stream pointer and mark should be reset to
	 * 0. This method is called if we need to seek backwards within the stream.
	 */
	void resetStream() throws IOException;

}
