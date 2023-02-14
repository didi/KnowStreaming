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
import java.util.TreeMap;
import org.apache.kafka.common.errors.UnsupportedVersionException;
import org.apache.kafka.common.protocol.ApiMessage;
import org.apache.kafka.common.protocol.Message;
import org.apache.kafka.common.protocol.MessageUtil;
import org.apache.kafka.common.protocol.ObjectSerializationCache;
import org.apache.kafka.common.protocol.Readable;
import org.apache.kafka.common.protocol.Writable;
import org.apache.kafka.common.protocol.types.ArrayOf;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.protocol.types.RawTaggedField;
import org.apache.kafka.common.protocol.types.RawTaggedFieldWriter;
import org.apache.kafka.common.protocol.types.Schema;
import org.apache.kafka.common.protocol.types.Struct;
import org.apache.kafka.common.protocol.types.Type;
import org.apache.kafka.common.utils.ByteUtils;


public class DescribeConfigsRequestData implements ApiMessage {
    private List<DescribeConfigsResource> resources;
    private boolean includeSynoyms;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("resources", new ArrayOf(DescribeConfigsResource.SCHEMA_0), "The resources whose configurations we want to describe.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("resources", new ArrayOf(DescribeConfigsResource.SCHEMA_0), "The resources whose configurations we want to describe."),
            new Field("include_synoyms", Type.BOOLEAN, "True if we should include all synonyms.")
        );
    
