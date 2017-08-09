package se.montesmites.ekonomi.report;

import java.time.YearMonth;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.EventId;
import se.montesmites.ekonomi.model.tuple.CurrencyEntryListTuple;
import se.montesmites.ekonomi.model.tuple.YearMonthAccountIdTuple;

public class EntryAggregate {

    private final Function<EventId, Optional<YearMonth>> yearMonthProvider;

    private final Map<YearMonthAccountIdTuple, CurrencyEntryListTuple> aggregate;

    public EntryAggregate(Function<EventId, Optional<YearMonth>> yearMonthProvider) {
        this(new ConcurrentHashMap<>(), yearMonthProvider);
    }

    private EntryAggregate(
            Map<YearMonthAccountIdTuple, CurrencyEntryListTuple> aggregate,
            Function<EventId, Optional<YearMonth>> yearMonthProvider) {
        this.aggregate = aggregate;
        this.yearMonthProvider = yearMonthProvider;
    }

    public Map<YearMonthAccountIdTuple, CurrencyEntryListTuple> getAggregate() {
        return aggregate;
    }

    public void accumulate(Entry entry) {
        yearMonthProvider.apply(entry.getEventId()).ifPresent(yearMonth
                -> aggregate.merge(
                        new YearMonthAccountIdTuple(yearMonth, entry.getAccountId()),
                        new CurrencyEntryListTuple(entry),
                        (tuple1, tuple2) -> tuple1.merge(tuple2))
        );
    }

    public EntryAggregate merge(EntryAggregate that) {
        Map<YearMonthAccountIdTuple, CurrencyEntryListTuple> map = new ConcurrentHashMap<>();
        map.putAll(this.getAggregate());
        that.aggregate.entrySet().stream()
                .forEach(e
                        -> map.merge(
                        e.getKey(),
                        e.getValue(),
                        (tuple1, tuple2) -> tuple1.merge(tuple2)));
        return new EntryAggregate(map, yearMonthProvider);
    }
}