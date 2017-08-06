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
    private final AccountId accountId = new AccountId(yearId, "3010");
    private final Function<EventId, YearMonth> dateProvider
            = eventId -> yearMonth;
    private final YearMonthAccountIdTuple yearMonthAccountIdTuple
            = new YearMonthAccountIdTuple(yearMonth, accountId);
    private Entry entry;

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
        Currency amount = new Currency(1);
        EventId eventId = new EventId(yearId, 1, series);
        EntryStatus status = new EntryStatus(EntryStatus.Status.ACTIVE);
        this.entry = new Entry(eventId, accountId, amount, status);
        AmountAggregate act
                = Stream.of(entry)
                        .collect(new AmountCollector(dateProvider));
        assertEquals(1, act.getAggregate().size());
        assertEquals(1,
                act.getAggregate().get(yearMonthAccountIdTuple).getEntries().size());
        assertEquals(entry,
                act.getAggregate().get(yearMonthAccountIdTuple)
                        .getEntries().get(0));
        assertEquals(amount,
                act.getAggregate().get(yearMonthAccountIdTuple)
                        .getAmount());
    }
}
