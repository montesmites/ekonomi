package se.montesmites.ekonomi.model.tuple;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

import java.util.List;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.Entry;

public record AmountEntryListTuple(Currency amount, List<Entry>entries) {

  public AmountEntryListTuple(Entry entry) {
    this(entry.amount(), List.of(entry));
  }

  public AmountEntryListTuple merge(AmountEntryListTuple that) {
    var sum = this.amount().add(that.amount());
    var entries = concat(this.entries().stream(), that.entries().stream()).collect(toList());
    return new AmountEntryListTuple(sum, entries);
  }
}
