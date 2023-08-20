package fun.fengwk.automapper.processor.translator;

import java.util.Collections;
import java.util.List;

/**
 * @author fengwk
 */
public class MethodInfo {

    private final String methodName;
    private final List<Param> params;
    private final Return ret;
    private final boolean isDefault;

    public MethodInfo(String methodName, List<Param> params, Return ret, boolean isDefault) {
        this.methodName = methodName;
        this.params = params != null ? params : Collections.emptyList();
        this.ret = ret;
        this.isDefault = isDefault;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<Param> getParams() {
        return params;
    }

    public Return getRet() {
        return ret;
    }

    public boolean isDefault() {
        return isDefault;
    }

}
