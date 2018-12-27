package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

import java.util.stream.Stream;

public abstract class Section {

  public static Section empty() {
    return Section.of(Header.empty(), Body.empty(), Footer.empty());
  }

  public static Section of(Header header, Body body, Footer footer) {
    return new Section() {
      @Override
      public Header header() {
        return header;
      }

      @Override
      public Body body() {
        return body;
      }

      @Override
      public Footer footer() {
        return footer;
      }
    };
  }

  public abstract Header header();

  public abstract Body body();

  public abstract Footer footer();

  public final Stream<Row> stream() {
    Stream.Builder<Row> sb = Stream.builder();
    header().stream().forEach(sb::add);
    body().stream().map(AmountsProvider::asRow).forEach(sb::add);
    footer().stream().forEach(sb::add);
    return sb.build();
  }

  public final boolean isEquivalentTo(Section that) {
    var these = this.stream().collect(toList());
    var those = that.stream().collect(toList());
    return these.size() == those.size()
        && range(0, these.size()).allMatch(i -> these.get(i).isEquivalentTo(those.get(i)));
  }

  public final String asString(String delimiter) {
    return header().asString(delimiter)
        + delimiter
        + body().asString(delimiter)
        + delimiter
        + footer().asString(delimiter);
  }
}
