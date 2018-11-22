package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.i18n.Messages;

import java.util.function.Function;

import static se.montesmites.ekonomi.i18n.Messages.Message.HEADER_ROW_AVERAGE;
import static se.montesmites.ekonomi.i18n.Messages.Message.HEADER_ROW_TOTAL;

public interface HeaderRow extends RowWithGranularFormatters {
    enum HeaderType {
        HEADER_TYPE_SHORT_MONTHS(column -> Messages.getShortMonth(column.getMonth().orElseThrow()));

        private final Function<Column, String> format;

        HeaderType(Function<Column, String> format) {
            this.format = format;
        }

        final String format(Column column) {
            return format.apply(column);
        }
    }

    HeaderType getHeaderType();

    @Override
    default String formatMonth(Column column) {
        return getHeaderType().format(column);
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
