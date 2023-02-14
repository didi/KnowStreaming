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


public class CreateAclsRequestData implements ApiMessage {
    private List<AclCreation> creations;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("creations", new ArrayOf(AclCreation.SCHEMA_0), "The ACLs that we want to create.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("creations", new ArrayOf(AclCreation.SCHEMA_1), "The ACLs that we want to create.")
        );
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("creations", new CompactArrayOf(AclCreation.SCHEMA_2), "The ACLs that we want to create."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2
    };
    
    public CreateAclsRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public CreateAclsRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public CreateAclsRequestData() {
        this.creations = new ArrayList<AclCreation>();
    }
    
    @Override
    public short apiKey() {
        return 30;
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
                    throw new RuntimeException("non-nullable field creations was serialized as null");
                } else {
                    ArrayList<AclCreation> newCollection = new ArrayList<AclCreation>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new AclCreation(_readable, _version));
                    }
                    this.creations = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field creations was serialized as null");
                } else {
                    ArrayList<AclCreation> newCollection = new ArrayList<AclCreation>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new AclCreation(_readable, _version));
                    }
                    this.creations = newCollection;
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
            _writable.writeUnsignedVarint(creations.size() + 1);
            for (AclCreation creationsElement : creations) {
                creationsElement.write(_writable, _cache, _version);
            }
        } else {
            _writable.writeInt(creations.size());
            for (AclCreation creationsElement : creations) {
                creationsElement.write(_writable, _cache, _version);
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
            Object[] _nestedObjects = struct.getArray("creations");
            this.creations = new ArrayList<AclCreation>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.creations.add(new AclCreation((Struct) nestedObject, _version));
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
            Struct[] _nestedObjects = new Struct[creations.size()];
            int i = 0;
            for (AclCreation element : this.creations) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("creations", (Object[]) _nestedObjects);
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
                _arraySize += ByteUtils.sizeOfUnsignedVarint(creations.size() + 1);
            } else {
                _arraySize += 4;
            }
            for (AclCreation creationsElement : creations) {
                _arraySize += creationsElement.size(_cache, _version);
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
        if (!(obj instanceof CreateAclsRequestData)) return false;
        CreateAclsRequestData other = (CreateAclsRequestData) obj;
        if (this.creations == null) {
            if (other.creations != null) return false;
        } else {
            if (!this.creations.equals(other.creations)) return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (creations == null ? 0 : creations.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "CreateAclsRequestData("
            + "creations=" + MessageUtil.deepToString(creations.iterator())
            + ")";
    }
    
    public List<AclCreation> creations() {
        return this.creations;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public CreateAclsRequestData setCreations(List<AclCreation> v) {
        this.creations = v;
        return this;
    }
    
    static public class AclCreation implements Message {
        private byte resourceType;
        private String resourceName;
        private byte resourcePatternType;
        private String principal;
        private String host;
        private byte operation;
        private byte permissionType;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("resource_type", Type.INT8, "The type of the resource."),
                new Field("resource_name", Type.STRING, "The resource name for the ACL."),
                new Field("principal", Type.STRING, "The principal for the ACL."),
                new Field("host", Type.STRING, "The host for the ACL."),
                new Field("operation", Type.INT8, "The operation type for the ACL (read, write, etc.)."),
                new Field("permission_type", Type.INT8, "The permission type for the ACL (allow, deny, etc.).")
            );
        
        public static final Schema SCHEMA_1 =
            new Schema(
                new Field("resource_type", Type.INT8, "The type of the resource."),
                new Field("resource_name", Type.STRING, "The resource name for the ACL."),
                new Field("resource_pattern_type", Type.INT8, "The pattern type for the ACL."),
                new Field("principal", Type.STRING, "The principal for the ACL."),
                new Field("host", Type.STRING, "The host for the ACL."),
                new Field("operation", Type.INT8, "The operation type for the ACL (read, write, etc.)."),
                new Field("permission_type", Type.INT8, "The permission type for the ACL (allow, deny, etc.).")
            );
        
        public static final Schema SCHEMA_2 =
            new Schema(
                new Field("resource_type", Type.INT8, "The type of the resource."),
                new Field("resource_name", Type.COMPACT_STRING, "The resource name for the ACL."),
                new Field("resource_pattern_type", Type.INT8, "The pattern type for the ACL."),
                new Field("principal", Type.COMPACT_STRING, "The principal for the ACL."),
                new Field("host", Type.COMPACT_STRING, "The host for the ACL."),
                new Field("operation", Type.INT8, "The operation type for the ACL (read, write, etc.)."),
                new Field("permission_type", Type.INT8, "The permission type for the ACL (allow, deny, etc.)."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2
        };
        
        public AclCreation(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public AclCreation(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public AclCreation() {
            this.resourceType = (byte) 0;
            this.resourceName = "";
            this.resourcePatternType = (byte) 3;
            this.principal = "";
            this.host = "";
            this.operation = (byte) 0;
            this.permissionType = (byte) 0;
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of AclCreation");
            }
            this.resourceType = _readable.readByte();
            {
                int length;
                if (_version >= 2) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field resourceName was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field resourceName had invalid length " + length);
                } else {
                    this.resourceName = _readable.readString(length);
                }
            }
            if (_version >= 1) {
                this.resourcePatternType = _readable.readByte();
            } else {
                this.resourcePatternType = (byte) 3;
            }
            {
                int length;
                if (_version >= 2) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field principal was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field principal had invalid length " + length);
                } else {
                    this.principal = _readable.readString(length);
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
                    throw new RuntimeException("non-nullable field host was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field host had invalid length " + length);
                } else {
                    this.host = _readable.readString(length);
                }
            }
            this.operation = _readable.readByte();
            this.permissionType = _readable.readByte();
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
                throw new UnsupportedVersionException("Can't write version " + _version + " of AclCreation");
            }
            int _numTaggedFields = 0;
            _writable.writeByte(resourceType);
            {
                byte[] _stringBytes = _cache.getSerializedValue(resourceName);
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            if (_version >= 1) {
                _writable.writeByte(resourcePatternType);
            } else {
                if (resourcePatternType != (byte) 3) {
                    throw new UnsupportedVersionException("Attempted to write a non-default resourcePatternType at version " + _version);
                }
            }
            {
                byte[] _stringBytes = _cache.getSerializedValue(principal);
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            {
                byte[] _stringBytes = _cache.getSerializedValue(host);
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeByte(operation);
            _writable.writeByte(permissionType);
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of AclCreation");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 2) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.resourceType = struct.getByte("resource_type");
            this.resourceName = struct.getString("resource_name");
            if (_version >= 1) {
                this.resourcePatternType = struct.getByte("resource_pattern_type");
            } else {
                this.resourcePatternType = (byte) 3;
            }
            this.principal = struct.getString("principal");
            this.host = struct.getString("host");
            this.operation = struct.getByte("operation");
            this.permissionType = struct.getByte("permission_type");
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
                throw new UnsupportedVersionException("Can't write version " + _version + " of AclCreation");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 2) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("resource_type", this.resourceType);
            struct.set("resource_name", this.resourceName);
            if (_version >= 1) {
                struct.set("resource_pattern_type", this.resourcePatternType);
            } else {
                if (resourcePatternType != (byte) 3) {
                    throw new UnsupportedVersionException("Attempted to write a non-default resourcePatternType at version " + _version);
                }
            }
            struct.set("principal", this.principal);
            struct.set("host", this.host);
            struct.set("operation", this.operation);
            struct.set("permission_type", this.permissionType);
            if (_version >= 2) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of AclCreation");
            }
            _size += 1;
            {
                byte[] _stringBytes = resourceName.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'resourceName' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(resourceName, _stringBytes);
                if (_version >= 2) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
            }
            if (_version >= 1) {
                _size += 1;
            }
            {
                byte[] _stringBytes = principal.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'principal' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(principal, _stringBytes);
                if (_version >= 2) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
            }
            {
                byte[] _stringBytes = host.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'host' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(host, _stringBytes);
                if (_version >= 2) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
            }
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
            if (!(obj instanceof AclCreation)) return false;
            AclCreation other = (AclCreation) obj;
            if (resourceType != other.resourceType) return false;
            if (this.resourceName == null) {
                if (other.resourceName != null) return false;
            } else {
                if (!this.resourceName.equals(other.resourceName)) return false;
            }
            if (resourcePatternType != other.resourcePatternType) return false;
            if (this.principal == null) {
                if (other.principal != null) return false;
            } else {
                if (!this.principal.equals(other.principal)) return false;
            }
            if (this.host == null) {
                if (other.host != null) return false;
            } else {
                if (!this.host.equals(other.host)) return false;
            }
            if (operation != other.operation) return false;
            if (permissionType != other.permissionType) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + resourceType;
            hashCode = 31 * hashCode + (resourceName == null ? 0 : resourceName.hashCode());
            hashCode = 31 * hashCode + resourcePatternType;
            hashCode = 31 * hashCode + (principal == null ? 0 : principal.hashCode());
            hashCode = 31 * hashCode + (host == null ? 0 : host.hashCode());
            hashCode = 31 * hashCode + operation;
            hashCode = 31 * hashCode + permissionType;
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "AclCreation("
                + "resourceType=" + resourceType
                + ", resourceName=" + ((resourceName == null) ? "null" : "'" + resourceName.toString() + "'")
                + ", resourcePatternType=" + resourcePatternType
                + ", principal=" + ((principal == null) ? "null" : "'" + principal.toString() + "'")
                + ", host=" + ((host == null) ? "null" : "'" + host.toString() + "'")
                + ", operation=" + operation
                + ", permissionType=" + permissionType
                + ")";
        }
        
        public byte resourceType() {
            return this.resourceType;
        }
        
        public String resourceName() {
            return this.resourceName;
        }
        
        public byte resourcePatternType() {
            return this.resourcePatternType;
        }
        
        public String principal() {
            return this.principal;
        }
        
        public String host() {
            return this.host;
        }
        
        public byte operation() {
            return this.operation;
        }
        
        public byte permissionType() {
            return this.permissionType;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public AclCreation setResourceType(byte v) {
            this.resourceType = v;
            return this;
        }
        
        public AclCreation setResourceName(String v) {
            this.resourceName = v;
            return this;
        }
        
        public AclCreation setResourcePatternType(byte v) {
            this.resourcePatternType = v;
            return this;
        }
        
        public AclCreation setPrincipal(String v) {
            this.principal = v;
            return this;
        }
        
        public AclCreation setHost(String v) {
            this.host = v;
            return this;
        }
        
        public AclCreation setOperation(byte v) {
            this.operation = v;
            return this;
        }
        
        public AclCreation setPermissionType(byte v) {
            this.permissionType = v;
            return this;
        }
    }
}
