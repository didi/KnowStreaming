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


public class StopReplicaRequestData implements ApiMessage {
    private int controllerId;
    private int controllerEpoch;
    private long brokerEpoch;
    private boolean deletePartitions;
    private List<StopReplicaPartitionV0> ungroupedPartitions;
    private List<StopReplicaTopic> topics;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("controller_id", Type.INT32, "The controller id."),
            new Field("controller_epoch", Type.INT32, "The controller epoch."),
            new Field("delete_partitions", Type.BOOLEAN, "Whether these partitions should be deleted."),
            new Field("ungrouped_partitions", new ArrayOf(StopReplicaPartitionV0.SCHEMA_0), "The partitions to stop.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("controller_id", Type.INT32, "The controller id."),
            new Field("controller_epoch", Type.INT32, "The controller epoch."),
            new Field("broker_epoch", Type.INT64, "The broker epoch."),
            new Field("delete_partitions", Type.BOOLEAN, "Whether these partitions should be deleted."),
            new Field("topics", new ArrayOf(StopReplicaTopic.SCHEMA_1), "The topics to stop.")
        );
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("controller_id", Type.INT32, "The controller id."),
            new Field("controller_epoch", Type.INT32, "The controller epoch."),
            new Field("broker_epoch", Type.INT64, "The broker epoch."),
            new Field("delete_partitions", Type.BOOLEAN, "Whether these partitions should be deleted."),
            new Field("topics", new CompactArrayOf(StopReplicaTopic.SCHEMA_2), "The topics to stop."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2
    };
    
    public StopReplicaRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public StopReplicaRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public StopReplicaRequestData() {
        this.controllerId = 0;
        this.controllerEpoch = 0;
        this.brokerEpoch = -1L;
        this.deletePartitions = false;
        this.ungroupedPartitions = new ArrayList<StopReplicaPartitionV0>();
        this.topics = new ArrayList<StopReplicaTopic>();
    }
    
