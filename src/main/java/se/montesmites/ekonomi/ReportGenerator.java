package se.montesmites.ekonomi;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import se.montesmites.ekonomi.configuration.EkonomiProperties;
import se.montesmites.ekonomi.db.AccountRepository;
import se.montesmites.ekonomi.db.BalanceRepository;
import se.montesmites.ekonomi.db.model.AccountQualifier;
import se.montesmites.ekonomi.db.model.AccountQualifierAndName;
import se.montesmites.ekonomi.db.model.Amount;
import se.montesmites.ekonomi.jpa.migration.MonthlyAccountSum;
import se.montesmites.ekonomi.report.Report;
import se.montesmites.ekonomi.report.ReportDataFetcher;
import se.montesmites.ekonomi.report.data.MonthlyAccountSumRepository;
import se.montesmites.ekonomi.report.xml.JaxbReportBuilder;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class ReportGenerator {

  private final EkonomiProperties properties;

  private final AccountRepository accountRepository;
  private final BalanceRepository balanceRepository;
  private final MonthlyAccountSumRepository monthlyAccountSumRepository;

  public void run() {
    var calendarYear = properties.getReport().getFiscalYear();
    renderToFile(
        generateReport(
            reportDataFetcher(Year.of(calendarYear)),
            properties.getReport().getTemplate().asPath(),
            Year.of(calendarYear)),
        destinationPath(
            Paths.get(properties.getReport().getOutputDir()),
            properties.getReport().getTitle(),
            Year.of(calendarYear)));
  }

  private ReportDataFetcher reportDataFetcher(Year calendarYear) {
    return switch (properties.getDatasource().getType()) {
      case DATABASE -> databaseReportDataFetcher(calendarYear);
      case SIE, SPCS -> ReportDataFetcher.empty();
    };
  }

  private Path destinationPath(Path outputDir, String title, java.time.Year year) {
    var pathFormat = "%s %s %d.txt";
    return outputDir.resolve(
        String.format(
            pathFormat,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HHmmss")),
            title,
            year.getValue()));
  }

  private Report generateReport(
      ReportDataFetcher reportDataFetcher, Path template, java.time.Year year) {
    return new JaxbReportBuilder(template).report(reportDataFetcher, year);
  }

  private void renderToFile(Report report, Path outputPath) {
    try (var writer = Files.newBufferedWriter(outputPath)) {
      Files.createDirectories(outputPath.getParent());
      var lines = report.renderWithNoTrailingEmptyRows();
      for (var i = 0; i < lines.size() - 1; i++) {
        writer.append(lines.get(i));
        writer.newLine();
      }
      writer.append(lines.get(lines.size() - 1));
      writer.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private ReportDataFetcher databaseReportDataFetcher(Year calendarYear) {
    record AccountQualifierAndMonth(AccountQualifier qualifier, Month month) {}

    var accountsByQualifier =
        accountRepository.findAllByFiscalYearCalendarYear(calendarYear).stream()
            .map(
                account ->
                    new AccountQualifierAndName(
                        new AccountQualifier(account.qualifier()), account.name()))
            .collect(toMap(AccountQualifierAndName::qualifier, identity()));
    var balances =
        balanceRepository.findAllByAccountFiscalYearCalendarYear(calendarYear).stream()
            .collect(
                toMap(
                    balanceEntity -> new AccountQualifier(balanceEntity.getAccount().qualifier()),
                    balanceEntity -> new Amount(balanceEntity.getBalance())));

    var monthlyAccountSums =
        monthlyAccountSumRepository.fetchAllMonthlyAccountSums(calendarYear.getValue());
    var amounts =
        monthlyAccountSums.stream()
            .collect(
                toMap(
                    monthlyAccountSum ->
                        new AccountQualifierAndMonth(
                            new AccountQualifier(monthlyAccountSum.accountQualifier()),
                            monthlyAccountSum.month()),
                    monthlyAccountSum -> new Amount(monthlyAccountSum.totalAmount())));
    var months =
        Collections.unmodifiableSet(
            monthlyAccountSums.stream()
                .map(MonthlyAccountSum::month)
                .collect(toCollection(() -> EnumSet.noneOf(Month.class))));

    return new ReportDataFetcher() {
      @Override
      public Optional<Amount> fetchAmount(YearMonth yearMonth, AccountQualifier qualifier) {
        return Optional.ofNullable(
            amounts.get(new AccountQualifierAndMonth(qualifier, yearMonth.getMonth())));
      }

      @Override
      public Optional<Amount> fetchBalance(Year year, AccountQualifier qualifier) {
        return Optional.ofNullable(balances.get(qualifier));
      }

      @Override
      public Stream<AccountQualifier> streamAccountQualifiers(
          Year year, Predicate<AccountQualifier> filter) {
        return accountsByQualifier.keySet().stream().filter(filter);
      }

      @Override
      public Set<Month> touchedMonths(Year year) {
        return months;
      }

      @Override
      public Optional<AccountQualifierAndName> getAccount(
          YearMonth yearMonth, AccountQualifier qualifier) {
        return calendarYear.equals(Year.of(yearMonth.getYear()))
            ? Optional.ofNullable(accountsByQualifier.get(qualifier))
            : Optional.empty();
      }
    };
  }
}
