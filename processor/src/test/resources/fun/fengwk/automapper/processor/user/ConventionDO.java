package fun.fengwk.automapper.processor.user;

import java.time.LocalDateTime;

/**
 * @author fengwk
 */
public class ConventionDO<ID> extends BaseDO<ID> {

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

}
