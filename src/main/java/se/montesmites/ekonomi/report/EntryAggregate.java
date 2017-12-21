package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.EventId;
import se.montesmites.ekonomi.model.tuple.AmountEntryListTuple;
import se.montesmites.ekonomi.model.tuple.YearMonthAccountIdTuple;

import java.time.YearMonth;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class EntryAggregate {

    private final Function<EventId, Optional<YearMonth>> yearMonthProvider;

    private final Map<YearMonthAccountIdTuple, AmountEntryListTuple> aggregate;

    EntryAggregate(Function<EventId, Optional<YearMonth>> yearMonthProvider) {
        this(new ConcurrentHashMap<>(), yearMonthProvider);
    }

    private EntryAggregate(
            Map<YearMonthAccountIdTuple, AmountEntryListTuple> aggregate,
            Function<EventId, Optional<YearMonth>> yearMonthProvider) {
        this.aggregate = aggregate;
        this.yearMonthProvider = yearMonthProvider;
    }

    public Map<YearMonthAccountIdTuple, AmountEntryListTuple> getAggregate() {
        return aggregate;
    }

    public void accumulate(Entry entry) {
        yearMonthProvider.apply(entry.getEventId())
                .ifPresent(yearMonth
                                   -> aggregate.merge(new YearMonthAccountIdTuple(yearMonth, entry.getAccountId()),
                                                      new AmountEntryListTuple(entry),
                                                      AmountEntryListTuple::merge)
                );
    }

    public EntryAggregate merge(EntryAggregate that) {
        Map<YearMonthAccountIdTuple, AmountEntryListTuple> map = new ConcurrentHashMap<>(this.getAggregate());
        that.aggregate.forEach((key, value) -> map.merge(key, value, AmountEntryListTuple::merge));
        return new EntryAggregate(map, yearMonthProvider);
    }
}
