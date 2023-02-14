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


public class OffsetFetchRequestData implements ApiMessage {
    private String groupId;
    private List<OffsetFetchRequestTopic> topics;
    private boolean requireStable;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("group_id", Type.STRING, "The group to fetch offsets for."),
            new Field("topics", new ArrayOf(OffsetFetchRequestTopic.SCHEMA_0), "Each topic we would like to fetch offsets for, or null to fetch offsets for all topics.")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("group_id", Type.STRING, "The group to fetch offsets for."),
            new Field("topics", ArrayOf.nullable(OffsetFetchRequestTopic.SCHEMA_0), "Each topic we would like to fetch offsets for, or null to fetch offsets for all topics.")
        );
    
    public static final Schema SCHEMA_3 = SCHEMA_2;
    
    public static final Schema SCHEMA_4 = SCHEMA_3;
    
    public static final Schema SCHEMA_5 = SCHEMA_4;
    
    public static final Schema SCHEMA_6 =
        new Schema(
            new Field("group_id", Type.COMPACT_STRING, "The group to fetch offsets for."),
            new Field("topics", CompactArrayOf.nullable(OffsetFetchRequestTopic.SCHEMA_6), "Each topic we would like to fetch offsets for, or null to fetch offsets for all topics."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema SCHEMA_7 =
        new Schema(
            new Field("group_id", Type.COMPACT_STRING, "The group to fetch offsets for."),
            new Field("topics", CompactArrayOf.nullable(OffsetFetchRequestTopic.SCHEMA_6), "Each topic we would like to fetch offsets for, or null to fetch offsets for all topics."),
            new Field("require_stable", Type.BOOLEAN, "Whether broker should hold on returning unstable offsets but set a retriable error code for the partition."),
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
        SCHEMA_6,
        SCHEMA_7
    };
    
    public OffsetFetchRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public OffsetFetchRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public OffsetFetchRequestData() {
        this.groupId = "";
        this.topics = new ArrayList<OffsetFetchRequestTopic>();
        this.requireStable = false;
    }
    
    @Override
    public short apiKey() {
        return 9;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 7;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        {
            int length;
            if (_version >= 6) {
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
        {
            if (_version >= 6) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    this.topics = null;
                } else {
                    ArrayList<OffsetFetchRequestTopic> newCollection = new ArrayList<OffsetFetchRequestTopic>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new OffsetFetchRequestTopic(_readable, _version));
                    }
                    this.topics = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    if (_version >= 2) {
                        this.topics = null;
                    } else {
                        throw new RuntimeException("non-nullable field topics was serialized as null");
                    }
                } else {
                    ArrayList<OffsetFetchRequestTopic> newCollection = new ArrayList<OffsetFetchRequestTopic>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new OffsetFetchRequestTopic(_readable, _version));
                    }
                    this.topics = newCollection;
                }
            }
        }
        if (_version >= 7) {
            this.requireStable = _readable.readByte() != 0;
        } else {
            this.requireStable = false;
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
        {
            byte[] _stringBytes = _cache.getSerializedValue(groupId);
            if (_version >= 6) {
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
            } else {
                _writable.writeShort((short) _stringBytes.length);
            }
            _writable.writeByteArray(_stringBytes);
        }
        if (_version >= 6) {
            if (topics == null) {
                _writable.writeUnsignedVarint(0);
            } else {
                _writable.writeUnsignedVarint(topics.size() + 1);
                for (OffsetFetchRequestTopic topicsElement : topics) {
                    topicsElement.write(_writable, _cache, _version);
                }
            }
        } else {
            if (topics == null) {
                if (_version >= 2) {
                    _writable.writeInt(-1);
                } else {
                    throw new NullPointerException();
                }
            } else {
                _writable.writeInt(topics.size());
                for (OffsetFetchRequestTopic topicsElement : topics) {
                    topicsElement.write(_writable, _cache, _version);
                }
            }
        }
        if (_version >= 7) {
            _writable.writeByte(requireStable ? (byte) 1 : (byte) 0);
        } else {
            if (requireStable) {
                throw new UnsupportedVersionException("Attempted to write a non-default requireStable at version " + _version);
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
        this.groupId = struct.getString("group_id");
        {
            Object[] _nestedObjects = struct.getArray("topics");
            if (_nestedObjects == null) {
                this.topics = null;
            } else {
                this.topics = new ArrayList<OffsetFetchRequestTopic>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.topics.add(new OffsetFetchRequestTopic((Struct) nestedObject, _version));
                }
            }
        }
        if (_version >= 7) {
            this.requireStable = struct.getBoolean("require_stable");
        } else {
            this.requireStable = false;
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
        struct.set("group_id", this.groupId);
        {
            if (topics == null) {
                struct.set("topics", null);
            } else {
                Struct[] _nestedObjects = new Struct[topics.size()];
                int i = 0;
                for (OffsetFetchRequestTopic element : this.topics) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("topics", (Object[]) _nestedObjects);
            }
        }
        if (_version >= 7) {
            struct.set("require_stable", this.requireStable);
        } else {
            if (requireStable) {
                throw new UnsupportedVersionException("Attempted to write a non-default requireStable at version " + _version);
            }
        }
        if (_version >= 6) {
            struct.set("_tagged_fields", _taggedFields);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        {
            byte[] _stringBytes = groupId.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'groupId' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(groupId, _stringBytes);
            if (_version >= 6) {
                _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
            } else {
                _size += _stringBytes.length + 2;
            }
        }
        if (topics == null) {
            if (_version >= 6) {
                _size += 1;
            } else {
                _size += 4;
            }
        } else {
            int _arraySize = 0;
            if (_version >= 6) {
                _arraySize += ByteUtils.sizeOfUnsignedVarint(topics.size() + 1);
            } else {
                _arraySize += 4;
            }
            for (OffsetFetchRequestTopic topicsElement : topics) {
                _arraySize += topicsElement.size(_cache, _version);
            }
            _size += _arraySize;
        }
        if (_version >= 7) {
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
        if (!(obj instanceof OffsetFetchRequestData)) return false;
        OffsetFetchRequestData other = (OffsetFetchRequestData) obj;
        if (this.groupId == null) {
            if (other.groupId != null) return false;
        } else {
            if (!this.groupId.equals(other.groupId)) return false;
        }
        if (this.topics == null) {
            if (other.topics != null) return false;
        } else {
            if (!this.topics.equals(other.topics)) return false;
        }
        if (requireStable != other.requireStable) return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (groupId == null ? 0 : groupId.hashCode());
        hashCode = 31 * hashCode + (topics == null ? 0 : topics.hashCode());
        hashCode = 31 * hashCode + (requireStable ? 1231 : 1237);
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "OffsetFetchRequestData("
            + "groupId=" + ((groupId == null) ? "null" : "'" + groupId.toString() + "'")
            + ", topics=" + ((topics == null) ? "null" : MessageUtil.deepToString(topics.iterator()))
            + ", requireStable=" + (requireStable ? "true" : "false")
            + ")";
    }
    
    public String groupId() {
        return this.groupId;
    }
    
    public List<OffsetFetchRequestTopic> topics() {
        return this.topics;
    }
    
    public boolean requireStable() {
        return this.requireStable;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public OffsetFetchRequestData setGroupId(String v) {
        this.groupId = v;
        return this;
    }
    
    public OffsetFetchRequestData setTopics(List<OffsetFetchRequestTopic> v) {
        this.topics = v;
        return this;
    }
    
    public OffsetFetchRequestData setRequireStable(boolean v) {
        this.requireStable = v;
        return this;
    }
    
    static public class OffsetFetchRequestTopic implements Message {
        private String name;
        private List<Integer> partitionIndexes;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partition_indexes", new ArrayOf(Type.INT32), "The partition indexes we would like to fetch offsets for.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 = SCHEMA_4;
        
        public static final Schema SCHEMA_6 =
            new Schema(
                new Field("name", Type.COMPACT_STRING, "The topic name."),
                new Field("partition_indexes", new CompactArrayOf(Type.INT32), "The partition indexes we would like to fetch offsets for."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema SCHEMA_7 = SCHEMA_6;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3,
            SCHEMA_4,
            SCHEMA_5,
            SCHEMA_6,
            SCHEMA_7
        };
        
        public OffsetFetchRequestTopic(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public OffsetFetchRequestTopic(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public OffsetFetchRequestTopic() {
            this.name = "";
            this.partitionIndexes = new ArrayList<Integer>();
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 7;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 7) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetFetchRequestTopic");
            }
            {
                int length;
                if (_version >= 6) {
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
                if (_version >= 6) {
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
            if (_version > 7) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of OffsetFetchRequestTopic");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                if (_version >= 6) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            if (_version >= 6) {
                _writable.writeUnsignedVarint(partitionIndexes.size() + 1);
            } else {
                _writable.writeInt(partitionIndexes.size());
            }
            for (Integer partitionIndexesElement : partitionIndexes) {
                _writable.writeInt(partitionIndexesElement);
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
            if (_version > 7) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetFetchRequestTopic");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 6) {
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
            if (_version > 7) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of OffsetFetchRequestTopic");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 6) {
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
            if (_version >= 6) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 7) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of OffsetFetchRequestTopic");
            }
            {
                byte[] _stringBytes = name.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'name' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(name, _stringBytes);
                if (_version >= 6) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
            }
            {
                int _arraySize = 0;
                if (_version >= 6) {
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
            if (!(obj instanceof OffsetFetchRequestTopic)) return false;
            OffsetFetchRequestTopic other = (OffsetFetchRequestTopic) obj;
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
            return "OffsetFetchRequestTopic("
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
        
        public OffsetFetchRequestTopic setName(String v) {
            this.name = v;
            return this;
        }
        
        public OffsetFetchRequestTopic setPartitionIndexes(List<Integer> v) {
            this.partitionIndexes = v;
            return this;
        }
    }
}
