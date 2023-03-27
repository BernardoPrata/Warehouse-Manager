package ggc;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Partner implements Serializable, Observer {

    /*
     * All information related to the Partner.
     */
    private static final long serialVersionUID = 202110252010L;

    private String _id;
    private String _name;
    private String _address;
    /*
     * Partner attributes pre-defined.
     */
    private int _points = 0;
    private int _purchaseValue = 0;
    private int _salesValue = 0;
    private int _salesPayedValue = 0;

    // DeliveryMethod
    private DeliveryMethod _deliveryMethod = null;
    private PartnerState _state = new Normal(this);

    /** Transactions */
    private List<Transaction> _transactions = new ArrayList<>();

    /** Notifications */
    private List<Notification> _notifications = new ArrayList<>();

    Partner(String id, String name, String address) {
        _id = id;
        _name = name;
        _address = address;
        updateStatus();
    }

    public String getId() {
        return _id;
    }

    public String getAddress() {
        return _address;
    }

    public int getPoints() {
        return _points;
    }

    public DeliveryMethod getDeliveryMethod() {
        return _deliveryMethod;
    }

    protected void increasePurchase(double amount) {
        _purchaseValue += (int) Math.round(amount);
    }

    protected void increaseSalesValue(double amount) {
        _salesValue += (int) Math.round(amount);
    }

    protected void increaseSalesPayedValue(double amount) {
        _salesPayedValue += (int) Math.round(amount);
    }

    protected void increasePoints(int amount) {
        if (amount >= 0) {
            _points += amount;
            updateStatus();
        }
    }

    /**
     * Function to updateStatus according to partnerpoints.
     */
    public void updateStatus() {
        _state.stateUp(getPoints());
    }

    protected void setPoints(int amount) {
        if (amount >= 0) {
            _points = amount;
            updateStatus();
        }
    }

    protected void stateDown() {
        _state.stateDown();
    }

    protected void stateDown(int limitDate, int actualDate) {
        _state.stateDown(limitDate, actualDate);
    }

    public List<Transaction> getTransactions() {
        return _transactions;
    }

    /**
     * @param state the partner state.
     */
    protected void setState(PartnerState state) {
        _state = state;
    }

    public double getDiscount(int limitDate, int actualDate, int N) {
        return _state.getDiscount(limitDate, actualDate, N);
    }

    public double getPenalty(int limitDate, int actualDate, int N) {
        return _state.getPenalty(limitDate, actualDate, N);
    }

    /**
     * @return current status
     */
    public String status() {
        return _state.status();
    }

    public void storeTransaction(Transaction transaction) {
        _transactions.add(transaction);
    }

    public String toStringWNotifications() {
        String s = getToStringWNotifications();
        _notifications = new ArrayList<>();
        return s;
    }

    /**
     * Object to formatted String , combining with the Notifications
     */
    public String getToStringWNotifications() {
        String s = _id + "|" + _name + "|" + _address + "|" + status() + "|" + _points + "|" + _purchaseValue + "|"
                + _salesValue + "|" + _salesPayedValue;
        for (Notification notification : _notifications)
            s += "\n" + notification;
        return s;
    }

    /**
     * Object to formatted String
     */
    @Override
    public String toString() {
        return _id + "|" + _name + "|" + _address + "|" + status() + "|" + _points + "|" + _purchaseValue + "|"
                + _salesValue + "|" + _salesPayedValue;

    }

    public void addToInbox(Notification notification) {
        _notifications.add(notification);
    }

    public boolean hasDeliveryMethod() {
        return _deliveryMethod != null;
    }

    public void accept(Notification notification) {
        if (!hasDeliveryMethod())
            _deliveryMethod = new DeliveryMethodOmission();
        _deliveryMethod.visitPartner(this, notification);
    }
}
