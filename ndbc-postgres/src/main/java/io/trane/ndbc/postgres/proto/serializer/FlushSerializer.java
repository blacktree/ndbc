package io.trane.ndbc.postgres.proto.serializer;

import io.trane.ndbc.postgres.proto.Message.Flush;
import io.trane.ndbc.proto.BufferWriter;

public final class FlushSerializer {

  public final void encode(final Flush msg, final BufferWriter b) {
    b.writeChar('H');
    b.writeInt(4);
  }
}
