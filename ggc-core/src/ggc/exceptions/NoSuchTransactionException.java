
package ggc.exceptions;

public class NoSuchTransactionException extends Exception {

    /** Serial number. */
    private static final long serialVersionUID = 201608231557L;

    /** The unknown id. */
    private final int _id;

    /**
     * @param id
     */
    public NoSuchTransactionException(int id) {
        _id = id;
    }

    /**
     * @return the id
     */
    public int getId() {
        return _id;
    }

}
