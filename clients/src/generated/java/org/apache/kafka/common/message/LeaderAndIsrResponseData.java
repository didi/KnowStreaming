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


public class LeaderAndIsrResponseData implements ApiMessage {
    private short errorCode;
    private List<LeaderAndIsrPartitionError> partitionErrors;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
            new Field("partition_errors", new ArrayOf(LeaderAndIsrPartitionError.SCHEMA_0), "Each partition.")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema SCHEMA_2 = SCHEMA_1;
    
    public static final Schema SCHEMA_3 = SCHEMA_2;
    
    public static final Schema SCHEMA_4 =
        new Schema(
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
            new Field("partition_errors", new CompactArrayOf(LeaderAndIsrPartitionError.SCHEMA_4), "Each partition."),
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
    
    public LeaderAndIsrResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public LeaderAndIsrResponseData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public LeaderAndIsrResponseData() {
        this.errorCode = (short) 0;
        this.partitionErrors = new ArrayList<LeaderAndIsrPartitionError>();
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
        this.errorCode = _readable.readShort();
        {
            if (_version >= 4) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field partitionErrors was serialized as null");
                } else {
                    ArrayList<LeaderAndIsrPartitionError> newCollection = new ArrayList<LeaderAndIsrPartitionError>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new LeaderAndIsrPartitionError(_readable, _version));
                    }
                    this.partitionErrors = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field partitionErrors was serialized as null");
                } else {
                    ArrayList<LeaderAndIsrPartitionError> newCollection = new ArrayList<LeaderAndIsrPartitionError>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new LeaderAndIsrPartitionError(_readable, _version));
                    }
                    this.partitionErrors = newCollection;
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
        _writable.writeShort(errorCode);
        if (_version >= 4) {
            _writable.writeUnsignedVarint(partitionErrors.size() + 1);
            for (LeaderAndIsrPartitionError partitionErrorsElement : partitionErrors) {
                partitionErrorsElement.write(_writable, _cache, _version);
            }
        } else {
            _writable.writeInt(partitionErrors.size());
            for (LeaderAndIsrPartitionError partitionErrorsElement : partitionErrors) {
                partitionErrorsElement.write(_writable, _cache, _version);
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
        this.errorCode = struct.getShort("error_code");
        {
            Object[] _nestedObjects = struct.getArray("partition_errors");
            this.partitionErrors = new ArrayList<LeaderAndIsrPartitionError>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.partitionErrors.add(new LeaderAndIsrPartitionError((Struct) nestedObject, _version));
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
        struct.set("error_code", this.errorCode);
        {
            Struct[] _nestedObjects = new Struct[partitionErrors.size()];
            int i = 0;
            for (LeaderAndIsrPartitionError element : this.partitionErrors) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("partition_errors", (Object[]) _nestedObjects);
        }
        if (_version >= 4) {
            struct.set("_tagged_fields", _taggedFields);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        _size += 2;
        {
            int _arraySize = 0;
            if (_version >= 4) {
                _arraySize += ByteUtils.sizeOfUnsignedVarint(partitionErrors.size() + 1);
            } else {
                _arraySize += 4;
            }
            for (LeaderAndIsrPartitionError partitionErrorsElement : partitionErrors) {
                _arraySize += partitionErrorsElement.size(_cache, _version);
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
        if (!(obj instanceof LeaderAndIsrResponseData)) return false;
        LeaderAndIsrResponseData other = (LeaderAndIsrResponseData) obj;
        if (errorCode != other.errorCode) return false;
        if (this.partitionErrors == null) {
            if (other.partitionErrors != null) return false;
        } else {
            if (!this.partitionErrors.equals(other.partitionErrors)) return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + errorCode;
        hashCode = 31 * hashCode + (partitionErrors == null ? 0 : partitionErrors.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "LeaderAndIsrResponseData("
            + "errorCode=" + errorCode
            + ", partitionErrors=" + MessageUtil.deepToString(partitionErrors.iterator())
            + ")";
    }
    
    public short errorCode() {
        return this.errorCode;
    }
    
    public List<LeaderAndIsrPartitionError> partitionErrors() {
        return this.partitionErrors;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public LeaderAndIsrResponseData setErrorCode(short v) {
        this.errorCode = v;
        return this;
    }
    
    public LeaderAndIsrResponseData setPartitionErrors(List<LeaderAndIsrPartitionError> v) {
        this.partitionErrors = v;
        return this;
    }
    
    static public class LeaderAndIsrPartitionError implements Message {
        private String topicName;
        private int partitionIndex;
        private short errorCode;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("topic_name", Type.STRING, "The topic name."),
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("error_code", Type.INT16, "The partition error code, or 0 if there was no error.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 =
            new Schema(
                new Field("topic_name", Type.COMPACT_STRING, "The topic name."),
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("error_code", Type.INT16, "The partition error code, or 0 if there was no error."),
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
        
        public LeaderAndIsrPartitionError(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public LeaderAndIsrPartitionError(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public LeaderAndIsrPartitionError() {
            this.topicName = "";
            this.partitionIndex = 0;
            this.errorCode = (short) 0;
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of LeaderAndIsrPartitionError");
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
            this.partitionIndex = _readable.readInt();
            this.errorCode = _readable.readShort();
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
                throw new UnsupportedVersionException("Can't write version " + _version + " of LeaderAndIsrPartitionError");
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
            _writable.writeInt(partitionIndex);
            _writable.writeShort(errorCode);
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of LeaderAndIsrPartitionError");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 4) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.topicName = struct.getString("topic_name");
            this.partitionIndex = struct.getInt("partition_index");
            this.errorCode = struct.getShort("error_code");
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
                throw new UnsupportedVersionException("Can't write version " + _version + " of LeaderAndIsrPartitionError");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 4) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("topic_name", this.topicName);
            struct.set("partition_index", this.partitionIndex);
            struct.set("error_code", this.errorCode);
            if (_version >= 4) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 4) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of LeaderAndIsrPartitionError");
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
            if (!(obj instanceof LeaderAndIsrPartitionError)) return false;
            LeaderAndIsrPartitionError other = (LeaderAndIsrPartitionError) obj;
            if (this.topicName == null) {
                if (other.topicName != null) return false;
            } else {
                if (!this.topicName.equals(other.topicName)) return false;
            }
            if (partitionIndex != other.partitionIndex) return false;
            if (errorCode != other.errorCode) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (topicName == null ? 0 : topicName.hashCode());
            hashCode = 31 * hashCode + partitionIndex;
            hashCode = 31 * hashCode + errorCode;
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "LeaderAndIsrPartitionError("
                + "topicName=" + ((topicName == null) ? "null" : "'" + topicName.toString() + "'")
                + ", partitionIndex=" + partitionIndex
                + ", errorCode=" + errorCode
                + ")";
        }
        
        public String topicName() {
            return this.topicName;
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
        
        public LeaderAndIsrPartitionError setTopicName(String v) {
            this.topicName = v;
            return this;
        }
        
        public LeaderAndIsrPartitionError setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
        
        public LeaderAndIsrPartitionError setErrorCode(short v) {
            this.errorCode = v;
            return this;
        }
    }
}
