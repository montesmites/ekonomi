package se.montesmites.ekonomi.report;

import java.util.Optional;
import java.util.stream.Stream;

public interface Section {

  static Section of(Header header, Body body) {
    return Section.of(header, body, Optional.of(RowAggregator.of(body::stream)));
  }

  static Section of(
      Header header, Body body, Optional<RowAggregator> rowAggregator) {
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
        return rowAggregator.map(Footer::of).orElse(Footer.empty());
      }
    };
  }

  default Stream<Row> streamBeforeSection() {
    return Stream.empty();
  }

  default Header header() {
    return Header.empty();
  }

  default Body body() {
    return Body.empty();
  }

  default Footer footer() {
    return Footer.empty();
  }

  default Stream<Row> streamAfterSection() {
    return Stream.of(Row.empty());
  }

  default Stream<Row> stream() {
    Stream.Builder<Row> sb = Stream.builder();
    streamBeforeSection().forEach(sb::add);
    header().stream().forEach(sb::add);
    body().stream().forEach(sb::add);
    footer().stream().forEach(sb::add);
    streamAfterSection().forEach(sb::add);
    return sb.build();
  }
}
