package fun.fengwk.automapper.processor.naming;

/**
 * @author fengwk
 */
public class UpperUnderScoreCaseConverter implements NamingConverter {

    private static final LowerUnderScoreCaseConverter lowerUnderScoreCaseConverter = new LowerUnderScoreCaseConverter();

    @Override
    public String convert(String name) {
        name = lowerUnderScoreCaseConverter.convert(name);
        return name.toUpperCase();
    }

}
