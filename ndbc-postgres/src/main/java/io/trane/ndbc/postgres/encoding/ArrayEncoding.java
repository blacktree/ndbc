package io.trane.ndbc.postgres.encoding;

import io.trane.ndbc.proto.BufferReader;
import io.trane.ndbc.proto.BufferWriter;
import io.trane.ndbc.value.Value;

abstract class ArrayEncoding<I, V extends Value<I[]>> extends Encoding<I[], V> {

  protected abstract I[] newArray(int length);

  protected abstract I[] emptyArray();
  
  protected abstract Encoding<I, ?> itemEncoding();

  @Override
  public final String encodeText(final I[] value) {
    return null;
  }

  @Override
  public final I[] decodeText(final String value) {
    return null;
  }

  @Override
  public final void encodeBinary(final I[] value, final BufferWriter b) {
    b.writeInt(1); // dimensions
    b.writeInt(0); // flags
    b.writeInt(itemEncoding().oid());
    b.writeInt(value.length);
    b.writeInt(1); // lbound
    for (I v : value)
      if (v == null)
        b.writeInt(-1);
      else {
        final int lengthPosition = b.writerIndex();
        b.writeInt(0); // length
        itemEncoding().encodeBinary(v, b);
        b.writeLengthNoSelf(lengthPosition);
      }
  }

  @Override
  public final I[] decodeBinary(final BufferReader b) {
    int dimensions = b.readInt();
    assert (dimensions <= 1);
    b.readInt(); // flags bit 0: 0=no-nulls, 1=has-nulls
    b.readInt(); // elementOid
    if (dimensions == 0)
      return emptyArray();
    else {
      int length = b.readInt();
      int lbound = b.readInt();
      assert (lbound == 1);

      I[] result = newArray(length);

      for (int i = 0; i < length; i++) {
        int elemLength = b.readInt();
        if (elemLength == -1)
          result[i] = null;
        else {
          result[i] = itemEncoding().decodeBinary(b.readSlice(elemLength));
        }
      }
      return result;
    }
  }
}
