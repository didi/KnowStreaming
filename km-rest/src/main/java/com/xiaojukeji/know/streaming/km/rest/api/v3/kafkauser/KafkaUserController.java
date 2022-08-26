package com.xiaojukeji.know.streaming.km.rest.api.v3.kafkauser;

import com.didiglobal.logi.security.util.HttpRequestUtil;
import com.xiaojukeji.know.streaming.km.biz.kafkauser.KafkaUserManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.kafkauser.ClusterKafkaUserDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.kafkauser.ClusterKafkaUserTokenDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.kafkauser.KafkaUserParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.kafkauser.KafkaUserTokenVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.core.service.kafkauser.KafkaUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author zengqiao
 * @date 22/02/23
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "KafkaUser-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class KafkaUserController {
    @Autowired
    private KafkaUserService kafkaUserService;

    @Autowired
    private KafkaUserManager kafkaUserManager;

    @ApiOperation(value = "创建KafkaUser", notes = "")
    @PostMapping(value ="kafka-users")
    @ResponseBody
    public Result<Void> createKafkaUser(@Validated @RequestBody ClusterKafkaUserTokenDTO dto) {
        return kafkaUserManager.createKafkaUserWithTokenEncrypted(dto, HttpRequestUtil.getOperator());
    }

    @ApiOperation(value = "删除KafkaUser", notes = "")
    @DeleteMapping(value ="kafka-users")
    @ResponseBody
    public Result<Void> deleteKafkaUser(@Validated @RequestBody ClusterKafkaUserDTO dto) {
        return kafkaUserService.deleteKafkaUser(new KafkaUserParam(dto.getClusterId(), dto.getKafkaUser()), HttpRequestUtil.getOperator());
    }

    @ApiOperation(value = "修改KafkaUser密码", notes = "")
    @PutMapping(value ="kafka-users/token")
    @ResponseBody
    public Result<Void> modifyKafkaUserToken(@Validated @RequestBody ClusterKafkaUserTokenDTO dto) {
        return kafkaUserManager.modifyKafkaUserWithTokenEncrypted(dto, HttpRequestUtil.getOperator());
    }

    @ApiOperation(value = "查看KafkaUser密码", notes = "")
    @GetMapping(value ="clusters/{clusterPhyId}/kafka-users/{kafkaUser}/token")
    @ResponseBody
    public Result<KafkaUserTokenVO> getKafkaUserToken(@PathVariable Long clusterPhyId, @PathVariable String kafkaUser) {
        return kafkaUserManager.getKafkaUserTokenWithEncrypt(clusterPhyId, kafkaUser);
    }
}
