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


public class ElectLeadersResponseData implements ApiMessage {
    private int throttleTimeMs;
    private short errorCode;
    private List<ReplicaElectionResult> replicaElectionResults;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("replica_election_results", new ArrayOf(ReplicaElectionResult.SCHEMA_0), "The election results, or an empty array if the requester did not have permission and the request asks for all partitions.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("error_code", Type.INT16, "The top level response error code."),
            new Field("replica_election_results", new ArrayOf(ReplicaElectionResult.SCHEMA_0), "The election results, or an empty array if the requester did not have permission and the request asks for all partitions.")
        );
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("error_code", Type.INT16, "The top level response error code."),
            new Field("replica_election_results", new CompactArrayOf(ReplicaElectionResult.SCHEMA_2), "The election results, or an empty array if the requester did not have permission and the request asks for all partitions."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2
    };
    
    public ElectLeadersResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public ElectLeadersResponseData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public ElectLeadersResponseData() {
        this.throttleTimeMs = 0;
        this.errorCode = (short) 0;
        this.replicaElectionResults = new ArrayList<ReplicaElectionResult>();
    }
    
    @Override
    public short apiKey() {
        return 43;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 2;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        this.throttleTimeMs = _readable.readInt();
        if (_version >= 1) {
            this.errorCode = _readable.readShort();
        } else {
            this.errorCode = (short) 0;
        }
        {
            if (_version >= 2) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field replicaElectionResults was serialized as null");
                } else {
                    ArrayList<ReplicaElectionResult> newCollection = new ArrayList<ReplicaElectionResult>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new ReplicaElectionResult(_readable, _version));
                    }
                    this.replicaElectionResults = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field replicaElectionResults was serialized as null");
                } else {
                    ArrayList<ReplicaElectionResult> newCollection = new ArrayList<ReplicaElectionResult>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new ReplicaElectionResult(_readable, _version));
                    }
                    this.replicaElectionResults = newCollection;
                }
            }
        }
        this._unknownTaggedFields = null;
        if (_version >= 2) {
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
        _writable.writeInt(throttleTimeMs);
        if (_version >= 1) {
            _writable.writeShort(errorCode);
        } else {
            if (errorCode != (short) 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default errorCode at version " + _version);
            }
        }
        if (_version >= 2) {
            _writable.writeUnsignedVarint(replicaElectionResults.size() + 1);
            for (ReplicaElectionResult replicaElectionResultsElement : replicaElectionResults) {
                replicaElectionResultsElement.write(_writable, _cache, _version);
            }
        } else {
            _writable.writeInt(replicaElectionResults.size());
            for (ReplicaElectionResult replicaElectionResultsElement : replicaElectionResults) {
                replicaElectionResultsElement.write(_writable, _cache, _version);
            }
        }
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        if (_version >= 2) {
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
        if (_version >= 2) {
            _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
        }
        this.throttleTimeMs = struct.getInt("throttle_time_ms");
        if (_version >= 1) {
            this.errorCode = struct.getShort("error_code");
        } else {
            this.errorCode = (short) 0;
        }
        {
            Object[] _nestedObjects = struct.getArray("replica_election_results");
            this.replicaElectionResults = new ArrayList<ReplicaElectionResult>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.replicaElectionResults.add(new ReplicaElectionResult((Struct) nestedObject, _version));
            }
        }
        if (_version >= 2) {
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
        if (_version >= 2) {
            _taggedFields = new TreeMap<>();
        }
        Struct struct = new Struct(SCHEMAS[_version]);
        struct.set("throttle_time_ms", this.throttleTimeMs);
        if (_version >= 1) {
            struct.set("error_code", this.errorCode);
        } else {
            if (errorCode != (short) 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default errorCode at version " + _version);
            }
        }
        {
            Struct[] _nestedObjects = new Struct[replicaElectionResults.size()];
            int i = 0;
            for (ReplicaElectionResult element : this.replicaElectionResults) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("replica_election_results", (Object[]) _nestedObjects);
        }
        if (_version >= 2) {
            struct.set("_tagged_fields", _taggedFields);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        _size += 4;
        if (_version >= 1) {
            _size += 2;
        }
        {
            int _arraySize = 0;
            if (_version >= 2) {
                _arraySize += ByteUtils.sizeOfUnsignedVarint(replicaElectionResults.size() + 1);
            } else {
                _arraySize += 4;
            }
            for (ReplicaElectionResult replicaElectionResultsElement : replicaElectionResults) {
                _arraySize += replicaElectionResultsElement.size(_cache, _version);
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
        if (_version >= 2) {
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
        if (!(obj instanceof ElectLeadersResponseData)) return false;
        ElectLeadersResponseData other = (ElectLeadersResponseData) obj;
        if (throttleTimeMs != other.throttleTimeMs) return false;
        if (errorCode != other.errorCode) return false;
        if (this.replicaElectionResults == null) {
            if (other.replicaElectionResults != null) return false;
        } else {
            if (!this.replicaElectionResults.equals(other.replicaElectionResults)) return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + throttleTimeMs;
        hashCode = 31 * hashCode + errorCode;
        hashCode = 31 * hashCode + (replicaElectionResults == null ? 0 : replicaElectionResults.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "ElectLeadersResponseData("
            + "throttleTimeMs=" + throttleTimeMs
            + ", errorCode=" + errorCode
            + ", replicaElectionResults=" + MessageUtil.deepToString(replicaElectionResults.iterator())
            + ")";
    }
    
    public int throttleTimeMs() {
        return this.throttleTimeMs;
    }
    
    public short errorCode() {
        return this.errorCode;
    }
    
    public List<ReplicaElectionResult> replicaElectionResults() {
        return this.replicaElectionResults;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public ElectLeadersResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
    
    public ElectLeadersResponseData setErrorCode(short v) {
        this.errorCode = v;
        return this;
    }
    
    public ElectLeadersResponseData setReplicaElectionResults(List<ReplicaElectionResult> v) {
        this.replicaElectionResults = v;
        return this;
    }
    
    static public class ReplicaElectionResult implements Message {
        private String topic;
        private List<PartitionResult> partitionResult;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("topic", Type.STRING, "The topic name"),
                new Field("partition_result", new ArrayOf(PartitionResult.SCHEMA_0), "The results for each partition")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 =
            new Schema(
                new Field("topic", Type.COMPACT_STRING, "The topic name"),
                new Field("partition_result", new CompactArrayOf(PartitionResult.SCHEMA_2), "The results for each partition"),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2
        };
        
        public ReplicaElectionResult(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public ReplicaElectionResult(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public ReplicaElectionResult() {
            this.topic = "";
            this.partitionResult = new ArrayList<PartitionResult>();
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 2;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of ReplicaElectionResult");
            }
            {
                int length;
                if (_version >= 2) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field topic was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field topic had invalid length " + length);
                } else {
                    this.topic = _readable.readString(length);
                }
            }
            {
                if (_version >= 2) {
                    int arrayLength;
                    arrayLength = _readable.readUnsignedVarint() - 1;
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field partitionResult was serialized as null");
                    } else {
                        ArrayList<PartitionResult> newCollection = new ArrayList<PartitionResult>(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new PartitionResult(_readable, _version));
                        }
                        this.partitionResult = newCollection;
                    }
                } else {
                    int arrayLength;
                    arrayLength = _readable.readInt();
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field partitionResult was serialized as null");
                    } else {
                        ArrayList<PartitionResult> newCollection = new ArrayList<PartitionResult>(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new PartitionResult(_readable, _version));
                        }
                        this.partitionResult = newCollection;
                    }
                }
            }
            this._unknownTaggedFields = null;
            if (_version >= 2) {
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
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of ReplicaElectionResult");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(topic);
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            if (_version >= 2) {
                _writable.writeUnsignedVarint(partitionResult.size() + 1);
                for (PartitionResult partitionResultElement : partitionResult) {
                    partitionResultElement.write(_writable, _cache, _version);
                }
            } else {
                _writable.writeInt(partitionResult.size());
                for (PartitionResult partitionResultElement : partitionResult) {
                    partitionResultElement.write(_writable, _cache, _version);
                }
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 2) {
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
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of ReplicaElectionResult");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 2) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.topic = struct.getString("topic");
            {
                Object[] _nestedObjects = struct.getArray("partition_result");
                this.partitionResult = new ArrayList<PartitionResult>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.partitionResult.add(new PartitionResult((Struct) nestedObject, _version));
                }
            }
            if (_version >= 2) {
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
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of ReplicaElectionResult");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 2) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("topic", this.topic);
            {
                Struct[] _nestedObjects = new Struct[partitionResult.size()];
                int i = 0;
                for (PartitionResult element : this.partitionResult) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("partition_result", (Object[]) _nestedObjects);
            }
            if (_version >= 2) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of ReplicaElectionResult");
            }
            {
                byte[] _stringBytes = topic.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'topic' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(topic, _stringBytes);
                if (_version >= 2) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
            }
            {
                int _arraySize = 0;
                if (_version >= 2) {
                    _arraySize += ByteUtils.sizeOfUnsignedVarint(partitionResult.size() + 1);
                } else {
                    _arraySize += 4;
                }
                for (PartitionResult partitionResultElement : partitionResult) {
                    _arraySize += partitionResultElement.size(_cache, _version);
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
            if (_version >= 2) {
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
            if (!(obj instanceof ReplicaElectionResult)) return false;
            ReplicaElectionResult other = (ReplicaElectionResult) obj;
            if (this.topic == null) {
                if (other.topic != null) return false;
            } else {
                if (!this.topic.equals(other.topic)) return false;
            }
            if (this.partitionResult == null) {
                if (other.partitionResult != null) return false;
            } else {
                if (!this.partitionResult.equals(other.partitionResult)) return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (topic == null ? 0 : topic.hashCode());
            hashCode = 31 * hashCode + (partitionResult == null ? 0 : partitionResult.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "ReplicaElectionResult("
                + "topic=" + ((topic == null) ? "null" : "'" + topic.toString() + "'")
                + ", partitionResult=" + MessageUtil.deepToString(partitionResult.iterator())
                + ")";
        }
        
        public String topic() {
            return this.topic;
        }
        
        public List<PartitionResult> partitionResult() {
            return this.partitionResult;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public ReplicaElectionResult setTopic(String v) {
            this.topic = v;
            return this;
        }
        
        public ReplicaElectionResult setPartitionResult(List<PartitionResult> v) {
            this.partitionResult = v;
            return this;
        }
    }
    
    static public class PartitionResult implements Message {
        private int partitionId;
        private short errorCode;
        private String errorMessage;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("partition_id", Type.INT32, "The partition id"),
                new Field("error_code", Type.INT16, "The result error, or zero if there was no error."),
                new Field("error_message", Type.NULLABLE_STRING, "The result message, or null if there was no error.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 =
            new Schema(
                new Field("partition_id", Type.INT32, "The partition id"),
                new Field("error_code", Type.INT16, "The result error, or zero if there was no error."),
                new Field("error_message", Type.COMPACT_NULLABLE_STRING, "The result message, or null if there was no error."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2
        };
        
        public PartitionResult(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public PartitionResult(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public PartitionResult() {
            this.partitionId = 0;
            this.errorCode = (short) 0;
            this.errorMessage = "";
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 2;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of PartitionResult");
            }
            this.partitionId = _readable.readInt();
            this.errorCode = _readable.readShort();
            {
                int length;
                if (_version >= 2) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    this.errorMessage = null;
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field errorMessage had invalid length " + length);
                } else {
                    this.errorMessage = _readable.readString(length);
                }
            }
            this._unknownTaggedFields = null;
            if (_version >= 2) {
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
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of PartitionResult");
            }
            int _numTaggedFields = 0;
            _writable.writeInt(partitionId);
            _writable.writeShort(errorCode);
            if (errorMessage == null) {
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(0);
                } else {
                    _writable.writeShort((short) -1);
                }
            } else {
                byte[] _stringBytes = _cache.getSerializedValue(errorMessage);
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 2) {
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
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of PartitionResult");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 2) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.partitionId = struct.getInt("partition_id");
            this.errorCode = struct.getShort("error_code");
            this.errorMessage = struct.getString("error_message");
            if (_version >= 2) {
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
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of PartitionResult");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 2) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("partition_id", this.partitionId);
            struct.set("error_code", this.errorCode);
            struct.set("error_message", this.errorMessage);
            if (_version >= 2) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of PartitionResult");
            }
            _size += 4;
            _size += 2;
            if (errorMessage == null) {
                if (_version >= 2) {
                    _size += 1;
                } else {
                    _size += 2;
                }
            } else {
                byte[] _stringBytes = errorMessage.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'errorMessage' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(errorMessage, _stringBytes);
                if (_version >= 2) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
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
            if (_version >= 2) {
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
            if (!(obj instanceof PartitionResult)) return false;
            PartitionResult other = (PartitionResult) obj;
            if (partitionId != other.partitionId) return false;
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
            hashCode = 31 * hashCode + partitionId;
            hashCode = 31 * hashCode + errorCode;
            hashCode = 31 * hashCode + (errorMessage == null ? 0 : errorMessage.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "PartitionResult("
                + "partitionId=" + partitionId
                + ", errorCode=" + errorCode
                + ", errorMessage=" + ((errorMessage == null) ? "null" : "'" + errorMessage.toString() + "'")
                + ")";
        }
        
        public int partitionId() {
            return this.partitionId;
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
        
        public PartitionResult setPartitionId(int v) {
            this.partitionId = v;
            return this;
        }
        
        public PartitionResult setErrorCode(short v) {
            this.errorCode = v;
            return this;
        }
        
        public PartitionResult setErrorMessage(String v) {
            this.errorMessage = v;
            return this;
        }
    }
}
