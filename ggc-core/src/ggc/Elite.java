package ggc;

public class Elite extends PartnerState {

    private static final long serialVersionUID = 202110252140L;

    public Elite(Partner partner) {
        super(partner);
    }

    public void stateUp(int points) {

    }

    // Será que é visitor aqui??
    public void stateDown(int limitDate, int actualDate) {
        if (actualDate - limitDate > 15) {
            // perde 75% dos pontos
            _partner.setPoints((int) Math.round(_partner.getPoints() * 0.25));
            _partner.setState(new Selection(_partner));
        }
    }

    public void stateDown() {
        _partner.setState(new Selection(_partner));
    }

    public double getDiscount(int limitDate, int actualDate, int N) {
        if (limitDate - actualDate >= N || (0 <= limitDate - actualDate && limitDate - actualDate < N)) {
            // P1 and P2
            return 0.10;
        } else if (0 < actualDate - limitDate && actualDate - limitDate <= N) {
            // P3
            return 0.05;
        } else
            // P4
            return 0;

    }

    public double getPenalty(int limitDate, int actualDate, int N) {
        // no penalties
        return 0;
    }
}
