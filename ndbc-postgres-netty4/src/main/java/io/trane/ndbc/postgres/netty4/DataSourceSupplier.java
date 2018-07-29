package io.trane.ndbc.postgres.netty4;

import java.util.function.Function;
import java.util.function.Supplier;

import io.trane.future.Future;
import io.trane.ndbc.Config;
import io.trane.ndbc.datasource.Connection;
import io.trane.ndbc.netty4.InitSSLHandler;
import io.trane.ndbc.netty4.Netty4DataSourceSupplier;
import io.trane.ndbc.netty4.NettyChannel;
import io.trane.ndbc.postgres.encoding.EncodingRegistry;
import io.trane.ndbc.postgres.proto.ExtendedExchange;
import io.trane.ndbc.postgres.proto.ExtendedExecuteExchange;
import io.trane.ndbc.postgres.proto.ExtendedQueryExchange;
import io.trane.ndbc.postgres.proto.InitSSLExchange;
import io.trane.ndbc.postgres.proto.QueryResultExchange;
import io.trane.ndbc.postgres.proto.SimpleExecuteExchange;
import io.trane.ndbc.postgres.proto.SimpleQueryExchange;
import io.trane.ndbc.postgres.proto.StartupExchange;
import io.trane.ndbc.postgres.proto.marshaller.BindMarshaller;
import io.trane.ndbc.postgres.proto.marshaller.CancelRequestMarshaller;
import io.trane.ndbc.postgres.proto.marshaller.CloseMarshaller;
import io.trane.ndbc.postgres.proto.marshaller.DescribeMarshaller;
import io.trane.ndbc.postgres.proto.marshaller.ExecuteMarshaller;
import io.trane.ndbc.postgres.proto.marshaller.FlushMarshaller;
import io.trane.ndbc.postgres.proto.marshaller.ParseMarshaller;
import io.trane.ndbc.postgres.proto.marshaller.PasswordMessageMarshaller;
import io.trane.ndbc.postgres.proto.marshaller.PostgresMarshaller;
import io.trane.ndbc.postgres.proto.marshaller.QueryMarshaller;
import io.trane.ndbc.postgres.proto.marshaller.SSLRequestMarshaller;
import io.trane.ndbc.postgres.proto.marshaller.StartupMessageMarshaller;
import io.trane.ndbc.postgres.proto.marshaller.SyncMarshaller;
import io.trane.ndbc.postgres.proto.marshaller.TerminateMarshaller;
import io.trane.ndbc.postgres.proto.unmarshaller.PostgresUnmarshaller;
import io.trane.ndbc.proto.Marshaller;

public final class DataSourceSupplier extends Netty4DataSourceSupplier {

  private final static StartupExchange startup         = new StartupExchange();
  private final static InitSSLExchange initSSLExchange = new InitSSLExchange();
  private final static InitSSLHandler  initSSLHandler  = new InitSSLHandler();

  public DataSourceSupplier(final Config config) {
    super(config, createMarshaller(config), new PostgresUnmarshaller(), createConnection(config));
  }

  private static final Marshaller createMarshaller(Config config) {
    EncodingRegistry encoding = new EncodingRegistry(config.loadCustomEncodings());
    return new PostgresMarshaller(new BindMarshaller(encoding), new CancelRequestMarshaller(),
        new CloseMarshaller(), new DescribeMarshaller(), new ExecuteMarshaller(), new FlushMarshaller(),
        new ParseMarshaller(encoding), new QueryMarshaller(), new PasswordMessageMarshaller(),
        new StartupMessageMarshaller(), new SyncMarshaller(), new TerminateMarshaller(),
        new SSLRequestMarshaller());
  }

  private static Function<Supplier<Future<NettyChannel>>, Supplier<Future<Connection>>> createConnection(
      Config config) {
    EncodingRegistry encoding = new EncodingRegistry(config.loadCustomEncodings());
    final QueryResultExchange queryResultExchange = new QueryResultExchange(encoding);
    return (channelSupplier) -> () -> {
      final ExtendedExchange extendedExchange = new ExtendedExchange();
      return channelSupplier.get().flatMap(channel -> initSSLExchange.apply(config.ssl()).run(channel)
          .flatMap(ssl -> initSSLHandler.apply(config.host(), config.port(), ssl, channel))
          .flatMap(v -> startup.apply(config.charset(), config.user(), config.password(), config.database())
              .run(channel)
              .map(backendKeyData -> new io.trane.ndbc.postgres.Connection(channel, channelSupplier,
                  backendKeyData, new SimpleQueryExchange(queryResultExchange),
                  new SimpleExecuteExchange(),
                  new ExtendedQueryExchange(queryResultExchange, extendedExchange),
                  new ExtendedExecuteExchange(extendedExchange)))));
    };
  }
}
