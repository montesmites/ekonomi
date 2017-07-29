package se.montesmites.ekonomi.model.tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.Entry;

public class AccountIdAmountAggregate {

    private final List<AccountIdAmountTuple> tuples;

    public AccountIdAmountAggregate() {
        this(Collections.emptyList());
    }

    public AccountIdAmountAggregate(Entry entry) {
        this(new AccountIdAmountTuple(entry));
    }

    public AccountIdAmountAggregate(AccountIdAmountTuple tuple) {
        this(Arrays.asList(tuple));
    }

    public AccountIdAmountAggregate(List<AccountIdAmountTuple> tuples) {
        this.tuples = tuples;
    }

    public List<AccountIdAmountTuple> getTuples() {
        return tuples;
    }

    public Map<AccountId, Currency> asAccountIdAmountMap() {
        return tuples.stream()
                .collect(toMap(
                        AccountIdAmountTuple::getAccountId,
                        AccountIdAmountTuple::getAmount
                ));
    }

    public AccountIdAmountAggregate merge(AccountIdAmountAggregate that) {
        Set<AccountId> accountsInThis = this.tuples.stream()
                .map(t -> t.getAccountId()).collect(toSet());
        Set<AccountId> accountsInThat = that.tuples.stream()
                .map(t -> t.getAccountId()).collect(
                toSet());
        Set<AccountId> accountsOnlyInThis = accountsInThis.stream()
                .filter(a -> !accountsInThat.contains(a)).collect(toSet());
        Set<AccountId> accountsOnlyInThat = accountsInThat.stream()
                .filter(a -> !accountsInThis.contains(a)).collect(toSet());
        Set<AccountId> accountsInBoth = accountsInThis.stream()
                .filter(a -> accountsInThat.contains(a)).collect(toSet());
        List<AccountIdAmountTuple> list = new ArrayList<>();
        this.tuples.stream()
                .filter(t -> accountsOnlyInThis.contains(t.getAccountId()))
                .forEach(list::add);
        that.tuples.stream()
                .filter(t -> accountsOnlyInThat.contains(t.getAccountId()))
                .forEach(list::add);
        this.tuples.stream()
                .filter(t1 -> accountsInBoth.contains(t1.getAccountId()))
                .forEach(t2 -> list.add(new AccountIdAmountTuple(
                t2.getAccountId(),
                t2.getAmount().add(that.tuples.stream()
                        .filter(t3 -> t3.getAccountId().equals(t2.getAccountId()))
                        .findAny().get().getAmount()))));
        return new AccountIdAmountAggregate(list);
    }
}
