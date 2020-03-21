package se.montesmites.ekonomi.model.tuple;

import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;

public record AccountIdAmountTuple(AccountId accountId, Currency amount) {

}
