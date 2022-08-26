package com.xiaojukeji.know.streaming.km.core.service.km.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.po.km.KmNodePO;
import com.xiaojukeji.know.streaming.km.common.utils.NetUtils;
import com.xiaojukeji.know.streaming.km.core.service.km.KmNodeService;
import com.xiaojukeji.know.streaming.km.persistence.mysql.km.KmNodeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KmNodeServiceImpl implements KmNodeService {
    private static final ILog LOGGER = LogFactory.getLog(KmNodeServiceImpl.class);

    public static final long CLEAR_INTERVAL = 3 * 10 * 1000L;

    @Autowired
    private KmNodeDAO kmNodeDAO;

    @Value("${spring.application.name}")
    private String appName;

    @PostConstruct
    public void init(){
        beat();
    }

    @Override
    public List<String> listKmHosts(){
        List<KmNodePO> kmNodePOS = listByAppName(appName);

        if(CollectionUtils.isEmpty(kmNodePOS)){return new ArrayList<>();}

        return kmNodePOS.stream().map(KmNodePO::getHostName).collect(Collectors.toList());
    }

    @Scheduled(cron="0/10 * * * * ?")
    public boolean beat() {
        clear();

        try {
            String host = NetUtils.localHost();
            KmNodePO kmNodePO = selectByHostAndAppName(host, appName);
            if(null != kmNodePO){
                updateBeatTime(kmNodePO);
            }else {
                kmNodePO = new KmNodePO();
                kmNodePO.setBeatTime(new Date());
                kmNodePO.setHostName(host);
                kmNodePO.setIp(NetUtils.localIp());
                kmNodePO.setAppName(appName);

                kmNodeDAO.insert(kmNodePO);
            }
            return true;
        }catch (Exception e){
            LOGGER.error("method=beat||msg=exception!", e);
        }

        return false;
    }

    /**************************************************** private method ****************************************************/
    private void clear(){
        long currentTime = System.currentTimeMillis();

        List<KmNodePO> kmNodePOS = listByAppName(appName);
        if(CollectionUtils.isEmpty(kmNodePOS)){return;}

        for (KmNodePO kmNodePO : kmNodePOS) {
            if (kmNodePO.getBeatTime().getTime() + CLEAR_INTERVAL < currentTime) {
                kmNodeDAO.deleteById(kmNodePO.getId());
            }
        }
    }

    private List<KmNodePO> listByAppName(String appName){
        LambdaQueryWrapper<KmNodePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KmNodePO::getAppName, appName);

        return kmNodeDAO.selectList(queryWrapper);
    }

    private KmNodePO selectByHostAndAppName(String hostName, String appName){
        LambdaQueryWrapper<KmNodePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KmNodePO::getHostName, hostName);
        queryWrapper.eq(KmNodePO::getAppName, appName);

        return kmNodeDAO.selectOne(queryWrapper);
    }

    int updateBeatTime(KmNodePO kmNodePO){
        kmNodePO.setBeatTime(new Date());

        return kmNodeDAO.updateById(kmNodePO);
    }
}
