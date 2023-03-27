package ggc;

import java.io.Serializable;

public class Batch implements Serializable {

    private static final long serialVersionUID = 202110252330L;

    private Product _product;
    private Partner _partner;
    private int _stock = 0;
    private double _price = 0;

    Batch(Product product, Partner partner, int stock, double price) {
        _product = product;
        _partner = partner;
        _stock = stock;
        _price = price;

    }

    public String getPartnerId() {
        return _partner.getId();

    }

    public String getProductId() {
        return _product.getId();
    }

    public Product getProduct() {
        return _product;
    }

    public int getStock() {
        return _stock;
    }

    public int getPrice() {
        return (int) Math.round(_price);
    }

    public double getRealPrice() {
        return _price;
    }

    public void increaseStock(int amount) {
        /* by now, assumed amount is always >0 */
        _stock += amount;
    }

    public void decreaseStock(int amount) {
        /* by now, assumed amount is always >0 */
        _stock -= amount;
        _product.decreaseStock(amount);
    }

    /**
     * Object to formatted String
     */
    @Override
    public String toString() {
        return getProductId() + "|" + getPartnerId() + "|" + (int) Math.round(_price) + "|" + _stock;
    }

}