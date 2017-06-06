package io.trane.ndbc.postgres.proto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.trane.ndbc.ResultSet;
import io.trane.ndbc.Row;
import io.trane.ndbc.postgres.encoding.Format;
import io.trane.ndbc.postgres.encoding.ValueEncoding;
import io.trane.ndbc.postgres.proto.Message.CommandComplete;
import io.trane.ndbc.postgres.proto.Message.DataRow;
import io.trane.ndbc.postgres.proto.Message.EmptyQueryResponse;
import io.trane.ndbc.postgres.proto.Message.RowDescription;
import io.trane.ndbc.proto.BufferReader;
import io.trane.ndbc.proto.Exchange;
import io.trane.ndbc.proto.ServerMessage;
import io.trane.ndbc.util.PartialFunction;
import io.trane.ndbc.value.Value;

public final class QueryResultExchange {

  private final ValueEncoding encoding;

  public QueryResultExchange(final ValueEncoding encoding) {
    super();
    this.encoding = encoding;
  }

  public final Exchange<ResultSet> apply() {
    return Exchange.receive(rowDescription)
        .flatMap(desc -> gatherDataRows(new ArrayList<>()).map(rows -> toResultSet(desc, rows)));
  }

  private final Row toRow(final ValueEncoding encoding, final RowDescription desc, final DataRow data) {

    final RowDescription.Field[] fields = desc.fields;
    final BufferReader[] values = data.values;

    final int length = fields.length;
    final Map<String, Integer> positions = new HashMap<>(length);
    final Value<?>[] columns = new Value<?>[length];

    for (int i = 0; i < length; i++) {
      final RowDescription.Field field = fields[i];
      positions.put(field.name, i);
      final BufferReader reader = values[i];
      if (reader == null)
        columns[i] = Value.NULL;
      else
        columns[i] = encoding.decode(field.dataType, Format.fromCode(field.formatCode), reader);
      reader.release();
    }

    return Row.apply(positions, columns);
  }

  private final Exchange<List<DataRow>> gatherDataRows(final List<DataRow> rows) {
    return Exchange.receive(PartialFunction.<ServerMessage, Exchange<List<DataRow>>>apply()
        .orElse(EmptyQueryResponse.class, msg -> Exchange.value(rows))
        .orElse(CommandComplete.class, msg -> Exchange.value(rows)).orElse(DataRow.class, row -> {
          rows.add(row);
          return gatherDataRows(rows);
        }));
  }

  private final ResultSet toResultSet(final RowDescription desc, final List<DataRow> dataRows) {
    final int size = dataRows.size();
    final List<Row> rows = new ArrayList<>(size);
    for (DataRow dr : dataRows) 
      rows.add(toRow(encoding, desc, dr));
    return new ResultSet(rows);
  }

  private final PartialFunction<ServerMessage, Exchange<RowDescription>> rowDescription = PartialFunction
      .when(RowDescription.class, msg -> Exchange.value(msg));

}
