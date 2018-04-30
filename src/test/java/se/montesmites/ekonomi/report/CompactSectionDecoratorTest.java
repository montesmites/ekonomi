package se.montesmites.ekonomi.report;

import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.Currency;

import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

class CompactSectionDecoratorTest {
    @Test
    void fundamentals() {
        var title = "TITLE";
        var values = Column.streamMonths().collect(toMap(month -> month, month -> Currency.of(month.ordinal())));
        var section = new DefaultSection(title, () -> Stream.of(new DefaultRowWithAmounts(title, values::get)));
        var decorator = new CompactSectionDecorator();
        var actualSection = decorator.decorate(section);
        var expectedSection =
                new _Section4TestUtil()
                        .addBodyRowWithAmounts(title.toUpperCase(), values::get)
                        .add(new EmptyRow());
        expectedSection.assertIsEqualTo(actualSection);
    }
}
