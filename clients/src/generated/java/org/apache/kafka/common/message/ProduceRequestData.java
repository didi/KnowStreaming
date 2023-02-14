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
import java.util.Arrays;
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
import org.apache.kafka.common.utils.Bytes;


public class ProduceRequestData implements ApiMessage {
    private String transactionalId;
    private short acks;
    private int timeoutMs;
    private List<TopicProduceData> topics;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("acks", Type.INT16, "The number of acknowledgments the producer requires the leader to have received before considering a request complete. Allowed values: 0 for no acknowledgments, 1 for only the leader and -1 for the full ISR."),
            new Field("timeout_ms", Type.INT32, "The timeout to await a response in miliseconds."),
            new Field("topics", new ArrayOf(TopicProduceData.SCHEMA_0), "Each topic to produce to.")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema SCHEMA_2 = SCHEMA_1;
    
    public static final Schema SCHEMA_3 =
        new Schema(
            new Field("transactional_id", Type.NULLABLE_STRING, "The transactional ID, or null if the producer is not transactional."),
            new Field("acks", Type.INT16, "The number of acknowledgments the producer requires the leader to have received before considering a request complete. Allowed values: 0 for no acknowledgments, 1 for only the leader and -1 for the full ISR."),
            new Field("timeout_ms", Type.INT32, "The timeout to await a response in miliseconds."),
            new Field("topics", new ArrayOf(TopicProduceData.SCHEMA_0), "Each topic to produce to.")
        );
    
    public static final Schema SCHEMA_4 = SCHEMA_3;
    
    public static final Schema SCHEMA_5 = SCHEMA_4;
    
    public static final Schema SCHEMA_6 = SCHEMA_5;
    
    public static final Schema SCHEMA_7 = SCHEMA_6;
    
