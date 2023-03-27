package ggc.exceptions;

public class DuplicatePartnerException extends Exception {

    /** The partner id */
    private final String _id;

    /**
     * @param id
     */
    public DuplicatePartnerException(String id) {
        _id = id;
    }

    /**
     * @return the holder id
     */
    public String getId() {
        return _id;
    }
}
