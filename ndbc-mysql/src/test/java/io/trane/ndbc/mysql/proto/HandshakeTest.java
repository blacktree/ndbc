// package io.trane.ndbc.mysql.proto;
//
// import static org.junit.Assert.assertEquals;
// import static org.junit.Assert.assertTrue;
//
// import java.io.ByteArrayInputStream;
// import java.io.ByteArrayOutputStream;
// import java.io.IOException;
// import java.net.Socket;
// import java.nio.charset.Charset;
// import java.util.Arrays;
// import java.util.Optional;
// import java.util.logging.Logger;
//
// import org.junit.Ignore;
// import org.junit.Test;
//
// import io.trane.ndbc.mysql.proto.Message.HandshakeResponseMessage;
// import io.trane.ndbc.mysql.proto.Message.InitialHandshakeMessage;
// import io.trane.ndbc.mysql.proto.Message.OkResponseMessage;
// import io.trane.ndbc.mysql.proto.Message.QueryCommand;
// import io.trane.ndbc.mysql.proto.Message.TextResultSet;
// import io.trane.ndbc.mysql.proto.Message.ServerResponseMessage;
// import
// io.trane.ndbc.mysql.proto.marshaller.HandshakeResponsePacketMarshaller;
// import io.trane.ndbc.mysql.proto.marshaller.TextCommandMarshaller;
// import
// io.trane.ndbc.mysql.proto.unmarshaller.InitialHandshakePacketUnmarshaller;
// import io.trane.ndbc.mysql.proto.unmarshaller.ServerResponseUnmarshaller;
// import io.trane.ndbc.mysql.proto.unmarshaller.TextResultSetUnmarshaller;
// import io.trane.ndbc.proto.BufferReader;
// import io.trane.ndbc.proto.BufferWriter;
//
// public class HandshakeTest {
//
// private final InitialHandshakePacketUnmarshaller
// initialHandshakePacketUnmarshaller = new
// InitialHandshakePacketUnmarshaller();
// private final HandshakeResponsePacketMarshaller
// handshakeResponsePacketMarshaller = new HandshakeResponsePacketMarshaller();
// private final ServerResponseUnmarshaller serverResponseUnmarshaller = new
// ServerResponseUnmarshaller();
// private final TextCommandMarshaller textCommandMarshaller = new
// TextCommandMarshaller();
// private final TextResultSetUnmarshaller textResultSetUnmarshaller = new
// TextResultSetUnmarshaller();
// private final Charset utf8 = Charset.forName("UTF-8");
//
// private final Logger logger =
// Logger.getLogger(HandshakeTest.class.getName());
//
// private void info(final String msg) {
// logger.info(msg);
// }
//
// @Test
// @Ignore
// public void handShakeAndQuery() throws IOException {
// // use to test locally docker run -e MYSQL_ROOT_PASSWORD=mysql -p 3306:3306
// -d
// // mysql
// final Socket clientSocket = new Socket("localhost", 3306);
//
// final BufferWriter bw = new
// TestSyncBufferWriter(clientSocket.getOutputStream(), utf8);
// final BufferReader br = new
// TestSyncBufferReader(clientSocket.getInputStream(), utf8);
//
// // StartupExchange
// final InitialHandshakeMessage serverHandshake =
// initialHandshakePacketUnmarshaller.decode(br);
// info("RECEIVED:" + serverHandshake);
//
// final HandshakeResponseMessage handshakeResponsePacketMessage = new
// HandshakeResponseMessage(
// serverHandshake.sequence + 1, "root", Optional.of("mysql"),
// Optional.of("mysql"), "utf8",
// serverHandshake.seed, "mysql_native_password");
//
// handshakeResponsePacketMarshaller.encode(handshakeResponsePacketMessage, bw,
// utf8);
// info("SENT:" + handshakeResponsePacketMessage);
// final ServerResponseMessage response = serverResponseUnmarshaller.decode(br);
// info("RECEIVED:" + response);
//
// assertTrue(response instanceof OkResponseMessage);
//
// // SimpleQuery
// final QueryCommand queryCommand = new QueryCommand("SELECT 'TEST' as test");
// textCommandMarshaller.encode(queryCommand, bw, utf8);
// info("SENT:" + queryCommand);
// final TextResultSet resultSet = textResultSetUnmarshaller.decode(br);
// info("RECEIVED:" + resultSet);
// assertEquals(1, resultSet.fields.size());
// assertEquals(1, resultSet.textRows.size());
// assertEquals("TEST", resultSet.textRows.get(0).values.get(0));
//
// clientSocket.close();
// }
//
// @Test
// public void initalHandshakePacket() {
// final byte[] binary = {(byte) 0x36, (byte) 0x00, (byte) 0x00, (byte) 0x00,
// (byte) 0x0a, (byte) 0x35,
// (byte) 0x2e, (byte) 0x35, (byte) 0x2e, (byte) 0x32, (byte) 0x2d, (byte) 0x6d,
// (byte) 0x32, (byte) 0x00,
// (byte) 0x0b, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x64, (byte) 0x76,
// (byte) 0x48, (byte) 0x40,
// (byte) 0x49, (byte) 0x2d, (byte) 0x43, (byte) 0x4a, (byte) 0x00, (byte) 0xff,
// (byte) 0xf7, (byte) 0x21,
// (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
// (byte) 0x00, (byte) 0x00,
// (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
// (byte) 0x00, (byte) 0x2a,
// (byte) 0x34, (byte) 0x64, (byte) 0x7c, (byte) 0x63, (byte) 0x5a, (byte) 0x77,
// (byte) 0x6b, (byte) 0x34,
// (byte) 0x5e, (byte) 0x5d, (byte) 0x3a, (byte) 0x00};
// final TestSyncBufferReader br = new TestSyncBufferReader(new
// ByteArrayInputStream(binary), utf8);
// final InitialHandshakeMessage initialHandshake =
// initialHandshakePacketUnmarshaller.decode(br);
// assertEquals(initialHandshake.protocolVersion, 10);
// assertEquals(initialHandshake.serverVersion, "5.5.2-m2");
// assertEquals(initialHandshake.connectionId, 11);
// assertEquals(initialHandshake.serverCapabilites, 0xf7ff);
// assertEquals(initialHandshake.statusFlag, 2);
// assertEquals(initialHandshake.seed.length, 20);
// final byte[] expectedSalt = {100, 118, 72, 64, 73, 45, 67, 74, 42, 52, 100,
// 124, 99, 90, 119, 107, 52, 94, 93,
// 58};
//
// assertTrue(Arrays.equals(expectedSalt, initialHandshake.seed));
// }
//
// @Test
// public void handshakeResponsePacket() {
// final byte[] salt = {70, 38, 43, 66, 74, 48, 79, 126, 76, 66, 70, 118, 67,
// 40, 63, 68, 120, 80, 103, 54};
// final HandshakeResponseMessage handshakeResponsePacketMessage = new
// HandshakeResponseMessage(1, "username",
// Optional.of("password"), Optional.empty(), "utf8", salt,
// "mysql_native_password");
// final ByteArrayOutputStream baos = new ByteArrayOutputStream();
// final TestSyncBufferWriter bw = new TestSyncBufferWriter(baos, utf8);
// handshakeResponsePacketMarshaller.encode(handshakeResponsePacketMessage, bw,
// utf8);
//
// final PacketBufferReader packet = new PacketBufferReader(
// new TestSyncBufferReader(new ByteArrayInputStream(baos.toByteArray()),
// utf8));
//
// final long capabilites = packet.readUnsignedInt();
// assertEquals(HandshakeResponsePacketMarshaller.BASE_CAPABILITIES,
// capabilites);
//
// final long max = packet.readUnsignedInt();
// assertEquals(255 * 255 * 255, max);
//
// final byte charset = packet.readByte();
// assertEquals(33, charset);
//
// packet.readBytes(23);
// final String username = packet.readCString();
// assertEquals("username", username);
//
// final byte[] expectedHashedPassword =
// HandshakeResponsePacketMarshaller.scramble411("password", salt, utf8);
// final int length = packet.readByte();
// final byte[] hashedPassword = packet.readBytes(length);
// assertEquals(expectedHashedPassword.length, hashedPassword.length);
// assertTrue(Arrays.equals(expectedHashedPassword, hashedPassword));
//
// }
//
// }
