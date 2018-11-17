package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.i18n.Messages;

import static se.montesmites.ekonomi.i18n.Messages.Message.HEADER_ROW_AVERAGE;
import static se.montesmites.ekonomi.i18n.Messages.Message.HEADER_ROW_TOTAL;

public interface HeaderRow extends Row {

    @Override
    default String formatMonth(Column column) {
        return Messages.getShortMonth(column.getMonth().orElseThrow());
    }

    @Override
    default String formatTotal() {
        return Messages.get(HEADER_ROW_TOTAL);
    }

    @Override
    default String formatAverage() {
        return Messages.get(HEADER_ROW_AVERAGE);
    }
}
