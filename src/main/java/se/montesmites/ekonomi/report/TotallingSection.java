package se.montesmites.ekonomi.report;

import java.util.List;
import java.util.stream.Stream;

public class TotallingSection implements Section {

    private final String title;
    private final List<Section> sections;

    public TotallingSection(String title, List<Section> sections) {
        this.title = title;
        this.sections = sections;
    }

    @Override
    public TitleRow getTitle() {
        return new TitleRow(title);
    }

    @Override
    public HeaderRow getHeader() {
        return new HeaderRow();
    }

    @Override
    public FooterRow getFooter() {
        return new FooterRow(
                () -> sections.stream().flatMap(Section::streamBodyRows));
    }

    @Override
    public Stream<BodyRow> streamBodyRows() {
        return Stream.empty();
    }
}
