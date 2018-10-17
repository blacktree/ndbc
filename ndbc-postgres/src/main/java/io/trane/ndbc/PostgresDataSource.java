package io.trane.ndbc;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.trane.future.Future;

public class PostgresDataSource implements DataSource<PostgresPreparedStatement, PostgresRow> {

  public static PostgresDataSource fromSystemProperties(final String prefix) {
    return apply(io.trane.ndbc.DataSource.fromSystemProperties(prefix));
  }

  public static PostgresDataSource fromPropertiesFile(final String prefix,
      final String fileName)
      throws IOException {
    return apply(io.trane.ndbc.DataSource.fromPropertiesFile(prefix,
        fileName));
  }

  public static PostgresDataSource fromProperties(final String prefix, final Properties properties) {
    return apply(io.trane.ndbc.DataSource.fromProperties(prefix, properties));
  }

  public static PostgresDataSource fromConfig(final Config config) {
    return apply(DataSource.fromConfig(config));
  }

  public static PostgresDataSource apply(final DataSource<PreparedStatement, Row> ds) {
    return new PostgresDataSource(ds);
  }

  private final DataSource<PreparedStatement, Row> underlying;

  protected PostgresDataSource(final DataSource<PreparedStatement, Row> underlying) {
    this.underlying = underlying;
  }

  @Override
  public Future<List<PostgresRow>> query(final String query) {
    return conv(underlying.query(query));
  }

  @Override
  public Future<Long> execute(final String statement) {
    return underlying.execute(statement);
  }

  @Override
  public Future<List<PostgresRow>> query(final PostgresPreparedStatement query) {
    return conv(underlying.query(query));
  }

  @Override
  public Future<Long> execute(final PostgresPreparedStatement statement) {
    return underlying.execute(statement);
  }

  @Override
  public <T> Future<T> transactional(final Supplier<Future<T>> supplier) {
    return underlying.transactional(supplier);
  }

  @Override
  public TransactionalDataSource<PostgresPreparedStatement, PostgresRow> transactional() {
    return new TransactionalDataSource<PostgresPreparedStatement, PostgresRow>() {

      private final TransactionalDataSource<PreparedStatement, Row> underlying = PostgresDataSource.this.underlying
          .transactional();

      @Override
      public TransactionalDataSource<PostgresPreparedStatement, PostgresRow> transactional() {
        return this;
      }

      @Override
      public <T> Future<T> transactional(final Supplier<Future<T>> supplier) {
        return underlying.transactional(supplier);
      }

      @Override
      public Future<List<PostgresRow>> query(final PostgresPreparedStatement query) {
        return conv(underlying.query(query));
      }

      @Override
      public Future<List<PostgresRow>> query(final String query) {
        return conv(underlying.query(query));
      }

      @Override
      public Future<Long> execute(final PostgresPreparedStatement statement) {
        return underlying.execute(statement);
      }

      @Override
      public Future<Long> execute(final String statement) {
        return underlying.execute(statement);
      }

      @Override
      public Config config() {
        return underlying.config();
      }

      @Override
      public Future<Void> close() {
        return underlying.close();
      }

      @Override
      public Future<Void> rollback() {
        return underlying.rollback();
      }

      @Override
      public Future<Void> commit() {
        return underlying.commit();
      }
    };
  }

  @Override
  public Future<Void> close() {
    return underlying.close();
  }

  @Override
  public Config config() {
    return underlying.config();
  }

  private final Future<List<PostgresRow>> conv(final Future<List<Row>> f) {
    return f.map(l -> l.stream().map(PostgresRow::apply).collect(Collectors.toList()));
  }
}
