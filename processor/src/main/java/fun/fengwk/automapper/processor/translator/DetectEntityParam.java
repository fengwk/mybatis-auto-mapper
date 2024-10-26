package fun.fengwk.automapper.processor.translator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
public class DetectEntityParam extends Param {

    public DetectEntityParam(Param param) {
        super(param.getFullType(), param.getType(), param.getName(), param.getFieldName(),
            param.isInferredName(), param.isInferredFieldName(), param.isIterable(), param.isJavaBean(),
            convertToDetectEntityBeanFields(param.getBeanFields(), param.getName()), param.isSelective(), param.isDynamicOrderBy());
    }

    private static List<BeanField> convertToDetectEntityBeanFields(List<BeanField> bfs, String paramName) {
        if (bfs == null) {
            return null;
        }
        return bfs.stream().map(bf -> new DetectEntityBeanField(bf, paramName)).collect(Collectors.toList());
    }

}
