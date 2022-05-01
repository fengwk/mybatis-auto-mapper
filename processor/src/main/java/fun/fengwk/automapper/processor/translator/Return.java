package fun.fengwk.automapper.processor.translator;

import java.util.List;

/**
 * @author fengwk
 */
public class Return {

    /**
     * 类型。
     */
    private final String type;

    /**
     * 是否为javaBean。
     */
    private final boolean isJavaBean;

    /**
     * 如果当前参数是javaBean，如果当前对象是集合则判断其泛型对象是否为javaBean，那么该参数有值，否则为null。
     */
    private final List<BeanField> beanFields;

    public Return(String type, boolean isJavaBean, List<BeanField> beanFields) {
        this.type = type;
        this.isJavaBean = isJavaBean;
        this.beanFields = beanFields;
    }

    public String getType() {
        return type;
    }

    public boolean isJavaBean() {
        return isJavaBean;
    }

    public List<BeanField> getBeanFields() {
        return beanFields;
    }
}
