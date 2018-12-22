package se.montesmites.ekonomi.report.builder;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.AmountFetcher;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.Body;

class BodyBuilderTest {

  @Test
  void empty() {
    var bodyBuilder = BodyBuilder.empty();
    var exp = Body.empty();
    var act = bodyBuilder.body();
    assertEquals(exp.asString("\n"), act.asString("\n"));
  }

  @Test
  void fromAccountGroups() {
    var description1 = "description1";
    var description2 = "description2";
    var year = Year.of(2018);
    var yearid = new YearId("yearid");
    var accountGroups =
        List.of(AccountGroup.of(description1, "1111"), AccountGroup.of(description2, "2222"));
    var amountsProvider1 =
        AmountsProvider.of(description1, month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var amountsProvider2 =
        AmountsProvider.of(description2, month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var amountFetcher =
        new AmountFetcher() {
          @Override
          public Optional<Currency> fetchAmount(AccountId accountId, YearMonth yearMonth) {
            var account = accountId.getId();
            var month = yearMonth.getMonth();
            return Map.ofEntries(entry("1111", amountsProvider1), entry("2222", amountsProvider2))
                .get(account)
                .getMonthlyAmount(month)
                .map(Currency::negate);
          }

          @Override
          public Optional<Balance> fetchBalance(AccountId accountId) {
            return Optional.empty();
          }

          @Override
          public Stream<AccountId> streamAccountIds(Year year, Predicate<AccountId> filter) {
            return Stream.of(new AccountId(yearid, "1111"), new AccountId(yearid, "2222"))
                .filter(filter);
          }

          @Override
          public Set<Month> touchedMonths(Year year) {
            return EnumSet.allOf(Month.class);
          }
        };
    var bodyBuilder = new BodyBuilder(year, amountFetcher);
    var exp = Body.of(List.of(amountsProvider1, amountsProvider2));
    var act = bodyBuilder.accountGroups(accountGroups).body();
    assertEquals(exp.asString("\n"), act.asString("\n"));
  }
}
