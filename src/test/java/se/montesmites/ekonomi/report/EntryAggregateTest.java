package se.montesmites.ekonomi.report;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Month;
import java.time.YearMonth;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.EntryStatus;
import se.montesmites.ekonomi.model.EventId;
import se.montesmites.ekonomi.model.Series;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.model.tuple.AmountEntryListTuple;
import se.montesmites.ekonomi.model.tuple.YearMonthAccountIdTuple;

class EntryAggregateTest {

  private final YearId yearId = new YearId("A");
  private final Series series = new Series("A");

  @Test
  void collectEmptyStream() {
    final YearMonth yearMonth = YearMonth.of(2012, Month.JANUARY);
    EntryAggregate act = Stream.<Entry>empty().collect(amountCollector(yearMonth));
    assertEquals(0, act.getAggregate().size());
  }

  @Test
  void collectOneEntry() {
    final YearMonth yearMonth = YearMonth.of(2012, Month.JANUARY);
    final long amount = 100;
    final int accountid = 3010;
    final Entry entry = entry(1, accountid, amount);
    final EntryAggregate aggregate = Stream.of(entry).collect(amountCollector(yearMonth));
    final Map<YearMonthAccountIdTuple, AmountEntryListTuple> act = aggregate.getAggregate();
    assertEquals(1, act.size());
    assertEquals(1, sizeOf(act, yearMonth, accountid));
    assertEquals(entry, entryOf(act, yearMonth, accountid, 0));
    assertEquals(currency(amount), amountOf(act, yearMonth, accountid));
  }

  @Test
  void collectTwoEntries_sameAccount() {
    final YearMonth yearMonth = YearMonth.of(2012, Month.JANUARY);
    final long amount1 = 100;
    final long amount2 = 200;
    final int accountid = 3010;
    final Entry entry1 = entry(1, accountid, amount1);
    final Entry entry2 = entry(2, accountid, amount2);
    final EntryAggregate aggregate = Stream.of(entry1, entry2).collect(amountCollector(yearMonth));
    final Map<YearMonthAccountIdTuple, AmountEntryListTuple> act = aggregate.getAggregate();
    assertEquals(1, act.size());
    assertEquals(2, sizeOf(act, yearMonth, accountid));
    assertEquals(entry1, entryOf(act, yearMonth, accountid, 0));
    assertEquals(entry2, entryOf(act, yearMonth, accountid, 1));
    assertEquals(currency(amount1 + amount2), amountOf(act, yearMonth, accountid));
  }

  @Test
  void collectTwoEntries_differentAccounts() {
    final YearMonth yearMonth = YearMonth.of(2012, Month.JANUARY);
    final long amount1 = 100;
    final long amount2 = 200;
    final int accountid1 = 3010;
    final int accountid2 = 3020;
    final Entry entry1 = entry(1, accountid1, amount1);
    final Entry entry2 = entry(2, accountid2, amount2);
    final EntryAggregate aggregate = Stream.of(entry1, entry2).collect(amountCollector(yearMonth));
    final Map<YearMonthAccountIdTuple, AmountEntryListTuple> act = aggregate.getAggregate();
    assertEquals(2, act.size());
    assertEquals(1, sizeOf(act, yearMonth, accountid1));
    assertEquals(1, sizeOf(act, yearMonth, accountid2));
    assertEquals(entry1, entryOf(act, yearMonth, accountid1, 0));
    assertEquals(entry2, entryOf(act, yearMonth, accountid2, 0));
    assertEquals(currency(amount1), amountOf(act, yearMonth, accountid1));
    assertEquals(currency(amount2), amountOf(act, yearMonth, accountid2));
  }

  @Test
  void collectTwoEntries_sameAccounts_differentMonths() {
    final YearMonth yearMonth1 = YearMonth.of(2012, Month.JANUARY);
    final YearMonth yearMonth2 = YearMonth.of(2012, Month.FEBRUARY);
    final long amount1 = 100;
    final long amount2 = 200;
    final int accountid1 = 3010;
    final int accountid2 = 3010;
    final Entry entry1 = entry(1, accountid1, amount1);
    final Entry entry2 = entry(2, accountid2, amount2);
    final EntryAggregate aggregate =
        Stream.of(entry1, entry2)
            .collect(
                new EntryCollector(
                    eventId ->
                        eventId.id() == 1 ? Optional.of(yearMonth1) : Optional.of(yearMonth2)));
    final Map<YearMonthAccountIdTuple, AmountEntryListTuple> act = aggregate.getAggregate();
    assertEquals(2, act.size());
    assertEquals(1, sizeOf(act, yearMonth1, accountid1));
    assertEquals(1, sizeOf(act, yearMonth2, accountid2));
    assertEquals(entry1, entryOf(act, yearMonth1, accountid1, 0));
    assertEquals(entry2, entryOf(act, yearMonth2, accountid2, 0));
    assertEquals(currency(amount1), amountOf(act, yearMonth1, accountid1));
    assertEquals(currency(amount2), amountOf(act, yearMonth2, accountid2));
  }

  private AccountId accountId(int accountid) {
    return new AccountId(yearId, "" + accountid);
  }

  private Entry entry(int eventid, int accountid, long amount) {
    final EventId eventId = new EventId(yearId, eventid, series);
    final EntryStatus status = new EntryStatus(EntryStatus.Status.ACTIVE);
    return new Entry(eventId, accountId(accountid), currency(amount), status);
  }

  private Currency currency(long amount) {
    return new Currency(amount);
  }

  private AmountEntryListTuple tuple(
      Map<YearMonthAccountIdTuple, AmountEntryListTuple> map, YearMonth yearMonth, int accountid) {
    YearMonthAccountIdTuple key = new YearMonthAccountIdTuple(yearMonth, accountId(accountid));
    return map.get(key);
  }

  private int sizeOf(
      Map<YearMonthAccountIdTuple, AmountEntryListTuple> map, YearMonth yearMonth, int accountid) {
    return tuple(map, yearMonth, accountid).entries().size();
  }

  private Currency amountOf(
      Map<YearMonthAccountIdTuple, AmountEntryListTuple> map, YearMonth yearMonth, int accountid) {
    return tuple(map, yearMonth, accountid).amount();
  }

  private Entry entryOf(
      Map<YearMonthAccountIdTuple, AmountEntryListTuple> map,
      YearMonth yearMonth,
      int accountid,
      int entryIndex) {
    return tuple(map, yearMonth, accountid).entries().get(entryIndex);
  }

  private EntryCollector amountCollector(YearMonth yearMonth) {
    return new EntryCollector(eventId -> Optional.of(yearMonth));
  }
}
