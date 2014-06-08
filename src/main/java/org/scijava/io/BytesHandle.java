
package org.scijava.io;

import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A data handle for bytes in memory.
 * 
 * @see DataHandle
 */
public class BytesHandle extends AbstractNIOHandle<BytesLocation> {

	// -- Constants --

	/** Initial length of a new file. */
	private static final int INITIAL_LENGTH = 1000000;

	// -- Fields --

	/** Backing ByteBuffer. */
	private ByteBuffer buffer;

	/** Length of the file. */
	// protected long length;

	// -- Constructors --

	/**
	 * Creates a random access byte stream to read from, and write to, the bytes
	 * specified by the byte[] argument.
	 */
	public BytesHandle(final byte[] bytes) {
		buffer = ByteBuffer.wrap(bytes);
	}

	public BytesHandle(final ByteBuffer bytes) {
		buffer = bytes;
	}

	/**
	 * Creates a random access byte stream to read from, and write to.
	 * 
	 * @param capacity Number of bytes to initially allocate.
	 */
	public BytesHandle(final int capacity) {
		buffer = ByteBuffer.allocate(capacity);
		buffer.limit(capacity);
	}

	/** Creates a random access byte stream to write to a byte array. */
	public BytesHandle() {
		buffer = ByteBuffer.allocate(INITIAL_LENGTH);
		buffer.limit(0);
	}

	// -- ByteArrayHandle API methods --

	/** Gets the byte array backing this FileHandle. */
	public byte[] getBytes() {
		return buffer.array();
	}

	/**
	 * Gets the byte buffer backing this handle. <b>NOTE:</b> This is the backing
	 * buffer. Any modifications to this buffer including position, length and
	 * capacity will affect subsequent calls upon its source handle.
	 * 
	 * @return Backing buffer of this handle.
	 */
	public ByteBuffer getByteBuffer() {
		return buffer;
	}

	// -- AbstractNIOHandle API methods --

	@Override
	public void setLength(final long length) throws IOException {
		if (length > buffer.capacity()) {
			final long fp = getOffset();
			final ByteBuffer tmp = ByteBuffer.allocate((int) (length * 2));
			final ByteOrder order = buffer == null ? null : getOrder();
			seek(0);
			buffer = tmp.put(buffer);
			if (order != null) setOrder(order);
			seek(fp);
		}
		buffer.limit((int) length);
	}

	// -- IRandomAccess API methods --

	@Override
	public void close() {
		// NB: No action needed.
	}

	@Override
	public long getOffset() {
		return buffer.position();
	}

	@Override
	public long length() {
		return buffer.limit();
	}

