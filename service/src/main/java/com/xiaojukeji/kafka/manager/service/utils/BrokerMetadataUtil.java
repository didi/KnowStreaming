package com.xiaojukeji.kafka.manager.service.utils;

import kafka.admin.BrokerMetadata;
import scala.Option;
import scala.collection.JavaConversions;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by limeng on 2017/10/24
 */
public class BrokerMetadataUtil {

    public static Seq<BrokerMetadata> convert2BrokerMetadata(Seq<Object> brokerListSeq) {

        List<BrokerMetadata> brokerMetadataList = new ArrayList<>();
        scala.collection.Iterator<Object> brokerIter = brokerListSeq.iterator();
        while (brokerIter.hasNext()) {
            Integer brokerId = (Integer) brokerIter.next();
            BrokerMetadata brokerMetadata = new BrokerMetadata(brokerId, Option.<String>empty());
            brokerMetadataList.add(brokerMetadata);
        }
        return JavaConversions.asScalaBuffer(brokerMetadataList).toSeq();
    }
}
