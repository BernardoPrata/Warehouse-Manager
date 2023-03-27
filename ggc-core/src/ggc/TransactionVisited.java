package ggc;

public interface TransactionVisited {
    public default Transaction accept(TransactionVisitor visitor) {
        return null;
    }
}