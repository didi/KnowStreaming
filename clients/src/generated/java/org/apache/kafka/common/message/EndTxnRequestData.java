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

// THIS CODE IS AUTOMATICALLY GENERATED.  DO NOT EDIT.

package org.apache.kafka.common.message;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.apache.kafka.common.errors.UnsupportedVersionException;
import org.apache.kafka.common.protocol.ApiMessage;
import org.apache.kafka.common.protocol.ObjectSerializationCache;
import org.apache.kafka.common.protocol.Readable;
import org.apache.kafka.common.protocol.Writable;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.protocol.types.RawTaggedField;
import org.apache.kafka.common.protocol.types.RawTaggedFieldWriter;
import org.apache.kafka.common.protocol.types.Schema;
import org.apache.kafka.common.protocol.types.Struct;
import org.apache.kafka.common.protocol.types.Type;
import org.apache.kafka.common.utils.ByteUtils;


public class EndTxnRequestData implements ApiMessage {
    private String transactionalId;
    private long producerId;
    private short producerEpoch;
    private boolean committed;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("transactional_id", Type.STRING, "The ID of the transaction to end."),
            new Field("producer_id", Type.INT64, "The producer ID."),
            new Field("producer_epoch", Type.INT16, "The current epoch associated with the producer."),
            new Field("committed", Type.BOOLEAN, "True if the transaction was committed, false if it was aborted.")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1
    };
    
    public EndTxnRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public EndTxnRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public EndTxnRequestData() {
        this.transactionalId = "";
        this.producerId = 0L;
        this.producerEpoch = (short) 0;
        this.committed = false;
    }
    
    @Override
    public short apiKey() {
        return 26;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 1;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        {
            int length;
            length = _readable.readShort();
            if (length < 0) {
                throw new RuntimeException("non-nullable field transactionalId was serialized as null");
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field transactionalId had invalid length " + length);
            } else {
                this.transactionalId = _readable.readString(length);
            }
        }
        this.producerId = _readable.readLong();
        this.producerEpoch = _readable.readShort();
        this.committed = _readable.readByte() != 0;
        this._unknownTaggedFields = null;
    }
    
    @Override
    public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        {
            byte[] _stringBytes = _cache.getSerializedValue(transactionalId);
            _writable.writeShort((short) _stringBytes.length);
            _writable.writeByteArray(_stringBytes);
        }
        _writable.writeLong(producerId);
        _writable.writeShort(producerEpoch);
        _writable.writeByte(committed ? (byte) 1 : (byte) 0);
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        if (_numTaggedFields > 0) {
            throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void fromStruct(Struct struct, short _version) {
        this._unknownTaggedFields = null;
        this.transactionalId = struct.getString("transactional_id");
        this.producerId = struct.getLong("producer_id");
        this.producerEpoch = struct.getShort("producer_epoch");
        this.committed = struct.getBoolean("committed");
    }
    
    @Override
    public Struct toStruct(short _version) {
        TreeMap<Integer, Object> _taggedFields = null;
        Struct struct = new Struct(SCHEMAS[_version]);
        struct.set("transactional_id", this.transactionalId);
        struct.set("producer_id", this.producerId);
        struct.set("producer_epoch", this.producerEpoch);
        struct.set("committed", this.committed);
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        {
            byte[] _stringBytes = transactionalId.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'transactionalId' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(transactionalId, _stringBytes);
            _size += _stringBytes.length + 2;
        }
        _size += 8;
        _size += 2;
        _size += 1;
        if (_unknownTaggedFields != null) {
            _numTaggedFields += _unknownTaggedFields.size();
            for (RawTaggedField _field : _unknownTaggedFields) {
                _size += ByteUtils.sizeOfUnsignedVarint(_field.tag());
                _size += ByteUtils.sizeOfUnsignedVarint(_field.size());
                _size += _field.size();
            }
        }
        if (_numTaggedFields > 0) {
            throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
        }
        return _size;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EndTxnRequestData)) return false;
        EndTxnRequestData other = (EndTxnRequestData) obj;
        if (this.transactionalId == null) {
            if (other.transactionalId != null) return false;
        } else {
            if (!this.transactionalId.equals(other.transactionalId)) return false;
        }
        if (producerId != other.producerId) return false;
        if (producerEpoch != other.producerEpoch) return false;
        if (committed != other.committed) return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (transactionalId == null ? 0 : transactionalId.hashCode());
        hashCode = 31 * hashCode + ((int) (producerId >> 32) ^ (int) producerId);
        hashCode = 31 * hashCode + producerEpoch;
        hashCode = 31 * hashCode + (committed ? 1231 : 1237);
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "EndTxnRequestData("
            + "transactionalId=" + ((transactionalId == null) ? "null" : "'" + transactionalId.toString() + "'")
            + ", producerId=" + producerId
            + ", producerEpoch=" + producerEpoch
            + ", committed=" + (committed ? "true" : "false")
            + ")";
    }
    
    public String transactionalId() {
        return this.transactionalId;
    }
    
    public long producerId() {
        return this.producerId;
    }
    
    public short producerEpoch() {
        return this.producerEpoch;
    }
    
    public boolean committed() {
        return this.committed;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public EndTxnRequestData setTransactionalId(String v) {
        this.transactionalId = v;
        return this;
    }
    
    public EndTxnRequestData setProducerId(long v) {
        this.producerId = v;
        return this;
    }
    
    public EndTxnRequestData setProducerEpoch(short v) {
        this.producerEpoch = v;
        return this;
    }
    
    public EndTxnRequestData setCommitted(boolean v) {
        this.committed = v;
        return this;
    }
}
