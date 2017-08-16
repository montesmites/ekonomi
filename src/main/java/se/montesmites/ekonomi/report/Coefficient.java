package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.model.Currency;

public enum Coefficient {
    SAME (1),
    NEGATE (-1);
    
    private final int coefficient;
    
    private Coefficient(int coefficient) {
        this.coefficient = coefficient;
    }
    
    public long apply(long number) {
        return number * coefficient;
    }
    
    public Currency apply(Currency amount) {
        return new Currency(amount.getAmount() * coefficient);
    }
}
