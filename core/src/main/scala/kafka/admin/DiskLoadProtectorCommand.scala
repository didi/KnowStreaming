/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kafka.admin

import java.io.FileReader
import java.util.Properties

import com.didichuxing.datachannel.kafka.server.DiskLoadProtector
import joptsimple.OptionParser
import kafka.server.ConfigType
import kafka.utils.{CommandLineUtils, Logging}
import kafka.zk.{AdminZkClient, KafkaZkClient}
import org.apache.kafka.common.security.JaasUtils
import org.apache.kafka.common.utils.{Time, Utils}

import scala.collection._


object DiskLoadProtectorCommand extends Logging {

  def main(args: Array[String]): Unit = {

    val opts = validateAndParseArgs(args)
    val zkConnect = opts.options.valueOf(opts.zkConnectOpt)
    val time = Time.SYSTEM
    val zkClient = KafkaZkClient(zkConnect, JaasUtils.isZkSaslEnabled, 30000, 30000, Int.MaxValue, time)
    val adminZkClient = new AdminZkClient(zkClient)
    try {
      var brokerId = "";
      if (opts.options.has(opts.configBrokerIdOpt)) {
        brokerId = opts.options.valueOf(opts.configBrokerIdOpt);
      }

      if (opts.options.has(opts.describeOpt)) {
        val diskLoadProtector = new DiskLoadProtector()
        var finalConfig = diskLoadProtector.getConfig;
        if (brokerId.nonEmpty) {
          val config = readConfig(adminZkClient, brokerId)
          if (config == null) {
            config.keySet().forEach( key  => finalConfig.put(key, config.get(key)))
          }
          print("broker %s config is:\n".format(brokerId))
          printConfig(finalConfig);
        } else {
          print("all broker's config is:\n".format(brokerId))
          val config = readConfig(adminZkClient, brokerId);
          if (config == null) {
            config.keySet().forEach( key  => finalConfig.put(key, config.get(key)))
          }
          printConfig(finalConfig)
          println();

          finalConfig = diskLoadProtector.getConfig;
          readAllConfig(adminZkClient).foreach{
            case (brokerId, props) =>
              print("broker %s config is:\n".format(brokerId))
              props.keySet().forEach( key  => finalConfig.put(key, config.get(key)))
              printConfig(finalConfig);
              println();
          }
        }
      } else {
        val configFileName = opts.options.valueOf(opts.configFileOpt);
        val properties = new Properties();
        properties.load(new FileReader(configFileName));
        changeEntityConfig(adminZkClient, ConfigType.DiskLoadProtector, brokerId, properties);
      }
    } catch {
      case e: Throwable =>

        println("Partitions reassignment failed due to " + e.getMessage)
        println(Utils.stackTrace(e))
    } finally zkClient.close()
  }

  def validateAndParseArgs(args: Array[String]): CommandOptions = {
    val opts = new CommandOptions(args);
    if (args.length == 0)
      CommandLineUtils.printUsageAndDie(opts.parser, "This command get and set configuration for disk load protector.")

    CommandLineUtils.checkRequiredArgs(opts.parser, opts.options, opts.zkConnectOpt)
    opts
  }

  class CommandOptions(args: Array[String]) {
    val parser = new OptionParser

    val zkConnectOpt = parser.accepts("zookeeper", "REQUIRED: The connection string for the zookeeper connection in the " +
      "form host:port. Multiple URLS can be given to allow fail-over.")
      .withRequiredArg
      .describedAs("urls")
      .ofType(classOf[String])
    val configFileOpt = parser.accepts("config", "The json file with the disk load protector configuration")
      .withOptionalArg()
      .describedAs("manual assignment file path")
      .ofType(classOf[String])
    val configBrokerIdOpt = parser.accepts("broker", "The special broker to be set configuration")
      .withOptionalArg()
      .describedAs("broker id")
      .ofType(classOf[String])
    val describeOpt = parser.accepts("describe", "describe the configuration")
      .withOptionalArg()
      .describedAs("describe")
    val options = parser.parse(args: _*)
  }

  private def changeEntityConfig(zkUtils: AdminZkClient, rootEntityType: String, fullSanitizedEntityName: String, config: Properties) = {
    zkUtils.changeEntityConfig(rootEntityType, fullSanitizedEntityName, config)
  }

  private def readConfig(zkUtils: AdminZkClient, brokerId: String): Properties = {
    zkUtils.fetchEntityConfig(ConfigType.DiskLoadProtector, brokerId)
  }

  private def readAllConfig(zkUtils: AdminZkClient): Map[String, Properties] = {
    zkUtils.fetchAllEntityConfigs(ConfigType.DiskLoadProtector)
  }

  private def printConfig(properties: Properties): Unit = {
    properties.keySet().forEach(
      key  => print("\t%s=%s\n".format(key, properties.get(key)))
    )
  }
}
