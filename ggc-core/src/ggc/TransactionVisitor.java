package ggc;

public interface TransactionVisitor {
    public Transaction visitSale(Sale sale);

    public Transaction visitAcquisiton(Acquisiton acquisiton);

    public Transaction visitBreakdown(Breakdown breakdown);
}
