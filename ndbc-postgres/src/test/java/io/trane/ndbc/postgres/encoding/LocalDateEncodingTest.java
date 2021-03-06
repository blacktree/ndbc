package io.trane.ndbc.postgres.encoding;

import io.trane.ndbc.value.LocalDateValue;

public class LocalDateEncodingTest extends EncodingTest<LocalDateValue, LocalDateEncoding> {

  public LocalDateEncodingTest() {
    super(new LocalDateEncoding(UTF8), Oid.DATE, LocalDateValue.class,
        r -> new LocalDateValue(randomLocalDateTime(r).toLocalDate()));
  }
}
