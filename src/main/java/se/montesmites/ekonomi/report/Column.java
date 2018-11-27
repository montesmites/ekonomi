package se.montesmites.ekonomi.report;

import java.time.Month;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public enum Column {
  DESCRIPTION(ColumnType.DESCRIPTION),
  JANUARY(ColumnType.MONTH),
  FEBRUARY(ColumnType.MONTH),
  MARCH(ColumnType.MONTH),
  APRIL(ColumnType.MONTH),
  MAY(ColumnType.MONTH),
  JUNE(ColumnType.MONTH),
  JULY(ColumnType.MONTH),
  AUGUST(ColumnType.MONTH),
  SEPTEMBER(ColumnType.MONTH),
  OCTOBER(ColumnType.MONTH),
  NOVEMBER(ColumnType.MONTH),
  DECEMBER(ColumnType.MONTH),
  TOTAL(ColumnType.TOTAL),
  AVERAGE(ColumnType.AVERAGE);

  public static Stream<Column> stream() {
    return Arrays.stream(values());
  }

  public static Stream<Column> streamMonths() {
    return stream().filter(col -> col.getColumnType() == ColumnType.MONTH);
  }

  public static Column valueOf(Month month) {
    return Column.valueOf(month.name());
  }

  private final ColumnType type;
  private final Optional<Month> month;

  Column(ColumnType type) {
    this.type = type;
    this.month = type.asMonth(this);
  }

  public ColumnType getColumnType() {
    return type;
  }

  public Optional<Month> getMonth() {
    return month;
  }
}
