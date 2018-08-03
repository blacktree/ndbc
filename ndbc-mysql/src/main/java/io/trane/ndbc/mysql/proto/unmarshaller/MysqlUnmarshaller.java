// package io.trane.ndbc.mysql.proto.unmarshaller;
//
// import java.util.Optional;
//
// import io.trane.ndbc.mysql.proto.Message.ExecuteStatementCommand;
// import io.trane.ndbc.mysql.proto.Message.HandshakeResponseMessage;
// import io.trane.ndbc.mysql.proto.Message.PrepareStatementCommand;
// import io.trane.ndbc.mysql.proto.Message.QueryCommand;
// import io.trane.ndbc.mysql.proto.Message.StatementCommand;
// import io.trane.ndbc.proto.BufferReader;
// import io.trane.ndbc.proto.ServerMessage;
// import io.trane.ndbc.proto.Unmarshaller;
// import io.trane.ndbc.util.Try;
//
// public class MysqlUnmarshaller implements Unmarshaller {
// private final InitialHandshakePacketUnmarshaller
// initialHandshakePacketUnmarshaller = new
// InitialHandshakePacketUnmarshaller();
// private final ServerResponseUnmarshaller serverResponseUnmarshaller = new
// ServerResponseUnmarshaller();
// private final TextResultSetUnmarshaller textResultSetUnmarshaller = new
// TextResultSetUnmarshaller();
// private final PrepareStatementOkUnmarshaller prepareStatementOkUnmarshaller =
// new PrepareStatementOkUnmarshaller();
//
// @Override
// public Optional<Try<ServerMessage>> decode(
// final Optional<Class<? extends io.trane.ndbc.proto.ClientMessage>>
// previousClientMessageClass,
// final BufferReader b) {
// return Optional.of(previousClientMessageClass.<Try<ServerMessage>>map(p -> {
// if (HandshakeResponseMessage.class.isAssignableFrom(p)) {
// return Try.apply(() -> serverResponseUnmarshaller.decode(b));
// } else if (QueryCommand.class.isAssignableFrom(p)) {
// return Try.apply(() -> textResultSetUnmarshaller.decode(b));
// } else if (StatementCommand.class.isAssignableFrom(p)) {
// return Try.apply(() -> serverResponseUnmarshaller.decode(b));
// } else if (PrepareStatementCommand.class.isAssignableFrom(p)) {
// return Try.apply(() -> prepareStatementOkUnmarshaller.decode(b));
// } else if (ExecuteStatementCommand.class.isAssignableFrom(p)) {
// return Try.apply(() -> serverResponseUnmarshaller.decode(b));
// } else {
// return Try.failure(new IllegalStateException("Unknown message"));
// }
// }).orElseGet(() -> {
// return Try.apply(() -> initialHandshakePacketUnmarshaller.decode(b));
// })); // TODO review
// }
// }
