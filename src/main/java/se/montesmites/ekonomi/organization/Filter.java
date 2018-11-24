package se.montesmites.ekonomi.organization;

import java.util.function.Predicate;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.Year;

public interface Filter {

  static Filter get() {
    return new Filter() {};
  }

  static FilterEntry get(Predicate<Entry> filter) {
    return () -> filter;
  }

  default Predicate<Account> accountFilter() {
    return __ -> true;
  }

  default Predicate<Balance> balanceFilter() {
    return __ -> true;
  }

  default Predicate<Entry> entryFilter() {
    return __ -> true;
  }

  default Predicate<Event> eventFilter() {
    return __ -> true;
  }

  default Predicate<Year> yearFilter() {
    return __ -> true;
  }
}
