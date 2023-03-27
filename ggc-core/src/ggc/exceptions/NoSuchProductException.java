package ggc.exceptions;

public class NoSuchProductException extends Exception {

    /** Serial number. */
    private static final long serialVersionUID = 201608231557L;

    /** The unknown id. */
    private final String _id;

    /**
     * @param id
     */
    public NoSuchProductException(String id) {
        _id = id;
    }

    /**
     * @return the id
     */
    public String getId() {
        return _id;
    }

}
