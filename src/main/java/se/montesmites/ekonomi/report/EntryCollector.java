package se.montesmites.ekonomi.report;

import java.time.YearMonth;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import static java.util.function.Function.*;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import static java.util.stream.Collector.Characteristics.CONCURRENT;
import static java.util.stream.Collector.Characteristics.UNORDERED;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.EventId;

public class EntryCollector implements Collector<Entry, EntryAggregate, EntryAggregate> {

    private final Function<EventId, Optional<YearMonth>> yearMonthProvider;

    public EntryCollector(Function<EventId, Optional<YearMonth>> yearMonthProvider) {
        this.yearMonthProvider = yearMonthProvider;
    }

    @Override
    public Supplier<EntryAggregate> supplier() {
        return () -> new EntryAggregate(yearMonthProvider);
    }

    @Override
    public BiConsumer<EntryAggregate, Entry> accumulator() {
        return (aggregate, entry) -> aggregate.accumulate(entry);
    }

    @Override
    public BinaryOperator<EntryAggregate> combiner() {
        return (aggregate1, aggregate2) -> aggregate1.merge(aggregate2);
    }

    @Override
    public Function<EntryAggregate, EntryAggregate> finisher() {
        return identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(UNORDERED, CONCURRENT);
    }
}
