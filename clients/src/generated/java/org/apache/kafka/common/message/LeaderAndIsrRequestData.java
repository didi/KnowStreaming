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


public class LeaderAndIsrRequestData implements ApiMessage {
    private int controllerId;
    private int controllerEpoch;
    private long brokerEpoch;
    private List<LeaderAndIsrPartitionState> ungroupedPartitionStates;
    private List<LeaderAndIsrTopicState> topicStates;
    private List<LeaderAndIsrLiveLeader> liveLeaders;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("controller_id", Type.INT32, "The current controller ID."),
            new Field("controller_epoch", Type.INT32, "The current controller epoch."),
            new Field("ungrouped_partition_states", new ArrayOf(LeaderAndIsrPartitionState.SCHEMA_0), "The state of each partition, in a v0 or v1 message."),
            new Field("live_leaders", new ArrayOf(LeaderAndIsrLiveLeader.SCHEMA_0), "The current live leaders.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("controller_id", Type.INT32, "The current controller ID."),
            new Field("controller_epoch", Type.INT32, "The current controller epoch."),
            new Field("ungrouped_partition_states", new ArrayOf(LeaderAndIsrPartitionState.SCHEMA_1), "The state of each partition, in a v0 or v1 message."),
            new Field("live_leaders", new ArrayOf(LeaderAndIsrLiveLeader.SCHEMA_0), "The current live leaders.")
        );
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("controller_id", Type.INT32, "The current controller ID."),
            new Field("controller_epoch", Type.INT32, "The current controller epoch."),
            new Field("broker_epoch", Type.INT64, "The current broker epoch."),
            new Field("topic_states", new ArrayOf(LeaderAndIsrTopicState.SCHEMA_2), "Each topic."),
            new Field("live_leaders", new ArrayOf(LeaderAndIsrLiveLeader.SCHEMA_0), "The current live leaders.")
        );
    
    public static final Schema SCHEMA_3 =
        new Schema(
            new Field("controller_id", Type.INT32, "The current controller ID."),
            new Field("controller_epoch", Type.INT32, "The current controller epoch."),
            new Field("broker_epoch", Type.INT64, "The current broker epoch."),
            new Field("topic_states", new ArrayOf(LeaderAndIsrTopicState.SCHEMA_3), "Each topic."),
            new Field("live_leaders", new ArrayOf(LeaderAndIsrLiveLeader.SCHEMA_0), "The current live leaders.")
        );
    
    public static final Schema SCHEMA_4 =
        new Schema(
            new Field("controller_id", Type.INT32, "The current controller ID."),
            new Field("controller_epoch", Type.INT32, "The current controller epoch."),
            new Field("broker_epoch", Type.INT64, "The current broker epoch."),
            new Field("topic_states", new CompactArrayOf(LeaderAndIsrTopicState.SCHEMA_4), "Each topic."),
            new Field("live_leaders", new CompactArrayOf(LeaderAndIsrLiveLeader.SCHEMA_4), "The current live leaders."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2,
        SCHEMA_3,
        SCHEMA_4
    };
    
    public LeaderAndIsrRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public LeaderAndIsrRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public LeaderAndIsrRequestData() {
        this.controllerId = 0;
        this.controllerEpoch = 0;
        this.brokerEpoch = -1L;
        this.ungroupedPartitionStates = new ArrayList<LeaderAndIsrPartitionState>();
        this.topicStates = new ArrayList<LeaderAndIsrTopicState>();
        this.liveLeaders = new ArrayList<LeaderAndIsrLiveLeader>();
    }
    
    @Override
    public short apiKey() {
        return 4;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 4;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        this.controllerId = _readable.readInt();
        this.controllerEpoch = _readable.readInt();
        if (_version >= 2) {
            this.brokerEpoch = _readable.readLong();
        } else {
            this.brokerEpoch = -1L;
        }
        if (_version <= 1) {
            int arrayLength;
            arrayLength = _readable.readInt();
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field ungroupedPartitionStates was serialized as null");
            } else {
                ArrayList<LeaderAndIsrPartitionState> newCollection = new ArrayList<LeaderAndIsrPartitionState>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new LeaderAndIsrPartitionState(_readable, _version));
                }
                this.ungroupedPartitionStates = newCollection;
            }
        } else {
            this.ungroupedPartitionStates = new ArrayList<LeaderAndIsrPartitionState>();
        }
        if (_version >= 2) {
            if (_version >= 4) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topicStates was serialized as null");
                } else {
                    ArrayList<LeaderAndIsrTopicState> newCollection = new ArrayList<LeaderAndIsrTopicState>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new LeaderAndIsrTopicState(_readable, _version));
                    }
                    this.topicStates = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topicStates was serialized as null");
                } else {
                    ArrayList<LeaderAndIsrTopicState> newCollection = new ArrayList<LeaderAndIsrTopicState>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new LeaderAndIsrTopicState(_readable, _version));
                    }
                    this.topicStates = newCollection;
                }
            }
        } else {
            this.topicStates = new ArrayList<LeaderAndIsrTopicState>();
        }
        {
            if (_version >= 4) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field liveLeaders was serialized as null");
                } else {
                    ArrayList<LeaderAndIsrLiveLeader> newCollection = new ArrayList<LeaderAndIsrLiveLeader>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new LeaderAndIsrLiveLeader(_readable, _version));
                    }
                    this.liveLeaders = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field liveLeaders was serialized as null");
                } else {
                    ArrayList<LeaderAndIsrLiveLeader> newCollection = new ArrayList<LeaderAndIsrLiveLeader>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new LeaderAndIsrLiveLeader(_readable, _version));
                    }
                    this.liveLeaders = newCollection;
                }
            }
        }
        this._unknownTaggedFields = null;
        if (_version >= 4) {
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
        if (_version >= 2) {
            _writable.writeLong(brokerEpoch);
        }
        if (_version <= 1) {
            _writable.writeInt(ungroupedPartitionStates.size());
            for (LeaderAndIsrPartitionState ungroupedPartitionStatesElement : ungroupedPartitionStates) {
                ungroupedPartitionStatesElement.write(_writable, _cache, _version);
            }
        } else {
            if (!ungroupedPartitionStates.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default ungroupedPartitionStates at version " + _version);
            }
        }
        if (_version >= 2) {
            if (_version >= 4) {
                _writable.writeUnsignedVarint(topicStates.size() + 1);
                for (LeaderAndIsrTopicState topicStatesElement : topicStates) {
                    topicStatesElement.write(_writable, _cache, _version);
                }
            } else {
                _writable.writeInt(topicStates.size());
                for (LeaderAndIsrTopicState topicStatesElement : topicStates) {
                    topicStatesElement.write(_writable, _cache, _version);
                }
            }
        } else {
            if (!topicStates.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default topicStates at version " + _version);
            }
        }
        if (_version >= 4) {
            _writable.writeUnsignedVarint(liveLeaders.size() + 1);
            for (LeaderAndIsrLiveLeader liveLeadersElement : liveLeaders) {
                liveLeadersElement.write(_writable, _cache, _version);
            }
        } else {
            _writable.writeInt(liveLeaders.size());
            for (LeaderAndIsrLiveLeader liveLeadersElement : liveLeaders) {
                liveLeadersElement.write(_writable, _cache, _version);
            }
        }
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        if (_version >= 4) {
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
        if (_version >= 4) {
            _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
        }
        this.controllerId = struct.getInt("controller_id");
        this.controllerEpoch = struct.getInt("controller_epoch");
        if (_version >= 2) {
            this.brokerEpoch = struct.getLong("broker_epoch");
        } else {
            this.brokerEpoch = -1L;
        }
        if (_version <= 1) {
            Object[] _nestedObjects = struct.getArray("ungrouped_partition_states");
            this.ungroupedPartitionStates = new ArrayList<LeaderAndIsrPartitionState>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.ungroupedPartitionStates.add(new LeaderAndIsrPartitionState((Struct) nestedObject, _version));
            }
        } else {
            this.ungroupedPartitionStates = new ArrayList<LeaderAndIsrPartitionState>();
        }
        if (_version >= 2) {
            Object[] _nestedObjects = struct.getArray("topic_states");
            this.topicStates = new ArrayList<LeaderAndIsrTopicState>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.topicStates.add(new LeaderAndIsrTopicState((Struct) nestedObject, _version));
            }
        } else {
            this.topicStates = new ArrayList<LeaderAndIsrTopicState>();
        }
        {
            Object[] _nestedObjects = struct.getArray("live_leaders");
            this.liveLeaders = new ArrayList<LeaderAndIsrLiveLeader>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.liveLeaders.add(new LeaderAndIsrLiveLeader((Struct) nestedObject, _version));
            }
        }
        if (_version >= 4) {
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
        if (_version >= 4) {
            _taggedFields = new TreeMap<>();
        }
        Struct struct = new Struct(SCHEMAS[_version]);
        struct.set("controller_id", this.controllerId);
        struct.set("controller_epoch", this.controllerEpoch);
        if (_version >= 2) {
            struct.set("broker_epoch", this.brokerEpoch);
        }
        if (_version <= 1) {
            Struct[] _nestedObjects = new Struct[ungroupedPartitionStates.size()];
            int i = 0;
            for (LeaderAndIsrPartitionState element : this.ungroupedPartitionStates) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("ungrouped_partition_states", (Object[]) _nestedObjects);
        } else {
            if (!ungroupedPartitionStates.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default ungroupedPartitionStates at version " + _version);
            }
        }
        if (_version >= 2) {
            Struct[] _nestedObjects = new Struct[topicStates.size()];
            int i = 0;
            for (LeaderAndIsrTopicState element : this.topicStates) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("topic_states", (Object[]) _nestedObjects);
        } else {
            if (!topicStates.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default topicStates at version " + _version);
            }
        }
        {
            Struct[] _nestedObjects = new Struct[liveLeaders.size()];
            int i = 0;
            for (LeaderAndIsrLiveLeader element : this.liveLeaders) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("live_leaders", (Object[]) _nestedObjects);
        }
        if (_version >= 4) {
            struct.set("_tagged_fields", _taggedFields);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        _size += 4;
        _size += 4;
        if (_version >= 2) {
            _size += 8;
        }
        if (_version <= 1) {
            {
                int _arraySize = 0;
                _arraySize += 4;
                for (LeaderAndIsrPartitionState ungroupedPartitionStatesElement : ungroupedPartitionStates) {
                    _arraySize += ungroupedPartitionStatesElement.size(_cache, _version);
                }
                _size += _arraySize;
            }
        }
        if (_version >= 2) {
            {
                int _arraySize = 0;
                if (_version >= 4) {
                    _arraySize += ByteUtils.sizeOfUnsignedVarint(topicStates.size() + 1);
                } else {
                    _arraySize += 4;
                }
                for (LeaderAndIsrTopicState topicStatesElement : topicStates) {
                    _arraySize += topicStatesElement.size(_cache, _version);
                }
                _size += _arraySize;
            }
        }
        {
            int _arraySize = 0;
            if (_version >= 4) {
                _arraySize += ByteUtils.sizeOfUnsignedVarint(liveLeaders.size() + 1);
            } else {
                _arraySize += 4;
            }
            for (LeaderAndIsrLiveLeader liveLeadersElement : liveLeaders) {
                _arraySize += liveLeadersElement.size(_cache, _version);
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
        if (_version >= 4) {
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
        if (!(obj instanceof LeaderAndIsrRequestData)) return false;
        LeaderAndIsrRequestData other = (LeaderAndIsrRequestData) obj;
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
        if (this.liveLeaders == null) {
            if (other.liveLeaders != null) return false;
        } else {
            if (!this.liveLeaders.equals(other.liveLeaders)) return false;
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
        hashCode = 31 * hashCode + (liveLeaders == null ? 0 : liveLeaders.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "LeaderAndIsrRequestData("
            + "controllerId=" + controllerId
            + ", controllerEpoch=" + controllerEpoch
            + ", brokerEpoch=" + brokerEpoch
            + ", ungroupedPartitionStates=" + MessageUtil.deepToString(ungroupedPartitionStates.iterator())
            + ", topicStates=" + MessageUtil.deepToString(topicStates.iterator())
            + ", liveLeaders=" + MessageUtil.deepToString(liveLeaders.iterator())
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
    
    public List<LeaderAndIsrPartitionState> ungroupedPartitionStates() {
        return this.ungroupedPartitionStates;
    }
    
    public List<LeaderAndIsrTopicState> topicStates() {
        return this.topicStates;
    }
    
    public List<LeaderAndIsrLiveLeader> liveLeaders() {
        return this.liveLeaders;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public LeaderAndIsrRequestData setControllerId(int v) {
        this.controllerId = v;
        return this;
    }
    
    public LeaderAndIsrRequestData setControllerEpoch(int v) {
        this.controllerEpoch = v;
        return this;
    }
    
    public LeaderAndIsrRequestData setBrokerEpoch(long v) {
        this.brokerEpoch = v;
        return this;
    }
    
    public LeaderAndIsrRequestData setUngroupedPartitionStates(List<LeaderAndIsrPartitionState> v) {
        this.ungroupedPartitionStates = v;
        return this;
    }
    
    public LeaderAndIsrRequestData setTopicStates(List<LeaderAndIsrTopicState> v) {
        this.topicStates = v;
        return this;
    }
    
    public LeaderAndIsrRequestData setLiveLeaders(List<LeaderAndIsrLiveLeader> v) {
        this.liveLeaders = v;
        return this;
    }
    
    static public class LeaderAndIsrTopicState implements Message {
        private String topicName;
        private List<LeaderAndIsrPartitionState> partitionStates;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_2 =
            new Schema(
                new Field("topic_name", Type.STRING, "The topic name."),
                new Field("partition_states", new ArrayOf(LeaderAndIsrPartitionState.SCHEMA_2), "The state of each partition")
            );
        
        public static final Schema SCHEMA_3 =
            new Schema(
                new Field("topic_name", Type.STRING, "The topic name."),
                new Field("partition_states", new ArrayOf(LeaderAndIsrPartitionState.SCHEMA_3), "The state of each partition")
            );
        
        public static final Schema SCHEMA_4 =
            new Schema(
                new Field("topic_name", Type.COMPACT_STRING, "The topic name."),
                new Field("partition_states", new CompactArrayOf(LeaderAndIsrPartitionState.SCHEMA_4), "The state of each partition"),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            null,
            null,
            SCHEMA_2,
            SCHEMA_3,
            SCHEMA_4
        };
        
        public LeaderAndIsrTopicState(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public LeaderAndIsrTopicState(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public LeaderAndIsrTopicState() {
            this.topicName = "";
            this.partitionStates = new ArrayList<LeaderAndIsrPartitionState>();
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 4;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 4) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of LeaderAndIsrTopicState");
            }
            {
                int length;
                if (_version >= 4) {
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
                if (_version >= 4) {
                    int arrayLength;
                    arrayLength = _readable.readUnsignedVarint() - 1;
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field partitionStates was serialized as null");
                    } else {
                        ArrayList<LeaderAndIsrPartitionState> newCollection = new ArrayList<LeaderAndIsrPartitionState>(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new LeaderAndIsrPartitionState(_readable, _version));
                        }
                        this.partitionStates = newCollection;
                    }
                } else {
                    int arrayLength;
                    arrayLength = _readable.readInt();
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field partitionStates was serialized as null");
                    } else {
                        ArrayList<LeaderAndIsrPartitionState> newCollection = new ArrayList<LeaderAndIsrPartitionState>(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new LeaderAndIsrPartitionState(_readable, _version));
                        }
                        this.partitionStates = newCollection;
                    }
                }
            }
            this._unknownTaggedFields = null;
            if (_version >= 4) {
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
            if (_version > 4) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of LeaderAndIsrTopicState");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(topicName);
                if (_version >= 4) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            if (_version >= 4) {
                _writable.writeUnsignedVarint(partitionStates.size() + 1);
                for (LeaderAndIsrPartitionState partitionStatesElement : partitionStates) {
                    partitionStatesElement.write(_writable, _cache, _version);
                }
            } else {
                _writable.writeInt(partitionStates.size());
                for (LeaderAndIsrPartitionState partitionStatesElement : partitionStates) {
                    partitionStatesElement.write(_writable, _cache, _version);
                }
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 4) {
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
            if (_version > 4) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of LeaderAndIsrTopicState");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 4) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.topicName = struct.getString("topic_name");
            {
                Object[] _nestedObjects = struct.getArray("partition_states");
                this.partitionStates = new ArrayList<LeaderAndIsrPartitionState>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.partitionStates.add(new LeaderAndIsrPartitionState((Struct) nestedObject, _version));
                }
            }
            if (_version >= 4) {
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
            if (_version > 4) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of LeaderAndIsrTopicState");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 4) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("topic_name", this.topicName);
            {
                Struct[] _nestedObjects = new Struct[partitionStates.size()];
                int i = 0;
                for (LeaderAndIsrPartitionState element : this.partitionStates) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("partition_states", (Object[]) _nestedObjects);
            }
            if (_version >= 4) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 4) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of LeaderAndIsrTopicState");
            }
            {
                byte[] _stringBytes = topicName.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'topicName' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(topicName, _stringBytes);
                if (_version >= 4) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
            }
            {
                int _arraySize = 0;
                if (_version >= 4) {
                    _arraySize += ByteUtils.sizeOfUnsignedVarint(partitionStates.size() + 1);
                } else {
                    _arraySize += 4;
                }
                for (LeaderAndIsrPartitionState partitionStatesElement : partitionStates) {
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
            if (_version >= 4) {
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
            if (!(obj instanceof LeaderAndIsrTopicState)) return false;
            LeaderAndIsrTopicState other = (LeaderAndIsrTopicState) obj;
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
            return "LeaderAndIsrTopicState("
                + "topicName=" + ((topicName == null) ? "null" : "'" + topicName.toString() + "'")
                + ", partitionStates=" + MessageUtil.deepToString(partitionStates.iterator())
                + ")";
        }
        
        public String topicName() {
            return this.topicName;
        }
        
        public List<LeaderAndIsrPartitionState> partitionStates() {
            return this.partitionStates;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public LeaderAndIsrTopicState setTopicName(String v) {
            this.topicName = v;
            return this;
        }
        
        public LeaderAndIsrTopicState setPartitionStates(List<LeaderAndIsrPartitionState> v) {
            this.partitionStates = v;
            return this;
        }
    }
    
    static public class LeaderAndIsrLiveLeader implements Message {
        private int brokerId;
        private String hostName;
        private int port;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("broker_id", Type.INT32, "The leader's broker ID."),
                new Field("host_name", Type.STRING, "The leader's hostname."),
                new Field("port", Type.INT32, "The leader's port.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 =
            new Schema(
                new Field("broker_id", Type.INT32, "The leader's broker ID."),
                new Field("host_name", Type.COMPACT_STRING, "The leader's hostname."),
                new Field("port", Type.INT32, "The leader's port."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3,
            SCHEMA_4
        };
        
        public LeaderAndIsrLiveLeader(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public LeaderAndIsrLiveLeader(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public LeaderAndIsrLiveLeader() {
            this.brokerId = 0;
            this.hostName = "";
            this.port = 0;
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 4;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 4) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of LeaderAndIsrLiveLeader");
            }
            this.brokerId = _readable.readInt();
            {
                int length;
                if (_version >= 4) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field hostName was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field hostName had invalid length " + length);
                } else {
                    this.hostName = _readable.readString(length);
                }
            }
            this.port = _readable.readInt();
            this._unknownTaggedFields = null;
            if (_version >= 4) {
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
            if (_version > 4) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of LeaderAndIsrLiveLeader");
            }
            int _numTaggedFields = 0;
            _writable.writeInt(brokerId);
            {
                byte[] _stringBytes = _cache.getSerializedValue(hostName);
                if (_version >= 4) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeInt(port);
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 4) {
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
            if (_version > 4) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of LeaderAndIsrLiveLeader");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 4) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.brokerId = struct.getInt("broker_id");
            this.hostName = struct.getString("host_name");
            this.port = struct.getInt("port");
            if (_version >= 4) {
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
            if (_version > 4) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of LeaderAndIsrLiveLeader");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 4) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("broker_id", this.brokerId);
            struct.set("host_name", this.hostName);
            struct.set("port", this.port);
            if (_version >= 4) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 4) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of LeaderAndIsrLiveLeader");
            }
            _size += 4;
            {
                byte[] _stringBytes = hostName.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'hostName' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(hostName, _stringBytes);
                if (_version >= 4) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
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
            if (_version >= 4) {
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
            if (!(obj instanceof LeaderAndIsrLiveLeader)) return false;
            LeaderAndIsrLiveLeader other = (LeaderAndIsrLiveLeader) obj;
            if (brokerId != other.brokerId) return false;
            if (this.hostName == null) {
                if (other.hostName != null) return false;
            } else {
                if (!this.hostName.equals(other.hostName)) return false;
            }
            if (port != other.port) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + brokerId;
            hashCode = 31 * hashCode + (hostName == null ? 0 : hostName.hashCode());
            hashCode = 31 * hashCode + port;
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "LeaderAndIsrLiveLeader("
                + "brokerId=" + brokerId
                + ", hostName=" + ((hostName == null) ? "null" : "'" + hostName.toString() + "'")
                + ", port=" + port
                + ")";
        }
        
        public int brokerId() {
            return this.brokerId;
        }
        
        public String hostName() {
            return this.hostName;
        }
        
        public int port() {
            return this.port;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public LeaderAndIsrLiveLeader setBrokerId(int v) {
            this.brokerId = v;
            return this;
        }
        
        public LeaderAndIsrLiveLeader setHostName(String v) {
            this.hostName = v;
            return this;
        }
        
        public LeaderAndIsrLiveLeader setPort(int v) {
            this.port = v;
            return this;
        }
    }
    
    static public class LeaderAndIsrPartitionState implements Message {
        private String topicName;
        private int partitionIndex;
        private int controllerEpoch;
        private int leader;
        private int leaderEpoch;
        private List<Integer> isr;
        private int zkVersion;
        private List<Integer> replicas;
        private List<Integer> addingReplicas;
        private List<Integer> removingReplicas;
        private boolean isNew;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("topic_name", Type.STRING, "The topic name.  This is only present in v0 or v1."),
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("controller_epoch", Type.INT32, "The controller epoch."),
                new Field("leader", Type.INT32, "The broker ID of the leader."),
                new Field("leader_epoch", Type.INT32, "The leader epoch."),
                new Field("isr", new ArrayOf(Type.INT32), "The in-sync replica IDs."),
                new Field("zk_version", Type.INT32, "The ZooKeeper version."),
                new Field("replicas", new ArrayOf(Type.INT32), "The replica IDs.")
            );
        
        public static final Schema SCHEMA_1 =
            new Schema(
                new Field("topic_name", Type.STRING, "The topic name.  This is only present in v0 or v1."),
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("controller_epoch", Type.INT32, "The controller epoch."),
                new Field("leader", Type.INT32, "The broker ID of the leader."),
                new Field("leader_epoch", Type.INT32, "The leader epoch."),
                new Field("isr", new ArrayOf(Type.INT32), "The in-sync replica IDs."),
                new Field("zk_version", Type.INT32, "The ZooKeeper version."),
                new Field("replicas", new ArrayOf(Type.INT32), "The replica IDs."),
                new Field("is_new", Type.BOOLEAN, "Whether the replica should have existed on the broker or not.")
            );
        
        public static final Schema SCHEMA_2 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("controller_epoch", Type.INT32, "The controller epoch."),
                new Field("leader", Type.INT32, "The broker ID of the leader."),
                new Field("leader_epoch", Type.INT32, "The leader epoch."),
                new Field("isr", new ArrayOf(Type.INT32), "The in-sync replica IDs."),
                new Field("zk_version", Type.INT32, "The ZooKeeper version."),
                new Field("replicas", new ArrayOf(Type.INT32), "The replica IDs."),
                new Field("is_new", Type.BOOLEAN, "Whether the replica should have existed on the broker or not.")
            );
        
        public static final Schema SCHEMA_3 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("controller_epoch", Type.INT32, "The controller epoch."),
                new Field("leader", Type.INT32, "The broker ID of the leader."),
                new Field("leader_epoch", Type.INT32, "The leader epoch."),
                new Field("isr", new ArrayOf(Type.INT32), "The in-sync replica IDs."),
                new Field("zk_version", Type.INT32, "The ZooKeeper version."),
                new Field("replicas", new ArrayOf(Type.INT32), "The replica IDs."),
                new Field("adding_replicas", new ArrayOf(Type.INT32), "The replica IDs that we are adding this partition to, or null if no replicas are being added."),
                new Field("removing_replicas", new ArrayOf(Type.INT32), "The replica IDs that we are removing this partition from, or null if no replicas are being removed."),
                new Field("is_new", Type.BOOLEAN, "Whether the replica should have existed on the broker or not.")
            );
        
        public static final Schema SCHEMA_4 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("controller_epoch", Type.INT32, "The controller epoch."),
                new Field("leader", Type.INT32, "The broker ID of the leader."),
                new Field("leader_epoch", Type.INT32, "The leader epoch."),
                new Field("isr", new CompactArrayOf(Type.INT32), "The in-sync replica IDs."),
                new Field("zk_version", Type.INT32, "The ZooKeeper version."),
                new Field("replicas", new CompactArrayOf(Type.INT32), "The replica IDs."),
                new Field("adding_replicas", new CompactArrayOf(Type.INT32), "The replica IDs that we are adding this partition to, or null if no replicas are being added."),
                new Field("removing_replicas", new CompactArrayOf(Type.INT32), "The replica IDs that we are removing this partition from, or null if no replicas are being removed."),
                new Field("is_new", Type.BOOLEAN, "Whether the replica should have existed on the broker or not."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3,
            SCHEMA_4
        };
        
        public LeaderAndIsrPartitionState(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public LeaderAndIsrPartitionState(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public LeaderAndIsrPartitionState() {
            this.topicName = "";
            this.partitionIndex = 0;
            this.controllerEpoch = 0;
            this.leader = 0;
            this.leaderEpoch = 0;
            this.isr = new ArrayList<Integer>();
            this.zkVersion = 0;
            this.replicas = new ArrayList<Integer>();
            this.addingReplicas = new ArrayList<Integer>();
            this.removingReplicas = new ArrayList<Integer>();
            this.isNew = false;
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
            if (_version <= 1) {
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
                if (_version >= 4) {
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
                if (_version >= 4) {
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
            if (_version >= 3) {
                int arrayLength;
                if (_version >= 4) {
                    arrayLength = _readable.readUnsignedVarint() - 1;
                } else {
                    arrayLength = _readable.readInt();
                }
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field addingReplicas was serialized as null");
                } else {
                    ArrayList<Integer> newCollection = new ArrayList<Integer>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(_readable.readInt());
                    }
                    this.addingReplicas = newCollection;
                }
            } else {
                this.addingReplicas = new ArrayList<Integer>();
            }
            if (_version >= 3) {
                int arrayLength;
                if (_version >= 4) {
                    arrayLength = _readable.readUnsignedVarint() - 1;
                } else {
                    arrayLength = _readable.readInt();
                }
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field removingReplicas was serialized as null");
                } else {
                    ArrayList<Integer> newCollection = new ArrayList<Integer>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(_readable.readInt());
                    }
                    this.removingReplicas = newCollection;
                }
            } else {
                this.removingReplicas = new ArrayList<Integer>();
            }
            if (_version >= 1) {
                this.isNew = _readable.readByte() != 0;
            } else {
                this.isNew = false;
            }
            this._unknownTaggedFields = null;
            if (_version >= 4) {
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
            if (_version <= 1) {
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
            if (_version >= 4) {
                _writable.writeUnsignedVarint(isr.size() + 1);
            } else {
                _writable.writeInt(isr.size());
            }
            for (Integer isrElement : isr) {
                _writable.writeInt(isrElement);
            }
            _writable.writeInt(zkVersion);
            if (_version >= 4) {
                _writable.writeUnsignedVarint(replicas.size() + 1);
            } else {
                _writable.writeInt(replicas.size());
            }
            for (Integer replicasElement : replicas) {
                _writable.writeInt(replicasElement);
            }
            if (_version >= 3) {
                if (_version >= 4) {
                    _writable.writeUnsignedVarint(addingReplicas.size() + 1);
                } else {
                    _writable.writeInt(addingReplicas.size());
                }
                for (Integer addingReplicasElement : addingReplicas) {
                    _writable.writeInt(addingReplicasElement);
                }
            }
            if (_version >= 3) {
                if (_version >= 4) {
                    _writable.writeUnsignedVarint(removingReplicas.size() + 1);
                } else {
                    _writable.writeInt(removingReplicas.size());
                }
                for (Integer removingReplicasElement : removingReplicas) {
                    _writable.writeInt(removingReplicasElement);
                }
            }
            if (_version >= 1) {
                _writable.writeByte(isNew ? (byte) 1 : (byte) 0);
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 4) {
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
            if (_version >= 4) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            if (_version <= 1) {
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
            if (_version >= 3) {
                Object[] _nestedObjects = struct.getArray("adding_replicas");
                this.addingReplicas = new ArrayList<Integer>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.addingReplicas.add((Integer) nestedObject);
                }
            } else {
                this.addingReplicas = new ArrayList<Integer>();
            }
            if (_version >= 3) {
                Object[] _nestedObjects = struct.getArray("removing_replicas");
                this.removingReplicas = new ArrayList<Integer>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.removingReplicas.add((Integer) nestedObject);
                }
            } else {
                this.removingReplicas = new ArrayList<Integer>();
            }
            if (_version >= 1) {
                this.isNew = struct.getBoolean("is_new");
            } else {
                this.isNew = false;
            }
            if (_version >= 4) {
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
            if (_version >= 4) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            if (_version <= 1) {
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
            if (_version >= 3) {
                Integer[] _nestedObjects = new Integer[addingReplicas.size()];
                int i = 0;
                for (Integer element : this.addingReplicas) {
                    _nestedObjects[i++] = element;
                }
                struct.set("adding_replicas", (Object[]) _nestedObjects);
            }
            if (_version >= 3) {
                Integer[] _nestedObjects = new Integer[removingReplicas.size()];
                int i = 0;
                for (Integer element : this.removingReplicas) {
                    _nestedObjects[i++] = element;
                }
                struct.set("removing_replicas", (Object[]) _nestedObjects);
            }
            if (_version >= 1) {
                struct.set("is_new", this.isNew);
            }
            if (_version >= 4) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version <= 1) {
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
                if (_version >= 4) {
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
                if (_version >= 4) {
                    _arraySize += ByteUtils.sizeOfUnsignedVarint(replicas.size() + 1);
                } else {
                    _arraySize += 4;
                }
                _arraySize += replicas.size() * 4;
                _size += _arraySize;
            }
            if (_version >= 3) {
                {
                    int _arraySize = 0;
                    if (_version >= 4) {
                        _arraySize += ByteUtils.sizeOfUnsignedVarint(addingReplicas.size() + 1);
                    } else {
                        _arraySize += 4;
                    }
                    _arraySize += addingReplicas.size() * 4;
                    _size += _arraySize;
                }
            }
            if (_version >= 3) {
                {
                    int _arraySize = 0;
                    if (_version >= 4) {
                        _arraySize += ByteUtils.sizeOfUnsignedVarint(removingReplicas.size() + 1);
                    } else {
                        _arraySize += 4;
                    }
                    _arraySize += removingReplicas.size() * 4;
                    _size += _arraySize;
                }
            }
            if (_version >= 1) {
                _size += 1;
            }
            if (_unknownTaggedFields != null) {
                _numTaggedFields += _unknownTaggedFields.size();
                for (RawTaggedField _field : _unknownTaggedFields) {
                    _size += ByteUtils.sizeOfUnsignedVarint(_field.tag());
                    _size += ByteUtils.sizeOfUnsignedVarint(_field.size());
                    _size += _field.size();
                }
            }
            if (_version >= 4) {
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
            if (!(obj instanceof LeaderAndIsrPartitionState)) return false;
            LeaderAndIsrPartitionState other = (LeaderAndIsrPartitionState) obj;
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
            if (this.addingReplicas == null) {
                if (other.addingReplicas != null) return false;
            } else {
                if (!this.addingReplicas.equals(other.addingReplicas)) return false;
            }
            if (this.removingReplicas == null) {
                if (other.removingReplicas != null) return false;
            } else {
                if (!this.removingReplicas.equals(other.removingReplicas)) return false;
            }
            if (isNew != other.isNew) return false;
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
            hashCode = 31 * hashCode + (addingReplicas == null ? 0 : addingReplicas.hashCode());
            hashCode = 31 * hashCode + (removingReplicas == null ? 0 : removingReplicas.hashCode());
            hashCode = 31 * hashCode + (isNew ? 1231 : 1237);
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "LeaderAndIsrPartitionState("
                + "topicName=" + ((topicName == null) ? "null" : "'" + topicName.toString() + "'")
                + ", partitionIndex=" + partitionIndex
                + ", controllerEpoch=" + controllerEpoch
                + ", leader=" + leader
                + ", leaderEpoch=" + leaderEpoch
                + ", isr=" + MessageUtil.deepToString(isr.iterator())
                + ", zkVersion=" + zkVersion
                + ", replicas=" + MessageUtil.deepToString(replicas.iterator())
                + ", addingReplicas=" + MessageUtil.deepToString(addingReplicas.iterator())
                + ", removingReplicas=" + MessageUtil.deepToString(removingReplicas.iterator())
                + ", isNew=" + (isNew ? "true" : "false")
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
        
        public List<Integer> addingReplicas() {
            return this.addingReplicas;
        }
        
        public List<Integer> removingReplicas() {
            return this.removingReplicas;
        }
        
        public boolean isNew() {
            return this.isNew;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public LeaderAndIsrPartitionState setTopicName(String v) {
            this.topicName = v;
            return this;
        }
        
        public LeaderAndIsrPartitionState setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
        
        public LeaderAndIsrPartitionState setControllerEpoch(int v) {
            this.controllerEpoch = v;
            return this;
        }
        
        public LeaderAndIsrPartitionState setLeader(int v) {
            this.leader = v;
            return this;
        }
        
        public LeaderAndIsrPartitionState setLeaderEpoch(int v) {
            this.leaderEpoch = v;
            return this;
        }
        
        public LeaderAndIsrPartitionState setIsr(List<Integer> v) {
            this.isr = v;
            return this;
        }
        
        public LeaderAndIsrPartitionState setZkVersion(int v) {
            this.zkVersion = v;
            return this;
        }
        
        public LeaderAndIsrPartitionState setReplicas(List<Integer> v) {
            this.replicas = v;
            return this;
        }
        
        public LeaderAndIsrPartitionState setAddingReplicas(List<Integer> v) {
            this.addingReplicas = v;
            return this;
        }
        
        public LeaderAndIsrPartitionState setRemovingReplicas(List<Integer> v) {
            this.removingReplicas = v;
            return this;
        }
        
        public LeaderAndIsrPartitionState setIsNew(boolean v) {
            this.isNew = v;
            return this;
        }
    }
}
