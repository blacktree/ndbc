package io.trane.ndbc.postgres.netty4;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import io.trane.future.Future;
import io.trane.ndbc.NdbcException;
import io.trane.ndbc.Row;
import io.trane.ndbc.test.DataSourceTest;

public class PostgresDataSourceTest extends DataSourceTest {

  @Parameters(name = "{1}")
  public static Collection<Object[]> data() {
    return PostgresEnv.dataSources;
  }

  public PostgresDataSourceTest() {
    super("varchar");
  }

  @Test(expected = NdbcException.class)
  public void cancellation() throws Throwable {
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    try {
      final Future<List<Row>> f = ds.query("SELECT pg_sleep(999)");
      f.raise(new Exception(""));
      f.get(timeout);
    } finally {
      scheduler.shutdown();
    }
  }
}
