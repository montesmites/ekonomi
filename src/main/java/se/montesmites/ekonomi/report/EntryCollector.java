package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.EventId;

import java.time.YearMonth;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.function.Function.identity;
import static java.util.stream.Collector.Characteristics.CONCURRENT;
import static java.util.stream.Collector.Characteristics.UNORDERED;

class EntryCollector implements Collector<Entry, EntryAggregate, EntryAggregate> {

    private final Function<EventId, Optional<YearMonth>> yearMonthProvider;

    EntryCollector(Function<EventId, Optional<YearMonth>> yearMonthProvider) {
        this.yearMonthProvider = yearMonthProvider;
    }

    @Override
    public Supplier<EntryAggregate> supplier() {
        return () -> new EntryAggregate(yearMonthProvider);
    }

    @Override
    public BiConsumer<EntryAggregate, Entry> accumulator() {
        return EntryAggregate::accumulate;
    }

    @Override
    public BinaryOperator<EntryAggregate> combiner() {
        return EntryAggregate::merge;
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
