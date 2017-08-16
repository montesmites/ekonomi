package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.model.Currency;

public enum Signedness {
    UNCHANGED_SIGN (1),
    NEGATED_SIGN (-1);
    
    private final int coefficient;
    
    private Signedness(int coefficient) {
        this.coefficient = coefficient;
    }
    
    public long apply(long number) {
        return number * coefficient;
    }
    
    public Currency apply(Currency amount) {
        return new Currency(amount.getAmount() * coefficient);
    }
}
