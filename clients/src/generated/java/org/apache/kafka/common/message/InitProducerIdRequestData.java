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
import java.util.NavigableMap;
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

import static java.util.Map.Entry;
import static org.apache.kafka.common.protocol.types.Field.TaggedFieldsSection;


public class InitProducerIdRequestData implements ApiMessage {
    private String transactionalId;
    private int transactionTimeoutMs;
    private long producerId;
    private short producerEpoch;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("transactional_id", Type.NULLABLE_STRING, "The transactional id, or null if the producer is not transactional."),
            new Field("transaction_timeout_ms", Type.INT32, "The time in ms to wait before aborting idle transactions sent by this producer. This is only relevant if a TransactionalId has been defined.")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("transactional_id", Type.COMPACT_NULLABLE_STRING, "The transactional id, or null if the producer is not transactional."),
            new Field("transaction_timeout_ms", Type.INT32, "The time in ms to wait before aborting idle transactions sent by this producer. This is only relevant if a TransactionalId has been defined."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema SCHEMA_3 =
        new Schema(
            new Field("transactional_id", Type.COMPACT_NULLABLE_STRING, "The transactional id, or null if the producer is not transactional."),
            new Field("transaction_timeout_ms", Type.INT32, "The time in ms to wait before aborting idle transactions sent by this producer. This is only relevant if a TransactionalId has been defined."),
            new Field("producer_id", Type.INT64, "The producer id. This is used to disambiguate requests if a transactional id is reused following its expiration."),
            new Field("producer_epoch", Type.INT16, "The producer's current epoch. This will be checked against the producer epoch on the broker, and the request will return an error if they do not match."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2,
        SCHEMA_3
    };
    
    public InitProducerIdRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public InitProducerIdRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public InitProducerIdRequestData() {
        this.transactionalId = "";
        this.transactionTimeoutMs = 0;
        this.producerId = -1L;
        this.producerEpoch = (short) -1;
    }
    
    @Override
    public short apiKey() {
        return 22;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 3;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        {
            int length;
            if (_version >= 2) {
                length = _readable.readUnsignedVarint() - 1;
            } else {
                length = _readable.readShort();
            }
            if (length < 0) {
                this.transactionalId = null;
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field transactionalId had invalid length " + length);
            } else {
                this.transactionalId = _readable.readString(length);
            }
        }
        this.transactionTimeoutMs = _readable.readInt();
        if (_version >= 3) {
            this.producerId = _readable.readLong();
        } else {
            this.producerId = -1L;
        }
        if (_version >= 3) {
            this.producerEpoch = _readable.readShort();
        } else {
            this.producerEpoch = (short) -1;
        }
        this._unknownTaggedFields = null;
        if (_version >= 2) {
            int _numTaggedFields = _readable.readUnsignedVarint();
            for (int _i = 0; _i < _numTaggedFields; _i++) {
                int _tag = _readable.readUnsignedVarint();
                int _size = _readable.readUnsignedVarint();
                switch (_tag) {
                    default:
                        this._unknownTaggedFields = _readable.readUnknownTaggedField(this._unknownTaggedFields, _tag, _size);
                        break;
                }
            }
        }
    }
    
    @Override
    public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        if (transactionalId == null) {
            if (_version >= 2) {
                _writable.writeUnsignedVarint(0);
            } else {
                _writable.writeShort((short) -1);
            }
        } else {
            byte[] _stringBytes = _cache.getSerializedValue(transactionalId);
            if (_version >= 2) {
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
            } else {
                _writable.writeShort((short) _stringBytes.length);
            }
            _writable.writeByteArray(_stringBytes);
        }
        _writable.writeInt(transactionTimeoutMs);
        if (_version >= 3) {
            _writable.writeLong(producerId);
        } else {
            if (producerId != -1L) {
                throw new UnsupportedVersionException("Attempted to write a non-default producerId at version " + _version);
            }
        }
        if (_version >= 3) {
            _writable.writeShort(producerEpoch);
        } else {
            if (producerEpoch != (short) -1) {
                throw new UnsupportedVersionException("Attempted to write a non-default producerEpoch at version " + _version);
            }
        }
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        if (_version >= 2) {
            _writable.writeUnsignedVarint(_numTaggedFields);
            _rawWriter.writeRawTags(_writable, Integer.MAX_VALUE);
        } else {
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void fromStruct(Struct struct, short _version) {
        NavigableMap<Integer, Object> _taggedFields = null;
        this._unknownTaggedFields = null;
        if (_version >= 2) {
            _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
        }
        this.transactionalId = struct.getString("transactional_id");
        this.transactionTimeoutMs = struct.getInt("transaction_timeout_ms");
        if (_version >= 3) {
            this.producerId = struct.getLong("producer_id");
        } else {
            this.producerId = -1L;
        }
        if (_version >= 3) {
            this.producerEpoch = struct.getShort("producer_epoch");
        } else {
            this.producerEpoch = (short) -1;
        }
        if (_version >= 2) {
            if (!_taggedFields.isEmpty()) {
                this._unknownTaggedFields = new ArrayList<>(_taggedFields.size());
                for (Entry<Integer, Object> entry : _taggedFields.entrySet()) {
                    this._unknownTaggedFields.add((RawTaggedField) entry.getValue());
                }
            }
        }
    }
    
    @Override
    public Struct toStruct(short _version) {
        TreeMap<Integer, Object> _taggedFields = null;
        if (_version >= 2) {
            _taggedFields = new TreeMap<>();
        }
        Struct struct = new Struct(SCHEMAS[_version]);
        struct.set("transactional_id", this.transactionalId);
        struct.set("transaction_timeout_ms", this.transactionTimeoutMs);
        if (_version >= 3) {
            struct.set("producer_id", this.producerId);
        } else {
            if (producerId != -1L) {
                throw new UnsupportedVersionException("Attempted to write a non-default producerId at version " + _version);
            }
        }
        if (_version >= 3) {
            struct.set("producer_epoch", this.producerEpoch);
        } else {
            if (producerEpoch != (short) -1) {
                throw new UnsupportedVersionException("Attempted to write a non-default producerEpoch at version " + _version);
            }
        }
        if (_version >= 2) {
            struct.set("_tagged_fields", _taggedFields);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        if (transactionalId == null) {
            if (_version >= 2) {
                _size += 1;
            } else {
                _size += 2;
            }
        } else {
            byte[] _stringBytes = transactionalId.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'transactionalId' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(transactionalId, _stringBytes);
            if (_version >= 2) {
                _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
            } else {
                _size += _stringBytes.length + 2;
            }
        }
        _size += 4;
        if (_version >= 3) {
            _size += 8;
        }
        if (_version >= 3) {
            _size += 2;
        }
        if (_unknownTaggedFields != null) {
            _numTaggedFields += _unknownTaggedFields.size();
            for (RawTaggedField _field : _unknownTaggedFields) {
                _size += ByteUtils.sizeOfUnsignedVarint(_field.tag());
                _size += ByteUtils.sizeOfUnsignedVarint(_field.size());
                _size += _field.size();
            }
        }
        if (_version >= 2) {
            _size += ByteUtils.sizeOfUnsignedVarint(_numTaggedFields);
        } else {
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
        }
        return _size;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof InitProducerIdRequestData)) return false;
        InitProducerIdRequestData other = (InitProducerIdRequestData) obj;
        if (this.transactionalId == null) {
            if (other.transactionalId != null) return false;
        } else {
            if (!this.transactionalId.equals(other.transactionalId)) return false;
        }
        if (transactionTimeoutMs != other.transactionTimeoutMs) return false;
        if (producerId != other.producerId) return false;
        if (producerEpoch != other.producerEpoch) return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (transactionalId == null ? 0 : transactionalId.hashCode());
        hashCode = 31 * hashCode + transactionTimeoutMs;
        hashCode = 31 * hashCode + ((int) (producerId >> 32) ^ (int) producerId);
        hashCode = 31 * hashCode + producerEpoch;
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "InitProducerIdRequestData("
            + "transactionalId=" + ((transactionalId == null) ? "null" : "'" + transactionalId.toString() + "'")
            + ", transactionTimeoutMs=" + transactionTimeoutMs
            + ", producerId=" + producerId
            + ", producerEpoch=" + producerEpoch
            + ")";
    }
    
    public String transactionalId() {
        return this.transactionalId;
    }
    
    public int transactionTimeoutMs() {
        return this.transactionTimeoutMs;
    }
    
    public long producerId() {
        return this.producerId;
    }
    
    public short producerEpoch() {
        return this.producerEpoch;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public InitProducerIdRequestData setTransactionalId(String v) {
        this.transactionalId = v;
        return this;
    }
    
    public InitProducerIdRequestData setTransactionTimeoutMs(int v) {
        this.transactionTimeoutMs = v;
        return this;
    }
    
    public InitProducerIdRequestData setProducerId(long v) {
        this.producerId = v;
        return this;
    }
    
    public InitProducerIdRequestData setProducerEpoch(short v) {
        this.producerEpoch = v;
        return this;
    }
}
