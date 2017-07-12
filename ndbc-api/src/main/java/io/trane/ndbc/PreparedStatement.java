package io.trane.ndbc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.trane.ndbc.value.BigDecimalValue;
import io.trane.ndbc.value.BooleanValue;
import io.trane.ndbc.value.ByteArrayValue;
import io.trane.ndbc.value.DoubleValue;
import io.trane.ndbc.value.FloatValue;
import io.trane.ndbc.value.IntegerValue;
import io.trane.ndbc.value.LocalDateTimeValue;
import io.trane.ndbc.value.LocalDateValue;
import io.trane.ndbc.value.LocalTimeValue;
import io.trane.ndbc.value.LongValue;
import io.trane.ndbc.value.OffsetTimeValue;
import io.trane.ndbc.value.ShortValue;
import io.trane.ndbc.value.StringValue;
import io.trane.ndbc.value.Value;

public final class PreparedStatement {

  private static final Value<?>[] emptyValues = new Value<?>[0];

  public static final PreparedStatement apply(final String query) {
    return new PreparedStatement(query, emptyValues);
  }

  private final String     query;
  private final Value<?>[] params;

  private PreparedStatement(final String query, final Value<?>[] params) {
    super();
    this.query = query;
    this.params = params;
  }

  public final PreparedStatement setBigDecimal(final BigDecimal value) {
    return setBigDecimal(params.length, value);
  }

  public final PreparedStatement setBigDecimal(final int index, final BigDecimal value) {
    return set(index, value == null ? Value.NULL : new BigDecimalValue(value));
  }

  public final PreparedStatement setBoolean(final Boolean value) {
    return set(value == null ? Value.NULL : new BooleanValue(value));
  }

  public final PreparedStatement setByteArray(final byte[] value) {
    return set(value == null ? Value.NULL : new ByteArrayValue(value));
  }

  public final PreparedStatement setDouble(final Double value) {
    return set(value == null ? Value.NULL : new DoubleValue(value));
  }

  public final PreparedStatement setFloat(final Float value) {
    return set(value == null ? Value.NULL : new FloatValue(value));
  }

  public final PreparedStatement setInteger(final Integer value) {
    return set(value == null ? Value.NULL : new IntegerValue(value));
  }

  public final PreparedStatement setLocalDate(final LocalDate value) {
    return set(value == null ? Value.NULL : new LocalDateValue(value));
  }

  public final PreparedStatement setLocalDateTime(final LocalDateTime value) {
    return set(value == null ? Value.NULL : new LocalDateTimeValue(value));
  }

  public final PreparedStatement setLocalTime(final LocalTime value) {
    return set(value == null ? Value.NULL : new LocalTimeValue(value));
  }

  public final PreparedStatement setLong(final Long value) {
    return set(value == null ? Value.NULL : new LongValue(value));
  }

  public final PreparedStatement setOffsetTime(final OffsetTime value) {
    return set(value == null ? Value.NULL : new OffsetTimeValue(value));
  }

  public final PreparedStatement setShort(final Short value) {
    return set(value == null ? Value.NULL : new ShortValue(value));
  }

  public final PreparedStatement setString(final String value) {
    return set(value == null ? Value.NULL : new StringValue(value));
  }

  public final PreparedStatement setNull() {
    return set(Value.NULL);
  }

  public final PreparedStatement set(final Value<?> param) {
    return set(params.length, param);
  }

  public final PreparedStatement set(final int index, final Value<?> param) {
    final Value<?>[] newParams;
    if (index >= params.length)
      newParams = Arrays.copyOf(params, index + 1);
    else
      newParams = Arrays.copyOf(params, params.length);
    newParams[params.length] = param;
    return new PreparedStatement(query, newParams);
  }

  public final String query() {
    return query;
  }

  public final List<Value<?>> params() {
    return Collections.unmodifiableList(Arrays.asList(params));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(params);
    result = prime * result + ((query == null) ? 0 : query.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final PreparedStatement other = (PreparedStatement) obj;
    if (!Arrays.equals(params, other.params))
      return false;
    if (query == null) {
      if (other.query != null)
        return false;
    } else if (!query.equals(other.query))
      return false;
    return true;
  }
}
