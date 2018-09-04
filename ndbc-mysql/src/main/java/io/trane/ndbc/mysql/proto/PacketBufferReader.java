package io.trane.ndbc.mysql.proto;

import java.nio.charset.Charset;
import java.util.Arrays;

import io.trane.ndbc.proto.BufferReader;

public class PacketBufferReader implements BufferReader {
	private final BufferReader b;
	public final int packetLength;
	public final int sequence;

	public PacketBufferReader(final BufferReader b) {
		final byte[] packetHeader = b.readBytes(4);
		this.packetLength = (packetHeader[0] & 0xff) + ((packetHeader[1] & 0xff) << 8)
				+ ((packetHeader[2] & 0xff) << 16);
		this.sequence = packetHeader[3];
		this.b = b.readSlice(this.packetLength);
	}

	public int getSequence() {
		return sequence;
	}

	@Override
	public int readableBytes() {
		return b.readableBytes();
	}

	@Override
	public int readInt() {
		return b.readInt();
	}

	public long readVariableLong() {
		final int len = Byte.toUnsignedInt(readByte());
		if (len < 251) {
			return len;
		} else if (len == 251) {
			return -1;
		} else if (len == 252) {
			return readUnsignedShort();
		} else if (len == 253) {
			return readUnsignedMiddle();
		} else if (len == 254) {
			return readLong();
		} else {
			throw new IllegalStateException("Invalid length byte: " + len);
		}
	}

	public byte[] readNullTerminatedBytes() {
		final byte[] buf = new byte[100];
		int length = 0;
		for (byte i = b.readByte(); i != 0; i = b.readByte()) {
			buf[length] = i;
			length++;
		}
		final byte[] result = new byte[length];
		System.arraycopy(buf, 0, result, 0, length);
		return result;
	}

	private int readUnsignedMiddle() {
		final byte[] bytes = readBytes(3);
		final int value = ((bytes[0] & 0xFF) << 0) | ((bytes[1] & 0xFF) << 8) | ((bytes[2] & 0xFF) << 16);
		return value;
	}

	public long readUnsignedInt() {
		final byte[] bytes = readBytes(4);
		final long value = ((bytes[0] & 0xFF) << 0) | ((bytes[1] & 0xFF) << 8) | ((bytes[2] & 0xFF) << 16)
				| ((bytes[3] & 0xFF) << 24);
		return value;
	}

	public byte[] readLengthCodedBytes() {
		final long length = readVariableLong();
		return readBytes((int) length);
	}

	public String readLengthCodedString(Charset charset) {
		return new String(readLengthCodedBytes(), charset);
	}

	@Override
	public byte readByte() {
		return b.readByte();
	}

	@Override
	public short readShort() {
		return b.readShort();
	}

	public int readUnsignedShort() {
		final byte[] bytes = readBytes(2);
		final int value = ((bytes[0] & 0xFF) << 0) | ((bytes[1] & 0xFF) << 8);
		return value;
	}

	@Override
	public String readCString(Charset charset) {
		return b.readCString(charset);
	}

	@Override
	public String readCString(final int length, Charset charset) {
		return b.readCString(length, charset);
	}

	@Override
	public String readString(Charset charset) {
		return b.readString(charset);
	}

	@Override
	public String readString(final int length, Charset charset) {
		return b.readString(length, charset);
	}

	@Override
	public byte[] readBytes() {
		return b.readBytes();
	}

	@Override
	public byte[] readBytes(final int length) {
		return b.readBytes(length);
	}

	@Override
	public int[] readInts() {
		return b.readInts();
	}

	@Override
	public int[] readInts(final int length) {
		return b.readInts(length);
	}

	@Override
	public short[] readShorts() {
		return b.readShorts();
	}

	@Override
	public short[] readShorts(final int length) {
		return b.readShorts(length);
	}

	@Override
	public BufferReader readSlice(final int length) {
		return b.readSlice(length);
	}

	@Override
	public void markReaderIndex() {
		b.markReaderIndex();
	}

	@Override
	public void resetReaderIndex() {
		b.resetReaderIndex();
	}

	@Override
	public void retain() {
		b.retain();
	}

	@Override
	public void release() {
		b.release();
	}

	@Override
	public Long readLong() {
		return b.readLong();
	}

	@Override
	public Float readFloat() {
		return b.readFloat();
	}

	@Override
	public Double readDouble() {
		return b.readDouble();
	}

	public void dump() {
		b.markReaderIndex();
		System.out.println(Arrays.toString(b.readBytes()));
		b.resetReaderIndex();
	}
}
