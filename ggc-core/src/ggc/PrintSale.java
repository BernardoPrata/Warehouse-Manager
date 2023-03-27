package ggc;

public class PrintSale implements TransactionVisitor {
    // responsible for printing Sales and BreakDowns
    private int _date = -1;

    public PrintSale() {
    }

    public PrintSale(int date) {
        _date = date;
    }

    public Sale visitSale(Sale sale) {
        if (_date >= 0 && !sale.isPayed()) {
            sale.calculatePricetoPay(_date); // when required to update PriceToPay
        }
        return sale;
    }

    public Acquisiton visitAcquisiton(Acquisiton acquisiton) {
        return null;
    }

    public Breakdown visitBreakdown(Breakdown breakdown) {
        return breakdown;
    }
}
