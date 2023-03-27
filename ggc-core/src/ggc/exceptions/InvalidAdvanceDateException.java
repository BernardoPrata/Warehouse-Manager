package ggc.exceptions;

public class InvalidAdvanceDateException extends Exception {
    private final int _wrongDate;

    /**
     * @param wrongDate
     */
    public InvalidAdvanceDateException(int wrongDate) {
        _wrongDate = wrongDate;
    }

    /**
     * @return the wrong amount inserted
     */
    public int getInvalidAmount() {
        return _wrongDate;
    }
}
