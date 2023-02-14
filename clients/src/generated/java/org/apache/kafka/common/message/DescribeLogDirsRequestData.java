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


public class DescribeLogDirsRequestData implements ApiMessage {
    private List<DescribableLogDirTopic> topics;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("topics", ArrayOf.nullable(DescribableLogDirTopic.SCHEMA_0), "Each topic that we want to describe log directories for, or null for all topics.")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1
    };
    
    public DescribeLogDirsRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public DescribeLogDirsRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public DescribeLogDirsRequestData() {
        this.topics = new ArrayList<DescribableLogDirTopic>();
    }
    
    @Override
    public short apiKey() {
        return 35;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 1;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        {
            int arrayLength;
            arrayLength = _readable.readInt();
            if (arrayLength < 0) {
                this.topics = null;
            } else {
                ArrayList<DescribableLogDirTopic> newCollection = new ArrayList<DescribableLogDirTopic>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new DescribableLogDirTopic(_readable, _version));
                }
                this.topics = newCollection;
            }
        }
        this._unknownTaggedFields = null;
    }
    
    @Override
    public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        if (topics == null) {
            _writable.writeInt(-1);
        } else {
            _writable.writeInt(topics.size());
            for (DescribableLogDirTopic topicsElement : topics) {
                topicsElement.write(_writable, _cache, _version);
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
        this._unknownTaggedFields = null;
        {
            Object[] _nestedObjects = struct.getArray("topics");
            if (_nestedObjects == null) {
                this.topics = null;
            } else {
                this.topics = new ArrayList<DescribableLogDirTopic>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.topics.add(new DescribableLogDirTopic((Struct) nestedObject, _version));
                }
            }
        }
    }
    
    @Override
    public Struct toStruct(short _version) {
        TreeMap<Integer, Object> _taggedFields = null;
        Struct struct = new Struct(SCHEMAS[_version]);
        {
            if (topics == null) {
                struct.set("topics", null);
            } else {
                Struct[] _nestedObjects = new Struct[topics.size()];
                int i = 0;
                for (DescribableLogDirTopic element : this.topics) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("topics", (Object[]) _nestedObjects);
            }
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        if (topics == null) {
            _size += 4;
        } else {
            int _arraySize = 0;
            _arraySize += 4;
            for (DescribableLogDirTopic topicsElement : topics) {
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
        if (!(obj instanceof DescribeLogDirsRequestData)) return false;
        DescribeLogDirsRequestData other = (DescribeLogDirsRequestData) obj;
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
        hashCode = 31 * hashCode + (topics == null ? 0 : topics.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "DescribeLogDirsRequestData("
            + "topics=" + ((topics == null) ? "null" : MessageUtil.deepToString(topics.iterator()))
            + ")";
    }
    
    public List<DescribableLogDirTopic> topics() {
        return this.topics;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public DescribeLogDirsRequestData setTopics(List<DescribableLogDirTopic> v) {
        this.topics = v;
        return this;
    }
    
    static public class DescribableLogDirTopic implements Message {
        private String topic;
        private List<Integer> partitionIndex;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("topic", Type.STRING, "The topic name"),
                new Field("partition_index", new ArrayOf(Type.INT32), "The partition indxes.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1
        };
        
        public DescribableLogDirTopic(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public DescribableLogDirTopic(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public DescribableLogDirTopic() {
            this.topic = "";
            this.partitionIndex = new ArrayList<Integer>();
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 1;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of DescribableLogDirTopic");
            }
            {
                int length;
                length = _readable.readShort();
                if (length < 0) {
                    throw new RuntimeException("non-nullable field topic was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field topic had invalid length " + length);
                } else {
                    this.topic = _readable.readString(length);
                }
            }
            {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field partitionIndex was serialized as null");
                } else {
                    ArrayList<Integer> newCollection = new ArrayList<Integer>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(_readable.readInt());
                    }
                    this.partitionIndex = newCollection;
                }
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of DescribableLogDirTopic");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(topic);
                _writable.writeShort((short) _stringBytes.length);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeInt(partitionIndex.size());
            for (Integer partitionIndexElement : partitionIndex) {
                _writable.writeInt(partitionIndexElement);
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
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of DescribableLogDirTopic");
            }
            this._unknownTaggedFields = null;
            this.topic = struct.getString("topic");
            {
                Object[] _nestedObjects = struct.getArray("partition_index");
                this.partitionIndex = new ArrayList<Integer>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.partitionIndex.add((Integer) nestedObject);
                }
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of DescribableLogDirTopic");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("topic", this.topic);
            {
                Integer[] _nestedObjects = new Integer[partitionIndex.size()];
                int i = 0;
                for (Integer element : this.partitionIndex) {
                    _nestedObjects[i++] = element;
                }
                struct.set("partition_index", (Object[]) _nestedObjects);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of DescribableLogDirTopic");
            }
            {
                byte[] _stringBytes = topic.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'topic' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(topic, _stringBytes);
                _size += _stringBytes.length + 2;
            }
            {
                int _arraySize = 0;
                _arraySize += 4;
                _arraySize += partitionIndex.size() * 4;
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
            if (!(obj instanceof DescribableLogDirTopic)) return false;
            DescribableLogDirTopic other = (DescribableLogDirTopic) obj;
            if (this.topic == null) {
                if (other.topic != null) return false;
            } else {
                if (!this.topic.equals(other.topic)) return false;
            }
            if (this.partitionIndex == null) {
                if (other.partitionIndex != null) return false;
            } else {
                if (!this.partitionIndex.equals(other.partitionIndex)) return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (topic == null ? 0 : topic.hashCode());
            hashCode = 31 * hashCode + (partitionIndex == null ? 0 : partitionIndex.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "DescribableLogDirTopic("
                + "topic=" + ((topic == null) ? "null" : "'" + topic.toString() + "'")
                + ", partitionIndex=" + MessageUtil.deepToString(partitionIndex.iterator())
                + ")";
        }
        
        public String topic() {
            return this.topic;
        }
        
        public List<Integer> partitionIndex() {
            return this.partitionIndex;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public DescribableLogDirTopic setTopic(String v) {
            this.topic = v;
            return this;
        }
        
        public DescribableLogDirTopic setPartitionIndex(List<Integer> v) {
            this.partitionIndex = v;
            return this;
        }
    }
}
