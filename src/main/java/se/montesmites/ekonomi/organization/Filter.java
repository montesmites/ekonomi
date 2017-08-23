package se.montesmites.ekonomi.organization;

import se.montesmites.ekonomi.model.*;

import java.util.function.Predicate;

public interface Filter {
    public static Filter get() {
        return new Filter() {
        };
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
