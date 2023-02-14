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


public class FetchRequestData implements ApiMessage {
    private int replicaId;
    private int maxWait;
    private int minBytes;
    private int maxBytes;
    private byte isolationLevel;
    private int sessionId;
    private int epoch;
    private List<FetchableTopic> topics;
    private List<ForgottenTopic> forgotten;
    private String rackId;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("replica_id", Type.INT32, "The broker ID of the follower, of -1 if this request is from a consumer."),
            new Field("max_wait", Type.INT32, "The maximum time in milliseconds to wait for the response."),
            new Field("min_bytes", Type.INT32, "The minimum bytes to accumulate in the response."),
            new Field("topics", new ArrayOf(FetchableTopic.SCHEMA_0), "The topics to fetch.")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema SCHEMA_2 = SCHEMA_1;
    
    public static final Schema SCHEMA_3 =
        new Schema(
            new Field("replica_id", Type.INT32, "The broker ID of the follower, of -1 if this request is from a consumer."),
            new Field("max_wait", Type.INT32, "The maximum time in milliseconds to wait for the response."),
            new Field("min_bytes", Type.INT32, "The minimum bytes to accumulate in the response."),
            new Field("max_bytes", Type.INT32, "The maximum bytes to fetch.  See KIP-74 for cases where this limit may not be honored."),
            new Field("topics", new ArrayOf(FetchableTopic.SCHEMA_0), "The topics to fetch.")
        );
    
    public static final Schema SCHEMA_4 =
        new Schema(
            new Field("replica_id", Type.INT32, "The broker ID of the follower, of -1 if this request is from a consumer."),
            new Field("max_wait", Type.INT32, "The maximum time in milliseconds to wait for the response."),
            new Field("min_bytes", Type.INT32, "The minimum bytes to accumulate in the response."),
            new Field("max_bytes", Type.INT32, "The maximum bytes to fetch.  See KIP-74 for cases where this limit may not be honored."),
            new Field("isolation_level", Type.INT8, "This setting controls the visibility of transactional records. Using READ_UNCOMMITTED (isolation_level = 0) makes all records visible. With READ_COMMITTED (isolation_level = 1), non-transactional and COMMITTED transactional records are visible. To be more concrete, READ_COMMITTED returns all data from offsets smaller than the current LSO (last stable offset), and enables the inclusion of the list of aborted transactions in the result, which allows consumers to discard ABORTED transactional records"),
            new Field("topics", new ArrayOf(FetchableTopic.SCHEMA_0), "The topics to fetch.")
        );
    
    public static final Schema SCHEMA_5 =
        new Schema(
            new Field("replica_id", Type.INT32, "The broker ID of the follower, of -1 if this request is from a consumer."),
            new Field("max_wait", Type.INT32, "The maximum time in milliseconds to wait for the response."),
            new Field("min_bytes", Type.INT32, "The minimum bytes to accumulate in the response."),
            new Field("max_bytes", Type.INT32, "The maximum bytes to fetch.  See KIP-74 for cases where this limit may not be honored."),
            new Field("isolation_level", Type.INT8, "This setting controls the visibility of transactional records. Using READ_UNCOMMITTED (isolation_level = 0) makes all records visible. With READ_COMMITTED (isolation_level = 1), non-transactional and COMMITTED transactional records are visible. To be more concrete, READ_COMMITTED returns all data from offsets smaller than the current LSO (last stable offset), and enables the inclusion of the list of aborted transactions in the result, which allows consumers to discard ABORTED transactional records"),
            new Field("topics", new ArrayOf(FetchableTopic.SCHEMA_5), "The topics to fetch.")
        );
    
    public static final Schema SCHEMA_6 = SCHEMA_5;
    
