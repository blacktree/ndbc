package io.trane.ndbc.postgres.netty4;

import java.util.Arrays;
import java.util.List;

import io.trane.ndbc.test.ArrayEncodingTest;

public class PostgresArrayEncodingTest extends ArrayEncodingTest {

  public PostgresArrayEncodingTest() {
    super(PostgresConfig.instance);
  }

  protected List<String> bigDecimalColumnTypes() {
    return Arrays.asList("numeric[]");
  }

  protected List<String> booleanColumnTypes() {
    return Arrays.asList("bool[]");
  }

  protected List<String> byteArrayColumnTypes() {
    return Arrays.asList("bytea[]");
  }

  protected List<String> doubleColumnTypes() {
    return Arrays.asList("float8[]");
  }

  protected List<String> floatColumnTypes() {
    return Arrays.asList("float4[]");
  }

  protected List<String> integerColumnTypes() {
    return Arrays.asList("int4[]");
  }

  protected List<String> localDateColumnTypes() {
    return Arrays.asList("date[]");
  }

  protected List<String> localDateTimeColumnTypes() {
    return Arrays.asList("timestamp[]");
  }

  protected List<String> localTimeColumnTypes() {
    return Arrays.asList("time[]");
  }

  protected List<String> longColumnTypes() {
    return Arrays.asList("int8[]");
  }

  protected List<String> offsetTimeColumnTypes() {
    return Arrays.asList("timetz[]");
  }

  protected List<String> shortColumnTypes() {
    return Arrays.asList("int2[]");
  }

  protected List<String> byteColumnTypes() {
    return Arrays.asList("smallint[]");
  }

  protected List<String> stringColumnTypes() {
    return Arrays.asList("text[]", "varchar[]");
  }
}
