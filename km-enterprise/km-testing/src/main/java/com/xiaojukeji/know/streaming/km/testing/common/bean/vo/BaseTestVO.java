package com.xiaojukeji.know.streaming.km.testing.common.bean.vo;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseTesting;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zengqiao
 * @date 21/8/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "用户测试")
@EnterpriseTesting
public class BaseTestVO {
    @ApiModelProperty(value="花费时间, 单位ms", example = "1")
    protected Long costTimeUnitMs;
}
