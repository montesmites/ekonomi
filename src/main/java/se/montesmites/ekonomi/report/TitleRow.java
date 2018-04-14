package se.montesmites.ekonomi.report;

public interface TitleRow extends Row {
    
    String getTitle();

    @Override
    default String formatDescription() {
        return getTitle();
    }
}
