package se.montesmites.ekonomi.report;

import java.util.Set;
import java.util.function.Predicate;

@FunctionalInterface
public interface TagFilter extends Predicate<Set<Tag>> {

  static TagFilter any() {
    return __ -> true;
  }

  static TagFilter isEqualTo(Tag tag) {
    return those -> those.stream().anyMatch(that -> that.getTag().equals(tag.getTag()));
  }
}
