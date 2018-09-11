package io.trane.ndbc.postgres.proto.marshaller;

import io.trane.ndbc.postgres.encoding.EncodingRegistry;
import io.trane.ndbc.postgres.encoding.Format;
import io.trane.ndbc.postgres.proto.Message.Bind;
import io.trane.ndbc.proto.BufferWriter;
import io.trane.ndbc.proto.Marshaller;
import io.trane.ndbc.value.Value;

public final class BindMarshaller implements Marshaller<Bind> {

  private final EncodingRegistry encoding;

  public BindMarshaller(final EncodingRegistry encoding) {
    this.encoding = encoding;
  }

  @Override
  public final void apply(final Bind msg, final BufferWriter b) {
    b.writeChar('B');
    b.writeInt(0);

    b.writeCString(msg.destinationPortalName);
    b.writeCString(msg.sourcePreparedStatementName);

    b.writeShort((short) msg.parameterFormatCodes.length);
    for (final short code : msg.parameterFormatCodes)
      b.writeShort(code);

    b.writeShort((short) msg.fields.size());

    int i = 0;
    for (final Value<?> field : msg.fields) {
      if ((field == null) || field.isNull())
        b.writeInt(-1);
      else {
        final int lengthPosition = b.writerIndex();
        b.writeInt(0);
        if (format(msg, i) != Format.BINARY)
          throw new IllegalStateException("Bug - Expected binary encoding");
        encoding.encodeBinary(field, b);
        b.writeLengthNoSelf(lengthPosition);
      }
      i++;
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
