package se.montesmites.ekonomi.report;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TagFilterTest {

  @Test
  void isEqualTo() {
    var tag1 = Tag.of("tag1");
    var tag2 = Tag.of("tag2");
    var tagFilter = TagFilter.isEqualTo(tag1);
    assertTrue(tagFilter.test(tag1));
    assertFalse(tagFilter.test(tag2));
  }
}
