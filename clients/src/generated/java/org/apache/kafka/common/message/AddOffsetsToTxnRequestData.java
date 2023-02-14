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


public class AddOffsetsToTxnRequestData implements ApiMessage {
    private String transactionalId;
    private long producerId;
    private short producerEpoch;
    private String groupId;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("transactional_id", Type.STRING, "The transactional id corresponding to the transaction."),
            new Field("producer_id", Type.INT64, "Current producer id in use by the transactional id."),
            new Field("producer_epoch", Type.INT16, "Current epoch associated with the producer id."),
            new Field("group_id", Type.STRING, "The unique group identifier.")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1
    };
    
    public AddOffsetsToTxnRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public AddOffsetsToTxnRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public AddOffsetsToTxnRequestData() {
        this.transactionalId = "";
        this.producerId = 0L;
        this.producerEpoch = (short) 0;
        this.groupId = "";
    }
    
    @Override
    public short apiKey() {
        return 25;
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
        {
            int length;
            length = _readable.readShort();
            if (length < 0) {
                throw new RuntimeException("non-nullable field groupId was serialized as null");
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field groupId had invalid length " + length);
            } else {
                this.groupId = _readable.readString(length);
            }
        }
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
        {
            byte[] _stringBytes = _cache.getSerializedValue(groupId);
            _writable.writeShort((short) _stringBytes.length);
            _writable.writeByteArray(_stringBytes);
        }
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
        this.groupId = struct.getString("group_id");
    }
    
    @Override
    public Struct toStruct(short _version) {
        TreeMap<Integer, Object> _taggedFields = null;
        Struct struct = new Struct(SCHEMAS[_version]);
        struct.set("transactional_id", this.transactionalId);
        struct.set("producer_id", this.producerId);
        struct.set("producer_epoch", this.producerEpoch);
        struct.set("group_id", this.groupId);
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
        {
            byte[] _stringBytes = groupId.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'groupId' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(groupId, _stringBytes);
            _size += _stringBytes.length + 2;
        }
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
        if (!(obj instanceof AddOffsetsToTxnRequestData)) return false;
        AddOffsetsToTxnRequestData other = (AddOffsetsToTxnRequestData) obj;
        if (this.transactionalId == null) {
            if (other.transactionalId != null) return false;
        } else {
            if (!this.transactionalId.equals(other.transactionalId)) return false;
        }
        if (producerId != other.producerId) return false;
        if (producerEpoch != other.producerEpoch) return false;
        if (this.groupId == null) {
            if (other.groupId != null) return false;
        } else {
            if (!this.groupId.equals(other.groupId)) return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (transactionalId == null ? 0 : transactionalId.hashCode());
        hashCode = 31 * hashCode + ((int) (producerId >> 32) ^ (int) producerId);
        hashCode = 31 * hashCode + producerEpoch;
        hashCode = 31 * hashCode + (groupId == null ? 0 : groupId.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "AddOffsetsToTxnRequestData("
            + "transactionalId=" + ((transactionalId == null) ? "null" : "'" + transactionalId.toString() + "'")
            + ", producerId=" + producerId
            + ", producerEpoch=" + producerEpoch
            + ", groupId=" + ((groupId == null) ? "null" : "'" + groupId.toString() + "'")
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
    
    public String groupId() {
        return this.groupId;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public AddOffsetsToTxnRequestData setTransactionalId(String v) {
        this.transactionalId = v;
        return this;
    }
    
    public AddOffsetsToTxnRequestData setProducerId(long v) {
        this.producerId = v;
        return this;
    }
    
    public AddOffsetsToTxnRequestData setProducerEpoch(short v) {
        this.producerEpoch = v;
        return this;
    }
    
    public AddOffsetsToTxnRequestData setGroupId(String v) {
        this.groupId = v;
        return this;
    }
}
