package com.xiaojukeji.kafka.manager.service.service.ha.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaResTypeEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASRelationDO;
import com.xiaojukeji.kafka.manager.common.utils.HAUtils;
import com.xiaojukeji.kafka.manager.common.utils.Tuple;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.dao.ha.HaASRelationDao;
import com.xiaojukeji.kafka.manager.service.service.ha.HaASRelationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class HaASRelationServiceImpl implements HaASRelationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HaASRelationServiceImpl.class);

    @Autowired
    private HaASRelationDao haASRelationDao;

    @Override
    public Result<Void> replaceTopicRelationsToDB(Long standbyClusterPhyId, List<HaASRelationDO> topicRelationDOList) {
        try {
            LambdaQueryWrapper<HaASRelationDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(HaASRelationDO::getResType, HaResTypeEnum.TOPIC.getCode());
            lambdaQueryWrapper.eq(HaASRelationDO::getStandbyClusterPhyId, standbyClusterPhyId);

            Map<String, HaASRelationDO> dbRelationMap = haASRelationDao.selectList(lambdaQueryWrapper).stream().collect(Collectors.toMap(HaASRelationDO::getUniqueField, Function.identity()));
            for (HaASRelationDO relationDO: topicRelationDOList) {
                HaASRelationDO dbRelationDO = dbRelationMap.remove(relationDO.getUniqueField());
                if (dbRelationDO == null) {
                    // DB中不存在，则插入新的
                    haASRelationDao.insert(relationDO);
                }
            }

            // dbRelationMap 中剩余的，是需要进行删除的
            for (HaASRelationDO dbRelationDO: dbRelationMap.values()) {
                if (System.currentTimeMillis() - dbRelationDO.getModifyTime().getTime() >= 5 * 1000L) {
                    // 修改时间超过了5分钟了，则进行删除
                    haASRelationDao.deleteById(dbRelationDO.getId());
                }
            }

            return Result.buildSuc();
        } catch (Exception e) {
            LOGGER.error("method=replaceTopicRelationsToDB||standbyClusterPhyId={}||errMsg=exception.", standbyClusterPhyId, e);

            return Result.buildFromRSAndMsg(ResultStatus.MYSQL_ERROR, e.getMessage());
        }
    }

    @Override
    public Result<Void> addHAToDB(HaASRelationDO haASRelationDO) {
        try{
            int count = haASRelationDao.insert(haASRelationDO);
            if (count < 1){
                LOGGER.error("add ha to db failed! haASRelationDO:{}" , haASRelationDO);
                return Result.buildFrom(ResultStatus.MYSQL_ERROR);
            }
        } catch (Exception e) {
            LOGGER.error("add ha to db failed! haASRelationDO:{}" , haASRelationDO);
            return Result.buildFrom(ResultStatus.MYSQL_ERROR);
        }
        return Result.buildSuc();
    }

    @Override
    public Result<Void> deleteById(Long id) {
        try {
            haASRelationDao.deleteById(id);
        } catch (Exception e){
            LOGGER.error("class=HaASRelationServiceImpl||method=deleteById||id={}||errMsg=exception", id, e);
            return Result.buildFrom(ResultStatus.MYSQL_ERROR);
        }
        return Result.buildSuc();
    }

    @Override
    public int updateRelationStatus(Long relationId, Integer newStatus) {
        return haASRelationDao.updateById(new HaASRelationDO(relationId, newStatus));
    }

    @Override
    public int updateById(HaASRelationDO haASRelationDO) {
        return haASRelationDao.updateById(haASRelationDO);
    }

    @Override
    public HaASRelationDO getActiveClusterHAFromDB(Long activeClusterPhyId) {
        LambdaQueryWrapper<HaASRelationDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HaASRelationDO::getActiveClusterPhyId, activeClusterPhyId);
        lambdaQueryWrapper.eq(HaASRelationDO::getResType, HaResTypeEnum.CLUSTER.getCode());

        return haASRelationDao.selectOne(lambdaQueryWrapper);
    }

    @Override
    public HaASRelationDO getSpecifiedHAFromDB(Long activeClusterPhyId, String activeResName,
                                               Long standbyClusterPhyId, String standbyResName,
                                               HaResTypeEnum resTypeEnum) {
        LambdaQueryWrapper<HaASRelationDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        HaASRelationDO relationDO = new HaASRelationDO(
                activeClusterPhyId,
                activeResName,
                standbyClusterPhyId,
                standbyResName,
                resTypeEnum.getCode(),
                HaStatusEnum.UNKNOWN.getCode()
        );
        lambdaQueryWrapper.eq(HaASRelationDO::getUniqueField, relationDO.getUniqueField());

        return haASRelationDao.selectOne(lambdaQueryWrapper);
    }

    @Override
    public HaASRelationDO getHAFromDB(Long firstClusterPhyId, String firstResName, HaResTypeEnum resTypeEnum) {
        List<HaASRelationDO> haASRelationDOS = listAllHAFromDB(firstClusterPhyId, resTypeEnum);
        for(HaASRelationDO haASRelationDO : haASRelationDOS){
            if (haASRelationDO.getActiveResName().equals(firstResName)
                    || haASRelationDO.getActiveResName().equals(firstResName)){
                return haASRelationDO;
            }
        }
        return null;
    }

    @Override
    public List<HaASRelationDO> getStandbyHAFromDB(Long standbyClusterPhyId, HaResTypeEnum resTypeEnum) {
        LambdaQueryWrapper<HaASRelationDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HaASRelationDO::getResType, resTypeEnum.getCode());
        lambdaQueryWrapper.eq(HaASRelationDO::getStandbyClusterPhyId, standbyClusterPhyId);

        return haASRelationDao.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<HaASRelationDO> getActiveHAFromDB(Long activeClusterPhyId, HaResTypeEnum resTypeEnum) {
        LambdaQueryWrapper<HaASRelationDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HaASRelationDO::getResType, resTypeEnum.getCode());
        lambdaQueryWrapper.eq(HaASRelationDO::getActiveClusterPhyId, activeClusterPhyId);

        return haASRelationDao.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<HaASRelationDO> listAllHAFromDB(HaResTypeEnum resTypeEnum) {
        LambdaQueryWrapper<HaASRelationDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HaASRelationDO::getResType, resTypeEnum.getCode());

        return haASRelationDao.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<HaASRelationDO> listAllHAFromDB(Long firstClusterPhyId, HaResTypeEnum resTypeEnum) {
        LambdaQueryWrapper<HaASRelationDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HaASRelationDO::getResType, resTypeEnum.getCode());
        lambdaQueryWrapper.and(lambda ->
                lambda.eq(HaASRelationDO::getActiveClusterPhyId, firstClusterPhyId).or().eq(HaASRelationDO::getStandbyClusterPhyId, firstClusterPhyId)
        );

        // 查询HA列表
        List<HaASRelationDO> doList = haASRelationDao.selectList(lambdaQueryWrapper);
        if (ValidateUtils.isNull(doList)) {
            return new ArrayList<>();
        }

        return doList;
    }

    @Override
    public  Map<String, Set<String>> listAllHAClient(Long firstClusterPhyId, Set<String> kafkaUserSet) {
        LambdaQueryWrapper<HaASRelationDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HaASRelationDO::getResType, HaResTypeEnum.KAFKA_USER_AND_CLIENT.getCode());
        lambdaQueryWrapper.and(lambda ->
                lambda.eq(HaASRelationDO::getActiveClusterPhyId, firstClusterPhyId).or().eq(HaASRelationDO::getStandbyClusterPhyId, firstClusterPhyId)
        );

        // 查询HA列表
        List<HaASRelationDO> doList = haASRelationDao.selectList(lambdaQueryWrapper);
        if (ValidateUtils.isNull(doList)) {
            return new HashMap<>();
        }

        Map<String, Set<String>> haClientMap = new HashMap<>();
        doList.forEach(elem -> {
            Tuple<String, String> data = HAUtils.splitKafkaUserAndClient(elem.getActiveResName());
            if (data == null || !kafkaUserSet.contains(data.getV1())) {
                return;
            }

            haClientMap.putIfAbsent(data.getV1(), new HashSet<>());
            haClientMap.get(data.getV1()).add(data.getV2());
        });

        return haClientMap;
    }

    @Override
    public List<HaASRelationDO> listAllHAFromDB(Long firstClusterPhyId, Long secondClusterPhyId, HaResTypeEnum resTypeEnum) {
        // 查询HA列表
        List<HaASRelationDO> doList = this.listAllHAFromDB(firstClusterPhyId, resTypeEnum);
        if (ValidateUtils.isNull(doList)) {
            return new ArrayList<>();
        }

        if (secondClusterPhyId == null) {
            // 如果为null，则直接返回全部
            return doList;
        }

        // 手动过滤掉不需要的集群
        return doList.stream()
                .filter(elem -> elem.getActiveClusterPhyId().equals(secondClusterPhyId) || elem.getStandbyClusterPhyId().equals(secondClusterPhyId))
                .collect(Collectors.toList());
    }

}
