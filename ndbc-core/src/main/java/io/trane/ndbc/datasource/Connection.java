package io.trane.ndbc.datasource;

import java.util.List;
import java.util.function.Supplier;

import io.trane.future.Future;
import io.trane.ndbc.PreparedStatement;
import io.trane.ndbc.Row;

public interface Connection {

  Future<Boolean> isValid();

  Future<Void> close();

  Future<List<Row>> query(String query);

  Future<Long> execute(String query);

  Future<List<Row>> query(PreparedStatement query);

  Future<Long> execute(PreparedStatement query);

  <R> Future<R> withTransaction(final Supplier<Future<R>> sup);
}
