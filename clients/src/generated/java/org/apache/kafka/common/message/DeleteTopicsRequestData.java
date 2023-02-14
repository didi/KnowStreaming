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


public class DeleteTopicsRequestData implements ApiMessage {
    private List<String> topicNames;
    private int timeoutMs;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("topic_names", new ArrayOf(Type.STRING), "The names of the topics to delete"),
            new Field("timeout_ms", Type.INT32, "The length of time in milliseconds to wait for the deletions to complete.")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema SCHEMA_2 = SCHEMA_1;
    
    public static final Schema SCHEMA_3 = SCHEMA_2;
    
    public static final Schema SCHEMA_4 =
        new Schema(
            new Field("topic_names", new CompactArrayOf(Type.COMPACT_STRING), "The names of the topics to delete"),
            new Field("timeout_ms", Type.INT32, "The length of time in milliseconds to wait for the deletions to complete."),
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
    
    public DeleteTopicsRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public DeleteTopicsRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public DeleteTopicsRequestData() {
        this.topicNames = new ArrayList<String>();
        this.timeoutMs = 0;
    }
    
    @Override
    public short apiKey() {
        return 20;
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
        {
            if (_version >= 4) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topicNames was serialized as null");
                } else {
                    ArrayList<String> newCollection = new ArrayList<String>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        int length;
                        length = _readable.readUnsignedVarint() - 1;
                        if (length < 0) {
                            throw new RuntimeException("non-nullable field topicNames element was serialized as null");
                        } else if (length > 0x7fff) {
                            throw new RuntimeException("string field topicNames element had invalid length " + length);
                        } else {
                            newCollection.add(_readable.readString(length));
                        }
                    }
                    this.topicNames = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topicNames was serialized as null");
                } else {
                    ArrayList<String> newCollection = new ArrayList<String>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        int length;
                        length = _readable.readShort();
                        if (length < 0) {
                            throw new RuntimeException("non-nullable field topicNames element was serialized as null");
                        } else if (length > 0x7fff) {
                            throw new RuntimeException("string field topicNames element had invalid length " + length);
                        } else {
                            newCollection.add(_readable.readString(length));
                        }
                    }
                    this.topicNames = newCollection;
                }
            }
        }
        this.timeoutMs = _readable.readInt();
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
        if (_version >= 4) {
            _writable.writeUnsignedVarint(topicNames.size() + 1);
            for (String topicNamesElement : topicNames) {
                {
                    byte[] _stringBytes = _cache.getSerializedValue(topicNamesElement);
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                    _writable.writeByteArray(_stringBytes);
                }
            }
        } else {
            _writable.writeInt(topicNames.size());
            for (String topicNamesElement : topicNames) {
                {
                    byte[] _stringBytes = _cache.getSerializedValue(topicNamesElement);
                    _writable.writeShort((short) _stringBytes.length);
                    _writable.writeByteArray(_stringBytes);
                }
            }
        }
        _writable.writeInt(timeoutMs);
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
        {
            Object[] _nestedObjects = struct.getArray("topic_names");
            this.topicNames = new ArrayList<String>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.topicNames.add((String) nestedObject);
            }
        }
        this.timeoutMs = struct.getInt("timeout_ms");
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
        {
            String[] _nestedObjects = new String[topicNames.size()];
            int i = 0;
            for (String element : this.topicNames) {
                _nestedObjects[i++] = element;
            }
            struct.set("topic_names", (Object[]) _nestedObjects);
        }
        struct.set("timeout_ms", this.timeoutMs);
        if (_version >= 4) {
            struct.set("_tagged_fields", _taggedFields);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        {
            int _arraySize = 0;
            if (_version >= 4) {
                _arraySize += ByteUtils.sizeOfUnsignedVarint(topicNames.size() + 1);
            } else {
                _arraySize += 4;
            }
            for (String topicNamesElement : topicNames) {
                byte[] _stringBytes = topicNamesElement.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'topicNamesElement' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(topicNamesElement, _stringBytes);
                if (_version >= 4) {
                    _arraySize += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _arraySize += _stringBytes.length + 2;
                }
            }
            _size += _arraySize;
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
        if (!(obj instanceof DeleteTopicsRequestData)) return false;
        DeleteTopicsRequestData other = (DeleteTopicsRequestData) obj;
        if (this.topicNames == null) {
            if (other.topicNames != null) return false;
        } else {
            if (!this.topicNames.equals(other.topicNames)) return false;
        }
        if (timeoutMs != other.timeoutMs) return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (topicNames == null ? 0 : topicNames.hashCode());
        hashCode = 31 * hashCode + timeoutMs;
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "DeleteTopicsRequestData("
            + "topicNames=" + MessageUtil.deepToString(topicNames.iterator())
            + ", timeoutMs=" + timeoutMs
            + ")";
    }
    
    public List<String> topicNames() {
        return this.topicNames;
    }
    
    public int timeoutMs() {
        return this.timeoutMs;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public DeleteTopicsRequestData setTopicNames(List<String> v) {
        this.topicNames = v;
        return this;
    }
    
    public DeleteTopicsRequestData setTimeoutMs(int v) {
        this.timeoutMs = v;
        return this;
    }
}
