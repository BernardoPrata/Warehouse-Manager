package ggc;

public class Normal extends PartnerState {

    private static final long serialVersionUID = 202110252138L;

    public Normal(Partner partner) {
        super(partner);
    }

    public void stateUp(int points) {
        if (points > 2000 && points < 25000)
            _partner.setState(new Selection(_partner));
        else if (points > 25000)
            _partner.setState(new Elite(_partner));
    }

    public void stateDown() {
        _partner.setPoints(0);
        _partner.setState(new Normal(_partner));
    }

    public void stateDown(int limitDate, int actualDate) {
        _partner.setPoints(0);
        _partner.setState(new Normal(_partner));

    }

    public double getDiscount(int limitDate, int actualDate, int N) {
        if (limitDate - actualDate >= N) {
            // P1
            return 0.10;
        } else
            return 0;
    }

    public double getPenalty(int limitDate, int actualDate, int N) {
        if (limitDate - actualDate >= N || (0 <= limitDate - actualDate && limitDate - actualDate < N)) {
            // P1 and P2
            return 0;
        } else if (0 < actualDate - limitDate && actualDate - limitDate <= N) {
            // P3 / penaltty:5% daily
            return (actualDate - limitDate) * 0.05;
        } else
            // P4 / penaltty:10% daily
            return (actualDate - limitDate) * 0.10;

    }
}
