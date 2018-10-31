package io.trane.ndbc.postgres.value;

import java.time.OffsetTime;

public final class OffsetTimeArrayValue extends PostgresValue<OffsetTime[]> {

  public OffsetTimeArrayValue(final OffsetTime[] value) {
    super(value);
  }

  @Override
  public final OffsetTime[] getOffsetTimeArray() {
    return get();
  }
}