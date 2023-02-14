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


public class MetadataRequestData implements ApiMessage {
    private List<MetadataRequestTopic> topics;
    private boolean allowAutoTopicCreation;
    private boolean includeClusterAuthorizedOperations;
    private boolean includeTopicAuthorizedOperations;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("topics", new ArrayOf(MetadataRequestTopic.SCHEMA_0), "The topics to fetch metadata for.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("topics", ArrayOf.nullable(MetadataRequestTopic.SCHEMA_0), "The topics to fetch metadata for.")
        );
    
    public static final Schema SCHEMA_2 = SCHEMA_1;
    
    public static final Schema SCHEMA_3 = SCHEMA_2;
    
    public static final Schema SCHEMA_4 =
        new Schema(
            new Field("topics", ArrayOf.nullable(MetadataRequestTopic.SCHEMA_0), "The topics to fetch metadata for."),
            new Field("allow_auto_topic_creation", Type.BOOLEAN, "If this is true, the broker may auto-create topics that we requested which do not already exist, if it is configured to do so.")
        );
    
    public static final Schema SCHEMA_5 = SCHEMA_4;
    
    public static final Schema SCHEMA_6 = SCHEMA_5;
    
    public static final Schema SCHEMA_7 = SCHEMA_6;
    
    public static final Schema SCHEMA_8 =
        new Schema(
            new Field("topics", ArrayOf.nullable(MetadataRequestTopic.SCHEMA_0), "The topics to fetch metadata for."),
            new Field("allow_auto_topic_creation", Type.BOOLEAN, "If this is true, the broker may auto-create topics that we requested which do not already exist, if it is configured to do so."),
            new Field("include_cluster_authorized_operations", Type.BOOLEAN, "Whether to include cluster authorized operations."),
            new Field("include_topic_authorized_operations", Type.BOOLEAN, "Whether to include topic authorized operations.")
        );
    