	@Override
	public int read(final byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(final byte[] b, final int off, int len) throws IOException {
		if (getOffset() + len > length()) {
			len = (int) (length() - getOffset());
		}
		buffer.get(b, off, len);
		return len;
	}

	@Override
	public int read(final ByteBuffer buf) throws IOException {
		return read(buf, 0, buf.capacity());
	}

	@Override
	public int read(final ByteBuffer buf, final int off, final int len)
		throws IOException
	{
		if (buf.hasArray()) {
			buffer.get(buf.array(), off, len);
			return len;
		}

		final byte[] b = new byte[len];
		read(b);
		buf.put(b, 0, len);
		return len;
	}

	@Override
	public void seek(final long pos) throws IOException {
		if (pos > length()) setLength(pos);
		buffer.position((int) pos);
	}

	@Override
	public ByteOrder getOrder() {
		return buffer.order();
	}

	@Override
	public void setOrder(final ByteOrder order) {
		buffer.order(order);
	}

	// -- DataInput API methods --

	@Override
	public boolean readBoolean() throws IOException {
		return readByte() != 0;
	}

	@Override
	public byte readByte() throws IOException {
		if (getOffset() + 1 > length()) {
			throw new EOFException();
		}
		try {
			return buffer.get();
		}
		catch (final BufferUnderflowException e) {
			final EOFException eof = new EOFException();
			eof.initCause(e);
			throw eof;
		}
	}

	@Override
	public char readChar() throws IOException {
		if (getOffset() + 2 > length()) {
			throw new EOFException();
		}
		try {
			return buffer.getChar();
		}
		catch (final BufferUnderflowException e) {
			final EOFException eof = new EOFException();
			eof.initCause(e);
			throw eof;
		}
	}

	@Override
	public double readDouble() throws IOException {
		if (getOffset() + 8 > length()) {
			throw new EOFException();
		}
		try {
			return buffer.getDouble();
		}
		catch (final BufferUnderflowException e) {
			final EOFException eof = new EOFException();
			eof.initCause(e);
			throw eof;
		}
	}

	@Override
	public float readFloat() throws IOException {
		if (getOffset() + 4 > length()) {
			throw new EOFException();
		}
		try {
			return buffer.getFloat();
		}
		catch (final BufferUnderflowException e) {
			final EOFException eof = new EOFException();
			eof.initCause(e);
			throw eof;
		}
	}

	@Override
	public void readFully(final byte[] b) throws IOException {
		readFully(b, 0, b.length);
	}

	@Override
	public void readFully(final byte[] b, final int off, final int len)
		throws IOException
	{
		if (getOffset() + len > length()) {
			throw new EOFException();
		}
		try {
			buffer.get(b, off, len);
		}
		catch (final BufferUnderflowException e) {
			final EOFException eof = new EOFException();
			eof.initCause(e);
			throw eof;
		}
	}

	@Override
	public int readInt() throws IOException {
		if (getOffset() + 4 > length()) {
			throw new EOFException();
		}
		try {
			return buffer.getInt();
		}
		catch (final BufferUnderflowException e) {
			final EOFException eof = new EOFException();
			eof.initCause(e);
			throw eof;
		}
	}

	@Override
	public String readLine() throws IOException {
		throw new IOException("Unimplemented");
	}

	@Override
	public long readLong() throws IOException {
		if (getOffset() + 8 > length()) {
			throw new EOFException();
		}
		try {
			return buffer.getLong();
		}
		catch (final BufferUnderflowException e) {
			final EOFException eof = new EOFException();
			eof.initCause(e);
			throw eof;
		}
	}

	@Override
	public short readShort() throws IOException {
		if (getOffset() + 2 > length()) {
			throw new EOFException();
		}
		try {
			return buffer.getShort();
		}
		catch (final BufferUnderflowException e) {
			final EOFException eof = new EOFException();
			eof.initCause(e);
			throw eof;
		}
	}

	@Override
	public int readUnsignedByte() throws IOException {
		return readByte() & 0xff;
	}

	@Override
	public int readUnsignedShort() throws IOException {
		return readShort() & 0xffff;
	}

	@Override
	public String readUTF() throws IOException {
		final int length = readUnsignedShort();
		final byte[] b = new byte[length];
		read(b);
		return new String(b, "UTF-8");
	}

	@Override
	public int skipBytes(final int n) throws IOException {
		final int skipped = (int) Math.min(n, length() - getOffset());
		if (skipped < 0) return 0;
		seek(getOffset() + skipped);
		return skipped;
	}

	// -- DataOutput API methods --

	@Override
	public void write(final byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	@Override
	public void write(final byte[] b, final int off, final int len)
		throws IOException
	{
		validateLength(len);
		buffer.put(b, off, len);
	}

	@Override
	public void write(final ByteBuffer buf) throws IOException {
		write(buf, 0, buf.capacity());
	}

	@Override
	public void write(final ByteBuffer buf, final int off, final int len)
		throws IOException
	{
		validateLength(len);
		buf.position(off);
		buf.limit(off + len);
		buffer.put(buf);
	}

	@Override
	public void write(final int b) throws IOException {
		validateLength(1);
		buffer.put((byte) b);
	}

	@Override
	public void writeBoolean(final boolean v) throws IOException {
		write(v ? 1 : 0);
	}

	@Override
	public void writeByte(final int v) throws IOException {
		write(v);
	}

	@Override
	public void writeBytes(final String s) throws IOException {
		write(s.getBytes("UTF-8"));
	}

	@Override
	public void writeChar(final int v) throws IOException {
		validateLength(2);
		buffer.putChar((char) v);
	}

	@Override
	public void writeChars(final String s) throws IOException {
		final int len = 2 * s.length();
		validateLength(len);
		final char[] c = s.toCharArray();
		for (int i = 0; i < c.length; i++) {
			writeChar(c[i]);
		}
	}

	@Override
	public void writeDouble(final double v) throws IOException {
		validateLength(8);
		buffer.putDouble(v);
	}

	@Override
	public void writeFloat(final float v) throws IOException {
		validateLength(4);
		buffer.putFloat(v);
	}

	@Override
	public void writeInt(final int v) throws IOException {
		validateLength(4);
		buffer.putInt(v);
	}

	@Override
	public void writeLong(final long v) throws IOException {
		validateLength(8);
		buffer.putLong(v);
	}

	@Override
	public void writeShort(final int v) throws IOException {
		validateLength(2);
		buffer.putShort((short) v);
	}

	@Override
	public void writeUTF(final String str) throws IOException {
		final byte[] b = str.getBytes("UTF-8");
		writeShort(b.length);
		write(b);
	}

}
