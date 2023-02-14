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


public class ListOffsetRequestData implements ApiMessage {
    private int replicaId;
    private byte isolationLevel;
    private List<ListOffsetTopic> topics;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("replica_id", Type.INT32, "The broker ID of the requestor, or -1 if this request is being made by a normal consumer."),
            new Field("topics", new ArrayOf(ListOffsetTopic.SCHEMA_0), "Each topic in the request.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("replica_id", Type.INT32, "The broker ID of the requestor, or -1 if this request is being made by a normal consumer."),
            new Field("topics", new ArrayOf(ListOffsetTopic.SCHEMA_1), "Each topic in the request.")
        );
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("replica_id", Type.INT32, "The broker ID of the requestor, or -1 if this request is being made by a normal consumer."),
            new Field("isolation_level", Type.INT8, "This setting controls the visibility of transactional records. Using READ_UNCOMMITTED (isolation_level = 0) makes all records visible. With READ_COMMITTED (isolation_level = 1), non-transactional and COMMITTED transactional records are visible. To be more concrete, READ_COMMITTED returns all data from offsets smaller than the current LSO (last stable offset), and enables the inclusion of the list of aborted transactions in the result, which allows consumers to discard ABORTED transactional records"),
            new Field("topics", new ArrayOf(ListOffsetTopic.SCHEMA_1), "Each topic in the request.")
        );
    
    public static final Schema SCHEMA_3 = SCHEMA_2;
    
    public static final Schema SCHEMA_4 =
        new Schema(
            new Field("replica_id", Type.INT32, "The broker ID of the requestor, or -1 if this request is being made by a normal consumer."),
            new Field("isolation_level", Type.INT8, "This setting controls the visibility of transactional records. Using READ_UNCOMMITTED (isolation_level = 0) makes all records visible. With READ_COMMITTED (isolation_level = 1), non-transactional and COMMITTED transactional records are visible. To be more concrete, READ_COMMITTED returns all data from offsets smaller than the current LSO (last stable offset), and enables the inclusion of the list of aborted transactions in the result, which allows consumers to discard ABORTED transactional records"),
            new Field("topics", new ArrayOf(ListOffsetTopic.SCHEMA_4), "Each topic in the request.")
        );
    
    public static final Schema SCHEMA_5 = SCHEMA_4;
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2,
        SCHEMA_3,
        SCHEMA_4,
        SCHEMA_5
    };
    
    public ListOffsetRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public ListOffsetRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public ListOffsetRequestData() {
        this.replicaId = 0;
        this.isolationLevel = (byte) 0;
        this.topics = new ArrayList<ListOffsetTopic>();
    }
    
    @Override
    public short apiKey() {
        return 2;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 5;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        this.replicaId = _readable.readInt();
        if (_version >= 2) {
            this.isolationLevel = _readable.readByte();
        } else {
            this.isolationLevel = (byte) 0;
        }
        {
            int arrayLength;
            arrayLength = _readable.readInt();
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field topics was serialized as null");
            } else {
                ArrayList<ListOffsetTopic> newCollection = new ArrayList<ListOffsetTopic>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new ListOffsetTopic(_readable, _version));
                }
                this.topics = newCollection;
            }
        }
        this._unknownTaggedFields = null;
    }
    
    @Override
    public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        _writable.writeInt(replicaId);
        if (_version >= 2) {
            _writable.writeByte(isolationLevel);
        } else {
            if (isolationLevel != (byte) 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default isolationLevel at version " + _version);
            }
        }
        _writable.writeInt(topics.size());
        for (ListOffsetTopic topicsElement : topics) {
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
        this.replicaId = struct.getInt("replica_id");
        if (_version >= 2) {
            this.isolationLevel = struct.getByte("isolation_level");
        } else {
            this.isolationLevel = (byte) 0;
        }
        {
            Object[] _nestedObjects = struct.getArray("topics");
            this.topics = new ArrayList<ListOffsetTopic>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.topics.add(new ListOffsetTopic((Struct) nestedObject, _version));
            }
        }
    }
    
    @Override
    public Struct toStruct(short _version) {
        TreeMap<Integer, Object> _taggedFields = null;
        Struct struct = new Struct(SCHEMAS[_version]);
        struct.set("replica_id", this.replicaId);
        if (_version >= 2) {
            struct.set("isolation_level", this.isolationLevel);
        } else {
            if (isolationLevel != (byte) 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default isolationLevel at version " + _version);
            }
        }
        {
            Struct[] _nestedObjects = new Struct[topics.size()];
            int i = 0;
            for (ListOffsetTopic element : this.topics) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("topics", (Object[]) _nestedObjects);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        _size += 4;
        if (_version >= 2) {
            _size += 1;
        }
        {
            int _arraySize = 0;
            _arraySize += 4;
            for (ListOffsetTopic topicsElement : topics) {
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
        if (!(obj instanceof ListOffsetRequestData)) return false;
        ListOffsetRequestData other = (ListOffsetRequestData) obj;
        if (replicaId != other.replicaId) return false;
        if (isolationLevel != other.isolationLevel) return false;
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
        hashCode = 31 * hashCode + replicaId;
        hashCode = 31 * hashCode + isolationLevel;
        hashCode = 31 * hashCode + (topics == null ? 0 : topics.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "ListOffsetRequestData("
            + "replicaId=" + replicaId
            + ", isolationLevel=" + isolationLevel
            + ", topics=" + MessageUtil.deepToString(topics.iterator())
            + ")";
    }
    
    public int replicaId() {
        return this.replicaId;
    }
    
    public byte isolationLevel() {
        return this.isolationLevel;
    }
    
    public List<ListOffsetTopic> topics() {
        return this.topics;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public ListOffsetRequestData setReplicaId(int v) {
        this.replicaId = v;
        return this;
    }
    
    public ListOffsetRequestData setIsolationLevel(byte v) {
        this.isolationLevel = v;
        return this;
    }
    
    public ListOffsetRequestData setTopics(List<ListOffsetTopic> v) {
        this.topics = v;
        return this;
    }
    
    static public class ListOffsetTopic implements Message {
        private String name;
        private List<ListOffsetPartition> partitions;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partitions", new ArrayOf(ListOffsetPartition.SCHEMA_0), "Each partition in the request.")
            );
        
        public static final Schema SCHEMA_1 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partitions", new ArrayOf(ListOffsetPartition.SCHEMA_1), "Each partition in the request.")
            );
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partitions", new ArrayOf(ListOffsetPartition.SCHEMA_4), "Each partition in the request.")
            );
        
        public static final Schema SCHEMA_5 = SCHEMA_4;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3,
            SCHEMA_4,
            SCHEMA_5
        };
        
        public ListOffsetTopic(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public ListOffsetTopic(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public ListOffsetTopic() {
            this.name = "";
            this.partitions = new ArrayList<ListOffsetPartition>();
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 5;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of ListOffsetTopic");
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
                    ArrayList<ListOffsetPartition> newCollection = new ArrayList<ListOffsetPartition>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new ListOffsetPartition(_readable, _version));
                    }
                    this.partitions = newCollection;
                }
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of ListOffsetTopic");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                _writable.writeShort((short) _stringBytes.length);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeInt(partitions.size());
            for (ListOffsetPartition partitionsElement : partitions) {
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
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of ListOffsetTopic");
            }
            this._unknownTaggedFields = null;
            this.name = struct.getString("name");
            {
                Object[] _nestedObjects = struct.getArray("partitions");
                this.partitions = new ArrayList<ListOffsetPartition>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.partitions.add(new ListOffsetPartition((Struct) nestedObject, _version));
                }
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of ListOffsetTopic");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("name", this.name);
            {
                Struct[] _nestedObjects = new Struct[partitions.size()];
                int i = 0;
                for (ListOffsetPartition element : this.partitions) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("partitions", (Object[]) _nestedObjects);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of ListOffsetTopic");
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
                for (ListOffsetPartition partitionsElement : partitions) {
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
            if (!(obj instanceof ListOffsetTopic)) return false;
            ListOffsetTopic other = (ListOffsetTopic) obj;
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
            return "ListOffsetTopic("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", partitions=" + MessageUtil.deepToString(partitions.iterator())
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public List<ListOffsetPartition> partitions() {
            return this.partitions;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public ListOffsetTopic setName(String v) {
            this.name = v;
            return this;
        }
        
        public ListOffsetTopic setPartitions(List<ListOffsetPartition> v) {
            this.partitions = v;
            return this;
        }
    }
    
    static public class ListOffsetPartition implements Message {
        private int partitionIndex;
        private int currentLeaderEpoch;
        private long timestamp;
        private int maxNumOffsets;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("timestamp", Type.INT64, "The current timestamp."),
                new Field("max_num_offsets", Type.INT32, "The maximum number of offsets to report.")
            );
        
        public static final Schema SCHEMA_1 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("timestamp", Type.INT64, "The current timestamp.")
            );
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("current_leader_epoch", Type.INT32, "The current leader epoch."),
                new Field("timestamp", Type.INT64, "The current timestamp.")
            );
        
        public static final Schema SCHEMA_5 = SCHEMA_4;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3,
            SCHEMA_4,
            SCHEMA_5
        };
        
        public ListOffsetPartition(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public ListOffsetPartition(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public ListOffsetPartition() {
            this.partitionIndex = 0;
            this.currentLeaderEpoch = 0;
            this.timestamp = 0L;
            this.maxNumOffsets = 0;
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 5;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of ListOffsetPartition");
            }
            this.partitionIndex = _readable.readInt();
            if (_version >= 4) {
                this.currentLeaderEpoch = _readable.readInt();
            } else {
                this.currentLeaderEpoch = 0;
            }
            this.timestamp = _readable.readLong();
            if (_version <= 0) {
                this.maxNumOffsets = _readable.readInt();
            } else {
                this.maxNumOffsets = 0;
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of ListOffsetPartition");
            }
            int _numTaggedFields = 0;
            _writable.writeInt(partitionIndex);
            if (_version >= 4) {
                _writable.writeInt(currentLeaderEpoch);
            } else {
                if (currentLeaderEpoch != 0) {
                    throw new UnsupportedVersionException("Attempted to write a non-default currentLeaderEpoch at version " + _version);
                }
            }
            _writable.writeLong(timestamp);
            if (_version <= 0) {
                _writable.writeInt(maxNumOffsets);
            } else {
                if (maxNumOffsets != 0) {
                    throw new UnsupportedVersionException("Attempted to write a non-default maxNumOffsets at version " + _version);
                }
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
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of ListOffsetPartition");
            }
            this._unknownTaggedFields = null;
            this.partitionIndex = struct.getInt("partition_index");
            if (_version >= 4) {
                this.currentLeaderEpoch = struct.getInt("current_leader_epoch");
            } else {
                this.currentLeaderEpoch = 0;
            }
            this.timestamp = struct.getLong("timestamp");
            if (_version <= 0) {
                this.maxNumOffsets = struct.getInt("max_num_offsets");
            } else {
                this.maxNumOffsets = 0;
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of ListOffsetPartition");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("partition_index", this.partitionIndex);
            if (_version >= 4) {
                struct.set("current_leader_epoch", this.currentLeaderEpoch);
            } else {
                if (currentLeaderEpoch != 0) {
                    throw new UnsupportedVersionException("Attempted to write a non-default currentLeaderEpoch at version " + _version);
                }
            }
            struct.set("timestamp", this.timestamp);
            if (_version <= 0) {
                struct.set("max_num_offsets", this.maxNumOffsets);
            } else {
                if (maxNumOffsets != 0) {
                    throw new UnsupportedVersionException("Attempted to write a non-default maxNumOffsets at version " + _version);
                }
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of ListOffsetPartition");
            }
            _size += 4;
            if (_version >= 4) {
                _size += 4;
            }
            _size += 8;
            if (_version <= 0) {
                _size += 4;
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
            if (!(obj instanceof ListOffsetPartition)) return false;
            ListOffsetPartition other = (ListOffsetPartition) obj;
            if (partitionIndex != other.partitionIndex) return false;
            if (currentLeaderEpoch != other.currentLeaderEpoch) return false;
            if (timestamp != other.timestamp) return false;
            if (maxNumOffsets != other.maxNumOffsets) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + partitionIndex;
            hashCode = 31 * hashCode + currentLeaderEpoch;
            hashCode = 31 * hashCode + ((int) (timestamp >> 32) ^ (int) timestamp);
            hashCode = 31 * hashCode + maxNumOffsets;
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "ListOffsetPartition("
                + "partitionIndex=" + partitionIndex
                + ", currentLeaderEpoch=" + currentLeaderEpoch
                + ", timestamp=" + timestamp
                + ", maxNumOffsets=" + maxNumOffsets
                + ")";
        }
        
        public int partitionIndex() {
            return this.partitionIndex;
        }
        
        public int currentLeaderEpoch() {
            return this.currentLeaderEpoch;
        }
        
        public long timestamp() {
            return this.timestamp;
        }
        
        public int maxNumOffsets() {
            return this.maxNumOffsets;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public ListOffsetPartition setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
        
        public ListOffsetPartition setCurrentLeaderEpoch(int v) {
            this.currentLeaderEpoch = v;
            return this;
        }
        
        public ListOffsetPartition setTimestamp(long v) {
            this.timestamp = v;
            return this;
        }
        
        public ListOffsetPartition setMaxNumOffsets(int v) {
            this.maxNumOffsets = v;
            return this;
        }
    }
}
