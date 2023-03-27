package ggc;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import ggc.exceptions.UnAvailableProductException;
import java.util.Comparator;
import java.util.Collections;

public class Product implements Serializable, Subject {
    private String _id = "";
    private double _maxPrice = 0;
    private int _stock = 0;

    private static final long serialVersionUID = 202110262254L;
    private List<Batch> _batches = new ArrayList<>();

    Product(String id, double price, int stock) {
        _id = id;
        _maxPrice = price;
        _stock = stock;
    }

    private Comparator<Batch> comparatorByPrice() {
        class BatchComparator implements Comparator<Batch> {
            public int compare(Batch b1, Batch b2) {
                return b1.getPrice() - b2.getPrice();
            }
        }
        return new BatchComparator();
    }

    public String getId() {
        return _id;
    }

    public void setBatch(List<Batch> batches) {
        _batches = batches;
        // Collections.sort(_batches, comparatorByPrice());
    }

    public double getPrice() {
        return _maxPrice;
    }

    public int getStock() {
        return _stock;
    }

    public void setMaxPrice(double price) {
        _maxPrice = price;
    }

    public void increaseStock(int amount) {
        if (amount > 0)
            _stock += amount;
    }

    public void decreaseStock(int amount) {
        if (amount > 0)
            _stock -= amount;

    }

    /**
     * Object to formatted String
     */
    @Override
    public String toString() {
        return _id + "|" + (int) Math.round(_maxPrice) + "|" + _stock;
    }

    private Set<Observer> _observers = new HashSet<Observer>();

    public void registerObserver(Observer o) {
        _observers.add(o);
    }

    public void removeObserver(Observer o) {
        if (_observers.contains(o))
            _observers.remove(o);
    }

    public boolean isObserver(Observer o) {
        return _observers.contains(o);
    }

    public List<Batch> getBatches() {
        Collections.sort(_batches, comparatorByPrice());
        return _batches;
    }

    public void addBatch(Batch batch) {
        getBatches().add(batch);
        increaseStock(batch.getStock());

    }

    public int getNValue() {
        return 5;
    }

    protected Breakdown breakDown(Partner partner, int amount, int date) throws UnAvailableProductException {
        return null;
    }

    public void notifyObservers(Notification notification) {
        for (Observer observer : _observers){
            observer.accept(notification);
        }
    }

    /**
     * 
     * This function returns price of product according to stock and batches. Also,
     * deletes the requested amount, or the maximum possible, from stock. Sells
     * Product.
     */
    protected double sell(int amount) throws UnAvailableProductException {

        double price = 0;
        if (!canBeBuilt(amount))
            throw new UnAvailableProductException(_id, amount, _stock);
        while (amount != 0 && !_batches.isEmpty()) {
            Batch batch = getBatches().get(0);
            if (batch.getStock() > amount) {
                // if stock is bigger than demand, all fine
                batch.decreaseStock(amount); // also decreases product.getStock()
                price += batch.getPrice() * amount;
                amount = 0;
            } else if (batch.getStock() <= amount) {
                // if stock is less or equal than demand, batch can be removed and amount
                // updated
                decreaseStock(batch.getStock());
                amount -= batch.getStock();
                price += batch.getPrice() * batch.getStock();
                _batches.remove(batch);
            }
        }
        return price;
    }

    public void sendNotification(double newPrice, int amount) {
        // Assumed all deliveries are by omission
        double price = 0;
        if (!getBatches().isEmpty())
            price = getBatches().get(0).getRealPrice();
        else
            price = getPrice();
        // for now only one DeliveryMethod, here we would check Partener
        if (getStock() == 0 && amount > 0)
            notifyObservers(new New(getId(), newPrice));
        else if (newPrice < price) {
            notifyObservers(new Bargain(getId(), newPrice));
        }

    }

    public boolean canBeBuilt(int amount) throws UnAvailableProductException {
        if (amount > _stock) {
            throw new UnAvailableProductException(_id, amount, _stock);
        }
        return true;
    }

}