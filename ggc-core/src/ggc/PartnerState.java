package ggc;

import java.io.Serializable;

public abstract class PartnerState implements Serializable {

    private static final long serialVersionUID = 202110252011L;

    protected Partner _partner;

    public PartnerState(Partner partner) {
        _partner = partner;
    }

    /**
     * @return current status
     */
    public String status() {
        return getClass().getSimpleName().toUpperCase();
    }

    public abstract void stateUp(int points);

    public abstract double getDiscount(int limitDate, int actualDate, int N);

    public abstract double getPenalty(int limitDate, int actualDate, int N);

    public void stateDown(int limitDate, int actualDate) {
    }

    public abstract void stateDown();

}