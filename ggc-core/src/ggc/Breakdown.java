package ggc;

import java.util.List;
import java.util.ArrayList;

public class Breakdown extends Transaction {

    // Como breakdwon é compra e Venda, posso fazer isso aqui , ou ser na propria
    // WareHouse
    private double _vBase;
    private double _toPayValue;
    private int _date;
    private List<Batch> _finalBatches = new ArrayList<>(); // ns se é collection

    public Breakdown(Product product, Partner partner, int amount, int date, double difference) {
        super(product, partner, amount, true);
        _date = date;
        _vBase = difference;
    }

    protected void addBatch(Batch batch) {
        _finalBatches.add(batch);
    }

    public List<Batch> getBatches() {
        return _finalBatches;
    }

    protected void setToPayValue(double amount) {
        _toPayValue = amount;
    }

    public double getToPayValue() {
        return _toPayValue;
    }

    public double getVBaseValue() {
        return _vBase;
    }

    protected void setVBase(double amount) {
        _vBase = amount;
    }

    @Override
    public Transaction accept(TransactionVisitor visitor) {
        return visitor.visitBreakdown(this);
    }

    @Override
    public double DifferenceInPrices(PrintPriceSale visitor) {
        return visitor.visitBreakdown(this);
    }

    public String toString() {
        String s = "DESAGREGAÇÃO|" + getId() + "|" + getPartnerId() + "|" + getProductId() + "|" + getAmount() + "|"
                + Math.round(_vBase) + "|" + Math.round(_toPayValue) + "|" + _date + "|";
        int dim = 0;

        DeriveProduct derive = (DeriveProduct) getProduct();

        for (Batch entry : _finalBatches) {
            // Aqui entry.getPrice() x entry.getStock() -> é o preçounitario vs quantidade

            // WRONG, IF BATCH IS SOLD AFTER IT WONT STORE
            s += entry.getProductId() + ":" + derive.getRecipe().get(entry.getProduct()) * getAmount() + ":"
                    + derive.getRecipe().get(entry.getProduct()) * entry.getPrice() * getAmount();
            if (dim++ < _finalBatches.size() - 1)
                s += "#";
        }
        return s;
    }

}
