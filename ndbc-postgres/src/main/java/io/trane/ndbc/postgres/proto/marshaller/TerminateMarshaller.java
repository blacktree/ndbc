package io.trane.ndbc.postgres.proto.marshaller;

import io.trane.ndbc.postgres.proto.Message.Terminate;
import io.trane.ndbc.proto.BufferWriter;

public final class TerminateMarshaller {

  public final void encode(final Terminate msg, final BufferWriter b) {
    b.writeChar('X');
    b.writeInt(0);
    b.writeLength(1);
  }
}
