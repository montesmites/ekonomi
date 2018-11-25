package se.montesmites.ekonomi.report;

import java.util.function.Supplier;
import java.util.stream.Stream;

public interface Body {

  static Body empty() {
    return Stream::empty;
  }

  static Body of(Supplier<Stream<? extends RowWithAmounts>> rowWithAmounts) {
    return rowWithAmounts::get;
  }

  static Body of(RowWithAmounts rowWithAmounts) {
    return () -> Stream.of(rowWithAmounts);
  }

  default Body add(RowWithAmounts rowWithAmounts) {
    return () -> Stream.concat(this.stream(), Stream.of(rowWithAmounts));
  }

  Stream<? extends RowWithAmounts> stream();
}
