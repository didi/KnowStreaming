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


public class DescribeLogDirsResponseData implements ApiMessage {
    private int throttleTimeMs;
    private List<DescribeLogDirsResult> results;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("results", new ArrayOf(DescribeLogDirsResult.SCHEMA_0), "The log directories.")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1
    };
    
    public DescribeLogDirsResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public DescribeLogDirsResponseData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public DescribeLogDirsResponseData() {
        this.throttleTimeMs = 0;
        this.results = new ArrayList<DescribeLogDirsResult>();
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
        this.throttleTimeMs = _readable.readInt();
        {
            int arrayLength;
            arrayLength = _readable.readInt();
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field results was serialized as null");
            } else {
                ArrayList<DescribeLogDirsResult> newCollection = new ArrayList<DescribeLogDirsResult>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new DescribeLogDirsResult(_readable, _version));
                }
                this.results = newCollection;
            }
        }
        this._unknownTaggedFields = null;
    }
    
    @Override
    public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        _writable.writeInt(throttleTimeMs);
        _writable.writeInt(results.size());
        for (DescribeLogDirsResult resultsElement : results) {
            resultsElement.write(_writable, _cache, _version);
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
        this.throttleTimeMs = struct.getInt("throttle_time_ms");
        {
            Object[] _nestedObjects = struct.getArray("results");
            this.results = new ArrayList<DescribeLogDirsResult>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.results.add(new DescribeLogDirsResult((Struct) nestedObject, _version));
            }
        }
    }
    
    @Override
    public Struct toStruct(short _version) {
        TreeMap<Integer, Object> _taggedFields = null;
        Struct struct = new Struct(SCHEMAS[_version]);
        struct.set("throttle_time_ms", this.throttleTimeMs);
        {
            Struct[] _nestedObjects = new Struct[results.size()];
            int i = 0;
            for (DescribeLogDirsResult element : this.results) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("results", (Object[]) _nestedObjects);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        _size += 4;
        {
            int _arraySize = 0;
            _arraySize += 4;
            for (DescribeLogDirsResult resultsElement : results) {
                _arraySize += resultsElement.size(_cache, _version);
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
        if (!(obj instanceof DescribeLogDirsResponseData)) return false;
        DescribeLogDirsResponseData other = (DescribeLogDirsResponseData) obj;
        if (throttleTimeMs != other.throttleTimeMs) return false;
        if (this.results == null) {
            if (other.results != null) return false;
        } else {
            if (!this.results.equals(other.results)) return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + throttleTimeMs;
        hashCode = 31 * hashCode + (results == null ? 0 : results.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "DescribeLogDirsResponseData("
            + "throttleTimeMs=" + throttleTimeMs
            + ", results=" + MessageUtil.deepToString(results.iterator())
            + ")";
    }
    
    public int throttleTimeMs() {
        return this.throttleTimeMs;
    }
    
    public List<DescribeLogDirsResult> results() {
        return this.results;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public DescribeLogDirsResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
    
    public DescribeLogDirsResponseData setResults(List<DescribeLogDirsResult> v) {
        this.results = v;
        return this;
    }
    
    static public class DescribeLogDirsResult implements Message {
        private short errorCode;
        private String logDir;
        private List<DescribeLogDirsTopic> topics;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
                new Field("log_dir", Type.STRING, "The absolute log directory path."),
                new Field("topics", new ArrayOf(DescribeLogDirsTopic.SCHEMA_0), "Each topic.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1
        };
        
        public DescribeLogDirsResult(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public DescribeLogDirsResult(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public DescribeLogDirsResult() {
            this.errorCode = (short) 0;
            this.logDir = "";
            this.topics = new ArrayList<DescribeLogDirsTopic>();
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of DescribeLogDirsResult");
            }
            this.errorCode = _readable.readShort();
            {
                int length;
                length = _readable.readShort();
                if (length < 0) {
                    throw new RuntimeException("non-nullable field logDir was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field logDir had invalid length " + length);
                } else {
                    this.logDir = _readable.readString(length);
                }
            }
            {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topics was serialized as null");
                } else {
                    ArrayList<DescribeLogDirsTopic> newCollection = new ArrayList<DescribeLogDirsTopic>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new DescribeLogDirsTopic(_readable, _version));
                    }
                    this.topics = newCollection;
                }
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of DescribeLogDirsResult");
            }
            int _numTaggedFields = 0;
            _writable.writeShort(errorCode);
            {
                byte[] _stringBytes = _cache.getSerializedValue(logDir);
                _writable.writeShort((short) _stringBytes.length);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeInt(topics.size());
            for (DescribeLogDirsTopic topicsElement : topics) {
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
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of DescribeLogDirsResult");
            }
            this._unknownTaggedFields = null;
            this.errorCode = struct.getShort("error_code");
            this.logDir = struct.getString("log_dir");
            {
                Object[] _nestedObjects = struct.getArray("topics");
                this.topics = new ArrayList<DescribeLogDirsTopic>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.topics.add(new DescribeLogDirsTopic((Struct) nestedObject, _version));
                }
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of DescribeLogDirsResult");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("error_code", this.errorCode);
            struct.set("log_dir", this.logDir);
            {
                Struct[] _nestedObjects = new Struct[topics.size()];
                int i = 0;
                for (DescribeLogDirsTopic element : this.topics) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("topics", (Object[]) _nestedObjects);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of DescribeLogDirsResult");
            }
            _size += 2;
            {
                byte[] _stringBytes = logDir.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'logDir' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(logDir, _stringBytes);
                _size += _stringBytes.length + 2;
            }
            {
                int _arraySize = 0;
                _arraySize += 4;
                for (DescribeLogDirsTopic topicsElement : topics) {
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
            if (!(obj instanceof DescribeLogDirsResult)) return false;
            DescribeLogDirsResult other = (DescribeLogDirsResult) obj;
            if (errorCode != other.errorCode) return false;
            if (this.logDir == null) {
                if (other.logDir != null) return false;
            } else {
                if (!this.logDir.equals(other.logDir)) return false;
            }
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
            hashCode = 31 * hashCode + (logDir == null ? 0 : logDir.hashCode());
            hashCode = 31 * hashCode + (topics == null ? 0 : topics.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "DescribeLogDirsResult("
                + "errorCode=" + errorCode
                + ", logDir=" + ((logDir == null) ? "null" : "'" + logDir.toString() + "'")
                + ", topics=" + MessageUtil.deepToString(topics.iterator())
                + ")";
        }
        
        public short errorCode() {
            return this.errorCode;
        }
        
        public String logDir() {
            return this.logDir;
        }
        
        public List<DescribeLogDirsTopic> topics() {
            return this.topics;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public DescribeLogDirsResult setErrorCode(short v) {
            this.errorCode = v;
            return this;
        }
        
        public DescribeLogDirsResult setLogDir(String v) {
            this.logDir = v;
            return this;
        }
        
        public DescribeLogDirsResult setTopics(List<DescribeLogDirsTopic> v) {
            this.topics = v;
            return this;
        }
    }
    
    static public class DescribeLogDirsTopic implements Message {
        private String name;
        private List<DescribeLogDirsPartition> partitions;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partitions", new ArrayOf(DescribeLogDirsPartition.SCHEMA_0), "")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1
        };
        
        public DescribeLogDirsTopic(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public DescribeLogDirsTopic(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public DescribeLogDirsTopic() {
            this.name = "";
            this.partitions = new ArrayList<DescribeLogDirsPartition>();
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of DescribeLogDirsTopic");
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
                    ArrayList<DescribeLogDirsPartition> newCollection = new ArrayList<DescribeLogDirsPartition>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new DescribeLogDirsPartition(_readable, _version));
                    }
                    this.partitions = newCollection;
                }
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of DescribeLogDirsTopic");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                _writable.writeShort((short) _stringBytes.length);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeInt(partitions.size());
            for (DescribeLogDirsPartition partitionsElement : partitions) {
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
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of DescribeLogDirsTopic");
            }
            this._unknownTaggedFields = null;
            this.name = struct.getString("name");
            {
                Object[] _nestedObjects = struct.getArray("partitions");
                this.partitions = new ArrayList<DescribeLogDirsPartition>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.partitions.add(new DescribeLogDirsPartition((Struct) nestedObject, _version));
                }
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of DescribeLogDirsTopic");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("name", this.name);
            {
                Struct[] _nestedObjects = new Struct[partitions.size()];
                int i = 0;
                for (DescribeLogDirsPartition element : this.partitions) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("partitions", (Object[]) _nestedObjects);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of DescribeLogDirsTopic");
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
                for (DescribeLogDirsPartition partitionsElement : partitions) {
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
            if (!(obj instanceof DescribeLogDirsTopic)) return false;
            DescribeLogDirsTopic other = (DescribeLogDirsTopic) obj;
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
            return "DescribeLogDirsTopic("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", partitions=" + MessageUtil.deepToString(partitions.iterator())
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public List<DescribeLogDirsPartition> partitions() {
            return this.partitions;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public DescribeLogDirsTopic setName(String v) {
            this.name = v;
            return this;
        }
        
        public DescribeLogDirsTopic setPartitions(List<DescribeLogDirsPartition> v) {
            this.partitions = v;
            return this;
        }
    }
    
    static public class DescribeLogDirsPartition implements Message {
        private int partitionIndex;
        private long partitionSize;
        private long offsetLag;
        private boolean isFutureKey;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("partition_size", Type.INT64, "The size of the log segments in this partition in bytes."),
                new Field("offset_lag", Type.INT64, "The lag of the log's LEO w.r.t. partition's HW (if it is the current log for the partition) or current replica's LEO (if it is the future log for the partition)"),
                new Field("is_future_key", Type.BOOLEAN, "True if this log is created by AlterReplicaLogDirsRequest and will replace the current log of the replica in the future.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1
        };
        
        public DescribeLogDirsPartition(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public DescribeLogDirsPartition(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public DescribeLogDirsPartition() {
            this.partitionIndex = 0;
            this.partitionSize = 0L;
            this.offsetLag = 0L;
            this.isFutureKey = false;
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of DescribeLogDirsPartition");
            }
            this.partitionIndex = _readable.readInt();
            this.partitionSize = _readable.readLong();
            this.offsetLag = _readable.readLong();
            this.isFutureKey = _readable.readByte() != 0;
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of DescribeLogDirsPartition");
            }
            int _numTaggedFields = 0;
            _writable.writeInt(partitionIndex);
            _writable.writeLong(partitionSize);
            _writable.writeLong(offsetLag);
            _writable.writeByte(isFutureKey ? (byte) 1 : (byte) 0);
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of DescribeLogDirsPartition");
            }
            this._unknownTaggedFields = null;
            this.partitionIndex = struct.getInt("partition_index");
            this.partitionSize = struct.getLong("partition_size");
            this.offsetLag = struct.getLong("offset_lag");
            this.isFutureKey = struct.getBoolean("is_future_key");
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of DescribeLogDirsPartition");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("partition_index", this.partitionIndex);
            struct.set("partition_size", this.partitionSize);
            struct.set("offset_lag", this.offsetLag);
            struct.set("is_future_key", this.isFutureKey);
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of DescribeLogDirsPartition");
            }
            _size += 4;
            _size += 8;
            _size += 8;
            _size += 1;
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
            if (!(obj instanceof DescribeLogDirsPartition)) return false;
            DescribeLogDirsPartition other = (DescribeLogDirsPartition) obj;
            if (partitionIndex != other.partitionIndex) return false;
            if (partitionSize != other.partitionSize) return false;
            if (offsetLag != other.offsetLag) return false;
            if (isFutureKey != other.isFutureKey) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + partitionIndex;
            hashCode = 31 * hashCode + ((int) (partitionSize >> 32) ^ (int) partitionSize);
            hashCode = 31 * hashCode + ((int) (offsetLag >> 32) ^ (int) offsetLag);
            hashCode = 31 * hashCode + (isFutureKey ? 1231 : 1237);
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "DescribeLogDirsPartition("
                + "partitionIndex=" + partitionIndex
                + ", partitionSize=" + partitionSize
                + ", offsetLag=" + offsetLag
                + ", isFutureKey=" + (isFutureKey ? "true" : "false")
                + ")";
        }
        
        public int partitionIndex() {
            return this.partitionIndex;
        }
        
        public long partitionSize() {
            return this.partitionSize;
        }
        
        public long offsetLag() {
            return this.offsetLag;
        }
        
        public boolean isFutureKey() {
            return this.isFutureKey;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public DescribeLogDirsPartition setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
        
        public DescribeLogDirsPartition setPartitionSize(long v) {
            this.partitionSize = v;
            return this;
        }
        
        public DescribeLogDirsPartition setOffsetLag(long v) {
            this.offsetLag = v;
            return this;
        }
        
        public DescribeLogDirsPartition setIsFutureKey(boolean v) {
            this.isFutureKey = v;
            return this;
        }
    }
}
