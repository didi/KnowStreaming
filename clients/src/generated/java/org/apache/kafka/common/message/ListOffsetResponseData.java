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


public class ListOffsetResponseData implements ApiMessage {
    private int throttleTimeMs;
    private List<ListOffsetTopicResponse> topics;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("topics", new ArrayOf(ListOffsetTopicResponse.SCHEMA_0), "Each topic in the response.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("topics", new ArrayOf(ListOffsetTopicResponse.SCHEMA_1), "Each topic in the response.")
        );
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("topics", new ArrayOf(ListOffsetTopicResponse.SCHEMA_1), "Each topic in the response.")
        );
    
    public static final Schema SCHEMA_3 = SCHEMA_2;
    
    public static final Schema SCHEMA_4 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("topics", new ArrayOf(ListOffsetTopicResponse.SCHEMA_4), "Each topic in the response.")
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
    
    public ListOffsetResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public ListOffsetResponseData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public ListOffsetResponseData() {
        this.throttleTimeMs = 0;
        this.topics = new ArrayList<ListOffsetTopicResponse>();
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
                ArrayList<ListOffsetTopicResponse> newCollection = new ArrayList<ListOffsetTopicResponse>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new ListOffsetTopicResponse(_readable, _version));
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
        for (ListOffsetTopicResponse topicsElement : topics) {
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
            this.topics = new ArrayList<ListOffsetTopicResponse>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.topics.add(new ListOffsetTopicResponse((Struct) nestedObject, _version));
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
            for (ListOffsetTopicResponse element : this.topics) {
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
            for (ListOffsetTopicResponse topicsElement : topics) {
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
        if (!(obj instanceof ListOffsetResponseData)) return false;
        ListOffsetResponseData other = (ListOffsetResponseData) obj;
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
        return "ListOffsetResponseData("
            + "throttleTimeMs=" + throttleTimeMs
            + ", topics=" + MessageUtil.deepToString(topics.iterator())
            + ")";
    }
    
    public int throttleTimeMs() {
        return this.throttleTimeMs;
    }
    
    public List<ListOffsetTopicResponse> topics() {
        return this.topics;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public ListOffsetResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
    
    public ListOffsetResponseData setTopics(List<ListOffsetTopicResponse> v) {
        this.topics = v;
        return this;
    }
    
    static public class ListOffsetTopicResponse implements Message {
        private String name;
        private List<ListOffsetPartitionResponse> partitions;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.STRING, "The topic name"),
                new Field("partitions", new ArrayOf(ListOffsetPartitionResponse.SCHEMA_0), "Each partition in the response.")
            );
        
        public static final Schema SCHEMA_1 =
            new Schema(
                new Field("name", Type.STRING, "The topic name"),
                new Field("partitions", new ArrayOf(ListOffsetPartitionResponse.SCHEMA_1), "Each partition in the response.")
            );
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 =
            new Schema(
                new Field("name", Type.STRING, "The topic name"),
                new Field("partitions", new ArrayOf(ListOffsetPartitionResponse.SCHEMA_4), "Each partition in the response.")
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
        
        public ListOffsetTopicResponse(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public ListOffsetTopicResponse(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public ListOffsetTopicResponse() {
            this.name = "";
            this.partitions = new ArrayList<ListOffsetPartitionResponse>();
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of ListOffsetTopicResponse");
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
                    ArrayList<ListOffsetPartitionResponse> newCollection = new ArrayList<ListOffsetPartitionResponse>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new ListOffsetPartitionResponse(_readable, _version));
                    }
                    this.partitions = newCollection;
                }
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of ListOffsetTopicResponse");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                _writable.writeShort((short) _stringBytes.length);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeInt(partitions.size());
            for (ListOffsetPartitionResponse partitionsElement : partitions) {
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of ListOffsetTopicResponse");
            }
            this._unknownTaggedFields = null;
            this.name = struct.getString("name");
            {
                Object[] _nestedObjects = struct.getArray("partitions");
                this.partitions = new ArrayList<ListOffsetPartitionResponse>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.partitions.add(new ListOffsetPartitionResponse((Struct) nestedObject, _version));
                }
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of ListOffsetTopicResponse");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("name", this.name);
            {
                Struct[] _nestedObjects = new Struct[partitions.size()];
                int i = 0;
                for (ListOffsetPartitionResponse element : this.partitions) {
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
                throw new UnsupportedVersionException("Can't size version " + _version + " of ListOffsetTopicResponse");
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
                for (ListOffsetPartitionResponse partitionsElement : partitions) {
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
            if (!(obj instanceof ListOffsetTopicResponse)) return false;
            ListOffsetTopicResponse other = (ListOffsetTopicResponse) obj;
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
            return "ListOffsetTopicResponse("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", partitions=" + MessageUtil.deepToString(partitions.iterator())
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public List<ListOffsetPartitionResponse> partitions() {
            return this.partitions;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public ListOffsetTopicResponse setName(String v) {
            this.name = v;
            return this;
        }
        
        public ListOffsetTopicResponse setPartitions(List<ListOffsetPartitionResponse> v) {
            this.partitions = v;
            return this;
        }
    }
    
    static public class ListOffsetPartitionResponse implements Message {
        private int partitionIndex;
        private short errorCode;
        private List<Long> oldStyleOffsets;
        private long timestamp;
        private long offset;
        private int leaderEpoch;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("error_code", Type.INT16, "The partition error code, or 0 if there was no error."),
                new Field("old_style_offsets", new ArrayOf(Type.INT64), "The result offsets.")
            );
        
        public static final Schema SCHEMA_1 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("error_code", Type.INT16, "The partition error code, or 0 if there was no error."),
                new Field("timestamp", Type.INT64, "The timestamp associated with the returned offset."),
                new Field("offset", Type.INT64, "The returned offset.")
            );
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("error_code", Type.INT16, "The partition error code, or 0 if there was no error."),
                new Field("timestamp", Type.INT64, "The timestamp associated with the returned offset."),
                new Field("offset", Type.INT64, "The returned offset."),
                new Field("leader_epoch", Type.INT32, "")
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
        
        public ListOffsetPartitionResponse(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public ListOffsetPartitionResponse(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public ListOffsetPartitionResponse() {
            this.partitionIndex = 0;
            this.errorCode = (short) 0;
            this.oldStyleOffsets = new ArrayList<Long>();
            this.timestamp = -1L;
            this.offset = -1L;
            this.leaderEpoch = 0;
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of ListOffsetPartitionResponse");
            }
            this.partitionIndex = _readable.readInt();
            this.errorCode = _readable.readShort();
            if (_version <= 0) {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field oldStyleOffsets was serialized as null");
                } else {
                    ArrayList<Long> newCollection = new ArrayList<Long>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(_readable.readLong());
                    }
                    this.oldStyleOffsets = newCollection;
                }
            } else {
                this.oldStyleOffsets = new ArrayList<Long>();
            }
            if (_version >= 1) {
                this.timestamp = _readable.readLong();
            } else {
                this.timestamp = -1L;
            }
            if (_version >= 1) {
                this.offset = _readable.readLong();
            } else {
                this.offset = -1L;
            }
            if (_version >= 4) {
                this.leaderEpoch = _readable.readInt();
            } else {
                this.leaderEpoch = 0;
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of ListOffsetPartitionResponse");
            }
            int _numTaggedFields = 0;
            _writable.writeInt(partitionIndex);
            _writable.writeShort(errorCode);
            if (_version <= 0) {
                _writable.writeInt(oldStyleOffsets.size());
                for (Long oldStyleOffsetsElement : oldStyleOffsets) {
                    _writable.writeLong(oldStyleOffsetsElement);
                }
            } else {
                if (!oldStyleOffsets.isEmpty()) {
                    throw new UnsupportedVersionException("Attempted to write a non-default oldStyleOffsets at version " + _version);
                }
            }
            if (_version >= 1) {
                _writable.writeLong(timestamp);
            } else {
                if (timestamp != -1L) {
                    throw new UnsupportedVersionException("Attempted to write a non-default timestamp at version " + _version);
                }
            }
            if (_version >= 1) {
                _writable.writeLong(offset);
            } else {
                if (offset != -1L) {
                    throw new UnsupportedVersionException("Attempted to write a non-default offset at version " + _version);
                }
            }
            if (_version >= 4) {
                _writable.writeInt(leaderEpoch);
            } else {
                if (leaderEpoch != 0) {
                    throw new UnsupportedVersionException("Attempted to write a non-default leaderEpoch at version " + _version);
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of ListOffsetPartitionResponse");
            }
            this._unknownTaggedFields = null;
            this.partitionIndex = struct.getInt("partition_index");
            this.errorCode = struct.getShort("error_code");
            if (_version <= 0) {
                Object[] _nestedObjects = struct.getArray("old_style_offsets");
                this.oldStyleOffsets = new ArrayList<Long>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.oldStyleOffsets.add((Long) nestedObject);
                }
            } else {
                this.oldStyleOffsets = new ArrayList<Long>();
            }
            if (_version >= 1) {
                this.timestamp = struct.getLong("timestamp");
            } else {
                this.timestamp = -1L;
            }
            if (_version >= 1) {
                this.offset = struct.getLong("offset");
            } else {
                this.offset = -1L;
            }
            if (_version >= 4) {
                this.leaderEpoch = struct.getInt("leader_epoch");
            } else {
                this.leaderEpoch = 0;
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of ListOffsetPartitionResponse");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("partition_index", this.partitionIndex);
            struct.set("error_code", this.errorCode);
            if (_version <= 0) {
                Long[] _nestedObjects = new Long[oldStyleOffsets.size()];
                int i = 0;
                for (Long element : this.oldStyleOffsets) {
                    _nestedObjects[i++] = element;
                }
                struct.set("old_style_offsets", (Object[]) _nestedObjects);
            } else {
                if (!oldStyleOffsets.isEmpty()) {
                    throw new UnsupportedVersionException("Attempted to write a non-default oldStyleOffsets at version " + _version);
                }
            }
            if (_version >= 1) {
                struct.set("timestamp", this.timestamp);
            } else {
                if (timestamp != -1L) {
                    throw new UnsupportedVersionException("Attempted to write a non-default timestamp at version " + _version);
                }
            }
            if (_version >= 1) {
                struct.set("offset", this.offset);
            } else {
                if (offset != -1L) {
                    throw new UnsupportedVersionException("Attempted to write a non-default offset at version " + _version);
                }
            }
            if (_version >= 4) {
                struct.set("leader_epoch", this.leaderEpoch);
            } else {
                if (leaderEpoch != 0) {
                    throw new UnsupportedVersionException("Attempted to write a non-default leaderEpoch at version " + _version);
                }
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of ListOffsetPartitionResponse");
            }
            _size += 4;
            _size += 2;
            if (_version <= 0) {
                {
                    int _arraySize = 0;
                    _arraySize += 4;
                    _arraySize += oldStyleOffsets.size() * 8;
                    _size += _arraySize;
                }
            }
            if (_version >= 1) {
                _size += 8;
            }
            if (_version >= 1) {
                _size += 8;
            }
            if (_version >= 4) {
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
            if (!(obj instanceof ListOffsetPartitionResponse)) return false;
            ListOffsetPartitionResponse other = (ListOffsetPartitionResponse) obj;
            if (partitionIndex != other.partitionIndex) return false;
            if (errorCode != other.errorCode) return false;
            if (this.oldStyleOffsets == null) {
                if (other.oldStyleOffsets != null) return false;
            } else {
                if (!this.oldStyleOffsets.equals(other.oldStyleOffsets)) return false;
            }
            if (timestamp != other.timestamp) return false;
            if (offset != other.offset) return false;
            if (leaderEpoch != other.leaderEpoch) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + partitionIndex;
            hashCode = 31 * hashCode + errorCode;
            hashCode = 31 * hashCode + (oldStyleOffsets == null ? 0 : oldStyleOffsets.hashCode());
            hashCode = 31 * hashCode + ((int) (timestamp >> 32) ^ (int) timestamp);
            hashCode = 31 * hashCode + ((int) (offset >> 32) ^ (int) offset);
            hashCode = 31 * hashCode + leaderEpoch;
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "ListOffsetPartitionResponse("
                + "partitionIndex=" + partitionIndex
                + ", errorCode=" + errorCode
                + ", oldStyleOffsets=" + MessageUtil.deepToString(oldStyleOffsets.iterator())
                + ", timestamp=" + timestamp
                + ", offset=" + offset
                + ", leaderEpoch=" + leaderEpoch
                + ")";
        }
        
        public int partitionIndex() {
            return this.partitionIndex;
        }
        
        public short errorCode() {
            return this.errorCode;
        }
        
        public List<Long> oldStyleOffsets() {
            return this.oldStyleOffsets;
        }
        
        public long timestamp() {
            return this.timestamp;
        }
        
        public long offset() {
            return this.offset;
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
        
        public ListOffsetPartitionResponse setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
        
        public ListOffsetPartitionResponse setErrorCode(short v) {
            this.errorCode = v;
            return this;
        }
        
        public ListOffsetPartitionResponse setOldStyleOffsets(List<Long> v) {
            this.oldStyleOffsets = v;
            return this;
        }
        
        public ListOffsetPartitionResponse setTimestamp(long v) {
            this.timestamp = v;
            return this;
        }
        
        public ListOffsetPartitionResponse setOffset(long v) {
            this.offset = v;
            return this;
        }
        
        public ListOffsetPartitionResponse setLeaderEpoch(int v) {
            this.leaderEpoch = v;
            return this;
        }
    }
}
