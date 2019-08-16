package se.montesmites.ekonomi.sie;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.sie.file.SieToOrganizationConverter;

class SieToOrganizationConverterTest {

  private static final String PATH_TO_SIE_FILE = "/se/montesmites/ekonomi/sie/OVNINGSB.SE";
  private Path pathToSieFile;

  @BeforeEach
  void beforeEach() throws Exception {
    pathToSieFile = Paths.get(Sie4FileReaderTest.class.getResource(PATH_TO_SIE_FILE).toURI());
  }

  @Test
  void organization() {
    var converter = SieToOrganizationConverter.of();
    var organization = converter.convert(pathToSieFile);
    var years =
        organization.streamYears().collect(toMap(year -> year.getYear().getValue(), year -> year));
    var balances =
        organization
            .streamBalances()
            .collect(groupingBy(balance -> balance.getAccountId().getYearId()));
    var accounts = organization.streamAccounts().collect(toList());
    var events = organization.streamEvents().collect(toList());
    var entries = organization.streamEntries().collect(toList());
    assertEquals(2, years.size());
    assertEquals(26, balances.get(years.get(2014).getYearId()).size());
    assertEquals(26, balances.get(years.get(2015).getYearId()).size());
    assertEquals(590, accounts.size());
    assertEquals(62, events.size());
    assertEquals(218, entries.size());
    assertEquals(
        Currency.zero(),
        entries.stream().map(Entry::getAmount).reduce(Currency.zero(), Currency::add));
  }
}
