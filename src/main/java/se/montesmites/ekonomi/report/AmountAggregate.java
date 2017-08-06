package se.montesmites.ekonomi.report;

import java.time.YearMonth;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.EventId;
import se.montesmites.ekonomi.model.tuple.CurrencyEntryListTuple;
import se.montesmites.ekonomi.model.tuple.YearMonthAccountIdTuple;

public class AmountAggregate {

    private final Function<EventId, YearMonth> yearMonthProvider;

    private final Map<YearMonthAccountIdTuple, CurrencyEntryListTuple> aggregate;

    public AmountAggregate(Function<EventId, YearMonth> yearMonthProvider) {
        this(new ConcurrentHashMap<>(), yearMonthProvider);
    }

    private AmountAggregate(
            Map<YearMonthAccountIdTuple, CurrencyEntryListTuple> aggregate,
            Function<EventId, YearMonth> yearMonthProvider) {
        this.aggregate = aggregate;
        this.yearMonthProvider = yearMonthProvider;
    }

    public Map<YearMonthAccountIdTuple, CurrencyEntryListTuple> getAggregate() {
        return aggregate;
    }

    public void accumulate(Entry entry) {
        aggregate.merge(
                new YearMonthAccountIdTuple(entry, yearMonthProvider),
                new CurrencyEntryListTuple(entry),
                (tuple1, tuple2) -> tuple1.merge(tuple2));
    }

    public AmountAggregate merge(AmountAggregate that) {
        Map<YearMonthAccountIdTuple, CurrencyEntryListTuple> map = new ConcurrentHashMap<>();
        map.putAll(this.getAggregate());
        that.aggregate.entrySet().stream()
                .forEach(e
                        -> map.merge(
                        e.getKey(),
                        e.getValue(),
                        (tuple1, tuple2) -> tuple1.merge(tuple2)));
        return new AmountAggregate(map, yearMonthProvider);
    }
}
