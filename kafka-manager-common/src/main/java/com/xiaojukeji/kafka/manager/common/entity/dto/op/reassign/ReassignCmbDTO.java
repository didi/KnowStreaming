package com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 查询迁移任务
 */
@ApiModel(description = "查询迁移任务")
public class ReassignCmbDTO {

  private static final int DEFAULT_PAGE_NO = 1;
  private static final int DEFAULT_PAGE_SIZE = 10;
  private static final String DEFAULT_FIELD = "begin_time";
  private static final String DEFAULT_SORT = "desc";

  @ApiModelProperty(value = "逻辑集群id")
  private List<Long> logicClusterIds;

  @ApiModelProperty(value = "迁移任务状态")
  private List<Integer> taskStatuss;

  @ApiModelProperty(value = "排序字段")
  private String field;

  @ApiModelProperty(value = "升序/降序")
  private String sort;

  @ApiModelProperty(value = "当前页")
  private Integer pageNo;

  @ApiModelProperty(value = "页面数量")
  private Integer pageSize;

  public List<Long> getLogicClusterIds() {
    return logicClusterIds;
  }

  public void setLogicClusterIds(List<Long> logicClusterIds) {
    this.logicClusterIds = logicClusterIds;
  }

  public List<Integer> getTaskStatuss() {
    return taskStatuss;
  }

  public void setTaskStatuss(List<Integer> taskStatuss) {
    this.taskStatuss = taskStatuss;
  }

  public String getField() {
    return ValidateUtils.isBlank(field) ? DEFAULT_FIELD : field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public String getSort() {
    return ValidateUtils.isBlank(sort) ? DEFAULT_SORT : sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public Integer getPageNo() {
    return pageNo != null && pageNo > 0 ? pageNo : DEFAULT_PAGE_NO;
  }

  public void setPageNo(Integer pageNo) {
    this.pageNo = pageNo;
  }

  public Integer getPageSize() {
    return pageSize != null && pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }
}
