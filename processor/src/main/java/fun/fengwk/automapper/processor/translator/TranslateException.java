package fun.fengwk.automapper.processor.translator;

import fun.fengwk.automapper.processor.AutoMapperException;

/**
 * @author fengwk
 */
public class TranslateException extends AutoMapperException {

    private static final long serialVersionUID = 1L;

    public TranslateException(String message) {
        super(message);
    }

    public TranslateException(String message, Object... args) {
        super(message, args);
    }

}
