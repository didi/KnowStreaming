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

package kafka.server

import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.message.DefaultPrincipalData
import org.apache.kafka.common.protocol.{ByteBufferAccessor, MessageUtil}
import org.apache.kafka.common.security.auth.KafkaPrincipal

import java.nio.ByteBuffer

object EnvelopeUtils {

  def serialize(principal: KafkaPrincipal): Array[Byte] = {
    val data = new DefaultPrincipalData().setType(principal.getPrincipalType).setName(principal.getName).setTokenAuthenticated(principal.tokenAuthenticated)
    MessageUtil.toVersionPrefixedBytes(0.toShort, data)
  }

  def deserialize(bytes: Array[Byte]): KafkaPrincipal = {
    val buffer = ByteBuffer.wrap(bytes)
    val version = buffer.getShort
    if (version < 0.toShort || version > 0.toShort) throw new SerializationException("Invalid principal data version " + version)
    val data = new DefaultPrincipalData(new ByteBufferAccessor(buffer), version)
    val kafkaPrincipal = new KafkaPrincipal(data.`type`, data.name)
    kafkaPrincipal.tokenAuthenticated(data.tokenAuthenticated)
    kafkaPrincipal
  }
}
