package se.montesmites.ekonomi.report;

import java.time.Month;
import java.time.YearMonth;
import java.util.Map;
import java.util.function.Function;
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

    private final YearMonth yearMonth = YearMonth.of(2012, Month.JANUARY);
    private final YearId yearId = new YearId("A");
    private final Series series = new Series("A");
    private final Function<EventId, YearMonth> dateProvider
            = eventId -> yearMonth;

    @Before
    public void before() {
    }

    @Test
    public void collectEmptyStream() {
        AmountAggregate act
                = Stream.<Entry>empty().collect(amountCollector());
        assertEquals(0, act.getAggregate().size());
    }

    @Test
    public void collectOneEntry() {
        final long amount = 100;
        final int accountid = 3010;
        final Entry entry = entry(1, accountid, amount);
        final AmountAggregate aggregate
                = Stream.of(entry).collect(amountCollector());
        final Map<YearMonthAccountIdTuple, CurrencyEntryListTuple> act
                = aggregate.getAggregate();
        assertEquals(1, act.size());
        assertEquals(1, act.get(tuple(accountid)).getEntries().size());
        assertEquals(entry, act.get(tuple(accountid)).getEntries().get(0));
        assertEquals(
                currency(amount),
                act.get(tuple(accountid)).getAmount());
    }

    @Test
    public void collectTwoEntries_sameAccount() {
        final long amount1 = 100;
        final long amount2 = 200;
        final int accountid = 3010;
        final Entry entry1 = entry(1, accountid, amount1);
        final Entry entry2 = entry(2, accountid, amount2);
        final AmountAggregate aggregate
                = Stream.of(entry1, entry2).collect(amountCollector());
        final Map<YearMonthAccountIdTuple, CurrencyEntryListTuple> act
                = aggregate.getAggregate();
        assertEquals(1, act.size());
        assertEquals(2, act.get(tuple(accountid)).getEntries().size());
        assertEquals(
                currency(amount1 + amount2),
                act.get(tuple(accountid)).getAmount());
    }

    @Test
    public void collectTwoEntries_differentAccounts() {
        final long amount1 = 100;
        final long amount2 = 200;
        final int accountid1 = 3010;
        final int accountid2 = 3020;
        final Entry entry1 = entry(1, accountid1, amount1);
        final Entry entry2 = entry(2, accountid2, amount2);
        final AmountAggregate aggregate
                = Stream.of(entry1, entry2).collect(amountCollector());
        final Map<YearMonthAccountIdTuple, CurrencyEntryListTuple> act
                = aggregate.getAggregate();
        assertEquals(2, act.size());
        assertEquals(1, act.get(tuple(accountid1)).getEntries().size());
        assertEquals(1, act.get(tuple(accountid2)).getEntries().size());
        assertEquals(currency(amount1), act.get(tuple(accountid1)).getAmount());
        assertEquals(currency(amount2), act.get(tuple(accountid2)).getAmount());
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

    private YearMonthAccountIdTuple tuple(int accountid) {
        return new YearMonthAccountIdTuple(yearMonth, accountId(accountid));
    }

    private AmountCollector amountCollector() {
        return new AmountCollector(dateProvider);
    }
}
