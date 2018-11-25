package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;

import java.util.stream.Stream;

public interface Section {

  static Section empty() {
    return Section.of(Header.empty(), Body.empty(), Footer.empty());
  }

  static Section of(Header header, Body body, Footer footer) {
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

  static Section compact(String description, Body body) {
    var footer = Footer.of(body.aggregate().merge(DESCRIPTION, TitleRow.of(description)));
    return Section.of(Header.empty(), Body.empty(), footer);
  }

  Header header();

  Body body();

  Footer footer();

  default Stream<Row> stream() {
    Stream.Builder<Row> sb = Stream.builder();
    header().stream().forEach(sb::add);
    body().stream().forEach(sb::add);
    footer().stream().forEach(sb::add);
    return sb.build();
  }

  default boolean isEquivalentTo(Section that) {
    var these = this.stream().collect(toList());
    var those = that.stream().collect(toList());
    return these.size() == those.size()
        && range(0, these.size()).allMatch(i -> these.get(i).isEquivalentTo(those.get(i)));
  }
}
