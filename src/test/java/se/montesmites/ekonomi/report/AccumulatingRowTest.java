package se.montesmites.ekonomi.report;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.organization.Organization;
import testdata.DefaultTestDataExtension;
import testdata.OrganizationInjector;

import java.time.Year;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.montesmites.ekonomi.report.Column.*;

@ExtendWith(DefaultTestDataExtension.class)
class AccumulatingRowTest {
    private final Year year = Year.of(2012);

    private YearId yearId;
    @OrganizationInjector
    private Organization organization;
    private CashflowDataFetcher fetcher;

    @BeforeEach
    void before() {
        this.fetcher = new CashflowDataFetcher(this.organization);
        this.yearId = organization.getYear(year).get().getYearId();
    }

    @Test
    void fourAccounts() {
        AccumulatingNegatedRow row = new AccumulatingNegatedRow(
                fetcher,
                () -> Stream.of("1910", "1920", "1930", "1940")
                        .map(account -> new AccountId(yearId, account)),
                year
        );
        assertEquals(expectedAmounts().get(DESCRIPTION), row.getBalance());
        Column.streamMonths().forEach(month -> assertEquals(expectedAmounts().get(month), row.getMonthlyAmount(month), month.name()));
        assertEquals(new Currency(0), row.getYearlyTotal());
    }

    private Map<Column, Currency> expectedAmounts() {
        return new EnumMap< Column, Currency>(Column.class) {
            {
                put(DESCRIPTION, new Currency(127622521));
                put(JANUARY, new Currency(140745321));
                put(FEBRUARY, new Currency(140888401));
                put(MARCH, new Currency(185524301));
                put(APRIL, new Currency(117604301));
                put(MAY, new Currency(127820601));
                put(JUNE, new Currency(159100301));
                put(JULY, new Currency(196575843));
                put(AUGUST, new Currency(184371543));
                put(SEPTEMBER, new Currency(161756373));
                put(OCTOBER, new Currency(190541831));
                put(NOVEMBER, new Currency(207509032));
                put(DECEMBER, new Currency(241088832));
            }
        };
    }
}
