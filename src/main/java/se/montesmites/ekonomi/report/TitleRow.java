package se.montesmites.ekonomi.report;

public interface TitleRow extends Row {
    
    public String getTitle();

    @Override
    default String formatDescription() {
        return getTitle();
    }
}
