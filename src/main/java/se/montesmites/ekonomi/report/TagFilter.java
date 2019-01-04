package se.montesmites.ekonomi.report;

import java.util.function.Predicate;

@FunctionalInterface
public interface TagFilter extends Predicate<Tag> {

  static TagFilter isEqualTo(Tag tag) {
    return that -> that.getTag().equals(tag.getTag());
  }
}
