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


public class ListGroupsResponseData implements ApiMessage {
    private int throttleTimeMs;
    private short errorCode;
    private List<ListedGroup> groups;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
            new Field("groups", new ArrayOf(ListedGroup.SCHEMA_0), "Each group in the response.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
            new Field("groups", new ArrayOf(ListedGroup.SCHEMA_0), "Each group in the response.")
        );
    
    public static final Schema SCHEMA_2 = SCHEMA_1;
    
    public static final Schema SCHEMA_3 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
            new Field("groups", new CompactArrayOf(ListedGroup.SCHEMA_3), "Each group in the response."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2,
        SCHEMA_3
    };
    
    public ListGroupsResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public ListGroupsResponseData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public ListGroupsResponseData() {
        this.throttleTimeMs = 0;
        this.errorCode = (short) 0;
        this.groups = new ArrayList<ListedGroup>();
    }
    
    @Override
    public short apiKey() {
        return 16;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 3;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        if (_version >= 1) {
            this.throttleTimeMs = _readable.readInt();
        } else {
            this.throttleTimeMs = 0;
        }
        this.errorCode = _readable.readShort();
        {
            if (_version >= 3) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field groups was serialized as null");
                } else {
                    ArrayList<ListedGroup> newCollection = new ArrayList<ListedGroup>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new ListedGroup(_readable, _version));
                    }
                    this.groups = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field groups was serialized as null");
                } else {
                    ArrayList<ListedGroup> newCollection = new ArrayList<ListedGroup>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new ListedGroup(_readable, _version));
                    }
                    this.groups = newCollection;
                }
            }
        }
        this._unknownTaggedFields = null;
        if (_version >= 3) {
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
        if (_version >= 1) {
            _writable.writeInt(throttleTimeMs);
        }
        _writable.writeShort(errorCode);
        if (_version >= 3) {
            _writable.writeUnsignedVarint(groups.size() + 1);
            for (ListedGroup groupsElement : groups) {
                groupsElement.write(_writable, _cache, _version);
            }
        } else {
            _writable.writeInt(groups.size());
            for (ListedGroup groupsElement : groups) {
                groupsElement.write(_writable, _cache, _version);
            }
        }
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        if (_version >= 3) {
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
        if (_version >= 3) {
            _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
        }
        if (_version >= 1) {
            this.throttleTimeMs = struct.getInt("throttle_time_ms");
        } else {
            this.throttleTimeMs = 0;
        }
        this.errorCode = struct.getShort("error_code");
        {
            Object[] _nestedObjects = struct.getArray("groups");
            this.groups = new ArrayList<ListedGroup>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.groups.add(new ListedGroup((Struct) nestedObject, _version));
            }
        }
        if (_version >= 3) {
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
        if (_version >= 3) {
            _taggedFields = new TreeMap<>();
        }
        Struct struct = new Struct(SCHEMAS[_version]);
        if (_version >= 1) {
            struct.set("throttle_time_ms", this.throttleTimeMs);
        }
        struct.set("error_code", this.errorCode);
        {
            Struct[] _nestedObjects = new Struct[groups.size()];
            int i = 0;
            for (ListedGroup element : this.groups) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("groups", (Object[]) _nestedObjects);
        }
        if (_version >= 3) {
            struct.set("_tagged_fields", _taggedFields);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        if (_version >= 1) {
            _size += 4;
        }
        _size += 2;
        {
            int _arraySize = 0;
            if (_version >= 3) {
                _arraySize += ByteUtils.sizeOfUnsignedVarint(groups.size() + 1);
            } else {
                _arraySize += 4;
            }
            for (ListedGroup groupsElement : groups) {
                _arraySize += groupsElement.size(_cache, _version);
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
        if (_version >= 3) {
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
        if (!(obj instanceof ListGroupsResponseData)) return false;
        ListGroupsResponseData other = (ListGroupsResponseData) obj;
        if (throttleTimeMs != other.throttleTimeMs) return false;
        if (errorCode != other.errorCode) return false;
        if (this.groups == null) {
            if (other.groups != null) return false;
        } else {
            if (!this.groups.equals(other.groups)) return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + throttleTimeMs;
        hashCode = 31 * hashCode + errorCode;
        hashCode = 31 * hashCode + (groups == null ? 0 : groups.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "ListGroupsResponseData("
            + "throttleTimeMs=" + throttleTimeMs
            + ", errorCode=" + errorCode
            + ", groups=" + MessageUtil.deepToString(groups.iterator())
            + ")";
    }
    
    public int throttleTimeMs() {
        return this.throttleTimeMs;
    }
    
    public short errorCode() {
        return this.errorCode;
    }
    
    public List<ListedGroup> groups() {
        return this.groups;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public ListGroupsResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
    
    public ListGroupsResponseData setErrorCode(short v) {
        this.errorCode = v;
        return this;
    }
    
    public ListGroupsResponseData setGroups(List<ListedGroup> v) {
        this.groups = v;
        return this;
    }
    
    static public class ListedGroup implements Message {
        private String groupId;
        private String protocolType;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("group_id", Type.STRING, "The group ID."),
                new Field("protocol_type", Type.STRING, "The group protocol type.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 =
            new Schema(
                new Field("group_id", Type.COMPACT_STRING, "The group ID."),
                new Field("protocol_type", Type.COMPACT_STRING, "The group protocol type."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3
        };
        
        public ListedGroup(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public ListedGroup(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public ListedGroup() {
            this.groupId = "";
            this.protocolType = "";
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 3;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of ListedGroup");
            }
            {
                int length;
                if (_version >= 3) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field groupId was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field groupId had invalid length " + length);
                } else {
                    this.groupId = _readable.readString(length);
                }
            }
            {
                int length;
                if (_version >= 3) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field protocolType was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field protocolType had invalid length " + length);
                } else {
                    this.protocolType = _readable.readString(length);
                }
            }
            this._unknownTaggedFields = null;
            if (_version >= 3) {
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
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of ListedGroup");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(groupId);
                if (_version >= 3) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            {
                byte[] _stringBytes = _cache.getSerializedValue(protocolType);
                if (_version >= 3) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 3) {
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
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of ListedGroup");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 3) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.groupId = struct.getString("group_id");
            this.protocolType = struct.getString("protocol_type");
            if (_version >= 3) {
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
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of ListedGroup");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 3) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("group_id", this.groupId);
            struct.set("protocol_type", this.protocolType);
            if (_version >= 3) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of ListedGroup");
            }
            {
                byte[] _stringBytes = groupId.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'groupId' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(groupId, _stringBytes);
                if (_version >= 3) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
            }
            {
                byte[] _stringBytes = protocolType.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'protocolType' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(protocolType, _stringBytes);
                if (_version >= 3) {
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
            if (_version >= 3) {
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
            if (!(obj instanceof ListedGroup)) return false;
            ListedGroup other = (ListedGroup) obj;
            if (this.groupId == null) {
                if (other.groupId != null) return false;
            } else {
                if (!this.groupId.equals(other.groupId)) return false;
            }
            if (this.protocolType == null) {
                if (other.protocolType != null) return false;
            } else {
                if (!this.protocolType.equals(other.protocolType)) return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (groupId == null ? 0 : groupId.hashCode());
            hashCode = 31 * hashCode + (protocolType == null ? 0 : protocolType.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "ListedGroup("
                + "groupId=" + ((groupId == null) ? "null" : "'" + groupId.toString() + "'")
                + ", protocolType=" + ((protocolType == null) ? "null" : "'" + protocolType.toString() + "'")
                + ")";
        }
        
        public String groupId() {
            return this.groupId;
        }
        
        public String protocolType() {
            return this.protocolType;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public ListedGroup setGroupId(String v) {
            this.groupId = v;
            return this;
        }
        
        public ListedGroup setProtocolType(String v) {
            this.protocolType = v;
            return this;
        }
    }
}
