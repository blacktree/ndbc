package io.trane.ndbc.postgres.proto.marshaller;

import io.trane.ndbc.postgres.proto.Message.Close;
import io.trane.ndbc.proto.BufferWriter;
import io.trane.ndbc.proto.Marshaller;

public final class CloseMarshaller implements Marshaller<Close> {

  @Override
  public final void apply(final Close msg, final BufferWriter b) {
    b.writeChar('C');
    b.writeInt(0);

    if (msg instanceof Close.ClosePreparedStatement)
      b.writeChar('S');
    else if (msg instanceof Close.ClosePortal)
      b.writeChar('P');
    else
      throw new IllegalStateException("Invalid close message: " + msg);

    b.writeCString(msg.name);
    b.writeLength(1);
  }
}
