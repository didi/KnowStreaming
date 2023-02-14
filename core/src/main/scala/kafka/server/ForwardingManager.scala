/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
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

import java.nio.ByteBuffer

import kafka.network.RequestChannel
import kafka.utils.Logging
import org.apache.kafka.clients.ClientResponse
import org.apache.kafka.common.errors.TimeoutException
import org.apache.kafka.common.protocol.Errors
import org.apache.kafka.common.requests.{AbstractRequest, AbstractResponse, EnvelopeRequest, EnvelopeResponse, RequestContext, RequestHeader}

trait ForwardingManager {
  def forwardRequest(haClusterId: String, request: RequestChannel.Request, responseCallback: Option[AbstractResponse] => Unit): Unit
}

object ForwardingManager {
  def apply(channelManager: BrokerToHAClusterChannelManager): ForwardingManager = {
    new ForwardingManagerImpl(channelManager)
  }

  def buildEnvelopeRequest(context: RequestContext, forwardRequestBuffer: ByteBuffer): EnvelopeRequest.Builder = {
    val serializedPrincipal = EnvelopeUtils.serialize(context.principal)
    new EnvelopeRequest.Builder(
      forwardRequestBuffer,
      serializedPrincipal,
      context.clientAddress.getAddress
    )
  }
}

class ForwardingManagerImpl(channelManager: BrokerToHAClusterChannelManager) extends ForwardingManager with Logging {

  override def forwardRequest(haClusterId: String, request: RequestChannel.Request, responseCallback: Option[AbstractResponse] => Unit): Unit = {
    val requestBuffer = request.buffer.duplicate()
    requestBuffer.flip()
    val envelopeRequest = ForwardingManager.buildEnvelopeRequest(request.context, requestBuffer)

    class ForwardingResponseHandler extends HAClusterRequestCompletionHandler {
      override def onComplete(clientResponse: ClientResponse): Unit = {
        val requestBody = request.body[AbstractRequest]
        if (clientResponse.authenticationException != null) {
          error(s"Returning `UNKNOWN_SERVER_ERROR` in response to request $requestBody " +
            s"due to authentication error", clientResponse.authenticationException)
          responseCallback(Some(requestBody.getErrorResponse(Errors.UNKNOWN_SERVER_ERROR.exception)))
        } else {
          val envelopeResponse = clientResponse.responseBody.asInstanceOf[EnvelopeResponse]
          val envelopeError = envelopeResponse.error()
          if (envelopeError == Errors.UNSUPPORTED_VERSION) {
            responseCallback(None)
          } else {
            val response = parseResponse(envelopeResponse.responseData(), requestBody, request.header)
            info(s"${request.header.apiKey} Forward user: ${request.context.principal} ${request.header.clientId()} request complete, response for correlationId ${request.header.correlationId()} to client.")
            responseCallback(Option(response))
          }
        }
      }

      override def onTimeout(): Unit = {
        error(s"Forwarding of the request $request failed due to timeout exception")
        val response = request.body[AbstractRequest].getErrorResponse(new TimeoutException())
        responseCallback(Option(response))
      }

      override def onError(errorMsg: String): Unit = {
        error(errorMsg)
        val response = request.body[AbstractRequest].getErrorResponse(new IllegalArgumentException(errorMsg))
        responseCallback(Option(response))
      }
    }

    channelManager.sendRequest(haClusterId, envelopeRequest, new ForwardingResponseHandler, request.context.principal())
  }

  private def parseResponse(buffer: ByteBuffer, request: AbstractRequest, header: RequestHeader): AbstractResponse = {
    try {
      AbstractResponse.parseResponse(buffer, header)
    } catch {
      case e: Exception =>
        error(s"Failed to parse response from envelope for request with header $header", e)
        request.getErrorResponse(Errors.UNKNOWN_SERVER_ERROR.exception)
    }
  }
}
