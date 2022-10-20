echo "Wait ElasticSearch Start...${SERVER_ES_ADDRESS}"
while true
do
  curl -s --connect-timeout 10 -o /dev/null  http://${SERVER_ES_ADDRESS}/_cat/nodes > /dev/null 2>&1
  if [ "$?" != "0" ];then
    sleep 1s
  else
    echo "ElasticSearch Start Initialize"
    break
  fi
done

curl -s --connect-timeout 10 -o /dev/null -X POST -H 'cache-control: no-cache' -H 'content-type: application/json' http://${SERVER_ES_ADDRESS}/_template/ks_kafka_broker_metric -d '{
    "order" : 10,
    "index_patterns" : [
      "ks_kafka_broker_metric*"
    ],
    "settings" : {
      "index" : {
        "number_of_shards" : "10"
      }
    },
    "mappings" : {
      "properties" : {
        "brokerId" : {
          "type" : "long"
        },
        "routingValue" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "ignore_above" : 256,
              "type" : "keyword"
            }
          }
        },
        "clusterPhyId" : {
          "type" : "long"
        },
        "metrics" : {
          "properties" : {
            "NetworkProcessorAvgIdle" : {
              "type" : "float"
            },
            "UnderReplicatedPartitions" : {
              "type" : "float"
            },
            "BytesIn_min_15" : {
              "type" : "float"
            },
            "HealthCheckTotal" : {
              "type" : "float"
            },
            "RequestHandlerAvgIdle" : {
              "type" : "float"
            },
            "connectionsCount" : {
              "type" : "float"
            },
            "BytesIn_min_5" : {
              "type" : "float"
            },
            "HealthScore" : {
              "type" : "float"
            },
            "BytesOut" : {
              "type" : "float"
            },
            "BytesOut_min_15" : {
              "type" : "float"
            },
            "BytesIn" : {
              "type" : "float"
            },
            "BytesOut_min_5" : {
              "type" : "float"
            },
            "TotalRequestQueueSize" : {
              "type" : "float"
            },
            "MessagesIn" : {
              "type" : "float"
            },
            "TotalProduceRequests" : {
              "type" : "float"
            },
            "HealthCheckPassed" : {
              "type" : "float"
            },
            "TotalResponseQueueSize" : {
              "type" : "float"
            }
          }
        },
        "key" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "ignore_above" : 256,
              "type" : "keyword"
            }
          }
        },
        "timestamp" : {
          "format" : "yyyy-MM-dd HH:mm:ss Z||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd HH:mm:ss.SSS Z||yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd HH:mm:ss,SSS||yyyy/MM/dd HH:mm:ss||yyyy-MM-dd HH:mm:ss,SSS Z||yyyy/MM/dd HH:mm:ss,SSS Z||epoch_millis",
          "index" : true,
          "type" : "date",
          "doc_values" : true
        }
      }
    },
    "aliases" : { }
  }'

