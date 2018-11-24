package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.i18n.Messages;

import static se.montesmites.ekonomi.i18n.Messages.Message.HEADER_ROW_AVERAGE;
import static se.montesmites.ekonomi.i18n.Messages.Message.HEADER_ROW_TOTAL;

public interface HeaderRow extends RowWithGranularFormatters {
  HeaderRow SHORT_MONTHS_HEADER = column -> Messages.getShortMonth(column.getMonth().orElseThrow());

  @Override
  String formatMonth(Column column);

  @Override
  default String formatTotal() {
    return Messages.get(HEADER_ROW_TOTAL);
  }

  @Override
  default String formatAverage() {
    return Messages.get(HEADER_ROW_AVERAGE);
  }
}
