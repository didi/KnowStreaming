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


public class ProduceResponseData implements ApiMessage {
    private List<TopicProduceResponse> responses;
    private int throttleTimeMs;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("responses", new ArrayOf(TopicProduceResponse.SCHEMA_0), "Each produce response")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("responses", new ArrayOf(TopicProduceResponse.SCHEMA_0), "Each produce response"),
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota.")
        );
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("responses", new ArrayOf(TopicProduceResponse.SCHEMA_2), "Each produce response"),
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota.")
        );
    
    public static final Schema SCHEMA_3 = SCHEMA_2;
    
    public static final Schema SCHEMA_4 = SCHEMA_3;
    
    public static final Schema SCHEMA_5 =
        new Schema(
            new Field("responses", new ArrayOf(TopicProduceResponse.SCHEMA_5), "Each produce response"),
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota.")
        );
    
    public static final Schema SCHEMA_6 = SCHEMA_5;
    
    public static final Schema SCHEMA_7 = SCHEMA_6;
    
    public static final Schema SCHEMA_8 =
        new Schema(
            new Field("responses", new ArrayOf(TopicProduceResponse.SCHEMA_8), "Each produce response"),
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota.")
        );
    
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
    
    public ProduceResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public ProduceResponseData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public ProduceResponseData() {
        this.responses = new ArrayList<TopicProduceResponse>();
        this.throttleTimeMs = 0;
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
        {
            int arrayLength;
            arrayLength = _readable.readInt();
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field responses was serialized as null");
            } else {
                ArrayList<TopicProduceResponse> newCollection = new ArrayList<TopicProduceResponse>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new TopicProduceResponse(_readable, _version));
                }
                this.responses = newCollection;
            }
        }
        if (_version >= 1) {
            this.throttleTimeMs = _readable.readInt();
        } else {
            this.throttleTimeMs = 0;
        }
        this._unknownTaggedFields = null;
    }
    
    @Override
    public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        _writable.writeInt(responses.size());
        for (TopicProduceResponse responsesElement : responses) {
            responsesElement.write(_writable, _cache, _version);
        }
        if (_version >= 1) {
            _writable.writeInt(throttleTimeMs);
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
            Object[] _nestedObjects = struct.getArray("responses");
            this.responses = new ArrayList<TopicProduceResponse>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.responses.add(new TopicProduceResponse((Struct) nestedObject, _version));
            }
        }
        if (_version >= 1) {
            this.throttleTimeMs = struct.getInt("throttle_time_ms");
        } else {
            this.throttleTimeMs = 0;
        }
    }
    
    @Override
    public Struct toStruct(short _version) {
        TreeMap<Integer, Object> _taggedFields = null;
        Struct struct = new Struct(SCHEMAS[_version]);
        {
            Struct[] _nestedObjects = new Struct[responses.size()];
            int i = 0;
            for (TopicProduceResponse element : this.responses) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("responses", (Object[]) _nestedObjects);
        }
        if (_version >= 1) {
            struct.set("throttle_time_ms", this.throttleTimeMs);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        {
            int _arraySize = 0;
            _arraySize += 4;
            for (TopicProduceResponse responsesElement : responses) {
                _arraySize += responsesElement.size(_cache, _version);
            }
            _size += _arraySize;
        }
        if (_version >= 1) {
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
        if (!(obj instanceof ProduceResponseData)) return false;
        ProduceResponseData other = (ProduceResponseData) obj;
        if (this.responses == null) {
            if (other.responses != null) return false;
        } else {
            if (!this.responses.equals(other.responses)) return false;
        }
        if (throttleTimeMs != other.throttleTimeMs) return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (responses == null ? 0 : responses.hashCode());
        hashCode = 31 * hashCode + throttleTimeMs;
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "ProduceResponseData("
            + "responses=" + MessageUtil.deepToString(responses.iterator())
            + ", throttleTimeMs=" + throttleTimeMs
            + ")";
    }
    
    public List<TopicProduceResponse> responses() {
        return this.responses;
    }
    
    public int throttleTimeMs() {
        return this.throttleTimeMs;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public ProduceResponseData setResponses(List<TopicProduceResponse> v) {
        this.responses = v;
        return this;
    }
    
    public ProduceResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
    
    static public class TopicProduceResponse implements Message {
        private String name;
        private List<PartitionProduceResponse> partitions;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.STRING, "The topic name"),
                new Field("partitions", new ArrayOf(PartitionProduceResponse.SCHEMA_0), "Each partition that we produced to within the topic.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 =
            new Schema(
                new Field("name", Type.STRING, "The topic name"),
                new Field("partitions", new ArrayOf(PartitionProduceResponse.SCHEMA_2), "Each partition that we produced to within the topic.")
            );
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 =
            new Schema(
                new Field("name", Type.STRING, "The topic name"),
                new Field("partitions", new ArrayOf(PartitionProduceResponse.SCHEMA_5), "Each partition that we produced to within the topic.")
            );
        
        public static final Schema SCHEMA_6 = SCHEMA_5;
        
        public static final Schema SCHEMA_7 = SCHEMA_6;
        
        public static final Schema SCHEMA_8 =
            new Schema(
                new Field("name", Type.STRING, "The topic name"),
                new Field("partitions", new ArrayOf(PartitionProduceResponse.SCHEMA_8), "Each partition that we produced to within the topic.")
            );
        
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
        
        public TopicProduceResponse(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public TopicProduceResponse(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public TopicProduceResponse() {
            this.name = "";
            this.partitions = new ArrayList<PartitionProduceResponse>();
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of TopicProduceResponse");
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
                    ArrayList<PartitionProduceResponse> newCollection = new ArrayList<PartitionProduceResponse>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new PartitionProduceResponse(_readable, _version));
                    }
                    this.partitions = newCollection;
                }
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of TopicProduceResponse");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                _writable.writeShort((short) _stringBytes.length);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeInt(partitions.size());
            for (PartitionProduceResponse partitionsElement : partitions) {
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of TopicProduceResponse");
            }
            this._unknownTaggedFields = null;
            this.name = struct.getString("name");
            {
                Object[] _nestedObjects = struct.getArray("partitions");
                this.partitions = new ArrayList<PartitionProduceResponse>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.partitions.add(new PartitionProduceResponse((Struct) nestedObject, _version));
                }
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of TopicProduceResponse");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("name", this.name);
            {
                Struct[] _nestedObjects = new Struct[partitions.size()];
                int i = 0;
                for (PartitionProduceResponse element : this.partitions) {
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
                throw new UnsupportedVersionException("Can't size version " + _version + " of TopicProduceResponse");
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
                for (PartitionProduceResponse partitionsElement : partitions) {
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
            if (!(obj instanceof TopicProduceResponse)) return false;
            TopicProduceResponse other = (TopicProduceResponse) obj;
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
            return "TopicProduceResponse("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", partitions=" + MessageUtil.deepToString(partitions.iterator())
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public List<PartitionProduceResponse> partitions() {
            return this.partitions;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public TopicProduceResponse setName(String v) {
            this.name = v;
            return this;
        }
        
        public TopicProduceResponse setPartitions(List<PartitionProduceResponse> v) {
            this.partitions = v;
            return this;
        }
    }
    
    static public class PartitionProduceResponse implements Message {
        private int partitionIndex;
        private short errorCode;
        private long baseOffset;
        private long logAppendTimeMs;
        private long logStartOffset;
        private List<BatchIndexAndErrorMessage> recordErrors;
        private String errorMessage;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
                new Field("base_offset", Type.INT64, "The base offset.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
                new Field("base_offset", Type.INT64, "The base offset."),
                new Field("log_append_time_ms", Type.INT64, "The timestamp returned by broker after appending the messages. If CreateTime is used for the topic, the timestamp will be -1.  If LogAppendTime is used for the topic, the timestamp will be the broker local time when the messages are appended.")
            );
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
                new Field("base_offset", Type.INT64, "The base offset."),
                new Field("log_append_time_ms", Type.INT64, "The timestamp returned by broker after appending the messages. If CreateTime is used for the topic, the timestamp will be -1.  If LogAppendTime is used for the topic, the timestamp will be the broker local time when the messages are appended."),
                new Field("log_start_offset", Type.INT64, "The log start offset.")
            );
        
        public static final Schema SCHEMA_6 = SCHEMA_5;
        
        public static final Schema SCHEMA_7 = SCHEMA_6;
        
        public static final Schema SCHEMA_8 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
                new Field("base_offset", Type.INT64, "The base offset."),
                new Field("log_append_time_ms", Type.INT64, "The timestamp returned by broker after appending the messages. If CreateTime is used for the topic, the timestamp will be -1.  If LogAppendTime is used for the topic, the timestamp will be the broker local time when the messages are appended."),
                new Field("log_start_offset", Type.INT64, "The log start offset."),
                new Field("record_errors", new ArrayOf(BatchIndexAndErrorMessage.SCHEMA_8), "The batch indices of records that caused the batch to be dropped"),
                new Field("error_message", Type.NULLABLE_STRING, "The global error message summarizing the common root cause of the records that caused the batch to be dropped")
            );
        
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
        
        public PartitionProduceResponse(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public PartitionProduceResponse(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public PartitionProduceResponse() {
            this.partitionIndex = 0;
            this.errorCode = (short) 0;
            this.baseOffset = 0L;
            this.logAppendTimeMs = -1L;
            this.logStartOffset = -1L;
            this.recordErrors = new ArrayList<BatchIndexAndErrorMessage>();
            this.errorMessage = null;
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of PartitionProduceResponse");
            }
            this.partitionIndex = _readable.readInt();
            this.errorCode = _readable.readShort();
            this.baseOffset = _readable.readLong();
            if (_version >= 2) {
                this.logAppendTimeMs = _readable.readLong();
            } else {
                this.logAppendTimeMs = -1L;
            }
            if (_version >= 5) {
                this.logStartOffset = _readable.readLong();
            } else {
                this.logStartOffset = -1L;
            }
            if (_version >= 8) {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field recordErrors was serialized as null");
                } else {
                    ArrayList<BatchIndexAndErrorMessage> newCollection = new ArrayList<BatchIndexAndErrorMessage>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new BatchIndexAndErrorMessage(_readable, _version));
                    }
                    this.recordErrors = newCollection;
                }
            } else {
                this.recordErrors = new ArrayList<BatchIndexAndErrorMessage>();
            }
            if (_version >= 8) {
                int length;
                length = _readable.readShort();
                if (length < 0) {
                    this.errorMessage = null;
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field errorMessage had invalid length " + length);
                } else {
                    this.errorMessage = _readable.readString(length);
                }
            } else {
                this.errorMessage = null;
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of PartitionProduceResponse");
            }
            int _numTaggedFields = 0;
            _writable.writeInt(partitionIndex);
            _writable.writeShort(errorCode);
            _writable.writeLong(baseOffset);
            if (_version >= 2) {
                _writable.writeLong(logAppendTimeMs);
            }
            if (_version >= 5) {
                _writable.writeLong(logStartOffset);
            }
            if (_version >= 8) {
                _writable.writeInt(recordErrors.size());
                for (BatchIndexAndErrorMessage recordErrorsElement : recordErrors) {
                    recordErrorsElement.write(_writable, _cache, _version);
                }
            }
            if (_version >= 8) {
                if (errorMessage == null) {
                    _writable.writeShort((short) -1);
                } else {
                    byte[] _stringBytes = _cache.getSerializedValue(errorMessage);
                    _writable.writeShort((short) _stringBytes.length);
                    _writable.writeByteArray(_stringBytes);
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
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of PartitionProduceResponse");
            }
            this._unknownTaggedFields = null;
            this.partitionIndex = struct.getInt("partition_index");
            this.errorCode = struct.getShort("error_code");
            this.baseOffset = struct.getLong("base_offset");
            if (_version >= 2) {
                this.logAppendTimeMs = struct.getLong("log_append_time_ms");
            } else {
                this.logAppendTimeMs = -1L;
            }
            if (_version >= 5) {
                this.logStartOffset = struct.getLong("log_start_offset");
            } else {
                this.logStartOffset = -1L;
            }
            if (_version >= 8) {
                Object[] _nestedObjects = struct.getArray("record_errors");
                this.recordErrors = new ArrayList<BatchIndexAndErrorMessage>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.recordErrors.add(new BatchIndexAndErrorMessage((Struct) nestedObject, _version));
                }
            } else {
                this.recordErrors = new ArrayList<BatchIndexAndErrorMessage>();
            }
            if (_version >= 8) {
                this.errorMessage = struct.getString("error_message");
            } else {
                this.errorMessage = null;
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of PartitionProduceResponse");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("partition_index", this.partitionIndex);
            struct.set("error_code", this.errorCode);
            struct.set("base_offset", this.baseOffset);
            if (_version >= 2) {
                struct.set("log_append_time_ms", this.logAppendTimeMs);
            }
            if (_version >= 5) {
                struct.set("log_start_offset", this.logStartOffset);
            }
            if (_version >= 8) {
                Struct[] _nestedObjects = new Struct[recordErrors.size()];
                int i = 0;
                for (BatchIndexAndErrorMessage element : this.recordErrors) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("record_errors", (Object[]) _nestedObjects);
            }
            if (_version >= 8) {
                struct.set("error_message", this.errorMessage);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of PartitionProduceResponse");
            }
            _size += 4;
            _size += 2;
            _size += 8;
            if (_version >= 2) {
                _size += 8;
            }
            if (_version >= 5) {
                _size += 8;
            }
            if (_version >= 8) {
                {
                    int _arraySize = 0;
                    _arraySize += 4;
                    for (BatchIndexAndErrorMessage recordErrorsElement : recordErrors) {
                        _arraySize += recordErrorsElement.size(_cache, _version);
                    }
                    _size += _arraySize;
                }
            }
            if (_version >= 8) {
                if (errorMessage == null) {
                    _size += 2;
                } else {
                    byte[] _stringBytes = errorMessage.getBytes(StandardCharsets.UTF_8);
                    if (_stringBytes.length > 0x7fff) {
                        throw new RuntimeException("'errorMessage' field is too long to be serialized");
                    }
                    _cache.cacheSerializedValue(errorMessage, _stringBytes);
                    _size += _stringBytes.length + 2;
                }
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
            if (!(obj instanceof PartitionProduceResponse)) return false;
            PartitionProduceResponse other = (PartitionProduceResponse) obj;
            if (partitionIndex != other.partitionIndex) return false;
            if (errorCode != other.errorCode) return false;
            if (baseOffset != other.baseOffset) return false;
            if (logAppendTimeMs != other.logAppendTimeMs) return false;
            if (logStartOffset != other.logStartOffset) return false;
            if (this.recordErrors == null) {
                if (other.recordErrors != null) return false;
            } else {
                if (!this.recordErrors.equals(other.recordErrors)) return false;
            }
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
            hashCode = 31 * hashCode + ((int) (baseOffset >> 32) ^ (int) baseOffset);
            hashCode = 31 * hashCode + ((int) (logAppendTimeMs >> 32) ^ (int) logAppendTimeMs);
            hashCode = 31 * hashCode + ((int) (logStartOffset >> 32) ^ (int) logStartOffset);
            hashCode = 31 * hashCode + (recordErrors == null ? 0 : recordErrors.hashCode());
            hashCode = 31 * hashCode + (errorMessage == null ? 0 : errorMessage.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "PartitionProduceResponse("
                + "partitionIndex=" + partitionIndex
                + ", errorCode=" + errorCode
                + ", baseOffset=" + baseOffset
                + ", logAppendTimeMs=" + logAppendTimeMs
                + ", logStartOffset=" + logStartOffset
                + ", recordErrors=" + MessageUtil.deepToString(recordErrors.iterator())
                + ", errorMessage=" + ((errorMessage == null) ? "null" : "'" + errorMessage.toString() + "'")
                + ")";
        }
        
        public int partitionIndex() {
            return this.partitionIndex;
        }
        
        public short errorCode() {
            return this.errorCode;
        }
        
        public long baseOffset() {
            return this.baseOffset;
        }
        
        public long logAppendTimeMs() {
            return this.logAppendTimeMs;
        }
        
        public long logStartOffset() {
            return this.logStartOffset;
        }
        
        public List<BatchIndexAndErrorMessage> recordErrors() {
            return this.recordErrors;
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
        
        public PartitionProduceResponse setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
        
        public PartitionProduceResponse setErrorCode(short v) {
            this.errorCode = v;
            return this;
        }
        
        public PartitionProduceResponse setBaseOffset(long v) {
            this.baseOffset = v;
            return this;
        }
        
        public PartitionProduceResponse setLogAppendTimeMs(long v) {
            this.logAppendTimeMs = v;
            return this;
        }
        
        public PartitionProduceResponse setLogStartOffset(long v) {
            this.logStartOffset = v;
            return this;
        }
        
        public PartitionProduceResponse setRecordErrors(List<BatchIndexAndErrorMessage> v) {
            this.recordErrors = v;
            return this;
        }
        
        public PartitionProduceResponse setErrorMessage(String v) {
            this.errorMessage = v;
            return this;
        }
    }
    
    static public class BatchIndexAndErrorMessage implements Message {
        private int batchIndex;
        private String batchIndexErrorMessage;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_8 =
            new Schema(
                new Field("batch_index", Type.INT32, "The batch index of the record that cause the batch to be dropped"),
                new Field("batch_index_error_message", Type.NULLABLE_STRING, "The error message of the record that caused the batch to be dropped")
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            SCHEMA_8
        };
        
        public BatchIndexAndErrorMessage(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public BatchIndexAndErrorMessage(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public BatchIndexAndErrorMessage() {
            this.batchIndex = 0;
            this.batchIndexErrorMessage = null;
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of BatchIndexAndErrorMessage");
            }
            this.batchIndex = _readable.readInt();
            {
                int length;
                length = _readable.readShort();
                if (length < 0) {
                    this.batchIndexErrorMessage = null;
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field batchIndexErrorMessage had invalid length " + length);
                } else {
                    this.batchIndexErrorMessage = _readable.readString(length);
                }
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of BatchIndexAndErrorMessage");
            }
            int _numTaggedFields = 0;
            _writable.writeInt(batchIndex);
            if (batchIndexErrorMessage == null) {
                _writable.writeShort((short) -1);
            } else {
                byte[] _stringBytes = _cache.getSerializedValue(batchIndexErrorMessage);
                _writable.writeShort((short) _stringBytes.length);
                _writable.writeByteArray(_stringBytes);
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of BatchIndexAndErrorMessage");
            }
            this._unknownTaggedFields = null;
            this.batchIndex = struct.getInt("batch_index");
            this.batchIndexErrorMessage = struct.getString("batch_index_error_message");
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of BatchIndexAndErrorMessage");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("batch_index", this.batchIndex);
            struct.set("batch_index_error_message", this.batchIndexErrorMessage);
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of BatchIndexAndErrorMessage");
            }
            _size += 4;
            if (batchIndexErrorMessage == null) {
                _size += 2;
            } else {
                byte[] _stringBytes = batchIndexErrorMessage.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'batchIndexErrorMessage' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(batchIndexErrorMessage, _stringBytes);
                _size += _stringBytes.length + 2;
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
            if (!(obj instanceof BatchIndexAndErrorMessage)) return false;
            BatchIndexAndErrorMessage other = (BatchIndexAndErrorMessage) obj;
            if (batchIndex != other.batchIndex) return false;
            if (this.batchIndexErrorMessage == null) {
                if (other.batchIndexErrorMessage != null) return false;
            } else {
                if (!this.batchIndexErrorMessage.equals(other.batchIndexErrorMessage)) return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + batchIndex;
            hashCode = 31 * hashCode + (batchIndexErrorMessage == null ? 0 : batchIndexErrorMessage.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "BatchIndexAndErrorMessage("
                + "batchIndex=" + batchIndex
                + ", batchIndexErrorMessage=" + ((batchIndexErrorMessage == null) ? "null" : "'" + batchIndexErrorMessage.toString() + "'")
                + ")";
        }
        
        public int batchIndex() {
            return this.batchIndex;
        }
        
        public String batchIndexErrorMessage() {
            return this.batchIndexErrorMessage;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public BatchIndexAndErrorMessage setBatchIndex(int v) {
            this.batchIndex = v;
            return this;
        }
        
        public BatchIndexAndErrorMessage setBatchIndexErrorMessage(String v) {
            this.batchIndexErrorMessage = v;
            return this;
        }
    }
}
