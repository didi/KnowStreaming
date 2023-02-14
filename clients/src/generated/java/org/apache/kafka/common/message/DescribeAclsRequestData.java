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
import org.apache.kafka.common.protocol.ObjectSerializationCache;
import org.apache.kafka.common.protocol.Readable;
import org.apache.kafka.common.protocol.Writable;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.protocol.types.RawTaggedField;
import org.apache.kafka.common.protocol.types.RawTaggedFieldWriter;
import org.apache.kafka.common.protocol.types.Schema;
import org.apache.kafka.common.protocol.types.Struct;
import org.apache.kafka.common.protocol.types.Type;
import org.apache.kafka.common.utils.ByteUtils;

import static java.util.Map.Entry;
import static org.apache.kafka.common.protocol.types.Field.TaggedFieldsSection;


public class DescribeAclsRequestData implements ApiMessage {
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
            new Field("resource_name_filter", Type.NULLABLE_STRING, "The resource name, or null to match any resource name."),
            new Field("principal_filter", Type.NULLABLE_STRING, "The principal to match, or null to match any principal."),
            new Field("host_filter", Type.NULLABLE_STRING, "The host to match, or null to match any host."),
            new Field("operation", Type.INT8, "The operation to match."),
            new Field("permission_type", Type.INT8, "The permission type to match.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("resource_type_filter", Type.INT8, "The resource type."),
            new Field("resource_name_filter", Type.NULLABLE_STRING, "The resource name, or null to match any resource name."),
            new Field("pattern_type_filter", Type.INT8, "The resource pattern to match."),
            new Field("principal_filter", Type.NULLABLE_STRING, "The principal to match, or null to match any principal."),
            new Field("host_filter", Type.NULLABLE_STRING, "The host to match, or null to match any host."),
            new Field("operation", Type.INT8, "The operation to match."),
            new Field("permission_type", Type.INT8, "The permission type to match.")
        );
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("resource_type_filter", Type.INT8, "The resource type."),
            new Field("resource_name_filter", Type.COMPACT_NULLABLE_STRING, "The resource name, or null to match any resource name."),
            new Field("pattern_type_filter", Type.INT8, "The resource pattern to match."),
            new Field("principal_filter", Type.COMPACT_NULLABLE_STRING, "The principal to match, or null to match any principal."),
            new Field("host_filter", Type.COMPACT_NULLABLE_STRING, "The host to match, or null to match any host."),
            new Field("operation", Type.INT8, "The operation to match."),
            new Field("permission_type", Type.INT8, "The permission type to match."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2
    };
    
    public DescribeAclsRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public DescribeAclsRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public DescribeAclsRequestData() {
        this.resourceTypeFilter = (byte) 0;
        this.resourceNameFilter = "";
        this.patternTypeFilter = (byte) 3;
        this.principalFilter = "";
        this.hostFilter = "";
        this.operation = (byte) 0;
        this.permissionType = (byte) 0;
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
        if (!(obj instanceof DescribeAclsRequestData)) return false;
        DescribeAclsRequestData other = (DescribeAclsRequestData) obj;
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
        return "DescribeAclsRequestData("
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
    
    public DescribeAclsRequestData setResourceTypeFilter(byte v) {
        this.resourceTypeFilter = v;
        return this;
    }
    
    public DescribeAclsRequestData setResourceNameFilter(String v) {
        this.resourceNameFilter = v;
        return this;
    }
    
    public DescribeAclsRequestData setPatternTypeFilter(byte v) {
        this.patternTypeFilter = v;
        return this;
    }
    
    public DescribeAclsRequestData setPrincipalFilter(String v) {
        this.principalFilter = v;
        return this;
    }
    
    public DescribeAclsRequestData setHostFilter(String v) {
        this.hostFilter = v;
        return this;
    }
    
    public DescribeAclsRequestData setOperation(byte v) {
        this.operation = v;
        return this;
    }
    
    public DescribeAclsRequestData setPermissionType(byte v) {
        this.permissionType = v;
        return this;
    }
}
