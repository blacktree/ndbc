package io.trane.ndbc.mysql.proto.unmarshaller;

import java.util.Optional;

import io.trane.ndbc.mysql.proto.Message.ServerMessage;
import io.trane.ndbc.mysql.proto.PacketBufferReader;
import io.trane.ndbc.proto.BufferReader;
import io.trane.ndbc.proto.Unmarshaller;

public abstract class MysqlUnmarshaller<T extends ServerMessage> implements Unmarshaller<T> {

  @Override
  public Optional<T> apply(final BufferReader bufferReader) {
    final PacketBufferReader p = new PacketBufferReader(bufferReader);
    p.markReaderIndex();
    final int header = p.readByte() & 0xFF;
    if (!acceptsHeader(header))
      throw new IllegalStateException(
          "Invalid packet for " + getClass() + ". Remaining bytes: " + bufferReader.readableBytes());
    p.resetReaderIndex();
    return Optional.of(decode(header, p));
  }

  public <U extends ServerMessage> MysqlUnmarshaller<ServerMessage> orElse(final MysqlUnmarshaller<U> other) {

    return new MysqlUnmarshaller<ServerMessage>() {

      @Override
      protected boolean acceptsHeader(final int header) {
        return MysqlUnmarshaller.this.acceptsHeader(header) || other.acceptsHeader(header);
      }

      @Override
      protected ServerMessage decode(final int header, final PacketBufferReader packet) {
        if (MysqlUnmarshaller.this.acceptsHeader(header))
          return MysqlUnmarshaller.this.decode(header, packet);
        else
          return other.decode(header, packet);
      }

      @Override
      public String toString() {
        return MysqlUnmarshaller.this.toString() + ".orElse(" + other.toString() + ")";
      }
    };
  }

  protected boolean acceptsHeader(final int header) {
    return true;
  }

  protected abstract T decode(int header, PacketBufferReader packet);

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
