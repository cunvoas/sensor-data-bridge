package nl.sikken.bertrik.cayenne;

/**
 * <b>FR :</b> Exception levée lors d’erreurs de parsing Cayenne.<br>
 * <b>EN :</b> Exception thrown during Cayenne parsing errors.
 */
public class CayenneException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * @param message the exception message
     */
    public CayenneException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param e the throwable
     */
    public CayenneException(Throwable e) {
        super(e);
    }

}
