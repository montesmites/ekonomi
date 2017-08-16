package se.montesmites.ekonomi.report;

public interface TitleRow extends Row {
    
    public String getTitle();
    
    @Override
    default String getText(Column column) {
        switch (column.getColumnType()) {
            case DESCRIPTION: return getTitle();
            default: return "";
        }
    }
}
