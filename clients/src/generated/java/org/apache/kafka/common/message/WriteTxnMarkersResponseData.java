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


public class WriteTxnMarkersResponseData implements ApiMessage {
    private List<WritableTxnMarkerResult> markers;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("markers", new ArrayOf(WritableTxnMarkerResult.SCHEMA_0), "The results for writing makers.")
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0
    };
    
    public WriteTxnMarkersResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public WriteTxnMarkersResponseData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public WriteTxnMarkersResponseData() {
        this.markers = new ArrayList<WritableTxnMarkerResult>();
    }
    
    @Override
    public short apiKey() {
        return 27;
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
        {
            int arrayLength;
            arrayLength = _readable.readInt();
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field markers was serialized as null");
            } else {
                ArrayList<WritableTxnMarkerResult> newCollection = new ArrayList<WritableTxnMarkerResult>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new WritableTxnMarkerResult(_readable, _version));
                }
                this.markers = newCollection;
            }
        }
        this._unknownTaggedFields = null;
    }
    
    @Override
    public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        _writable.writeInt(markers.size());
        for (WritableTxnMarkerResult markersElement : markers) {
            markersElement.write(_writable, _cache, _version);
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
            Object[] _nestedObjects = struct.getArray("markers");
            this.markers = new ArrayList<WritableTxnMarkerResult>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.markers.add(new WritableTxnMarkerResult((Struct) nestedObject, _version));
            }
        }
    }
    
    @Override
    public Struct toStruct(short _version) {
        TreeMap<Integer, Object> _taggedFields = null;
        Struct struct = new Struct(SCHEMAS[_version]);
        {
            Struct[] _nestedObjects = new Struct[markers.size()];
            int i = 0;
            for (WritableTxnMarkerResult element : this.markers) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("markers", (Object[]) _nestedObjects);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        {
            int _arraySize = 0;
            _arraySize += 4;
            for (WritableTxnMarkerResult markersElement : markers) {
                _arraySize += markersElement.size(_cache, _version);
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
        if (!(obj instanceof WriteTxnMarkersResponseData)) return false;
        WriteTxnMarkersResponseData other = (WriteTxnMarkersResponseData) obj;
        if (this.markers == null) {
            if (other.markers != null) return false;
        } else {
            if (!this.markers.equals(other.markers)) return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (markers == null ? 0 : markers.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "WriteTxnMarkersResponseData("
            + "markers=" + MessageUtil.deepToString(markers.iterator())
            + ")";
    }
    
    public List<WritableTxnMarkerResult> markers() {
        return this.markers;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public WriteTxnMarkersResponseData setMarkers(List<WritableTxnMarkerResult> v) {
        this.markers = v;
        return this;
    }
    
    static public class WritableTxnMarkerResult implements Message {
        private long producerId;
        private List<WritableTxnMarkerTopicResult> topics;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("producer_id", Type.INT64, "The current producer ID in use by the transactional ID."),
                new Field("topics", new ArrayOf(WritableTxnMarkerTopicResult.SCHEMA_0), "The results by topic.")
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0
        };
        
        public WritableTxnMarkerResult(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public WritableTxnMarkerResult(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public WritableTxnMarkerResult() {
            this.producerId = 0L;
            this.topics = new ArrayList<WritableTxnMarkerTopicResult>();
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of WritableTxnMarkerResult");
            }
            this.producerId = _readable.readLong();
            {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topics was serialized as null");
                } else {
                    ArrayList<WritableTxnMarkerTopicResult> newCollection = new ArrayList<WritableTxnMarkerTopicResult>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new WritableTxnMarkerTopicResult(_readable, _version));
                    }
                    this.topics = newCollection;
                }
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of WritableTxnMarkerResult");
            }
            int _numTaggedFields = 0;
            _writable.writeLong(producerId);
            _writable.writeInt(topics.size());
            for (WritableTxnMarkerTopicResult topicsElement : topics) {
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
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of WritableTxnMarkerResult");
            }
            this._unknownTaggedFields = null;
            this.producerId = struct.getLong("producer_id");
            {
                Object[] _nestedObjects = struct.getArray("topics");
                this.topics = new ArrayList<WritableTxnMarkerTopicResult>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.topics.add(new WritableTxnMarkerTopicResult((Struct) nestedObject, _version));
                }
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of WritableTxnMarkerResult");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("producer_id", this.producerId);
            {
                Struct[] _nestedObjects = new Struct[topics.size()];
                int i = 0;
                for (WritableTxnMarkerTopicResult element : this.topics) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("topics", (Object[]) _nestedObjects);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of WritableTxnMarkerResult");
            }
            _size += 8;
            {
                int _arraySize = 0;
                _arraySize += 4;
                for (WritableTxnMarkerTopicResult topicsElement : topics) {
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
            if (!(obj instanceof WritableTxnMarkerResult)) return false;
            WritableTxnMarkerResult other = (WritableTxnMarkerResult) obj;
            if (producerId != other.producerId) return false;
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
            hashCode = 31 * hashCode + ((int) (producerId >> 32) ^ (int) producerId);
            hashCode = 31 * hashCode + (topics == null ? 0 : topics.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "WritableTxnMarkerResult("
                + "producerId=" + producerId
                + ", topics=" + MessageUtil.deepToString(topics.iterator())
                + ")";
        }
        
        public long producerId() {
            return this.producerId;
        }
        
        public List<WritableTxnMarkerTopicResult> topics() {
            return this.topics;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public WritableTxnMarkerResult setProducerId(long v) {
            this.producerId = v;
            return this;
        }
        
        public WritableTxnMarkerResult setTopics(List<WritableTxnMarkerTopicResult> v) {
            this.topics = v;
            return this;
        }
    }
    
    static public class WritableTxnMarkerTopicResult implements Message {
        private String name;
        private List<WritableTxnMarkerPartitionResult> partitions;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partitions", new ArrayOf(WritableTxnMarkerPartitionResult.SCHEMA_0), "The results by partition.")
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0
        };
        
        public WritableTxnMarkerTopicResult(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public WritableTxnMarkerTopicResult(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public WritableTxnMarkerTopicResult() {
            this.name = "";
            this.partitions = new ArrayList<WritableTxnMarkerPartitionResult>();
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of WritableTxnMarkerTopicResult");
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
                    ArrayList<WritableTxnMarkerPartitionResult> newCollection = new ArrayList<WritableTxnMarkerPartitionResult>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new WritableTxnMarkerPartitionResult(_readable, _version));
                    }
                    this.partitions = newCollection;
                }
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of WritableTxnMarkerTopicResult");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                _writable.writeShort((short) _stringBytes.length);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeInt(partitions.size());
            for (WritableTxnMarkerPartitionResult partitionsElement : partitions) {
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of WritableTxnMarkerTopicResult");
            }
            this._unknownTaggedFields = null;
            this.name = struct.getString("name");
            {
                Object[] _nestedObjects = struct.getArray("partitions");
                this.partitions = new ArrayList<WritableTxnMarkerPartitionResult>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.partitions.add(new WritableTxnMarkerPartitionResult((Struct) nestedObject, _version));
                }
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of WritableTxnMarkerTopicResult");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("name", this.name);
            {
                Struct[] _nestedObjects = new Struct[partitions.size()];
                int i = 0;
                for (WritableTxnMarkerPartitionResult element : this.partitions) {
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
                throw new UnsupportedVersionException("Can't size version " + _version + " of WritableTxnMarkerTopicResult");
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
                for (WritableTxnMarkerPartitionResult partitionsElement : partitions) {
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
            if (!(obj instanceof WritableTxnMarkerTopicResult)) return false;
            WritableTxnMarkerTopicResult other = (WritableTxnMarkerTopicResult) obj;
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
            return "WritableTxnMarkerTopicResult("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", partitions=" + MessageUtil.deepToString(partitions.iterator())
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public List<WritableTxnMarkerPartitionResult> partitions() {
            return this.partitions;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public WritableTxnMarkerTopicResult setName(String v) {
            this.name = v;
            return this;
        }
        
        public WritableTxnMarkerTopicResult setPartitions(List<WritableTxnMarkerPartitionResult> v) {
            this.partitions = v;
            return this;
        }
    }
    
    static public class WritableTxnMarkerPartitionResult implements Message {
        private int partitionIndex;
        private short errorCode;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("error_code", Type.INT16, "The error code, or 0 if there was no error.")
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0
        };
        
        public WritableTxnMarkerPartitionResult(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public WritableTxnMarkerPartitionResult(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public WritableTxnMarkerPartitionResult() {
            this.partitionIndex = 0;
            this.errorCode = (short) 0;
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of WritableTxnMarkerPartitionResult");
            }
            this.partitionIndex = _readable.readInt();
            this.errorCode = _readable.readShort();
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of WritableTxnMarkerPartitionResult");
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of WritableTxnMarkerPartitionResult");
            }
            this._unknownTaggedFields = null;
            this.partitionIndex = struct.getInt("partition_index");
            this.errorCode = struct.getShort("error_code");
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of WritableTxnMarkerPartitionResult");
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
                throw new UnsupportedVersionException("Can't size version " + _version + " of WritableTxnMarkerPartitionResult");
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
            if (!(obj instanceof WritableTxnMarkerPartitionResult)) return false;
            WritableTxnMarkerPartitionResult other = (WritableTxnMarkerPartitionResult) obj;
            if (partitionIndex != other.partitionIndex) return false;
            if (errorCode != other.errorCode) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + partitionIndex;
            hashCode = 31 * hashCode + errorCode;
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "WritableTxnMarkerPartitionResult("
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
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public WritableTxnMarkerPartitionResult setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
        
        public WritableTxnMarkerPartitionResult setErrorCode(short v) {
            this.errorCode = v;
            return this;
        }
    }
}
