package io.trane.ndbc.mysql.proto;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import io.trane.ndbc.value.Value;

public interface Message {

  interface ServerMessage extends io.trane.ndbc.proto.ServerMessage, Message {
  }

  interface ClientMessage extends io.trane.ndbc.proto.ClientMessage, Message {
  }

  public static class HandshakeResponseMessage implements ClientMessage {
    final public int              sequence;
    final public String           username;
    final public Optional<String> password;
    final public Optional<String> database;
    final public String           encoding;
    final public byte[]           seed;
    final public String           authenticationMethod;

    public HandshakeResponseMessage(final int sequence, final String username, final Optional<String> password,
        final Optional<String> database, final String encoding, final byte[] seed,
        final String authenticationMethod) {
      this.sequence = sequence;
      this.username = username;
      this.password = password;
      this.database = database;
      this.encoding = encoding;
      this.seed = seed;
      this.authenticationMethod = authenticationMethod;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o)
        return true;
      if ((o == null) || (getClass() != o.getClass()))
        return false;

      final HandshakeResponseMessage that = (HandshakeResponseMessage) o;

      if (!username.equals(that.username))
        return false;
      if (!password.equals(that.password))
        return false;
      if (!database.equals(that.database))
        return false;
      if (!encoding.equals(that.encoding))
        return false;
      if (!Arrays.equals(seed, that.seed))
        return false;
      return authenticationMethod.equals(that.authenticationMethod);
    }

    @Override
    public int hashCode() {
      int result = username.hashCode();
      result = (31 * result) + password.hashCode();
      result = (31 * result) + database.hashCode();
      result = (31 * result) + encoding.hashCode();
      result = (31 * result) + Arrays.hashCode(seed);
      result = (31 * result) + authenticationMethod.hashCode();
      return result;
    }

