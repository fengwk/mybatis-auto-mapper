package fun.fengwk.automapper.processor.translator;

/**
 * @author fengwk
 */
public class DetectEntityBeanField extends BeanField {

    private final String paramName;

    public DetectEntityBeanField(BeanField beanField, String paramName) {
        super(beanField.getName(), beanField.getFieldName(), beanField.isUseGeneratedKeys(),
            beanField.isSelective(), beanField.getUpdateIncrement(), beanField.getTypeHandler(),
            beanField.isId(), beanField.isExcludeField(), beanField.isIncludeField());
        this.paramName = paramName;
    }

    @Override
    public String getName() {
        return paramName + "." + super.getName();
    }

    @Override
    public String getVariableName() {
        if (getTypeHandler() == null) {
            return getName();
        } else {
            return getName() + ",typeHandler=" + getTypeHandler();
        }
    }

    public String getCollectionItemName() {
        return super.getName();
    }

    public String getCollectionItemVariableName() {
        if (getTypeHandler() == null) {
            return super.getName();
        } else {
            return super.getName() + ",typeHandler=" + getTypeHandler();
        }
    }

}
