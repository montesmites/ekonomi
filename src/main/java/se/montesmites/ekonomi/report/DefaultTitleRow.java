package se.montesmites.ekonomi.report;

public class DefaultTitleRow implements TitleRow {

    private final String title;

    DefaultTitleRow(String title) {
        this.title = title.toUpperCase();
    }

    @Override
    public String getTitle() {
        return title;
    }
}
