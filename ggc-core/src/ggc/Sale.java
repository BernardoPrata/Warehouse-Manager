package ggc;

public class Sale extends Transaction {
    private int _limitDate = 0;
    private int _paymentDate = 0; // negative to start
    private double _vbase = 0;
    private double _finalPrice = 0; // com multas

    String s = "";

    public Sale(Product product, Partner partner, int amount, int limitDate, double price) {
        super(product, partner, amount, false);
        _limitDate = limitDate;
        _vbase = price; // price to pay on exact day of selling
        partner.increaseSalesValue(price);
    }

    public double getOriginalPrice() {
        return _vbase;
    }

    public double getPriceToPay() {
        return _finalPrice;
    }

    protected void setPriceToPay(double price) {
        // value will be altered many times during course of program,until reaches final
        if (!isPayed())
            _finalPrice = price;
    }

    public int getLimitDate() {
        return _limitDate;
    }

    protected void setPaymentDate(int date) {
        if (!isPayed())
            _paymentDate = date;
    }

    public double calculatePricetoPay(int actualDate) {
        if (!isPayed()) {
            Partner partner = getPartner();
            Product product = getProduct();

            int N = product.getNValue();

            double multa = partner.getPenalty(_limitDate, actualDate, N);

            if (multa == 0) {
                // if penalty =0, must exist a discount (can also be zero)
                double desconto = partner.getDiscount(_limitDate, actualDate, N);
                setPriceToPay(_vbase * (1 - desconto));
                return _vbase * (1 - desconto);
            } else
                setPriceToPay(_vbase * (1 + multa));
            return _vbase * (1 + multa);
        }
        return 0;
    }

    @Override
    public double pay(int actualDate) {
        if (!isPayed()) {
            calculatePricetoPay(actualDate);
            setPaymentDate(actualDate);
            if (actualDate <= _limitDate) {
                getPartner().increasePoints((int) Math.round(10 * _finalPrice));
            } else if (actualDate > _limitDate) {
                getPartner().stateDown(_limitDate, actualDate);

            }
            getPartner().increaseSalesPayedValue(_finalPrice);
            pay();
            return _finalPrice;
        }
        return 0;
    }

    @Override
    public String toString() {
        s = "VENDA|" + getId() + "|" + getPartnerId() + "|" + getProductId() + "|" + getAmount() + "|"
                + Math.round(_vbase) + "|" + Math.round(_finalPrice) + "|" + _limitDate;
        // FALTA AQUI O FINALPRICE É O VALOR QUE TINHAMQUE PAGAR SE FOSSE HOJE
        if (isPayed())
            s += "|" + _paymentDate;
        return s;
    }

    @Override
    public Transaction accept(TransactionVisitor visitor) {
        return visitor.visitSale(this);
    }

    @Override
    public double DifferenceInPrices(PrintPriceSale visitor) {
        return visitor.visitSale(this);
    }
}
