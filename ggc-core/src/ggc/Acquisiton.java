package ggc;

public class Acquisiton extends Transaction {
    private double _price;
    private int _paymentDate = -1; // negative to start

    public Acquisiton(Product product, Partner partner, int amount, double price, int date) {
        super(product, partner, amount, true);
        _price = price;
        _paymentDate = date;
        partner.increasePurchase(price * amount);

    }

    public Acquisiton(Product product, Partner partner, int amount, double price) {
        super(product, partner, amount, true);
        _price = price;

    }

    @Override
    public Transaction accept(TransactionVisitor visitor) {
        return visitor.visitAcquisiton(this);
    }

    @Override
    public double DifferenceInPrices(PrintPriceSale visitor) {
        return visitor.visitAcquisiton(this);
    }

    @Override
    public String toString() {
        return "COMPRA|" + getId() + "|" + getPartnerId() + "|" + getProductId() + "|" + getAmount() + "|"
                + Math.round(_price) * getAmount() + "|" + _paymentDate;

    }

}
