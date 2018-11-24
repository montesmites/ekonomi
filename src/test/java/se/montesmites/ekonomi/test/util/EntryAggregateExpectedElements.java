package se.montesmites.ekonomi.test.util;

import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.model.tuple.AccountIdAmountTuple;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public enum EntryAggregateExpectedElements {
    BY_DATE_20120112 {
        @Override
        public Map<AccountId, Currency> getAggregate(YearId yearId) {
            return asMap(
                    List.of(
                            tuple(yearId, 1650, -1085600),
                            tuple(yearId, 1920, -50000000),
                            tuple(yearId, 1930, -8365353),
                            tuple(yearId, 1940, 50000000),
                            tuple(yearId, 2440, 1463852),
                            tuple(yearId, 2510, 1400000),
                            tuple(yearId, 2710, 3361000),
                            tuple(yearId, 2940, 3527105),
                            tuple(yearId, 3740, -5),
                            tuple(yearId, 3960, 1),
                            tuple(yearId, 6570, 8000),
                            tuple(yearId, 7510, -309000)));
        }
    },
    BY_YEARMONTH_201201 {
        @Override
        public Map<AccountId, Currency> getAggregate(YearId yearId) {
            return asMap(
                    List.of(
                            tuple(yearId, 1400, 12077000),
                            tuple(yearId, 1510, -32556400),
                            tuple(yearId, 1650, -98200),
                            tuple(yearId, 1710, 3200000),
                            tuple(yearId, 1910, -117000),
                            tuple(yearId, 1920, -29543100),
                            tuple(yearId, 1930, -7217100),
                            tuple(yearId, 1940, 50000000),
                            tuple(yearId, 2440, 881799),
                            tuple(yearId, 2510, 1400000),
                            tuple(yearId, 2710, 49700),
                            tuple(yearId, 2920, -711413),
                            tuple(yearId, 2940, -2588),
                            tuple(yearId, 2941, -229645),
                            tuple(yearId, 3041, -12807500),
                            tuple(yearId, 3051, -4370000),
                            tuple(yearId, 3590, -147000),
                            tuple(yearId, 3740, 98),
                            tuple(yearId, 3960, -764999),
                            tuple(yearId, 4010, 15099000),
                            tuple(yearId, 4990, -12077000),
                            tuple(yearId, 5010, 1600000),
                            tuple(yearId, 5090, 100320),
                            tuple(yearId, 5410, 446650),
                            tuple(yearId, 5611, 353544),
                            tuple(yearId, 5615, 371875),
                            tuple(yearId, 6071, 40000),
                            tuple(yearId, 6212, 256800),
                            tuple(yearId, 6250, 44000),
                            tuple(yearId, 6570, 8000),
                            tuple(yearId, 7010, 5188204),
                            tuple(yearId, 7082, 553604),
                            tuple(yearId, 7090, 135413),
                            tuple(yearId, 7210, 4800000),
                            tuple(yearId, 7290, 576000),
                            tuple(yearId, 7385, 413700),
                            tuple(yearId, 7399, -413700),
                            tuple(yearId, 7510, 3220693),
                            tuple(yearId, 7519, 229645),
                            tuple(yearId, 7690, 9600)));
        }
    };

    public abstract Map<AccountId, Currency> getAggregate(YearId yearId);

    private static AccountIdAmountTuple tuple(YearId yearId, int account, long amount) {
        return new AccountIdAmountTuple(
                new AccountId(yearId, Integer.toString(account)), new Currency(amount));
    }

    private static Map<AccountId, Currency> asMap(List<AccountIdAmountTuple> tuples) {
        return tuples
                .stream()
                .collect(toMap(AccountIdAmountTuple::getAccountId, AccountIdAmountTuple::getAmount));
    }
}
