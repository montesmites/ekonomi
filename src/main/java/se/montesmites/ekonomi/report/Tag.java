package se.montesmites.ekonomi.report;

public class Tag {

  public static Tag of(String tag) {
    return new Tag(tag);
  }

  private final String tag;

  private Tag(String tag) {
    this.tag = tag;
  }

  public String getTag() {
    return tag;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    var tag1 = (Tag) o;

    return tag.equals(tag1.tag);
  }

  @Override
  public int hashCode() {
    return tag.hashCode();
  }
}