curl -s -o /dev/null -X POST -H 'cache-control: no-cache' -H 'content-type: application/json' http://${SERVER_ES_ADDRESS}/_template/ks_kafka_cluster_metric -d '{
    "order" : 10,
    "index_patterns" : [
      "ks_kafka_cluster_metric*"
    ],
    "settings" : {
      "index" : {
        "number_of_shards" : "10"
      }
    },
    "mappings" : {
      "properties" : {
        "routingValue" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "ignore_above" : 256,
              "type" : "keyword"
            }
          }
        },
        "clusterPhyId" : {
          "type" : "long"
        },
        "metrics" : {
          "properties" : {
            "Connections" : {
              "type" : "double"
            },
            "BytesIn_min_15" : {
              "type" : "double"
            },
            "PartitionURP" : {
              "type" : "double"
            },
            "HealthScore_Topics" : {
              "type" : "double"
            },
            "EventQueueSize" : {
              "type" : "double"
            },
            "ActiveControllerCount" : {
              "type" : "double"
            },
            "GroupDeads" : {
              "type" : "double"
            },
            "BytesIn_min_5" : {
              "type" : "double"
            },
            "HealthCheckTotal_Topics" : {
              "type" : "double"
            },
            "Partitions" : {
              "type" : "double"
            },
            "BytesOut" : {
              "type" : "double"
            },
            "Groups" : {
              "type" : "double"
            },
            "BytesOut_min_15" : {
              "type" : "double"
            },
            "TotalRequestQueueSize" : {
              "type" : "double"
            },
            "HealthCheckPassed_Groups" : {
              "type" : "double"
            },
            "TotalProduceRequests" : {
              "type" : "double"
            },
            "HealthCheckPassed" : {
              "type" : "double"
            },
            "TotalLogSize" : {
              "type" : "double"
            },
            "GroupEmptys" : {
              "type" : "double"
            },
            "PartitionNoLeader" : {
              "type" : "double"
            },
            "HealthScore_Brokers" : {
              "type" : "double"
            },
            "Messages" : {
              "type" : "double"
            },
            "Topics" : {
              "type" : "double"
            },
            "PartitionMinISR_E" : {
              "type" : "double"
            },
            "HealthCheckTotal" : {
              "type" : "double"
            },
            "Brokers" : {
              "type" : "double"
            },
            "Replicas" : {
              "type" : "double"
            },
            "HealthCheckTotal_Groups" : {
              "type" : "double"
            },
            "GroupRebalances" : {
              "type" : "double"
            },
            "MessageIn" : {
              "type" : "double"
            },
            "HealthScore" : {
              "type" : "double"
            },
            "HealthCheckPassed_Topics" : {
              "type" : "double"
            },
            "HealthCheckTotal_Brokers" : {
              "type" : "double"
            },
            "PartitionMinISR_S" : {
              "type" : "double"
            },
            "BytesIn" : {
              "type" : "double"
            },
            "BytesOut_min_5" : {
              "type" : "double"
            },
            "GroupActives" : {
              "type" : "double"
            },
            "MessagesIn" : {
              "type" : "double"
            },
            "GroupReBalances" : {
              "type" : "double"
            },
            "HealthCheckPassed_Brokers" : {
              "type" : "double"
            },
            "HealthScore_Groups" : {
              "type" : "double"
            },
            "TotalResponseQueueSize" : {
              "type" : "double"
            },
            "Zookeepers" : {
              "type" : "double"
            },
            "LeaderMessages" : {
              "type" : "double"
            },
            "HealthScore_Cluster" : {
              "type" : "double"
            },
            "HealthCheckPassed_Cluster" : {
              "type" : "double"
            },
            "HealthCheckTotal_Cluster" : {
              "type" : "double"
            }
          }
        },
        "key" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "ignore_above" : 256,
              "type" : "keyword"
            }
          }
        },
        "timestamp" : {
          "format" : "yyyy-MM-dd HH:mm:ss Z||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd HH:mm:ss.SSS Z||yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd HH:mm:ss,SSS||yyyy/MM/dd HH:mm:ss||yyyy-MM-dd HH:mm:ss,SSS Z||yyyy/MM/dd HH:mm:ss,SSS Z||epoch_millis",
          "type" : "date"
        }
      }
    },
    "aliases" : { }
   }'

curl -s -o /dev/null -X POST -H 'cache-control: no-cache' -H 'content-type: application/json' http://${SERVER_ES_ADDRESS}/_template/ks_kafka_group_metric -d '{
    "order" : 10,
    "index_patterns" : [
      "ks_kafka_group_metric*"
    ],
    "settings" : {
      "index" : {
        "number_of_shards" : "10"
      }
    },
    "mappings" : {
      "properties" : {
        "group" : {
          "type" : "keyword"
        },
        "partitionId" : {
          "type" : "long"
        },
        "routingValue" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "ignore_above" : 256,
              "type" : "keyword"
            }
          }
        },
        "clusterPhyId" : {
          "type" : "long"
        },
        "topic" : {
          "type" : "keyword"
        },
        "metrics" : {
          "properties" : {
            "HealthScore" : {
              "type" : "float"
            },
            "Lag" : {
              "type" : "float"
            },
            "OffsetConsumed" : {
              "type" : "float"
            },
            "HealthCheckTotal" : {
              "type" : "float"
            },
            "HealthCheckPassed" : {
              "type" : "float"
            }
          }
        },
        "groupMetric" : {
          "type" : "keyword"
        },
        "key" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "ignore_above" : 256,
              "type" : "keyword"
            }
          }
        },
        "timestamp" : {
          "format" : "yyyy-MM-dd HH:mm:ss Z||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd HH:mm:ss.SSS Z||yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd HH:mm:ss,SSS||yyyy/MM/dd HH:mm:ss||yyyy-MM-dd HH:mm:ss,SSS Z||yyyy/MM/dd HH:mm:ss,SSS Z||epoch_millis",
          "index" : true,
          "type" : "date",
          "doc_values" : true
        }
      }
    },
    "aliases" : { }
  }'

