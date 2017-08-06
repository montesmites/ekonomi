package se.montesmites.ekonomi.report;

import java.time.Month;
import java.time.YearMonth;
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
                = Stream.<Entry>empty()
                        .collect(new AmountCollector(dateProvider));
        assertEquals(0, act.getAggregate().size());
    }

    @Test
    public void collectOneEntry() {
        final long amount = 100;
        final int accountid = 3010;
        Entry entry = entry(1, accountid, amount);
        AmountAggregate act
                = Stream.of(entry)
                        .collect(new AmountCollector(dateProvider));
        assertEquals(1, act.getAggregate().size());
        assertEquals(1,
                act.getAggregate().get(tuple(accountid)).getEntries().size());
        assertEquals(entry,
                act.getAggregate().get(tuple(accountid)).getEntries().get(0));
        assertEquals(currency(amount),
                act.getAggregate().get(tuple(accountid)).getAmount());
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
}
