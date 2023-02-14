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


public class DescribeAclsResponseData implements ApiMessage {
    private int throttleTimeMs;
    private short errorCode;
    private String errorMessage;
    private List<DescribeAclsResource> resources;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
            new Field("error_message", Type.NULLABLE_STRING, "The error message, or null if there was no error."),
            new Field("resources", new ArrayOf(DescribeAclsResource.SCHEMA_0), "Each Resource that is referenced in an ACL.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
            new Field("error_message", Type.NULLABLE_STRING, "The error message, or null if there was no error."),
            new Field("resources", new ArrayOf(DescribeAclsResource.SCHEMA_1), "Each Resource that is referenced in an ACL.")
        );
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
            new Field("error_message", Type.COMPACT_NULLABLE_STRING, "The error message, or null if there was no error."),
            new Field("resources", new CompactArrayOf(DescribeAclsResource.SCHEMA_2), "Each Resource that is referenced in an ACL."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2
    };
    
    public DescribeAclsResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public DescribeAclsResponseData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public DescribeAclsResponseData() {
        this.throttleTimeMs = 0;
        this.errorCode = (short) 0;
        this.errorMessage = "";
        this.resources = new ArrayList<DescribeAclsResource>();
    }
    
    @Override
    public short apiKey() {
        return 29;
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
        this.throttleTimeMs = _readable.readInt();
        this.errorCode = _readable.readShort();
        {
            int length;
            if (_version >= 2) {
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
        }
        {
            if (_version >= 2) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field resources was serialized as null");
                } else {
                    ArrayList<DescribeAclsResource> newCollection = new ArrayList<DescribeAclsResource>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new DescribeAclsResource(_readable, _version));
                    }
                    this.resources = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field resources was serialized as null");
                } else {
                    ArrayList<DescribeAclsResource> newCollection = new ArrayList<DescribeAclsResource>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new DescribeAclsResource(_readable, _version));
                    }
                    this.resources = newCollection;
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
        _writable.writeInt(throttleTimeMs);
        _writable.writeShort(errorCode);
        if (errorMessage == null) {
            if (_version >= 2) {
                _writable.writeUnsignedVarint(0);
            } else {
                _writable.writeShort((short) -1);
            }
        } else {
            byte[] _stringBytes = _cache.getSerializedValue(errorMessage);
            if (_version >= 2) {
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
            } else {
                _writable.writeShort((short) _stringBytes.length);
            }
            _writable.writeByteArray(_stringBytes);
        }
        if (_version >= 2) {
            _writable.writeUnsignedVarint(resources.size() + 1);
            for (DescribeAclsResource resourcesElement : resources) {
                resourcesElement.write(_writable, _cache, _version);
            }
        } else {
            _writable.writeInt(resources.size());
            for (DescribeAclsResource resourcesElement : resources) {
                resourcesElement.write(_writable, _cache, _version);
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
        this.throttleTimeMs = struct.getInt("throttle_time_ms");
        this.errorCode = struct.getShort("error_code");
        this.errorMessage = struct.getString("error_message");
        {
            Object[] _nestedObjects = struct.getArray("resources");
            this.resources = new ArrayList<DescribeAclsResource>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.resources.add(new DescribeAclsResource((Struct) nestedObject, _version));
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
        struct.set("throttle_time_ms", this.throttleTimeMs);
        struct.set("error_code", this.errorCode);
        struct.set("error_message", this.errorMessage);
        {
            Struct[] _nestedObjects = new Struct[resources.size()];
            int i = 0;
            for (DescribeAclsResource element : this.resources) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("resources", (Object[]) _nestedObjects);
        }
        if (_version >= 2) {
            struct.set("_tagged_fields", _taggedFields);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        _size += 4;
        _size += 2;
        if (errorMessage == null) {
            if (_version >= 2) {
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
            if (_version >= 2) {
                _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
            } else {
                _size += _stringBytes.length + 2;
            }
        }
        {
            int _arraySize = 0;
            if (_version >= 2) {
                _arraySize += ByteUtils.sizeOfUnsignedVarint(resources.size() + 1);
            } else {
                _arraySize += 4;
            }
            for (DescribeAclsResource resourcesElement : resources) {
                _arraySize += resourcesElement.size(_cache, _version);
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
        if (!(obj instanceof DescribeAclsResponseData)) return false;
        DescribeAclsResponseData other = (DescribeAclsResponseData) obj;
        if (throttleTimeMs != other.throttleTimeMs) return false;
        if (errorCode != other.errorCode) return false;
        if (this.errorMessage == null) {
            if (other.errorMessage != null) return false;
        } else {
            if (!this.errorMessage.equals(other.errorMessage)) return false;
        }
        if (this.resources == null) {
            if (other.resources != null) return false;
        } else {
            if (!this.resources.equals(other.resources)) return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + throttleTimeMs;
        hashCode = 31 * hashCode + errorCode;
        hashCode = 31 * hashCode + (errorMessage == null ? 0 : errorMessage.hashCode());
        hashCode = 31 * hashCode + (resources == null ? 0 : resources.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "DescribeAclsResponseData("
            + "throttleTimeMs=" + throttleTimeMs
            + ", errorCode=" + errorCode
            + ", errorMessage=" + ((errorMessage == null) ? "null" : "'" + errorMessage.toString() + "'")
            + ", resources=" + MessageUtil.deepToString(resources.iterator())
            + ")";
    }
    
    public int throttleTimeMs() {
        return this.throttleTimeMs;
    }
    
    public short errorCode() {
        return this.errorCode;
    }
    
    public String errorMessage() {
        return this.errorMessage;
    }
    
    public List<DescribeAclsResource> resources() {
        return this.resources;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public DescribeAclsResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
    
    public DescribeAclsResponseData setErrorCode(short v) {
        this.errorCode = v;
        return this;
    }
    
    public DescribeAclsResponseData setErrorMessage(String v) {
        this.errorMessage = v;
        return this;
    }
    
    public DescribeAclsResponseData setResources(List<DescribeAclsResource> v) {
        this.resources = v;
        return this;
    }
    
    static public class DescribeAclsResource implements Message {
        private byte resourceType;
        private String resourceName;
        private byte patternType;
        private List<AclDescription> acls;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("resource_type", Type.INT8, "The resource type."),
                new Field("resource_name", Type.STRING, "The resource name."),
                new Field("acls", new ArrayOf(AclDescription.SCHEMA_0), "The ACLs.")
            );
        
        public static final Schema SCHEMA_1 =
            new Schema(
                new Field("resource_type", Type.INT8, "The resource type."),
                new Field("resource_name", Type.STRING, "The resource name."),
                new Field("pattern_type", Type.INT8, "The resource pattern type."),
                new Field("acls", new ArrayOf(AclDescription.SCHEMA_0), "The ACLs.")
            );
        
        public static final Schema SCHEMA_2 =
            new Schema(
                new Field("resource_type", Type.INT8, "The resource type."),
                new Field("resource_name", Type.COMPACT_STRING, "The resource name."),
                new Field("pattern_type", Type.INT8, "The resource pattern type."),
                new Field("acls", new CompactArrayOf(AclDescription.SCHEMA_2), "The ACLs."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2
        };
        
        public DescribeAclsResource(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public DescribeAclsResource(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public DescribeAclsResource() {
            this.resourceType = (byte) 0;
            this.resourceName = "";
            this.patternType = (byte) 3;
            this.acls = new ArrayList<AclDescription>();
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of DescribeAclsResource");
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
                this.patternType = _readable.readByte();
            } else {
                this.patternType = (byte) 3;
            }
            {
                if (_version >= 2) {
                    int arrayLength;
                    arrayLength = _readable.readUnsignedVarint() - 1;
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field acls was serialized as null");
                    } else {
                        ArrayList<AclDescription> newCollection = new ArrayList<AclDescription>(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new AclDescription(_readable, _version));
                        }
                        this.acls = newCollection;
                    }
                } else {
                    int arrayLength;
                    arrayLength = _readable.readInt();
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field acls was serialized as null");
                    } else {
                        ArrayList<AclDescription> newCollection = new ArrayList<AclDescription>(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new AclDescription(_readable, _version));
                        }
                        this.acls = newCollection;
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
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of DescribeAclsResource");
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
                _writable.writeByte(patternType);
            } else {
                if (patternType != (byte) 3) {
                    throw new UnsupportedVersionException("Attempted to write a non-default patternType at version " + _version);
                }
            }
            if (_version >= 2) {
                _writable.writeUnsignedVarint(acls.size() + 1);
                for (AclDescription aclsElement : acls) {
                    aclsElement.write(_writable, _cache, _version);
                }
            } else {
                _writable.writeInt(acls.size());
                for (AclDescription aclsElement : acls) {
                    aclsElement.write(_writable, _cache, _version);
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
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of DescribeAclsResource");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 2) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.resourceType = struct.getByte("resource_type");
            this.resourceName = struct.getString("resource_name");
            if (_version >= 1) {
                this.patternType = struct.getByte("pattern_type");
            } else {
                this.patternType = (byte) 3;
            }
            {
                Object[] _nestedObjects = struct.getArray("acls");
                this.acls = new ArrayList<AclDescription>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.acls.add(new AclDescription((Struct) nestedObject, _version));
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
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of DescribeAclsResource");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 2) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("resource_type", this.resourceType);
            struct.set("resource_name", this.resourceName);
            if (_version >= 1) {
                struct.set("pattern_type", this.patternType);
            } else {
                if (patternType != (byte) 3) {
                    throw new UnsupportedVersionException("Attempted to write a non-default patternType at version " + _version);
                }
            }
            {
                Struct[] _nestedObjects = new Struct[acls.size()];
                int i = 0;
                for (AclDescription element : this.acls) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("acls", (Object[]) _nestedObjects);
            }
            if (_version >= 2) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of DescribeAclsResource");
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
                int _arraySize = 0;
                if (_version >= 2) {
                    _arraySize += ByteUtils.sizeOfUnsignedVarint(acls.size() + 1);
                } else {
                    _arraySize += 4;
                }
                for (AclDescription aclsElement : acls) {
                    _arraySize += aclsElement.size(_cache, _version);
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
            if (!(obj instanceof DescribeAclsResource)) return false;
            DescribeAclsResource other = (DescribeAclsResource) obj;
            if (resourceType != other.resourceType) return false;
            if (this.resourceName == null) {
                if (other.resourceName != null) return false;
            } else {
                if (!this.resourceName.equals(other.resourceName)) return false;
            }
            if (patternType != other.patternType) return false;
            if (this.acls == null) {
                if (other.acls != null) return false;
            } else {
                if (!this.acls.equals(other.acls)) return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + resourceType;
            hashCode = 31 * hashCode + (resourceName == null ? 0 : resourceName.hashCode());
            hashCode = 31 * hashCode + patternType;
            hashCode = 31 * hashCode + (acls == null ? 0 : acls.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "DescribeAclsResource("
                + "resourceType=" + resourceType
                + ", resourceName=" + ((resourceName == null) ? "null" : "'" + resourceName.toString() + "'")
                + ", patternType=" + patternType
                + ", acls=" + MessageUtil.deepToString(acls.iterator())
                + ")";
        }
        
        public byte resourceType() {
            return this.resourceType;
        }
        
        public String resourceName() {
            return this.resourceName;
        }
        
        public byte patternType() {
            return this.patternType;
        }
        
        public List<AclDescription> acls() {
            return this.acls;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public DescribeAclsResource setResourceType(byte v) {
            this.resourceType = v;
            return this;
        }
        
        public DescribeAclsResource setResourceName(String v) {
            this.resourceName = v;
            return this;
        }
        
        public DescribeAclsResource setPatternType(byte v) {
            this.patternType = v;
            return this;
        }
        
        public DescribeAclsResource setAcls(List<AclDescription> v) {
            this.acls = v;
            return this;
        }
    }
    
    static public class AclDescription implements Message {
        private String principal;
        private String host;
        private byte operation;
        private byte permissionType;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("principal", Type.STRING, "The ACL principal."),
                new Field("host", Type.STRING, "The ACL host."),
                new Field("operation", Type.INT8, "The ACL operation."),
                new Field("permission_type", Type.INT8, "The ACL permission type.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 =
            new Schema(
                new Field("principal", Type.COMPACT_STRING, "The ACL principal."),
                new Field("host", Type.COMPACT_STRING, "The ACL host."),
                new Field("operation", Type.INT8, "The ACL operation."),
                new Field("permission_type", Type.INT8, "The ACL permission type."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2
        };
        
        public AclDescription(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public AclDescription(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public AclDescription() {
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of AclDescription");
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
                throw new UnsupportedVersionException("Can't write version " + _version + " of AclDescription");
            }
            int _numTaggedFields = 0;
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of AclDescription");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 2) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
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
                throw new UnsupportedVersionException("Can't write version " + _version + " of AclDescription");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 2) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
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
                throw new UnsupportedVersionException("Can't size version " + _version + " of AclDescription");
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
            if (!(obj instanceof AclDescription)) return false;
            AclDescription other = (AclDescription) obj;
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
            hashCode = 31 * hashCode + (principal == null ? 0 : principal.hashCode());
            hashCode = 31 * hashCode + (host == null ? 0 : host.hashCode());
            hashCode = 31 * hashCode + operation;
            hashCode = 31 * hashCode + permissionType;
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "AclDescription("
                + "principal=" + ((principal == null) ? "null" : "'" + principal.toString() + "'")
                + ", host=" + ((host == null) ? "null" : "'" + host.toString() + "'")
                + ", operation=" + operation
                + ", permissionType=" + permissionType
                + ")";
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
        
        public AclDescription setPrincipal(String v) {
            this.principal = v;
            return this;
        }
        
        public AclDescription setHost(String v) {
            this.host = v;
            return this;
        }
        
        public AclDescription setOperation(byte v) {
            this.operation = v;
            return this;
        }
        
        public AclDescription setPermissionType(byte v) {
            this.permissionType = v;
            return this;
        }
    }
}
