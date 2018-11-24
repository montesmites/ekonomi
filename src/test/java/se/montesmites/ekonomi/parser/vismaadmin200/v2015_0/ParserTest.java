package se.montesmites.ekonomi.parser.vismaadmin200.v2015_0;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.montesmites.ekonomi.model.Year;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.parser.vismaadmin200.BinaryFile_VismaAdmin200;
import se.montesmites.ekonomi.parser.vismaadmin200.Parser;
import testdata.DefaultTestDataExtension;
import testdata.ParserInjector;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(DefaultTestDataExtension.class)
class ParserTest {
    @ParserInjector
    private Parser parser;

    @Test
    void parseYears() {
        Set<Year> exp =
                Set.of(
                        year("C", 2012, "2012-01-01", "2012-12-31"),
                        year("D", 2013, "2013-01-01", "2013-12-31"),
                        year("E", 2014, "2014-01-01", "2014-12-31"),
                        year("F", 2015, "2015-01-01", "2015-12-31"));
        Set<Year> act = set(parse(BinaryFile_2015_0.YEARS));
        assertEquals(exp, act);
    }

    @Test
    void parseAccounts() {
        final Map<String, Long> expCount =
                Map.of("C", (long) 525, "D", (long) 526, "E", (long) 536, "F", (long) 590);
        final Map<String, Long> actCount =
                parse(BinaryFile_2015_0.ACCOUNTS)
                        .collect(
                                Collectors.groupingBy(
                                        account -> account.getAccountId().getYearId().getId(), Collectors.counting()));
        assertEquals(expCount.entrySet(), actCount.entrySet());
    }

    @Test
    void parseEvents() {
        final Map<String, Long> expCount =
                Map.of("A", (long) 1, "C", (long) 272, "D", (long) 310, "E", (long) 295, "F", (long) 62);
        final Map<String, Long> actCount =
                parse(BinaryFile_2015_0.EVENTS)
                        .collect(
                                Collectors.groupingBy(
                                        event -> event.getEventId().getYearId().getId(), Collectors.counting()));
        assertEquals(expCount.entrySet(), actCount.entrySet());
    }

    @Test
    void parseEntries() {
        final Map<String, Long> expCount =
                Map.of("C", (long) 1313, "D", (long) 1404, "E", (long) 1344, "F", (long) 218);
        final Map<String, Long> actCount =
                parse(BinaryFile_2015_0.ENTRIES)
                        .collect(
                                Collectors.groupingBy(
                                        entry -> entry.getEventId().getYearId().getId(), Collectors.counting()));
        assertEquals(expCount.entrySet(), actCount.entrySet());
    }

    @Test
    void parseBalances() {
        final Map<String, Long> expCount =
                Map.of("C", (long) 33, "D", (long) 34, "E", (long) 33, "F", (long) 27);
        final Map<String, Long> actCount =
                parse(BinaryFile_2015_0.BALANCES)
                        .collect(
                                Collectors.groupingBy(
                                        balance -> balance.getAccountId().getYearId().getId(), Collectors.counting()));
        assertEquals(expCount.entrySet(), actCount.entrySet());
    }

    private <T> Stream<T> parse(BinaryFile_VismaAdmin200<T> bf) {
        return parser.parse(bf);
    }

    private Year year(String id, int year, String from, String to) {
        return new Year(
                new YearId(id), java.time.Year.of(year), LocalDate.parse(from), LocalDate.parse(to));
    }

    private <T> Set<T> set(Stream<T> l) {
        return l.collect(toSet());
    }
}
