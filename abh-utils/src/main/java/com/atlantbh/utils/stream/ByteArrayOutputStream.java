package com.atlantbh.utils.stream;


/**
 * Class similar to {@link java.io.ByteArrayOutputStream} with that difference it uses
 * System.arraycopy when size exceeded, and with different buffer size strategy.
 * @author earnau
 *
 */
public class ByteArrayOutputStream extends java.io.ByteArrayOutputStream {
	/**
	 * Default buffer size if not specified explicitly - 4K
	 */
	public static final int DEFAULT_INIT_SIZE = 4 * 1024;

	private final int initSize;
	
	protected int size;
	protected byte[] buffer = null;

	/**
	 * Initializes {@link ByteArrayOutputStream} with default initial size {@link ByteArrayOutputStream.DEFAULT_INIT_SIZE}
	 */
	public ByteArrayOutputStream() {
		this(DEFAULT_INIT_SIZE);
	}

	/**
	 * Initilizes {@link ByteArrayOutputStream} with provided initial size.
	 * @param initSize initial buffer size
	 */
	public ByteArrayOutputStream(int initSize) {
		this.initSize = initSize;
		this.size = 0;
		this.buffer = new byte[initSize];
	}
	
	public final void write(byte bytes[]) {
		ensureBufferSize(size + bytes.length);
		System.arraycopy(bytes, 0, buffer, size, bytes.length);
		size += bytes.length;
	}

	public final void write(byte bytes[], int off, int len) {
		ensureBufferSize(size + len);
		System.arraycopy(bytes, off, buffer, size, len);
		size += len;
	}

	public final void write(int b) {
		ensureBufferSize(size + 1);
		buffer[size++] = (byte) b;
	}

	/**
	 * Ensures buffer is large enough to accepts new bytes.
	 * In case it is not large enough, it will increase its length by initial size or
	 * by number of missing bytes.
	 * @param minSize min buffer length after method invocation
	 */
	private void ensureBufferSize(int minSize) {
		if (minSize > buffer.length) {
			byte[] old = buffer;
			buffer = new byte[Math.max(minSize, buffer.length + initSize)];
			System.arraycopy(old, 0, buffer, 0, old.length);
			old = null;
		}
	}

	/**
	 * Returns number of bytes currently written.
	 * @return number of bytes currently written
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Returns current buffer used for storing bytes. 
	 * Note that buffer may be larger than actual number of bytes written.
	 * @return current buffer
	 */
	public byte[] getBuffer() {
		return buffer;
	}
	
	/**
	 * Trims buffer to current size and returns it.
	 * In case bytes are immediately consumed, better alternative is combining buffer with current size.
	 * @return trimmed buffer
	 */
	public byte[] trimBuffer() {
		if (buffer.length != size) {
			byte[] old = buffer;
			buffer = new byte[size];
			System.arraycopy(old, 0, buffer, 0, size);
			old = null;
		}
		return buffer;
	}

	/**
	 * Reset output stream to its initial state
	 */
	public void reset() {
		// reset buffer size to avoid accidental memory leak
		if (size > initSize) {
			buffer = new byte[initSize];
		}
		size = 0;
	}
}
