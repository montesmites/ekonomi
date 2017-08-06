package se.montesmites.ekonomi.report;

import java.time.Month;
import java.time.YearMonth;
import java.util.Map;
import java.util.stream.Stream;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.EntryStatus;
import se.montesmites.ekonomi.model.EventId;
import se.montesmites.ekonomi.model.Series;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.model.tuple.CurrencyEntryListTuple;
import se.montesmites.ekonomi.model.tuple.YearMonthAccountIdTuple;

public class AmountAggregateTest {

    private final YearId yearId = new YearId("A");
    private final Series series = new Series("A");

    @Before
    public void before() {
    }

    @Test
    public void collectEmptyStream() {
        final YearMonth yearMonth = YearMonth.of(2012, Month.JANUARY);
        AmountAggregate act
                = Stream.<Entry>empty().collect(amountCollector(yearMonth));
        assertEquals(0, act.getAggregate().size());
    }

    @Test
    public void collectOneEntry() {
        final YearMonth yearMonth = YearMonth.of(2012, Month.JANUARY);
        final long amount = 100;
        final int accountid = 3010;
        final Entry entry = entry(1, accountid, amount);
        final AmountAggregate aggregate
                = Stream.of(entry).collect(amountCollector(yearMonth));
        final Map<YearMonthAccountIdTuple, CurrencyEntryListTuple> act
                = aggregate.getAggregate();
        assertEquals(1, act.size());
        assertEquals(1, sizeOf(act, yearMonth, accountid));
        assertEquals(entry, entryOf(act, yearMonth, accountid, 0));
        assertEquals(currency(amount), amountOf(act, yearMonth, accountid));
    }

    @Test
    public void collectTwoEntries_sameAccount() {
        final YearMonth yearMonth = YearMonth.of(2012, Month.JANUARY);
        final long amount1 = 100;
        final long amount2 = 200;
        final int accountid = 3010;
        final Entry entry1 = entry(1, accountid, amount1);
        final Entry entry2 = entry(2, accountid, amount2);
        final AmountAggregate aggregate
                = Stream.of(entry1, entry2).collect(amountCollector(yearMonth));
        final Map<YearMonthAccountIdTuple, CurrencyEntryListTuple> act
                = aggregate.getAggregate();
        assertEquals(1, act.size());
        assertEquals(2, sizeOf(act, yearMonth, accountid));
        assertEquals(entry1, entryOf(act, yearMonth, accountid, 0));
        assertEquals(entry2, entryOf(act, yearMonth, accountid, 1));
        assertEquals(
                currency(amount1 + amount2),
                amountOf(act, yearMonth, accountid));
    }

    @Test
    public void collectTwoEntries_differentAccounts() {
        final YearMonth yearMonth = YearMonth.of(2012, Month.JANUARY);
        final long amount1 = 100;
        final long amount2 = 200;
        final int accountid1 = 3010;
        final int accountid2 = 3020;
        final Entry entry1 = entry(1, accountid1, amount1);
        final Entry entry2 = entry(2, accountid2, amount2);
        final AmountAggregate aggregate
                = Stream.of(entry1, entry2).collect(amountCollector(yearMonth));
        final Map<YearMonthAccountIdTuple, CurrencyEntryListTuple> act
                = aggregate.getAggregate();
        assertEquals(2, act.size());
        assertEquals(1, sizeOf(act, yearMonth, accountid1));
        assertEquals(1, sizeOf(act, yearMonth, accountid2));
        assertEquals(entry1, entryOf(act, yearMonth, accountid1, 0));
        assertEquals(entry2, entryOf(act, yearMonth, accountid2, 0));
        assertEquals(currency(amount1), amountOf(act, yearMonth, accountid1));
        assertEquals(currency(amount2), amountOf(act, yearMonth, accountid2));
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

    private CurrencyEntryListTuple tuple(
            Map<YearMonthAccountIdTuple, CurrencyEntryListTuple> map,
            YearMonth yearMonth,
            int accountid) {
        YearMonthAccountIdTuple key
                = new YearMonthAccountIdTuple(yearMonth, accountId(accountid));
        return map.get(key);
    }

    private int sizeOf(
            Map<YearMonthAccountIdTuple, CurrencyEntryListTuple> map,
            YearMonth yearMonth,
            int accountid) {
        return tuple(map, yearMonth, accountid).getEntries().size();
    }

    private Currency amountOf(
            Map<YearMonthAccountIdTuple, CurrencyEntryListTuple> map,
            YearMonth yearMonth,
            int accountid) {
        return tuple(map, yearMonth, accountid).getAmount();
    }

    private Entry entryOf(
            Map<YearMonthAccountIdTuple, CurrencyEntryListTuple> map,
            YearMonth yearMonth,
            int accountid,
            int entryIndex) {
        return tuple(map, yearMonth, accountid).getEntries().get(entryIndex);
    }

    private AmountCollector amountCollector(YearMonth yearMonth) {
        return new AmountCollector(eventId -> yearMonth);
    }
}
