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


public class UpdateMetadataRequestData implements ApiMessage {
    private int controllerId;
    private int controllerEpoch;
    private long brokerEpoch;
    private List<UpdateMetadataPartitionState> ungroupedPartitionStates;
    private List<UpdateMetadataTopicState> topicStates;
    private List<UpdateMetadataBroker> liveBrokers;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("controller_id", Type.INT32, "The controller id."),
            new Field("controller_epoch", Type.INT32, "The controller epoch."),
            new Field("ungrouped_partition_states", new ArrayOf(UpdateMetadataPartitionState.SCHEMA_0), "In older versions of this RPC, each partition that we would like to update."),
            new Field("live_brokers", new ArrayOf(UpdateMetadataBroker.SCHEMA_0), "")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("controller_id", Type.INT32, "The controller id."),
            new Field("controller_epoch", Type.INT32, "The controller epoch."),
            new Field("ungrouped_partition_states", new ArrayOf(UpdateMetadataPartitionState.SCHEMA_0), "In older versions of this RPC, each partition that we would like to update."),
            new Field("live_brokers", new ArrayOf(UpdateMetadataBroker.SCHEMA_1), "")
        );
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("controller_id", Type.INT32, "The controller id."),
            new Field("controller_epoch", Type.INT32, "The controller epoch."),
            new Field("ungrouped_partition_states", new ArrayOf(UpdateMetadataPartitionState.SCHEMA_0), "In older versions of this RPC, each partition that we would like to update."),
            new Field("live_brokers", new ArrayOf(UpdateMetadataBroker.SCHEMA_2), "")
        );
    
    public static final Schema SCHEMA_3 =
        new Schema(
            new Field("controller_id", Type.INT32, "The controller id."),
            new Field("controller_epoch", Type.INT32, "The controller epoch."),
            new Field("ungrouped_partition_states", new ArrayOf(UpdateMetadataPartitionState.SCHEMA_0), "In older versions of this RPC, each partition that we would like to update."),
            new Field("live_brokers", new ArrayOf(UpdateMetadataBroker.SCHEMA_3), "")
        );
    
    public static final Schema SCHEMA_4 =
        new Schema(
            new Field("controller_id", Type.INT32, "The controller id."),
            new Field("controller_epoch", Type.INT32, "The controller epoch."),
            new Field("ungrouped_partition_states", new ArrayOf(UpdateMetadataPartitionState.SCHEMA_4), "In older versions of this RPC, each partition that we would like to update."),
            new Field("live_brokers", new ArrayOf(UpdateMetadataBroker.SCHEMA_3), "")
        );
    
    public static final Schema SCHEMA_5 =
        new Schema(
            new Field("controller_id", Type.INT32, "The controller id."),
            new Field("controller_epoch", Type.INT32, "The controller epoch."),
            new Field("broker_epoch", Type.INT64, "The broker epoch."),
            new Field("topic_states", new ArrayOf(UpdateMetadataTopicState.SCHEMA_5), "In newer versions of this RPC, each topic that we would like to update."),
            new Field("live_brokers", new ArrayOf(UpdateMetadataBroker.SCHEMA_3), "")
        );
    
    public static final Schema SCHEMA_6 =
        new Schema(
            new Field("controller_id", Type.INT32, "The controller id."),
            new Field("controller_epoch", Type.INT32, "The controller epoch."),
            new Field("broker_epoch", Type.INT64, "The broker epoch."),
            new Field("topic_states", new CompactArrayOf(UpdateMetadataTopicState.SCHEMA_6), "In newer versions of this RPC, each topic that we would like to update."),
            new Field("live_brokers", new CompactArrayOf(UpdateMetadataBroker.SCHEMA_6), ""),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2,
        SCHEMA_3,
        SCHEMA_4,
        SCHEMA_5,
        SCHEMA_6
    };
    
    public UpdateMetadataRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public UpdateMetadataRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public UpdateMetadataRequestData() {
        this.controllerId = 0;
        this.controllerEpoch = 0;
        this.brokerEpoch = -1L;
        this.ungroupedPartitionStates = new ArrayList<UpdateMetadataPartitionState>();
        this.topicStates = new ArrayList<UpdateMetadataTopicState>();
        this.liveBrokers = new ArrayList<UpdateMetadataBroker>();
    }
    
    @Override
    public short apiKey() {
        return 6;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 6;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        this.controllerId = _readable.readInt();
        this.controllerEpoch = _readable.readInt();
        if (_version >= 5) {
            this.brokerEpoch = _readable.readLong();
        } else {
            this.brokerEpoch = -1L;
        }
        if (_version <= 4) {
            int arrayLength;
            arrayLength = _readable.readInt();
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field ungroupedPartitionStates was serialized as null");
            } else {
                ArrayList<UpdateMetadataPartitionState> newCollection = new ArrayList<UpdateMetadataPartitionState>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new UpdateMetadataPartitionState(_readable, _version));
                }
                this.ungroupedPartitionStates = newCollection;
            }
        } else {
            this.ungroupedPartitionStates = new ArrayList<UpdateMetadataPartitionState>();
        }
        if (_version >= 5) {
            if (_version >= 6) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topicStates was serialized as null");
                } else {
                    ArrayList<UpdateMetadataTopicState> newCollection = new ArrayList<UpdateMetadataTopicState>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new UpdateMetadataTopicState(_readable, _version));
                    }
                    this.topicStates = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topicStates was serialized as null");
                } else {
                    ArrayList<UpdateMetadataTopicState> newCollection = new ArrayList<UpdateMetadataTopicState>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new UpdateMetadataTopicState(_readable, _version));
                    }
                    this.topicStates = newCollection;
                }
            }
        } else {
            this.topicStates = new ArrayList<UpdateMetadataTopicState>();
        }
        {
            if (_version >= 6) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field liveBrokers was serialized as null");
                } else {
                    ArrayList<UpdateMetadataBroker> newCollection = new ArrayList<UpdateMetadataBroker>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new UpdateMetadataBroker(_readable, _version));
                    }
                    this.liveBrokers = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field liveBrokers was serialized as null");
                } else {
                    ArrayList<UpdateMetadataBroker> newCollection = new ArrayList<UpdateMetadataBroker>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new UpdateMetadataBroker(_readable, _version));
                    }
                    this.liveBrokers = newCollection;
                }
            }
        }
        this._unknownTaggedFields = null;
        if (_version >= 6) {
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
        if (_version >= 5) {
            _writable.writeLong(brokerEpoch);
        }
        if (_version <= 4) {
            _writable.writeInt(ungroupedPartitionStates.size());
            for (UpdateMetadataPartitionState ungroupedPartitionStatesElement : ungroupedPartitionStates) {
                ungroupedPartitionStatesElement.write(_writable, _cache, _version);
            }
        } else {
            if (!ungroupedPartitionStates.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default ungroupedPartitionStates at version " + _version);
            }
        }
        if (_version >= 5) {
            if (_version >= 6) {
                _writable.writeUnsignedVarint(topicStates.size() + 1);
                for (UpdateMetadataTopicState topicStatesElement : topicStates) {
                    topicStatesElement.write(_writable, _cache, _version);
                }
            } else {
                _writable.writeInt(topicStates.size());
                for (UpdateMetadataTopicState topicStatesElement : topicStates) {
                    topicStatesElement.write(_writable, _cache, _version);
                }
            }
        } else {
            if (!topicStates.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default topicStates at version " + _version);
            }
        }
        if (_version >= 6) {
            _writable.writeUnsignedVarint(liveBrokers.size() + 1);
            for (UpdateMetadataBroker liveBrokersElement : liveBrokers) {
                liveBrokersElement.write(_writable, _cache, _version);
            }
        } else {
            _writable.writeInt(liveBrokers.size());
            for (UpdateMetadataBroker liveBrokersElement : liveBrokers) {
                liveBrokersElement.write(_writable, _cache, _version);
            }
        }
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        if (_version >= 6) {
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
        if (_version >= 6) {
            _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
        }
        this.controllerId = struct.getInt("controller_id");
        this.controllerEpoch = struct.getInt("controller_epoch");
        if (_version >= 5) {
            this.brokerEpoch = struct.getLong("broker_epoch");
        } else {
            this.brokerEpoch = -1L;
        }
        if (_version <= 4) {
            Object[] _nestedObjects = struct.getArray("ungrouped_partition_states");
            this.ungroupedPartitionStates = new ArrayList<UpdateMetadataPartitionState>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.ungroupedPartitionStates.add(new UpdateMetadataPartitionState((Struct) nestedObject, _version));
            }
        } else {
            this.ungroupedPartitionStates = new ArrayList<UpdateMetadataPartitionState>();
        }
        if (_version >= 5) {
            Object[] _nestedObjects = struct.getArray("topic_states");
            this.topicStates = new ArrayList<UpdateMetadataTopicState>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.topicStates.add(new UpdateMetadataTopicState((Struct) nestedObject, _version));
            }
        } else {
            this.topicStates = new ArrayList<UpdateMetadataTopicState>();
        }
        {
            Object[] _nestedObjects = struct.getArray("live_brokers");
            this.liveBrokers = new ArrayList<UpdateMetadataBroker>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.liveBrokers.add(new UpdateMetadataBroker((Struct) nestedObject, _version));
            }
        }
        if (_version >= 6) {
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
        if (_version >= 6) {
            _taggedFields = new TreeMap<>();
        }
        Struct struct = new Struct(SCHEMAS[_version]);
        struct.set("controller_id", this.controllerId);
        struct.set("controller_epoch", this.controllerEpoch);
        if (_version >= 5) {
            struct.set("broker_epoch", this.brokerEpoch);
        }
        if (_version <= 4) {
            Struct[] _nestedObjects = new Struct[ungroupedPartitionStates.size()];
            int i = 0;
            for (UpdateMetadataPartitionState element : this.ungroupedPartitionStates) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("ungrouped_partition_states", (Object[]) _nestedObjects);
        } else {
            if (!ungroupedPartitionStates.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default ungroupedPartitionStates at version " + _version);
            }
        }
        if (_version >= 5) {
            Struct[] _nestedObjects = new Struct[topicStates.size()];
            int i = 0;
            for (UpdateMetadataTopicState element : this.topicStates) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("topic_states", (Object[]) _nestedObjects);
        } else {
            if (!topicStates.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default topicStates at version " + _version);
            }
        }
        {
            Struct[] _nestedObjects = new Struct[liveBrokers.size()];
            int i = 0;
            for (UpdateMetadataBroker element : this.liveBrokers) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("live_brokers", (Object[]) _nestedObjects);
        }
        if (_version >= 6) {
            struct.set("_tagged_fields", _taggedFields);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        _size += 4;
        _size += 4;
        if (_version >= 5) {
            _size += 8;
        }
        if (_version <= 4) {
            {
                int _arraySize = 0;
                _arraySize += 4;
                for (UpdateMetadataPartitionState ungroupedPartitionStatesElement : ungroupedPartitionStates) {
                    _arraySize += ungroupedPartitionStatesElement.size(_cache, _version);
                }
                _size += _arraySize;
            }
        }
        if (_version >= 5) {
            {
                int _arraySize = 0;
                if (_version >= 6) {
                    _arraySize += ByteUtils.sizeOfUnsignedVarint(topicStates.size() + 1);
                } else {
                    _arraySize += 4;
                }
                for (UpdateMetadataTopicState topicStatesElement : topicStates) {
                    _arraySize += topicStatesElement.size(_cache, _version);
                }
                _size += _arraySize;
            }
        }
        {
            int _arraySize = 0;
            if (_version >= 6) {
                _arraySize += ByteUtils.sizeOfUnsignedVarint(liveBrokers.size() + 1);
            } else {
                _arraySize += 4;
            }
            for (UpdateMetadataBroker liveBrokersElement : liveBrokers) {
                _arraySize += liveBrokersElement.size(_cache, _version);
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
        if (_version >= 6) {
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
        if (!(obj instanceof UpdateMetadataRequestData)) return false;
        UpdateMetadataRequestData other = (UpdateMetadataRequestData) obj;
        if (controllerId != other.controllerId) return false;
        if (controllerEpoch != other.controllerEpoch) return false;
        if (brokerEpoch != other.brokerEpoch) return false;
        if (this.ungroupedPartitionStates == null) {
            if (other.ungroupedPartitionStates != null) return false;
        } else {
            if (!this.ungroupedPartitionStates.equals(other.ungroupedPartitionStates)) return false;
        }
        if (this.topicStates == null) {
            if (other.topicStates != null) return false;
        } else {
            if (!this.topicStates.equals(other.topicStates)) return false;
        }
        if (this.liveBrokers == null) {
            if (other.liveBrokers != null) return false;
        } else {
            if (!this.liveBrokers.equals(other.liveBrokers)) return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + controllerId;
        hashCode = 31 * hashCode + controllerEpoch;
        hashCode = 31 * hashCode + ((int) (brokerEpoch >> 32) ^ (int) brokerEpoch);
        hashCode = 31 * hashCode + (ungroupedPartitionStates == null ? 0 : ungroupedPartitionStates.hashCode());
        hashCode = 31 * hashCode + (topicStates == null ? 0 : topicStates.hashCode());
        hashCode = 31 * hashCode + (liveBrokers == null ? 0 : liveBrokers.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "UpdateMetadataRequestData("
            + "controllerId=" + controllerId
            + ", controllerEpoch=" + controllerEpoch
            + ", brokerEpoch=" + brokerEpoch
            + ", ungroupedPartitionStates=" + MessageUtil.deepToString(ungroupedPartitionStates.iterator())
            + ", topicStates=" + MessageUtil.deepToString(topicStates.iterator())
            + ", liveBrokers=" + MessageUtil.deepToString(liveBrokers.iterator())
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
    
    public List<UpdateMetadataPartitionState> ungroupedPartitionStates() {
        return this.ungroupedPartitionStates;
    }
    
    public List<UpdateMetadataTopicState> topicStates() {
        return this.topicStates;
    }
    
    public List<UpdateMetadataBroker> liveBrokers() {
        return this.liveBrokers;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public UpdateMetadataRequestData setControllerId(int v) {
        this.controllerId = v;
        return this;
    }
    
    public UpdateMetadataRequestData setControllerEpoch(int v) {
        this.controllerEpoch = v;
        return this;
    }
    
    public UpdateMetadataRequestData setBrokerEpoch(long v) {
        this.brokerEpoch = v;
        return this;
    }
    
    public UpdateMetadataRequestData setUngroupedPartitionStates(List<UpdateMetadataPartitionState> v) {
        this.ungroupedPartitionStates = v;
        return this;
    }
    
    public UpdateMetadataRequestData setTopicStates(List<UpdateMetadataTopicState> v) {
        this.topicStates = v;
        return this;
    }
    
    public UpdateMetadataRequestData setLiveBrokers(List<UpdateMetadataBroker> v) {
        this.liveBrokers = v;
        return this;
    }
    
    static public class UpdateMetadataTopicState implements Message {
        private String topicName;
        private List<UpdateMetadataPartitionState> partitionStates;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_5 =
            new Schema(
                new Field("topic_name", Type.STRING, "The topic name."),
                new Field("partition_states", new ArrayOf(UpdateMetadataPartitionState.SCHEMA_5), "The partition that we would like to update.")
            );
        
        public static final Schema SCHEMA_6 =
            new Schema(
                new Field("topic_name", Type.COMPACT_STRING, "The topic name."),
                new Field("partition_states", new CompactArrayOf(UpdateMetadataPartitionState.SCHEMA_6), "The partition that we would like to update."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            null,
            null,
            null,
            null,
            null,
            SCHEMA_5,
            SCHEMA_6
        };
        
        public UpdateMetadataTopicState(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public UpdateMetadataTopicState(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public UpdateMetadataTopicState() {
            this.topicName = "";
            this.partitionStates = new ArrayList<UpdateMetadataPartitionState>();
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 6;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 6) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of UpdateMetadataTopicState");
            }
            {
                int length;
                if (_version >= 6) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field topicName was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field topicName had invalid length " + length);
                } else {
                    this.topicName = _readable.readString(length);
                }
            }
            {
                if (_version >= 6) {
                    int arrayLength;
                    arrayLength = _readable.readUnsignedVarint() - 1;
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field partitionStates was serialized as null");
                    } else {
                        ArrayList<UpdateMetadataPartitionState> newCollection = new ArrayList<UpdateMetadataPartitionState>(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new UpdateMetadataPartitionState(_readable, _version));
                        }
                        this.partitionStates = newCollection;
                    }
                } else {
                    int arrayLength;
                    arrayLength = _readable.readInt();
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field partitionStates was serialized as null");
                    } else {
                        ArrayList<UpdateMetadataPartitionState> newCollection = new ArrayList<UpdateMetadataPartitionState>(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new UpdateMetadataPartitionState(_readable, _version));
                        }
                        this.partitionStates = newCollection;
                    }
                }
            }
            this._unknownTaggedFields = null;
            if (_version >= 6) {
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
            if (_version > 6) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of UpdateMetadataTopicState");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(topicName);
                if (_version >= 6) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            if (_version >= 6) {
                _writable.writeUnsignedVarint(partitionStates.size() + 1);
                for (UpdateMetadataPartitionState partitionStatesElement : partitionStates) {
                    partitionStatesElement.write(_writable, _cache, _version);
                }
            } else {
                _writable.writeInt(partitionStates.size());
                for (UpdateMetadataPartitionState partitionStatesElement : partitionStates) {
                    partitionStatesElement.write(_writable, _cache, _version);
                }
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 6) {
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
            if (_version > 6) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of UpdateMetadataTopicState");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 6) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.topicName = struct.getString("topic_name");
            {
                Object[] _nestedObjects = struct.getArray("partition_states");
                this.partitionStates = new ArrayList<UpdateMetadataPartitionState>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.partitionStates.add(new UpdateMetadataPartitionState((Struct) nestedObject, _version));
                }
            }
            if (_version >= 6) {
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
            if (_version > 6) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of UpdateMetadataTopicState");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 6) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("topic_name", this.topicName);
            {
                Struct[] _nestedObjects = new Struct[partitionStates.size()];
                int i = 0;
                for (UpdateMetadataPartitionState element : this.partitionStates) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("partition_states", (Object[]) _nestedObjects);
            }
            if (_version >= 6) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 6) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of UpdateMetadataTopicState");
            }
            {
                byte[] _stringBytes = topicName.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'topicName' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(topicName, _stringBytes);
                if (_version >= 6) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
            }
            {
                int _arraySize = 0;
                if (_version >= 6) {
                    _arraySize += ByteUtils.sizeOfUnsignedVarint(partitionStates.size() + 1);
                } else {
                    _arraySize += 4;
                }
                for (UpdateMetadataPartitionState partitionStatesElement : partitionStates) {
                    _arraySize += partitionStatesElement.size(_cache, _version);
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
            if (_version >= 6) {
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
            if (!(obj instanceof UpdateMetadataTopicState)) return false;
            UpdateMetadataTopicState other = (UpdateMetadataTopicState) obj;
            if (this.topicName == null) {
                if (other.topicName != null) return false;
            } else {
                if (!this.topicName.equals(other.topicName)) return false;
            }
            if (this.partitionStates == null) {
                if (other.partitionStates != null) return false;
            } else {
                if (!this.partitionStates.equals(other.partitionStates)) return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (topicName == null ? 0 : topicName.hashCode());
            hashCode = 31 * hashCode + (partitionStates == null ? 0 : partitionStates.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "UpdateMetadataTopicState("
                + "topicName=" + ((topicName == null) ? "null" : "'" + topicName.toString() + "'")
                + ", partitionStates=" + MessageUtil.deepToString(partitionStates.iterator())
                + ")";
        }
        
        public String topicName() {
            return this.topicName;
        }
        
        public List<UpdateMetadataPartitionState> partitionStates() {
            return this.partitionStates;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public UpdateMetadataTopicState setTopicName(String v) {
            this.topicName = v;
            return this;
        }
        
        public UpdateMetadataTopicState setPartitionStates(List<UpdateMetadataPartitionState> v) {
            this.partitionStates = v;
            return this;
        }
    }
    
    static public class UpdateMetadataBroker implements Message {
        private int id;
        private String v0Host;
        private int v0Port;
        private List<UpdateMetadataEndpoint> endpoints;
        private String rack;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("id", Type.INT32, "The broker id."),
                new Field("v0_host", Type.STRING, "The broker hostname."),
                new Field("v0_port", Type.INT32, "The broker port.")
            );
        
        public static final Schema SCHEMA_1 =
            new Schema(
                new Field("id", Type.INT32, "The broker id."),
                new Field("endpoints", new ArrayOf(UpdateMetadataEndpoint.SCHEMA_1), "The broker endpoints.")
            );
        
        public static final Schema SCHEMA_2 =
            new Schema(
                new Field("id", Type.INT32, "The broker id."),
                new Field("endpoints", new ArrayOf(UpdateMetadataEndpoint.SCHEMA_1), "The broker endpoints."),
                new Field("rack", Type.NULLABLE_STRING, "The rack which this broker belongs to.")
            );
        
        public static final Schema SCHEMA_3 =
            new Schema(
                new Field("id", Type.INT32, "The broker id."),
                new Field("endpoints", new ArrayOf(UpdateMetadataEndpoint.SCHEMA_3), "The broker endpoints."),
                new Field("rack", Type.NULLABLE_STRING, "The rack which this broker belongs to.")
            );
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 = SCHEMA_4;
        
        public static final Schema SCHEMA_6 =
            new Schema(
                new Field("id", Type.INT32, "The broker id."),
                new Field("endpoints", new CompactArrayOf(UpdateMetadataEndpoint.SCHEMA_6), "The broker endpoints."),
                new Field("rack", Type.COMPACT_NULLABLE_STRING, "The rack which this broker belongs to."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3,
            SCHEMA_4,
            SCHEMA_5,
            SCHEMA_6
        };
        
        public UpdateMetadataBroker(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public UpdateMetadataBroker(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public UpdateMetadataBroker() {
            this.id = 0;
            this.v0Host = "";
            this.v0Port = 0;
            this.endpoints = new ArrayList<UpdateMetadataEndpoint>();
            this.rack = "";
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 6;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 6) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of UpdateMetadataBroker");
            }
            this.id = _readable.readInt();
            if (_version <= 0) {
                int length;
                length = _readable.readShort();
                if (length < 0) {
                    throw new RuntimeException("non-nullable field v0Host was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field v0Host had invalid length " + length);
                } else {
                    this.v0Host = _readable.readString(length);
                }
            } else {
                this.v0Host = "";
            }
            if (_version <= 0) {
                this.v0Port = _readable.readInt();
            } else {
                this.v0Port = 0;
            }
            if (_version >= 1) {
                if (_version >= 6) {
                    int arrayLength;
                    arrayLength = _readable.readUnsignedVarint() - 1;
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field endpoints was serialized as null");
                    } else {
                        ArrayList<UpdateMetadataEndpoint> newCollection = new ArrayList<UpdateMetadataEndpoint>(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new UpdateMetadataEndpoint(_readable, _version));
                        }
                        this.endpoints = newCollection;
                    }
                } else {
                    int arrayLength;
                    arrayLength = _readable.readInt();
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field endpoints was serialized as null");
                    } else {
                        ArrayList<UpdateMetadataEndpoint> newCollection = new ArrayList<UpdateMetadataEndpoint>(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new UpdateMetadataEndpoint(_readable, _version));
                        }
                        this.endpoints = newCollection;
                    }
                }
            } else {
                this.endpoints = new ArrayList<UpdateMetadataEndpoint>();
            }
            if (_version >= 2) {
                int length;
                if (_version >= 6) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    this.rack = null;
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field rack had invalid length " + length);
                } else {
                    this.rack = _readable.readString(length);
                }
            } else {
                this.rack = "";
            }
            this._unknownTaggedFields = null;
            if (_version >= 6) {
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
            if (_version > 6) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of UpdateMetadataBroker");
            }
            int _numTaggedFields = 0;
            _writable.writeInt(id);
            if (_version <= 0) {
                {
                    byte[] _stringBytes = _cache.getSerializedValue(v0Host);
                    _writable.writeShort((short) _stringBytes.length);
                    _writable.writeByteArray(_stringBytes);
                }
            }
            if (_version <= 0) {
                _writable.writeInt(v0Port);
            }
            if (_version >= 1) {
                if (_version >= 6) {
                    _writable.writeUnsignedVarint(endpoints.size() + 1);
                    for (UpdateMetadataEndpoint endpointsElement : endpoints) {
                        endpointsElement.write(_writable, _cache, _version);
                    }
                } else {
                    _writable.writeInt(endpoints.size());
                    for (UpdateMetadataEndpoint endpointsElement : endpoints) {
                        endpointsElement.write(_writable, _cache, _version);
                    }
                }
            }
            if (_version >= 2) {
                if (rack == null) {
                    if (_version >= 6) {
                        _writable.writeUnsignedVarint(0);
                    } else {
                        _writable.writeShort((short) -1);
                    }
                } else {
                    byte[] _stringBytes = _cache.getSerializedValue(rack);
                    if (_version >= 6) {
                        _writable.writeUnsignedVarint(_stringBytes.length + 1);
                    } else {
                        _writable.writeShort((short) _stringBytes.length);
                    }
                    _writable.writeByteArray(_stringBytes);
                }
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 6) {
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
            if (_version > 6) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of UpdateMetadataBroker");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 6) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.id = struct.getInt("id");
            if (_version <= 0) {
                this.v0Host = struct.getString("v0_host");
            } else {
                this.v0Host = "";
            }
            if (_version <= 0) {
                this.v0Port = struct.getInt("v0_port");
            } else {
                this.v0Port = 0;
            }
            if (_version >= 1) {
                Object[] _nestedObjects = struct.getArray("endpoints");
                this.endpoints = new ArrayList<UpdateMetadataEndpoint>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.endpoints.add(new UpdateMetadataEndpoint((Struct) nestedObject, _version));
                }
            } else {
                this.endpoints = new ArrayList<UpdateMetadataEndpoint>();
            }
            if (_version >= 2) {
                this.rack = struct.getString("rack");
            } else {
                this.rack = "";
            }
            if (_version >= 6) {
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
            if (_version > 6) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of UpdateMetadataBroker");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 6) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("id", this.id);
            if (_version <= 0) {
                struct.set("v0_host", this.v0Host);
            }
            if (_version <= 0) {
                struct.set("v0_port", this.v0Port);
            }
            if (_version >= 1) {
                Struct[] _nestedObjects = new Struct[endpoints.size()];
                int i = 0;
                for (UpdateMetadataEndpoint element : this.endpoints) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("endpoints", (Object[]) _nestedObjects);
            }
            if (_version >= 2) {
                struct.set("rack", this.rack);
            }
            if (_version >= 6) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 6) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of UpdateMetadataBroker");
            }
            _size += 4;
            if (_version <= 0) {
                {
                    byte[] _stringBytes = v0Host.getBytes(StandardCharsets.UTF_8);
                    if (_stringBytes.length > 0x7fff) {
                        throw new RuntimeException("'v0Host' field is too long to be serialized");
                    }
                    _cache.cacheSerializedValue(v0Host, _stringBytes);
                    _size += _stringBytes.length + 2;
                }
            }
            if (_version <= 0) {
                _size += 4;
            }
            if (_version >= 1) {
                {
                    int _arraySize = 0;
                    if (_version >= 6) {
                        _arraySize += ByteUtils.sizeOfUnsignedVarint(endpoints.size() + 1);
                    } else {
                        _arraySize += 4;
                    }
                    for (UpdateMetadataEndpoint endpointsElement : endpoints) {
                        _arraySize += endpointsElement.size(_cache, _version);
                    }
                    _size += _arraySize;
                }
            }
            if (_version >= 2) {
                if (rack == null) {
                    if (_version >= 6) {
                        _size += 1;
                    } else {
                        _size += 2;
                    }
                } else {
                    byte[] _stringBytes = rack.getBytes(StandardCharsets.UTF_8);
                    if (_stringBytes.length > 0x7fff) {
                        throw new RuntimeException("'rack' field is too long to be serialized");
                    }
                    _cache.cacheSerializedValue(rack, _stringBytes);
                    if (_version >= 6) {
                        _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                    } else {
                        _size += _stringBytes.length + 2;
                    }
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
            if (_version >= 6) {
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
            if (!(obj instanceof UpdateMetadataBroker)) return false;
            UpdateMetadataBroker other = (UpdateMetadataBroker) obj;
            if (id != other.id) return false;
            if (this.v0Host == null) {
                if (other.v0Host != null) return false;
            } else {
                if (!this.v0Host.equals(other.v0Host)) return false;
            }
            if (v0Port != other.v0Port) return false;
            if (this.endpoints == null) {
                if (other.endpoints != null) return false;
            } else {
                if (!this.endpoints.equals(other.endpoints)) return false;
            }
            if (this.rack == null) {
                if (other.rack != null) return false;
            } else {
                if (!this.rack.equals(other.rack)) return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + id;
            hashCode = 31 * hashCode + (v0Host == null ? 0 : v0Host.hashCode());
            hashCode = 31 * hashCode + v0Port;
            hashCode = 31 * hashCode + (endpoints == null ? 0 : endpoints.hashCode());
            hashCode = 31 * hashCode + (rack == null ? 0 : rack.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "UpdateMetadataBroker("
                + "id=" + id
                + ", v0Host=" + ((v0Host == null) ? "null" : "'" + v0Host.toString() + "'")
                + ", v0Port=" + v0Port
                + ", endpoints=" + MessageUtil.deepToString(endpoints.iterator())
                + ", rack=" + ((rack == null) ? "null" : "'" + rack.toString() + "'")
                + ")";
        }
        
        public int id() {
            return this.id;
        }
        
        public String v0Host() {
            return this.v0Host;
        }
        
        public int v0Port() {
            return this.v0Port;
        }
        
        public List<UpdateMetadataEndpoint> endpoints() {
            return this.endpoints;
        }
        
        public String rack() {
            return this.rack;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public UpdateMetadataBroker setId(int v) {
            this.id = v;
            return this;
        }
        
        public UpdateMetadataBroker setV0Host(String v) {
            this.v0Host = v;
            return this;
        }
        
        public UpdateMetadataBroker setV0Port(int v) {
            this.v0Port = v;
            return this;
        }
        
        public UpdateMetadataBroker setEndpoints(List<UpdateMetadataEndpoint> v) {
            this.endpoints = v;
            return this;
        }
        
        public UpdateMetadataBroker setRack(String v) {
            this.rack = v;
            return this;
        }
    }
    
    static public class UpdateMetadataEndpoint implements Message {
        private int port;
        private String host;
        private String listener;
        private short securityProtocol;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_1 =
            new Schema(
                new Field("port", Type.INT32, "The port of this endpoint"),
                new Field("host", Type.STRING, "The hostname of this endpoint"),
                new Field("security_protocol", Type.INT16, "The security protocol type.")
            );
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 =
            new Schema(
                new Field("port", Type.INT32, "The port of this endpoint"),
                new Field("host", Type.STRING, "The hostname of this endpoint"),
                new Field("listener", Type.STRING, "The listener name."),
                new Field("security_protocol", Type.INT16, "The security protocol type.")
            );
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 = SCHEMA_4;
        
        public static final Schema SCHEMA_6 =
            new Schema(
                new Field("port", Type.INT32, "The port of this endpoint"),
                new Field("host", Type.COMPACT_STRING, "The hostname of this endpoint"),
                new Field("listener", Type.COMPACT_STRING, "The listener name."),
                new Field("security_protocol", Type.INT16, "The security protocol type."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            null,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3,
            SCHEMA_4,
            SCHEMA_5,
            SCHEMA_6
        };
        
        public UpdateMetadataEndpoint(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public UpdateMetadataEndpoint(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public UpdateMetadataEndpoint() {
            this.port = 0;
            this.host = "";
            this.listener = "";
            this.securityProtocol = (short) 0;
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 6;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 6) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of UpdateMetadataEndpoint");
            }
            this.port = _readable.readInt();
            {
                int length;
                if (_version >= 6) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field host was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field host had invalid length " + length);
                } else {
                    this.host = _readable.readString(length);
                }
            }
            if (_version >= 3) {
                int length;
                if (_version >= 6) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field listener was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field listener had invalid length " + length);
                } else {
                    this.listener = _readable.readString(length);
                }
            } else {
                this.listener = "";
            }
            this.securityProtocol = _readable.readShort();
            this._unknownTaggedFields = null;
            if (_version >= 6) {
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
            if (_version > 6) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of UpdateMetadataEndpoint");
            }
            int _numTaggedFields = 0;
            _writable.writeInt(port);
            {
                byte[] _stringBytes = _cache.getSerializedValue(host);
                if (_version >= 6) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            if (_version >= 3) {
                {
                    byte[] _stringBytes = _cache.getSerializedValue(listener);
                    if (_version >= 6) {
                        _writable.writeUnsignedVarint(_stringBytes.length + 1);
                    } else {
                        _writable.writeShort((short) _stringBytes.length);
                    }
                    _writable.writeByteArray(_stringBytes);
                }
            }
            _writable.writeShort(securityProtocol);
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 6) {
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
            if (_version > 6) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of UpdateMetadataEndpoint");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 6) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.port = struct.getInt("port");
            this.host = struct.getString("host");
            if (_version >= 3) {
                this.listener = struct.getString("listener");
            } else {
                this.listener = "";
            }
            this.securityProtocol = struct.getShort("security_protocol");
            if (_version >= 6) {
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
            if (_version > 6) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of UpdateMetadataEndpoint");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 6) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("port", this.port);
            struct.set("host", this.host);
            if (_version >= 3) {
                struct.set("listener", this.listener);
            }
            struct.set("security_protocol", this.securityProtocol);
            if (_version >= 6) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 6) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of UpdateMetadataEndpoint");
            }
            _size += 4;
            {
                byte[] _stringBytes = host.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'host' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(host, _stringBytes);
                if (_version >= 6) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
            }
            if (_version >= 3) {
                {
                    byte[] _stringBytes = listener.getBytes(StandardCharsets.UTF_8);
                    if (_stringBytes.length > 0x7fff) {
                        throw new RuntimeException("'listener' field is too long to be serialized");
                    }
                    _cache.cacheSerializedValue(listener, _stringBytes);
                    if (_version >= 6) {
                        _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                    } else {
                        _size += _stringBytes.length + 2;
                    }
                }
            }
            _size += 2;
            if (_unknownTaggedFields != null) {
                _numTaggedFields += _unknownTaggedFields.size();
                for (RawTaggedField _field : _unknownTaggedFields) {
                    _size += ByteUtils.sizeOfUnsignedVarint(_field.tag());
                    _size += ByteUtils.sizeOfUnsignedVarint(_field.size());
                    _size += _field.size();
                }
            }
            if (_version >= 6) {
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
            if (!(obj instanceof UpdateMetadataEndpoint)) return false;
            UpdateMetadataEndpoint other = (UpdateMetadataEndpoint) obj;
            if (port != other.port) return false;
            if (this.host == null) {
                if (other.host != null) return false;
            } else {
                if (!this.host.equals(other.host)) return false;
            }
            if (this.listener == null) {
                if (other.listener != null) return false;
            } else {
                if (!this.listener.equals(other.listener)) return false;
            }
            if (securityProtocol != other.securityProtocol) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + port;
            hashCode = 31 * hashCode + (host == null ? 0 : host.hashCode());
            hashCode = 31 * hashCode + (listener == null ? 0 : listener.hashCode());
            hashCode = 31 * hashCode + securityProtocol;
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "UpdateMetadataEndpoint("
                + "port=" + port
                + ", host=" + ((host == null) ? "null" : "'" + host.toString() + "'")
                + ", listener=" + ((listener == null) ? "null" : "'" + listener.toString() + "'")
                + ", securityProtocol=" + securityProtocol
                + ")";
        }
        
        public int port() {
            return this.port;
        }
        
        public String host() {
            return this.host;
        }
        
        public String listener() {
            return this.listener;
        }
        
        public short securityProtocol() {
            return this.securityProtocol;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public UpdateMetadataEndpoint setPort(int v) {
            this.port = v;
            return this;
        }
        
        public UpdateMetadataEndpoint setHost(String v) {
            this.host = v;
            return this;
        }
        
        public UpdateMetadataEndpoint setListener(String v) {
            this.listener = v;
            return this;
        }
        
        public UpdateMetadataEndpoint setSecurityProtocol(short v) {
            this.securityProtocol = v;
            return this;
        }
    }
    
    static public class UpdateMetadataPartitionState implements Message {
        private String topicName;
        private int partitionIndex;
        private int controllerEpoch;
        private int leader;
        private int leaderEpoch;
        private List<Integer> isr;
        private int zkVersion;
        private List<Integer> replicas;
        private List<Integer> offlineReplicas;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("topic_name", Type.STRING, "In older versions of this RPC, the topic name."),
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("controller_epoch", Type.INT32, "The controller epoch."),
                new Field("leader", Type.INT32, "The ID of the broker which is the current partition leader."),
                new Field("leader_epoch", Type.INT32, "The leader epoch of this partition."),
                new Field("isr", new ArrayOf(Type.INT32), "The brokers which are in the ISR for this partition."),
                new Field("zk_version", Type.INT32, "The Zookeeper version."),
                new Field("replicas", new ArrayOf(Type.INT32), "All the replicas of this partition.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 =
            new Schema(
                new Field("topic_name", Type.STRING, "In older versions of this RPC, the topic name."),
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("controller_epoch", Type.INT32, "The controller epoch."),
                new Field("leader", Type.INT32, "The ID of the broker which is the current partition leader."),
                new Field("leader_epoch", Type.INT32, "The leader epoch of this partition."),
                new Field("isr", new ArrayOf(Type.INT32), "The brokers which are in the ISR for this partition."),
                new Field("zk_version", Type.INT32, "The Zookeeper version."),
                new Field("replicas", new ArrayOf(Type.INT32), "All the replicas of this partition."),
                new Field("offline_replicas", new ArrayOf(Type.INT32), "The replicas of this partition which are offline.")
            );
        
        public static final Schema SCHEMA_5 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("controller_epoch", Type.INT32, "The controller epoch."),
                new Field("leader", Type.INT32, "The ID of the broker which is the current partition leader."),
                new Field("leader_epoch", Type.INT32, "The leader epoch of this partition."),
                new Field("isr", new ArrayOf(Type.INT32), "The brokers which are in the ISR for this partition."),
                new Field("zk_version", Type.INT32, "The Zookeeper version."),
                new Field("replicas", new ArrayOf(Type.INT32), "All the replicas of this partition."),
                new Field("offline_replicas", new ArrayOf(Type.INT32), "The replicas of this partition which are offline.")
            );
        
        public static final Schema SCHEMA_6 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("controller_epoch", Type.INT32, "The controller epoch."),
                new Field("leader", Type.INT32, "The ID of the broker which is the current partition leader."),
                new Field("leader_epoch", Type.INT32, "The leader epoch of this partition."),
                new Field("isr", new CompactArrayOf(Type.INT32), "The brokers which are in the ISR for this partition."),
                new Field("zk_version", Type.INT32, "The Zookeeper version."),
                new Field("replicas", new CompactArrayOf(Type.INT32), "All the replicas of this partition."),
                new Field("offline_replicas", new CompactArrayOf(Type.INT32), "The replicas of this partition which are offline."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3,
            SCHEMA_4,
            SCHEMA_5,
            SCHEMA_6
        };
        
        public UpdateMetadataPartitionState(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public UpdateMetadataPartitionState(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public UpdateMetadataPartitionState() {
            this.topicName = "";
            this.partitionIndex = 0;
            this.controllerEpoch = 0;
            this.leader = 0;
            this.leaderEpoch = 0;
            this.isr = new ArrayList<Integer>();
            this.zkVersion = 0;
            this.replicas = new ArrayList<Integer>();
            this.offlineReplicas = new ArrayList<Integer>();
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 32767;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version <= 4) {
                int length;
                length = _readable.readShort();
                if (length < 0) {
                    throw new RuntimeException("non-nullable field topicName was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field topicName had invalid length " + length);
                } else {
                    this.topicName = _readable.readString(length);
                }
            } else {
                this.topicName = "";
            }
            this.partitionIndex = _readable.readInt();
            this.controllerEpoch = _readable.readInt();
            this.leader = _readable.readInt();
            this.leaderEpoch = _readable.readInt();
            {
                int arrayLength;
                if (_version >= 6) {
                    arrayLength = _readable.readUnsignedVarint() - 1;
                } else {
                    arrayLength = _readable.readInt();
                }
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field isr was serialized as null");
                } else {
                    ArrayList<Integer> newCollection = new ArrayList<Integer>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(_readable.readInt());
                    }
                    this.isr = newCollection;
                }
            }
            this.zkVersion = _readable.readInt();
            {
                int arrayLength;
                if (_version >= 6) {
                    arrayLength = _readable.readUnsignedVarint() - 1;
                } else {
                    arrayLength = _readable.readInt();
                }
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field replicas was serialized as null");
                } else {
                    ArrayList<Integer> newCollection = new ArrayList<Integer>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(_readable.readInt());
                    }
                    this.replicas = newCollection;
                }
            }
            if (_version >= 4) {
                int arrayLength;
                if (_version >= 6) {
                    arrayLength = _readable.readUnsignedVarint() - 1;
                } else {
                    arrayLength = _readable.readInt();
                }
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field offlineReplicas was serialized as null");
                } else {
                    ArrayList<Integer> newCollection = new ArrayList<Integer>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(_readable.readInt());
                    }
                    this.offlineReplicas = newCollection;
                }
            } else {
                this.offlineReplicas = new ArrayList<Integer>();
            }
            this._unknownTaggedFields = null;
            if (_version >= 6) {
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
            if (_version <= 4) {
                {
                    byte[] _stringBytes = _cache.getSerializedValue(topicName);
                    _writable.writeShort((short) _stringBytes.length);
                    _writable.writeByteArray(_stringBytes);
                }
            }
            _writable.writeInt(partitionIndex);
            _writable.writeInt(controllerEpoch);
            _writable.writeInt(leader);
            _writable.writeInt(leaderEpoch);
            if (_version >= 6) {
                _writable.writeUnsignedVarint(isr.size() + 1);
            } else {
                _writable.writeInt(isr.size());
            }
            for (Integer isrElement : isr) {
                _writable.writeInt(isrElement);
            }
            _writable.writeInt(zkVersion);
            if (_version >= 6) {
                _writable.writeUnsignedVarint(replicas.size() + 1);
            } else {
                _writable.writeInt(replicas.size());
            }
            for (Integer replicasElement : replicas) {
                _writable.writeInt(replicasElement);
            }
            if (_version >= 4) {
                if (_version >= 6) {
                    _writable.writeUnsignedVarint(offlineReplicas.size() + 1);
                } else {
                    _writable.writeInt(offlineReplicas.size());
                }
                for (Integer offlineReplicasElement : offlineReplicas) {
                    _writable.writeInt(offlineReplicasElement);
                }
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 6) {
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
            if (_version >= 6) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            if (_version <= 4) {
                this.topicName = struct.getString("topic_name");
            } else {
                this.topicName = "";
            }
            this.partitionIndex = struct.getInt("partition_index");
            this.controllerEpoch = struct.getInt("controller_epoch");
            this.leader = struct.getInt("leader");
            this.leaderEpoch = struct.getInt("leader_epoch");
            {
                Object[] _nestedObjects = struct.getArray("isr");
                this.isr = new ArrayList<Integer>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.isr.add((Integer) nestedObject);
                }
            }
            this.zkVersion = struct.getInt("zk_version");
            {
                Object[] _nestedObjects = struct.getArray("replicas");
                this.replicas = new ArrayList<Integer>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.replicas.add((Integer) nestedObject);
                }
            }
            if (_version >= 4) {
                Object[] _nestedObjects = struct.getArray("offline_replicas");
                this.offlineReplicas = new ArrayList<Integer>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.offlineReplicas.add((Integer) nestedObject);
                }
            } else {
                this.offlineReplicas = new ArrayList<Integer>();
            }
            if (_version >= 6) {
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
            if (_version >= 6) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            if (_version <= 4) {
                struct.set("topic_name", this.topicName);
            }
            struct.set("partition_index", this.partitionIndex);
            struct.set("controller_epoch", this.controllerEpoch);
            struct.set("leader", this.leader);
            struct.set("leader_epoch", this.leaderEpoch);
            {
                Integer[] _nestedObjects = new Integer[isr.size()];
                int i = 0;
                for (Integer element : this.isr) {
                    _nestedObjects[i++] = element;
                }
                struct.set("isr", (Object[]) _nestedObjects);
            }
            struct.set("zk_version", this.zkVersion);
            {
                Integer[] _nestedObjects = new Integer[replicas.size()];
                int i = 0;
                for (Integer element : this.replicas) {
                    _nestedObjects[i++] = element;
                }
                struct.set("replicas", (Object[]) _nestedObjects);
            }
            if (_version >= 4) {
                Integer[] _nestedObjects = new Integer[offlineReplicas.size()];
                int i = 0;
                for (Integer element : this.offlineReplicas) {
                    _nestedObjects[i++] = element;
                }
                struct.set("offline_replicas", (Object[]) _nestedObjects);
            }
            if (_version >= 6) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version <= 4) {
                {
                    byte[] _stringBytes = topicName.getBytes(StandardCharsets.UTF_8);
                    if (_stringBytes.length > 0x7fff) {
                        throw new RuntimeException("'topicName' field is too long to be serialized");
                    }
                    _cache.cacheSerializedValue(topicName, _stringBytes);
                    _size += _stringBytes.length + 2;
                }
            }
            _size += 4;
            _size += 4;
            _size += 4;
            _size += 4;
            {
                int _arraySize = 0;
                if (_version >= 6) {
                    _arraySize += ByteUtils.sizeOfUnsignedVarint(isr.size() + 1);
                } else {
                    _arraySize += 4;
                }
                _arraySize += isr.size() * 4;
                _size += _arraySize;
            }
            _size += 4;
            {
                int _arraySize = 0;
                if (_version >= 6) {
                    _arraySize += ByteUtils.sizeOfUnsignedVarint(replicas.size() + 1);
                } else {
                    _arraySize += 4;
                }
                _arraySize += replicas.size() * 4;
                _size += _arraySize;
            }
            if (_version >= 4) {
                {
                    int _arraySize = 0;
                    if (_version >= 6) {
                        _arraySize += ByteUtils.sizeOfUnsignedVarint(offlineReplicas.size() + 1);
                    } else {
                        _arraySize += 4;
                    }
                    _arraySize += offlineReplicas.size() * 4;
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
            if (_version >= 6) {
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
            if (!(obj instanceof UpdateMetadataPartitionState)) return false;
            UpdateMetadataPartitionState other = (UpdateMetadataPartitionState) obj;
            if (this.topicName == null) {
                if (other.topicName != null) return false;
            } else {
                if (!this.topicName.equals(other.topicName)) return false;
            }
            if (partitionIndex != other.partitionIndex) return false;
            if (controllerEpoch != other.controllerEpoch) return false;
            if (leader != other.leader) return false;
            if (leaderEpoch != other.leaderEpoch) return false;
            if (this.isr == null) {
                if (other.isr != null) return false;
            } else {
                if (!this.isr.equals(other.isr)) return false;
            }
            if (zkVersion != other.zkVersion) return false;
            if (this.replicas == null) {
                if (other.replicas != null) return false;
            } else {
                if (!this.replicas.equals(other.replicas)) return false;
            }
            if (this.offlineReplicas == null) {
                if (other.offlineReplicas != null) return false;
            } else {
                if (!this.offlineReplicas.equals(other.offlineReplicas)) return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (topicName == null ? 0 : topicName.hashCode());
            hashCode = 31 * hashCode + partitionIndex;
            hashCode = 31 * hashCode + controllerEpoch;
            hashCode = 31 * hashCode + leader;
            hashCode = 31 * hashCode + leaderEpoch;
            hashCode = 31 * hashCode + (isr == null ? 0 : isr.hashCode());
            hashCode = 31 * hashCode + zkVersion;
            hashCode = 31 * hashCode + (replicas == null ? 0 : replicas.hashCode());
            hashCode = 31 * hashCode + (offlineReplicas == null ? 0 : offlineReplicas.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "UpdateMetadataPartitionState("
                + "topicName=" + ((topicName == null) ? "null" : "'" + topicName.toString() + "'")
                + ", partitionIndex=" + partitionIndex
                + ", controllerEpoch=" + controllerEpoch
                + ", leader=" + leader
                + ", leaderEpoch=" + leaderEpoch
                + ", isr=" + MessageUtil.deepToString(isr.iterator())
                + ", zkVersion=" + zkVersion
                + ", replicas=" + MessageUtil.deepToString(replicas.iterator())
                + ", offlineReplicas=" + MessageUtil.deepToString(offlineReplicas.iterator())
                + ")";
        }
        
        public String topicName() {
            return this.topicName;
        }
        
        public int partitionIndex() {
            return this.partitionIndex;
        }
        
        public int controllerEpoch() {
            return this.controllerEpoch;
        }
        
        public int leader() {
            return this.leader;
        }
        
        public int leaderEpoch() {
            return this.leaderEpoch;
        }
        
        public List<Integer> isr() {
            return this.isr;
        }
        
        public int zkVersion() {
            return this.zkVersion;
        }
        
        public List<Integer> replicas() {
            return this.replicas;
        }
        
        public List<Integer> offlineReplicas() {
            return this.offlineReplicas;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public UpdateMetadataPartitionState setTopicName(String v) {
            this.topicName = v;
            return this;
        }
        
        public UpdateMetadataPartitionState setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
        
        public UpdateMetadataPartitionState setControllerEpoch(int v) {
            this.controllerEpoch = v;
            return this;
        }
        
        public UpdateMetadataPartitionState setLeader(int v) {
            this.leader = v;
            return this;
        }
        
        public UpdateMetadataPartitionState setLeaderEpoch(int v) {
            this.leaderEpoch = v;
            return this;
        }
        
        public UpdateMetadataPartitionState setIsr(List<Integer> v) {
            this.isr = v;
            return this;
        }
        
        public UpdateMetadataPartitionState setZkVersion(int v) {
            this.zkVersion = v;
            return this;
        }
        
        public UpdateMetadataPartitionState setReplicas(List<Integer> v) {
            this.replicas = v;
            return this;
        }
        
        public UpdateMetadataPartitionState setOfflineReplicas(List<Integer> v) {
            this.offlineReplicas = v;
            return this;
        }
    }
}