    public static final Schema SCHEMA_2 = SCHEMA_1;
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2
    };
    
    public DescribeConfigsRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public DescribeConfigsRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public DescribeConfigsRequestData() {
        this.resources = new ArrayList<DescribeConfigsResource>();
        this.includeSynoyms = false;
    }
    
    @Override
    public short apiKey() {
        return 32;
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
            int arrayLength;
            arrayLength = _readable.readInt();
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field resources was serialized as null");
            } else {
                ArrayList<DescribeConfigsResource> newCollection = new ArrayList<DescribeConfigsResource>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new DescribeConfigsResource(_readable, _version));
                }
                this.resources = newCollection;
            }
        }
        if (_version >= 1) {
            this.includeSynoyms = _readable.readByte() != 0;
        } else {
            this.includeSynoyms = false;
        }
        this._unknownTaggedFields = null;
    }
    
    @Override
    public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        _writable.writeInt(resources.size());
        for (DescribeConfigsResource resourcesElement : resources) {
            resourcesElement.write(_writable, _cache, _version);
        }
        if (_version >= 1) {
            _writable.writeByte(includeSynoyms ? (byte) 1 : (byte) 0);
        } else {
            if (includeSynoyms) {
                throw new UnsupportedVersionException("Attempted to write a non-default includeSynoyms at version " + _version);
            }
        }
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        if (_numTaggedFields > 0) {
            throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void fromStruct(Struct struct, short _version) {
        this._unknownTaggedFields = null;
        {
            Object[] _nestedObjects = struct.getArray("resources");
            this.resources = new ArrayList<DescribeConfigsResource>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.resources.add(new DescribeConfigsResource((Struct) nestedObject, _version));
            }
        }
        if (_version >= 1) {
            this.includeSynoyms = struct.getBoolean("include_synoyms");
        } else {
            this.includeSynoyms = false;
        }
    }
    
    @Override
    public Struct toStruct(short _version) {
        TreeMap<Integer, Object> _taggedFields = null;
        Struct struct = new Struct(SCHEMAS[_version]);
        {
            Struct[] _nestedObjects = new Struct[resources.size()];
            int i = 0;
            for (DescribeConfigsResource element : this.resources) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("resources", (Object[]) _nestedObjects);
        }
        if (_version >= 1) {
            struct.set("include_synoyms", this.includeSynoyms);
        } else {
            if (includeSynoyms) {
                throw new UnsupportedVersionException("Attempted to write a non-default includeSynoyms at version " + _version);
            }
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        {
            int _arraySize = 0;
            _arraySize += 4;
            for (DescribeConfigsResource resourcesElement : resources) {
                _arraySize += resourcesElement.size(_cache, _version);
            }
            _size += _arraySize;
        }
        if (_version >= 1) {
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
        if (_numTaggedFields > 0) {
            throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
        }
        return _size;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DescribeConfigsRequestData)) return false;
        DescribeConfigsRequestData other = (DescribeConfigsRequestData) obj;
        if (this.resources == null) {
            if (other.resources != null) return false;
        } else {
            if (!this.resources.equals(other.resources)) return false;
        }
        if (includeSynoyms != other.includeSynoyms) return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (resources == null ? 0 : resources.hashCode());
        hashCode = 31 * hashCode + (includeSynoyms ? 1231 : 1237);
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "DescribeConfigsRequestData("
            + "resources=" + MessageUtil.deepToString(resources.iterator())
            + ", includeSynoyms=" + (includeSynoyms ? "true" : "false")
            + ")";
    }
    
    public List<DescribeConfigsResource> resources() {
        return this.resources;
    }
    
    public boolean includeSynoyms() {
        return this.includeSynoyms;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public DescribeConfigsRequestData setResources(List<DescribeConfigsResource> v) {
        this.resources = v;
        return this;
    }
    
    public DescribeConfigsRequestData setIncludeSynoyms(boolean v) {
        this.includeSynoyms = v;
        return this;
    }
    
    static public class DescribeConfigsResource implements Message {
        private byte resourceType;
        private String resourceName;
        private List<String> configurationKeys;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("resource_type", Type.INT8, "The resource type."),
                new Field("resource_name", Type.STRING, "The resource name."),
                new Field("configuration_keys", ArrayOf.nullable(Type.STRING), "The configuration keys to list, or null to list all configuration keys.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2
        };
        
        public DescribeConfigsResource(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public DescribeConfigsResource(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public DescribeConfigsResource() {
            this.resourceType = (byte) 0;
            this.resourceName = "";
            this.configurationKeys = new ArrayList<String>();
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of DescribeConfigsResource");
            }
            this.resourceType = _readable.readByte();
            {
                int length;
                length = _readable.readShort();
                if (length < 0) {
                    throw new RuntimeException("non-nullable field resourceName was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field resourceName had invalid length " + length);
                } else {
                    this.resourceName = _readable.readString(length);
                }
            }
            {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    this.configurationKeys = null;
                } else {
                    ArrayList<String> newCollection = new ArrayList<String>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        int length;
                        length = _readable.readShort();
                        if (length < 0) {
                            throw new RuntimeException("non-nullable field configurationKeys element was serialized as null");
                        } else if (length > 0x7fff) {
                            throw new RuntimeException("string field configurationKeys element had invalid length " + length);
                        } else {
                            newCollection.add(_readable.readString(length));
                        }
                    }
                    this.configurationKeys = newCollection;
                }
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of DescribeConfigsResource");
            }
            int _numTaggedFields = 0;
            _writable.writeByte(resourceType);
            {
                byte[] _stringBytes = _cache.getSerializedValue(resourceName);
                _writable.writeShort((short) _stringBytes.length);
                _writable.writeByteArray(_stringBytes);
            }
            if (configurationKeys == null) {
                _writable.writeInt(-1);
            } else {
                _writable.writeInt(configurationKeys.size());
                for (String configurationKeysElement : configurationKeys) {
                    {
                        byte[] _stringBytes = _cache.getSerializedValue(configurationKeysElement);
                        _writable.writeShort((short) _stringBytes.length);
                        _writable.writeByteArray(_stringBytes);
                    }
                }
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public void fromStruct(Struct struct, short _version) {
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of DescribeConfigsResource");
            }
            this._unknownTaggedFields = null;
            this.resourceType = struct.getByte("resource_type");
            this.resourceName = struct.getString("resource_name");
            {
                Object[] _nestedObjects = struct.getArray("configuration_keys");
                if (_nestedObjects == null) {
                    this.configurationKeys = null;
                } else {
                    this.configurationKeys = new ArrayList<String>(_nestedObjects.length);
                    for (Object nestedObject : _nestedObjects) {
                        this.configurationKeys.add((String) nestedObject);
                    }
                }
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of DescribeConfigsResource");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("resource_type", this.resourceType);
            struct.set("resource_name", this.resourceName);
            {
                if (configurationKeys == null) {
                    struct.set("configuration_keys", null);
                } else {
                    String[] _nestedObjects = new String[configurationKeys.size()];
                    int i = 0;
                    for (String element : this.configurationKeys) {
                        _nestedObjects[i++] = element;
                    }
                    struct.set("configuration_keys", (Object[]) _nestedObjects);
                }
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of DescribeConfigsResource");
            }
            _size += 1;
            {
                byte[] _stringBytes = resourceName.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'resourceName' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(resourceName, _stringBytes);
                _size += _stringBytes.length + 2;
            }
            if (configurationKeys == null) {
                _size += 4;
            } else {
                int _arraySize = 0;
                _arraySize += 4;
                for (String configurationKeysElement : configurationKeys) {
                    byte[] _stringBytes = configurationKeysElement.getBytes(StandardCharsets.UTF_8);
                    if (_stringBytes.length > 0x7fff) {
                        throw new RuntimeException("'configurationKeysElement' field is too long to be serialized");
                    }
                    _cache.cacheSerializedValue(configurationKeysElement, _stringBytes);
                    _arraySize += _stringBytes.length + 2;
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
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
            return _size;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DescribeConfigsResource)) return false;
            DescribeConfigsResource other = (DescribeConfigsResource) obj;
            if (resourceType != other.resourceType) return false;
            if (this.resourceName == null) {
                if (other.resourceName != null) return false;
            } else {
                if (!this.resourceName.equals(other.resourceName)) return false;
            }
            if (this.configurationKeys == null) {
                if (other.configurationKeys != null) return false;
            } else {
                if (!this.configurationKeys.equals(other.configurationKeys)) return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + resourceType;
            hashCode = 31 * hashCode + (resourceName == null ? 0 : resourceName.hashCode());
            hashCode = 31 * hashCode + (configurationKeys == null ? 0 : configurationKeys.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "DescribeConfigsResource("
                + "resourceType=" + resourceType
                + ", resourceName=" + ((resourceName == null) ? "null" : "'" + resourceName.toString() + "'")
                + ", configurationKeys=" + ((configurationKeys == null) ? "null" : MessageUtil.deepToString(configurationKeys.iterator()))
                + ")";
        }
        
        public byte resourceType() {
            return this.resourceType;
        }
        
        public String resourceName() {
            return this.resourceName;
        }
        
        public List<String> configurationKeys() {
            return this.configurationKeys;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public DescribeConfigsResource setResourceType(byte v) {
            this.resourceType = v;
            return this;
        }
        
        public DescribeConfigsResource setResourceName(String v) {
            this.resourceName = v;
            return this;
        }
        
        public DescribeConfigsResource setConfigurationKeys(List<String> v) {
            this.configurationKeys = v;
            return this;
        }
    }
}
