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
import java.util.Iterator;
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
import org.apache.kafka.common.utils.ImplicitLinkedHashCollection;
import org.apache.kafka.common.utils.ImplicitLinkedHashMultiCollection;


public class OffsetDeleteResponseData implements ApiMessage {
    private short errorCode;
    private int throttleTimeMs;
    private OffsetDeleteResponseTopicCollection topics;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("error_code", Type.INT16, "The top-level error code, or 0 if there was no error."),
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("topics", new ArrayOf(OffsetDeleteResponseTopic.SCHEMA_0), "The responses for each topic.")
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0
    };
    
    public OffsetDeleteResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public OffsetDeleteResponseData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public OffsetDeleteResponseData() {
        this.errorCode = (short) 0;
        this.throttleTimeMs = 0;
        this.topics = new OffsetDeleteResponseTopicCollection(0);
    }
    
    @Override
    public short apiKey() {
        return 47;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 0;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        this.errorCode = _readable.readShort();
        this.throttleTimeMs = _readable.readInt();
        {
            int arrayLength;
            arrayLength = _readable.readInt();
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field topics was serialized as null");
            } else {
                OffsetDeleteResponseTopicCollection newCollection = new OffsetDeleteResponseTopicCollection(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new OffsetDeleteResponseTopic(_readable, _version));
                }
                this.topics = newCollection;
            }
        }
        this._unknownTaggedFields = null;
    }
    
    @Override
    public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        _writable.writeShort(errorCode);
        _writable.writeInt(throttleTimeMs);
        _writable.writeInt(topics.size());
        for (OffsetDeleteResponseTopic topicsElement : topics) {
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
        this.errorCode = struct.getShort("error_code");
        this.throttleTimeMs = struct.getInt("throttle_time_ms");
        {
            Object[] _nestedObjects = struct.getArray("topics");
            this.topics = new OffsetDeleteResponseTopicCollection(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.topics.add(new OffsetDeleteResponseTopic((Struct) nestedObject, _version));
            }
        }
    }
    
    @Override
    public Struct toStruct(short _version) {
        TreeMap<Integer, Object> _taggedFields = null;
        Struct struct = new Struct(SCHEMAS[_version]);
        struct.set("error_code", this.errorCode);
        struct.set("throttle_time_ms", this.throttleTimeMs);
        {
            Struct[] _nestedObjects = new Struct[topics.size()];
            int i = 0;
            for (OffsetDeleteResponseTopic element : this.topics) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("topics", (Object[]) _nestedObjects);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        _size += 2;
        _size += 4;
        {
            int _arraySize = 0;
            _arraySize += 4;
            for (OffsetDeleteResponseTopic topicsElement : topics) {
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
        if (!(obj instanceof OffsetDeleteResponseData)) return false;
        OffsetDeleteResponseData other = (OffsetDeleteResponseData) obj;
        if (errorCode != other.errorCode) return false;
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
        hashCode = 31 * hashCode + errorCode;
        hashCode = 31 * hashCode + throttleTimeMs;
        hashCode = 31 * hashCode + (topics == null ? 0 : topics.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "OffsetDeleteResponseData("
            + "errorCode=" + errorCode
            + ", throttleTimeMs=" + throttleTimeMs
            + ", topics=" + MessageUtil.deepToString(topics.iterator())
            + ")";
    }
    
    public short errorCode() {
        return this.errorCode;
    }
    
    public int throttleTimeMs() {
        return this.throttleTimeMs;
    }
    
    public OffsetDeleteResponseTopicCollection topics() {
        return this.topics;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public OffsetDeleteResponseData setErrorCode(short v) {
        this.errorCode = v;
        return this;
    }
    
    public OffsetDeleteResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
    
    public OffsetDeleteResponseData setTopics(OffsetDeleteResponseTopicCollection v) {
        this.topics = v;
        return this;
    }
    
    static public class OffsetDeleteResponseTopic implements Message, ImplicitLinkedHashMultiCollection.Element {
        private String name;
        private OffsetDeleteResponsePartitionCollection partitions;
        private List<RawTaggedField> _unknownTaggedFields;
        private int next;
        private int prev;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partitions", new ArrayOf(OffsetDeleteResponsePartition.SCHEMA_0), "The responses for each partition in the topic.")
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0
        };
        
        public OffsetDeleteResponseTopic(Readable _readable, short _version) {
            read(_readable, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public OffsetDeleteResponseTopic(Struct struct, short _version) {
            fromStruct(struct, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public OffsetDeleteResponseTopic() {
            this.name = "";
            this.partitions = new OffsetDeleteResponsePartitionCollection(0);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 0;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetDeleteResponseTopic");
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
                    OffsetDeleteResponsePartitionCollection newCollection = new OffsetDeleteResponsePartitionCollection(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new OffsetDeleteResponsePartition(_readable, _version));
                    }
                    this.partitions = newCollection;
                }
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of OffsetDeleteResponseTopic");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                _writable.writeShort((short) _stringBytes.length);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeInt(partitions.size());
            for (OffsetDeleteResponsePartition partitionsElement : partitions) {
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
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetDeleteResponseTopic");
            }
            this._unknownTaggedFields = null;
            this.name = struct.getString("name");
            {
                Object[] _nestedObjects = struct.getArray("partitions");
                this.partitions = new OffsetDeleteResponsePartitionCollection(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.partitions.add(new OffsetDeleteResponsePartition((Struct) nestedObject, _version));
                }
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of OffsetDeleteResponseTopic");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("name", this.name);
            {
                Struct[] _nestedObjects = new Struct[partitions.size()];
                int i = 0;
                for (OffsetDeleteResponsePartition element : this.partitions) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("partitions", (Object[]) _nestedObjects);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of OffsetDeleteResponseTopic");
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
                for (OffsetDeleteResponsePartition partitionsElement : partitions) {
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
            if (!(obj instanceof OffsetDeleteResponseTopic)) return false;
            OffsetDeleteResponseTopic other = (OffsetDeleteResponseTopic) obj;
            if (this.name == null) {
                if (other.name != null) return false;
            } else {
                if (!this.name.equals(other.name)) return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (name == null ? 0 : name.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "OffsetDeleteResponseTopic("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", partitions=" + MessageUtil.deepToString(partitions.iterator())
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public OffsetDeleteResponsePartitionCollection partitions() {
            return this.partitions;
        }
        
        @Override
        public int next() {
            return this.next;
        }
        
        @Override
        public int prev() {
            return this.prev;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public OffsetDeleteResponseTopic setName(String v) {
            this.name = v;
            return this;
        }
        
        public OffsetDeleteResponseTopic setPartitions(OffsetDeleteResponsePartitionCollection v) {
            this.partitions = v;
            return this;
        }
        
        @Override
        public void setNext(int v) {
            this.next = v;
        }
        
        @Override
        public void setPrev(int v) {
            this.prev = v;
        }
    }
    
    static public class OffsetDeleteResponsePartition implements Message, ImplicitLinkedHashMultiCollection.Element {
        private int partitionIndex;
        private short errorCode;
        private List<RawTaggedField> _unknownTaggedFields;
        private int next;
        private int prev;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("error_code", Type.INT16, "The error code, or 0 if there was no error.")
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0
        };
        
        public OffsetDeleteResponsePartition(Readable _readable, short _version) {
            read(_readable, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public OffsetDeleteResponsePartition(Struct struct, short _version) {
            fromStruct(struct, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public OffsetDeleteResponsePartition() {
            this.partitionIndex = 0;
            this.errorCode = (short) 0;
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 0;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetDeleteResponsePartition");
            }
            this.partitionIndex = _readable.readInt();
            this.errorCode = _readable.readShort();
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of OffsetDeleteResponsePartition");
            }
            int _numTaggedFields = 0;
            _writable.writeInt(partitionIndex);
            _writable.writeShort(errorCode);
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public void fromStruct(Struct struct, short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetDeleteResponsePartition");
            }
            this._unknownTaggedFields = null;
            this.partitionIndex = struct.getInt("partition_index");
            this.errorCode = struct.getShort("error_code");
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of OffsetDeleteResponsePartition");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("partition_index", this.partitionIndex);
            struct.set("error_code", this.errorCode);
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of OffsetDeleteResponsePartition");
            }
            _size += 4;
            _size += 2;
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
            if (!(obj instanceof OffsetDeleteResponsePartition)) return false;
            OffsetDeleteResponsePartition other = (OffsetDeleteResponsePartition) obj;
            if (partitionIndex != other.partitionIndex) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + partitionIndex;
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "OffsetDeleteResponsePartition("
                + "partitionIndex=" + partitionIndex
                + ", errorCode=" + errorCode
                + ")";
        }
        
        public int partitionIndex() {
            return this.partitionIndex;
        }
        
        public short errorCode() {
            return this.errorCode;
        }
        
        @Override
        public int next() {
            return this.next;
        }
        
        @Override
        public int prev() {
            return this.prev;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public OffsetDeleteResponsePartition setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
        
        public OffsetDeleteResponsePartition setErrorCode(short v) {
            this.errorCode = v;
            return this;
        }
        
        @Override
        public void setNext(int v) {
            this.next = v;
        }
        
        @Override
        public void setPrev(int v) {
            this.prev = v;
        }
    }
    
    public static class OffsetDeleteResponsePartitionCollection extends ImplicitLinkedHashMultiCollection<OffsetDeleteResponsePartition> {
        public OffsetDeleteResponsePartitionCollection() {
            super();
        }
        
        public OffsetDeleteResponsePartitionCollection(int expectedNumElements) {
            super(expectedNumElements);
        }
        
        public OffsetDeleteResponsePartitionCollection(Iterator<OffsetDeleteResponsePartition> iterator) {
            super(iterator);
        }
        
        public OffsetDeleteResponsePartition find(int partitionIndex) {
            OffsetDeleteResponsePartition _key = new OffsetDeleteResponsePartition();
            _key.setPartitionIndex(partitionIndex);
            return find(_key);
        }
        
        public List<OffsetDeleteResponsePartition> findAll(int partitionIndex) {
            OffsetDeleteResponsePartition _key = new OffsetDeleteResponsePartition();
            _key.setPartitionIndex(partitionIndex);
            return findAll(_key);
        }
        
    }
    
    public static class OffsetDeleteResponseTopicCollection extends ImplicitLinkedHashMultiCollection<OffsetDeleteResponseTopic> {
        public OffsetDeleteResponseTopicCollection() {
            super();
        }
        
        public OffsetDeleteResponseTopicCollection(int expectedNumElements) {
            super(expectedNumElements);
        }
        
        public OffsetDeleteResponseTopicCollection(Iterator<OffsetDeleteResponseTopic> iterator) {
            super(iterator);
        }
        
        public OffsetDeleteResponseTopic find(String name) {
            OffsetDeleteResponseTopic _key = new OffsetDeleteResponseTopic();
            _key.setName(name);
            return find(_key);
        }
        
        public List<OffsetDeleteResponseTopic> findAll(String name) {
            OffsetDeleteResponseTopic _key = new OffsetDeleteResponseTopic();
            _key.setName(name);
            return findAll(_key);
        }
        
    }
}
