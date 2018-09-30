package io.trane.ndbc.postgres.encoding;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Assert;

import io.trane.ndbc.postgres.value.BigDecimalArrayValue;

public class BigDecimalArrayEncodingTest extends EncodingTest<BigDecimalArrayValue, BigDecimalArrayEncoding> {

  public BigDecimalArrayEncodingTest() {
    super(new BigDecimalArrayEncoding(new BigDecimalEncoding(UTF8), UTF8), Oid.NUMERIC_ARRAY,
        BigDecimalArrayValue.class, r -> {
          final BigDecimal[] bigDecimals = new BigDecimal[r.nextInt(8)];
          Arrays.setAll(bigDecimals, p -> BigDecimal.valueOf(r.nextDouble()));

          return new BigDecimalArrayValue(bigDecimals);
        }, (a, b) -> Assert.assertArrayEquals(a.getBigDecimalArray(), b.getBigDecimalArray()));
  }
}
