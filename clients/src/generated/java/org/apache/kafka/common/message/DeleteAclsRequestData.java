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


public class DeleteAclsRequestData implements ApiMessage {
    private List<DeleteAclsFilter> filters;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("filters", new ArrayOf(DeleteAclsFilter.SCHEMA_0), "The filters to use when deleting ACLs.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("filters", new ArrayOf(DeleteAclsFilter.SCHEMA_1), "The filters to use when deleting ACLs.")
        );
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("filters", new CompactArrayOf(DeleteAclsFilter.SCHEMA_2), "The filters to use when deleting ACLs."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2
    };
    
    public DeleteAclsRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public DeleteAclsRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public DeleteAclsRequestData() {
        this.filters = new ArrayList<DeleteAclsFilter>();
    }
    
    @Override
    public short apiKey() {
        return 31;
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
                    throw new RuntimeException("non-nullable field filters was serialized as null");
                } else {
                    ArrayList<DeleteAclsFilter> newCollection = new ArrayList<DeleteAclsFilter>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new DeleteAclsFilter(_readable, _version));
                    }
                    this.filters = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field filters was serialized as null");
                } else {
                    ArrayList<DeleteAclsFilter> newCollection = new ArrayList<DeleteAclsFilter>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new DeleteAclsFilter(_readable, _version));
                    }
                    this.filters = newCollection;
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
            _writable.writeUnsignedVarint(filters.size() + 1);
            for (DeleteAclsFilter filtersElement : filters) {
                filtersElement.write(_writable, _cache, _version);
            }
        } else {
            _writable.writeInt(filters.size());
            for (DeleteAclsFilter filtersElement : filters) {
                filtersElement.write(_writable, _cache, _version);
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
            Object[] _nestedObjects = struct.getArray("filters");
            this.filters = new ArrayList<DeleteAclsFilter>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.filters.add(new DeleteAclsFilter((Struct) nestedObject, _version));
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
            Struct[] _nestedObjects = new Struct[filters.size()];
            int i = 0;
            for (DeleteAclsFilter element : this.filters) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("filters", (Object[]) _nestedObjects);
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
                _arraySize += ByteUtils.sizeOfUnsignedVarint(filters.size() + 1);
            } else {
                _arraySize += 4;
            }
            for (DeleteAclsFilter filtersElement : filters) {
                _arraySize += filtersElement.size(_cache, _version);
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
        if (!(obj instanceof DeleteAclsRequestData)) return false;
        DeleteAclsRequestData other = (DeleteAclsRequestData) obj;
        if (this.filters == null) {
            if (other.filters != null) return false;
        } else {
            if (!this.filters.equals(other.filters)) return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (filters == null ? 0 : filters.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "DeleteAclsRequestData("
            + "filters=" + MessageUtil.deepToString(filters.iterator())
            + ")";
    }
    
    public List<DeleteAclsFilter> filters() {
        return this.filters;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public DeleteAclsRequestData setFilters(List<DeleteAclsFilter> v) {
        this.filters = v;
        return this;
    }
    
    static public class DeleteAclsFilter implements Message {
        private byte resourceTypeFilter;
        private String resourceNameFilter;
        private byte patternTypeFilter;
        private String principalFilter;
        private String hostFilter;
        private byte operation;
        private byte permissionType;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("resource_type_filter", Type.INT8, "The resource type."),
                new Field("resource_name_filter", Type.NULLABLE_STRING, "The resource name."),
                new Field("principal_filter", Type.NULLABLE_STRING, "The principal filter, or null to accept all principals."),
                new Field("host_filter", Type.NULLABLE_STRING, "The host filter, or null to accept all hosts."),
                new Field("operation", Type.INT8, "The ACL operation."),
                new Field("permission_type", Type.INT8, "The permission type.")
            );
        
        public static final Schema SCHEMA_1 =
            new Schema(
                new Field("resource_type_filter", Type.INT8, "The resource type."),
                new Field("resource_name_filter", Type.NULLABLE_STRING, "The resource name."),
                new Field("pattern_type_filter", Type.INT8, "The pattern type."),
                new Field("principal_filter", Type.NULLABLE_STRING, "The principal filter, or null to accept all principals."),
                new Field("host_filter", Type.NULLABLE_STRING, "The host filter, or null to accept all hosts."),
                new Field("operation", Type.INT8, "The ACL operation."),
                new Field("permission_type", Type.INT8, "The permission type.")
            );
        
        public static final Schema SCHEMA_2 =
            new Schema(
                new Field("resource_type_filter", Type.INT8, "The resource type."),
                new Field("resource_name_filter", Type.COMPACT_NULLABLE_STRING, "The resource name."),
                new Field("pattern_type_filter", Type.INT8, "The pattern type."),
                new Field("principal_filter", Type.COMPACT_NULLABLE_STRING, "The principal filter, or null to accept all principals."),
                new Field("host_filter", Type.COMPACT_NULLABLE_STRING, "The host filter, or null to accept all hosts."),
                new Field("operation", Type.INT8, "The ACL operation."),
                new Field("permission_type", Type.INT8, "The permission type."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2
        };
        
        public DeleteAclsFilter(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public DeleteAclsFilter(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public DeleteAclsFilter() {
            this.resourceTypeFilter = (byte) 0;
            this.resourceNameFilter = "";
            this.patternTypeFilter = (byte) 3;
            this.principalFilter = "";
            this.hostFilter = "";
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of DeleteAclsFilter");
            }
            this.resourceTypeFilter = _readable.readByte();
            {
                int length;
                if (_version >= 2) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    this.resourceNameFilter = null;
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field resourceNameFilter had invalid length " + length);
                } else {
                    this.resourceNameFilter = _readable.readString(length);
                }
            }
            if (_version >= 1) {
                this.patternTypeFilter = _readable.readByte();
            } else {
                this.patternTypeFilter = (byte) 3;
            }
            {
                int length;
                if (_version >= 2) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    this.principalFilter = null;
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field principalFilter had invalid length " + length);
                } else {
                    this.principalFilter = _readable.readString(length);
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
                    this.hostFilter = null;
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field hostFilter had invalid length " + length);
                } else {
                    this.hostFilter = _readable.readString(length);
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
                throw new UnsupportedVersionException("Can't write version " + _version + " of DeleteAclsFilter");
            }
            int _numTaggedFields = 0;
            _writable.writeByte(resourceTypeFilter);
            if (resourceNameFilter == null) {
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(0);
                } else {
                    _writable.writeShort((short) -1);
                }
            } else {
                byte[] _stringBytes = _cache.getSerializedValue(resourceNameFilter);
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            if (_version >= 1) {
                _writable.writeByte(patternTypeFilter);
            } else {
                if (patternTypeFilter != (byte) 3) {
                    throw new UnsupportedVersionException("Attempted to write a non-default patternTypeFilter at version " + _version);
                }
            }
            if (principalFilter == null) {
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(0);
                } else {
                    _writable.writeShort((short) -1);
                }
            } else {
                byte[] _stringBytes = _cache.getSerializedValue(principalFilter);
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            if (hostFilter == null) {
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(0);
                } else {
                    _writable.writeShort((short) -1);
                }
            } else {
                byte[] _stringBytes = _cache.getSerializedValue(hostFilter);
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of DeleteAclsFilter");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 2) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.resourceTypeFilter = struct.getByte("resource_type_filter");
            this.resourceNameFilter = struct.getString("resource_name_filter");
            if (_version >= 1) {
                this.patternTypeFilter = struct.getByte("pattern_type_filter");
            } else {
                this.patternTypeFilter = (byte) 3;
            }
            this.principalFilter = struct.getString("principal_filter");
            this.hostFilter = struct.getString("host_filter");
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
                throw new UnsupportedVersionException("Can't write version " + _version + " of DeleteAclsFilter");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 2) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("resource_type_filter", this.resourceTypeFilter);
            struct.set("resource_name_filter", this.resourceNameFilter);
            if (_version >= 1) {
                struct.set("pattern_type_filter", this.patternTypeFilter);
            } else {
                if (patternTypeFilter != (byte) 3) {
                    throw new UnsupportedVersionException("Attempted to write a non-default patternTypeFilter at version " + _version);
                }
            }
            struct.set("principal_filter", this.principalFilter);
            struct.set("host_filter", this.hostFilter);
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
                throw new UnsupportedVersionException("Can't size version " + _version + " of DeleteAclsFilter");
            }
            _size += 1;
            if (resourceNameFilter == null) {
                if (_version >= 2) {
                    _size += 1;
                } else {
                    _size += 2;
                }
            } else {
                byte[] _stringBytes = resourceNameFilter.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'resourceNameFilter' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(resourceNameFilter, _stringBytes);
                if (_version >= 2) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
            }
            if (_version >= 1) {
                _size += 1;
            }
            if (principalFilter == null) {
                if (_version >= 2) {
                    _size += 1;
                } else {
                    _size += 2;
                }
            } else {
                byte[] _stringBytes = principalFilter.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'principalFilter' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(principalFilter, _stringBytes);
                if (_version >= 2) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
            }
            if (hostFilter == null) {
                if (_version >= 2) {
                    _size += 1;
                } else {
                    _size += 2;
                }
            } else {
                byte[] _stringBytes = hostFilter.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'hostFilter' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(hostFilter, _stringBytes);
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
            if (!(obj instanceof DeleteAclsFilter)) return false;
            DeleteAclsFilter other = (DeleteAclsFilter) obj;
            if (resourceTypeFilter != other.resourceTypeFilter) return false;
            if (this.resourceNameFilter == null) {
                if (other.resourceNameFilter != null) return false;
            } else {
                if (!this.resourceNameFilter.equals(other.resourceNameFilter)) return false;
            }
            if (patternTypeFilter != other.patternTypeFilter) return false;
            if (this.principalFilter == null) {
                if (other.principalFilter != null) return false;
            } else {
                if (!this.principalFilter.equals(other.principalFilter)) return false;
            }
            if (this.hostFilter == null) {
                if (other.hostFilter != null) return false;
            } else {
                if (!this.hostFilter.equals(other.hostFilter)) return false;
            }
            if (operation != other.operation) return false;
            if (permissionType != other.permissionType) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + resourceTypeFilter;
            hashCode = 31 * hashCode + (resourceNameFilter == null ? 0 : resourceNameFilter.hashCode());
            hashCode = 31 * hashCode + patternTypeFilter;
            hashCode = 31 * hashCode + (principalFilter == null ? 0 : principalFilter.hashCode());
            hashCode = 31 * hashCode + (hostFilter == null ? 0 : hostFilter.hashCode());
            hashCode = 31 * hashCode + operation;
            hashCode = 31 * hashCode + permissionType;
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "DeleteAclsFilter("
                + "resourceTypeFilter=" + resourceTypeFilter
                + ", resourceNameFilter=" + ((resourceNameFilter == null) ? "null" : "'" + resourceNameFilter.toString() + "'")
                + ", patternTypeFilter=" + patternTypeFilter
                + ", principalFilter=" + ((principalFilter == null) ? "null" : "'" + principalFilter.toString() + "'")
                + ", hostFilter=" + ((hostFilter == null) ? "null" : "'" + hostFilter.toString() + "'")
                + ", operation=" + operation
                + ", permissionType=" + permissionType
                + ")";
        }
        
        public byte resourceTypeFilter() {
            return this.resourceTypeFilter;
        }
        
        public String resourceNameFilter() {
            return this.resourceNameFilter;
        }
        
        public byte patternTypeFilter() {
            return this.patternTypeFilter;
        }
        
        public String principalFilter() {
            return this.principalFilter;
        }
        
        public String hostFilter() {
            return this.hostFilter;
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
        
        public DeleteAclsFilter setResourceTypeFilter(byte v) {
            this.resourceTypeFilter = v;
            return this;
        }
        
        public DeleteAclsFilter setResourceNameFilter(String v) {
            this.resourceNameFilter = v;
            return this;
        }
        
        public DeleteAclsFilter setPatternTypeFilter(byte v) {
            this.patternTypeFilter = v;
            return this;
        }
        
        public DeleteAclsFilter setPrincipalFilter(String v) {
            this.principalFilter = v;
            return this;
        }
        
        public DeleteAclsFilter setHostFilter(String v) {
            this.hostFilter = v;
            return this;
        }
        
        public DeleteAclsFilter setOperation(byte v) {
            this.operation = v;
            return this;
        }
        
        public DeleteAclsFilter setPermissionType(byte v) {
            this.permissionType = v;
            return this;
        }
    }
}
