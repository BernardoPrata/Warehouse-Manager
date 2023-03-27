package ggc;

import java.io.Serializable;

public class Notification implements Serializable {
    private String _idProduct;
    private double _price;

    Notification(String idProduct, double price) {
        _idProduct = idProduct;
        _price = price;
    }

    public String getProductId() {
        return _idProduct;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName().toUpperCase() + "|" + _idProduct + "|" + (int) Math.round(_price);
    }
}
