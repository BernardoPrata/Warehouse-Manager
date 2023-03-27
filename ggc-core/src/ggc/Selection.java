package ggc;

public class Selection extends PartnerState {

    private static final long serialVersionUID = 202110252130L;

    public Selection(Partner partner) {
        super(partner);
    }

    public void stateUp(int points) {
        if (points > 25000) {
            _partner.setState(new Elite(_partner));

        }
    }

    public void stateDown(int limitDate, int actualDate) {
        if (actualDate - limitDate > 2) {
            // perde 90% dos pontos
            _partner.setPoints((int) Math.round(_partner.getPoints() * 0.10));
            _partner.setState(new Normal(_partner));
        }
    }

    public void stateDown() {
        _partner.setState(new Normal(_partner));
    }

    public double getDiscount(int limitDate, int actualDate, int N) {
        if (limitDate - actualDate >= N) {
            // P1
            return 0.10;
        } else if (0 <= limitDate - actualDate && limitDate - actualDate < N) {
            // P2 -> only if ≥ 2 days before limitDate get 5%discount
            if (limitDate - actualDate >= 2)
                return 0.05;
            return 0;
        } else
            return 0;
    }

    public double getPenalty(int limitDate, int actualDate, int N) {
        if (limitDate - actualDate >= N || (0 <= limitDate - actualDate && limitDate - actualDate < N)) {
            // P1 and P2
            return 0;
        } else if (0 < actualDate - limitDate && actualDate - limitDate <= N) {
            // > 1 dia depois da data limite: 2% diários (0, caso contrário)
            if (actualDate - limitDate > 1)
                return 0.02 * (actualDate - limitDate);
            return 0;
        } else // if (actualDate - limitDate > N) {
               // P4
            return (actualDate - limitDate) * 0.05;

    }

}
