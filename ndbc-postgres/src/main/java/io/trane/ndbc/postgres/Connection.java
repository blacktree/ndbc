package io.trane.ndbc.postgres;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

import io.trane.future.Future;
import io.trane.future.InterruptHandler;
import io.trane.future.Promise;
import io.trane.future.Transformer;
import io.trane.ndbc.PreparedStatement;
import io.trane.ndbc.Row;
import io.trane.ndbc.postgres.proto.Message.BackendKeyData;
import io.trane.ndbc.postgres.proto.Message.CancelRequest;
import io.trane.ndbc.proto.Channel;
import io.trane.ndbc.proto.Exchange;
import io.trane.ndbc.value.Value;

public final class Connection implements io.trane.ndbc.datasource.Connection {

  private static final Logger                                           logger       = Logger
      .getLogger(Connection.class.getName());

  private static final PreparedStatement                                isValidQuery = PreparedStatement
      .apply("SELECT 1");

  private final Channel                                                 channel;
  private final Supplier<? extends Future<? extends Channel>>           channelSupplier;
  private final Optional<BackendKeyData>                                backendKeyData;
  private final Function<String, Exchange<List<Row>>>                   simpleQueryExchange;
  private final Function<String, Exchange<Integer>>                     simpleExecuteExchange;
  private final BiFunction<String, List<Value<?>>, Exchange<List<Row>>> extendedQueryExchange;
  private final BiFunction<String, List<Value<?>>, Exchange<Integer>>   extendedExecuteExchange;

  public Connection(final Channel channel,
      final Supplier<? extends Future<? extends Channel>> channelSupplier,
      final Optional<BackendKeyData> backendKeyData,
      final Function<String, Exchange<List<Row>>> simpleQueryExchange,
      final Function<String, Exchange<Integer>> simpleExecuteExchange,
      final BiFunction<String, List<Value<?>>, Exchange<List<Row>>> extendedQueryExchange,
      final BiFunction<String, List<Value<?>>, Exchange<Integer>> extendedExecuteExchange) {
    this.channel = channel;
    this.channelSupplier = channelSupplier;
    this.backendKeyData = backendKeyData;
    this.simpleQueryExchange = simpleQueryExchange;
    this.simpleExecuteExchange = simpleExecuteExchange;
    this.extendedQueryExchange = extendedQueryExchange;
    this.extendedExecuteExchange = extendedExecuteExchange;
  }

  @Override
  public final Future<List<Row>> query(final String query) {
    return run(simpleQueryExchange.apply(query));
  }

  @Override
  public final Future<Integer> execute(final String command) {
    return run(simpleExecuteExchange.apply(command));
  }

  @Override
  public final Future<List<Row>> query(final PreparedStatement query) {
    return run(extendedQueryExchange.apply(query.query(), query.params()));
  }

  @Override
  public final Future<Integer> execute(final PreparedStatement command) {
    return run(extendedExecuteExchange.apply(command.query(), command.params()));
  }

  @Override
  public final Future<Boolean> isValid() {
    return query(isValidQuery).map(r -> true).rescue(e -> Future.FALSE);
  }

  @Override
  public final Future<Void> close() {
    return Exchange.CLOSE.run(channel);
  }

  @Override
  public <R> Future<R> withTransaction(final Supplier<Future<R>> sup) {
    return execute("BEGIN").flatMap(v -> sup.get()).transformWith(new Transformer<R, Future<R>>() {
      @Override
      public Future<R> onException(final Throwable ex) {
        return execute("ROLLBACK").flatMap(v -> Future.exception(ex));
      }

      @Override
      public Future<R> onValue(final R value) {
        return execute("COMMIT").map(v -> value);
      }
    });
  }

  private final <T> Future<T> run(final Exchange<T> exchange) {
    return cancellable(exchange.run(channel));
  }

  private final <T> Future<T> cancellable(final Future<T> fut) {
    return backendKeyData.map(data -> {
      final Promise<T> p = Promise.create(v -> handler(v, data));
      p.become(fut);
      return (Future<T>) p;
    }).orElse(fut);
  }

  private final <T> InterruptHandler handler(final Promise<T> p, final BackendKeyData data) {
    return ex -> channelSupplier.get().flatMap(channel -> Exchange
        .send(new CancelRequest(data.processId, data.secretKey)).then(Exchange.CLOSE)
        .run(channel))
        .onFailure(e -> logger.warning("Can't cancel request. Reason: " + e))
        .ensure(() -> p.setException(ex));
  }
}
