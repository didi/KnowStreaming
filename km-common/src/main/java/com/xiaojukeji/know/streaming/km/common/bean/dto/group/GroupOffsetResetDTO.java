package com.xiaojukeji.know.streaming.km.common.bean.dto.group;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.know.streaming.km.common.bean.dto.partition.PartitionOffsetDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.topic.ClusterTopicDTO;
import com.xiaojukeji.know.streaming.km.common.enums.OffsetTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 重置offset
 * @author zengqiao
 * @date 19/4/8
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupOffsetResetDTO extends ClusterTopicDTO {
    @NotBlank(message = "groupName不允许为空")
    @ApiModelProperty(value = "消费组名称", example = "g-know-streaming")
    private String groupName;

    /**
     * @see OffsetTypeEnum
     */
    @NotNull(message = "resetType不允许为空")
    @ApiModelProperty(value = "重置方式", example = "1")
    private Integer resetType;

    @ApiModelProperty(value = "重置到指定offset")
    private List<PartitionOffsetDTO> offsetList;

    @ApiModelProperty(value = "重置到指定时间")
    private Long timestamp;

    @ApiModelProperty(value = "如果不存在则创建")
    private Boolean createIfNotExist;

    public boolean isCreateIfNotExist() {
        return createIfNotExist != null && createIfNotExist;
    }
}