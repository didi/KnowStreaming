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


public class OffsetForLeaderEpochRequestData implements ApiMessage {
    private int replicaId;
    private List<OffsetForLeaderTopic> topics;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("topics", new ArrayOf(OffsetForLeaderTopic.SCHEMA_0), "Each topic to get offsets for.")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("topics", new ArrayOf(OffsetForLeaderTopic.SCHEMA_2), "Each topic to get offsets for.")
        );
    
    public static final Schema SCHEMA_3 =
        new Schema(
            new Field("replica_id", Type.INT32, "The broker ID of the follower, of -1 if this request is from a consumer."),
            new Field("topics", new ArrayOf(OffsetForLeaderTopic.SCHEMA_2), "Each topic to get offsets for.")
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2,
        SCHEMA_3
    };
    
    public OffsetForLeaderEpochRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public OffsetForLeaderEpochRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public OffsetForLeaderEpochRequestData() {
        this.replicaId = -2;
        this.topics = new ArrayList<OffsetForLeaderTopic>();
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
        if (_version >= 3) {
            this.replicaId = _readable.readInt();
        } else {
            this.replicaId = -2;
        }
        {
            int arrayLength;
            arrayLength = _readable.readInt();
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field topics was serialized as null");
            } else {
                ArrayList<OffsetForLeaderTopic> newCollection = new ArrayList<OffsetForLeaderTopic>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new OffsetForLeaderTopic(_readable, _version));
                }
                this.topics = newCollection;
            }
        }
        this._unknownTaggedFields = null;
    }
    
    @Override
    public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        if (_version >= 3) {
            _writable.writeInt(replicaId);
        }
        _writable.writeInt(topics.size());
        for (OffsetForLeaderTopic topicsElement : topics) {
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
        if (_version >= 3) {
            this.replicaId = struct.getInt("replica_id");
        } else {
            this.replicaId = -2;
        }
        {
            Object[] _nestedObjects = struct.getArray("topics");
            this.topics = new ArrayList<OffsetForLeaderTopic>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.topics.add(new OffsetForLeaderTopic((Struct) nestedObject, _version));
            }
        }
    }
    
    @Override
    public Struct toStruct(short _version) {
        TreeMap<Integer, Object> _taggedFields = null;
        Struct struct = new Struct(SCHEMAS[_version]);
        if (_version >= 3) {
            struct.set("replica_id", this.replicaId);
        }
        {
            Struct[] _nestedObjects = new Struct[topics.size()];
            int i = 0;
            for (OffsetForLeaderTopic element : this.topics) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("topics", (Object[]) _nestedObjects);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        if (_version >= 3) {
            _size += 4;
        }
        {
            int _arraySize = 0;
            _arraySize += 4;
            for (OffsetForLeaderTopic topicsElement : topics) {
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
        if (!(obj instanceof OffsetForLeaderEpochRequestData)) return false;
        OffsetForLeaderEpochRequestData other = (OffsetForLeaderEpochRequestData) obj;
        if (replicaId != other.replicaId) return false;
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
        hashCode = 31 * hashCode + (topics == null ? 0 : topics.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "OffsetForLeaderEpochRequestData("
            + "replicaId=" + replicaId
            + ", topics=" + MessageUtil.deepToString(topics.iterator())
            + ")";
    }
    
    public int replicaId() {
        return this.replicaId;
    }
    
    public List<OffsetForLeaderTopic> topics() {
        return this.topics;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public OffsetForLeaderEpochRequestData setReplicaId(int v) {
        this.replicaId = v;
        return this;
    }
    
    public OffsetForLeaderEpochRequestData setTopics(List<OffsetForLeaderTopic> v) {
        this.topics = v;
        return this;
    }
    
    static public class OffsetForLeaderTopic implements Message {
        private String name;
        private List<OffsetForLeaderPartition> partitions;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partitions", new ArrayOf(OffsetForLeaderPartition.SCHEMA_0), "Each partition to get offsets for.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partitions", new ArrayOf(OffsetForLeaderPartition.SCHEMA_2), "Each partition to get offsets for.")
            );
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3
        };
        
        public OffsetForLeaderTopic(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public OffsetForLeaderTopic(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public OffsetForLeaderTopic() {
            this.name = "";
            this.partitions = new ArrayList<OffsetForLeaderPartition>();
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetForLeaderTopic");
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
                    ArrayList<OffsetForLeaderPartition> newCollection = new ArrayList<OffsetForLeaderPartition>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new OffsetForLeaderPartition(_readable, _version));
                    }
                    this.partitions = newCollection;
                }
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of OffsetForLeaderTopic");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                _writable.writeShort((short) _stringBytes.length);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeInt(partitions.size());
            for (OffsetForLeaderPartition partitionsElement : partitions) {
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetForLeaderTopic");
            }
            this._unknownTaggedFields = null;
            this.name = struct.getString("name");
            {
                Object[] _nestedObjects = struct.getArray("partitions");
                this.partitions = new ArrayList<OffsetForLeaderPartition>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.partitions.add(new OffsetForLeaderPartition((Struct) nestedObject, _version));
                }
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of OffsetForLeaderTopic");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("name", this.name);
            {
                Struct[] _nestedObjects = new Struct[partitions.size()];
                int i = 0;
                for (OffsetForLeaderPartition element : this.partitions) {
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
                throw new UnsupportedVersionException("Can't size version " + _version + " of OffsetForLeaderTopic");
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
                for (OffsetForLeaderPartition partitionsElement : partitions) {
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
            if (!(obj instanceof OffsetForLeaderTopic)) return false;
            OffsetForLeaderTopic other = (OffsetForLeaderTopic) obj;
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
            return "OffsetForLeaderTopic("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", partitions=" + MessageUtil.deepToString(partitions.iterator())
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public List<OffsetForLeaderPartition> partitions() {
            return this.partitions;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public OffsetForLeaderTopic setName(String v) {
            this.name = v;
            return this;
        }
        
        public OffsetForLeaderTopic setPartitions(List<OffsetForLeaderPartition> v) {
            this.partitions = v;
            return this;
        }
    }
    
    static public class OffsetForLeaderPartition implements Message {
        private int partitionIndex;
        private int currentLeaderEpoch;
        private int leaderEpoch;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("leader_epoch", Type.INT32, "The epoch to look up an offset for.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("current_leader_epoch", Type.INT32, "An epoch used to fence consumers/replicas with old metadata.  If the epoch provided by the client is larger than the current epoch known to the broker, then the UNKNOWN_LEADER_EPOCH error code will be returned. If the provided epoch is smaller, then the FENCED_LEADER_EPOCH error code will be returned."),
                new Field("leader_epoch", Type.INT32, "The epoch to look up an offset for.")
            );
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3
        };
        
        public OffsetForLeaderPartition(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public OffsetForLeaderPartition(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public OffsetForLeaderPartition() {
            this.partitionIndex = 0;
            this.currentLeaderEpoch = -1;
            this.leaderEpoch = 0;
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetForLeaderPartition");
            }
            this.partitionIndex = _readable.readInt();
            if (_version >= 2) {
                this.currentLeaderEpoch = _readable.readInt();
            } else {
                this.currentLeaderEpoch = -1;
            }
            this.leaderEpoch = _readable.readInt();
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of OffsetForLeaderPartition");
            }
            int _numTaggedFields = 0;
            _writable.writeInt(partitionIndex);
            if (_version >= 2) {
                _writable.writeInt(currentLeaderEpoch);
            }
            _writable.writeInt(leaderEpoch);
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetForLeaderPartition");
            }
            this._unknownTaggedFields = null;
            this.partitionIndex = struct.getInt("partition_index");
            if (_version >= 2) {
                this.currentLeaderEpoch = struct.getInt("current_leader_epoch");
            } else {
                this.currentLeaderEpoch = -1;
            }
            this.leaderEpoch = struct.getInt("leader_epoch");
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of OffsetForLeaderPartition");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("partition_index", this.partitionIndex);
            if (_version >= 2) {
                struct.set("current_leader_epoch", this.currentLeaderEpoch);
            }
            struct.set("leader_epoch", this.leaderEpoch);
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of OffsetForLeaderPartition");
            }
            _size += 4;
            if (_version >= 2) {
                _size += 4;
            }
            _size += 4;
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
            if (!(obj instanceof OffsetForLeaderPartition)) return false;
            OffsetForLeaderPartition other = (OffsetForLeaderPartition) obj;
            if (partitionIndex != other.partitionIndex) return false;
            if (currentLeaderEpoch != other.currentLeaderEpoch) return false;
            if (leaderEpoch != other.leaderEpoch) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + partitionIndex;
            hashCode = 31 * hashCode + currentLeaderEpoch;
            hashCode = 31 * hashCode + leaderEpoch;
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "OffsetForLeaderPartition("
                + "partitionIndex=" + partitionIndex
                + ", currentLeaderEpoch=" + currentLeaderEpoch
                + ", leaderEpoch=" + leaderEpoch
                + ")";
        }
        
        public int partitionIndex() {
            return this.partitionIndex;
        }
        
        public int currentLeaderEpoch() {
            return this.currentLeaderEpoch;
        }
        
        public int leaderEpoch() {
            return this.leaderEpoch;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public OffsetForLeaderPartition setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
        
        public OffsetForLeaderPartition setCurrentLeaderEpoch(int v) {
            this.currentLeaderEpoch = v;
            return this;
        }
        
        public OffsetForLeaderPartition setLeaderEpoch(int v) {
            this.leaderEpoch = v;
            return this;
        }
    }
}
