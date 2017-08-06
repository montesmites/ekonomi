package se.montesmites.ekonomi.report;

import java.time.YearMonth;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import static java.util.stream.Collector.Characteristics.CONCURRENT;
import static java.util.stream.Collector.Characteristics.UNORDERED;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.EventId;
import se.montesmites.ekonomi.model.tuple.YearMonthAccountIdTuple;

public class AmountCollector implements Collector<Entry, Map<YearMonthAccountIdTuple, Currency>, AmountAggregate> {

    private final Function<EventId, YearMonth> yearMonthProvider;

    public AmountCollector(Function<EventId, YearMonth> yearMonthProvider) {
        this.yearMonthProvider = yearMonthProvider;
    }

    @Override
    public Supplier<Map<YearMonthAccountIdTuple, Currency>> supplier() {
        return () -> new ConcurrentHashMap<>();
    }

    @Override
    public BiConsumer<Map<YearMonthAccountIdTuple, Currency>, Entry> accumulator() {
        return (map, entry)
                -> map.merge(
                        new YearMonthAccountIdTuple(entry, yearMonthProvider),
                        entry.getAmount(),
                        (sum, term) -> sum.add(term));
    }

    @Override
    public BinaryOperator<Map<YearMonthAccountIdTuple, Currency>> combiner() {
        return (map1, map2) -> {
            Map<YearMonthAccountIdTuple, Currency> ret = new ConcurrentHashMap<>();
            ret.putAll(map1);
            ret.putAll(map2);
            return ret;
        };
    }

    @Override
    public Function<Map<YearMonthAccountIdTuple, Currency>, AmountAggregate> finisher() {
        return (map) -> new AmountAggregate(map);
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(UNORDERED, CONCURRENT);
    }
}
