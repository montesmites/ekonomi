package se.montesmites.ekonomi.model.tuple;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.Entry;

;

public class CurrencyEntryListTuple {

    private final Currency amount;
    private final List<Entry> entries;
    
    public CurrencyEntryListTuple(Entry entry) {
        this(entry.getAmount(), Arrays.asList(entry));
    }
    
    public CurrencyEntryListTuple(Currency amount, List<Entry> entries) {
        this.amount = amount;
        this.entries = entries.stream().collect(toList());
    }

    public Currency getAmount() {
        return amount;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public CurrencyEntryListTuple merge(CurrencyEntryListTuple that) {
        Currency sum = this.getAmount().add(that.getAmount());
        List<Entry> retEntries
                = Stream.concat(
                        this.getEntries().stream(),
                        that.getEntries().stream())
                        .collect(toList());
        return new CurrencyEntryListTuple(sum, retEntries);
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
        final CurrencyEntryListTuple other = (CurrencyEntryListTuple) obj;
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
