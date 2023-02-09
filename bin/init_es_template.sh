esaddr=127.0.0.1
port=8060
curl -s --connect-timeout 10 -o /dev/null  http://${esaddr}:${port}/_cat/nodes >/dev/null 2>&1 
if [ "$?" != "0" ];then
    echo "Elasticserach 访问失败, 请安装完后检查并重新执行该脚本 "
    exit
fi

curl -s --connect-timeout 10 -o /dev/null -X POST -H 'cache-control: no-cache' -H 'content-type: application/json' http://${esaddr}:${port}/_template/ks_kafka_broker_metric -d '{
    "order" : 10,
    "index_patterns" : [
      "ks_kafka_broker_metric*"
    ],
    "settings" : {
      "index" : {
        "number_of_shards" : "2"
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

curl -s -o /dev/null -X POST -H 'cache-control: no-cache' -H 'content-type: application/json' http://${esaddr}:${port}/_template/ks_kafka_cluster_metric -d '{
    "order" : 10,
    "index_patterns" : [
      "ks_kafka_cluster_metric*"
    ],
    "settings" : {
      "index" : {
        "number_of_shards" : "2"
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

curl -s -o /dev/null -X POST -H 'cache-control: no-cache' -H 'content-type: application/json' http://${esaddr}:${port}/_template/ks_kafka_group_metric -d '{
    "order" : 10,
    "index_patterns" : [
      "ks_kafka_group_metric*"
    ],
    "settings" : {
      "index" : {
        "number_of_shards" : "6"
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

curl -s -o /dev/null -X POST -H 'cache-control: no-cache' -H 'content-type: application/json' http://${esaddr}:${port}/_template/ks_kafka_partition_metric -d '{
    "order" : 10,
    "index_patterns" : [
      "ks_kafka_partition_metric*"
    ],
    "settings" : {
      "index" : {
        "number_of_shards" : "6"
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

curl -s -o /dev/null -X POST -H 'cache-control: no-cache' -H 'content-type: application/json' http://${esaddr}:${port}/_template/ks_kafka_topic_metric -d '{
    "order" : 10,
    "index_patterns" : [
      "ks_kafka_topic_metric*"
    ],
    "settings" : {
      "index" : {
        "number_of_shards" : "6"
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
        "number_of_shards" : "2"
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

curl -s -o /dev/null -X POST -H 'cache-control: no-cache' -H 'content-type: application/json' http://${SERVER_ES_ADDRESS}/_template/ks_kafka_connect_cluster_metric -d '{
    "order" : 10,
    "index_patterns" : [
      "ks_kafka_connect_cluster_metric*"
    ],
    "settings" : {
      "index" : {
        "number_of_shards" : "2"
      }
    },
    "mappings" : {
      "properties" : {
        "connectClusterId" : {
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
            "ConnectorCount" : {
              "type" : "float"
            },
            "TaskCount" : {
              "type" : "float"
            },
            "ConnectorStartupAttemptsTotal" : {
              "type" : "float"
            },
            "ConnectorStartupFailurePercentage" : {
              "type" : "float"
            },
            "ConnectorStartupFailureTotal" : {
              "type" : "float"
            },
            "ConnectorStartupSuccessPercentage" : {
              "type" : "float"
            },
            "ConnectorStartupSuccessTotal" : {
              "type" : "float"
            },
            "TaskStartupAttemptsTotal" : {
              "type" : "float"
            },
            "TaskStartupFailurePercentage" : {
              "type" : "float"
            },
            "TaskStartupFailureTotal" : {
              "type" : "float"
            },
            "TaskStartupSuccessPercentage" : {
              "type" : "float"
            },
            "TaskStartupSuccessTotal" : {
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

curl -s -o /dev/null -X POST -H 'cache-control: no-cache' -H 'content-type: application/json' http://${SERVER_ES_ADDRESS}/_template/ks_kafka_connect_connector_metric -d '{
    "order" : 10,
    "index_patterns" : [
      "ks_kafka_connect_connector_metric*"
    ],
    "settings" : {
      "index" : {
        "number_of_shards" : "2"
      }
    },
    "mappings" : {
      "properties" : {
        "connectClusterId" : {
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
        "connectorName" : {
          "type" : "keyword"
        },
        "connectorNameAndClusterId" : {
          "type" : "keyword"
        },
        "clusterPhyId" : {
          "type" : "long"
        },
        "metrics" : {
          "properties" : {
            "HealthState" : {
              "type" : "float"
            },
            "ConnectorTotalTaskCount" : {
              "type" : "float"
            },
            "HealthCheckPassed" : {
              "type" : "float"
            },
            "HealthCheckTotal" : {
              "type" : "float"
            },
            "ConnectorRunningTaskCount" : {
              "type" : "float"
            },
            "ConnectorPausedTaskCount" : {
              "type" : "float"
            },
            "ConnectorFailedTaskCount" : {
              "type" : "float"
            },
            "ConnectorUnassignedTaskCount" : {
              "type" : "float"
            },
            "BatchSizeAvg" : {
              "type" : "float"
            },
            "BatchSizeMax" : {
              "type" : "float"
            },
            "OffsetCommitAvgTimeMs" : {
              "type" : "float"
            },
            "OffsetCommitMaxTimeMs" : {
              "type" : "float"
            },
             "OffsetCommitFailurePercentage" : {
               "type" : "float"
             },
             "OffsetCommitSuccessPercentage" : {
               "type" : "float"
             },
             "PollBatchAvgTimeMs" : {
               "type" : "float"
             },
             "PollBatchMaxTimeMs" : {
               "type" : "float"
             },
             "SourceRecordActiveCount" : {
               "type" : "float"
             },
             "SourceRecordActiveCountAvg" : {
               "type" : "float"
             },
             "SourceRecordActiveCountMax" : {
               "type" : "float"
             },
             "SourceRecordPollRate" : {
               "type" : "float"
             },
             "SourceRecordPollTotal" : {
               "type" : "float"
             },
             "SourceRecordWriteRate" : {
               "type" : "float"
             },
             "SourceRecordWriteTotal" : {
               "type" : "float"
             },
           "OffsetCommitCompletionRate" : {
             "type" : "float"
           },
           "OffsetCommitCompletionTotal" : {
             "type" : "float"
           },
           "OffsetCommitSkipRate" : {
             "type" : "float"
           },
           "OffsetCommitSkipTotal" : {
             "type" : "float"
           },
           "PartitionCount" : {
             "type" : "float"
           },
           "PutBatchAvgTimeMs" : {
             "type" : "float"
           },
           "PutBatchMaxTimeMs" : {
             "type" : "float"
           },
           "SinkRecordActiveCount" : {
             "type" : "float"
           },
           "SinkRecordActiveCountAvg" : {
             "type" : "float"
           },
           "SinkRecordActiveCountMax" : {
             "type" : "float"
           },
           "SinkRecordLagMax" : {
             "type" : "float"
           },
           "SinkRecordReadRate" : {
             "type" : "float"
           },
           "SinkRecordReadTotal" : {
             "type" : "float"
           },
           "SinkRecordSendRate" : {
             "type" : "float"
           },
           "SinkRecordSendTotal" : {
             "type" : "float"
           },
           "DeadletterqueueProduceFailures" : {
             "type" : "float"
           },
           "DeadletterqueueProduceRequests" : {
             "type" : "float"
           },
           "LastErrorTimestamp" : {
             "type" : "float"
           },
           "TotalErrorsLogged" : {
             "type" : "float"
           },
           "TotalRecordErrors" : {
             "type" : "float"
           },
           "TotalRecordFailures" : {
             "type" : "float"
           },
           "TotalRecordsSkipped" : {
             "type" : "float"
           },
           "TotalRetries" : {
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

curl -s -o /dev/null -X POST -H 'cache-control: no-cache' -H 'content-type: application/json' http://${SERVER_ES_ADDRESS}/_template/ks_kafka_connect_mirror_maker_metric -d '{
    "order" : 10,
    "index_patterns" : [
      "ks_kafka_connect_mirror_maker_metric*"
    ],
    "settings" : {
      "index" : {
        "number_of_shards" : "2"
      }
    },
    "mappings" : {
      "properties" : {
        "connectClusterId" : {
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
        "connectorName" : {
          "type" : "keyword"
        },
        "connectorNameAndClusterId" : {
          "type" : "keyword"
        },
        "clusterPhyId" : {
          "type" : "long"
        },
        "metrics" : {
          "properties" : {
            "HealthState" : {
              "type" : "float"
            },
            "HealthCheckTotal" : {
              "type" : "float"
            },
            "ByteCount" : {
              "type" : "float"
            },
            "ByteRate" : {
              "type" : "float"
            },
            "RecordAgeMs" : {
              "type" : "float"
            },
            "RecordAgeMsAvg" : {
              "type" : "float"
            },
            "RecordAgeMsMax" : {
              "type" : "float"
            },
            "RecordAgeMsMin" : {
              "type" : "float"
            },
            "RecordCount" : {
              "type" : "float"
            },
            "RecordRate" : {
              "type" : "float"
            },
            "ReplicationLatencyMs" : {
              "type" : "float"
            },
            "ReplicationLatencyMsAvg" : {
              "type" : "float"
            },
            "ReplicationLatencyMsMax" : {
               "type" : "float"
            },
            "ReplicationLatencyMsMin" : {
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


for i in {0..6};
do
    logdate=_$(date -d "${i} day ago" +%Y-%m-%d)
    curl -s --connect-timeout 10 -o /dev/null -X PUT http://${esaddr}:${port}/ks_kafka_broker_metric${logdate}  && \
    curl -s -o /dev/null -X PUT http://${esaddr}:${port}/ks_kafka_cluster_metric${logdate} && \
    curl -s -o /dev/null -X PUT http://${esaddr}:${port}/ks_kafka_group_metric${logdate} && \
    curl -s -o /dev/null -X PUT http://${esaddr}:${port}/ks_kafka_partition_metric${logdate} && \
    curl -s -o /dev/null -X PUT http://${esaddr}:${port}/ks_kafka_zookeeper_metric${logdate} && \
    curl -s -o /dev/null -X PUT http://${esaddr}:${port}/ks_kafka_connect_cluster_metric${logdate} && \
    curl -s -o /dev/null -X PUT http://${esaddr}:${port}/ks_kafka_connect_connector_metric${logdate} && \
    curl -s -o /dev/null -X PUT http://${esaddr}:${port}/ks_kafka_connect_mirror_maker_metric${logdate} && \
    curl -s -o /dev/null -X PUT http://${esaddr}:${port}/ks_kafka_topic_metric${logdate} || \
    exit 2
done