curl -s -o /dev/null -X POST -H 'cache-control: no-cache' -H 'content-type: application/json' http://${SERVER_ES_ADDRESS}/_template/ks_kafka_partition_metric -d '{
    "order" : 10,
    "index_patterns" : [
      "ks_kafka_partition_metric*"
    ],
    "settings" : {
      "index" : {
        "number_of_shards" : "10"
      }
    },
    "mappings" : {
      "properties" : {
        "brokerId" : {
          "type" : "long"
        },
        "partitionId" : {
          "type" : "long"
        },
        "routingValue" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "ignore_above" : 256,
              "type" : "keyword"
            }
          }
        },
        "clusterPhyId" : {
          "type" : "long"
        },
        "topic" : {
          "type" : "keyword"
        },
        "metrics" : {
          "properties" : {
            "LogStartOffset" : {
              "type" : "float"
            },
            "Messages" : {
              "type" : "float"
            },
            "LogEndOffset" : {
              "type" : "float"
            }
          }
        },
        "key" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "ignore_above" : 256,
              "type" : "keyword"
            }
          }
        },
        "timestamp" : {
          "format" : "yyyy-MM-dd HH:mm:ss Z||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd HH:mm:ss.SSS Z||yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd HH:mm:ss,SSS||yyyy/MM/dd HH:mm:ss||yyyy-MM-dd HH:mm:ss,SSS Z||yyyy/MM/dd HH:mm:ss,SSS Z||epoch_millis",
          "index" : true,
          "type" : "date",
          "doc_values" : true
        }
      }
    },
    "aliases" : { }
  }'

curl -s -o /dev/null -X POST -H 'cache-control: no-cache' -H 'content-type: application/json' http://${SERVER_ES_ADDRESS}/_template/ks_kafka_replication_metric -d '{
    "order" : 10,
    "index_patterns" : [
      "ks_kafka_replication_metric*"
    ],
    "settings" : {
      "index" : {
        "number_of_shards" : "10"
      }
    },
    "mappings" : {
      "properties" : {
        "brokerId" : {
          "type" : "long"
        },
        "partitionId" : {
          "type" : "long"
        },
        "routingValue" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "ignore_above" : 256,
              "type" : "keyword"
            }
          }
        },
        "clusterPhyId" : {
          "type" : "long"
        },
        "topic" : {
          "type" : "keyword"
        },
        "metrics" : {
          "properties" : {
            "LogStartOffset" : {
              "type" : "float"
            },
            "Messages" : {
              "type" : "float"
            },
            "LogEndOffset" : {
              "type" : "float"
            }
          }
        },
        "key" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "ignore_above" : 256,
              "type" : "keyword"
            }
          }
        },
        "timestamp" : {
          "format" : "yyyy-MM-dd HH:mm:ss Z||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd HH:mm:ss.SSS Z||yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd HH:mm:ss,SSS||yyyy/MM/dd HH:mm:ss||yyyy-MM-dd HH:mm:ss,SSS Z||yyyy/MM/dd HH:mm:ss,SSS Z||epoch_millis",
          "index" : true,
          "type" : "date",
          "doc_values" : true
        }
      }
    },
    "aliases" : { }
  }'

curl -s -o /dev/null -X POST -H 'cache-control: no-cache' -H 'content-type: application/json' http://${SERVER_ES_ADDRESS}/_template/ks_kafka_topic_metric -d '{
    "order" : 10,
    "index_patterns" : [
      "ks_kafka_topic_metric*"
    ],
    "settings" : {
      "index" : {
        "number_of_shards" : "10"
      }
    },
    "mappings" : {
      "properties" : {
        "brokerId" : {
          "type" : "long"
        },
        "routingValue" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "ignore_above" : 256,
              "type" : "keyword"
            }
          }
        },
        "topic" : {
          "type" : "keyword"
        },
        "clusterPhyId" : {
          "type" : "long"
        },
        "metrics" : {
          "properties" : {
            "BytesIn_min_15" : {
              "type" : "float"
            },
            "Messages" : {
              "type" : "float"
            },
            "BytesRejected" : {
              "type" : "float"
            },
            "PartitionURP" : {
              "type" : "float"
            },
            "HealthCheckTotal" : {
              "type" : "float"
            },
            "ReplicationCount" : {
              "type" : "float"
            },
            "ReplicationBytesOut" : {
              "type" : "float"
            },
            "ReplicationBytesIn" : {
              "type" : "float"
            },
            "FailedFetchRequests" : {
              "type" : "float"
            },
            "BytesIn_min_5" : {
              "type" : "float"
            },
            "HealthScore" : {
              "type" : "float"
            },
            "LogSize" : {
              "type" : "float"
            },
            "BytesOut" : {
              "type" : "float"
            },
            "BytesOut_min_15" : {
              "type" : "float"
            },
            "FailedProduceRequests" : {
              "type" : "float"
            },
            "BytesIn" : {
              "type" : "float"
            },
            "BytesOut_min_5" : {
              "type" : "float"
            },
            "MessagesIn" : {
              "type" : "float"
            },
            "TotalProduceRequests" : {
              "type" : "float"
            },
            "HealthCheckPassed" : {
              "type" : "float"
            }
          }
        },
        "brokerAgg" : {
          "type" : "keyword"
        },
        "key" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "ignore_above" : 256,
              "type" : "keyword"
            }
          }
        },
        "timestamp" : {
          "format" : "yyyy-MM-dd HH:mm:ss Z||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd HH:mm:ss.SSS Z||yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd HH:mm:ss,SSS||yyyy/MM/dd HH:mm:ss||yyyy-MM-dd HH:mm:ss,SSS Z||yyyy/MM/dd HH:mm:ss,SSS Z||epoch_millis",
          "index" : true,
          "type" : "date",
          "doc_values" : true
        }
      }
    },
    "aliases" : { }
  }'