    public static final Schema SCHEMA_8 = SCHEMA_7;
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2,
        SCHEMA_3,
        SCHEMA_4,
        SCHEMA_5,
        SCHEMA_6,
        SCHEMA_7,
        SCHEMA_8
    };
    
    public ProduceRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public ProduceRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public ProduceRequestData() {
        this.transactionalId = "";
        this.acks = (short) 0;
        this.timeoutMs = 0;
        this.topics = new ArrayList<TopicProduceData>();
    }
    
    @Override
    public short apiKey() {
        return 0;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 8;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        if (_version >= 3) {
            int length;
            length = _readable.readShort();
            if (length < 0) {
                this.transactionalId = null;
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field transactionalId had invalid length " + length);
            } else {
                this.transactionalId = _readable.readString(length);
            }
        } else {
            this.transactionalId = "";
        }
        this.acks = _readable.readShort();
        this.timeoutMs = _readable.readInt();
        {
            int arrayLength;
            arrayLength = _readable.readInt();
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field topics was serialized as null");
            } else {
                ArrayList<TopicProduceData> newCollection = new ArrayList<TopicProduceData>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new TopicProduceData(_readable, _version));
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
            if (transactionalId == null) {
                _writable.writeShort((short) -1);
            } else {
                byte[] _stringBytes = _cache.getSerializedValue(transactionalId);
                _writable.writeShort((short) _stringBytes.length);
                _writable.writeByteArray(_stringBytes);
            }
        } else {
            if (transactionalId == null || !transactionalId.equals("")) {
                throw new UnsupportedVersionException("Attempted to write a non-default transactionalId at version " + _version);
            }
        }
        _writable.writeShort(acks);
        _writable.writeInt(timeoutMs);
        _writable.writeInt(topics.size());
        for (TopicProduceData topicsElement : topics) {
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
            this.transactionalId = struct.getString("transactional_id");
        } else {
            this.transactionalId = "";
        }
        this.acks = struct.getShort("acks");
        this.timeoutMs = struct.getInt("timeout_ms");
        {
            Object[] _nestedObjects = struct.getArray("topics");
            this.topics = new ArrayList<TopicProduceData>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.topics.add(new TopicProduceData((Struct) nestedObject, _version));
            }
        }
    }
    
    @Override
    public Struct toStruct(short _version) {
        TreeMap<Integer, Object> _taggedFields = null;
        Struct struct = new Struct(SCHEMAS[_version]);
        if (_version >= 3) {
            struct.set("transactional_id", this.transactionalId);
        } else {
            if (transactionalId == null || !transactionalId.equals("")) {
                throw new UnsupportedVersionException("Attempted to write a non-default transactionalId at version " + _version);
            }
        }
        struct.set("acks", this.acks);
        struct.set("timeout_ms", this.timeoutMs);
        {
            Struct[] _nestedObjects = new Struct[topics.size()];
            int i = 0;
            for (TopicProduceData element : this.topics) {
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
            if (transactionalId == null) {
                _size += 2;
            } else {
                byte[] _stringBytes = transactionalId.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'transactionalId' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(transactionalId, _stringBytes);
                _size += _stringBytes.length + 2;
            }
        }
        _size += 2;
        _size += 4;
        {
            int _arraySize = 0;
            _arraySize += 4;
            for (TopicProduceData topicsElement : topics) {
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
        if (!(obj instanceof ProduceRequestData)) return false;
        ProduceRequestData other = (ProduceRequestData) obj;
        if (this.transactionalId == null) {
            if (other.transactionalId != null) return false;
        } else {
            if (!this.transactionalId.equals(other.transactionalId)) return false;
        }
        if (acks != other.acks) return false;
        if (timeoutMs != other.timeoutMs) return false;
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
        hashCode = 31 * hashCode + (transactionalId == null ? 0 : transactionalId.hashCode());
        hashCode = 31 * hashCode + acks;
        hashCode = 31 * hashCode + timeoutMs;
        hashCode = 31 * hashCode + (topics == null ? 0 : topics.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "ProduceRequestData("
            + "transactionalId=" + ((transactionalId == null) ? "null" : "'" + transactionalId.toString() + "'")
            + ", acks=" + acks
            + ", timeoutMs=" + timeoutMs
            + ", topics=" + MessageUtil.deepToString(topics.iterator())
            + ")";
    }
    
    public String transactionalId() {
        return this.transactionalId;
    }
    
    public short acks() {
        return this.acks;
    }
    
    public int timeoutMs() {
        return this.timeoutMs;
    }
    
    public List<TopicProduceData> topics() {
        return this.topics;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public ProduceRequestData setTransactionalId(String v) {
        this.transactionalId = v;
        return this;
    }
    
    public ProduceRequestData setAcks(short v) {
        this.acks = v;
        return this;
    }
    
    public ProduceRequestData setTimeoutMs(int v) {
        this.timeoutMs = v;
        return this;
    }
    
    public ProduceRequestData setTopics(List<TopicProduceData> v) {
        this.topics = v;
        return this;
    }
    
    static public class TopicProduceData implements Message {
        private String name;
        private List<PartitionProduceData> partitions;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partitions", new ArrayOf(PartitionProduceData.SCHEMA_0), "Each partition to produce to.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 = SCHEMA_4;
        
        public static final Schema SCHEMA_6 = SCHEMA_5;
        
        public static final Schema SCHEMA_7 = SCHEMA_6;
        
        public static final Schema SCHEMA_8 = SCHEMA_7;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3,
            SCHEMA_4,
            SCHEMA_5,
            SCHEMA_6,
            SCHEMA_7,
            SCHEMA_8
        };
        
        public TopicProduceData(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public TopicProduceData(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public TopicProduceData() {
            this.name = "";
            this.partitions = new ArrayList<PartitionProduceData>();
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 8;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of TopicProduceData");
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
                    ArrayList<PartitionProduceData> newCollection = new ArrayList<PartitionProduceData>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new PartitionProduceData(_readable, _version));
                    }
                    this.partitions = newCollection;
                }
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of TopicProduceData");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                _writable.writeShort((short) _stringBytes.length);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeInt(partitions.size());
            for (PartitionProduceData partitionsElement : partitions) {
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
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of TopicProduceData");
            }
            this._unknownTaggedFields = null;
            this.name = struct.getString("name");
            {
                Object[] _nestedObjects = struct.getArray("partitions");
                this.partitions = new ArrayList<PartitionProduceData>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.partitions.add(new PartitionProduceData((Struct) nestedObject, _version));
                }
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of TopicProduceData");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("name", this.name);
            {
                Struct[] _nestedObjects = new Struct[partitions.size()];
                int i = 0;
                for (PartitionProduceData element : this.partitions) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("partitions", (Object[]) _nestedObjects);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of TopicProduceData");
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
                for (PartitionProduceData partitionsElement : partitions) {
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
            if (!(obj instanceof TopicProduceData)) return false;
            TopicProduceData other = (TopicProduceData) obj;
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
            return "TopicProduceData("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", partitions=" + MessageUtil.deepToString(partitions.iterator())
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public List<PartitionProduceData> partitions() {
            return this.partitions;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public TopicProduceData setName(String v) {
            this.name = v;
            return this;
        }
        
        public TopicProduceData setPartitions(List<PartitionProduceData> v) {
            this.partitions = v;
            return this;
        }
    }
    
    static public class PartitionProduceData implements Message {
        private int partitionIndex;
        private byte[] records;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("records", Type.NULLABLE_BYTES, "The record data to be produced.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 = SCHEMA_4;
        
        public static final Schema SCHEMA_6 = SCHEMA_5;
        
        public static final Schema SCHEMA_7 = SCHEMA_6;
        
        public static final Schema SCHEMA_8 = SCHEMA_7;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3,
            SCHEMA_4,
            SCHEMA_5,
            SCHEMA_6,
            SCHEMA_7,
            SCHEMA_8
        };
        
        public PartitionProduceData(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public PartitionProduceData(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public PartitionProduceData() {
            this.partitionIndex = 0;
            this.records = Bytes.EMPTY;
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 8;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of PartitionProduceData");
            }
            this.partitionIndex = _readable.readInt();
            {
                int length;
                length = _readable.readInt();
                if (length < 0) {
                    this.records = null;
                } else {
                    byte[] newBytes = new byte[length];
                    _readable.readArray(newBytes);
                    this.records = newBytes;
                }
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of PartitionProduceData");
            }
            int _numTaggedFields = 0;
            _writable.writeInt(partitionIndex);
            if (records == null) {
                _writable.writeInt(-1);
            } else {
                _writable.writeInt(records.length);
                _writable.writeByteArray(records);
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
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of PartitionProduceData");
            }
            this._unknownTaggedFields = null;
            this.partitionIndex = struct.getInt("partition_index");
            this.records = struct.getByteArray("records");
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of PartitionProduceData");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("partition_index", this.partitionIndex);
            struct.setByteArray("records", this.records);
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of PartitionProduceData");
            }
            _size += 4;
            if (records == null) {
                _size += 4;
            } else {
                int _bytesSize = records.length;
                _bytesSize += 4;
                _size += _bytesSize;
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
            if (!(obj instanceof PartitionProduceData)) return false;
            PartitionProduceData other = (PartitionProduceData) obj;
            if (partitionIndex != other.partitionIndex) return false;
            if (!Arrays.equals(this.records, other.records)) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + partitionIndex;
            hashCode = 31 * hashCode + Arrays.hashCode(records);
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "PartitionProduceData("
                + "partitionIndex=" + partitionIndex
                + ", records=" + Arrays.toString(records)
                + ")";
        }
        
        public int partitionIndex() {
            return this.partitionIndex;
        }
        
        public byte[] records() {
            return this.records;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public PartitionProduceData setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
        
        public PartitionProduceData setRecords(byte[] v) {
            this.records = v;
            return this;
        }
    }
}