    public static final Schema SCHEMA_7 =
        new Schema(
            new Field("replica_id", Type.INT32, "The broker ID of the follower, of -1 if this request is from a consumer."),
            new Field("max_wait", Type.INT32, "The maximum time in milliseconds to wait for the response."),
            new Field("min_bytes", Type.INT32, "The minimum bytes to accumulate in the response."),
            new Field("max_bytes", Type.INT32, "The maximum bytes to fetch.  See KIP-74 for cases where this limit may not be honored."),
            new Field("isolation_level", Type.INT8, "This setting controls the visibility of transactional records. Using READ_UNCOMMITTED (isolation_level = 0) makes all records visible. With READ_COMMITTED (isolation_level = 1), non-transactional and COMMITTED transactional records are visible. To be more concrete, READ_COMMITTED returns all data from offsets smaller than the current LSO (last stable offset), and enables the inclusion of the list of aborted transactions in the result, which allows consumers to discard ABORTED transactional records"),
            new Field("session_id", Type.INT32, "The fetch session ID."),
            new Field("epoch", Type.INT32, "The epoch of the partition leader as known to the follower replica or a consumer."),
            new Field("topics", new ArrayOf(FetchableTopic.SCHEMA_5), "The topics to fetch."),
            new Field("forgotten", new ArrayOf(ForgottenTopic.SCHEMA_7), "In an incremental fetch request, the partitions to remove.")
        );
    
    public static final Schema SCHEMA_8 = SCHEMA_7;
    
    public static final Schema SCHEMA_9 =
        new Schema(
            new Field("replica_id", Type.INT32, "The broker ID of the follower, of -1 if this request is from a consumer."),
            new Field("max_wait", Type.INT32, "The maximum time in milliseconds to wait for the response."),
            new Field("min_bytes", Type.INT32, "The minimum bytes to accumulate in the response."),
            new Field("max_bytes", Type.INT32, "The maximum bytes to fetch.  See KIP-74 for cases where this limit may not be honored."),
            new Field("isolation_level", Type.INT8, "This setting controls the visibility of transactional records. Using READ_UNCOMMITTED (isolation_level = 0) makes all records visible. With READ_COMMITTED (isolation_level = 1), non-transactional and COMMITTED transactional records are visible. To be more concrete, READ_COMMITTED returns all data from offsets smaller than the current LSO (last stable offset), and enables the inclusion of the list of aborted transactions in the result, which allows consumers to discard ABORTED transactional records"),
            new Field("session_id", Type.INT32, "The fetch session ID."),
            new Field("epoch", Type.INT32, "The epoch of the partition leader as known to the follower replica or a consumer."),
            new Field("topics", new ArrayOf(FetchableTopic.SCHEMA_9), "The topics to fetch."),
            new Field("forgotten", new ArrayOf(ForgottenTopic.SCHEMA_7), "In an incremental fetch request, the partitions to remove.")
        );
    
    public static final Schema SCHEMA_10 = SCHEMA_9;
    
