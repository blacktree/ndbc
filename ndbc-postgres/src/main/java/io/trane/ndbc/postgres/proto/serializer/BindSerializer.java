package io.trane.ndbc.postgres.proto.serializer;

import io.trane.ndbc.postgres.encoding.Format;
import io.trane.ndbc.postgres.encoding.ValueEncoding;
import io.trane.ndbc.postgres.proto.Message.Bind;
import io.trane.ndbc.proto.BufferWriter;
import io.trane.ndbc.value.Value;

public final class BindSerializer {

  private final ValueEncoding encoding;

  public BindSerializer(final ValueEncoding encoding) {
    super();
    this.encoding = encoding;
  }

  public final void encode(final Bind msg, final BufferWriter b) {
    b.writeChar('B');
    b.writeInt(0);

    b.writeCString(msg.destinationPortalName);
    b.writeCString(msg.sourcePreparedStatementName);

    b.writeShort((short) msg.parameterFormatCodes.length);
    for (final short code : msg.parameterFormatCodes)
      b.writeShort(code);

    b.writeShort((short) msg.fields.length);
    for (int i = 0; i < msg.fields.length; i++) {
      final Value<?> field = msg.fields[i];
      if (field == null || field.isNull())
        b.writeInt(-1);
      else {
        final int lengthPosition = b.writerIndex();
        b.writeInt(0);
        encoding.encode(format(msg, i), field, b);
        b.writeLengthNoSelf(lengthPosition);
      }
    }

    b.writeShort((short) msg.resultColumnFormatCodes.length);
    for (final short code : msg.resultColumnFormatCodes)
      b.writeShort(code);

    b.writeLength(1);
  }

  private final Format format(final Bind msg, final int index) {
    if (msg.parameterFormatCodes.length == 1)
      return Format.fromCode(msg.parameterFormatCodes[0]);
    else
      return Format.fromCode(msg.parameterFormatCodes[index]);
  }
}
