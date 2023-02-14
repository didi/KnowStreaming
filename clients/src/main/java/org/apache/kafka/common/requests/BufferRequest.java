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
package org.apache.kafka.common.requests;

import org.apache.kafka.common.network.NetworkSend;
import org.apache.kafka.common.network.Send;
import org.apache.kafka.common.protocol.ApiKeys;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.protocol.types.Schema;
import org.apache.kafka.common.protocol.types.Struct;

import java.nio.ByteBuffer;

import static org.apache.kafka.common.protocol.types.Type.BYTES;

public class BufferRequest extends AbstractRequest {

    public static final Schema[] SCHEMAS = {new Schema(new Field("buffer", BYTES))};

    public static class Builder extends AbstractRequest.Builder<BufferRequest> {

        private ByteBuffer buffer;

        public Builder() {
            super(ApiKeys.BUFFER);
        }

        public Builder(ByteBuffer buffer) {
            this();
            this.buffer = buffer;
        }

        @Override
        public BufferRequest build(short version) {
            return new BufferRequest(buffer);
        }

        @Override
        public String toString() {
            return "BufferRequestBuilder";
        }


    }

    private ByteBuffer data;

    public BufferRequest(ByteBuffer data) {
        super(ApiKeys.BUFFER, (short) 0);
        this.data = data;
    }

    @Override
    public Send toSend(String destination, RequestHeader header) {
        return new NetworkSend(destination, data);
    }

    @Override
    protected Struct toStruct() {
        return null;
    }

    @Override
    public AbstractResponse getErrorResponse(int throttleTimeMs, Throwable e) {
        return null;
    }

    @Override
    public String toString(boolean verbose) {
        return "BufferRequest";
    }
}
