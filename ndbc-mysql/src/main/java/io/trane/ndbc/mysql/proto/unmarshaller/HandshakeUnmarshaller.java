package io.trane.ndbc.mysql.proto.unmarshaller;

import java.nio.charset.Charset;

import io.trane.ndbc.mysql.proto.Message.Handshake;
import io.trane.ndbc.mysql.proto.PacketBufferReader;

/**
 * https://mariadb.com/kb/en/library/1-connecting-connecting/#initial-handshake-packet
 * https://github.com/mysql/mysql-connector-j/blob/83c6dc41b96809df81444362933043b20a1d49d5/src/com/mysql/jdbc/MysqlIO.java#L1011
 * https://github.com/twitter/finagle/blob/7610016b6d01a267c4cc824a1753cce1eb81d2d2/finagle-mysql/src/main/scala/com/twitter/finagle/mysql/Result.scala
 */
public class HandshakeUnmarshaller extends MysqlUnmarshaller<Handshake> {

  private final Charset charset;

  public HandshakeUnmarshaller(final Charset charset) {
    this.charset = charset;
  }

  @Override
  public final Handshake decode(final int header, final PacketBufferReader packet) {

    final int protocolVersion = packet.readByte() & 0xff;
    final String serverVersion = packet.readCString(charset);
    final long connectionId = packet.readUnsignedInt();
    final byte[] salt1 = packet.readBytes(8);
    packet.readByte();
    int serverCapabilities = packet.readUnsignedShort();
    final int defaultCollation = packet.readByte() & 0xff;
    final int statusFlags = packet.readUnsignedShort();
    final int serverCapabilitiesHi = packet.readUnsignedShort();
    serverCapabilities |= serverCapabilitiesHi << 16;

    packet.readByte(); // auth plugin data, ignored

    packet.readBytes(10); // padding

    final byte[] salt2 = packet.readNullTerminatedBytes();

    return new Handshake(packet.getSequence(), protocolVersion, serverVersion, connectionId, concat(salt1, salt2),
        serverCapabilities, defaultCollation, statusFlags, "mysql_native_password");
  }

  private byte[] concat(final byte[] a, final byte[] b) {
    final byte[] c = new byte[a.length + b.length];
    System.arraycopy(a, 0, c, 0, a.length);
    System.arraycopy(b, 0, c, a.length, b.length);
    return c;
  }
}
