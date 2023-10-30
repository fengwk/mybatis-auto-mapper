package fun.fengwk.automapper.processor.translator;

import java.util.Collections;
import java.util.List;

/**
 * @author fengwk
 */
public class MethodInfo {

    private final String methodName;
    private final String methodExpr;
    private final List<Param> params;
    private final Return ret;
    private final boolean isDefault;

    public MethodInfo(String methodName, List<Param> params, Return ret, boolean isDefault) {
        this(methodName, methodName, params, ret, isDefault);
    }

    public MethodInfo(String methodName, String methodExpr, List<Param> params, Return ret, boolean isDefault) {
        this.methodName = methodName;
        this.methodExpr = methodExpr;
        this.params = params != null ? params : Collections.emptyList();
        this.ret = ret;
        this.isDefault = isDefault;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getMethodExpr() {
        return methodExpr;
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
