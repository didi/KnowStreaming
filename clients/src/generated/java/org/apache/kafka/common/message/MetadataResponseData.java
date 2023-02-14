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
import java.util.Iterator;
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
import org.apache.kafka.common.utils.ImplicitLinkedHashCollection;
import org.apache.kafka.common.utils.ImplicitLinkedHashMultiCollection;

import static java.util.Map.Entry;
import static org.apache.kafka.common.protocol.types.Field.TaggedFieldsSection;


public class MetadataResponseData implements ApiMessage {
    private int throttleTimeMs;
    private MetadataResponseBrokerCollection brokers;
    private String clusterId;
    private int controllerId;
    private MetadataResponseTopicCollection topics;
    private int clusterAuthorizedOperations;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("brokers", new ArrayOf(MetadataResponseBroker.SCHEMA_0), "Each broker in the response."),
            new Field("topics", new ArrayOf(MetadataResponseTopic.SCHEMA_0), "Each topic in the response.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("brokers", new ArrayOf(MetadataResponseBroker.SCHEMA_1), "Each broker in the response."),
            new Field("controller_id", Type.INT32, "The ID of the controller broker."),
            new Field("topics", new ArrayOf(MetadataResponseTopic.SCHEMA_1), "Each topic in the response.")
        );
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("brokers", new ArrayOf(MetadataResponseBroker.SCHEMA_1), "Each broker in the response."),
            new Field("cluster_id", Type.NULLABLE_STRING, "The cluster ID that responding broker belongs to."),
            new Field("controller_id", Type.INT32, "The ID of the controller broker."),
            new Field("topics", new ArrayOf(MetadataResponseTopic.SCHEMA_1), "Each topic in the response.")
        );
    
    public static final Schema SCHEMA_3 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("brokers", new ArrayOf(MetadataResponseBroker.SCHEMA_1), "Each broker in the response."),
            new Field("cluster_id", Type.NULLABLE_STRING, "The cluster ID that responding broker belongs to."),
            new Field("controller_id", Type.INT32, "The ID of the controller broker."),
            new Field("topics", new ArrayOf(MetadataResponseTopic.SCHEMA_1), "Each topic in the response.")
        );
    
    public static final Schema SCHEMA_4 = SCHEMA_3;
    
    public static final Schema SCHEMA_5 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("brokers", new ArrayOf(MetadataResponseBroker.SCHEMA_1), "Each broker in the response."),
            new Field("cluster_id", Type.NULLABLE_STRING, "The cluster ID that responding broker belongs to."),
            new Field("controller_id", Type.INT32, "The ID of the controller broker."),
            new Field("topics", new ArrayOf(MetadataResponseTopic.SCHEMA_5), "Each topic in the response.")
        );
    
    public static final Schema SCHEMA_6 = SCHEMA_5;
    
    public static final Schema SCHEMA_7 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("brokers", new ArrayOf(MetadataResponseBroker.SCHEMA_1), "Each broker in the response."),
            new Field("cluster_id", Type.NULLABLE_STRING, "The cluster ID that responding broker belongs to."),
            new Field("controller_id", Type.INT32, "The ID of the controller broker."),
            new Field("topics", new ArrayOf(MetadataResponseTopic.SCHEMA_7), "Each topic in the response.")
        );
    
    public static final Schema SCHEMA_8 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("brokers", new ArrayOf(MetadataResponseBroker.SCHEMA_1), "Each broker in the response."),
            new Field("cluster_id", Type.NULLABLE_STRING, "The cluster ID that responding broker belongs to."),
            new Field("controller_id", Type.INT32, "The ID of the controller broker."),
            new Field("topics", new ArrayOf(MetadataResponseTopic.SCHEMA_8), "Each topic in the response."),
            new Field("cluster_authorized_operations", Type.INT32, "32-bit bitfield to represent authorized operations for this cluster.")
        );
    
    public static final Schema SCHEMA_9 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("brokers", new CompactArrayOf(MetadataResponseBroker.SCHEMA_9), "Each broker in the response."),
            new Field("cluster_id", Type.COMPACT_NULLABLE_STRING, "The cluster ID that responding broker belongs to."),
            new Field("controller_id", Type.INT32, "The ID of the controller broker."),
            new Field("topics", new CompactArrayOf(MetadataResponseTopic.SCHEMA_9), "Each topic in the response."),
            new Field("cluster_authorized_operations", Type.INT32, "32-bit bitfield to represent authorized operations for this cluster."),
            TaggedFieldsSection.of(
            )
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
        SCHEMA_9
    };
    
    public MetadataResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public MetadataResponseData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public MetadataResponseData() {
        this.throttleTimeMs = 0;
        this.brokers = new MetadataResponseBrokerCollection(0);
        this.clusterId = null;
        this.controllerId = -1;
        this.topics = new MetadataResponseTopicCollection(0);
        this.clusterAuthorizedOperations = -2147483648;
    }
    
    @Override
    public short apiKey() {
        return 3;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 9;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        if (_version >= 3) {
            this.throttleTimeMs = _readable.readInt();
        } else {
            this.throttleTimeMs = 0;
        }
        {
            if (_version >= 9) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field brokers was serialized as null");
                } else {
                    MetadataResponseBrokerCollection newCollection = new MetadataResponseBrokerCollection(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new MetadataResponseBroker(_readable, _version));
                    }
                    this.brokers = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field brokers was serialized as null");
                } else {
                    MetadataResponseBrokerCollection newCollection = new MetadataResponseBrokerCollection(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new MetadataResponseBroker(_readable, _version));
                    }
                    this.brokers = newCollection;
                }
            }
        }
        if (_version >= 2) {
            int length;
            if (_version >= 9) {
                length = _readable.readUnsignedVarint() - 1;
            } else {
                length = _readable.readShort();
            }
            if (length < 0) {
                this.clusterId = null;
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field clusterId had invalid length " + length);
            } else {
                this.clusterId = _readable.readString(length);
            }
        } else {
            this.clusterId = null;
        }
        if (_version >= 1) {
            this.controllerId = _readable.readInt();
        } else {
            this.controllerId = -1;
        }
        {
            if (_version >= 9) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topics was serialized as null");
                } else {
                    MetadataResponseTopicCollection newCollection = new MetadataResponseTopicCollection(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new MetadataResponseTopic(_readable, _version));
                    }
                    this.topics = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topics was serialized as null");
                } else {
                    MetadataResponseTopicCollection newCollection = new MetadataResponseTopicCollection(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new MetadataResponseTopic(_readable, _version));
                    }
                    this.topics = newCollection;
                }
            }
        }
        if (_version >= 8) {
            this.clusterAuthorizedOperations = _readable.readInt();
        } else {
            this.clusterAuthorizedOperations = -2147483648;
        }
        this._unknownTaggedFields = null;
        if (_version >= 9) {
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
        if (_version >= 3) {
            _writable.writeInt(throttleTimeMs);
        }
        if (_version >= 9) {
            _writable.writeUnsignedVarint(brokers.size() + 1);
            for (MetadataResponseBroker brokersElement : brokers) {
                brokersElement.write(_writable, _cache, _version);
            }
        } else {
            _writable.writeInt(brokers.size());
            for (MetadataResponseBroker brokersElement : brokers) {
                brokersElement.write(_writable, _cache, _version);
            }
        }
        if (_version >= 2) {
            if (clusterId == null) {
                if (_version >= 9) {
                    _writable.writeUnsignedVarint(0);
                } else {
                    _writable.writeShort((short) -1);
                }
            } else {
                byte[] _stringBytes = _cache.getSerializedValue(clusterId);
                if (_version >= 9) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
        }
        if (_version >= 1) {
            _writable.writeInt(controllerId);
        }
        if (_version >= 9) {
            _writable.writeUnsignedVarint(topics.size() + 1);
            for (MetadataResponseTopic topicsElement : topics) {
                topicsElement.write(_writable, _cache, _version);
            }
        } else {
            _writable.writeInt(topics.size());
            for (MetadataResponseTopic topicsElement : topics) {
                topicsElement.write(_writable, _cache, _version);
            }
        }
        if (_version >= 8) {
            _writable.writeInt(clusterAuthorizedOperations);
        } else {
            if (clusterAuthorizedOperations != -2147483648) {
                throw new UnsupportedVersionException("Attempted to write a non-default clusterAuthorizedOperations at version " + _version);
            }
        }
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        if (_version >= 9) {
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
        if (_version >= 9) {
            _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
        }
        if (_version >= 3) {
            this.throttleTimeMs = struct.getInt("throttle_time_ms");
        } else {
            this.throttleTimeMs = 0;
        }
        {
            Object[] _nestedObjects = struct.getArray("brokers");
            this.brokers = new MetadataResponseBrokerCollection(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.brokers.add(new MetadataResponseBroker((Struct) nestedObject, _version));
            }
        }
        if (_version >= 2) {
            this.clusterId = struct.getString("cluster_id");
        } else {
            this.clusterId = null;
        }
        if (_version >= 1) {
            this.controllerId = struct.getInt("controller_id");
        } else {
            this.controllerId = -1;
        }
        {
            Object[] _nestedObjects = struct.getArray("topics");
            this.topics = new MetadataResponseTopicCollection(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.topics.add(new MetadataResponseTopic((Struct) nestedObject, _version));
            }
        }
        if (_version >= 8) {
            this.clusterAuthorizedOperations = struct.getInt("cluster_authorized_operations");
        } else {
            this.clusterAuthorizedOperations = -2147483648;
        }
        if (_version >= 9) {
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
        if (_version >= 9) {
            _taggedFields = new TreeMap<>();
        }
        Struct struct = new Struct(SCHEMAS[_version]);
        if (_version >= 3) {
            struct.set("throttle_time_ms", this.throttleTimeMs);
        }
        {
            Struct[] _nestedObjects = new Struct[brokers.size()];
            int i = 0;
            for (MetadataResponseBroker element : this.brokers) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("brokers", (Object[]) _nestedObjects);
        }
        if (_version >= 2) {
            struct.set("cluster_id", this.clusterId);
        }
        if (_version >= 1) {
            struct.set("controller_id", this.controllerId);
        }
        {
            Struct[] _nestedObjects = new Struct[topics.size()];
            int i = 0;
            for (MetadataResponseTopic element : this.topics) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("topics", (Object[]) _nestedObjects);
        }
        if (_version >= 8) {
            struct.set("cluster_authorized_operations", this.clusterAuthorizedOperations);
        } else {
            if (clusterAuthorizedOperations != -2147483648) {
                throw new UnsupportedVersionException("Attempted to write a non-default clusterAuthorizedOperations at version " + _version);
            }
        }
        if (_version >= 9) {
            struct.set("_tagged_fields", _taggedFields);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        if (_version >= 3) {
            _size += 4;
        }
        {
            int _arraySize = 0;
            if (_version >= 9) {
                _arraySize += ByteUtils.sizeOfUnsignedVarint(brokers.size() + 1);
            } else {
                _arraySize += 4;
            }
            for (MetadataResponseBroker brokersElement : brokers) {
                _arraySize += brokersElement.size(_cache, _version);
            }
            _size += _arraySize;
        }
        if (_version >= 2) {
            if (clusterId == null) {
                if (_version >= 9) {
                    _size += 1;
                } else {
                    _size += 2;
                }
            } else {
                byte[] _stringBytes = clusterId.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'clusterId' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(clusterId, _stringBytes);
                if (_version >= 9) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
            }
        }
        if (_version >= 1) {
            _size += 4;
        }
        {
            int _arraySize = 0;
            if (_version >= 9) {
                _arraySize += ByteUtils.sizeOfUnsignedVarint(topics.size() + 1);
            } else {
                _arraySize += 4;
            }
            for (MetadataResponseTopic topicsElement : topics) {
                _arraySize += topicsElement.size(_cache, _version);
            }
            _size += _arraySize;
        }
        if (_version >= 8) {
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
        if (_version >= 9) {
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
        if (!(obj instanceof MetadataResponseData)) return false;
        MetadataResponseData other = (MetadataResponseData) obj;
        if (throttleTimeMs != other.throttleTimeMs) return false;
        if (this.brokers == null) {
            if (other.brokers != null) return false;
        } else {
            if (!this.brokers.equals(other.brokers)) return false;
        }
        if (this.clusterId == null) {
            if (other.clusterId != null) return false;
        } else {
            if (!this.clusterId.equals(other.clusterId)) return false;
        }
        if (controllerId != other.controllerId) return false;
        if (this.topics == null) {
            if (other.topics != null) return false;
        } else {
            if (!this.topics.equals(other.topics)) return false;
        }
        if (clusterAuthorizedOperations != other.clusterAuthorizedOperations) return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + throttleTimeMs;
        hashCode = 31 * hashCode + (brokers == null ? 0 : brokers.hashCode());
        hashCode = 31 * hashCode + (clusterId == null ? 0 : clusterId.hashCode());
        hashCode = 31 * hashCode + controllerId;
        hashCode = 31 * hashCode + (topics == null ? 0 : topics.hashCode());
        hashCode = 31 * hashCode + clusterAuthorizedOperations;
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "MetadataResponseData("
            + "throttleTimeMs=" + throttleTimeMs
            + ", brokers=" + MessageUtil.deepToString(brokers.iterator())
            + ", clusterId=" + ((clusterId == null) ? "null" : "'" + clusterId.toString() + "'")
            + ", controllerId=" + controllerId
            + ", topics=" + MessageUtil.deepToString(topics.iterator())
            + ", clusterAuthorizedOperations=" + clusterAuthorizedOperations
            + ")";
    }
    
    public int throttleTimeMs() {
        return this.throttleTimeMs;
    }
    
    public MetadataResponseBrokerCollection brokers() {
        return this.brokers;
    }
    
    public String clusterId() {
        return this.clusterId;
    }
    
    public int controllerId() {
        return this.controllerId;
    }
    
    public MetadataResponseTopicCollection topics() {
        return this.topics;
    }
    
    public int clusterAuthorizedOperations() {
        return this.clusterAuthorizedOperations;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public MetadataResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
    
    public MetadataResponseData setBrokers(MetadataResponseBrokerCollection v) {
        this.brokers = v;
        return this;
    }
    
    public MetadataResponseData setClusterId(String v) {
        this.clusterId = v;
        return this;
    }
    
    public MetadataResponseData setControllerId(int v) {
        this.controllerId = v;
        return this;
    }
    
    public MetadataResponseData setTopics(MetadataResponseTopicCollection v) {
        this.topics = v;
        return this;
    }
    
    public MetadataResponseData setClusterAuthorizedOperations(int v) {
        this.clusterAuthorizedOperations = v;
        return this;
    }
    
    static public class MetadataResponseBroker implements Message, ImplicitLinkedHashMultiCollection.Element {
        private int nodeId;
        private String host;
        private int port;
        private String rack;
        private List<RawTaggedField> _unknownTaggedFields;
        private int next;
        private int prev;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("node_id", Type.INT32, "The broker ID."),
                new Field("host", Type.STRING, "The broker hostname."),
                new Field("port", Type.INT32, "The broker port.")
            );
        
        public static final Schema SCHEMA_1 =
            new Schema(
                new Field("node_id", Type.INT32, "The broker ID."),
                new Field("host", Type.STRING, "The broker hostname."),
                new Field("port", Type.INT32, "The broker port."),
                new Field("rack", Type.NULLABLE_STRING, "The rack of the broker, or null if it has not been assigned to a rack.")
            );
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 = SCHEMA_4;
        
        public static final Schema SCHEMA_6 = SCHEMA_5;
        
        public static final Schema SCHEMA_7 = SCHEMA_6;
        
        public static final Schema SCHEMA_8 = SCHEMA_7;
        
        public static final Schema SCHEMA_9 =
            new Schema(
                new Field("node_id", Type.INT32, "The broker ID."),
                new Field("host", Type.COMPACT_STRING, "The broker hostname."),
                new Field("port", Type.INT32, "The broker port."),
                new Field("rack", Type.COMPACT_NULLABLE_STRING, "The rack of the broker, or null if it has not been assigned to a rack."),
                TaggedFieldsSection.of(
                )
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
            SCHEMA_9
        };
        
        public MetadataResponseBroker(Readable _readable, short _version) {
            read(_readable, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public MetadataResponseBroker(Struct struct, short _version) {
            fromStruct(struct, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public MetadataResponseBroker() {
            this.nodeId = 0;
            this.host = "";
            this.port = 0;
            this.rack = null;
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 9;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 9) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of MetadataResponseBroker");
            }
            this.nodeId = _readable.readInt();
            {
                int length;
                if (_version >= 9) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field host was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field host had invalid length " + length);
                } else {
                    this.host = _readable.readString(length);
                }
            }
            this.port = _readable.readInt();
            if (_version >= 1) {
                int length;
                if (_version >= 9) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    this.rack = null;
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field rack had invalid length " + length);
                } else {
                    this.rack = _readable.readString(length);
                }
            } else {
                this.rack = null;
            }
            this._unknownTaggedFields = null;
            if (_version >= 9) {
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
            if (_version > 9) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of MetadataResponseBroker");
            }
            int _numTaggedFields = 0;
            _writable.writeInt(nodeId);
            {
                byte[] _stringBytes = _cache.getSerializedValue(host);
                if (_version >= 9) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeInt(port);
            if (_version >= 1) {
                if (rack == null) {
                    if (_version >= 9) {
                        _writable.writeUnsignedVarint(0);
                    } else {
                        _writable.writeShort((short) -1);
                    }
                } else {
                    byte[] _stringBytes = _cache.getSerializedValue(rack);
                    if (_version >= 9) {
                        _writable.writeUnsignedVarint(_stringBytes.length + 1);
                    } else {
                        _writable.writeShort((short) _stringBytes.length);
                    }
                    _writable.writeByteArray(_stringBytes);
                }
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 9) {
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
            if (_version > 9) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of MetadataResponseBroker");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 9) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.nodeId = struct.getInt("node_id");
            this.host = struct.getString("host");
            this.port = struct.getInt("port");
            if (_version >= 1) {
                this.rack = struct.getString("rack");
            } else {
                this.rack = null;
            }
            if (_version >= 9) {
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
            if (_version > 9) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of MetadataResponseBroker");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 9) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("node_id", this.nodeId);
            struct.set("host", this.host);
            struct.set("port", this.port);
            if (_version >= 1) {
                struct.set("rack", this.rack);
            }
            if (_version >= 9) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 9) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of MetadataResponseBroker");
            }
            _size += 4;
            {
                byte[] _stringBytes = host.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'host' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(host, _stringBytes);
                if (_version >= 9) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
            }
            _size += 4;
            if (_version >= 1) {
                if (rack == null) {
                    if (_version >= 9) {
                        _size += 1;
                    } else {
                        _size += 2;
                    }
                } else {
                    byte[] _stringBytes = rack.getBytes(StandardCharsets.UTF_8);
                    if (_stringBytes.length > 0x7fff) {
                        throw new RuntimeException("'rack' field is too long to be serialized");
                    }
                    _cache.cacheSerializedValue(rack, _stringBytes);
                    if (_version >= 9) {
                        _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                    } else {
                        _size += _stringBytes.length + 2;
                    }
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
            if (_version >= 9) {
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
            if (!(obj instanceof MetadataResponseBroker)) return false;
            MetadataResponseBroker other = (MetadataResponseBroker) obj;
            if (nodeId != other.nodeId) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + nodeId;
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "MetadataResponseBroker("
                + "nodeId=" + nodeId
                + ", host=" + ((host == null) ? "null" : "'" + host.toString() + "'")
                + ", port=" + port
                + ", rack=" + ((rack == null) ? "null" : "'" + rack.toString() + "'")
                + ")";
        }
        
        public int nodeId() {
            return this.nodeId;
        }
        
        public String host() {
            return this.host;
        }
        
        public int port() {
            return this.port;
        }
        
        public String rack() {
            return this.rack;
        }
        
        @Override
        public int next() {
            return this.next;
        }
        
        @Override
        public int prev() {
            return this.prev;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public MetadataResponseBroker setNodeId(int v) {
            this.nodeId = v;
            return this;
        }
        
        public MetadataResponseBroker setHost(String v) {
            this.host = v;
            return this;
        }
        
        public MetadataResponseBroker setPort(int v) {
            this.port = v;
            return this;
        }
        
        public MetadataResponseBroker setRack(String v) {
            this.rack = v;
            return this;
        }
        
        @Override
        public void setNext(int v) {
            this.next = v;
        }
        
        @Override
        public void setPrev(int v) {
            this.prev = v;
        }
    }
    
    public static class MetadataResponseBrokerCollection extends ImplicitLinkedHashMultiCollection<MetadataResponseBroker> {
        public MetadataResponseBrokerCollection() {
            super();
        }
        
        public MetadataResponseBrokerCollection(int expectedNumElements) {
            super(expectedNumElements);
        }
        
        public MetadataResponseBrokerCollection(Iterator<MetadataResponseBroker> iterator) {
            super(iterator);
        }
        
        public MetadataResponseBroker find(int nodeId) {
            MetadataResponseBroker _key = new MetadataResponseBroker();
            _key.setNodeId(nodeId);
            return find(_key);
        }
        
        public List<MetadataResponseBroker> findAll(int nodeId) {
            MetadataResponseBroker _key = new MetadataResponseBroker();
            _key.setNodeId(nodeId);
            return findAll(_key);
        }
        
    }
    
    static public class MetadataResponseTopic implements Message, ImplicitLinkedHashMultiCollection.Element {
        private short errorCode;
        private String name;
        private boolean isInternal;
        private List<MetadataResponsePartition> partitions;
        private int topicAuthorizedOperations;
        private List<RawTaggedField> _unknownTaggedFields;
        private int next;
        private int prev;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("error_code", Type.INT16, "The topic error, or 0 if there was no error."),
                new Field("name", Type.STRING, "The topic name."),
                new Field("partitions", new ArrayOf(MetadataResponsePartition.SCHEMA_0), "Each partition in the topic.")
            );
        
        public static final Schema SCHEMA_1 =
            new Schema(
                new Field("error_code", Type.INT16, "The topic error, or 0 if there was no error."),
                new Field("name", Type.STRING, "The topic name."),
                new Field("is_internal", Type.BOOLEAN, "True if the topic is internal."),
                new Field("partitions", new ArrayOf(MetadataResponsePartition.SCHEMA_0), "Each partition in the topic.")
            );
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 =
            new Schema(
                new Field("error_code", Type.INT16, "The topic error, or 0 if there was no error."),
                new Field("name", Type.STRING, "The topic name."),
                new Field("is_internal", Type.BOOLEAN, "True if the topic is internal."),
                new Field("partitions", new ArrayOf(MetadataResponsePartition.SCHEMA_5), "Each partition in the topic.")
            );
        
        public static final Schema SCHEMA_6 = SCHEMA_5;
        
        public static final Schema SCHEMA_7 =
            new Schema(
                new Field("error_code", Type.INT16, "The topic error, or 0 if there was no error."),
                new Field("name", Type.STRING, "The topic name."),
                new Field("is_internal", Type.BOOLEAN, "True if the topic is internal."),
                new Field("partitions", new ArrayOf(MetadataResponsePartition.SCHEMA_7), "Each partition in the topic.")
            );
        
        public static final Schema SCHEMA_8 =
            new Schema(
                new Field("error_code", Type.INT16, "The topic error, or 0 if there was no error."),
                new Field("name", Type.STRING, "The topic name."),
                new Field("is_internal", Type.BOOLEAN, "True if the topic is internal."),
                new Field("partitions", new ArrayOf(MetadataResponsePartition.SCHEMA_7), "Each partition in the topic."),
                new Field("topic_authorized_operations", Type.INT32, "32-bit bitfield to represent authorized operations for this topic.")
            );
        
        public static final Schema SCHEMA_9 =
            new Schema(
                new Field("error_code", Type.INT16, "The topic error, or 0 if there was no error."),
                new Field("name", Type.COMPACT_STRING, "The topic name."),
                new Field("is_internal", Type.BOOLEAN, "True if the topic is internal."),
                new Field("partitions", new CompactArrayOf(MetadataResponsePartition.SCHEMA_9), "Each partition in the topic."),
                new Field("topic_authorized_operations", Type.INT32, "32-bit bitfield to represent authorized operations for this topic."),
                TaggedFieldsSection.of(
                )
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
            SCHEMA_9
        };
        
        public MetadataResponseTopic(Readable _readable, short _version) {
            read(_readable, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public MetadataResponseTopic(Struct struct, short _version) {
            fromStruct(struct, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public MetadataResponseTopic() {
            this.errorCode = (short) 0;
            this.name = "";
            this.isInternal = false;
            this.partitions = new ArrayList<MetadataResponsePartition>();
            this.topicAuthorizedOperations = -2147483648;
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 9;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 9) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of MetadataResponseTopic");
            }
            this.errorCode = _readable.readShort();
            {
                int length;
                if (_version >= 9) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field name was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field name had invalid length " + length);
                } else {
                    this.name = _readable.readString(length);
                }
            }
            if (_version >= 1) {
                this.isInternal = _readable.readByte() != 0;
            } else {
                this.isInternal = false;
            }
            {
                if (_version >= 9) {
                    int arrayLength;
                    arrayLength = _readable.readUnsignedVarint() - 1;
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field partitions was serialized as null");
                    } else {
                        ArrayList<MetadataResponsePartition> newCollection = new ArrayList<MetadataResponsePartition>(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new MetadataResponsePartition(_readable, _version));
                        }
                        this.partitions = newCollection;
                    }
                } else {
                    int arrayLength;
                    arrayLength = _readable.readInt();
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field partitions was serialized as null");
                    } else {
                        ArrayList<MetadataResponsePartition> newCollection = new ArrayList<MetadataResponsePartition>(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new MetadataResponsePartition(_readable, _version));
                        }
                        this.partitions = newCollection;
                    }
                }
            }
            if (_version >= 8) {
                this.topicAuthorizedOperations = _readable.readInt();
            } else {
                this.topicAuthorizedOperations = -2147483648;
            }
            this._unknownTaggedFields = null;
            if (_version >= 9) {
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
            if (_version > 9) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of MetadataResponseTopic");
            }
            int _numTaggedFields = 0;
            _writable.writeShort(errorCode);
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                if (_version >= 9) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            if (_version >= 1) {
                _writable.writeByte(isInternal ? (byte) 1 : (byte) 0);
            }
            if (_version >= 9) {
                _writable.writeUnsignedVarint(partitions.size() + 1);
                for (MetadataResponsePartition partitionsElement : partitions) {
                    partitionsElement.write(_writable, _cache, _version);
                }
            } else {
                _writable.writeInt(partitions.size());
                for (MetadataResponsePartition partitionsElement : partitions) {
                    partitionsElement.write(_writable, _cache, _version);
                }
            }
            if (_version >= 8) {
                _writable.writeInt(topicAuthorizedOperations);
            } else {
                if (topicAuthorizedOperations != -2147483648) {
                    throw new UnsupportedVersionException("Attempted to write a non-default topicAuthorizedOperations at version " + _version);
                }
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 9) {
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
            if (_version > 9) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of MetadataResponseTopic");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 9) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.errorCode = struct.getShort("error_code");
            this.name = struct.getString("name");
            if (_version >= 1) {
                this.isInternal = struct.getBoolean("is_internal");
            } else {
                this.isInternal = false;
            }
            {
                Object[] _nestedObjects = struct.getArray("partitions");
                this.partitions = new ArrayList<MetadataResponsePartition>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.partitions.add(new MetadataResponsePartition((Struct) nestedObject, _version));
                }
            }
            if (_version >= 8) {
                this.topicAuthorizedOperations = struct.getInt("topic_authorized_operations");
            } else {
                this.topicAuthorizedOperations = -2147483648;
            }
            if (_version >= 9) {
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
            if (_version > 9) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of MetadataResponseTopic");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 9) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("error_code", this.errorCode);
            struct.set("name", this.name);
            if (_version >= 1) {
                struct.set("is_internal", this.isInternal);
            }
            {
                Struct[] _nestedObjects = new Struct[partitions.size()];
                int i = 0;
                for (MetadataResponsePartition element : this.partitions) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("partitions", (Object[]) _nestedObjects);
            }
            if (_version >= 8) {
                struct.set("topic_authorized_operations", this.topicAuthorizedOperations);
            } else {
                if (topicAuthorizedOperations != -2147483648) {
                    throw new UnsupportedVersionException("Attempted to write a non-default topicAuthorizedOperations at version " + _version);
                }
            }
            if (_version >= 9) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 9) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of MetadataResponseTopic");
            }
            _size += 2;
            {
                byte[] _stringBytes = name.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'name' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(name, _stringBytes);
                if (_version >= 9) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
            }
            if (_version >= 1) {
                _size += 1;
            }
            {
                int _arraySize = 0;
                if (_version >= 9) {
                    _arraySize += ByteUtils.sizeOfUnsignedVarint(partitions.size() + 1);
                } else {
                    _arraySize += 4;
                }
                for (MetadataResponsePartition partitionsElement : partitions) {
                    _arraySize += partitionsElement.size(_cache, _version);
                }
                _size += _arraySize;
            }
            if (_version >= 8) {
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
            if (_version >= 9) {
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
            if (!(obj instanceof MetadataResponseTopic)) return false;
            MetadataResponseTopic other = (MetadataResponseTopic) obj;
            if (this.name == null) {
                if (other.name != null) return false;
            } else {
                if (!this.name.equals(other.name)) return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (name == null ? 0 : name.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "MetadataResponseTopic("
                + "errorCode=" + errorCode
                + ", name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", isInternal=" + (isInternal ? "true" : "false")
                + ", partitions=" + MessageUtil.deepToString(partitions.iterator())
                + ", topicAuthorizedOperations=" + topicAuthorizedOperations
                + ")";
        }
        
        public short errorCode() {
            return this.errorCode;
        }
        
        public String name() {
            return this.name;
        }
        
        public boolean isInternal() {
            return this.isInternal;
        }
        
        public List<MetadataResponsePartition> partitions() {
            return this.partitions;
        }
        
        public int topicAuthorizedOperations() {
            return this.topicAuthorizedOperations;
        }
        
        @Override
        public int next() {
            return this.next;
        }
        
        @Override
        public int prev() {
            return this.prev;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public MetadataResponseTopic setErrorCode(short v) {
            this.errorCode = v;
            return this;
        }
        
        public MetadataResponseTopic setName(String v) {
            this.name = v;
            return this;
        }
        
        public MetadataResponseTopic setIsInternal(boolean v) {
            this.isInternal = v;
            return this;
        }
        
        public MetadataResponseTopic setPartitions(List<MetadataResponsePartition> v) {
            this.partitions = v;
            return this;
        }
        
        public MetadataResponseTopic setTopicAuthorizedOperations(int v) {
            this.topicAuthorizedOperations = v;
            return this;
        }
        
        @Override
        public void setNext(int v) {
            this.next = v;
        }
        
        @Override
        public void setPrev(int v) {
            this.prev = v;
        }
    }
    
    static public class MetadataResponsePartition implements Message {
        private short errorCode;
        private int partitionIndex;
        private int leaderId;
        private int leaderEpoch;
        private List<Integer> replicaNodes;
        private List<Integer> isrNodes;
        private List<Integer> offlineReplicas;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("error_code", Type.INT16, "The partition error, or 0 if there was no error."),
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("leader_id", Type.INT32, "The ID of the leader broker."),
                new Field("replica_nodes", new ArrayOf(Type.INT32), "The set of all nodes that host this partition."),
                new Field("isr_nodes", new ArrayOf(Type.INT32), "The set of nodes that are in sync with the leader for this partition.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 =
            new Schema(
                new Field("error_code", Type.INT16, "The partition error, or 0 if there was no error."),
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("leader_id", Type.INT32, "The ID of the leader broker."),
                new Field("replica_nodes", new ArrayOf(Type.INT32), "The set of all nodes that host this partition."),
                new Field("isr_nodes", new ArrayOf(Type.INT32), "The set of nodes that are in sync with the leader for this partition."),
                new Field("offline_replicas", new ArrayOf(Type.INT32), "The set of offline replicas of this partition.")
            );
        
        public static final Schema SCHEMA_6 = SCHEMA_5;
        
        public static final Schema SCHEMA_7 =
            new Schema(
                new Field("error_code", Type.INT16, "The partition error, or 0 if there was no error."),
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("leader_id", Type.INT32, "The ID of the leader broker."),
                new Field("leader_epoch", Type.INT32, "The leader epoch of this partition."),
                new Field("replica_nodes", new ArrayOf(Type.INT32), "The set of all nodes that host this partition."),
                new Field("isr_nodes", new ArrayOf(Type.INT32), "The set of nodes that are in sync with the leader for this partition."),
                new Field("offline_replicas", new ArrayOf(Type.INT32), "The set of offline replicas of this partition.")
            );
        
        public static final Schema SCHEMA_8 = SCHEMA_7;
        
        public static final Schema SCHEMA_9 =
            new Schema(
                new Field("error_code", Type.INT16, "The partition error, or 0 if there was no error."),
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("leader_id", Type.INT32, "The ID of the leader broker."),
                new Field("leader_epoch", Type.INT32, "The leader epoch of this partition."),
                new Field("replica_nodes", new CompactArrayOf(Type.INT32), "The set of all nodes that host this partition."),
                new Field("isr_nodes", new CompactArrayOf(Type.INT32), "The set of nodes that are in sync with the leader for this partition."),
                new Field("offline_replicas", new CompactArrayOf(Type.INT32), "The set of offline replicas of this partition."),
                TaggedFieldsSection.of(
                )
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
            SCHEMA_9
        };
        
        public MetadataResponsePartition(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public MetadataResponsePartition(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public MetadataResponsePartition() {
            this.errorCode = (short) 0;
            this.partitionIndex = 0;
            this.leaderId = 0;
            this.leaderEpoch = -1;
            this.replicaNodes = new ArrayList<Integer>();
            this.isrNodes = new ArrayList<Integer>();
            this.offlineReplicas = new ArrayList<Integer>();
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 9;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 9) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of MetadataResponsePartition");
            }
            this.errorCode = _readable.readShort();
            this.partitionIndex = _readable.readInt();
            this.leaderId = _readable.readInt();
            if (_version >= 7) {
                this.leaderEpoch = _readable.readInt();
            } else {
                this.leaderEpoch = -1;
            }
            {
                int arrayLength;
                if (_version >= 9) {
                    arrayLength = _readable.readUnsignedVarint() - 1;
                } else {
                    arrayLength = _readable.readInt();
                }
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field replicaNodes was serialized as null");
                } else {
                    ArrayList<Integer> newCollection = new ArrayList<Integer>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(_readable.readInt());
                    }
                    this.replicaNodes = newCollection;
                }
            }
            {
                int arrayLength;
                if (_version >= 9) {
                    arrayLength = _readable.readUnsignedVarint() - 1;
                } else {
                    arrayLength = _readable.readInt();
                }
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field isrNodes was serialized as null");
                } else {
                    ArrayList<Integer> newCollection = new ArrayList<Integer>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(_readable.readInt());
                    }
                    this.isrNodes = newCollection;
                }
            }
            if (_version >= 5) {
                int arrayLength;
                if (_version >= 9) {
                    arrayLength = _readable.readUnsignedVarint() - 1;
                } else {
                    arrayLength = _readable.readInt();
                }
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field offlineReplicas was serialized as null");
                } else {
                    ArrayList<Integer> newCollection = new ArrayList<Integer>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(_readable.readInt());
                    }
                    this.offlineReplicas = newCollection;
                }
            } else {
                this.offlineReplicas = new ArrayList<Integer>();
            }
            this._unknownTaggedFields = null;
            if (_version >= 9) {
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
            if (_version > 9) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of MetadataResponsePartition");
            }
            int _numTaggedFields = 0;
            _writable.writeShort(errorCode);
            _writable.writeInt(partitionIndex);
            _writable.writeInt(leaderId);
            if (_version >= 7) {
                _writable.writeInt(leaderEpoch);
            }
            if (_version >= 9) {
                _writable.writeUnsignedVarint(replicaNodes.size() + 1);
            } else {
                _writable.writeInt(replicaNodes.size());
            }
            for (Integer replicaNodesElement : replicaNodes) {
                _writable.writeInt(replicaNodesElement);
            }
            if (_version >= 9) {
                _writable.writeUnsignedVarint(isrNodes.size() + 1);
            } else {
                _writable.writeInt(isrNodes.size());
            }
            for (Integer isrNodesElement : isrNodes) {
                _writable.writeInt(isrNodesElement);
            }
            if (_version >= 5) {
                if (_version >= 9) {
                    _writable.writeUnsignedVarint(offlineReplicas.size() + 1);
                } else {
                    _writable.writeInt(offlineReplicas.size());
                }
                for (Integer offlineReplicasElement : offlineReplicas) {
                    _writable.writeInt(offlineReplicasElement);
                }
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 9) {
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
            if (_version > 9) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of MetadataResponsePartition");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 9) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.errorCode = struct.getShort("error_code");
            this.partitionIndex = struct.getInt("partition_index");
            this.leaderId = struct.getInt("leader_id");
            if (_version >= 7) {
                this.leaderEpoch = struct.getInt("leader_epoch");
            } else {
                this.leaderEpoch = -1;
            }
            {
                Object[] _nestedObjects = struct.getArray("replica_nodes");
                this.replicaNodes = new ArrayList<Integer>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.replicaNodes.add((Integer) nestedObject);
                }
            }
            {
                Object[] _nestedObjects = struct.getArray("isr_nodes");
                this.isrNodes = new ArrayList<Integer>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.isrNodes.add((Integer) nestedObject);
                }
            }
            if (_version >= 5) {
                Object[] _nestedObjects = struct.getArray("offline_replicas");
                this.offlineReplicas = new ArrayList<Integer>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.offlineReplicas.add((Integer) nestedObject);
                }
            } else {
                this.offlineReplicas = new ArrayList<Integer>();
            }
            if (_version >= 9) {
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
            if (_version > 9) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of MetadataResponsePartition");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 9) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("error_code", this.errorCode);
            struct.set("partition_index", this.partitionIndex);
            struct.set("leader_id", this.leaderId);
            if (_version >= 7) {
                struct.set("leader_epoch", this.leaderEpoch);
            }
            {
                Integer[] _nestedObjects = new Integer[replicaNodes.size()];
                int i = 0;
                for (Integer element : this.replicaNodes) {
                    _nestedObjects[i++] = element;
                }
                struct.set("replica_nodes", (Object[]) _nestedObjects);
            }
            {
                Integer[] _nestedObjects = new Integer[isrNodes.size()];
                int i = 0;
                for (Integer element : this.isrNodes) {
                    _nestedObjects[i++] = element;
                }
                struct.set("isr_nodes", (Object[]) _nestedObjects);
            }
            if (_version >= 5) {
                Integer[] _nestedObjects = new Integer[offlineReplicas.size()];
                int i = 0;
                for (Integer element : this.offlineReplicas) {
                    _nestedObjects[i++] = element;
                }
                struct.set("offline_replicas", (Object[]) _nestedObjects);
            }
            if (_version >= 9) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 9) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of MetadataResponsePartition");
            }
            _size += 2;
            _size += 4;
            _size += 4;
            if (_version >= 7) {
                _size += 4;
            }
            {
                int _arraySize = 0;
                if (_version >= 9) {
                    _arraySize += ByteUtils.sizeOfUnsignedVarint(replicaNodes.size() + 1);
                } else {
                    _arraySize += 4;
                }
                _arraySize += replicaNodes.size() * 4;
                _size += _arraySize;
            }
            {
                int _arraySize = 0;
                if (_version >= 9) {
                    _arraySize += ByteUtils.sizeOfUnsignedVarint(isrNodes.size() + 1);
                } else {
                    _arraySize += 4;
                }
                _arraySize += isrNodes.size() * 4;
                _size += _arraySize;
            }
            if (_version >= 5) {
                {
                    int _arraySize = 0;
                    if (_version >= 9) {
                        _arraySize += ByteUtils.sizeOfUnsignedVarint(offlineReplicas.size() + 1);
                    } else {
                        _arraySize += 4;
                    }
                    _arraySize += offlineReplicas.size() * 4;
                    _size += _arraySize;
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
            if (_version >= 9) {
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
            if (!(obj instanceof MetadataResponsePartition)) return false;
            MetadataResponsePartition other = (MetadataResponsePartition) obj;
            if (errorCode != other.errorCode) return false;
            if (partitionIndex != other.partitionIndex) return false;
            if (leaderId != other.leaderId) return false;
            if (leaderEpoch != other.leaderEpoch) return false;
            if (this.replicaNodes == null) {
                if (other.replicaNodes != null) return false;
            } else {
                if (!this.replicaNodes.equals(other.replicaNodes)) return false;
            }
            if (this.isrNodes == null) {
                if (other.isrNodes != null) return false;
            } else {
                if (!this.isrNodes.equals(other.isrNodes)) return false;
            }
            if (this.offlineReplicas == null) {
                if (other.offlineReplicas != null) return false;
            } else {
                if (!this.offlineReplicas.equals(other.offlineReplicas)) return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + errorCode;
            hashCode = 31 * hashCode + partitionIndex;
            hashCode = 31 * hashCode + leaderId;
            hashCode = 31 * hashCode + leaderEpoch;
            hashCode = 31 * hashCode + (replicaNodes == null ? 0 : replicaNodes.hashCode());
            hashCode = 31 * hashCode + (isrNodes == null ? 0 : isrNodes.hashCode());
            hashCode = 31 * hashCode + (offlineReplicas == null ? 0 : offlineReplicas.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "MetadataResponsePartition("
                + "errorCode=" + errorCode
                + ", partitionIndex=" + partitionIndex
                + ", leaderId=" + leaderId
                + ", leaderEpoch=" + leaderEpoch
                + ", replicaNodes=" + MessageUtil.deepToString(replicaNodes.iterator())
                + ", isrNodes=" + MessageUtil.deepToString(isrNodes.iterator())
                + ", offlineReplicas=" + MessageUtil.deepToString(offlineReplicas.iterator())
                + ")";
        }
        
        public short errorCode() {
            return this.errorCode;
        }
        
        public int partitionIndex() {
            return this.partitionIndex;
        }
        
        public int leaderId() {
            return this.leaderId;
        }
        
        public int leaderEpoch() {
            return this.leaderEpoch;
        }
        
        public List<Integer> replicaNodes() {
            return this.replicaNodes;
        }
        
        public List<Integer> isrNodes() {
            return this.isrNodes;
        }
        
        public List<Integer> offlineReplicas() {
            return this.offlineReplicas;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public MetadataResponsePartition setErrorCode(short v) {
            this.errorCode = v;
            return this;
        }
        
        public MetadataResponsePartition setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
        
        public MetadataResponsePartition setLeaderId(int v) {
            this.leaderId = v;
            return this;
        }
        
        public MetadataResponsePartition setLeaderEpoch(int v) {
            this.leaderEpoch = v;
            return this;
        }
        
        public MetadataResponsePartition setReplicaNodes(List<Integer> v) {
            this.replicaNodes = v;
            return this;
        }
        
        public MetadataResponsePartition setIsrNodes(List<Integer> v) {
            this.isrNodes = v;
            return this;
        }
        
        public MetadataResponsePartition setOfflineReplicas(List<Integer> v) {
            this.offlineReplicas = v;
            return this;
        }
    }
    
    public static class MetadataResponseTopicCollection extends ImplicitLinkedHashMultiCollection<MetadataResponseTopic> {
        public MetadataResponseTopicCollection() {
            super();
        }
        
        public MetadataResponseTopicCollection(int expectedNumElements) {
            super(expectedNumElements);
        }
        
        public MetadataResponseTopicCollection(Iterator<MetadataResponseTopic> iterator) {
            super(iterator);
        }
        
        public MetadataResponseTopic find(String name) {
            MetadataResponseTopic _key = new MetadataResponseTopic();
            _key.setName(name);
            return find(_key);
        }
        
        public List<MetadataResponseTopic> findAll(String name) {
            MetadataResponseTopic _key = new MetadataResponseTopic();
            _key.setName(name);
            return findAll(_key);
        }
        
    }
}
