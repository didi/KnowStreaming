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


public class DescribeDelegationTokenRequestData implements ApiMessage {
    private List<DescribeDelegationTokenOwner> owners;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("owners", ArrayOf.nullable(DescribeDelegationTokenOwner.SCHEMA_0), "Each owner that we want to describe delegation tokens for, or null to describe all tokens.")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("owners", CompactArrayOf.nullable(DescribeDelegationTokenOwner.SCHEMA_2), "Each owner that we want to describe delegation tokens for, or null to describe all tokens."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2
    };
    
    public DescribeDelegationTokenRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public DescribeDelegationTokenRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public DescribeDelegationTokenRequestData() {
        this.owners = new ArrayList<DescribeDelegationTokenOwner>();
    }
    
    @Override
    public short apiKey() {
        return 41;
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
                    this.owners = null;
                } else {
                    ArrayList<DescribeDelegationTokenOwner> newCollection = new ArrayList<DescribeDelegationTokenOwner>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new DescribeDelegationTokenOwner(_readable, _version));
                    }
                    this.owners = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    this.owners = null;
                } else {
                    ArrayList<DescribeDelegationTokenOwner> newCollection = new ArrayList<DescribeDelegationTokenOwner>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new DescribeDelegationTokenOwner(_readable, _version));
                    }
                    this.owners = newCollection;
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
            if (owners == null) {
                _writable.writeUnsignedVarint(0);
            } else {
                _writable.writeUnsignedVarint(owners.size() + 1);
                for (DescribeDelegationTokenOwner ownersElement : owners) {
                    ownersElement.write(_writable, _cache, _version);
                }
            }
        } else {
            if (owners == null) {
                _writable.writeInt(-1);
            } else {
                _writable.writeInt(owners.size());
                for (DescribeDelegationTokenOwner ownersElement : owners) {
                    ownersElement.write(_writable, _cache, _version);
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
            Object[] _nestedObjects = struct.getArray("owners");
            if (_nestedObjects == null) {
                this.owners = null;
            } else {
                this.owners = new ArrayList<DescribeDelegationTokenOwner>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.owners.add(new DescribeDelegationTokenOwner((Struct) nestedObject, _version));
                }
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
            if (owners == null) {
                struct.set("owners", null);
            } else {
                Struct[] _nestedObjects = new Struct[owners.size()];
                int i = 0;
                for (DescribeDelegationTokenOwner element : this.owners) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("owners", (Object[]) _nestedObjects);
            }
        }
        if (_version >= 2) {
            struct.set("_tagged_fields", _taggedFields);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        if (owners == null) {
            if (_version >= 2) {
                _size += 1;
            } else {
                _size += 4;
            }
        } else {
            int _arraySize = 0;
            if (_version >= 2) {
                _arraySize += ByteUtils.sizeOfUnsignedVarint(owners.size() + 1);
            } else {
                _arraySize += 4;
            }
            for (DescribeDelegationTokenOwner ownersElement : owners) {
                _arraySize += ownersElement.size(_cache, _version);
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
        if (!(obj instanceof DescribeDelegationTokenRequestData)) return false;
        DescribeDelegationTokenRequestData other = (DescribeDelegationTokenRequestData) obj;
        if (this.owners == null) {
            if (other.owners != null) return false;
        } else {
            if (!this.owners.equals(other.owners)) return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (owners == null ? 0 : owners.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "DescribeDelegationTokenRequestData("
            + "owners=" + ((owners == null) ? "null" : MessageUtil.deepToString(owners.iterator()))
            + ")";
    }
    
    public List<DescribeDelegationTokenOwner> owners() {
        return this.owners;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public DescribeDelegationTokenRequestData setOwners(List<DescribeDelegationTokenOwner> v) {
        this.owners = v;
        return this;
    }
    
    static public class DescribeDelegationTokenOwner implements Message {
        private String principalType;
        private String principalName;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("principal_type", Type.STRING, "The owner principal type."),
                new Field("principal_name", Type.STRING, "The owner principal name.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 =
            new Schema(
                new Field("principal_type", Type.COMPACT_STRING, "The owner principal type."),
                new Field("principal_name", Type.COMPACT_STRING, "The owner principal name."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2
        };
        
        public DescribeDelegationTokenOwner(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public DescribeDelegationTokenOwner(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public DescribeDelegationTokenOwner() {
            this.principalType = "";
            this.principalName = "";
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of DescribeDelegationTokenOwner");
            }
            {
                int length;
                if (_version >= 2) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field principalType was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field principalType had invalid length " + length);
                } else {
                    this.principalType = _readable.readString(length);
                }
            }
            {
                int length;
                if (_version >= 2) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field principalName was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field principalName had invalid length " + length);
                } else {
                    this.principalName = _readable.readString(length);
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
                throw new UnsupportedVersionException("Can't write version " + _version + " of DescribeDelegationTokenOwner");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(principalType);
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            {
                byte[] _stringBytes = _cache.getSerializedValue(principalName);
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of DescribeDelegationTokenOwner");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 2) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.principalType = struct.getString("principal_type");
            this.principalName = struct.getString("principal_name");
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
                throw new UnsupportedVersionException("Can't write version " + _version + " of DescribeDelegationTokenOwner");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 2) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("principal_type", this.principalType);
            struct.set("principal_name", this.principalName);
            if (_version >= 2) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of DescribeDelegationTokenOwner");
            }
            {
                byte[] _stringBytes = principalType.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'principalType' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(principalType, _stringBytes);
                if (_version >= 2) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
            }
            {
                byte[] _stringBytes = principalName.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'principalName' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(principalName, _stringBytes);
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
            if (!(obj instanceof DescribeDelegationTokenOwner)) return false;
            DescribeDelegationTokenOwner other = (DescribeDelegationTokenOwner) obj;
            if (this.principalType == null) {
                if (other.principalType != null) return false;
            } else {
                if (!this.principalType.equals(other.principalType)) return false;
            }
            if (this.principalName == null) {
                if (other.principalName != null) return false;
            } else {
                if (!this.principalName.equals(other.principalName)) return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (principalType == null ? 0 : principalType.hashCode());
            hashCode = 31 * hashCode + (principalName == null ? 0 : principalName.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "DescribeDelegationTokenOwner("
                + "principalType=" + ((principalType == null) ? "null" : "'" + principalType.toString() + "'")
                + ", principalName=" + ((principalName == null) ? "null" : "'" + principalName.toString() + "'")
                + ")";
        }
        
        public String principalType() {
            return this.principalType;
        }
        
        public String principalName() {
            return this.principalName;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public DescribeDelegationTokenOwner setPrincipalType(String v) {
            this.principalType = v;
            return this;
        }
        
        public DescribeDelegationTokenOwner setPrincipalName(String v) {
            this.principalName = v;
            return this;
        }
    }
}
