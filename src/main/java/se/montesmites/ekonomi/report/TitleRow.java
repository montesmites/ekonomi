package se.montesmites.ekonomi.report;

public class TitleRow implements Row {
    private final String title;
    
    public TitleRow(String title) {
        this.title = title.toUpperCase();
    }
    
    @Override
    public String getText(Column column) {
        switch (column.getColumnType()) {
            case DESCRIPTION: return title;
            default: return "";
        }
    }
}
