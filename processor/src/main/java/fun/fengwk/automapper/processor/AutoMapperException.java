package fun.fengwk.automapper.processor;

/**
 * @author fengwk
 */
public class AutoMapperException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AutoMapperException(Throwable cause) {
        super(cause);
    }

    public AutoMapperException(String message) {
        super(message);
    }

    public AutoMapperException(String message, Object... args) {
        super(String.format(message, args));
    }

}