    public static final Schema SCHEMA_11 =
        new Schema(
            new Field("replica_id", Type.INT32, "The broker ID of the follower, of -1 if this request is from a consumer."),
            new Field("max_wait", Type.INT32, "The maximum time in milliseconds to wait for the response."),
            new Field("min_bytes", Type.INT32, "The minimum bytes to accumulate in the response."),
            new Field("max_bytes", Type.INT32, "The maximum bytes to fetch.  See KIP-74 for cases where this limit may not be honored."),
            new Field("isolation_level", Type.INT8, "This setting controls the visibility of transactional records. Using READ_UNCOMMITTED (isolation_level = 0) makes all records visible. With READ_COMMITTED (isolation_level = 1), non-transactional and COMMITTED transactional records are visible. To be more concrete, READ_COMMITTED returns all data from offsets smaller than the current LSO (last stable offset), and enables the inclusion of the list of aborted transactions in the result, which allows consumers to discard ABORTED transactional records"),
            new Field("session_id", Type.INT32, "The fetch session ID."),
            new Field("epoch", Type.INT32, "The epoch of the partition leader as known to the follower replica or a consumer."),
            new Field("topics", new ArrayOf(FetchableTopic.SCHEMA_9), "The topics to fetch."),
            new Field("forgotten", new ArrayOf(ForgottenTopic.SCHEMA_7), "In an incremental fetch request, the partitions to remove."),
            new Field("rack_id", Type.STRING, "Rack ID of the consumer making this request")
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
    
    public FetchRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public FetchRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public FetchRequestData() {
        this.replicaId = 0;
        this.maxWait = 0;
        this.minBytes = 0;
        this.maxBytes = 0x7fffffff;
        this.isolationLevel = (byte) 0;
        this.sessionId = 0;
        this.epoch = -1;
        this.topics = new ArrayList<FetchableTopic>();
        this.forgotten = new ArrayList<ForgottenTopic>();
        this.rackId = "";
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
        this.replicaId = _readable.readInt();
        this.maxWait = _readable.readInt();
        this.minBytes = _readable.readInt();
        if (_version >= 3) {
            this.maxBytes = _readable.readInt();
        } else {
            this.maxBytes = 0x7fffffff;
        }
        if (_version >= 4) {
            this.isolationLevel = _readable.readByte();
        } else {
            this.isolationLevel = (byte) 0;
        }
        if (_version >= 7) {
            this.sessionId = _readable.readInt();
        } else {
            this.sessionId = 0;
        }
        if (_version >= 7) {
            this.epoch = _readable.readInt();
        } else {
            this.epoch = -1;
        }
        {
            int arrayLength;
            arrayLength = _readable.readInt();
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field topics was serialized as null");
            } else {
                ArrayList<FetchableTopic> newCollection = new ArrayList<FetchableTopic>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new FetchableTopic(_readable, _version));
                }
                this.topics = newCollection;
            }
        }
        if (_version >= 7) {
            int arrayLength;
            arrayLength = _readable.readInt();
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field forgotten was serialized as null");
            } else {
                ArrayList<ForgottenTopic> newCollection = new ArrayList<ForgottenTopic>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new ForgottenTopic(_readable, _version));
                }
                this.forgotten = newCollection;
            }
        } else {
            this.forgotten = new ArrayList<ForgottenTopic>();
        }
        if (_version >= 11) {
            int length;
            length = _readable.readShort();
            if (length < 0) {
                throw new RuntimeException("non-nullable field rackId was serialized as null");
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field rackId had invalid length " + length);
            } else {
                this.rackId = _readable.readString(length);
            }
        } else {
            this.rackId = "";
        }
        this._unknownTaggedFields = null;
    }
    
    @Override
    public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        _writable.writeInt(replicaId);
        _writable.writeInt(maxWait);
        _writable.writeInt(minBytes);
        if (_version >= 3) {
            _writable.writeInt(maxBytes);
        }
        if (_version >= 4) {
            _writable.writeByte(isolationLevel);
        } else {
            if (isolationLevel != (byte) 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default isolationLevel at version " + _version);
            }
        }
        if (_version >= 7) {
            _writable.writeInt(sessionId);
        } else {
            if (sessionId != 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default sessionId at version " + _version);
            }
        }
        if (_version >= 7) {
            _writable.writeInt(epoch);
        } else {
            if (epoch != -1) {
                throw new UnsupportedVersionException("Attempted to write a non-default epoch at version " + _version);
            }
        }
        _writable.writeInt(topics.size());
        for (FetchableTopic topicsElement : topics) {
            topicsElement.write(_writable, _cache, _version);
        }
        if (_version >= 7) {
            _writable.writeInt(forgotten.size());
            for (ForgottenTopic forgottenElement : forgotten) {
                forgottenElement.write(_writable, _cache, _version);
            }
        } else {
            if (!forgotten.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default forgotten at version " + _version);
            }
        }
        if (_version >= 11) {
            {
                byte[] _stringBytes = _cache.getSerializedValue(rackId);
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
        this._unknownTaggedFields = null;
        this.replicaId = struct.getInt("replica_id");
        this.maxWait = struct.getInt("max_wait");
        this.minBytes = struct.getInt("min_bytes");
        if (_version >= 3) {
            this.maxBytes = struct.getInt("max_bytes");
        } else {
            this.maxBytes = 0x7fffffff;
        }
        if (_version >= 4) {
            this.isolationLevel = struct.getByte("isolation_level");
        } else {
            this.isolationLevel = (byte) 0;
        }
        if (_version >= 7) {
            this.sessionId = struct.getInt("session_id");
        } else {
            this.sessionId = 0;
        }
        if (_version >= 7) {
            this.epoch = struct.getInt("epoch");
        } else {
            this.epoch = -1;
        }
        {
            Object[] _nestedObjects = struct.getArray("topics");
            this.topics = new ArrayList<FetchableTopic>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.topics.add(new FetchableTopic((Struct) nestedObject, _version));
            }
        }
        if (_version >= 7) {
            Object[] _nestedObjects = struct.getArray("forgotten");
            this.forgotten = new ArrayList<ForgottenTopic>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.forgotten.add(new ForgottenTopic((Struct) nestedObject, _version));
            }
        } else {
            this.forgotten = new ArrayList<ForgottenTopic>();
        }
        if (_version >= 11) {
            this.rackId = struct.getString("rack_id");
        } else {
            this.rackId = "";
        }
    }
    
    @Override
    public Struct toStruct(short _version) {
        TreeMap<Integer, Object> _taggedFields = null;
        Struct struct = new Struct(SCHEMAS[_version]);
        struct.set("replica_id", this.replicaId);
        struct.set("max_wait", this.maxWait);
        struct.set("min_bytes", this.minBytes);
        if (_version >= 3) {
            struct.set("max_bytes", this.maxBytes);
        }
        if (_version >= 4) {
            struct.set("isolation_level", this.isolationLevel);
        } else {
            if (isolationLevel != (byte) 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default isolationLevel at version " + _version);
            }
        }
        if (_version >= 7) {
            struct.set("session_id", this.sessionId);
        } else {
            if (sessionId != 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default sessionId at version " + _version);
            }
        }
        if (_version >= 7) {
            struct.set("epoch", this.epoch);
        } else {
            if (epoch != -1) {
                throw new UnsupportedVersionException("Attempted to write a non-default epoch at version " + _version);
            }
        }
        {
            Struct[] _nestedObjects = new Struct[topics.size()];
            int i = 0;
            for (FetchableTopic element : this.topics) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("topics", (Object[]) _nestedObjects);
        }
        if (_version >= 7) {
            Struct[] _nestedObjects = new Struct[forgotten.size()];
            int i = 0;
            for (ForgottenTopic element : this.forgotten) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("forgotten", (Object[]) _nestedObjects);
        } else {
            if (!forgotten.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default forgotten at version " + _version);
            }
        }
        if (_version >= 11) {
            struct.set("rack_id", this.rackId);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        _size += 4;
        _size += 4;
        _size += 4;
        if (_version >= 3) {
            _size += 4;
        }
        if (_version >= 4) {
            _size += 1;
        }
        if (_version >= 7) {
            _size += 4;
        }
        if (_version >= 7) {
            _size += 4;
        }
        {
            int _arraySize = 0;
            _arraySize += 4;
            for (FetchableTopic topicsElement : topics) {
                _arraySize += topicsElement.size(_cache, _version);
            }
            _size += _arraySize;
        }
        if (_version >= 7) {
            {
                int _arraySize = 0;
                _arraySize += 4;
                for (ForgottenTopic forgottenElement : forgotten) {
                    _arraySize += forgottenElement.size(_cache, _version);
                }
                _size += _arraySize;
            }
        }
        if (_version >= 11) {
            {
                byte[] _stringBytes = rackId.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'rackId' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(rackId, _stringBytes);
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
        if (!(obj instanceof FetchRequestData)) return false;
        FetchRequestData other = (FetchRequestData) obj;
        if (replicaId != other.replicaId) return false;
        if (maxWait != other.maxWait) return false;
        if (minBytes != other.minBytes) return false;
        if (maxBytes != other.maxBytes) return false;
        if (isolationLevel != other.isolationLevel) return false;
        if (sessionId != other.sessionId) return false;
        if (epoch != other.epoch) return false;
        if (this.topics == null) {
            if (other.topics != null) return false;
        } else {
            if (!this.topics.equals(other.topics)) return false;
        }
        if (this.forgotten == null) {
            if (other.forgotten != null) return false;
        } else {
            if (!this.forgotten.equals(other.forgotten)) return false;
        }
        if (this.rackId == null) {
            if (other.rackId != null) return false;
        } else {
            if (!this.rackId.equals(other.rackId)) return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + replicaId;
        hashCode = 31 * hashCode + maxWait;
        hashCode = 31 * hashCode + minBytes;
        hashCode = 31 * hashCode + maxBytes;
        hashCode = 31 * hashCode + isolationLevel;
        hashCode = 31 * hashCode + sessionId;
        hashCode = 31 * hashCode + epoch;
        hashCode = 31 * hashCode + (topics == null ? 0 : topics.hashCode());
        hashCode = 31 * hashCode + (forgotten == null ? 0 : forgotten.hashCode());
        hashCode = 31 * hashCode + (rackId == null ? 0 : rackId.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "FetchRequestData("
            + "replicaId=" + replicaId
            + ", maxWait=" + maxWait
            + ", minBytes=" + minBytes
            + ", maxBytes=" + maxBytes
            + ", isolationLevel=" + isolationLevel
            + ", sessionId=" + sessionId
            + ", epoch=" + epoch
            + ", topics=" + MessageUtil.deepToString(topics.iterator())
            + ", forgotten=" + MessageUtil.deepToString(forgotten.iterator())
            + ", rackId=" + ((rackId == null) ? "null" : "'" + rackId.toString() + "'")
            + ")";
    }
    
    public int replicaId() {
        return this.replicaId;
    }
    
    public int maxWait() {
        return this.maxWait;
    }
    
    public int minBytes() {
        return this.minBytes;
    }
    
    public int maxBytes() {
        return this.maxBytes;
    }
    
    public byte isolationLevel() {
        return this.isolationLevel;
    }
    
    public int sessionId() {
        return this.sessionId;
    }
    
    public int epoch() {
        return this.epoch;
    }
    
    public List<FetchableTopic> topics() {
        return this.topics;
    }
    
    public List<ForgottenTopic> forgotten() {
        return this.forgotten;
    }
    
    public String rackId() {
        return this.rackId;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public FetchRequestData setReplicaId(int v) {
        this.replicaId = v;
        return this;
    }
    
    public FetchRequestData setMaxWait(int v) {
        this.maxWait = v;
        return this;
    }
    
    public FetchRequestData setMinBytes(int v) {
        this.minBytes = v;
        return this;
    }
    
    public FetchRequestData setMaxBytes(int v) {
        this.maxBytes = v;
        return this;
    }
    
    public FetchRequestData setIsolationLevel(byte v) {
        this.isolationLevel = v;
        return this;
    }
    
    public FetchRequestData setSessionId(int v) {
        this.sessionId = v;
        return this;
    }
    
    public FetchRequestData setEpoch(int v) {
        this.epoch = v;
        return this;
    }
    
    public FetchRequestData setTopics(List<FetchableTopic> v) {
        this.topics = v;
        return this;
    }
    
    public FetchRequestData setForgotten(List<ForgottenTopic> v) {
        this.forgotten = v;
        return this;
    }
    
    public FetchRequestData setRackId(String v) {
        this.rackId = v;
        return this;
    }
    
    static public class FetchableTopic implements Message {
        private String name;
        private List<FetchPartition> fetchPartitions;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.STRING, "The name of the topic to fetch."),
                new Field("fetch_partitions", new ArrayOf(FetchPartition.SCHEMA_0), "The partitions to fetch.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 =
            new Schema(
                new Field("name", Type.STRING, "The name of the topic to fetch."),
                new Field("fetch_partitions", new ArrayOf(FetchPartition.SCHEMA_5), "The partitions to fetch.")
            );
        
        public static final Schema SCHEMA_6 = SCHEMA_5;
        
        public static final Schema SCHEMA_7 = SCHEMA_6;
        
        public static final Schema SCHEMA_8 = SCHEMA_7;
        
        public static final Schema SCHEMA_9 =
            new Schema(
                new Field("name", Type.STRING, "The name of the topic to fetch."),
                new Field("fetch_partitions", new ArrayOf(FetchPartition.SCHEMA_9), "The partitions to fetch.")
            );
        
        public static final Schema SCHEMA_10 = SCHEMA_9;
        
        public static final Schema SCHEMA_11 = SCHEMA_10;
        
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
        
        public FetchableTopic(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public FetchableTopic(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public FetchableTopic() {
            this.name = "";
            this.fetchPartitions = new ArrayList<FetchPartition>();
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of FetchableTopic");
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
                    throw new RuntimeException("non-nullable field fetchPartitions was serialized as null");
                } else {
                    ArrayList<FetchPartition> newCollection = new ArrayList<FetchPartition>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new FetchPartition(_readable, _version));
                    }
                    this.fetchPartitions = newCollection;
                }
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of FetchableTopic");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                _writable.writeShort((short) _stringBytes.length);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeInt(fetchPartitions.size());
            for (FetchPartition fetchPartitionsElement : fetchPartitions) {
                fetchPartitionsElement.write(_writable, _cache, _version);
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of FetchableTopic");
            }
            this._unknownTaggedFields = null;
            this.name = struct.getString("name");
            {
                Object[] _nestedObjects = struct.getArray("fetch_partitions");
                this.fetchPartitions = new ArrayList<FetchPartition>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.fetchPartitions.add(new FetchPartition((Struct) nestedObject, _version));
                }
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of FetchableTopic");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("name", this.name);
            {
                Struct[] _nestedObjects = new Struct[fetchPartitions.size()];
                int i = 0;
                for (FetchPartition element : this.fetchPartitions) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("fetch_partitions", (Object[]) _nestedObjects);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of FetchableTopic");
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
                for (FetchPartition fetchPartitionsElement : fetchPartitions) {
                    _arraySize += fetchPartitionsElement.size(_cache, _version);
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
            if (!(obj instanceof FetchableTopic)) return false;
            FetchableTopic other = (FetchableTopic) obj;
            if (this.name == null) {
                if (other.name != null) return false;
            } else {
                if (!this.name.equals(other.name)) return false;
            }
            if (this.fetchPartitions == null) {
                if (other.fetchPartitions != null) return false;
            } else {
                if (!this.fetchPartitions.equals(other.fetchPartitions)) return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (name == null ? 0 : name.hashCode());
            hashCode = 31 * hashCode + (fetchPartitions == null ? 0 : fetchPartitions.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "FetchableTopic("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", fetchPartitions=" + MessageUtil.deepToString(fetchPartitions.iterator())
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public List<FetchPartition> fetchPartitions() {
            return this.fetchPartitions;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public FetchableTopic setName(String v) {
            this.name = v;
            return this;
        }
        
        public FetchableTopic setFetchPartitions(List<FetchPartition> v) {
            this.fetchPartitions = v;
            return this;
        }
    }
    
    static public class FetchPartition implements Message {
        private int partitionIndex;
        private int currentLeaderEpoch;
        private long fetchOffset;
        private long logStartOffset;
        private int maxBytes;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("fetch_offset", Type.INT64, "The message offset."),
                new Field("max_bytes", Type.INT32, "The maximum bytes to fetch from this partition.  See KIP-74 for cases where this limit may not be honored.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("fetch_offset", Type.INT64, "The message offset."),
                new Field("log_start_offset", Type.INT64, "The earliest available offset of the follower replica.  The field is only used when the request is sent by the follower."),
                new Field("max_bytes", Type.INT32, "The maximum bytes to fetch from this partition.  See KIP-74 for cases where this limit may not be honored.")
            );
        
        public static final Schema SCHEMA_6 = SCHEMA_5;
        
        public static final Schema SCHEMA_7 = SCHEMA_6;
        
        public static final Schema SCHEMA_8 = SCHEMA_7;
        
        public static final Schema SCHEMA_9 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("current_leader_epoch", Type.INT32, "The current leader epoch of the partition."),
                new Field("fetch_offset", Type.INT64, "The message offset."),
                new Field("log_start_offset", Type.INT64, "The earliest available offset of the follower replica.  The field is only used when the request is sent by the follower."),
                new Field("max_bytes", Type.INT32, "The maximum bytes to fetch from this partition.  See KIP-74 for cases where this limit may not be honored.")
            );
        
        public static final Schema SCHEMA_10 = SCHEMA_9;
        
        public static final Schema SCHEMA_11 = SCHEMA_10;
        
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
        
        public FetchPartition(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public FetchPartition(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public FetchPartition() {
            this.partitionIndex = 0;
            this.currentLeaderEpoch = -1;
            this.fetchOffset = 0L;
            this.logStartOffset = -1L;
            this.maxBytes = 0;
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of FetchPartition");
            }
            this.partitionIndex = _readable.readInt();
            if (_version >= 9) {
                this.currentLeaderEpoch = _readable.readInt();
            } else {
                this.currentLeaderEpoch = -1;
            }
            this.fetchOffset = _readable.readLong();
            if (_version >= 5) {
                this.logStartOffset = _readable.readLong();
            } else {
                this.logStartOffset = -1L;
            }
            this.maxBytes = _readable.readInt();
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of FetchPartition");
            }
            int _numTaggedFields = 0;
            _writable.writeInt(partitionIndex);
            if (_version >= 9) {
                _writable.writeInt(currentLeaderEpoch);
            }
            _writable.writeLong(fetchOffset);
            if (_version >= 5) {
                _writable.writeLong(logStartOffset);
            } else {
                if (logStartOffset != -1L) {
                    throw new UnsupportedVersionException("Attempted to write a non-default logStartOffset at version " + _version);
                }
            }
            _writable.writeInt(maxBytes);
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of FetchPartition");
            }
            this._unknownTaggedFields = null;
            this.partitionIndex = struct.getInt("partition_index");
            if (_version >= 9) {
                this.currentLeaderEpoch = struct.getInt("current_leader_epoch");
            } else {
                this.currentLeaderEpoch = -1;
            }
            this.fetchOffset = struct.getLong("fetch_offset");
            if (_version >= 5) {
                this.logStartOffset = struct.getLong("log_start_offset");
            } else {
                this.logStartOffset = -1L;
            }
            this.maxBytes = struct.getInt("max_bytes");
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of FetchPartition");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("partition_index", this.partitionIndex);
            if (_version >= 9) {
                struct.set("current_leader_epoch", this.currentLeaderEpoch);
            }
            struct.set("fetch_offset", this.fetchOffset);
            if (_version >= 5) {
                struct.set("log_start_offset", this.logStartOffset);
            } else {
                if (logStartOffset != -1L) {
                    throw new UnsupportedVersionException("Attempted to write a non-default logStartOffset at version " + _version);
                }
            }
            struct.set("max_bytes", this.maxBytes);
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of FetchPartition");
            }
            _size += 4;
            if (_version >= 9) {
                _size += 4;
            }
            _size += 8;
            if (_version >= 5) {
                _size += 8;
            }
            _size += 4;
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
            if (!(obj instanceof FetchPartition)) return false;
            FetchPartition other = (FetchPartition) obj;
            if (partitionIndex != other.partitionIndex) return false;
            if (currentLeaderEpoch != other.currentLeaderEpoch) return false;
            if (fetchOffset != other.fetchOffset) return false;
            if (logStartOffset != other.logStartOffset) return false;
            if (maxBytes != other.maxBytes) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + partitionIndex;
            hashCode = 31 * hashCode + currentLeaderEpoch;
            hashCode = 31 * hashCode + ((int) (fetchOffset >> 32) ^ (int) fetchOffset);
            hashCode = 31 * hashCode + ((int) (logStartOffset >> 32) ^ (int) logStartOffset);
            hashCode = 31 * hashCode + maxBytes;
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "FetchPartition("
                + "partitionIndex=" + partitionIndex
                + ", currentLeaderEpoch=" + currentLeaderEpoch
                + ", fetchOffset=" + fetchOffset
                + ", logStartOffset=" + logStartOffset
                + ", maxBytes=" + maxBytes
                + ")";
        }
        
        public int partitionIndex() {
            return this.partitionIndex;
        }
        
        public int currentLeaderEpoch() {
            return this.currentLeaderEpoch;
        }
        
        public long fetchOffset() {
            return this.fetchOffset;
        }
        
        public long logStartOffset() {
            return this.logStartOffset;
        }
        
        public int maxBytes() {
            return this.maxBytes;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public FetchPartition setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
        
        public FetchPartition setCurrentLeaderEpoch(int v) {
            this.currentLeaderEpoch = v;
            return this;
        }
        
        public FetchPartition setFetchOffset(long v) {
            this.fetchOffset = v;
            return this;
        }
        
        public FetchPartition setLogStartOffset(long v) {
            this.logStartOffset = v;
            return this;
        }
        
        public FetchPartition setMaxBytes(int v) {
            this.maxBytes = v;
            return this;
        }
    }
    
    static public class ForgottenTopic implements Message {
        private String name;
        private List<Integer> forgottenPartitionIndexes;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_7 =
            new Schema(
                new Field("name", Type.STRING, "The partition name."),
                new Field("forgotten_partition_indexes", new ArrayOf(Type.INT32), "The partitions indexes to forget.")
            );
        
        public static final Schema SCHEMA_8 = SCHEMA_7;
        
        public static final Schema SCHEMA_9 = SCHEMA_8;
        
        public static final Schema SCHEMA_10 = SCHEMA_9;
        
        public static final Schema SCHEMA_11 = SCHEMA_10;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            SCHEMA_7,
            SCHEMA_8,
            SCHEMA_9,
            SCHEMA_10,
            SCHEMA_11
        };
        
        public ForgottenTopic(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public ForgottenTopic(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public ForgottenTopic() {
            this.name = "";
            this.forgottenPartitionIndexes = new ArrayList<Integer>();
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of ForgottenTopic");
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
                    throw new RuntimeException("non-nullable field forgottenPartitionIndexes was serialized as null");
                } else {
                    ArrayList<Integer> newCollection = new ArrayList<Integer>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(_readable.readInt());
                    }
                    this.forgottenPartitionIndexes = newCollection;
                }
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of ForgottenTopic");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                _writable.writeShort((short) _stringBytes.length);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeInt(forgottenPartitionIndexes.size());
            for (Integer forgottenPartitionIndexesElement : forgottenPartitionIndexes) {
                _writable.writeInt(forgottenPartitionIndexesElement);
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of ForgottenTopic");
            }
            this._unknownTaggedFields = null;
            this.name = struct.getString("name");
            {
                Object[] _nestedObjects = struct.getArray("forgotten_partition_indexes");
                this.forgottenPartitionIndexes = new ArrayList<Integer>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.forgottenPartitionIndexes.add((Integer) nestedObject);
                }
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of ForgottenTopic");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("name", this.name);
            {
                Integer[] _nestedObjects = new Integer[forgottenPartitionIndexes.size()];
                int i = 0;
                for (Integer element : this.forgottenPartitionIndexes) {
                    _nestedObjects[i++] = element;
                }
                struct.set("forgotten_partition_indexes", (Object[]) _nestedObjects);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 11) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of ForgottenTopic");
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
                _arraySize += forgottenPartitionIndexes.size() * 4;
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
            if (!(obj instanceof ForgottenTopic)) return false;
            ForgottenTopic other = (ForgottenTopic) obj;
            if (this.name == null) {
                if (other.name != null) return false;
            } else {
                if (!this.name.equals(other.name)) return false;
            }
            if (this.forgottenPartitionIndexes == null) {
                if (other.forgottenPartitionIndexes != null) return false;
            } else {
                if (!this.forgottenPartitionIndexes.equals(other.forgottenPartitionIndexes)) return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (name == null ? 0 : name.hashCode());
            hashCode = 31 * hashCode + (forgottenPartitionIndexes == null ? 0 : forgottenPartitionIndexes.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "ForgottenTopic("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", forgottenPartitionIndexes=" + MessageUtil.deepToString(forgottenPartitionIndexes.iterator())
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public List<Integer> forgottenPartitionIndexes() {
            return this.forgottenPartitionIndexes;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public ForgottenTopic setName(String v) {
            this.name = v;
            return this;
        }
        
        public ForgottenTopic setForgottenPartitionIndexes(List<Integer> v) {
            this.forgottenPartitionIndexes = v;
            return this;
        }
    }
}
