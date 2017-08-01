package se.montesmites.ekonomi.report;

import java.time.format.TextStyle;
import java.util.Locale;

public class HeaderRow implements Row {

    @Override
    public String getText(Column column) {
        switch (column.getColumnType()) {
            case DESCRIPTION:
                return "Description";
            case TOTAL:
                return "Total";
            default:
                return column.getMonth().get()
                        .getDisplayName(TextStyle.SHORT, Locale.UK);
        }
    }
}
