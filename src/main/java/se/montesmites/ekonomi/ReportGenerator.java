package se.montesmites.ekonomi;

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
import se.montesmites.ekonomi.jpa.migration.MonthlyAccountSum;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.organization.OrganizationBuilder;
import se.montesmites.ekonomi.report.AmountsFetcher;
import se.montesmites.ekonomi.report.DataFetcher;
import se.montesmites.ekonomi.report.Report;
import se.montesmites.ekonomi.report.data.MonthlyAccountSumRepository;
import se.montesmites.ekonomi.report.xml.JaxbReportBuilder;
import se.montesmites.ekonomi.sie.file.SieToOrganizationConverter;

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
            amountsFetcher(Year.of(calendarYear)),
            properties.getReport().getTemplate().asPath(),
            Year.of(calendarYear)),
        destinationPath(
            Paths.get(properties.getReport().getOutputDir()),
            properties.getReport().getTitle(),
            Year.of(calendarYear)));
  }

  private AmountsFetcher amountsFetcher(Year calendarYear) {
    return switch (properties.getDatasource().getType()) {
      case DATABASE -> databaseAmountsFetcher(calendarYear);
      case SIE -> new DataFetcher(
          SieToOrganizationConverter.of()
              .convert(Paths.get(properties.getDatasource().getSieInputPath())));
      case SPCS -> new DataFetcher(
          new OrganizationBuilder(Paths.get(properties.getDatasource().getSpcsInputDir())).build());
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

  private Report generateReport(AmountsFetcher amountsFetcher, Path template, java.time.Year year) {
    return new JaxbReportBuilder(template).report(amountsFetcher, year);
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

  private AmountsFetcher databaseAmountsFetcher(Year calendarYear) {
    record AccountQualifierAndMonth(String qualifier, Month month) {}

    var accounts = accountRepository.findAllByFiscalYearCalendarYear(calendarYear);
    var balances =
        balanceRepository.findAllByAccountFiscalYearCalendarYear(calendarYear).stream()
            .collect(
                toMap(
                    balanceEntity -> balanceEntity.getAccount().qualifier(),
                    balanceEntity -> Currency.from(balanceEntity.getBalance())));

    var monthlyAccountSums =
        monthlyAccountSumRepository.fetchAllMonthlyAccountSums(calendarYear.getValue());
    var amounts =
        monthlyAccountSums.stream()
            .collect(
                toMap(
                    monthlyAccountSum ->
                        new AccountQualifierAndMonth(
                            monthlyAccountSum.accountQualifier(), monthlyAccountSum.month()),
                    monthlyAccountSum -> Currency.from(monthlyAccountSum.totalAmount())));
    var months =
        Collections.unmodifiableSet(
            monthlyAccountSums.stream()
                .map(MonthlyAccountSum::month)
                .collect(toCollection(() -> EnumSet.noneOf(Month.class))));
    return new AmountsFetcher() {
      @Override
      public Optional<Currency> fetchAmount(AccountId accountId, YearMonth yearMonth) {
        return Optional.ofNullable(
            amounts.get(new AccountQualifierAndMonth(accountId.id(), yearMonth.getMonth())));
      }

      @Override
      public Optional<Balance> fetchBalance(AccountId accountId) {
        return Optional.ofNullable(balances.get(accountId.id()))
            .map(balance -> new Balance(accountId, balance));
      }

      @Override
      public Stream<AccountId> streamAccountIds(Year year, Predicate<AccountId> filter) {
        return accounts.stream()
            .map(
                account ->
                    new AccountId(new YearId(String.valueOf(year.getValue())), account.qualifier()))
            .filter(filter);
      }

      @Override
      public Set<Month> touchedMonths(Year year) {
        return months;
      }
    };
  }
}
