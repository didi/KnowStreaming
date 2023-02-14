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
import org.apache.kafka.common.protocol.Message;
import org.apache.kafka.common.protocol.MessageUtil;
import org.apache.kafka.common.protocol.ObjectSerializationCache;
import org.apache.kafka.common.protocol.Readable;
import org.apache.kafka.common.protocol.Writable;
import org.apache.kafka.common.protocol.types.ArrayOf;
import org.apache.kafka.common.protocol.types.CompactArrayOf;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.protocol.types.RawTaggedField;
import org.apache.kafka.common.protocol.types.RawTaggedFieldWriter;
import org.apache.kafka.common.protocol.types.Schema;
import org.apache.kafka.common.protocol.types.Struct;
import org.apache.kafka.common.protocol.types.Type;
import org.apache.kafka.common.utils.ByteUtils;

import static java.util.Map.Entry;
import static org.apache.kafka.common.protocol.types.Field.TaggedFieldsSection;


public class TxnOffsetCommitRequestData implements ApiMessage {
    private String transactionalId;
    private String groupId;
    private long producerId;
    private short producerEpoch;
    private int generationId;
    private String memberId;
    private String groupInstanceId;
    private List<TxnOffsetCommitRequestTopic> topics;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("transactional_id", Type.STRING, "The ID of the transaction."),
            new Field("group_id", Type.STRING, "The ID of the group."),
            new Field("producer_id", Type.INT64, "The current producer ID in use by the transactional ID."),
            new Field("producer_epoch", Type.INT16, "The current epoch associated with the producer ID."),
            new Field("topics", new ArrayOf(TxnOffsetCommitRequestTopic.SCHEMA_0), "Each topic that we want to commit offsets for.")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("transactional_id", Type.STRING, "The ID of the transaction."),
            new Field("group_id", Type.STRING, "The ID of the group."),
            new Field("producer_id", Type.INT64, "The current producer ID in use by the transactional ID."),
            new Field("producer_epoch", Type.INT16, "The current epoch associated with the producer ID."),
            new Field("topics", new ArrayOf(TxnOffsetCommitRequestTopic.SCHEMA_2), "Each topic that we want to commit offsets for.")
        );
    
    public static final Schema SCHEMA_3 =
        new Schema(
            new Field("transactional_id", Type.COMPACT_STRING, "The ID of the transaction."),
            new Field("group_id", Type.COMPACT_STRING, "The ID of the group."),
            new Field("producer_id", Type.INT64, "The current producer ID in use by the transactional ID."),
            new Field("producer_epoch", Type.INT16, "The current epoch associated with the producer ID."),
            new Field("generation_id", Type.INT32, "The generation of the consumer."),
            new Field("member_id", Type.COMPACT_STRING, "The member ID assigned by the group coordinator."),
            new Field("group_instance_id", Type.COMPACT_NULLABLE_STRING, "The unique identifier of the consumer instance provided by end user."),
            new Field("topics", new CompactArrayOf(TxnOffsetCommitRequestTopic.SCHEMA_3), "Each topic that we want to commit offsets for."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2,
        SCHEMA_3
    };
    
    public TxnOffsetCommitRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public TxnOffsetCommitRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public TxnOffsetCommitRequestData() {
        this.transactionalId = "";
        this.groupId = "";
        this.producerId = 0L;
        this.producerEpoch = (short) 0;
        this.generationId = -1;
        this.memberId = "";
        this.groupInstanceId = null;
        this.topics = new ArrayList<TxnOffsetCommitRequestTopic>();
    }
    
    @Override
    public short apiKey() {
        return 28;
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
            if (_version >= 3) {
                length = _readable.readUnsignedVarint() - 1;
            } else {
                length = _readable.readShort();
            }
            if (length < 0) {
                throw new RuntimeException("non-nullable field transactionalId was serialized as null");
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field transactionalId had invalid length " + length);
            } else {
                this.transactionalId = _readable.readString(length);
            }
        }
        {
            int length;
            if (_version >= 3) {
                length = _readable.readUnsignedVarint() - 1;
            } else {
                length = _readable.readShort();
            }
            if (length < 0) {
                throw new RuntimeException("non-nullable field groupId was serialized as null");
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field groupId had invalid length " + length);
            } else {
                this.groupId = _readable.readString(length);
            }
        }
        this.producerId = _readable.readLong();
        this.producerEpoch = _readable.readShort();
        if (_version >= 3) {
            this.generationId = _readable.readInt();
        } else {
            this.generationId = -1;
        }
        if (_version >= 3) {
            int length;
            length = _readable.readUnsignedVarint() - 1;
            if (length < 0) {
                throw new RuntimeException("non-nullable field memberId was serialized as null");
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field memberId had invalid length " + length);
            } else {
                this.memberId = _readable.readString(length);
            }
        } else {
            this.memberId = "";
        }
        if (_version >= 3) {
            int length;
            length = _readable.readUnsignedVarint() - 1;
            if (length < 0) {
                this.groupInstanceId = null;
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field groupInstanceId had invalid length " + length);
            } else {
                this.groupInstanceId = _readable.readString(length);
            }
        } else {
            this.groupInstanceId = null;
        }
        {
            if (_version >= 3) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topics was serialized as null");
                } else {
                    ArrayList<TxnOffsetCommitRequestTopic> newCollection = new ArrayList<TxnOffsetCommitRequestTopic>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new TxnOffsetCommitRequestTopic(_readable, _version));
                    }
                    this.topics = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topics was serialized as null");
                } else {
                    ArrayList<TxnOffsetCommitRequestTopic> newCollection = new ArrayList<TxnOffsetCommitRequestTopic>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new TxnOffsetCommitRequestTopic(_readable, _version));
                    }
                    this.topics = newCollection;
                }
            }
        }
        this._unknownTaggedFields = null;
        if (_version >= 3) {
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
        {
            byte[] _stringBytes = _cache.getSerializedValue(transactionalId);
            if (_version >= 3) {
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
            } else {
                _writable.writeShort((short) _stringBytes.length);
            }
            _writable.writeByteArray(_stringBytes);
        }
        {
            byte[] _stringBytes = _cache.getSerializedValue(groupId);
            if (_version >= 3) {
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
            } else {
                _writable.writeShort((short) _stringBytes.length);
            }
            _writable.writeByteArray(_stringBytes);
        }
        _writable.writeLong(producerId);
        _writable.writeShort(producerEpoch);
        if (_version >= 3) {
            _writable.writeInt(generationId);
        } else {
            if (generationId != -1) {
                throw new UnsupportedVersionException("Attempted to write a non-default generationId at version " + _version);
            }
        }
        if (_version >= 3) {
            {
                byte[] _stringBytes = _cache.getSerializedValue(memberId);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
        } else {
            if (!memberId.equals("")) {
                throw new UnsupportedVersionException("Attempted to write a non-default memberId at version " + _version);
            }
        }
        if (_version >= 3) {
            if (groupInstanceId == null) {
                _writable.writeUnsignedVarint(0);
            } else {
                byte[] _stringBytes = _cache.getSerializedValue(groupInstanceId);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
        } else {
            if (groupInstanceId != null) {
                throw new UnsupportedVersionException("Attempted to write a non-default groupInstanceId at version " + _version);
            }
        }
        if (_version >= 3) {
            _writable.writeUnsignedVarint(topics.size() + 1);
            for (TxnOffsetCommitRequestTopic topicsElement : topics) {
                topicsElement.write(_writable, _cache, _version);
            }
        } else {
            _writable.writeInt(topics.size());
            for (TxnOffsetCommitRequestTopic topicsElement : topics) {
                topicsElement.write(_writable, _cache, _version);
            }
        }
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        if (_version >= 3) {
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
        if (_version >= 3) {
            _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
        }
        this.transactionalId = struct.getString("transactional_id");
        this.groupId = struct.getString("group_id");
        this.producerId = struct.getLong("producer_id");
        this.producerEpoch = struct.getShort("producer_epoch");
        if (_version >= 3) {
            this.generationId = struct.getInt("generation_id");
        } else {
            this.generationId = -1;
        }
        if (_version >= 3) {
            this.memberId = struct.getString("member_id");
        } else {
            this.memberId = "";
        }
        if (_version >= 3) {
            this.groupInstanceId = struct.getString("group_instance_id");
        } else {
            this.groupInstanceId = null;
        }
        {
            Object[] _nestedObjects = struct.getArray("topics");
            this.topics = new ArrayList<TxnOffsetCommitRequestTopic>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.topics.add(new TxnOffsetCommitRequestTopic((Struct) nestedObject, _version));
            }
        }
        if (_version >= 3) {
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
        if (_version >= 3) {
            _taggedFields = new TreeMap<>();
        }
        Struct struct = new Struct(SCHEMAS[_version]);
        struct.set("transactional_id", this.transactionalId);
        struct.set("group_id", this.groupId);
        struct.set("producer_id", this.producerId);
        struct.set("producer_epoch", this.producerEpoch);
        if (_version >= 3) {
            struct.set("generation_id", this.generationId);
        } else {
            if (generationId != -1) {
                throw new UnsupportedVersionException("Attempted to write a non-default generationId at version " + _version);
            }
        }
        if (_version >= 3) {
            struct.set("member_id", this.memberId);
        } else {
            if (!memberId.equals("")) {
                throw new UnsupportedVersionException("Attempted to write a non-default memberId at version " + _version);
            }
        }
        if (_version >= 3) {
            struct.set("group_instance_id", this.groupInstanceId);
        } else {
            if (groupInstanceId != null) {
                throw new UnsupportedVersionException("Attempted to write a non-default groupInstanceId at version " + _version);
            }
        }
        {
            Struct[] _nestedObjects = new Struct[topics.size()];
            int i = 0;
            for (TxnOffsetCommitRequestTopic element : this.topics) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("topics", (Object[]) _nestedObjects);
        }
        if (_version >= 3) {
            struct.set("_tagged_fields", _taggedFields);
        }
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
            if (_version >= 3) {
                _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
            } else {
                _size += _stringBytes.length + 2;
            }
        }
        {
            byte[] _stringBytes = groupId.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'groupId' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(groupId, _stringBytes);
            if (_version >= 3) {
                _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
            } else {
                _size += _stringBytes.length + 2;
            }
        }
        _size += 8;
        _size += 2;
        if (_version >= 3) {
            _size += 4;
        }
        if (_version >= 3) {
            {
                byte[] _stringBytes = memberId.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'memberId' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(memberId, _stringBytes);
                _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
            }
        }
        if (_version >= 3) {
            if (groupInstanceId == null) {
                _size += 1;
            } else {
                byte[] _stringBytes = groupInstanceId.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'groupInstanceId' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(groupInstanceId, _stringBytes);
                _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
            }
        }
        {
            int _arraySize = 0;
            if (_version >= 3) {
                _arraySize += ByteUtils.sizeOfUnsignedVarint(topics.size() + 1);
            } else {
                _arraySize += 4;
            }
            for (TxnOffsetCommitRequestTopic topicsElement : topics) {
                _arraySize += topicsElement.size(_cache, _version);
            }
            _size += _arraySize;
        }
        if (_unknownTaggedFields != null) {
            _numTaggedFields += _unknownTaggedFields.size();
            for (RawTaggedField _field : _unknownTaggedFields) {
                _size += ByteUtils.sizeOfUnsignedVarint(_field.tag());
                _size += ByteUtils.sizeOfUnsignedVarint(_field.size());
                _size += _field.size();
            }
        }
        if (_version >= 3) {
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
        if (!(obj instanceof TxnOffsetCommitRequestData)) return false;
        TxnOffsetCommitRequestData other = (TxnOffsetCommitRequestData) obj;
        if (this.transactionalId == null) {
            if (other.transactionalId != null) return false;
        } else {
            if (!this.transactionalId.equals(other.transactionalId)) return false;
        }
        if (this.groupId == null) {
            if (other.groupId != null) return false;
        } else {
            if (!this.groupId.equals(other.groupId)) return false;
        }
        if (producerId != other.producerId) return false;
        if (producerEpoch != other.producerEpoch) return false;
        if (generationId != other.generationId) return false;
        if (this.memberId == null) {
            if (other.memberId != null) return false;
        } else {
            if (!this.memberId.equals(other.memberId)) return false;
        }
        if (this.groupInstanceId == null) {
            if (other.groupInstanceId != null) return false;
        } else {
            if (!this.groupInstanceId.equals(other.groupInstanceId)) return false;
        }
        if (this.topics == null) {
            if (other.topics != null) return false;
        } else {
            if (!this.topics.equals(other.topics)) return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (transactionalId == null ? 0 : transactionalId.hashCode());
        hashCode = 31 * hashCode + (groupId == null ? 0 : groupId.hashCode());
        hashCode = 31 * hashCode + ((int) (producerId >> 32) ^ (int) producerId);
        hashCode = 31 * hashCode + producerEpoch;
        hashCode = 31 * hashCode + generationId;
        hashCode = 31 * hashCode + (memberId == null ? 0 : memberId.hashCode());
        hashCode = 31 * hashCode + (groupInstanceId == null ? 0 : groupInstanceId.hashCode());
        hashCode = 31 * hashCode + (topics == null ? 0 : topics.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "TxnOffsetCommitRequestData("
            + "transactionalId=" + ((transactionalId == null) ? "null" : "'" + transactionalId.toString() + "'")
            + ", groupId=" + ((groupId == null) ? "null" : "'" + groupId.toString() + "'")
            + ", producerId=" + producerId
            + ", producerEpoch=" + producerEpoch
            + ", generationId=" + generationId
            + ", memberId=" + ((memberId == null) ? "null" : "'" + memberId.toString() + "'")
            + ", groupInstanceId=" + ((groupInstanceId == null) ? "null" : "'" + groupInstanceId.toString() + "'")
            + ", topics=" + MessageUtil.deepToString(topics.iterator())
            + ")";
    }
    
    public String transactionalId() {
        return this.transactionalId;
    }
    
    public String groupId() {
        return this.groupId;
    }
    
    public long producerId() {
        return this.producerId;
    }
    
    public short producerEpoch() {
        return this.producerEpoch;
    }
    
    public int generationId() {
        return this.generationId;
    }
    
    public String memberId() {
        return this.memberId;
    }
    
    public String groupInstanceId() {
        return this.groupInstanceId;
    }
    
    public List<TxnOffsetCommitRequestTopic> topics() {
        return this.topics;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public TxnOffsetCommitRequestData setTransactionalId(String v) {
        this.transactionalId = v;
        return this;
    }
    
    public TxnOffsetCommitRequestData setGroupId(String v) {
        this.groupId = v;
        return this;
    }
    
    public TxnOffsetCommitRequestData setProducerId(long v) {
        this.producerId = v;
        return this;
    }
    
    public TxnOffsetCommitRequestData setProducerEpoch(short v) {
        this.producerEpoch = v;
        return this;
    }
    
    public TxnOffsetCommitRequestData setGenerationId(int v) {
        this.generationId = v;
        return this;
    }
    
    public TxnOffsetCommitRequestData setMemberId(String v) {
        this.memberId = v;
        return this;
    }
    
    public TxnOffsetCommitRequestData setGroupInstanceId(String v) {
        this.groupInstanceId = v;
        return this;
    }
    
    public TxnOffsetCommitRequestData setTopics(List<TxnOffsetCommitRequestTopic> v) {
        this.topics = v;
        return this;
    }
    
    static public class TxnOffsetCommitRequestTopic implements Message {
        private String name;
        private List<TxnOffsetCommitRequestPartition> partitions;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partitions", new ArrayOf(TxnOffsetCommitRequestPartition.SCHEMA_0), "The partitions inside the topic that we want to committ offsets for.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partitions", new ArrayOf(TxnOffsetCommitRequestPartition.SCHEMA_2), "The partitions inside the topic that we want to committ offsets for.")
            );
        
        public static final Schema SCHEMA_3 =
            new Schema(
                new Field("name", Type.COMPACT_STRING, "The topic name."),
                new Field("partitions", new CompactArrayOf(TxnOffsetCommitRequestPartition.SCHEMA_3), "The partitions inside the topic that we want to committ offsets for."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3
        };
        
        public TxnOffsetCommitRequestTopic(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public TxnOffsetCommitRequestTopic(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public TxnOffsetCommitRequestTopic() {
            this.name = "";
            this.partitions = new ArrayList<TxnOffsetCommitRequestPartition>();
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
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of TxnOffsetCommitRequestTopic");
            }
            {
                int length;
                if (_version >= 3) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field name was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field name had invalid length " + length);
                } else {
                    this.name = _readable.readString(length);
                }
            }
            {
                if (_version >= 3) {
                    int arrayLength;
                    arrayLength = _readable.readUnsignedVarint() - 1;
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field partitions was serialized as null");
                    } else {
                        ArrayList<TxnOffsetCommitRequestPartition> newCollection = new ArrayList<TxnOffsetCommitRequestPartition>(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new TxnOffsetCommitRequestPartition(_readable, _version));
                        }
                        this.partitions = newCollection;
                    }
                } else {
                    int arrayLength;
                    arrayLength = _readable.readInt();
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field partitions was serialized as null");
                    } else {
                        ArrayList<TxnOffsetCommitRequestPartition> newCollection = new ArrayList<TxnOffsetCommitRequestPartition>(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new TxnOffsetCommitRequestPartition(_readable, _version));
                        }
                        this.partitions = newCollection;
                    }
                }
            }
            this._unknownTaggedFields = null;
            if (_version >= 3) {
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
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of TxnOffsetCommitRequestTopic");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                if (_version >= 3) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            if (_version >= 3) {
                _writable.writeUnsignedVarint(partitions.size() + 1);
                for (TxnOffsetCommitRequestPartition partitionsElement : partitions) {
                    partitionsElement.write(_writable, _cache, _version);
                }
            } else {
                _writable.writeInt(partitions.size());
                for (TxnOffsetCommitRequestPartition partitionsElement : partitions) {
                    partitionsElement.write(_writable, _cache, _version);
                }
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 3) {
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
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of TxnOffsetCommitRequestTopic");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 3) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.name = struct.getString("name");
            {
                Object[] _nestedObjects = struct.getArray("partitions");
                this.partitions = new ArrayList<TxnOffsetCommitRequestPartition>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.partitions.add(new TxnOffsetCommitRequestPartition((Struct) nestedObject, _version));
                }
            }
            if (_version >= 3) {
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
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of TxnOffsetCommitRequestTopic");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 3) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("name", this.name);
            {
                Struct[] _nestedObjects = new Struct[partitions.size()];
                int i = 0;
                for (TxnOffsetCommitRequestPartition element : this.partitions) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("partitions", (Object[]) _nestedObjects);
            }
            if (_version >= 3) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of TxnOffsetCommitRequestTopic");
            }
            {
                byte[] _stringBytes = name.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'name' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(name, _stringBytes);
                if (_version >= 3) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
            }
            {
                int _arraySize = 0;
                if (_version >= 3) {
                    _arraySize += ByteUtils.sizeOfUnsignedVarint(partitions.size() + 1);
                } else {
                    _arraySize += 4;
                }
                for (TxnOffsetCommitRequestPartition partitionsElement : partitions) {
                    _arraySize += partitionsElement.size(_cache, _version);
                }
                _size += _arraySize;
            }
            if (_unknownTaggedFields != null) {
                _numTaggedFields += _unknownTaggedFields.size();
                for (RawTaggedField _field : _unknownTaggedFields) {
                    _size += ByteUtils.sizeOfUnsignedVarint(_field.tag());
                    _size += ByteUtils.sizeOfUnsignedVarint(_field.size());
                    _size += _field.size();
                }
            }
            if (_version >= 3) {
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
            if (!(obj instanceof TxnOffsetCommitRequestTopic)) return false;
            TxnOffsetCommitRequestTopic other = (TxnOffsetCommitRequestTopic) obj;
            if (this.name == null) {
                if (other.name != null) return false;
            } else {
                if (!this.name.equals(other.name)) return false;
            }
            if (this.partitions == null) {
                if (other.partitions != null) return false;
            } else {
                if (!this.partitions.equals(other.partitions)) return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (name == null ? 0 : name.hashCode());
            hashCode = 31 * hashCode + (partitions == null ? 0 : partitions.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "TxnOffsetCommitRequestTopic("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", partitions=" + MessageUtil.deepToString(partitions.iterator())
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public List<TxnOffsetCommitRequestPartition> partitions() {
            return this.partitions;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public TxnOffsetCommitRequestTopic setName(String v) {
            this.name = v;
            return this;
        }
        
        public TxnOffsetCommitRequestTopic setPartitions(List<TxnOffsetCommitRequestPartition> v) {
            this.partitions = v;
            return this;
        }
    }
    
    static public class TxnOffsetCommitRequestPartition implements Message {
        private int partitionIndex;
        private long committedOffset;
        private int committedLeaderEpoch;
        private String committedMetadata;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("partition_index", Type.INT32, "The index of the partition within the topic."),
                new Field("committed_offset", Type.INT64, "The message offset to be committed."),
                new Field("committed_metadata", Type.NULLABLE_STRING, "Any associated metadata the client wants to keep.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 =
            new Schema(
                new Field("partition_index", Type.INT32, "The index of the partition within the topic."),
                new Field("committed_offset", Type.INT64, "The message offset to be committed."),
                new Field("committed_leader_epoch", Type.INT32, "The leader epoch of the last consumed record."),
                new Field("committed_metadata", Type.NULLABLE_STRING, "Any associated metadata the client wants to keep.")
            );
        
        public static final Schema SCHEMA_3 =
            new Schema(
                new Field("partition_index", Type.INT32, "The index of the partition within the topic."),
                new Field("committed_offset", Type.INT64, "The message offset to be committed."),
                new Field("committed_leader_epoch", Type.INT32, "The leader epoch of the last consumed record."),
                new Field("committed_metadata", Type.COMPACT_NULLABLE_STRING, "Any associated metadata the client wants to keep."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3
        };
        
        public TxnOffsetCommitRequestPartition(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public TxnOffsetCommitRequestPartition(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public TxnOffsetCommitRequestPartition() {
            this.partitionIndex = 0;
            this.committedOffset = 0L;
            this.committedLeaderEpoch = -1;
            this.committedMetadata = "";
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
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of TxnOffsetCommitRequestPartition");
            }
            this.partitionIndex = _readable.readInt();
            this.committedOffset = _readable.readLong();
            if (_version >= 2) {
                this.committedLeaderEpoch = _readable.readInt();
            } else {
                this.committedLeaderEpoch = -1;
            }
            {
                int length;
                if (_version >= 3) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    this.committedMetadata = null;
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field committedMetadata had invalid length " + length);
                } else {
                    this.committedMetadata = _readable.readString(length);
                }
            }
            this._unknownTaggedFields = null;
            if (_version >= 3) {
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
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of TxnOffsetCommitRequestPartition");
            }
            int _numTaggedFields = 0;
            _writable.writeInt(partitionIndex);
            _writable.writeLong(committedOffset);
            if (_version >= 2) {
                _writable.writeInt(committedLeaderEpoch);
            }
            if (committedMetadata == null) {
                if (_version >= 3) {
                    _writable.writeUnsignedVarint(0);
                } else {
                    _writable.writeShort((short) -1);
                }
            } else {
                byte[] _stringBytes = _cache.getSerializedValue(committedMetadata);
                if (_version >= 3) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 3) {
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
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of TxnOffsetCommitRequestPartition");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 3) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.partitionIndex = struct.getInt("partition_index");
            this.committedOffset = struct.getLong("committed_offset");
            if (_version >= 2) {
                this.committedLeaderEpoch = struct.getInt("committed_leader_epoch");
            } else {
                this.committedLeaderEpoch = -1;
            }
            this.committedMetadata = struct.getString("committed_metadata");
            if (_version >= 3) {
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
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of TxnOffsetCommitRequestPartition");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 3) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("partition_index", this.partitionIndex);
            struct.set("committed_offset", this.committedOffset);
            if (_version >= 2) {
                struct.set("committed_leader_epoch", this.committedLeaderEpoch);
            }
            struct.set("committed_metadata", this.committedMetadata);
            if (_version >= 3) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of TxnOffsetCommitRequestPartition");
            }
            _size += 4;
            _size += 8;
            if (_version >= 2) {
                _size += 4;
            }
            if (committedMetadata == null) {
                if (_version >= 3) {
                    _size += 1;
                } else {
                    _size += 2;
                }
            } else {
                byte[] _stringBytes = committedMetadata.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'committedMetadata' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(committedMetadata, _stringBytes);
                if (_version >= 3) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
            }
            if (_unknownTaggedFields != null) {
                _numTaggedFields += _unknownTaggedFields.size();
                for (RawTaggedField _field : _unknownTaggedFields) {
                    _size += ByteUtils.sizeOfUnsignedVarint(_field.tag());
                    _size += ByteUtils.sizeOfUnsignedVarint(_field.size());
                    _size += _field.size();
                }
            }
            if (_version >= 3) {
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
            if (!(obj instanceof TxnOffsetCommitRequestPartition)) return false;
            TxnOffsetCommitRequestPartition other = (TxnOffsetCommitRequestPartition) obj;
            if (partitionIndex != other.partitionIndex) return false;
            if (committedOffset != other.committedOffset) return false;
            if (committedLeaderEpoch != other.committedLeaderEpoch) return false;
            if (this.committedMetadata == null) {
                if (other.committedMetadata != null) return false;
            } else {
                if (!this.committedMetadata.equals(other.committedMetadata)) return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + partitionIndex;
            hashCode = 31 * hashCode + ((int) (committedOffset >> 32) ^ (int) committedOffset);
            hashCode = 31 * hashCode + committedLeaderEpoch;
            hashCode = 31 * hashCode + (committedMetadata == null ? 0 : committedMetadata.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "TxnOffsetCommitRequestPartition("
                + "partitionIndex=" + partitionIndex
                + ", committedOffset=" + committedOffset
                + ", committedLeaderEpoch=" + committedLeaderEpoch
                + ", committedMetadata=" + ((committedMetadata == null) ? "null" : "'" + committedMetadata.toString() + "'")
                + ")";
        }
        
        public int partitionIndex() {
            return this.partitionIndex;
        }
        
        public long committedOffset() {
            return this.committedOffset;
        }
        
        public int committedLeaderEpoch() {
            return this.committedLeaderEpoch;
        }
        
        public String committedMetadata() {
            return this.committedMetadata;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public TxnOffsetCommitRequestPartition setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
        
        public TxnOffsetCommitRequestPartition setCommittedOffset(long v) {
            this.committedOffset = v;
            return this;
        }
        
        public TxnOffsetCommitRequestPartition setCommittedLeaderEpoch(int v) {
            this.committedLeaderEpoch = v;
            return this;
        }
        
        public TxnOffsetCommitRequestPartition setCommittedMetadata(String v) {
            this.committedMetadata = v;
            return this;
        }
    }
}
