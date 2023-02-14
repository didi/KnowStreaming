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


public class CreateTopicsResponseData implements ApiMessage {
    private int throttleTimeMs;
    private CreatableTopicResultCollection topics;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("topics", new ArrayOf(CreatableTopicResult.SCHEMA_0), "Results for each topic we tried to create.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("topics", new ArrayOf(CreatableTopicResult.SCHEMA_1), "Results for each topic we tried to create.")
        );
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("topics", new ArrayOf(CreatableTopicResult.SCHEMA_1), "Results for each topic we tried to create.")
        );
    
    public static final Schema SCHEMA_3 = SCHEMA_2;
    
    public static final Schema SCHEMA_4 = SCHEMA_3;
    
    public static final Schema SCHEMA_5 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("topics", new CompactArrayOf(CreatableTopicResult.SCHEMA_5), "Results for each topic we tried to create."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2,
        SCHEMA_3,
        SCHEMA_4,
        SCHEMA_5
    };
    
    public CreateTopicsResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public CreateTopicsResponseData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public CreateTopicsResponseData() {
        this.throttleTimeMs = 0;
        this.topics = new CreatableTopicResultCollection(0);
    }
    
    @Override
    public short apiKey() {
        return 19;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 5;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        if (_version >= 2) {
            this.throttleTimeMs = _readable.readInt();
        } else {
            this.throttleTimeMs = 0;
        }
        {
            if (_version >= 5) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topics was serialized as null");
                } else {
                    CreatableTopicResultCollection newCollection = new CreatableTopicResultCollection(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new CreatableTopicResult(_readable, _version));
                    }
                    this.topics = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topics was serialized as null");
                } else {
                    CreatableTopicResultCollection newCollection = new CreatableTopicResultCollection(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new CreatableTopicResult(_readable, _version));
                    }
                    this.topics = newCollection;
                }
            }
        }
        this._unknownTaggedFields = null;
        if (_version >= 5) {
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
        if (_version >= 2) {
            _writable.writeInt(throttleTimeMs);
        }
        if (_version >= 5) {
            _writable.writeUnsignedVarint(topics.size() + 1);
            for (CreatableTopicResult topicsElement : topics) {
                topicsElement.write(_writable, _cache, _version);
            }
        } else {
            _writable.writeInt(topics.size());
            for (CreatableTopicResult topicsElement : topics) {
                topicsElement.write(_writable, _cache, _version);
            }
        }
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        if (_version >= 5) {
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
        if (_version >= 5) {
            _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
        }
        if (_version >= 2) {
            this.throttleTimeMs = struct.getInt("throttle_time_ms");
        } else {
            this.throttleTimeMs = 0;
        }
        {
            Object[] _nestedObjects = struct.getArray("topics");
            this.topics = new CreatableTopicResultCollection(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.topics.add(new CreatableTopicResult((Struct) nestedObject, _version));
            }
        }
        if (_version >= 5) {
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
        if (_version >= 5) {
            _taggedFields = new TreeMap<>();
        }
        Struct struct = new Struct(SCHEMAS[_version]);
        if (_version >= 2) {
            struct.set("throttle_time_ms", this.throttleTimeMs);
        }
        {
            Struct[] _nestedObjects = new Struct[topics.size()];
            int i = 0;
            for (CreatableTopicResult element : this.topics) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("topics", (Object[]) _nestedObjects);
        }
        if (_version >= 5) {
            struct.set("_tagged_fields", _taggedFields);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        if (_version >= 2) {
            _size += 4;
        }
        {
            int _arraySize = 0;
            if (_version >= 5) {
                _arraySize += ByteUtils.sizeOfUnsignedVarint(topics.size() + 1);
            } else {
                _arraySize += 4;
            }
            for (CreatableTopicResult topicsElement : topics) {
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
        if (_version >= 5) {
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
        if (!(obj instanceof CreateTopicsResponseData)) return false;
        CreateTopicsResponseData other = (CreateTopicsResponseData) obj;
        if (throttleTimeMs != other.throttleTimeMs) return false;
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
        hashCode = 31 * hashCode + (topics == null ? 0 : topics.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "CreateTopicsResponseData("
            + "throttleTimeMs=" + throttleTimeMs
            + ", topics=" + MessageUtil.deepToString(topics.iterator())
            + ")";
    }
    
    public int throttleTimeMs() {
        return this.throttleTimeMs;
    }
    
    public CreatableTopicResultCollection topics() {
        return this.topics;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public CreateTopicsResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
    
    public CreateTopicsResponseData setTopics(CreatableTopicResultCollection v) {
        this.topics = v;
        return this;
    }
    
    static public class CreatableTopicResult implements Message, ImplicitLinkedHashMultiCollection.Element {
        private String name;
        private short errorCode;
        private String errorMessage;
        private short topicConfigErrorCode;
        private int numPartitions;
        private short replicationFactor;
        private List<CreatableTopicConfigs> configs;
        private List<RawTaggedField> _unknownTaggedFields;
        private int next;
        private int prev;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("error_code", Type.INT16, "The error code, or 0 if there was no error.")
            );
        
        public static final Schema SCHEMA_1 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
                new Field("error_message", Type.NULLABLE_STRING, "The error message, or null if there was no error.")
            );
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 =
            new Schema(
                new Field("name", Type.COMPACT_STRING, "The topic name."),
                new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
                new Field("error_message", Type.COMPACT_NULLABLE_STRING, "The error message, or null if there was no error."),
                new Field("num_partitions", Type.INT32, "Number of partitions of the topic."),
                new Field("replication_factor", Type.INT16, "Replicator factor of the topic."),
                new Field("configs", CompactArrayOf.nullable(CreatableTopicConfigs.SCHEMA_5), "Configuration of the topic."),
                TaggedFieldsSection.of(
                    0, new Field("topic_config_error_code", Type.INT16, "Optional topic config error returned if configs are not returned in the response.")
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3,
            SCHEMA_4,
            SCHEMA_5
        };
        
        public CreatableTopicResult(Readable _readable, short _version) {
            read(_readable, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public CreatableTopicResult(Struct struct, short _version) {
            fromStruct(struct, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public CreatableTopicResult() {
            this.name = "";
            this.errorCode = (short) 0;
            this.errorMessage = "";
            this.topicConfigErrorCode = (short) 0;
            this.numPartitions = -1;
            this.replicationFactor = (short) -1;
            this.configs = new ArrayList<CreatableTopicConfigs>();
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 5;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of CreatableTopicResult");
            }
            {
                int length;
                if (_version >= 5) {
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
            this.errorCode = _readable.readShort();
            if (_version >= 1) {
                int length;
                if (_version >= 5) {
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
            } else {
                this.errorMessage = "";
            }
            this.topicConfigErrorCode = (short) 0;
            if (_version >= 5) {
                this.numPartitions = _readable.readInt();
            } else {
                this.numPartitions = -1;
            }
            if (_version >= 5) {
                this.replicationFactor = _readable.readShort();
            } else {
                this.replicationFactor = (short) -1;
            }
            if (_version >= 5) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    this.configs = null;
                } else {
                    ArrayList<CreatableTopicConfigs> newCollection = new ArrayList<CreatableTopicConfigs>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new CreatableTopicConfigs(_readable, _version));
                    }
                    this.configs = newCollection;
                }
            } else {
                this.configs = new ArrayList<CreatableTopicConfigs>();
            }
            this._unknownTaggedFields = null;
            if (_version >= 5) {
                int _numTaggedFields = _readable.readUnsignedVarint();
                for (int _i = 0; _i < _numTaggedFields; _i++) {
                    int _tag = _readable.readUnsignedVarint();
                    int _size = _readable.readUnsignedVarint();
                    switch (_tag) {
                        case 0: {
                            this.topicConfigErrorCode = _readable.readShort();
                            break;
                        }
                        default:
                            this._unknownTaggedFields = _readable.readUnknownTaggedField(this._unknownTaggedFields, _tag, _size);
                            break;
                    }
                }
            }
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of CreatableTopicResult");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                if (_version >= 5) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeShort(errorCode);
            if (_version >= 1) {
                if (errorMessage == null) {
                    if (_version >= 5) {
                        _writable.writeUnsignedVarint(0);
                    } else {
                        _writable.writeShort((short) -1);
                    }
                } else {
                    byte[] _stringBytes = _cache.getSerializedValue(errorMessage);
                    if (_version >= 5) {
                        _writable.writeUnsignedVarint(_stringBytes.length + 1);
                    } else {
                        _writable.writeShort((short) _stringBytes.length);
                    }
                    _writable.writeByteArray(_stringBytes);
                }
            }
            if (_version >= 5) {
                if (topicConfigErrorCode != (short) 0) {
                    _numTaggedFields++;
                }
            }
            if (_version >= 5) {
                _writable.writeInt(numPartitions);
            }
            if (_version >= 5) {
                _writable.writeShort(replicationFactor);
            }
            if (_version >= 5) {
                if (configs == null) {
                    _writable.writeUnsignedVarint(0);
                } else {
                    _writable.writeUnsignedVarint(configs.size() + 1);
                    for (CreatableTopicConfigs configsElement : configs) {
                        configsElement.write(_writable, _cache, _version);
                    }
                }
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 5) {
                _writable.writeUnsignedVarint(_numTaggedFields);
                {
                    if (topicConfigErrorCode != (short) 0) {
                        _writable.writeUnsignedVarint(0);
                        _writable.writeUnsignedVarint(2);
                        _writable.writeShort(topicConfigErrorCode);
                    }
                }
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
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of CreatableTopicResult");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 5) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.name = struct.getString("name");
            this.errorCode = struct.getShort("error_code");
            if (_version >= 1) {
                this.errorMessage = struct.getString("error_message");
            } else {
                this.errorMessage = "";
            }
            if (_version >= 5) {
                if (_taggedFields.containsKey(0)) {
                    this.topicConfigErrorCode = (Short) _taggedFields.remove(0);
                } else {
                    this.topicConfigErrorCode = (short) 0;
                }
            } else {
                this.topicConfigErrorCode = (short) 0;
            }
            if (_version >= 5) {
                this.numPartitions = struct.getInt("num_partitions");
            } else {
                this.numPartitions = -1;
            }
            if (_version >= 5) {
                this.replicationFactor = struct.getShort("replication_factor");
            } else {
                this.replicationFactor = (short) -1;
            }
            if (_version >= 5) {
                Object[] _nestedObjects = struct.getArray("configs");
                if (_nestedObjects == null) {
                    this.configs = null;
                } else {
                    this.configs = new ArrayList<CreatableTopicConfigs>(_nestedObjects.length);
                    for (Object nestedObject : _nestedObjects) {
                        this.configs.add(new CreatableTopicConfigs((Struct) nestedObject, _version));
                    }
                }
            } else {
                this.configs = new ArrayList<CreatableTopicConfigs>();
            }
            if (_version >= 5) {
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
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of CreatableTopicResult");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 5) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("name", this.name);
            struct.set("error_code", this.errorCode);
            if (_version >= 1) {
                struct.set("error_message", this.errorMessage);
            }
            if (_version >= 5) {
                if (topicConfigErrorCode != (short) 0) {
                    _taggedFields.put(0, topicConfigErrorCode);
                }
            }
            if (_version >= 5) {
                struct.set("num_partitions", this.numPartitions);
            }
            if (_version >= 5) {
                struct.set("replication_factor", this.replicationFactor);
            }
            if (_version >= 5) {
                if (configs == null) {
                    struct.set("configs", null);
                } else {
                    Struct[] _nestedObjects = new Struct[configs.size()];
                    int i = 0;
                    for (CreatableTopicConfigs element : this.configs) {
                        _nestedObjects[i++] = element.toStruct(_version);
                    }
                    struct.set("configs", (Object[]) _nestedObjects);
                }
            }
            if (_version >= 5) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of CreatableTopicResult");
            }
            {
                byte[] _stringBytes = name.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'name' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(name, _stringBytes);
                if (_version >= 5) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
            }
            _size += 2;
            if (_version >= 1) {
                if (errorMessage == null) {
                    if (_version >= 5) {
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
                    if (_version >= 5) {
                        _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                    } else {
                        _size += _stringBytes.length + 2;
                    }
                }
            }
            if (_version >= 5) {
                if (topicConfigErrorCode != (short) 0) {
                    _numTaggedFields++;
                    _size += 1;
                    _size += 1;
                    _size += 2;
                }
            }
            if (_version >= 5) {
                _size += 4;
            }
            if (_version >= 5) {
                _size += 2;
            }
            if (_version >= 5) {
                if (configs == null) {
                    _size += 1;
                } else {
                    int _arraySize = 0;
                    _arraySize += ByteUtils.sizeOfUnsignedVarint(configs.size() + 1);
                    for (CreatableTopicConfigs configsElement : configs) {
                        _arraySize += configsElement.size(_cache, _version);
                    }
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
            if (_version >= 5) {
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
            if (!(obj instanceof CreatableTopicResult)) return false;
            CreatableTopicResult other = (CreatableTopicResult) obj;
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
            return "CreatableTopicResult("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", errorCode=" + errorCode
                + ", errorMessage=" + ((errorMessage == null) ? "null" : "'" + errorMessage.toString() + "'")
                + ", topicConfigErrorCode=" + topicConfigErrorCode
                + ", numPartitions=" + numPartitions
                + ", replicationFactor=" + replicationFactor
                + ", configs=" + ((configs == null) ? "null" : MessageUtil.deepToString(configs.iterator()))
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public short errorCode() {
            return this.errorCode;
        }
        
        public String errorMessage() {
            return this.errorMessage;
        }
        
        public short topicConfigErrorCode() {
            return this.topicConfigErrorCode;
        }
        
        public int numPartitions() {
            return this.numPartitions;
        }
        
        public short replicationFactor() {
            return this.replicationFactor;
        }
        
        public List<CreatableTopicConfigs> configs() {
            return this.configs;
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
        
        public CreatableTopicResult setName(String v) {
            this.name = v;
            return this;
        }
        
        public CreatableTopicResult setErrorCode(short v) {
            this.errorCode = v;
            return this;
        }
        
        public CreatableTopicResult setErrorMessage(String v) {
            this.errorMessage = v;
            return this;
        }
        
        public CreatableTopicResult setTopicConfigErrorCode(short v) {
            this.topicConfigErrorCode = v;
            return this;
        }
        
        public CreatableTopicResult setNumPartitions(int v) {
            this.numPartitions = v;
            return this;
        }
        
        public CreatableTopicResult setReplicationFactor(short v) {
            this.replicationFactor = v;
            return this;
        }
        
        public CreatableTopicResult setConfigs(List<CreatableTopicConfigs> v) {
            this.configs = v;
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
    
    static public class CreatableTopicConfigs implements Message {
        private String name;
        private String value;
        private boolean readOnly;
        private byte configSource;
        private boolean isSensitive;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_5 =
            new Schema(
                new Field("name", Type.COMPACT_STRING, "The configuration name."),
                new Field("value", Type.COMPACT_NULLABLE_STRING, "The configuration value."),
                new Field("read_only", Type.BOOLEAN, "True if the configuration is read-only."),
                new Field("config_source", Type.INT8, "The configuration source."),
                new Field("is_sensitive", Type.BOOLEAN, "True if this configuration is sensitive."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            null,
            null,
            null,
            null,
            null,
            SCHEMA_5
        };
        
        public CreatableTopicConfigs(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public CreatableTopicConfigs(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public CreatableTopicConfigs() {
            this.name = "";
            this.value = "";
            this.readOnly = false;
            this.configSource = (byte) -1;
            this.isSensitive = false;
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 5;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of CreatableTopicConfigs");
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
                int length;
                length = _readable.readUnsignedVarint() - 1;
                if (length < 0) {
                    this.value = null;
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field value had invalid length " + length);
                } else {
                    this.value = _readable.readString(length);
                }
            }
            this.readOnly = _readable.readByte() != 0;
            this.configSource = _readable.readByte();
            this.isSensitive = _readable.readByte() != 0;
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
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of CreatableTopicConfigs");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
            if (value == null) {
                _writable.writeUnsignedVarint(0);
            } else {
                byte[] _stringBytes = _cache.getSerializedValue(value);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeByte(readOnly ? (byte) 1 : (byte) 0);
            _writable.writeByte(configSource);
            _writable.writeByte(isSensitive ? (byte) 1 : (byte) 0);
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            _writable.writeUnsignedVarint(_numTaggedFields);
            _rawWriter.writeRawTags(_writable, Integer.MAX_VALUE);
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public void fromStruct(Struct struct, short _version) {
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of CreatableTopicConfigs");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            this.name = struct.getString("name");
            this.value = struct.getString("value");
            this.readOnly = struct.getBoolean("read_only");
            this.configSource = struct.getByte("config_source");
            this.isSensitive = struct.getBoolean("is_sensitive");
            if (!_taggedFields.isEmpty()) {
                this._unknownTaggedFields = new ArrayList<>(_taggedFields.size());
                for (Entry<Integer, Object> entry : _taggedFields.entrySet()) {
                    this._unknownTaggedFields.add((RawTaggedField) entry.getValue());
                }
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of CreatableTopicConfigs");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            _taggedFields = new TreeMap<>();
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("name", this.name);
            struct.set("value", this.value);
            struct.set("read_only", this.readOnly);
            struct.set("config_source", this.configSource);
            struct.set("is_sensitive", this.isSensitive);
            struct.set("_tagged_fields", _taggedFields);
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of CreatableTopicConfigs");
            }
            {
                byte[] _stringBytes = name.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'name' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(name, _stringBytes);
                _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
            }
            if (value == null) {
                _size += 1;
            } else {
                byte[] _stringBytes = value.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'value' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(value, _stringBytes);
                _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
            }
            _size += 1;
            _size += 1;
            _size += 1;
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
            if (!(obj instanceof CreatableTopicConfigs)) return false;
            CreatableTopicConfigs other = (CreatableTopicConfigs) obj;
            if (this.name == null) {
                if (other.name != null) return false;
            } else {
                if (!this.name.equals(other.name)) return false;
            }
            if (this.value == null) {
                if (other.value != null) return false;
            } else {
                if (!this.value.equals(other.value)) return false;
            }
            if (readOnly != other.readOnly) return false;
            if (configSource != other.configSource) return false;
            if (isSensitive != other.isSensitive) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (name == null ? 0 : name.hashCode());
            hashCode = 31 * hashCode + (value == null ? 0 : value.hashCode());
            hashCode = 31 * hashCode + (readOnly ? 1231 : 1237);
            hashCode = 31 * hashCode + configSource;
            hashCode = 31 * hashCode + (isSensitive ? 1231 : 1237);
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "CreatableTopicConfigs("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", value=" + ((value == null) ? "null" : "'" + value.toString() + "'")
                + ", readOnly=" + (readOnly ? "true" : "false")
                + ", configSource=" + configSource
                + ", isSensitive=" + (isSensitive ? "true" : "false")
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public String value() {
            return this.value;
        }
        
        public boolean readOnly() {
            return this.readOnly;
        }
        
        public byte configSource() {
            return this.configSource;
        }
        
        public boolean isSensitive() {
            return this.isSensitive;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public CreatableTopicConfigs setName(String v) {
            this.name = v;
            return this;
        }
        
        public CreatableTopicConfigs setValue(String v) {
            this.value = v;
            return this;
        }
        
        public CreatableTopicConfigs setReadOnly(boolean v) {
            this.readOnly = v;
            return this;
        }
        
        public CreatableTopicConfigs setConfigSource(byte v) {
            this.configSource = v;
            return this;
        }
        
        public CreatableTopicConfigs setIsSensitive(boolean v) {
            this.isSensitive = v;
            return this;
        }
    }
    
    public static class CreatableTopicResultCollection extends ImplicitLinkedHashMultiCollection<CreatableTopicResult> {
        public CreatableTopicResultCollection() {
            super();
        }
        
        public CreatableTopicResultCollection(int expectedNumElements) {
            super(expectedNumElements);
        }
        
        public CreatableTopicResultCollection(Iterator<CreatableTopicResult> iterator) {
            super(iterator);
        }
        
        public CreatableTopicResult find(String name) {
            CreatableTopicResult _key = new CreatableTopicResult();
            _key.setName(name);
            return find(_key);
        }
        
        public List<CreatableTopicResult> findAll(String name) {
            CreatableTopicResult _key = new CreatableTopicResult();
            _key.setName(name);
            return findAll(_key);
        }
        
    }
}
