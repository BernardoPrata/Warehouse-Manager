package ggc.exceptions;

/**
 * Partner does not exist.
 */
public class NoSuchPartnerException extends Exception {

    /** Serial number. */
    private static final long serialVersionUID = 201608231557L;

    /** The unknown id. */
    private final String _id;

    /**
     * @param id
     */
    public NoSuchPartnerException(String id) {
        _id = id;
    }

    /**
     * @return the id
     */
    public String getId() {
        return _id;
    }

}
