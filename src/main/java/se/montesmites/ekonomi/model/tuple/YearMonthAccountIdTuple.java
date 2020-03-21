package se.montesmites.ekonomi.model.tuple;

import java.time.YearMonth;
import se.montesmites.ekonomi.model.AccountId;

public record YearMonthAccountIdTuple(YearMonth yearMonth, AccountId accountId) {

}
