package ggc;

import java.io.Serializable;

public class Transaction implements Serializable, TransactionVisited {
    private int _id = 0;
    private int _amount = 0;
    private Product _product;
    private Partner _partner;
    private boolean _isPayed = false;

    public int getId() {
        return _id;
    }

    public int getAmount() {
        return _amount;
    }

    public String getPartnerId() {
        return _partner.getId();
    }

    public String getProductId() {
        return _product.getId();
    }

    protected void setId(int id) {
        _id = id;
    }

    public Product getProduct() {
        return _product;
    }

    public Partner getPartner() {
        return _partner;
    }

    public boolean isPayed() {
        return _isPayed;
    }

    public void pay() {
        if (!_isPayed)
            _isPayed = true;
    }

    public double DifferenceInPrices(PrintPriceSale visitor) {
        return 0;
    }

    public double pay(int actualDate) {
        return 0.0;
    }

    public Transaction(int id, Product product, Partner partner, int amount, boolean isPayed) {
        _id = id;
        _amount = amount;
        _product = product;
        _partner = partner;
        _isPayed = isPayed;
    }

    public Transaction(Product product, Partner partner, int amount, boolean isPayed) {
        _amount = amount;
        _product = product;
        _partner = partner;
        _isPayed = isPayed;
    }
}
