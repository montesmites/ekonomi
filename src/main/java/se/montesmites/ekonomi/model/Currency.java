package se.montesmites.ekonomi.model;

public class Currency {

    private final int decimals;
    private final long amount;
    
    public Currency(long amount) {
        this(2, amount);
    }
    
    public Currency(int decimals, long amount) {
        this.decimals = decimals;
        this.amount = amount;
    }

    public int getDecimals() {
        return decimals;
    }

    public long getAmount() {
        return amount;
    }
    
    public double toDouble() {
        return (double) amount / Math.pow(10, decimals);
    }
    
    public String format() {
        String fmt = "%.2f";
        String msg = String.format(fmt, toDouble());
        return msg;
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
        if (this.decimals != other.decimals) {
            return false;
        }
        return this.amount == other.amount;
    }

    @Override
    public String toString() {
        return "Currency{" + "decimals=" + decimals + ", amount=" + amount + '}';
    }
}
