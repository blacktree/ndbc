package io.trane.ndbc.postgres.proto.unmarshaller;

import java.nio.charset.Charset;

import io.trane.ndbc.postgres.proto.Message.ParameterStatus;
import io.trane.ndbc.proto.BufferReader;;

public final class ParameterStatusUnmarshaller extends PostgresUnmarshaller<ParameterStatus> {

  public ParameterStatusUnmarshaller(final Charset charset) {
    super(charset);
  }

  @Override
  protected boolean acceptsType(final byte tpe) {
    return tpe == 'S';
  }

  @Override
  public final ParameterStatus decode(final byte tpe, final BufferReader b) {
    return new ParameterStatus(b.readCString(charset), b.readCString(charset));
  }
}
