package ggc;

public class PrintAcquisition implements TransactionVisitor {

    public Sale visitSale(Sale sale) {
        return null;
    }

    public Acquisiton visitAcquisiton(Acquisiton acquisiton) {
        return acquisiton;
    }

    public Breakdown visitBreakdown(Breakdown breakdown) {
        return null;
    }
}