    public static final Schema SCHEMA_9 =
        new Schema(
            new Field("topics", CompactArrayOf.nullable(MetadataRequestTopic.SCHEMA_9), "The topics to fetch metadata for."),
            new Field("allow_auto_topic_creation", Type.BOOLEAN, "If this is true, the broker may auto-create topics that we requested which do not already exist, if it is configured to do so."),
            new Field("include_cluster_authorized_operations", Type.BOOLEAN, "Whether to include cluster authorized operations."),
            new Field("include_topic_authorized_operations", Type.BOOLEAN, "Whether to include topic authorized operations."),
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
    
    public MetadataRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public MetadataRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public MetadataRequestData() {
        this.topics = new ArrayList<MetadataRequestTopic>();
        this.allowAutoTopicCreation = true;
        this.includeClusterAuthorizedOperations = false;
        this.includeTopicAuthorizedOperations = false;
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
        {
            if (_version >= 9) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    this.topics = null;
                } else {
                    ArrayList<MetadataRequestTopic> newCollection = new ArrayList<MetadataRequestTopic>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new MetadataRequestTopic(_readable, _version));
                    }
                    this.topics = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    if (_version >= 1) {
                        this.topics = null;
                    } else {
                        throw new RuntimeException("non-nullable field topics was serialized as null");
                    }
                } else {
                    ArrayList<MetadataRequestTopic> newCollection = new ArrayList<MetadataRequestTopic>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new MetadataRequestTopic(_readable, _version));
                    }
                    this.topics = newCollection;
                }
            }
        }
        if (_version >= 4) {
            this.allowAutoTopicCreation = _readable.readByte() != 0;
        } else {
            this.allowAutoTopicCreation = true;
        }
        if (_version >= 8) {
            this.includeClusterAuthorizedOperations = _readable.readByte() != 0;
        } else {
            this.includeClusterAuthorizedOperations = false;
        }
        if (_version >= 8) {
            this.includeTopicAuthorizedOperations = _readable.readByte() != 0;
        } else {
            this.includeTopicAuthorizedOperations = false;
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
        if (_version >= 9) {
            if (topics == null) {
                _writable.writeUnsignedVarint(0);
            } else {
                _writable.writeUnsignedVarint(topics.size() + 1);
                for (MetadataRequestTopic topicsElement : topics) {
                    topicsElement.write(_writable, _cache, _version);
                }
            }
        } else {
            if (topics == null) {
                if (_version >= 1) {
                    _writable.writeInt(-1);
                } else {
                    throw new NullPointerException();
                }
            } else {
                _writable.writeInt(topics.size());
                for (MetadataRequestTopic topicsElement : topics) {
                    topicsElement.write(_writable, _cache, _version);
                }
            }
        }
        if (_version >= 4) {
            _writable.writeByte(allowAutoTopicCreation ? (byte) 1 : (byte) 0);
        } else {
            if (!allowAutoTopicCreation) {
                throw new UnsupportedVersionException("Attempted to write a non-default allowAutoTopicCreation at version " + _version);
            }
        }
        if (_version >= 8) {
            _writable.writeByte(includeClusterAuthorizedOperations ? (byte) 1 : (byte) 0);
        } else {
            if (includeClusterAuthorizedOperations) {
                throw new UnsupportedVersionException("Attempted to write a non-default includeClusterAuthorizedOperations at version " + _version);
            }
        }
        if (_version >= 8) {
            _writable.writeByte(includeTopicAuthorizedOperations ? (byte) 1 : (byte) 0);
        } else {
            if (includeTopicAuthorizedOperations) {
                throw new UnsupportedVersionException("Attempted to write a non-default includeTopicAuthorizedOperations at version " + _version);
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
        {
            Object[] _nestedObjects = struct.getArray("topics");
            if (_nestedObjects == null) {
                this.topics = null;
            } else {
                this.topics = new ArrayList<MetadataRequestTopic>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.topics.add(new MetadataRequestTopic((Struct) nestedObject, _version));
                }
            }
        }
        if (_version >= 4) {
            this.allowAutoTopicCreation = struct.getBoolean("allow_auto_topic_creation");
        } else {
            this.allowAutoTopicCreation = true;
        }
        if (_version >= 8) {
            this.includeClusterAuthorizedOperations = struct.getBoolean("include_cluster_authorized_operations");
        } else {
            this.includeClusterAuthorizedOperations = false;
        }
        if (_version >= 8) {
            this.includeTopicAuthorizedOperations = struct.getBoolean("include_topic_authorized_operations");
        } else {
            this.includeTopicAuthorizedOperations = false;
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
        {
            if (topics == null) {
                struct.set("topics", null);
            } else {
                Struct[] _nestedObjects = new Struct[topics.size()];
                int i = 0;
                for (MetadataRequestTopic element : this.topics) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("topics", (Object[]) _nestedObjects);
            }
        }
        if (_version >= 4) {
            struct.set("allow_auto_topic_creation", this.allowAutoTopicCreation);
        } else {
            if (!allowAutoTopicCreation) {
                throw new UnsupportedVersionException("Attempted to write a non-default allowAutoTopicCreation at version " + _version);
            }
        }
        if (_version >= 8) {
            struct.set("include_cluster_authorized_operations", this.includeClusterAuthorizedOperations);
        } else {
            if (includeClusterAuthorizedOperations) {
                throw new UnsupportedVersionException("Attempted to write a non-default includeClusterAuthorizedOperations at version " + _version);
            }
        }
        if (_version >= 8) {
            struct.set("include_topic_authorized_operations", this.includeTopicAuthorizedOperations);
        } else {
            if (includeTopicAuthorizedOperations) {
                throw new UnsupportedVersionException("Attempted to write a non-default includeTopicAuthorizedOperations at version " + _version);
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
        if (topics == null) {
            if (_version >= 9) {
                _size += 1;
            } else {
                _size += 4;
            }
        } else {
            int _arraySize = 0;
            if (_version >= 9) {
                _arraySize += ByteUtils.sizeOfUnsignedVarint(topics.size() + 1);
            } else {
                _arraySize += 4;
            }
            for (MetadataRequestTopic topicsElement : topics) {
                _arraySize += topicsElement.size(_cache, _version);
            }
            _size += _arraySize;
        }
        if (_version >= 4) {
            _size += 1;
        }
        if (_version >= 8) {
            _size += 1;
        }
        if (_version >= 8) {
            _size += 1;
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
        if (!(obj instanceof MetadataRequestData)) return false;
        MetadataRequestData other = (MetadataRequestData) obj;
        if (this.topics == null) {
            if (other.topics != null) return false;
        } else {
            if (!this.topics.equals(other.topics)) return false;
        }
        if (allowAutoTopicCreation != other.allowAutoTopicCreation) return false;
        if (includeClusterAuthorizedOperations != other.includeClusterAuthorizedOperations) return false;
        if (includeTopicAuthorizedOperations != other.includeTopicAuthorizedOperations) return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (topics == null ? 0 : topics.hashCode());
        hashCode = 31 * hashCode + (allowAutoTopicCreation ? 1231 : 1237);
        hashCode = 31 * hashCode + (includeClusterAuthorizedOperations ? 1231 : 1237);
        hashCode = 31 * hashCode + (includeTopicAuthorizedOperations ? 1231 : 1237);
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "MetadataRequestData("
            + "topics=" + ((topics == null) ? "null" : MessageUtil.deepToString(topics.iterator()))
            + ", allowAutoTopicCreation=" + (allowAutoTopicCreation ? "true" : "false")
            + ", includeClusterAuthorizedOperations=" + (includeClusterAuthorizedOperations ? "true" : "false")
            + ", includeTopicAuthorizedOperations=" + (includeTopicAuthorizedOperations ? "true" : "false")
            + ")";
    }
    
    public List<MetadataRequestTopic> topics() {
        return this.topics;
    }
    
    public boolean allowAutoTopicCreation() {
        return this.allowAutoTopicCreation;
    }
    
    public boolean includeClusterAuthorizedOperations() {
        return this.includeClusterAuthorizedOperations;
    }
    
    public boolean includeTopicAuthorizedOperations() {
        return this.includeTopicAuthorizedOperations;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public MetadataRequestData setTopics(List<MetadataRequestTopic> v) {
        this.topics = v;
        return this;
    }
    
    public MetadataRequestData setAllowAutoTopicCreation(boolean v) {
        this.allowAutoTopicCreation = v;
        return this;
    }
    
    public MetadataRequestData setIncludeClusterAuthorizedOperations(boolean v) {
        this.includeClusterAuthorizedOperations = v;
        return this;
    }
    
    public MetadataRequestData setIncludeTopicAuthorizedOperations(boolean v) {
        this.includeTopicAuthorizedOperations = v;
        return this;
    }
    
    static public class MetadataRequestTopic implements Message {
        private String name;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.STRING, "The topic name.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 = SCHEMA_4;
        
        public static final Schema SCHEMA_6 = SCHEMA_5;
        
        public static final Schema SCHEMA_7 = SCHEMA_6;
        
        public static final Schema SCHEMA_8 = SCHEMA_7;
        
        public static final Schema SCHEMA_9 =
            new Schema(
                new Field("name", Type.COMPACT_STRING, "The topic name."),
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
        
        public MetadataRequestTopic(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public MetadataRequestTopic(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public MetadataRequestTopic() {
            this.name = "";
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of MetadataRequestTopic");
            }
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
                throw new UnsupportedVersionException("Can't write version " + _version + " of MetadataRequestTopic");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                if (_version >= 9) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of MetadataRequestTopic");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 9) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.name = struct.getString("name");
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
                throw new UnsupportedVersionException("Can't write version " + _version + " of MetadataRequestTopic");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 9) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("name", this.name);
            if (_version >= 9) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 9) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of MetadataRequestTopic");
            }
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
            if (!(obj instanceof MetadataRequestTopic)) return false;
            MetadataRequestTopic other = (MetadataRequestTopic) obj;
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
            return "MetadataRequestTopic("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public MetadataRequestTopic setName(String v) {
            this.name = v;
            return this;
        }
    }
}
