package se.montesmites.ekonomi.report;

public class DefaultTitleRow implements TitleRow {

    private final String title;

    public DefaultTitleRow(String title) {
        this.title = title.toUpperCase();
    }

    @Override
    public String getTitle() {
        return title;
    }
}
