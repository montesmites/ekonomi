package se.montesmites.ekonomi.report;

public class EmptyRow implements Row {
    
    @Override
    public String getText(Column column) {
        return "";
    }

}
