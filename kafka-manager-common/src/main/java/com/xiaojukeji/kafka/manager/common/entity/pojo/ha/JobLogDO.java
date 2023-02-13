package com.xiaojukeji.kafka.manager.common.entity.pojo.ha;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.kafka.manager.common.entity.pojo.BaseDO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@TableName("job_log")
public class JobLogDO extends BaseDO {
    /**
     * 业务类型
     */
    private Integer bizType;

    /**
     * 业务关键字
     */
    private String bizKeyword;

    /**
     * 打印时间
     */
    private Date printTime;

    /**
     * 内容
     */
    private String content;

    public JobLogDO(Integer bizType, String bizKeyword) {
        this.bizType = bizType;
        this.bizKeyword = bizKeyword;
    }

    public JobLogDO(Integer bizType, String bizKeyword, Date printTime, String content) {
        this.bizType = bizType;
        this.bizKeyword = bizKeyword;
        this.printTime = printTime;
        this.content = content;
    }

    public JobLogDO setAndCopyNew(Date printTime, String content) {
        return new JobLogDO(this.bizType, this.bizKeyword, printTime, content);
    }
}
