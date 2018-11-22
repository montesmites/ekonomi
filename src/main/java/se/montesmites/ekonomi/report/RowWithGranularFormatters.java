package se.montesmites.ekonomi.report;

public interface RowWithGranularFormatters extends Row {
    @Override
    default String format(Column column) {
        switch (column.getColumnType()) {
            case DESCRIPTION:
                return formatDescription();
            case TOTAL:
                return formatTotal();
            case AVERAGE:
                return formatAverage();
            default:
                return formatMonth(column);
        }
    }

    default String formatDescription() {
        return "";
    }

    default String formatTotal() {
        return "";
    }

    default String formatMonth(Column column) {
        return "";
    }

    default String formatAverage() {
        return "";
    }
}