curl -s -o /dev/null -X POST -H 'cache-control: no-cache' -H 'content-type: application/json' http://${SERVER_ES_ADDRESS}/_template/ks_kafka_zookeeper_metric -d '{
    "order" : 10,
    "index_patterns" : [
      "ks_kafka_zookeeper_metric*"
    ],
    "settings" : {
      "index" : {
        "number_of_shards" : "10"
      }
    },
    "mappings" : {
      "properties" : {
        "routingValue" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "ignore_above" : 256,
              "type" : "keyword"
            }
          }
        },
        "clusterPhyId" : {
          "type" : "long"
        },
        "metrics" : {
          "properties" : {
            "AvgRequestLatency" : {
              "type" : "double"
            },
            "MinRequestLatency" : {
              "type" : "double"
            },
            "MaxRequestLatency" : {
              "type" : "double"
            },
            "OutstandingRequests" : {
              "type" : "double"
            },
            "NodeCount" : {
              "type" : "double"
            },
            "WatchCount" : {
              "type" : "double"
            },
            "NumAliveConnections" : {
              "type" : "double"
            },
            "PacketsReceived" : {
              "type" : "double"
            },
            "PacketsSent" : {
              "type" : "double"
            },
            "EphemeralsCount" : {
              "type" : "double"
            },
            "ApproximateDataSize" : {
              "type" : "double"
            },
            "OpenFileDescriptorCount" : {
              "type" : "double"
            },
            "MaxFileDescriptorCount" : {
              "type" : "double"
            }
          }
        },
        "key" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "ignore_above" : 256,
              "type" : "keyword"
            }
          }
        },
        "timestamp" : {
          "format" : "yyyy-MM-dd HH:mm:ss Z||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd HH:mm:ss.SSS Z||yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd HH:mm:ss,SSS||yyyy/MM/dd HH:mm:ss||yyyy-MM-dd HH:mm:ss,SSS Z||yyyy/MM/dd HH:mm:ss,SSS Z||epoch_millis",
          "type" : "date"
        }
      }
    },
    "aliases" : { }
  }'

for i in {0..6};
do
    logdate=_$(date -d "${i} day ago" +%Y-%m-%d)
    curl -s --connect-timeout 10 -o /dev/null -X PUT http://${SERVER_ES_ADDRESS}/ks_kafka_broker_metric${logdate}  && \
    curl -s -o /dev/null -X PUT http://${SERVER_ES_ADDRESS}/ks_kafka_cluster_metric${logdate} && \
    curl -s -o /dev/null -X PUT http://${SERVER_ES_ADDRESS}/ks_kafka_group_metric${logdate} && \
    curl -s -o /dev/null -X PUT http://${SERVER_ES_ADDRESS}/ks_kafka_partition_metric${logdate} && \
    curl -s -o /dev/null -X PUT http://${SERVER_ES_ADDRESS}/ks_kafka_replication_metric${logdate} && \
    curl -s -o /dev/null -X PUT http://${SERVER_ES_ADDRESS}/ks_kafka_zookeeper_metric${logdate} && \
    curl -s -o /dev/null -X PUT http://${SERVER_ES_ADDRESS}/ks_kafka_topic_metric${logdate} || \
    exit 2
done

echo "ElasticSearch Initialize Success"
