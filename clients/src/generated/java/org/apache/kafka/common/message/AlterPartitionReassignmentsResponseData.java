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


public class AlterPartitionReassignmentsResponseData implements ApiMessage {
    private int throttleTimeMs;
    private short errorCode;
    private String errorMessage;
    private List<ReassignableTopicResponse> responses;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("error_code", Type.INT16, "The top-level error code, or 0 if there was no error."),
            new Field("error_message", Type.COMPACT_NULLABLE_STRING, "The top-level error message, or null if there was no error."),
            new Field("responses", new CompactArrayOf(ReassignableTopicResponse.SCHEMA_0), "The responses to topics to reassign."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0
    };
    
    public AlterPartitionReassignmentsResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public AlterPartitionReassignmentsResponseData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public AlterPartitionReassignmentsResponseData() {
        this.throttleTimeMs = 0;
        this.errorCode = (short) 0;
        this.errorMessage = "";
        this.responses = new ArrayList<ReassignableTopicResponse>();
    }
    
    @Override
    public short apiKey() {
        return 45;
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
        this.throttleTimeMs = _readable.readInt();
        this.errorCode = _readable.readShort();
        {
            int length;
            length = _readable.readUnsignedVarint() - 1;
            if (length < 0) {
                this.errorMessage = null;
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field errorMessage had invalid length " + length);
            } else {
                this.errorMessage = _readable.readString(length);
            }
        }
        {
            int arrayLength;
            arrayLength = _readable.readUnsignedVarint() - 1;
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field responses was serialized as null");
            } else {
                ArrayList<ReassignableTopicResponse> newCollection = new ArrayList<ReassignableTopicResponse>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new ReassignableTopicResponse(_readable, _version));
                }
                this.responses = newCollection;
            }
        }
        this._unknownTaggedFields = null;
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
    
    @Override
    public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        _writable.writeInt(throttleTimeMs);
        _writable.writeShort(errorCode);
        if (errorMessage == null) {
            _writable.writeUnsignedVarint(0);
        } else {
            byte[] _stringBytes = _cache.getSerializedValue(errorMessage);
            _writable.writeUnsignedVarint(_stringBytes.length + 1);
            _writable.writeByteArray(_stringBytes);
        }
        _writable.writeUnsignedVarint(responses.size() + 1);
        for (ReassignableTopicResponse responsesElement : responses) {
            responsesElement.write(_writable, _cache, _version);
        }
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        _writable.writeUnsignedVarint(_numTaggedFields);
        _rawWriter.writeRawTags(_writable, Integer.MAX_VALUE);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void fromStruct(Struct struct, short _version) {
        NavigableMap<Integer, Object> _taggedFields = null;
        this._unknownTaggedFields = null;
        _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
        this.throttleTimeMs = struct.getInt("throttle_time_ms");
        this.errorCode = struct.getShort("error_code");
        this.errorMessage = struct.getString("error_message");
        {
            Object[] _nestedObjects = struct.getArray("responses");
            this.responses = new ArrayList<ReassignableTopicResponse>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.responses.add(new ReassignableTopicResponse((Struct) nestedObject, _version));
            }
        }
        if (!_taggedFields.isEmpty()) {
            this._unknownTaggedFields = new ArrayList<>(_taggedFields.size());
            for (Entry<Integer, Object> entry : _taggedFields.entrySet()) {
                this._unknownTaggedFields.add((RawTaggedField) entry.getValue());
            }
        }
    }
    
    @Override
    public Struct toStruct(short _version) {
        TreeMap<Integer, Object> _taggedFields = null;
        _taggedFields = new TreeMap<>();
        Struct struct = new Struct(SCHEMAS[_version]);
        struct.set("throttle_time_ms", this.throttleTimeMs);
        struct.set("error_code", this.errorCode);
        struct.set("error_message", this.errorMessage);
        {
            Struct[] _nestedObjects = new Struct[responses.size()];
            int i = 0;
            for (ReassignableTopicResponse element : this.responses) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("responses", (Object[]) _nestedObjects);
        }
        struct.set("_tagged_fields", _taggedFields);
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        _size += 4;
        _size += 2;
        if (errorMessage == null) {
            _size += 1;
        } else {
            byte[] _stringBytes = errorMessage.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'errorMessage' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(errorMessage, _stringBytes);
            _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
        }
        {
            int _arraySize = 0;
            _arraySize += ByteUtils.sizeOfUnsignedVarint(responses.size() + 1);
            for (ReassignableTopicResponse responsesElement : responses) {
                _arraySize += responsesElement.size(_cache, _version);
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
        _size += ByteUtils.sizeOfUnsignedVarint(_numTaggedFields);
        return _size;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AlterPartitionReassignmentsResponseData)) return false;
        AlterPartitionReassignmentsResponseData other = (AlterPartitionReassignmentsResponseData) obj;
        if (throttleTimeMs != other.throttleTimeMs) return false;
        if (errorCode != other.errorCode) return false;
        if (this.errorMessage == null) {
            if (other.errorMessage != null) return false;
        } else {
            if (!this.errorMessage.equals(other.errorMessage)) return false;
        }
        if (this.responses == null) {
            if (other.responses != null) return false;
        } else {
            if (!this.responses.equals(other.responses)) return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + throttleTimeMs;
        hashCode = 31 * hashCode + errorCode;
        hashCode = 31 * hashCode + (errorMessage == null ? 0 : errorMessage.hashCode());
        hashCode = 31 * hashCode + (responses == null ? 0 : responses.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "AlterPartitionReassignmentsResponseData("
            + "throttleTimeMs=" + throttleTimeMs
            + ", errorCode=" + errorCode
            + ", errorMessage=" + ((errorMessage == null) ? "null" : "'" + errorMessage.toString() + "'")
            + ", responses=" + MessageUtil.deepToString(responses.iterator())
            + ")";
    }
    
    public int throttleTimeMs() {
        return this.throttleTimeMs;
    }
    
    public short errorCode() {
        return this.errorCode;
    }
    
    public String errorMessage() {
        return this.errorMessage;
    }
    
    public List<ReassignableTopicResponse> responses() {
        return this.responses;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public AlterPartitionReassignmentsResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
    
    public AlterPartitionReassignmentsResponseData setErrorCode(short v) {
        this.errorCode = v;
        return this;
    }
    
    public AlterPartitionReassignmentsResponseData setErrorMessage(String v) {
        this.errorMessage = v;
        return this;
    }
    
    public AlterPartitionReassignmentsResponseData setResponses(List<ReassignableTopicResponse> v) {
        this.responses = v;
        return this;
    }
    
    static public class ReassignableTopicResponse implements Message {
        private String name;
        private List<ReassignablePartitionResponse> partitions;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.COMPACT_STRING, "The topic name"),
                new Field("partitions", new CompactArrayOf(ReassignablePartitionResponse.SCHEMA_0), "The responses to partitions to reassign"),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0
        };
        
        public ReassignableTopicResponse(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public ReassignableTopicResponse(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public ReassignableTopicResponse() {
            this.name = "";
            this.partitions = new ArrayList<ReassignablePartitionResponse>();
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of ReassignableTopicResponse");
            }
            {
                int length;
                length = _readable.readUnsignedVarint() - 1;
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
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field partitions was serialized as null");
                } else {
                    ArrayList<ReassignablePartitionResponse> newCollection = new ArrayList<ReassignablePartitionResponse>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new ReassignablePartitionResponse(_readable, _version));
                    }
                    this.partitions = newCollection;
                }
            }
            this._unknownTaggedFields = null;
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
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of ReassignableTopicResponse");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeUnsignedVarint(partitions.size() + 1);
            for (ReassignablePartitionResponse partitionsElement : partitions) {
                partitionsElement.write(_writable, _cache, _version);
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            _writable.writeUnsignedVarint(_numTaggedFields);
            _rawWriter.writeRawTags(_writable, Integer.MAX_VALUE);
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public void fromStruct(Struct struct, short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of ReassignableTopicResponse");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            this.name = struct.getString("name");
            {
                Object[] _nestedObjects = struct.getArray("partitions");
                this.partitions = new ArrayList<ReassignablePartitionResponse>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.partitions.add(new ReassignablePartitionResponse((Struct) nestedObject, _version));
                }
            }
            if (!_taggedFields.isEmpty()) {
                this._unknownTaggedFields = new ArrayList<>(_taggedFields.size());
                for (Entry<Integer, Object> entry : _taggedFields.entrySet()) {
                    this._unknownTaggedFields.add((RawTaggedField) entry.getValue());
                }
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of ReassignableTopicResponse");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            _taggedFields = new TreeMap<>();
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("name", this.name);
            {
                Struct[] _nestedObjects = new Struct[partitions.size()];
                int i = 0;
                for (ReassignablePartitionResponse element : this.partitions) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("partitions", (Object[]) _nestedObjects);
            }
            struct.set("_tagged_fields", _taggedFields);
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of ReassignableTopicResponse");
            }
            {
                byte[] _stringBytes = name.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'name' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(name, _stringBytes);
                _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
            }
            {
                int _arraySize = 0;
                _arraySize += ByteUtils.sizeOfUnsignedVarint(partitions.size() + 1);
                for (ReassignablePartitionResponse partitionsElement : partitions) {
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
            _size += ByteUtils.sizeOfUnsignedVarint(_numTaggedFields);
            return _size;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ReassignableTopicResponse)) return false;
            ReassignableTopicResponse other = (ReassignableTopicResponse) obj;
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
            return "ReassignableTopicResponse("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", partitions=" + MessageUtil.deepToString(partitions.iterator())
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public List<ReassignablePartitionResponse> partitions() {
            return this.partitions;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public ReassignableTopicResponse setName(String v) {
            this.name = v;
            return this;
        }
        
        public ReassignableTopicResponse setPartitions(List<ReassignablePartitionResponse> v) {
            this.partitions = v;
            return this;
        }
    }
    
    static public class ReassignablePartitionResponse implements Message {
        private int partitionIndex;
        private short errorCode;
        private String errorMessage;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("error_code", Type.INT16, "The error code for this partition, or 0 if there was no error."),
                new Field("error_message", Type.COMPACT_NULLABLE_STRING, "The error message for this partition, or null if there was no error."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0
        };
        
        public ReassignablePartitionResponse(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public ReassignablePartitionResponse(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public ReassignablePartitionResponse() {
            this.partitionIndex = 0;
            this.errorCode = (short) 0;
            this.errorMessage = "";
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of ReassignablePartitionResponse");
            }
            this.partitionIndex = _readable.readInt();
            this.errorCode = _readable.readShort();
            {
                int length;
                length = _readable.readUnsignedVarint() - 1;
                if (length < 0) {
                    this.errorMessage = null;
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field errorMessage had invalid length " + length);
                } else {
                    this.errorMessage = _readable.readString(length);
                }
            }
            this._unknownTaggedFields = null;
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
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of ReassignablePartitionResponse");
            }
            int _numTaggedFields = 0;
            _writable.writeInt(partitionIndex);
            _writable.writeShort(errorCode);
            if (errorMessage == null) {
                _writable.writeUnsignedVarint(0);
            } else {
                byte[] _stringBytes = _cache.getSerializedValue(errorMessage);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            _writable.writeUnsignedVarint(_numTaggedFields);
            _rawWriter.writeRawTags(_writable, Integer.MAX_VALUE);
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public void fromStruct(Struct struct, short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of ReassignablePartitionResponse");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            this.partitionIndex = struct.getInt("partition_index");
            this.errorCode = struct.getShort("error_code");
            this.errorMessage = struct.getString("error_message");
            if (!_taggedFields.isEmpty()) {
                this._unknownTaggedFields = new ArrayList<>(_taggedFields.size());
                for (Entry<Integer, Object> entry : _taggedFields.entrySet()) {
                    this._unknownTaggedFields.add((RawTaggedField) entry.getValue());
                }
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of ReassignablePartitionResponse");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            _taggedFields = new TreeMap<>();
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("partition_index", this.partitionIndex);
            struct.set("error_code", this.errorCode);
            struct.set("error_message", this.errorMessage);
            struct.set("_tagged_fields", _taggedFields);
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of ReassignablePartitionResponse");
            }
            _size += 4;
            _size += 2;
            if (errorMessage == null) {
                _size += 1;
            } else {
                byte[] _stringBytes = errorMessage.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'errorMessage' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(errorMessage, _stringBytes);
                _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
            }
            if (_unknownTaggedFields != null) {
                _numTaggedFields += _unknownTaggedFields.size();
                for (RawTaggedField _field : _unknownTaggedFields) {
                    _size += ByteUtils.sizeOfUnsignedVarint(_field.tag());
                    _size += ByteUtils.sizeOfUnsignedVarint(_field.size());
                    _size += _field.size();
                }
            }
            _size += ByteUtils.sizeOfUnsignedVarint(_numTaggedFields);
            return _size;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ReassignablePartitionResponse)) return false;
            ReassignablePartitionResponse other = (ReassignablePartitionResponse) obj;
            if (partitionIndex != other.partitionIndex) return false;
            if (errorCode != other.errorCode) return false;
            if (this.errorMessage == null) {
                if (other.errorMessage != null) return false;
            } else {
                if (!this.errorMessage.equals(other.errorMessage)) return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + partitionIndex;
            hashCode = 31 * hashCode + errorCode;
            hashCode = 31 * hashCode + (errorMessage == null ? 0 : errorMessage.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "ReassignablePartitionResponse("
                + "partitionIndex=" + partitionIndex
                + ", errorCode=" + errorCode
                + ", errorMessage=" + ((errorMessage == null) ? "null" : "'" + errorMessage.toString() + "'")
                + ")";
        }
        
        public int partitionIndex() {
            return this.partitionIndex;
        }
        
        public short errorCode() {
            return this.errorCode;
        }
        
        public String errorMessage() {
            return this.errorMessage;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public ReassignablePartitionResponse setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
        
        public ReassignablePartitionResponse setErrorCode(short v) {
            this.errorCode = v;
            return this;
        }
        
        public ReassignablePartitionResponse setErrorMessage(String v) {
            this.errorMessage = v;
            return this;
        }
    }
}
