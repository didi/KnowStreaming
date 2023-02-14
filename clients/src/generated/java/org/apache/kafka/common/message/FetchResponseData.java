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


public class FetchResponseData implements ApiMessage {
    private int throttleTimeMs;
    private short errorCode;
    private int sessionId;
    private List<FetchableTopicResponse> topics;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("topics", new ArrayOf(FetchableTopicResponse.SCHEMA_0), "The response topics.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("topics", new ArrayOf(FetchableTopicResponse.SCHEMA_0), "The response topics.")
        );
    
    public static final Schema SCHEMA_2 = SCHEMA_1;
    
    public static final Schema SCHEMA_3 = SCHEMA_2;
    
    public static final Schema SCHEMA_4 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("topics", new ArrayOf(FetchableTopicResponse.SCHEMA_4), "The response topics.")
        );
    
    public static final Schema SCHEMA_5 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("topics", new ArrayOf(FetchableTopicResponse.SCHEMA_5), "The response topics.")
        );
    
    public static final Schema SCHEMA_6 = SCHEMA_5;
    
    public static final Schema SCHEMA_7 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("error_code", Type.INT16, "The top level response error code."),
            new Field("session_id", Type.INT32, "The fetch session ID, or 0 if this is not part of a fetch session."),
            new Field("topics", new ArrayOf(FetchableTopicResponse.SCHEMA_5), "The response topics.")
        );
    
    public static final Schema SCHEMA_8 = SCHEMA_7;
    
    public static final Schema SCHEMA_9 = SCHEMA_8;
    
    public static final Schema SCHEMA_10 = SCHEMA_9;
    
    public static final Schema SCHEMA_11 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("error_code", Type.INT16, "The top level response error code."),
            new Field("session_id", Type.INT32, "The fetch session ID, or 0 if this is not part of a fetch session."),
            new Field("topics", new ArrayOf(FetchableTopicResponse.SCHEMA_11), "The response topics.")
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
        SCHEMA_8,
        SCHEMA_9,
        SCHEMA_10,
        SCHEMA_11
    };
    
    public FetchResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public FetchResponseData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public FetchResponseData() {
        this.throttleTimeMs = 0;
        this.errorCode = (short) 0;
        this.sessionId = 0;
        this.topics = new ArrayList<FetchableTopicResponse>();
    }
    
    @Override
    public short apiKey() {
        return 1;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 11;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        if (_version >= 1) {
            this.throttleTimeMs = _readable.readInt();
        } else {
            this.throttleTimeMs = 0;
        }
        if (_version >= 7) {
            this.errorCode = _readable.readShort();
        } else {
            this.errorCode = (short) 0;
        }
        if (_version >= 7) {
            this.sessionId = _readable.readInt();
        } else {
            this.sessionId = 0;
        }
        {
            int arrayLength;
            arrayLength = _readable.readInt();
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field topics was serialized as null");
            } else {
                ArrayList<FetchableTopicResponse> newCollection = new ArrayList<FetchableTopicResponse>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new FetchableTopicResponse(_readable, _version));
                }
                this.topics = newCollection;
            }
        }
        this._unknownTaggedFields = null;
    }
    
    @Override
    public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        if (_version >= 1) {
            _writable.writeInt(throttleTimeMs);
        }
        if (_version >= 7) {
            _writable.writeShort(errorCode);
        } else {
            if (errorCode != (short) 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default errorCode at version " + _version);
            }
        }
        if (_version >= 7) {
            _writable.writeInt(sessionId);
        } else {
            if (sessionId != 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default sessionId at version " + _version);
            }
        }
        _writable.writeInt(topics.size());
        for (FetchableTopicResponse topicsElement : topics) {
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
        if (_version >= 1) {
            this.throttleTimeMs = struct.getInt("throttle_time_ms");
        } else {
            this.throttleTimeMs = 0;
        }
        if (_version >= 7) {
            this.errorCode = struct.getShort("error_code");
        } else {
            this.errorCode = (short) 0;
        }
        if (_version >= 7) {
            this.sessionId = struct.getInt("session_id");
        } else {
            this.sessionId = 0;
        }
        {
            Object[] _nestedObjects = struct.getArray("topics");
            this.topics = new ArrayList<FetchableTopicResponse>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.topics.add(new FetchableTopicResponse((Struct) nestedObject, _version));
            }
        }
    }
    
    @Override
    public Struct toStruct(short _version) {
        TreeMap<Integer, Object> _taggedFields = null;
        Struct struct = new Struct(SCHEMAS[_version]);
        if (_version >= 1) {
            struct.set("throttle_time_ms", this.throttleTimeMs);
        }
        if (_version >= 7) {
            struct.set("error_code", this.errorCode);
        } else {
            if (errorCode != (short) 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default errorCode at version " + _version);
            }
        }
        if (_version >= 7) {
            struct.set("session_id", this.sessionId);
        } else {
            if (sessionId != 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default sessionId at version " + _version);
            }
        }
        {
            Struct[] _nestedObjects = new Struct[topics.size()];
            int i = 0;
            for (FetchableTopicResponse element : this.topics) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("topics", (Object[]) _nestedObjects);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        if (_version >= 1) {
            _size += 4;
        }
        if (_version >= 7) {
            _size += 2;
        }
        if (_version >= 7) {
            _size += 4;
        }
        {
            int _arraySize = 0;
            _arraySize += 4;
            for (FetchableTopicResponse topicsElement : topics) {
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
        if (!(obj instanceof FetchResponseData)) return false;
        FetchResponseData other = (FetchResponseData) obj;
        if (throttleTimeMs != other.throttleTimeMs) return false;
        if (errorCode != other.errorCode) return false;
        if (sessionId != other.sessionId) return false;
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
        hashCode = 31 * hashCode + errorCode;
        hashCode = 31 * hashCode + sessionId;
        hashCode = 31 * hashCode + (topics == null ? 0 : topics.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "FetchResponseData("
            + "throttleTimeMs=" + throttleTimeMs
            + ", errorCode=" + errorCode
            + ", sessionId=" + sessionId
            + ", topics=" + MessageUtil.deepToString(topics.iterator())
            + ")";
    }
    
    public int throttleTimeMs() {
        return this.throttleTimeMs;
    }
    
    public short errorCode() {
        return this.errorCode;
    }
    
    public int sessionId() {
        return this.sessionId;
    }
    
    public List<FetchableTopicResponse> topics() {
        return this.topics;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public FetchResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
    
    public FetchResponseData setErrorCode(short v) {
        this.errorCode = v;
        return this;
    }
    
    public FetchResponseData setSessionId(int v) {
        this.sessionId = v;
        return this;
    }
    
    public FetchResponseData setTopics(List<FetchableTopicResponse> v) {
        this.topics = v;
        return this;
    }
    
    static public class FetchableTopicResponse implements Message {
        private String name;
        private List<FetchablePartitionResponse> partitions;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partitions", new ArrayOf(FetchablePartitionResponse.SCHEMA_0), "The topic partitions.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partitions", new ArrayOf(FetchablePartitionResponse.SCHEMA_4), "The topic partitions.")
            );
        
        public static final Schema SCHEMA_5 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partitions", new ArrayOf(FetchablePartitionResponse.SCHEMA_5), "The topic partitions.")
            );
        
        public static final Schema SCHEMA_6 = SCHEMA_5;
        
        public static final Schema SCHEMA_7 = SCHEMA_6;
        
        public static final Schema SCHEMA_8 = SCHEMA_7;
        
        public static final Schema SCHEMA_9 = SCHEMA_8;
        
        public static final Schema SCHEMA_10 = SCHEMA_9;
        
        public static final Schema SCHEMA_11 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partitions", new ArrayOf(FetchablePartitionResponse.SCHEMA_11), "The topic partitions.")
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
            SCHEMA_8,
            SCHEMA_9,
            SCHEMA_10,
            SCHEMA_11
        };
        
        public FetchableTopicResponse(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public FetchableTopicResponse(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public FetchableTopicResponse() {
            this.name = "";
            this.partitions = new ArrayList<FetchablePartitionResponse>();
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 11;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of FetchableTopicResponse");
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
                    ArrayList<FetchablePartitionResponse> newCollection = new ArrayList<FetchablePartitionResponse>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new FetchablePartitionResponse(_readable, _version));
                    }
                    this.partitions = newCollection;
                }
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of FetchableTopicResponse");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                _writable.writeShort((short) _stringBytes.length);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeInt(partitions.size());
            for (FetchablePartitionResponse partitionsElement : partitions) {
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
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of FetchableTopicResponse");
            }
            this._unknownTaggedFields = null;
            this.name = struct.getString("name");
            {
                Object[] _nestedObjects = struct.getArray("partitions");
                this.partitions = new ArrayList<FetchablePartitionResponse>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.partitions.add(new FetchablePartitionResponse((Struct) nestedObject, _version));
                }
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of FetchableTopicResponse");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("name", this.name);
            {
                Struct[] _nestedObjects = new Struct[partitions.size()];
                int i = 0;
                for (FetchablePartitionResponse element : this.partitions) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("partitions", (Object[]) _nestedObjects);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of FetchableTopicResponse");
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
                for (FetchablePartitionResponse partitionsElement : partitions) {
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
            if (!(obj instanceof FetchableTopicResponse)) return false;
            FetchableTopicResponse other = (FetchableTopicResponse) obj;
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
            return "FetchableTopicResponse("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", partitions=" + MessageUtil.deepToString(partitions.iterator())
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public List<FetchablePartitionResponse> partitions() {
            return this.partitions;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public FetchableTopicResponse setName(String v) {
            this.name = v;
            return this;
        }
        
        public FetchableTopicResponse setPartitions(List<FetchablePartitionResponse> v) {
            this.partitions = v;
            return this;
        }
    }
    
    static public class FetchablePartitionResponse implements Message {
        private int partitionIndex;
        private short errorCode;
        private long highWatermark;
        private long lastStableOffset;
        private long logStartOffset;
        private List<AbortedTransaction> aborted;
        private int preferredReadReplica;
        private byte[] records;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partiiton index."),
                new Field("error_code", Type.INT16, "The error code, or 0 if there was no fetch error."),
                new Field("high_watermark", Type.INT64, "The current high water mark."),
                new Field("records", Type.NULLABLE_BYTES, "The record data.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partiiton index."),
                new Field("error_code", Type.INT16, "The error code, or 0 if there was no fetch error."),
                new Field("high_watermark", Type.INT64, "The current high water mark."),
                new Field("last_stable_offset", Type.INT64, "The last stable offset (or LSO) of the partition. This is the last offset such that the state of all transactional records prior to this offset have been decided (ABORTED or COMMITTED)"),
                new Field("aborted", ArrayOf.nullable(AbortedTransaction.SCHEMA_4), "The aborted transactions."),
                new Field("records", Type.NULLABLE_BYTES, "The record data.")
            );
        
        public static final Schema SCHEMA_5 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partiiton index."),
                new Field("error_code", Type.INT16, "The error code, or 0 if there was no fetch error."),
                new Field("high_watermark", Type.INT64, "The current high water mark."),
                new Field("last_stable_offset", Type.INT64, "The last stable offset (or LSO) of the partition. This is the last offset such that the state of all transactional records prior to this offset have been decided (ABORTED or COMMITTED)"),
                new Field("log_start_offset", Type.INT64, "The current log start offset."),
                new Field("aborted", ArrayOf.nullable(AbortedTransaction.SCHEMA_4), "The aborted transactions."),
                new Field("records", Type.NULLABLE_BYTES, "The record data.")
            );
        
        public static final Schema SCHEMA_6 = SCHEMA_5;
        
        public static final Schema SCHEMA_7 = SCHEMA_6;
        
        public static final Schema SCHEMA_8 = SCHEMA_7;
        
        public static final Schema SCHEMA_9 = SCHEMA_8;
        
        public static final Schema SCHEMA_10 = SCHEMA_9;
        
        public static final Schema SCHEMA_11 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partiiton index."),
                new Field("error_code", Type.INT16, "The error code, or 0 if there was no fetch error."),
                new Field("high_watermark", Type.INT64, "The current high water mark."),
                new Field("last_stable_offset", Type.INT64, "The last stable offset (or LSO) of the partition. This is the last offset such that the state of all transactional records prior to this offset have been decided (ABORTED or COMMITTED)"),
                new Field("log_start_offset", Type.INT64, "The current log start offset."),
                new Field("aborted", ArrayOf.nullable(AbortedTransaction.SCHEMA_4), "The aborted transactions."),
                new Field("preferred_read_replica", Type.INT32, "The preferred read replica for the consumer to use on its next fetch request"),
                new Field("records", Type.NULLABLE_BYTES, "The record data.")
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
            SCHEMA_8,
            SCHEMA_9,
            SCHEMA_10,
            SCHEMA_11
        };
        
        public FetchablePartitionResponse(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public FetchablePartitionResponse(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public FetchablePartitionResponse() {
            this.partitionIndex = 0;
            this.errorCode = (short) 0;
            this.highWatermark = 0L;
            this.lastStableOffset = -1L;
            this.logStartOffset = -1L;
            this.aborted = new ArrayList<AbortedTransaction>();
            this.preferredReadReplica = 0;
            this.records = Bytes.EMPTY;
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 11;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of FetchablePartitionResponse");
            }
            this.partitionIndex = _readable.readInt();
            this.errorCode = _readable.readShort();
            this.highWatermark = _readable.readLong();
            if (_version >= 4) {
                this.lastStableOffset = _readable.readLong();
            } else {
                this.lastStableOffset = -1L;
            }
            if (_version >= 5) {
                this.logStartOffset = _readable.readLong();
            } else {
                this.logStartOffset = -1L;
            }
            if (_version >= 4) {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    this.aborted = null;
                } else {
                    ArrayList<AbortedTransaction> newCollection = new ArrayList<AbortedTransaction>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new AbortedTransaction(_readable, _version));
                    }
                    this.aborted = newCollection;
                }
            } else {
                this.aborted = new ArrayList<AbortedTransaction>();
            }
            if (_version >= 11) {
                this.preferredReadReplica = _readable.readInt();
            } else {
                this.preferredReadReplica = 0;
            }
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
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of FetchablePartitionResponse");
            }
            int _numTaggedFields = 0;
            _writable.writeInt(partitionIndex);
            _writable.writeShort(errorCode);
            _writable.writeLong(highWatermark);
            if (_version >= 4) {
                _writable.writeLong(lastStableOffset);
            }
            if (_version >= 5) {
                _writable.writeLong(logStartOffset);
            }
            if (_version >= 4) {
                if (aborted == null) {
                    _writable.writeInt(-1);
                } else {
                    _writable.writeInt(aborted.size());
                    for (AbortedTransaction abortedElement : aborted) {
                        abortedElement.write(_writable, _cache, _version);
                    }
                }
            } else {
                if (aborted == null || !aborted.isEmpty()) {
                    throw new UnsupportedVersionException("Attempted to write a non-default aborted at version " + _version);
                }
            }
            if (_version >= 11) {
                _writable.writeInt(preferredReadReplica);
            }
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
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of FetchablePartitionResponse");
            }
            this._unknownTaggedFields = null;
            this.partitionIndex = struct.getInt("partition_index");
            this.errorCode = struct.getShort("error_code");
            this.highWatermark = struct.getLong("high_watermark");
            if (_version >= 4) {
                this.lastStableOffset = struct.getLong("last_stable_offset");
            } else {
                this.lastStableOffset = -1L;
            }
            if (_version >= 5) {
                this.logStartOffset = struct.getLong("log_start_offset");
            } else {
                this.logStartOffset = -1L;
            }
            if (_version >= 4) {
                Object[] _nestedObjects = struct.getArray("aborted");
                if (_nestedObjects == null) {
                    this.aborted = null;
                } else {
                    this.aborted = new ArrayList<AbortedTransaction>(_nestedObjects.length);
                    for (Object nestedObject : _nestedObjects) {
                        this.aborted.add(new AbortedTransaction((Struct) nestedObject, _version));
                    }
                }
            } else {
                this.aborted = new ArrayList<AbortedTransaction>();
            }
            if (_version >= 11) {
                this.preferredReadReplica = struct.getInt("preferred_read_replica");
            } else {
                this.preferredReadReplica = 0;
            }
            this.records = struct.getByteArray("records");
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of FetchablePartitionResponse");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("partition_index", this.partitionIndex);
            struct.set("error_code", this.errorCode);
            struct.set("high_watermark", this.highWatermark);
            if (_version >= 4) {
                struct.set("last_stable_offset", this.lastStableOffset);
            }
            if (_version >= 5) {
                struct.set("log_start_offset", this.logStartOffset);
            }
            if (_version >= 4) {
                if (aborted == null) {
                    struct.set("aborted", null);
                } else {
                    Struct[] _nestedObjects = new Struct[aborted.size()];
                    int i = 0;
                    for (AbortedTransaction element : this.aborted) {
                        _nestedObjects[i++] = element.toStruct(_version);
                    }
                    struct.set("aborted", (Object[]) _nestedObjects);
                }
            } else {
                if (aborted == null || !aborted.isEmpty()) {
                    throw new UnsupportedVersionException("Attempted to write a non-default aborted at version " + _version);
                }
            }
            if (_version >= 11) {
                struct.set("preferred_read_replica", this.preferredReadReplica);
            }
            struct.setByteArray("records", this.records);
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of FetchablePartitionResponse");
            }
            _size += 4;
            _size += 2;
            _size += 8;
            if (_version >= 4) {
                _size += 8;
            }
            if (_version >= 5) {
                _size += 8;
            }
            if (_version >= 4) {
                if (aborted == null) {
                    _size += 4;
                } else {
                    int _arraySize = 0;
                    _arraySize += 4;
                    for (AbortedTransaction abortedElement : aborted) {
                        _arraySize += abortedElement.size(_cache, _version);
                    }
                    _size += _arraySize;
                }
            }
            if (_version >= 11) {
                _size += 4;
            }
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
            if (!(obj instanceof FetchablePartitionResponse)) return false;
            FetchablePartitionResponse other = (FetchablePartitionResponse) obj;
            if (partitionIndex != other.partitionIndex) return false;
            if (errorCode != other.errorCode) return false;
            if (highWatermark != other.highWatermark) return false;
            if (lastStableOffset != other.lastStableOffset) return false;
            if (logStartOffset != other.logStartOffset) return false;
            if (this.aborted == null) {
                if (other.aborted != null) return false;
            } else {
                if (!this.aborted.equals(other.aborted)) return false;
            }
            if (preferredReadReplica != other.preferredReadReplica) return false;
            if (!Arrays.equals(this.records, other.records)) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + partitionIndex;
            hashCode = 31 * hashCode + errorCode;
            hashCode = 31 * hashCode + ((int) (highWatermark >> 32) ^ (int) highWatermark);
            hashCode = 31 * hashCode + ((int) (lastStableOffset >> 32) ^ (int) lastStableOffset);
            hashCode = 31 * hashCode + ((int) (logStartOffset >> 32) ^ (int) logStartOffset);
            hashCode = 31 * hashCode + (aborted == null ? 0 : aborted.hashCode());
            hashCode = 31 * hashCode + preferredReadReplica;
            hashCode = 31 * hashCode + Arrays.hashCode(records);
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "FetchablePartitionResponse("
                + "partitionIndex=" + partitionIndex
                + ", errorCode=" + errorCode
                + ", highWatermark=" + highWatermark
                + ", lastStableOffset=" + lastStableOffset
                + ", logStartOffset=" + logStartOffset
                + ", aborted=" + ((aborted == null) ? "null" : MessageUtil.deepToString(aborted.iterator()))
                + ", preferredReadReplica=" + preferredReadReplica
                + ", records=" + Arrays.toString(records)
                + ")";
        }
        
        public int partitionIndex() {
            return this.partitionIndex;
        }
        
        public short errorCode() {
            return this.errorCode;
        }
        
        public long highWatermark() {
            return this.highWatermark;
        }
        
        public long lastStableOffset() {
            return this.lastStableOffset;
        }
        
        public long logStartOffset() {
            return this.logStartOffset;
        }
        
        public List<AbortedTransaction> aborted() {
            return this.aborted;
        }
        
        public int preferredReadReplica() {
            return this.preferredReadReplica;
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
        
        public FetchablePartitionResponse setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
        
        public FetchablePartitionResponse setErrorCode(short v) {
            this.errorCode = v;
            return this;
        }
        
        public FetchablePartitionResponse setHighWatermark(long v) {
            this.highWatermark = v;
            return this;
        }
        
        public FetchablePartitionResponse setLastStableOffset(long v) {
            this.lastStableOffset = v;
            return this;
        }
        
        public FetchablePartitionResponse setLogStartOffset(long v) {
            this.logStartOffset = v;
            return this;
        }
        
        public FetchablePartitionResponse setAborted(List<AbortedTransaction> v) {
            this.aborted = v;
            return this;
        }
        
        public FetchablePartitionResponse setPreferredReadReplica(int v) {
            this.preferredReadReplica = v;
            return this;
        }
        
        public FetchablePartitionResponse setRecords(byte[] v) {
            this.records = v;
            return this;
        }
    }
    
    static public class AbortedTransaction implements Message {
        private long producerId;
        private long firstOffset;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_4 =
            new Schema(
                new Field("producer_id", Type.INT64, "The producer id associated with the aborted transaction."),
                new Field("first_offset", Type.INT64, "The first offset in the aborted transaction.")
            );
        
        public static final Schema SCHEMA_5 = SCHEMA_4;
        
        public static final Schema SCHEMA_6 = SCHEMA_5;
        
        public static final Schema SCHEMA_7 = SCHEMA_6;
        
        public static final Schema SCHEMA_8 = SCHEMA_7;
        
        public static final Schema SCHEMA_9 = SCHEMA_8;
        
        public static final Schema SCHEMA_10 = SCHEMA_9;
        
        public static final Schema SCHEMA_11 = SCHEMA_10;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            null,
            null,
            null,
            null,
            SCHEMA_4,
            SCHEMA_5,
            SCHEMA_6,
            SCHEMA_7,
            SCHEMA_8,
            SCHEMA_9,
            SCHEMA_10,
            SCHEMA_11
        };
        
        public AbortedTransaction(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public AbortedTransaction(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public AbortedTransaction() {
            this.producerId = 0L;
            this.firstOffset = 0L;
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 11;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of AbortedTransaction");
            }
            this.producerId = _readable.readLong();
            this.firstOffset = _readable.readLong();
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of AbortedTransaction");
            }
            int _numTaggedFields = 0;
            _writable.writeLong(producerId);
            _writable.writeLong(firstOffset);
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public void fromStruct(Struct struct, short _version) {
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of AbortedTransaction");
            }
            this._unknownTaggedFields = null;
            this.producerId = struct.getLong("producer_id");
            this.firstOffset = struct.getLong("first_offset");
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of AbortedTransaction");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("producer_id", this.producerId);
            struct.set("first_offset", this.firstOffset);
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of AbortedTransaction");
            }
            _size += 8;
            _size += 8;
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
            if (!(obj instanceof AbortedTransaction)) return false;
            AbortedTransaction other = (AbortedTransaction) obj;
            if (producerId != other.producerId) return false;
            if (firstOffset != other.firstOffset) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + ((int) (producerId >> 32) ^ (int) producerId);
            hashCode = 31 * hashCode + ((int) (firstOffset >> 32) ^ (int) firstOffset);
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "AbortedTransaction("
                + "producerId=" + producerId
                + ", firstOffset=" + firstOffset
                + ")";
        }
        
        public long producerId() {
            return this.producerId;
        }
        
        public long firstOffset() {
            return this.firstOffset;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public AbortedTransaction setProducerId(long v) {
            this.producerId = v;
            return this;
        }
        
        public AbortedTransaction setFirstOffset(long v) {
            this.firstOffset = v;
            return this;
        }
    }
}
