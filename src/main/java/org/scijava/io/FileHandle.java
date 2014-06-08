
package org.scijava.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.scijava.plugin.Plugin;

/**
 * A data handle for files on disk.
 * 
 * @author Curtis Rueden
 */
@Plugin(type = DataHandle.class)
public class FileHandle extends AbstractDataHandle<FileLocation> {

	// -- Fields --

	/** The random access file object backing this file handle. */
	private final RandomAccessFile raf;

	// -- Constructors --

	/**
	 * Creates a random access file stream to read from, and optionally to write
	 * to, the file specified by the File argument.
	 */
	public FileHandle(final File file, final String mode)
		throws FileNotFoundException
	{
		raf = new RandomAccessFile(file, mode);
	}

	/**
	 * Creates a random access file stream to read from, and optionally to write
	 * to, a file with the specified name.
	 */
	public FileHandle(final String name, final String mode)
		throws FileNotFoundException
	{
		raf = new RandomAccessFile(name, mode);
	}

	// -- FileHandle API methods --

	/** Gets the random access file object backing this FileHandle. */
	public RandomAccessFile getRandomAccessFile() {
		return raf;
	}

	// -- DataHandle methods --

	@Override
	public Class<FileLocation> getLocationType() {
		return FileLocation.class;
	}

	@Override
	public void close() throws IOException {
		raf.close();
	}

	@Override
	public long getOffset() throws IOException {
		return raf.getFilePointer();
	}

	@Override
	public long length() throws IOException {
		return raf.length();
	}

	@Override
	public int read(final byte[] b) throws IOException {
		return raf.read(b);
	}

	@Override
	public int read(final byte[] b, final int off, final int len)
		throws IOException
	{
		return raf.read(b, off, len);
	}

	@Override
	public int read(final ByteBuffer buffer) throws IOException {
		return read(buffer, 0, buffer.capacity());
	}

	@Override
	public int read(final ByteBuffer buffer, final int off, final int len)
		throws IOException
	{
		final byte[] b = new byte[len];
		final int n = read(b);
		buffer.put(b, off, len);
		return n;
	}

	@Override
	public void seek(final long pos) throws IOException {
		raf.seek(pos);
	}

	@Override
	public void write(final ByteBuffer buf) throws IOException {
		write(buf, 0, buf.capacity());
	}

	@Override
	public void write(final ByteBuffer buf, final int off, final int len)
		throws IOException
	{
		// FIXME
	}

	// -- DataInput API methods --

	@Override
	public boolean readBoolean() throws IOException {
		return raf.readBoolean();
	}

	@Override
	public byte readByte() throws IOException {
		return raf.readByte();
	}

	@Override
	public char readChar() throws IOException {
		return raf.readChar();
	}

	@Override
	public double readDouble() throws IOException {
		return raf.readDouble();
	}

	@Override
	public float readFloat() throws IOException {
		return raf.readFloat();
	}

	@Override
	public void readFully(final byte[] b) throws IOException {
		raf.readFully(b);
	}

	@Override
	public void readFully(final byte[] b, final int off, final int len)
		throws IOException
	{
		raf.readFully(b, off, len);
	}

	@Override
	public int readInt() throws IOException {
		return raf.readInt();
	}

	@Override
	public String readLine() throws IOException {
		return raf.readLine();
	}

	@Override
	public long readLong() throws IOException {
		return raf.readLong();
	}

	@Override
	public short readShort() throws IOException {
		return raf.readShort();
	}

	@Override
	public int readUnsignedByte() throws IOException {
		return raf.readUnsignedByte();
	}

	@Override
	public int readUnsignedShort() throws IOException {
		return raf.readUnsignedShort();
	}

	@Override
	public String readUTF() throws IOException {
		return raf.readUTF();
	}

	@Override
	public int skipBytes(final int n) throws IOException {
		return raf.skipBytes(n);
	}

	// -- DataOutput API metthods --

	@Override
	public void write(final byte[] b) throws IOException {
		raf.write(b);
	}

	@Override
	public void write(final byte[] b, final int off, final int len)
		throws IOException
	{
		raf.write(b, off, len);
	}

	@Override
	public void write(final int b) throws IOException {
		raf.write(b);
	}

	@Override
	public void writeBoolean(final boolean v) throws IOException {
		raf.writeBoolean(v);
	}

	@Override
	public void writeByte(final int v) throws IOException {
		raf.writeByte(v);
	}

	@Override
	public void writeBytes(final String s) throws IOException {
		raf.writeBytes(s);
	}

	@Override
	public void writeChar(final int v) throws IOException {
		raf.writeChar(v);
	}

	@Override
	public void writeChars(final String s) throws IOException {
		raf.writeChars(s);
	}

	@Override
	public void writeDouble(final double v) throws IOException {
		raf.writeDouble(v);
	}

	@Override
	public void writeFloat(final float v) throws IOException {
		raf.writeFloat(v);
	}

	@Override
	public void writeInt(final int v) throws IOException {
		raf.writeInt(v);
	}

	@Override
	public void writeLong(final long v) throws IOException {
		raf.writeLong(v);
	}

	@Override
	public void writeShort(final int v) throws IOException {
		raf.writeShort(v);
	}

	@Override
	public void writeUTF(final String str) throws IOException {
		raf.writeUTF(str);
	}

	@Override
	public ByteOrder getOrder() {
		// FIXME
		return null;
	}

	@Override
	public void setOrder(final ByteOrder order) {
		// FIXME
	}

	@Override
	public void resetStream() throws IOException {
		raf.seek(0);
	}

	// -- Typed methods --

	@Override
	public boolean supports(final Location location) {
		return location instanceof FileLocation;
	}

	// -- Helper methods --

	private File getFile() {
		return getLocation().getFile();
	}

}
