package se.montesmites.ekonomi.model;

import se.montesmites.ekonomi.i18n.Messages;

public class Currency {
    public static Currency of(long amount) {
        return new Currency(amount);
    }

    private final int decimals = 2;
    private final double divisor = Math.pow(10, decimals);
    private final long amount;

    public Currency(long amount) {
        this.amount = amount;
    }

    public Currency add(Currency that) {
        return new Currency(this.amount + that.amount);
    }

    public long getAmount() {
        return amount;
    }

    public int getDecimalPlaces() {
        return decimals;
    }

    public double toDouble() {
        return (double) amount / divisor;
    }

    public String format() {
        return Messages.formatNumber(this);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + this.decimals;
        hash = 79 * hash + (int) (this.amount ^ (this.amount >>> 32));
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
        final Currency other = (Currency) obj;
        return this.amount == other.amount;
    }

    @Override
    public String toString() {
        return "Currency{" + "decimals=" + decimals + ", amount=" + amount + '}';
    }
}
