package io.trane.ndbc.postgres.proto.serializer;

import io.trane.ndbc.postgres.proto.Message.Execute;
import io.trane.ndbc.proto.BufferWriter;

public final class ExecuteSerializer {

  public final void encode(final Execute msg, final BufferWriter b) {
    b.writeChar('E');
    b.writeInt(0);

    b.writeCString(msg.portalName);
    b.writeInt(msg.maxNumberOfRows);
    b.writeLength(1);
  }
}
