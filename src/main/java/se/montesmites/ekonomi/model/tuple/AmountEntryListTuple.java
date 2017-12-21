package se.montesmites.ekonomi.model.tuple;

import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

;

public class AmountEntryListTuple {

    private final Currency amount;
    private final List<Entry> entries;
    
    public AmountEntryListTuple(Entry entry) {
        this(entry.getAmount(), List.of(entry));
    }
    
    private AmountEntryListTuple(Currency amount, List<Entry> entries) {
        this.amount = amount;
        this.entries = new ArrayList<>(entries);
    }

    public Currency getAmount() {
        return amount;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public AmountEntryListTuple merge(AmountEntryListTuple that) {
        Currency sum = this.getAmount().add(that.getAmount());
        List<Entry> retEntries
                = Stream.concat(
                        this.getEntries().stream(),
                        that.getEntries().stream())
                        .collect(toList());
        return new AmountEntryListTuple(sum, retEntries);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + Objects.hashCode(this.amount);
        hash = 19 * hash + Objects.hashCode(this.entries);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AmountEntryListTuple other = (AmountEntryListTuple) obj;
        if (!Objects.equals(this.amount, other.amount)) {
            return false;
        }
        return Objects.equals(this.entries, other.entries);
    }

    @Override
    public String toString() {
        return "CurrencyEntryListTuple{" + "amount=" + amount + ", entries=" + entries + '}';
    }
}
