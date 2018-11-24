package se.montesmites.ekonomi.organization;

import java.util.function.Predicate;
import se.montesmites.ekonomi.model.Entry;

@FunctionalInterface
public interface FilterEntry extends Filter {

  @Override
  Predicate<Entry> entryFilter();
}
