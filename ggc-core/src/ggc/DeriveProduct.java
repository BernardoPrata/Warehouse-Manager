package ggc;

import java.util.TreeMap;
import java.util.Map;
import ggc.exceptions.UnAvailableProductException;

public class DeriveProduct extends Product {

    private static final long serialVersionUID = 202110262255L;

    private double _aggravation = 0;
    private Map<Product, Integer> _recipe = new TreeMap<>();

    DeriveProduct(String id, double price, int stock, double aggravation, Map<Product, Integer> recipe) {
        super(id, price, stock);
        _aggravation = aggravation;
        _recipe = recipe;
    }

    public double getAggravation() {
        return _aggravation;
    }

    public Map<Product, Integer> getRecipe() {
        return _recipe;
    }

    @Override
    public int getNValue() {
        return 3;
    }

    /**
     * Object to formatted String
     */
    @Override
    public String toString() {
        String s = new String();
        s = super.toString() + "|" + _aggravation + "|";
        int dim = 0;
        for (Map.Entry<Product, Integer> entry : _recipe.entrySet()) {
            s += entry.getKey().getId() + ":" + entry.getValue();
            if (dim++ < _recipe.size() - 1)
                s += "#";

        }
        return s;
    }

    @Override
    public boolean canBeBuilt(int amount) throws UnAvailableProductException {
        int newAmount = amount - getStock();
        if (amount > getStock()) {
            /**
             * Verify before any changes are made! if amount exceeds stock, since it´s
             * derive product check if more can be create based on the recipe.
             */
            for (Map.Entry<Product, Integer> element : _recipe.entrySet())
                if (!element.getKey().canBeBuilt(element.getValue() * newAmount)) {
                    throw new UnAvailableProductException(element.getKey().getId(), element.getValue() * newAmount,
                            element.getKey().getStock());
                }
        }
        return true;
    }

    @Override
    protected double sell(int amount) throws UnAvailableProductException {
        int newAmount = amount - getStock();
        double price = 0;
        if (canBeBuilt(amount)) {
            // update based on amount in stock
            price += getPriceAccordingToStock(this, amount);
            // Knowing every product exists ,we can remove the stock to sell
            if (newAmount > 0) {
                double priceAggregation = 0;
                for (Map.Entry<Product, Integer> element : _recipe.entrySet()) {
                    Product ProductEl = element.getKey();
                    int quantity = element.getValue() * newAmount;
                    priceAggregation += ProductEl.sell(quantity);
                }
                price += priceAggregation * (1 + _aggravation);
                if ((priceAggregation * (1 + _aggravation) / newAmount) > getPrice())
                    setMaxPrice((priceAggregation * (1 + _aggravation) / newAmount));
            }
        }

        return price;
    }

    private double getPriceAccordingToStock(Product product, int amount) {
        double price = 0;
        while (amount > 0 && !product.getBatches().isEmpty()) {
            Batch batch = product.getBatches().get(0);
            if (batch.getStock() > amount) {
                // if stock is bigger than demand, all fine
                batch.decreaseStock(amount);
                price += batch.getPrice() * amount;
                amount = 0;
            } else if (batch.getStock() <= amount) {
                // if stock is less or equal than demand, batch can be removed and amount
                // updated
                product.decreaseStock(batch.getStock());
                amount -= batch.getStock();
                price += batch.getPrice() * batch.getStock();
                product.getBatches().remove(batch);
            }

        }
        return price;

    }

    @Override
    protected Breakdown breakDown(Partner partner, int amount, int date) throws UnAvailableProductException {
        if (amount > getStock() || amount <= 0)
            throw new UnAvailableProductException(getId(), amount, getStock());
        Breakdown transaction = new Breakdown(this, partner, amount, date, 0);
        double initialPrice = getPriceAccordingToStock(this, amount);

        double finalPrice = 0;
        for (Map.Entry<Product, Integer> element : _recipe.entrySet()) {
            Product elementProduct = element.getKey();
            int quantity = element.getValue() * amount;
            double price = elementProduct.getBatches().isEmpty() ? elementProduct.getPrice()
                    : elementProduct.getBatches().get(0).getRealPrice();
            // sendNotification(elementProduct, price, quantity);
            Batch newBatch = new Batch(elementProduct, partner, quantity, price);
            elementProduct.sendNotification(price, quantity);
            elementProduct.addBatch(newBatch);
            transaction.addBatch(newBatch);
            finalPrice += price * quantity;

        }
        double price = initialPrice - finalPrice;
        transaction.setVBase(price);
        if (price > 0) { // prejuizo para o entreposto
            transaction.setToPayValue(price);
            partner.increasePoints((int) (price) * 10); // update partner points
            // partner pays immediately
        } else
            transaction.setToPayValue(0);

        return transaction;
    }

}
