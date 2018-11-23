package se.montesmites.ekonomi.report;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;
import static se.montesmites.ekonomi.report.HeaderRow.HeaderType.HEADER_TYPE_SHORT_MONTHS;

class HeaderTest {
    @Test
    void emptyHeader() {
        var header = Header.empty();
        var exp = List.<Row>of();
        var act = header.stream().collect(toList());
        assertHeaders(exp, act);
    }

    @Test
    void titleRow() {
        var title = "title";
        var header = Header.of(() -> title);
        var exp = List.of((TitleRow) () -> title);
        var act = header.stream().collect(toList());
        assertHeaders(exp, act);
    }

    @Test
    void headerRow() {
        var header = Header.of(() -> HEADER_TYPE_SHORT_MONTHS);
        var exp = List.of((HeaderRow) () -> HEADER_TYPE_SHORT_MONTHS);
        var act = header.stream().collect(toList());
        assertHeaders(exp, act);
    }

    @Test
    void bothTitleAndHeader() {
        var title = "title";
        var header = Header.of(() -> title).add((HeaderRow) () -> HEADER_TYPE_SHORT_MONTHS);
        var exp = List.of((TitleRow) () -> title, (HeaderRow) () -> HEADER_TYPE_SHORT_MONTHS);
        var act = header.stream().collect(toList());
        assertHeaders(exp, act);
    }

    @Test
    void combineTitleAndHeader() {
        var titleRow = (TitleRow) () -> "title";
        var headerRow = (HeaderRow) () -> HEADER_TYPE_SHORT_MONTHS;
        var combined = RowMerger.template(headerRow).merge(DESCRIPTION, titleRow).asRow();
        var header = Header.of(combined);
        var exp = List.of(combined);
        var act = header.stream().collect(toList());
        assertHeaders(exp, act);
    }

    private void assertHeaders(List<? extends Row> exp, List<? extends Row> act) {
        assertAll(
                () -> assertEquals(exp.size(), act.size()),
                () -> assertAll(
                        () -> range(0, exp.size())
                                .forEach(i -> exp.get(i).isEquivalentTo(act.get(i)))));
    }
}
