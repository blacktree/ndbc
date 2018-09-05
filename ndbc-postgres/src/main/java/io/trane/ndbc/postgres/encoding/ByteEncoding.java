package io.trane.ndbc.postgres.encoding;

import java.nio.charset.Charset;

import io.trane.ndbc.proto.BufferReader;
import io.trane.ndbc.proto.BufferWriter;
import io.trane.ndbc.value.ByteValue;

final class ByteEncoding extends Encoding<Byte, ByteValue> {

  public ByteEncoding(final Charset charset) {
    super(charset);
  }

  @Override
  public final Integer oid() {
    return Oid.INT2;
  }

  @Override
  public final Class<ByteValue> valueClass() {
    return ByteValue.class;
  }

  @Override
  public final String encodeText(final Byte value) {
    return Byte.toString(value);
  }

  @Override
  public final Byte decodeText(final String value) {
    return Byte.valueOf(value);
  }

  @Override
  public final void encodeBinary(final Byte value, final BufferWriter b) {
    b.writeShort(value);
  }

  @Override
  public final Byte decodeBinary(final BufferReader b) {
    return (byte) b.readShort();
  }

  @Override
  protected ByteValue box(final Byte value) {
    return new ByteValue(value);
  }
}
