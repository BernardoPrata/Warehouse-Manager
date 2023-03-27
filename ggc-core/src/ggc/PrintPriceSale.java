package ggc;

public class PrintPriceSale {
    // responsible for printing Sales and BreakDowns
    private int _date = -1;

    public PrintPriceSale(int date) {
        _date = date;
    }

    public double visitSale(Sale sale) {
        if (_date >= 0 && !sale.isPayed()) {
            return sale.calculatePricetoPay(_date) - sale.getOriginalPrice(); // when required to update PriceToPay
        }
        return 0;
    }

    public double visitAcquisiton(Acquisiton acquisiton) {
        return 0;
    }

    public double visitBreakdown(Breakdown breakdown) {
        return 0;
    }
}
