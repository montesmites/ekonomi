package se.montesmites.ekonomi.report;

import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.Currency;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static se.montesmites.ekonomi.report.HeaderRow.HeaderType.HEADER_TYPE_SHORT_MONTHS;

class CompactSectionDecoratorTest {
    @Test
    void fundamentals() {
        var title = "TITLE";
        var values = Column.streamMonths().collect(toMap(month -> month, month -> Currency.of(month.ordinal())));
        var bodyRows = (Supplier<Stream<Row>>) () -> Stream.of(new DefaultRowWithAmounts(title, values::get));
        var section = Section.of(() -> title, () -> HEADER_TYPE_SHORT_MONTHS, bodyRows, () -> bodyRows);
        var decorator = new CompactSectionDecorator();
        var actualSection = decorator.decorate(section);
        var expectedSection =
                new _Section4TestUtil()
                        .addBodyRowWithAmounts(title.toUpperCase(), values::get)
                        .add(new EmptyRow());
        expectedSection.assertIsEqualTo(actualSection);
    }
}
