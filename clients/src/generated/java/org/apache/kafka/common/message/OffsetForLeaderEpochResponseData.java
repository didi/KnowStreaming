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
import org.apache.kafka.common.protocol.Message;
import org.apache.kafka.common.protocol.MessageUtil;
import org.apache.kafka.common.protocol.ObjectSerializationCache;
import org.apache.kafka.common.protocol.Readable;
import org.apache.kafka.common.protocol.Writable;
import org.apache.kafka.common.protocol.types.ArrayOf;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.protocol.types.RawTaggedField;
import org.apache.kafka.common.protocol.types.RawTaggedFieldWriter;
import org.apache.kafka.common.protocol.types.Schema;
import org.apache.kafka.common.protocol.types.Struct;
import org.apache.kafka.common.protocol.types.Type;
import org.apache.kafka.common.utils.ByteUtils;


public class OffsetForLeaderEpochResponseData implements ApiMessage {
    private int throttleTimeMs;
    private List<OffsetForLeaderTopicResult> topics;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("topics", new ArrayOf(OffsetForLeaderTopicResult.SCHEMA_0), "Each topic we fetched offsets for.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("topics", new ArrayOf(OffsetForLeaderTopicResult.SCHEMA_1), "Each topic we fetched offsets for.")
        );
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("topics", new ArrayOf(OffsetForLeaderTopicResult.SCHEMA_1), "Each topic we fetched offsets for.")
        );
    
    public static final Schema SCHEMA_3 = SCHEMA_2;
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2,
        SCHEMA_3
    };
    
    public OffsetForLeaderEpochResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public OffsetForLeaderEpochResponseData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public OffsetForLeaderEpochResponseData() {
        this.throttleTimeMs = 0;
        this.topics = new ArrayList<OffsetForLeaderTopicResult>();
    }
    
    @Override
    public short apiKey() {
        return 23;
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
        if (_version >= 2) {
            this.throttleTimeMs = _readable.readInt();
        } else {
            this.throttleTimeMs = 0;
        }
        {
            int arrayLength;
            arrayLength = _readable.readInt();
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field topics was serialized as null");
            } else {
                ArrayList<OffsetForLeaderTopicResult> newCollection = new ArrayList<OffsetForLeaderTopicResult>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new OffsetForLeaderTopicResult(_readable, _version));
                }
                this.topics = newCollection;
            }
        }
        this._unknownTaggedFields = null;
    }
    
    @Override
    public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        if (_version >= 2) {
            _writable.writeInt(throttleTimeMs);
        }
        _writable.writeInt(topics.size());
        for (OffsetForLeaderTopicResult topicsElement : topics) {
            topicsElement.write(_writable, _cache, _version);
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
        if (_version >= 2) {
            this.throttleTimeMs = struct.getInt("throttle_time_ms");
        } else {
            this.throttleTimeMs = 0;
        }
        {
            Object[] _nestedObjects = struct.getArray("topics");
            this.topics = new ArrayList<OffsetForLeaderTopicResult>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.topics.add(new OffsetForLeaderTopicResult((Struct) nestedObject, _version));
            }
        }
    }
    
    @Override
    public Struct toStruct(short _version) {
        TreeMap<Integer, Object> _taggedFields = null;
        Struct struct = new Struct(SCHEMAS[_version]);
        if (_version >= 2) {
            struct.set("throttle_time_ms", this.throttleTimeMs);
        }
        {
            Struct[] _nestedObjects = new Struct[topics.size()];
            int i = 0;
            for (OffsetForLeaderTopicResult element : this.topics) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("topics", (Object[]) _nestedObjects);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        if (_version >= 2) {
            _size += 4;
        }
        {
            int _arraySize = 0;
            _arraySize += 4;
            for (OffsetForLeaderTopicResult topicsElement : topics) {
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
        if (_numTaggedFields > 0) {
            throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
        }
        return _size;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OffsetForLeaderEpochResponseData)) return false;
        OffsetForLeaderEpochResponseData other = (OffsetForLeaderEpochResponseData) obj;
        if (throttleTimeMs != other.throttleTimeMs) return false;
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
        hashCode = 31 * hashCode + throttleTimeMs;
        hashCode = 31 * hashCode + (topics == null ? 0 : topics.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "OffsetForLeaderEpochResponseData("
            + "throttleTimeMs=" + throttleTimeMs
            + ", topics=" + MessageUtil.deepToString(topics.iterator())
            + ")";
    }
    
    public int throttleTimeMs() {
        return this.throttleTimeMs;
    }
    
    public List<OffsetForLeaderTopicResult> topics() {
        return this.topics;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public OffsetForLeaderEpochResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
    
    public OffsetForLeaderEpochResponseData setTopics(List<OffsetForLeaderTopicResult> v) {
        this.topics = v;
        return this;
    }
    
    static public class OffsetForLeaderTopicResult implements Message {
        private String name;
        private List<OffsetForLeaderPartitionResult> partitions;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partitions", new ArrayOf(OffsetForLeaderPartitionResult.SCHEMA_0), "Each partition in the topic we fetched offsets for.")
            );
        
        public static final Schema SCHEMA_1 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partitions", new ArrayOf(OffsetForLeaderPartitionResult.SCHEMA_1), "Each partition in the topic we fetched offsets for.")
            );
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3
        };
        
        public OffsetForLeaderTopicResult(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public OffsetForLeaderTopicResult(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public OffsetForLeaderTopicResult() {
            this.name = "";
            this.partitions = new ArrayList<OffsetForLeaderPartitionResult>();
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetForLeaderTopicResult");
            }
            {
                int length;
                length = _readable.readShort();
                if (length < 0) {
                    throw new RuntimeException("non-nullable field name was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field name had invalid length " + length);
                } else {
                    this.name = _readable.readString(length);
                }
            }
            {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field partitions was serialized as null");
                } else {
                    ArrayList<OffsetForLeaderPartitionResult> newCollection = new ArrayList<OffsetForLeaderPartitionResult>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new OffsetForLeaderPartitionResult(_readable, _version));
                    }
                    this.partitions = newCollection;
                }
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of OffsetForLeaderTopicResult");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                _writable.writeShort((short) _stringBytes.length);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeInt(partitions.size());
            for (OffsetForLeaderPartitionResult partitionsElement : partitions) {
                partitionsElement.write(_writable, _cache, _version);
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
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetForLeaderTopicResult");
            }
            this._unknownTaggedFields = null;
            this.name = struct.getString("name");
            {
                Object[] _nestedObjects = struct.getArray("partitions");
                this.partitions = new ArrayList<OffsetForLeaderPartitionResult>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.partitions.add(new OffsetForLeaderPartitionResult((Struct) nestedObject, _version));
                }
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of OffsetForLeaderTopicResult");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("name", this.name);
            {
                Struct[] _nestedObjects = new Struct[partitions.size()];
                int i = 0;
                for (OffsetForLeaderPartitionResult element : this.partitions) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("partitions", (Object[]) _nestedObjects);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of OffsetForLeaderTopicResult");
            }
            {
                byte[] _stringBytes = name.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'name' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(name, _stringBytes);
                _size += _stringBytes.length + 2;
            }
            {
                int _arraySize = 0;
                _arraySize += 4;
                for (OffsetForLeaderPartitionResult partitionsElement : partitions) {
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
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
            return _size;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof OffsetForLeaderTopicResult)) return false;
            OffsetForLeaderTopicResult other = (OffsetForLeaderTopicResult) obj;
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
            return "OffsetForLeaderTopicResult("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", partitions=" + MessageUtil.deepToString(partitions.iterator())
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public List<OffsetForLeaderPartitionResult> partitions() {
            return this.partitions;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public OffsetForLeaderTopicResult setName(String v) {
            this.name = v;
            return this;
        }
        
        public OffsetForLeaderTopicResult setPartitions(List<OffsetForLeaderPartitionResult> v) {
            this.partitions = v;
            return this;
        }
    }
    
    static public class OffsetForLeaderPartitionResult implements Message {
        private short errorCode;
        private int partitionIndex;
        private int leaderEpoch;
        private long endOffset;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("error_code", Type.INT16, "The error code 0, or if there was no error."),
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("end_offset", Type.INT64, "The end offset of the epoch.")
            );
        
        public static final Schema SCHEMA_1 =
            new Schema(
                new Field("error_code", Type.INT16, "The error code 0, or if there was no error."),
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("leader_epoch", Type.INT32, "The leader epoch of the partition."),
                new Field("end_offset", Type.INT64, "The end offset of the epoch.")
            );
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3
        };
        
        public OffsetForLeaderPartitionResult(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public OffsetForLeaderPartitionResult(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public OffsetForLeaderPartitionResult() {
            this.errorCode = (short) 0;
            this.partitionIndex = 0;
            this.leaderEpoch = -1;
            this.endOffset = 0L;
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetForLeaderPartitionResult");
            }
            this.errorCode = _readable.readShort();
            this.partitionIndex = _readable.readInt();
            if (_version >= 1) {
                this.leaderEpoch = _readable.readInt();
            } else {
                this.leaderEpoch = -1;
            }
            this.endOffset = _readable.readLong();
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of OffsetForLeaderPartitionResult");
            }
            int _numTaggedFields = 0;
            _writable.writeShort(errorCode);
            _writable.writeInt(partitionIndex);
            if (_version >= 1) {
                _writable.writeInt(leaderEpoch);
            }
            _writable.writeLong(endOffset);
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public void fromStruct(Struct struct, short _version) {
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetForLeaderPartitionResult");
            }
            this._unknownTaggedFields = null;
            this.errorCode = struct.getShort("error_code");
            this.partitionIndex = struct.getInt("partition_index");
            if (_version >= 1) {
                this.leaderEpoch = struct.getInt("leader_epoch");
            } else {
                this.leaderEpoch = -1;
            }
            this.endOffset = struct.getLong("end_offset");
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of OffsetForLeaderPartitionResult");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("error_code", this.errorCode);
            struct.set("partition_index", this.partitionIndex);
            if (_version >= 1) {
                struct.set("leader_epoch", this.leaderEpoch);
            }
            struct.set("end_offset", this.endOffset);
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of OffsetForLeaderPartitionResult");
            }
            _size += 2;
            _size += 4;
            if (_version >= 1) {
                _size += 4;
            }
            _size += 8;
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
            if (!(obj instanceof OffsetForLeaderPartitionResult)) return false;
            OffsetForLeaderPartitionResult other = (OffsetForLeaderPartitionResult) obj;
            if (errorCode != other.errorCode) return false;
            if (partitionIndex != other.partitionIndex) return false;
            if (leaderEpoch != other.leaderEpoch) return false;
            if (endOffset != other.endOffset) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + errorCode;
            hashCode = 31 * hashCode + partitionIndex;
            hashCode = 31 * hashCode + leaderEpoch;
            hashCode = 31 * hashCode + ((int) (endOffset >> 32) ^ (int) endOffset);
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "OffsetForLeaderPartitionResult("
                + "errorCode=" + errorCode
                + ", partitionIndex=" + partitionIndex
                + ", leaderEpoch=" + leaderEpoch
                + ", endOffset=" + endOffset
                + ")";
        }
        
        public short errorCode() {
            return this.errorCode;
        }
        
        public int partitionIndex() {
            return this.partitionIndex;
        }
        
        public int leaderEpoch() {
            return this.leaderEpoch;
        }
        
        public long endOffset() {
            return this.endOffset;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public OffsetForLeaderPartitionResult setErrorCode(short v) {
            this.errorCode = v;
            return this;
        }
        
        public OffsetForLeaderPartitionResult setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
        
        public OffsetForLeaderPartitionResult setLeaderEpoch(int v) {
            this.leaderEpoch = v;
            return this;
        }
        
        public OffsetForLeaderPartitionResult setEndOffset(long v) {
            this.endOffset = v;
            return this;
        }
    }
}
