package se.montesmites.ekonomi.organization;

import se.montesmites.ekonomi.model.Entry;

import java.util.function.Predicate;

@FunctionalInterface
public interface FilterEntry extends Filter {
    @Override
    public Predicate<Entry> entryFilter();
}