    @Override
    public String toString() {
      return "HandshakeResponseMessage{" + "sequence=" + sequence + ", username='" + username + '\''
          + ", password=" + password + ", database=" + database + ", encoding='" + encoding + '\'' + ", seed="
          + Arrays.toString(seed) + ", authenticationMethod='" + authenticationMethod + '\'' + '}';
    }
  }

  public static class Handshake implements ServerMessage {

    final public int    sequence;
    final public int    protocolVersion;
    final public String serverVersion;
    final public long   connectionId;
    final public byte[] seed;
    final public int    serverCapabilites;
    final public int    characterSet;
    final public int    statusFlag;
    final public String authenticationMethod;

    public Handshake(final int sequence, final int protocolVersion, final String serverVersion,
        final long connectionId, final byte[] seed, final int serverCapabilites, final int characterSet,
        final int statusFlag, final String authenticationMethod) {
      this.sequence = sequence;
      this.protocolVersion = protocolVersion;
      this.serverVersion = serverVersion;
      this.connectionId = connectionId;
      this.seed = seed;
      this.serverCapabilites = serverCapabilites;
      this.characterSet = characterSet;
      this.statusFlag = statusFlag;
      this.authenticationMethod = authenticationMethod;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o)
        return true;
      if ((o == null) || (getClass() != o.getClass()))
        return false;

      final Handshake that = (Handshake) o;

      if (protocolVersion != that.protocolVersion)
        return false;
      if (connectionId != that.connectionId)
        return false;
      if (serverCapabilites != that.serverCapabilites)
        return false;
      if (characterSet != that.characterSet)
        return false;
      if (statusFlag != that.statusFlag)
        return false;
      if (!serverVersion.equals(that.serverVersion))
        return false;
      if (!Arrays.equals(seed, that.seed))
        return false;
      return authenticationMethod.equals(that.authenticationMethod);
    }

    @Override
    public int hashCode() {
      int result = protocolVersion;
      result = (31 * result) + serverVersion.hashCode();
      result = (31 * result) + (int) (connectionId ^ (connectionId >>> 32));
      result = (31 * result) + Arrays.hashCode(seed);
      result = (31 * result) + serverCapabilites;
      result = (31 * result) + characterSet;
      result = (31 * result) + statusFlag;
      result = (31 * result) + authenticationMethod.hashCode();
      return result;
    }

    @Override
    public String toString() {
      return "InitialHandshakeMessage{" + "protocolVersion=" + protocolVersion + ", serverVersion='"
          + serverVersion + '\'' + ", connectionId=" + connectionId + ", seed=" + Arrays.toString(seed)
          + ", serverCapabilites=" + serverCapabilites + ", characterSet=" + characterSet + ", statusFlag="
          + statusFlag + ", authenticationMethod='" + authenticationMethod + '\'' + '}';
    }

  }

  public static interface Terminator extends ServerMessage {

  }

  public static class OkPacket implements Terminator {
    public long         affectedRows;
    public long         insertId;
    public ServerStatus serverStatus;
    public int          warningCount;
    public String       message;

    public OkPacket(final long affectedRows, final long insertId, final ServerStatus serverStatus,
        final int warningCount, final String message) {
      this.affectedRows = affectedRows;
      this.insertId = insertId;
      this.serverStatus = serverStatus;
      this.warningCount = warningCount;
      this.message = message;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o)
        return true;
      if ((o == null) || (getClass() != o.getClass()))
        return false;

      final OkPacket that = (OkPacket) o;

      if (affectedRows != that.affectedRows)
        return false;
      if (insertId != that.insertId)
        return false;
      if (serverStatus != that.serverStatus)
        return false;
      if (warningCount != that.warningCount)
        return false;
      return message.equals(that.message);
    }

    @Override
    public int hashCode() {
      int result = (int) (affectedRows ^ (affectedRows >>> 32));
      result = (31 * result) + (int) (insertId ^ (insertId >>> 32));
      result = (31 * result) + serverStatus.hashCode();
      result = (31 * result) + warningCount;
      result = (31 * result) + message.hashCode();
      return result;
    }

    @Override
    public String toString() {
      return "OkResponseMessage{" + "affectedRows=" + affectedRows + ", insertId=" + insertId + ", serverStatus="
          + serverStatus + ", warningCount=" + warningCount + ", message='" + message + '\'' + '}';
    }
  }

  public static class ErrPacketMessage implements Terminator {
    public String errorMessage;

    public ErrPacketMessage(final String errorMessage) {
      this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o)
        return true;
      if ((o == null) || (getClass() != o.getClass()))
        return false;

      final ErrPacketMessage that = (ErrPacketMessage) o;

      return errorMessage != null ? errorMessage.equals(that.errorMessage) : that.errorMessage == null;
    }

    @Override
    public int hashCode() {
      return errorMessage != null ? errorMessage.hashCode() : 0;
    }

    @Override
    public String toString() {
      return "ErrorResponseMessage{" + "errorMessage='" + errorMessage + '\'' + '}';
    }
  }

  public static class EofPacket implements Terminator {
    public int          warnings;
    public ServerStatus serverStatus;

    public EofPacket(final int warnings, final ServerStatus serverStatus) {
      this.warnings = warnings;
      this.serverStatus = serverStatus;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + serverStatus.hashCode();
      result = (prime * result) + warnings;
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
      final EofPacket other = (EofPacket) obj;
      if (serverStatus != other.serverStatus)
        return false;
      if (warnings != other.warnings)
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "EofPacket [warnings=" + warnings + ", serverStatus=" + serverStatus + "]";
    }
  }

  public static interface Command extends ClientMessage {

  }

  public static interface TextCommand extends Command {
    public byte getCommand();

    public String getSqlStatement();
  }

  public static class PrepareStatementCommand implements TextCommand {
    private final byte   command = (byte) 0x16;
    private final String sqlStatement;

    public PrepareStatementCommand(final String sqlStatement) {
      this.sqlStatement = sqlStatement;
    }

    @Override
    public byte getCommand() {
      return command;
    }

    @Override
    public String getSqlStatement() {
      return sqlStatement;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o)
        return true;
      if ((o == null) || (getClass() != o.getClass()))
        return false;

      final PrepareStatementCommand that = (PrepareStatementCommand) o;

      if (command != that.command)
        return false;
      return sqlStatement.equals(that.sqlStatement);
    }

    @Override
    public int hashCode() {
      int result = command;
      result = (31 * result) + sqlStatement.hashCode();
      return result;
    }

    @Override
    public String toString() {
      return "PrepareStatementCommand{" + "command=" + command + ", sqlStatement='" + sqlStatement + '\'' + '}';
    }
  }

  public static class PrepareOk implements Terminator {
    public final long statementId;
    public final int  numOfColumns;
    public final int  numOfParameters;
    public final int  warningCount;

    public PrepareOk(final long statementId, final int numOfColumns, final int numOfParameters,
        final int warningCount) {
      this.statementId = statementId;
      this.numOfColumns = numOfColumns;
      this.numOfParameters = numOfParameters;
      this.warningCount = warningCount;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o)
        return true;
      if ((o == null) || (getClass() != o.getClass()))
        return false;

      final PrepareOk that = (PrepareOk) o;

      if (statementId != that.statementId)
        return false;
      if (numOfColumns != that.numOfColumns)
        return false;
      if (numOfParameters != that.numOfParameters)
        return false;
      if (warningCount != that.warningCount)
        return false;
      return true;
    }

    @Override
    public int hashCode() {
      int result = (int) (statementId ^ (statementId >>> 32));
      result = (31 * result) + (numOfColumns ^ (numOfColumns >>> 32));
      result = (31 * result) + numOfParameters;
      result = (31 * result) + warningCount;
      return result;
    }

    @Override
    public String toString() {
      return "PrepareOk{" + "statementId=" + statementId + ", numOfColumns=" + numOfColumns + ", numOfParameters="
          + numOfParameters + ", warningsCount=" + warningCount + '}';
    }
  }

  public static class ExecuteStatementCommand implements Command {
    public final long           statementId;
    public final List<Value<?>> values;

    public ExecuteStatementCommand(final long statementId, final List<Value<?>> values) {
      this.statementId = statementId;
      this.values = values;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + (int) (statementId ^ (statementId >>> 32));
      result = (prime * result) + ((values == null) ? 0 : values.hashCode());
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
      final ExecuteStatementCommand other = (ExecuteStatementCommand) obj;
      if (statementId != other.statementId)
        return false;
      if (values == null) {
        if (other.values != null)
          return false;
      } else if (!values.equals(other.values))
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "ExecuteStatementCommand [statementId=" + statementId + ", values=" + values + "]";
    }
  }

  public static class CloseStatementCommand implements Command {
    public final byte command = (byte) 0x19;
    public final long statementId;

    public CloseStatementCommand(final long statementId) {
      this.statementId = statementId;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + command;
      result = (prime * result) + (int) (statementId ^ (statementId >>> 32));
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
      final CloseStatementCommand other = (CloseStatementCommand) obj;
      if (command != other.command)
        return false;
      if (statementId != other.statementId)
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "CloseStatementCommand [command=" + command + ", statementId=" + statementId + "]";
    }
  }

  public static class QueryCommand implements TextCommand {
    private final byte   command = (byte) 0x03;
    private final String sqlStatement;

    public QueryCommand(final String sqlStatement) {
      this.sqlStatement = sqlStatement;
    }

    @Override
    public byte getCommand() {
      return command;
    }

    @Override
    public String getSqlStatement() {
      return sqlStatement;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o)
        return true;
      if ((o == null) || (getClass() != o.getClass()))
        return false;

      final QueryCommand that = (QueryCommand) o;

      if (command != that.command)
        return false;
      return sqlStatement.equals(that.sqlStatement);
    }

    @Override
    public int hashCode() {
      int result = command;
      result = (31 * result) + sqlStatement.hashCode();
      return result;
    }

    @Override
    public String toString() {
      return "QueryCommand{" + "command=" + command + ", sqlStatement='" + sqlStatement + '\'' + '}';
    }
  }

  public static class StatementCommand implements TextCommand {
    private final byte   command = (byte) 0x03;
    private final String sqlStatement;

    public StatementCommand(final String sqlStatement) {
      this.sqlStatement = sqlStatement;
    }

    @Override
    public byte getCommand() {
      return command;
    }

    @Override
    public String getSqlStatement() {
      return sqlStatement;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o)
        return true;
      if ((o == null) || (getClass() != o.getClass()))
        return false;

      final StatementCommand that = (StatementCommand) o;

      if (command != that.command)
        return false;
      return sqlStatement.equals(that.sqlStatement);
    }

    @Override
    public int hashCode() {
      int result = command;
      result = (31 * result) + sqlStatement.hashCode();
      return result;
    }

    @Override
    public String toString() {
      return "StatementCommand{" + "command=" + command + ", sqlStatement='" + sqlStatement + '\'' + '}';
    }
  }

  public static class Field implements ServerMessage {
    public String    catalog;
    public String    db;
    public String    table;
    public String    origTable;
    public String    name;
    public String    origName;
    public Charset   charset;
    public long      displayLength;
    public FieldType fieldType;
    public int       flags;
    public int       decimals;

    public Field(final String catalog, final String db, final String table, final String origTable,
        final String name, final String origName, final Charset charset, final long displayLength,
        final FieldType fieldType, final int flags, final int decimals) {
      this.catalog = catalog;
      this.db = db;
      this.table = table;
      this.origTable = origTable;
      this.name = name;
      this.origName = origName;
      this.charset = charset;
      this.displayLength = displayLength;
      this.fieldType = fieldType;
      this.flags = flags;
      this.decimals = decimals;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o)
        return true;
      if ((o == null) || (getClass() != o.getClass()))
        return false;

      final Field field = (Field) o;

      if (charset != field.charset)
        return false;
      if (displayLength != field.displayLength)
        return false;
      if (fieldType != field.fieldType)
        return false;
      if (flags != field.flags)
        return false;
      if (decimals != field.decimals)
        return false;
      if (!catalog.equals(field.catalog))
        return false;
      if (!db.equals(field.db))
        return false;
      if (!table.equals(field.table))
        return false;
      if (!origTable.equals(field.origTable))
        return false;
      if (!name.equals(field.name))
        return false;
      return origName.equals(field.origName);
    }

    @Override
    public int hashCode() {
      int result = catalog.hashCode();
      result = (31 * result) + db.hashCode();
      result = (31 * result) + table.hashCode();
      result = (31 * result) + origTable.hashCode();
      result = (31 * result) + name.hashCode();
      result = (31 * result) + origName.hashCode();
      result = (31 * result) + charset.hashCode();
      result = (31 * result) + (int) (displayLength ^ (displayLength >>> 32));
      result = (31 * result) + fieldType.hashCode();
      result = (31 * result) + flags;
      result = (31 * result) + decimals;
      return result;
    }

    @Override
    public String toString() {
      return "Field{" + "catalog='" + catalog + '\'' + ", db='" + db + '\'' + ", table='" + table + '\''
          + ", origTable='" + origTable + '\'' + ", name='" + name + '\'' + ", origName='" + origName + '\''
          + ", charset=" + charset + ", displayLength=" + displayLength + ", fieldType=" + fieldType
          + ", flags=" + flags + ", decimals=" + decimals + '}';
    }
  }

  public static class Row implements ServerMessage {
    public Value<?>[] values;

    public Row(final Value<?>[] values) {
      this.values = values;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + Arrays.hashCode(values);
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
      final Row other = (Row) obj;
      if (!Arrays.equals(values, other.values))
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "TextRow [values=" + Arrays.toString(values) + "]";
    }
  }

  public final static class ColumnCount implements ServerMessage {
    public final long count;

    public ColumnCount(final long count) {
      this.count = count;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + (int) (count ^ (count >>> 32));
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
      final ColumnCount other = (ColumnCount) obj;
      if (count != other.count)
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "ColumnCount [count=" + count + "]";
    }
  }
}
