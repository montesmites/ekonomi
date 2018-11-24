package se.montesmites.ekonomi.report;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface Section {

  static Section of(Header header, Supplier<Stream<Row>> bodyRows) {
    return Section.of(header, bodyRows, Optional.of(RowAggregator.of(bodyRows)));
  }

  static Section of(
      Header header,
      Supplier<Stream<Row>> bodyRows,
      Optional<RowAggregator> rowAggregator) {
    return new Section() {
      @Override
      public Header header() {
        return header;
      }

      @Override
      public Stream<Row> streamBody() {
        return bodyRows.get();
      }

      @Override
      public Stream<Row> streamFooter() {
        return rowAggregator.stream().flatMap(Stream::of).map(RowAggregator::aggregate);
      }
    };
  }

  static Section of(Supplier<Stream<Row>> rows) {
    return new Section() {
      @Override
      public Stream<Row> streamAfterSection() {
        return Stream.empty();
      }

      @Override
      public Stream<Row> stream() {
        return rows.get();
      }
    };
  }

  default Stream<Row> streamBeforeSection() {
    return Stream.empty();
  }

  default Header header() {
    return Header.empty();
  }

  default Stream<Row> streamBody() {
    return Stream.empty();
  }

  default Stream<Row> streamFooter() {
    return Stream.empty();
  }

  default Stream<Row> streamAfterSection() {
    return Stream.of(Row.empty());
  }

  default Stream<Row> stream() {
    Stream.Builder<Row> sb = Stream.builder();
    streamBeforeSection().forEach(sb::add);
    header().stream().forEach(sb::add);
    streamBody().forEach(sb::add);
    streamFooter().forEach(sb::add);
    streamAfterSection().forEach(sb::add);
    return sb.build();
  }
}
