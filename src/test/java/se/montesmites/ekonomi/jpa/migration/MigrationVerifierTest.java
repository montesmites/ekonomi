package se.montesmites.ekonomi.jpa.migration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.report.DataFetcher;
import se.montesmites.ekonomi.sie.file.SieToOrganizationConverter;

@SpringBootTest
public class MigrationVerifierTest {

  private static final Path PATH =
      Paths.get("C:/ProgramData/SPCS/SPCS Administration/Företag/nikka/sie");
  private static final SieToOrganizationConverter CONVERTER = SieToOrganizationConverter.of();

  @Autowired private MigrationVerifierRepository migrationVerifierRepository;

  @Test
  @Disabled
  void verifyMigration() {
    var sorter =
        Comparator.comparing(MonthlyAccountSum::year)
            .thenComparing(MonthlyAccountSum::month)
            .thenComparing(MonthlyAccountSum::accountQualifier)
            .thenComparing(MonthlyAccountSum::accountQualifier)
            .thenComparing(MonthlyAccountSum::totalAmount);
    var database =
        migrationVerifierRepository.fetchAllMonthlyAccountSums().stream().sorted(sorter).toList();
    var sie4 =
        Stream.of(2012, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022)
            .map(this::fetchAllMonthlyAccountSums)
            .flatMap(List::stream)
            .sorted(sorter)
            .toList();

    assertEquals(sie4, database);
  }

  private List<MonthlyAccountSum> fetchAllMonthlyAccountSums(int year) {
    record YearMonthAndAccountId(YearMonth yearMonth, AccountId accountId) {}

    var sie4 = new ArrayList<MonthlyAccountSum>();
    var file = PATH.resolve(year + "_sie4_transaktioner-och-balanser.SE");
    var sie4Data = CONVERTER.convert(file);
    var dataFetcher = new DataFetcher(sie4Data);
    dataFetcher
        .streamAccountIds(Year.of(year), __ -> true)
        .flatMap(
            accountId ->
                Stream.of(Month.values())
                    .map(month -> YearMonth.of(year, month))
                    .map(yearMonth -> new YearMonthAndAccountId(yearMonth, accountId)))
        .forEach(
            yearMonthAndAccountId -> {
              var maybeAccount = dataFetcher.getAccount(yearMonthAndAccountId.accountId());
              var maybeAmount =
                  dataFetcher.fetchAmount(
                      yearMonthAndAccountId.accountId(), yearMonthAndAccountId.yearMonth());
              maybeAccount.ifPresent(
                  account ->
                      maybeAmount.ifPresent(
                          amount -> {
                            var monthlyAccountSum =
                                new MonthlyAccountSum(
                                    year,
                                    yearMonthAndAccountId.yearMonth().getMonthValue(),
                                    account.accountId().id(),
                                    account.description(),
                                    sie4Data
                                        .streamEntries()
                                        .filter(
                                            entry ->
                                                sie4Data
                                                    .getEvent(entry.eventId())
                                                    .filter(
                                                        event ->
                                                            YearMonth.from(event.date())
                                                                .equals(
                                                                    yearMonthAndAccountId
                                                                        .yearMonth()))
                                                    .isPresent())
                                        .filter(
                                            entry -> entry.accountId().equals(account.accountId()))
                                        .count(),
                                    BigDecimal.valueOf(amount.amount(), 2));
                            sie4.add(monthlyAccountSum);
                          }));
            });
    return sie4;
  }
}