    @Override
    public short apiKey() {
        return 5;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 2;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        this.controllerId = _readable.readInt();
        this.controllerEpoch = _readable.readInt();
        if (_version >= 1) {
            this.brokerEpoch = _readable.readLong();
        } else {
            this.brokerEpoch = -1L;
        }
        this.deletePartitions = _readable.readByte() != 0;
        if (_version <= 0) {
            int arrayLength;
            arrayLength = _readable.readInt();
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field ungroupedPartitions was serialized as null");
            } else {
                ArrayList<StopReplicaPartitionV0> newCollection = new ArrayList<StopReplicaPartitionV0>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new StopReplicaPartitionV0(_readable, _version));
                }
                this.ungroupedPartitions = newCollection;
            }
        } else {
            this.ungroupedPartitions = new ArrayList<StopReplicaPartitionV0>();
        }
        if (_version >= 1) {
            if (_version >= 2) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topics was serialized as null");
                } else {
                    ArrayList<StopReplicaTopic> newCollection = new ArrayList<StopReplicaTopic>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new StopReplicaTopic(_readable, _version));
                    }
                    this.topics = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topics was serialized as null");
                } else {
                    ArrayList<StopReplicaTopic> newCollection = new ArrayList<StopReplicaTopic>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new StopReplicaTopic(_readable, _version));
                    }
                    this.topics = newCollection;
                }
            }
        } else {
            this.topics = new ArrayList<StopReplicaTopic>();
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
        _writable.writeInt(controllerId);
        _writable.writeInt(controllerEpoch);
        if (_version >= 1) {
            _writable.writeLong(brokerEpoch);
        }
        _writable.writeByte(deletePartitions ? (byte) 1 : (byte) 0);
        if (_version <= 0) {
            _writable.writeInt(ungroupedPartitions.size());
            for (StopReplicaPartitionV0 ungroupedPartitionsElement : ungroupedPartitions) {
                ungroupedPartitionsElement.write(_writable, _cache, _version);
            }
        } else {
            if (!ungroupedPartitions.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default ungroupedPartitions at version " + _version);
            }
        }
        if (_version >= 1) {
            if (_version >= 2) {
                _writable.writeUnsignedVarint(topics.size() + 1);
                for (StopReplicaTopic topicsElement : topics) {
                    topicsElement.write(_writable, _cache, _version);
                }
            } else {
                _writable.writeInt(topics.size());
                for (StopReplicaTopic topicsElement : topics) {
                    topicsElement.write(_writable, _cache, _version);
                }
            }
        } else {
            if (!topics.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default topics at version " + _version);
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
        this.controllerId = struct.getInt("controller_id");
        this.controllerEpoch = struct.getInt("controller_epoch");
        if (_version >= 1) {
            this.brokerEpoch = struct.getLong("broker_epoch");
        } else {
            this.brokerEpoch = -1L;
        }
        this.deletePartitions = struct.getBoolean("delete_partitions");
        if (_version <= 0) {
            Object[] _nestedObjects = struct.getArray("ungrouped_partitions");
            this.ungroupedPartitions = new ArrayList<StopReplicaPartitionV0>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.ungroupedPartitions.add(new StopReplicaPartitionV0((Struct) nestedObject, _version));
            }
        } else {
            this.ungroupedPartitions = new ArrayList<StopReplicaPartitionV0>();
        }
        if (_version >= 1) {
            Object[] _nestedObjects = struct.getArray("topics");
            this.topics = new ArrayList<StopReplicaTopic>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.topics.add(new StopReplicaTopic((Struct) nestedObject, _version));
            }
        } else {
            this.topics = new ArrayList<StopReplicaTopic>();
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
        struct.set("controller_id", this.controllerId);
        struct.set("controller_epoch", this.controllerEpoch);
        if (_version >= 1) {
            struct.set("broker_epoch", this.brokerEpoch);
        }
        struct.set("delete_partitions", this.deletePartitions);
        if (_version <= 0) {
            Struct[] _nestedObjects = new Struct[ungroupedPartitions.size()];
            int i = 0;
            for (StopReplicaPartitionV0 element : this.ungroupedPartitions) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("ungrouped_partitions", (Object[]) _nestedObjects);
        } else {
            if (!ungroupedPartitions.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default ungroupedPartitions at version " + _version);
            }
        }
        if (_version >= 1) {
            Struct[] _nestedObjects = new Struct[topics.size()];
            int i = 0;
            for (StopReplicaTopic element : this.topics) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("topics", (Object[]) _nestedObjects);
        } else {
            if (!topics.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default topics at version " + _version);
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
        _size += 4;
        _size += 4;
        if (_version >= 1) {
            _size += 8;
        }
        _size += 1;
        if (_version <= 0) {
            {
                int _arraySize = 0;
                _arraySize += 4;
                for (StopReplicaPartitionV0 ungroupedPartitionsElement : ungroupedPartitions) {
                    _arraySize += ungroupedPartitionsElement.size(_cache, _version);
                }
                _size += _arraySize;
            }
        }
        if (_version >= 1) {
            {
                int _arraySize = 0;
                if (_version >= 2) {
                    _arraySize += ByteUtils.sizeOfUnsignedVarint(topics.size() + 1);
                } else {
                    _arraySize += 4;
                }
                for (StopReplicaTopic topicsElement : topics) {
                    _arraySize += topicsElement.size(_cache, _version);
                }
                _size += _arraySize;
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
        if (!(obj instanceof StopReplicaRequestData)) return false;
        StopReplicaRequestData other = (StopReplicaRequestData) obj;
        if (controllerId != other.controllerId) return false;
        if (controllerEpoch != other.controllerEpoch) return false;
        if (brokerEpoch != other.brokerEpoch) return false;
        if (deletePartitions != other.deletePartitions) return false;
        if (this.ungroupedPartitions == null) {
            if (other.ungroupedPartitions != null) return false;
        } else {
            if (!this.ungroupedPartitions.equals(other.ungroupedPartitions)) return false;
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
        hashCode = 31 * hashCode + controllerId;
        hashCode = 31 * hashCode + controllerEpoch;
        hashCode = 31 * hashCode + ((int) (brokerEpoch >> 32) ^ (int) brokerEpoch);
        hashCode = 31 * hashCode + (deletePartitions ? 1231 : 1237);
        hashCode = 31 * hashCode + (ungroupedPartitions == null ? 0 : ungroupedPartitions.hashCode());
        hashCode = 31 * hashCode + (topics == null ? 0 : topics.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "StopReplicaRequestData("
            + "controllerId=" + controllerId
            + ", controllerEpoch=" + controllerEpoch
            + ", brokerEpoch=" + brokerEpoch
            + ", deletePartitions=" + (deletePartitions ? "true" : "false")
            + ", ungroupedPartitions=" + MessageUtil.deepToString(ungroupedPartitions.iterator())
            + ", topics=" + MessageUtil.deepToString(topics.iterator())
            + ")";
    }
    
    public int controllerId() {
        return this.controllerId;
    }
    
    public int controllerEpoch() {
        return this.controllerEpoch;
    }
    
    public long brokerEpoch() {
        return this.brokerEpoch;
    }
    
    public boolean deletePartitions() {
        return this.deletePartitions;
    }
    
    public List<StopReplicaPartitionV0> ungroupedPartitions() {
        return this.ungroupedPartitions;
    }
    
    public List<StopReplicaTopic> topics() {
        return this.topics;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public StopReplicaRequestData setControllerId(int v) {
        this.controllerId = v;
        return this;
    }
    
    public StopReplicaRequestData setControllerEpoch(int v) {
        this.controllerEpoch = v;
        return this;
    }
    
    public StopReplicaRequestData setBrokerEpoch(long v) {
        this.brokerEpoch = v;
        return this;
    }
    
    public StopReplicaRequestData setDeletePartitions(boolean v) {
        this.deletePartitions = v;
        return this;
    }
    
    public StopReplicaRequestData setUngroupedPartitions(List<StopReplicaPartitionV0> v) {
        this.ungroupedPartitions = v;
        return this;
    }
    
    public StopReplicaRequestData setTopics(List<StopReplicaTopic> v) {
        this.topics = v;
        return this;
    }
    
    static public class StopReplicaPartitionV0 implements Message {
        private String topicName;
        private int partitionIndex;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("topic_name", Type.STRING, "The topic name."),
                new Field("partition_index", Type.INT32, "The partition index.")
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0
        };
        
        public StopReplicaPartitionV0(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public StopReplicaPartitionV0(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public StopReplicaPartitionV0() {
            this.topicName = "";
            this.partitionIndex = 0;
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 2;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            {
                int length;
                length = _readable.readShort();
                if (length < 0) {
                    throw new RuntimeException("non-nullable field topicName was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field topicName had invalid length " + length);
                } else {
                    this.topicName = _readable.readString(length);
                }
            }
            this.partitionIndex = _readable.readInt();
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(topicName);
                _writable.writeShort((short) _stringBytes.length);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeInt(partitionIndex);
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
            this.topicName = struct.getString("topic_name");
            this.partitionIndex = struct.getInt("partition_index");
        }
        
        @Override
        public Struct toStruct(short _version) {
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("topic_name", this.topicName);
            struct.set("partition_index", this.partitionIndex);
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            {
                byte[] _stringBytes = topicName.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'topicName' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(topicName, _stringBytes);
                _size += _stringBytes.length + 2;
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
            if (!(obj instanceof StopReplicaPartitionV0)) return false;
            StopReplicaPartitionV0 other = (StopReplicaPartitionV0) obj;
            if (this.topicName == null) {
                if (other.topicName != null) return false;
            } else {
                if (!this.topicName.equals(other.topicName)) return false;
            }
            if (partitionIndex != other.partitionIndex) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (topicName == null ? 0 : topicName.hashCode());
            hashCode = 31 * hashCode + partitionIndex;
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "StopReplicaPartitionV0("
                + "topicName=" + ((topicName == null) ? "null" : "'" + topicName.toString() + "'")
                + ", partitionIndex=" + partitionIndex
                + ")";
        }
        
        public String topicName() {
            return this.topicName;
        }
        
        public int partitionIndex() {
            return this.partitionIndex;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public StopReplicaPartitionV0 setTopicName(String v) {
            this.topicName = v;
            return this;
        }
        
        public StopReplicaPartitionV0 setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
    }
    
    static public class StopReplicaTopic implements Message {
        private String name;
        private List<Integer> partitionIndexes;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_1 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partition_indexes", new ArrayOf(Type.INT32), "The partition indexes.")
            );
        
        public static final Schema SCHEMA_2 =
            new Schema(
                new Field("name", Type.COMPACT_STRING, "The topic name."),
                new Field("partition_indexes", new CompactArrayOf(Type.INT32), "The partition indexes."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            null,
            SCHEMA_1,
            SCHEMA_2
        };
        
        public StopReplicaTopic(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public StopReplicaTopic(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public StopReplicaTopic() {
            this.name = "";
            this.partitionIndexes = new ArrayList<Integer>();
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 2;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of StopReplicaTopic");
            }
            {
                int length;
                if (_version >= 2) {
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
                int arrayLength;
                if (_version >= 2) {
                    arrayLength = _readable.readUnsignedVarint() - 1;
                } else {
                    arrayLength = _readable.readInt();
                }
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field partitionIndexes was serialized as null");
                } else {
                    ArrayList<Integer> newCollection = new ArrayList<Integer>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(_readable.readInt());
                    }
                    this.partitionIndexes = newCollection;
                }
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
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of StopReplicaTopic");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            if (_version >= 2) {
                _writable.writeUnsignedVarint(partitionIndexes.size() + 1);
            } else {
                _writable.writeInt(partitionIndexes.size());
            }
            for (Integer partitionIndexesElement : partitionIndexes) {
                _writable.writeInt(partitionIndexesElement);
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
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of StopReplicaTopic");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 2) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.name = struct.getString("name");
            {
                Object[] _nestedObjects = struct.getArray("partition_indexes");
                this.partitionIndexes = new ArrayList<Integer>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.partitionIndexes.add((Integer) nestedObject);
                }
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
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of StopReplicaTopic");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 2) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("name", this.name);
            {
                Integer[] _nestedObjects = new Integer[partitionIndexes.size()];
                int i = 0;
                for (Integer element : this.partitionIndexes) {
                    _nestedObjects[i++] = element;
                }
                struct.set("partition_indexes", (Object[]) _nestedObjects);
            }
            if (_version >= 2) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of StopReplicaTopic");
            }
            {
                byte[] _stringBytes = name.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'name' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(name, _stringBytes);
                if (_version >= 2) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
            }
            {
                int _arraySize = 0;
                if (_version >= 2) {
                    _arraySize += ByteUtils.sizeOfUnsignedVarint(partitionIndexes.size() + 1);
                } else {
                    _arraySize += 4;
                }
                _arraySize += partitionIndexes.size() * 4;
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
            if (!(obj instanceof StopReplicaTopic)) return false;
            StopReplicaTopic other = (StopReplicaTopic) obj;
            if (this.name == null) {
                if (other.name != null) return false;
            } else {
                if (!this.name.equals(other.name)) return false;
            }
            if (this.partitionIndexes == null) {
                if (other.partitionIndexes != null) return false;
            } else {
                if (!this.partitionIndexes.equals(other.partitionIndexes)) return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (name == null ? 0 : name.hashCode());
            hashCode = 31 * hashCode + (partitionIndexes == null ? 0 : partitionIndexes.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "StopReplicaTopic("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", partitionIndexes=" + MessageUtil.deepToString(partitionIndexes.iterator())
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public List<Integer> partitionIndexes() {
            return this.partitionIndexes;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public StopReplicaTopic setName(String v) {
            this.name = v;
            return this;
        }
        
        public StopReplicaTopic setPartitionIndexes(List<Integer> v) {
            this.partitionIndexes = v;
            return this;
        }
    }
}
