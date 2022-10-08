package com.xiaojukeji.know.streaming.km.common.bean.vo.zookeeper;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wyc
 * @date 2022/9/23
 */
@Data
public class ZnodeStatVO {
    @ApiModelProperty(value = "节点被创建时的事物的ID", example = "0x1f09")
    private Long czxid;

    @ApiModelProperty(value = "创建时间", example = "Sat Mar 16 15:38:34 CST 2019")
    private Long ctime;

    @ApiModelProperty(value = "节点最后一次被修改时的事物的ID", example = "0x1f09")
    private Long mzxid;

    @ApiModelProperty(value = "最后一次修改时间", example = "Sat Mar 16 15:38:34 CST 2019")
    private Long mtime;

    @ApiModelProperty(value = "子节点列表最近一次呗修改的事物ID", example = "0x31")
    private Long pzxid;

    @ApiModelProperty(value = "子节点版本号", example = "0")
    private Integer cversion;

    @ApiModelProperty(value = "数据版本号", example = "0")
    private Integer version;

    @ApiModelProperty(value = "ACL版本号", example = "0")
    private Integer aversion;

    @ApiModelProperty(value = "创建临时节点的事物ID，持久节点事物为0", example = "0")
    private Long ephemeralOwner;

    @ApiModelProperty(value = "数据长度，每个节点都可保存数据", example = "22")
    private Integer dataLength;

    @ApiModelProperty(value = "子节点的个数", example = "6")
    private Integer numChildren;
}
