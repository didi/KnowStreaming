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


public class DeleteGroupsRequestData implements ApiMessage {
    private List<String> groupsNames;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("groups_names", new ArrayOf(Type.STRING), "The group names to delete.")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("groups_names", new CompactArrayOf(Type.COMPACT_STRING), "The group names to delete."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2
    };
    
    public DeleteGroupsRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public DeleteGroupsRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public DeleteGroupsRequestData() {
        this.groupsNames = new ArrayList<String>();
    }
    
    @Override
    public short apiKey() {
        return 42;
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
        {
            if (_version >= 2) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field groupsNames was serialized as null");
                } else {
                    ArrayList<String> newCollection = new ArrayList<String>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        int length;
                        length = _readable.readUnsignedVarint() - 1;
                        if (length < 0) {
                            throw new RuntimeException("non-nullable field groupsNames element was serialized as null");
                        } else if (length > 0x7fff) {
                            throw new RuntimeException("string field groupsNames element had invalid length " + length);
                        } else {
                            newCollection.add(_readable.readString(length));
                        }
                    }
                    this.groupsNames = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field groupsNames was serialized as null");
                } else {
                    ArrayList<String> newCollection = new ArrayList<String>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        int length;
                        length = _readable.readShort();
                        if (length < 0) {
                            throw new RuntimeException("non-nullable field groupsNames element was serialized as null");
                        } else if (length > 0x7fff) {
                            throw new RuntimeException("string field groupsNames element had invalid length " + length);
                        } else {
                            newCollection.add(_readable.readString(length));
                        }
                    }
                    this.groupsNames = newCollection;
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
        if (_version >= 2) {
            _writable.writeUnsignedVarint(groupsNames.size() + 1);
            for (String groupsNamesElement : groupsNames) {
                {
                    byte[] _stringBytes = _cache.getSerializedValue(groupsNamesElement);
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                    _writable.writeByteArray(_stringBytes);
                }
            }
        } else {
            _writable.writeInt(groupsNames.size());
            for (String groupsNamesElement : groupsNames) {
                {
                    byte[] _stringBytes = _cache.getSerializedValue(groupsNamesElement);
                    _writable.writeShort((short) _stringBytes.length);
                    _writable.writeByteArray(_stringBytes);
                }
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
        {
            Object[] _nestedObjects = struct.getArray("groups_names");
            this.groupsNames = new ArrayList<String>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.groupsNames.add((String) nestedObject);
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
        {
            String[] _nestedObjects = new String[groupsNames.size()];
            int i = 0;
            for (String element : this.groupsNames) {
                _nestedObjects[i++] = element;
            }
            struct.set("groups_names", (Object[]) _nestedObjects);
        }
        if (_version >= 2) {
            struct.set("_tagged_fields", _taggedFields);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        {
            int _arraySize = 0;
            if (_version >= 2) {
                _arraySize += ByteUtils.sizeOfUnsignedVarint(groupsNames.size() + 1);
            } else {
                _arraySize += 4;
            }
            for (String groupsNamesElement : groupsNames) {
                byte[] _stringBytes = groupsNamesElement.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'groupsNamesElement' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(groupsNamesElement, _stringBytes);
                if (_version >= 2) {
                    _arraySize += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _arraySize += _stringBytes.length + 2;
                }
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
        if (!(obj instanceof DeleteGroupsRequestData)) return false;
        DeleteGroupsRequestData other = (DeleteGroupsRequestData) obj;
        if (this.groupsNames == null) {
            if (other.groupsNames != null) return false;
        } else {
            if (!this.groupsNames.equals(other.groupsNames)) return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (groupsNames == null ? 0 : groupsNames.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "DeleteGroupsRequestData("
            + "groupsNames=" + MessageUtil.deepToString(groupsNames.iterator())
            + ")";
    }
    
    public List<String> groupsNames() {
        return this.groupsNames;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public DeleteGroupsRequestData setGroupsNames(List<String> v) {
        this.groupsNames = v;
        return this;
    }
}
