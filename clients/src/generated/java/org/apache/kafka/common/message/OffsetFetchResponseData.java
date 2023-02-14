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


public class OffsetFetchResponseData implements ApiMessage {
    private int throttleTimeMs;
    private List<OffsetFetchResponseTopic> topics;
    private short errorCode;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("topics", new ArrayOf(OffsetFetchResponseTopic.SCHEMA_0), "The responses per topic.")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("topics", new ArrayOf(OffsetFetchResponseTopic.SCHEMA_0), "The responses per topic."),
            new Field("error_code", Type.INT16, "The top-level error code, or 0 if there was no error.")
        );
    
    public static final Schema SCHEMA_3 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("topics", new ArrayOf(OffsetFetchResponseTopic.SCHEMA_0), "The responses per topic."),
            new Field("error_code", Type.INT16, "The top-level error code, or 0 if there was no error.")
        );
    
    public static final Schema SCHEMA_4 = SCHEMA_3;
    
    public static final Schema SCHEMA_5 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("topics", new ArrayOf(OffsetFetchResponseTopic.SCHEMA_5), "The responses per topic."),
            new Field("error_code", Type.INT16, "The top-level error code, or 0 if there was no error.")
        );
    
    public static final Schema SCHEMA_6 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("topics", new CompactArrayOf(OffsetFetchResponseTopic.SCHEMA_6), "The responses per topic."),
            new Field("error_code", Type.INT16, "The top-level error code, or 0 if there was no error."),
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
    
    public OffsetFetchResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public OffsetFetchResponseData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public OffsetFetchResponseData() {
        this.throttleTimeMs = 0;
        this.topics = new ArrayList<OffsetFetchResponseTopic>();
        this.errorCode = (short) 0;
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
        if (_version >= 3) {
            this.throttleTimeMs = _readable.readInt();
        } else {
            this.throttleTimeMs = 0;
        }
        {
            if (_version >= 6) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topics was serialized as null");
                } else {
                    ArrayList<OffsetFetchResponseTopic> newCollection = new ArrayList<OffsetFetchResponseTopic>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new OffsetFetchResponseTopic(_readable, _version));
                    }
                    this.topics = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topics was serialized as null");
                } else {
                    ArrayList<OffsetFetchResponseTopic> newCollection = new ArrayList<OffsetFetchResponseTopic>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new OffsetFetchResponseTopic(_readable, _version));
                    }
                    this.topics = newCollection;
                }
            }
        }
        if (_version >= 2) {
            this.errorCode = _readable.readShort();
        } else {
            this.errorCode = (short) 0;
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
        if (_version >= 3) {
            _writable.writeInt(throttleTimeMs);
        }
        if (_version >= 6) {
            _writable.writeUnsignedVarint(topics.size() + 1);
            for (OffsetFetchResponseTopic topicsElement : topics) {
                topicsElement.write(_writable, _cache, _version);
            }
        } else {
            _writable.writeInt(topics.size());
            for (OffsetFetchResponseTopic topicsElement : topics) {
                topicsElement.write(_writable, _cache, _version);
            }
        }
        if (_version >= 2) {
            _writable.writeShort(errorCode);
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
        if (_version >= 3) {
            this.throttleTimeMs = struct.getInt("throttle_time_ms");
        } else {
            this.throttleTimeMs = 0;
        }
        {
            Object[] _nestedObjects = struct.getArray("topics");
            this.topics = new ArrayList<OffsetFetchResponseTopic>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.topics.add(new OffsetFetchResponseTopic((Struct) nestedObject, _version));
            }
        }
        if (_version >= 2) {
            this.errorCode = struct.getShort("error_code");
        } else {
            this.errorCode = (short) 0;
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
        if (_version >= 3) {
            struct.set("throttle_time_ms", this.throttleTimeMs);
        }
        {
            Struct[] _nestedObjects = new Struct[topics.size()];
            int i = 0;
            for (OffsetFetchResponseTopic element : this.topics) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("topics", (Object[]) _nestedObjects);
        }
        if (_version >= 2) {
            struct.set("error_code", this.errorCode);
        }
        if (_version >= 6) {
            struct.set("_tagged_fields", _taggedFields);
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
            if (_version >= 6) {
                _arraySize += ByteUtils.sizeOfUnsignedVarint(topics.size() + 1);
            } else {
                _arraySize += 4;
            }
            for (OffsetFetchResponseTopic topicsElement : topics) {
                _arraySize += topicsElement.size(_cache, _version);
            }
            _size += _arraySize;
        }
        if (_version >= 2) {
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
        if (!(obj instanceof OffsetFetchResponseData)) return false;
        OffsetFetchResponseData other = (OffsetFetchResponseData) obj;
        if (throttleTimeMs != other.throttleTimeMs) return false;
        if (this.topics == null) {
            if (other.topics != null) return false;
        } else {
            if (!this.topics.equals(other.topics)) return false;
        }
        if (errorCode != other.errorCode) return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + throttleTimeMs;
        hashCode = 31 * hashCode + (topics == null ? 0 : topics.hashCode());
        hashCode = 31 * hashCode + errorCode;
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "OffsetFetchResponseData("
            + "throttleTimeMs=" + throttleTimeMs
            + ", topics=" + MessageUtil.deepToString(topics.iterator())
            + ", errorCode=" + errorCode
            + ")";
    }
    
    public int throttleTimeMs() {
        return this.throttleTimeMs;
    }
    
    public List<OffsetFetchResponseTopic> topics() {
        return this.topics;
    }
    
    public short errorCode() {
        return this.errorCode;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public OffsetFetchResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
    
    public OffsetFetchResponseData setTopics(List<OffsetFetchResponseTopic> v) {
        this.topics = v;
        return this;
    }
    
    public OffsetFetchResponseData setErrorCode(short v) {
        this.errorCode = v;
        return this;
    }
    
    static public class OffsetFetchResponseTopic implements Message {
        private String name;
        private List<OffsetFetchResponsePartition> partitions;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partitions", new ArrayOf(OffsetFetchResponsePartition.SCHEMA_0), "The responses per partition")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partitions", new ArrayOf(OffsetFetchResponsePartition.SCHEMA_5), "The responses per partition")
            );
        
        public static final Schema SCHEMA_6 =
            new Schema(
                new Field("name", Type.COMPACT_STRING, "The topic name."),
                new Field("partitions", new CompactArrayOf(OffsetFetchResponsePartition.SCHEMA_6), "The responses per partition"),
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
        
        public OffsetFetchResponseTopic(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public OffsetFetchResponseTopic(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public OffsetFetchResponseTopic() {
            this.name = "";
            this.partitions = new ArrayList<OffsetFetchResponsePartition>();
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetFetchResponseTopic");
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
                if (_version >= 6) {
                    int arrayLength;
                    arrayLength = _readable.readUnsignedVarint() - 1;
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field partitions was serialized as null");
                    } else {
                        ArrayList<OffsetFetchResponsePartition> newCollection = new ArrayList<OffsetFetchResponsePartition>(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new OffsetFetchResponsePartition(_readable, _version));
                        }
                        this.partitions = newCollection;
                    }
                } else {
                    int arrayLength;
                    arrayLength = _readable.readInt();
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field partitions was serialized as null");
                    } else {
                        ArrayList<OffsetFetchResponsePartition> newCollection = new ArrayList<OffsetFetchResponsePartition>(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new OffsetFetchResponsePartition(_readable, _version));
                        }
                        this.partitions = newCollection;
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
            if (_version > 7) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of OffsetFetchResponseTopic");
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
                _writable.writeUnsignedVarint(partitions.size() + 1);
                for (OffsetFetchResponsePartition partitionsElement : partitions) {
                    partitionsElement.write(_writable, _cache, _version);
                }
            } else {
                _writable.writeInt(partitions.size());
                for (OffsetFetchResponsePartition partitionsElement : partitions) {
                    partitionsElement.write(_writable, _cache, _version);
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
            if (_version > 7) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetFetchResponseTopic");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 6) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.name = struct.getString("name");
            {
                Object[] _nestedObjects = struct.getArray("partitions");
                this.partitions = new ArrayList<OffsetFetchResponsePartition>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.partitions.add(new OffsetFetchResponsePartition((Struct) nestedObject, _version));
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
                throw new UnsupportedVersionException("Can't write version " + _version + " of OffsetFetchResponseTopic");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 6) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("name", this.name);
            {
                Struct[] _nestedObjects = new Struct[partitions.size()];
                int i = 0;
                for (OffsetFetchResponsePartition element : this.partitions) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("partitions", (Object[]) _nestedObjects);
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
                throw new UnsupportedVersionException("Can't size version " + _version + " of OffsetFetchResponseTopic");
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
                    _arraySize += ByteUtils.sizeOfUnsignedVarint(partitions.size() + 1);
                } else {
                    _arraySize += 4;
                }
                for (OffsetFetchResponsePartition partitionsElement : partitions) {
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
            if (!(obj instanceof OffsetFetchResponseTopic)) return false;
            OffsetFetchResponseTopic other = (OffsetFetchResponseTopic) obj;
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
            return "OffsetFetchResponseTopic("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", partitions=" + MessageUtil.deepToString(partitions.iterator())
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public List<OffsetFetchResponsePartition> partitions() {
            return this.partitions;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public OffsetFetchResponseTopic setName(String v) {
            this.name = v;
            return this;
        }
        
        public OffsetFetchResponseTopic setPartitions(List<OffsetFetchResponsePartition> v) {
            this.partitions = v;
            return this;
        }
    }
    
    static public class OffsetFetchResponsePartition implements Message {
        private int partitionIndex;
        private long committedOffset;
        private int committedLeaderEpoch;
        private String metadata;
        private short errorCode;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("committed_offset", Type.INT64, "The committed message offset."),
                new Field("metadata", Type.NULLABLE_STRING, "The partition metadata."),
                new Field("error_code", Type.INT16, "The error code, or 0 if there was no error.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("committed_offset", Type.INT64, "The committed message offset."),
                new Field("committed_leader_epoch", Type.INT32, "The leader epoch."),
                new Field("metadata", Type.NULLABLE_STRING, "The partition metadata."),
                new Field("error_code", Type.INT16, "The error code, or 0 if there was no error.")
            );
        
        public static final Schema SCHEMA_6 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("committed_offset", Type.INT64, "The committed message offset."),
                new Field("committed_leader_epoch", Type.INT32, "The leader epoch."),
                new Field("metadata", Type.COMPACT_NULLABLE_STRING, "The partition metadata."),
                new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
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
        
        public OffsetFetchResponsePartition(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public OffsetFetchResponsePartition(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public OffsetFetchResponsePartition() {
            this.partitionIndex = 0;
            this.committedOffset = 0L;
            this.committedLeaderEpoch = -1;
            this.metadata = "";
            this.errorCode = (short) 0;
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetFetchResponsePartition");
            }
            this.partitionIndex = _readable.readInt();
            this.committedOffset = _readable.readLong();
            if (_version >= 5) {
                this.committedLeaderEpoch = _readable.readInt();
            } else {
                this.committedLeaderEpoch = -1;
            }
            {
                int length;
                if (_version >= 6) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    this.metadata = null;
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field metadata had invalid length " + length);
                } else {
                    this.metadata = _readable.readString(length);
                }
            }
            this.errorCode = _readable.readShort();
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
                throw new UnsupportedVersionException("Can't write version " + _version + " of OffsetFetchResponsePartition");
            }
            int _numTaggedFields = 0;
            _writable.writeInt(partitionIndex);
            _writable.writeLong(committedOffset);
            if (_version >= 5) {
                _writable.writeInt(committedLeaderEpoch);
            }
            if (metadata == null) {
                if (_version >= 6) {
                    _writable.writeUnsignedVarint(0);
                } else {
                    _writable.writeShort((short) -1);
                }
            } else {
                byte[] _stringBytes = _cache.getSerializedValue(metadata);
                if (_version >= 6) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeShort(errorCode);
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetFetchResponsePartition");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 6) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.partitionIndex = struct.getInt("partition_index");
            this.committedOffset = struct.getLong("committed_offset");
            if (_version >= 5) {
                this.committedLeaderEpoch = struct.getInt("committed_leader_epoch");
            } else {
                this.committedLeaderEpoch = -1;
            }
            this.metadata = struct.getString("metadata");
            this.errorCode = struct.getShort("error_code");
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
                throw new UnsupportedVersionException("Can't write version " + _version + " of OffsetFetchResponsePartition");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 6) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("partition_index", this.partitionIndex);
            struct.set("committed_offset", this.committedOffset);
            if (_version >= 5) {
                struct.set("committed_leader_epoch", this.committedLeaderEpoch);
            }
            struct.set("metadata", this.metadata);
            struct.set("error_code", this.errorCode);
            if (_version >= 6) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 7) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of OffsetFetchResponsePartition");
            }
            _size += 4;
            _size += 8;
            if (_version >= 5) {
                _size += 4;
            }
            if (metadata == null) {
                if (_version >= 6) {
                    _size += 1;
                } else {
                    _size += 2;
                }
            } else {
                byte[] _stringBytes = metadata.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'metadata' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(metadata, _stringBytes);
                if (_version >= 6) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
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
            if (!(obj instanceof OffsetFetchResponsePartition)) return false;
            OffsetFetchResponsePartition other = (OffsetFetchResponsePartition) obj;
            if (partitionIndex != other.partitionIndex) return false;
            if (committedOffset != other.committedOffset) return false;
            if (committedLeaderEpoch != other.committedLeaderEpoch) return false;
            if (this.metadata == null) {
                if (other.metadata != null) return false;
            } else {
                if (!this.metadata.equals(other.metadata)) return false;
            }
            if (errorCode != other.errorCode) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + partitionIndex;
            hashCode = 31 * hashCode + ((int) (committedOffset >> 32) ^ (int) committedOffset);
            hashCode = 31 * hashCode + committedLeaderEpoch;
            hashCode = 31 * hashCode + (metadata == null ? 0 : metadata.hashCode());
            hashCode = 31 * hashCode + errorCode;
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "OffsetFetchResponsePartition("
                + "partitionIndex=" + partitionIndex
                + ", committedOffset=" + committedOffset
                + ", committedLeaderEpoch=" + committedLeaderEpoch
                + ", metadata=" + ((metadata == null) ? "null" : "'" + metadata.toString() + "'")
                + ", errorCode=" + errorCode
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
        
        public String metadata() {
            return this.metadata;
        }
        
        public short errorCode() {
            return this.errorCode;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public OffsetFetchResponsePartition setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
        
        public OffsetFetchResponsePartition setCommittedOffset(long v) {
            this.committedOffset = v;
            return this;
        }
        
        public OffsetFetchResponsePartition setCommittedLeaderEpoch(int v) {
            this.committedLeaderEpoch = v;
            return this;
        }
        
        public OffsetFetchResponsePartition setMetadata(String v) {
            this.metadata = v;
            return this;
        }
        
        public OffsetFetchResponsePartition setErrorCode(short v) {
            this.errorCode = v;
            return this;
        }
    }
}
