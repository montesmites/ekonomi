package se.montesmites.ekonomi.report;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import se.montesmites.ekonomi.db.model.AccountQualifier;
import se.montesmites.ekonomi.db.model.AccountQualifierAndName;
import se.montesmites.ekonomi.db.model.Amount;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.YearId;

public interface ReportDataFetcher {

  Optional<Amount> fetchAmount(YearMonth yearMonth, AccountQualifier qualifier);

  Optional<Amount> fetchBalance(Year year, AccountQualifier qualifier);

  Stream<AccountQualifier> streamAccountQualifiers(Year year, Predicate<AccountQualifier> filter);

  Set<Month> touchedMonths(Year year);

  Optional<AccountQualifierAndName> getAccount(YearMonth yearMonth, AccountQualifier qualifier);

  default AmountsFetcher toAmountsFetcher() {
    return new AmountsFetcher() {
      @Override
      public Optional<Currency> fetchAmount(AccountId accountId, YearMonth yearMonth) {
        return ReportDataFetcher.this
            .fetchAmount(yearMonth, new AccountQualifier(accountId.id()))
            .map(Currency::from);
      }

      @Override
      public Optional<Balance> fetchBalance(AccountId accountId) {
        var yearStr = accountId.yearId().id();
        try {
          var year = Integer.parseInt(yearStr);
          return ReportDataFetcher.this
              .fetchBalance(Year.of(year), new AccountQualifier(accountId.id()))
              .map(balance -> new Balance(accountId, Currency.from(balance)));
        } catch (NumberFormatException numberFormatException) {
          return Optional.empty();
        }
      }

      @Override
      public Stream<AccountId> streamAccountIds(Year year, Predicate<AccountId> filter) {
        return ReportDataFetcher.this
            .streamAccountQualifiers(
                year,
                qualifier ->
                    filter.test(
                        new AccountId(
                            new YearId(year.format(DateTimeFormatter.ofPattern("yyyy"))),
                            qualifier.qualifier())))
            .map(qualifier -> new AccountId(new YearId(year.toString()), qualifier.qualifier()));
      }

      @Override
      public Set<Month> touchedMonths(Year year) {
        return ReportDataFetcher.this.touchedMonths(year);
      }
    };
  }

  static ReportDataFetcher empty() {
    return new ReportDataFetcher() {
      @Override
      public Optional<Amount> fetchAmount(YearMonth yearMonth, AccountQualifier qualifier) {
        return Optional.empty();
      }

      @Override
      public Optional<Amount> fetchBalance(Year year, AccountQualifier qualifier) {
        return Optional.empty();
      }

      @Override
      public Stream<AccountQualifier> streamAccountQualifiers(
          Year year, Predicate<AccountQualifier> filter) {
        return null;
      }

      @Override
      public Set<Month> touchedMonths(Year year) {
        return EnumSet.noneOf(Month.class);
      }

      @Override
      public Optional<AccountQualifierAndName> getAccount(
          YearMonth yearMonth, AccountQualifier qualifier) {
        return Optional.empty();
      }
    };
  }
}
