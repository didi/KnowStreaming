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

import java.util.ArrayList;
import java.util.Iterator;
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
import org.apache.kafka.common.utils.ImplicitLinkedHashCollection;
import org.apache.kafka.common.utils.ImplicitLinkedHashMultiCollection;

import static java.util.Map.Entry;
import static org.apache.kafka.common.protocol.types.Field.TaggedFieldsSection;


public class ApiVersionsResponseData implements ApiMessage {
    private short errorCode;
    private ApiVersionsResponseKeyCollection apiKeys;
    private int throttleTimeMs;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("error_code", Type.INT16, "The top-level error code."),
            new Field("api_keys", new ArrayOf(ApiVersionsResponseKey.SCHEMA_0), "The APIs supported by the broker.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("error_code", Type.INT16, "The top-level error code."),
            new Field("api_keys", new ArrayOf(ApiVersionsResponseKey.SCHEMA_0), "The APIs supported by the broker."),
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota.")
        );
    
    public static final Schema SCHEMA_2 = SCHEMA_1;
    
    public static final Schema SCHEMA_3 =
        new Schema(
            new Field("error_code", Type.INT16, "The top-level error code."),
            new Field("api_keys", new CompactArrayOf(ApiVersionsResponseKey.SCHEMA_3), "The APIs supported by the broker."),
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2,
        SCHEMA_3
    };
    
    public ApiVersionsResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public ApiVersionsResponseData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public ApiVersionsResponseData() {
        this.errorCode = (short) 0;
        this.apiKeys = new ApiVersionsResponseKeyCollection(0);
        this.throttleTimeMs = 0;
    }
    
    @Override
    public short apiKey() {
        return 18;
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
        this.errorCode = _readable.readShort();
        {
            if (_version >= 3) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field apiKeys was serialized as null");
                } else {
                    ApiVersionsResponseKeyCollection newCollection = new ApiVersionsResponseKeyCollection(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new ApiVersionsResponseKey(_readable, _version));
                    }
                    this.apiKeys = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field apiKeys was serialized as null");
                } else {
                    ApiVersionsResponseKeyCollection newCollection = new ApiVersionsResponseKeyCollection(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new ApiVersionsResponseKey(_readable, _version));
                    }
                    this.apiKeys = newCollection;
                }
            }
        }
        if (_version >= 1) {
            this.throttleTimeMs = _readable.readInt();
        } else {
            this.throttleTimeMs = 0;
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
        _writable.writeShort(errorCode);
        if (_version >= 3) {
            _writable.writeUnsignedVarint(apiKeys.size() + 1);
            for (ApiVersionsResponseKey apiKeysElement : apiKeys) {
                apiKeysElement.write(_writable, _cache, _version);
            }
        } else {
            _writable.writeInt(apiKeys.size());
            for (ApiVersionsResponseKey apiKeysElement : apiKeys) {
                apiKeysElement.write(_writable, _cache, _version);
            }
        }
        if (_version >= 1) {
            _writable.writeInt(throttleTimeMs);
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
        this.errorCode = struct.getShort("error_code");
        {
            Object[] _nestedObjects = struct.getArray("api_keys");
            this.apiKeys = new ApiVersionsResponseKeyCollection(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.apiKeys.add(new ApiVersionsResponseKey((Struct) nestedObject, _version));
            }
        }
        if (_version >= 1) {
            this.throttleTimeMs = struct.getInt("throttle_time_ms");
        } else {
            this.throttleTimeMs = 0;
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
        struct.set("error_code", this.errorCode);
        {
            Struct[] _nestedObjects = new Struct[apiKeys.size()];
            int i = 0;
            for (ApiVersionsResponseKey element : this.apiKeys) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("api_keys", (Object[]) _nestedObjects);
        }
        if (_version >= 1) {
            struct.set("throttle_time_ms", this.throttleTimeMs);
        }
        if (_version >= 3) {
            struct.set("_tagged_fields", _taggedFields);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        _size += 2;
        {
            int _arraySize = 0;
            if (_version >= 3) {
                _arraySize += ByteUtils.sizeOfUnsignedVarint(apiKeys.size() + 1);
            } else {
                _arraySize += 4;
            }
            for (ApiVersionsResponseKey apiKeysElement : apiKeys) {
                _arraySize += apiKeysElement.size(_cache, _version);
            }
            _size += _arraySize;
        }
        if (_version >= 1) {
            _size += 4;
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
        if (!(obj instanceof ApiVersionsResponseData)) return false;
        ApiVersionsResponseData other = (ApiVersionsResponseData) obj;
        if (errorCode != other.errorCode) return false;
        if (this.apiKeys == null) {
            if (other.apiKeys != null) return false;
        } else {
            if (!this.apiKeys.equals(other.apiKeys)) return false;
        }
        if (throttleTimeMs != other.throttleTimeMs) return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + errorCode;
        hashCode = 31 * hashCode + (apiKeys == null ? 0 : apiKeys.hashCode());
        hashCode = 31 * hashCode + throttleTimeMs;
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "ApiVersionsResponseData("
            + "errorCode=" + errorCode
            + ", apiKeys=" + MessageUtil.deepToString(apiKeys.iterator())
            + ", throttleTimeMs=" + throttleTimeMs
            + ")";
    }
    
    public short errorCode() {
        return this.errorCode;
    }
    
    public ApiVersionsResponseKeyCollection apiKeys() {
        return this.apiKeys;
    }
    
    public int throttleTimeMs() {
        return this.throttleTimeMs;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public ApiVersionsResponseData setErrorCode(short v) {
        this.errorCode = v;
        return this;
    }
    
    public ApiVersionsResponseData setApiKeys(ApiVersionsResponseKeyCollection v) {
        this.apiKeys = v;
        return this;
    }
    
    public ApiVersionsResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
    
    static public class ApiVersionsResponseKey implements Message, ImplicitLinkedHashMultiCollection.Element {
        private short apiKey;
        private short minVersion;
        private short maxVersion;
        private List<RawTaggedField> _unknownTaggedFields;
        private int next;
        private int prev;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("api_key", Type.INT16, "The API index."),
                new Field("min_version", Type.INT16, "The minimum supported version, inclusive."),
                new Field("max_version", Type.INT16, "The maximum supported version, inclusive.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 =
            new Schema(
                new Field("api_key", Type.INT16, "The API index."),
                new Field("min_version", Type.INT16, "The minimum supported version, inclusive."),
                new Field("max_version", Type.INT16, "The maximum supported version, inclusive."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3
        };
        
        public ApiVersionsResponseKey(Readable _readable, short _version) {
            read(_readable, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public ApiVersionsResponseKey(Struct struct, short _version) {
            fromStruct(struct, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public ApiVersionsResponseKey() {
            this.apiKey = (short) 0;
            this.minVersion = (short) 0;
            this.maxVersion = (short) 0;
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of ApiVersionsResponseKey");
            }
            this.apiKey = _readable.readShort();
            this.minVersion = _readable.readShort();
            this.maxVersion = _readable.readShort();
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
                throw new UnsupportedVersionException("Can't write version " + _version + " of ApiVersionsResponseKey");
            }
            int _numTaggedFields = 0;
            _writable.writeShort(apiKey);
            _writable.writeShort(minVersion);
            _writable.writeShort(maxVersion);
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of ApiVersionsResponseKey");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 3) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.apiKey = struct.getShort("api_key");
            this.minVersion = struct.getShort("min_version");
            this.maxVersion = struct.getShort("max_version");
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
                throw new UnsupportedVersionException("Can't write version " + _version + " of ApiVersionsResponseKey");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 3) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("api_key", this.apiKey);
            struct.set("min_version", this.minVersion);
            struct.set("max_version", this.maxVersion);
            if (_version >= 3) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 3) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of ApiVersionsResponseKey");
            }
            _size += 2;
            _size += 2;
            _size += 2;
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
            if (!(obj instanceof ApiVersionsResponseKey)) return false;
            ApiVersionsResponseKey other = (ApiVersionsResponseKey) obj;
            if (apiKey != other.apiKey) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + apiKey;
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "ApiVersionsResponseKey("
                + "apiKey=" + apiKey
                + ", minVersion=" + minVersion
                + ", maxVersion=" + maxVersion
                + ")";
        }
        
        public short apiKey() {
            return this.apiKey;
        }
        
        public short minVersion() {
            return this.minVersion;
        }
        
        public short maxVersion() {
            return this.maxVersion;
        }
        
        @Override
        public int next() {
            return this.next;
        }
        
        @Override
        public int prev() {
            return this.prev;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public ApiVersionsResponseKey setApiKey(short v) {
            this.apiKey = v;
            return this;
        }
        
        public ApiVersionsResponseKey setMinVersion(short v) {
            this.minVersion = v;
            return this;
        }
        
        public ApiVersionsResponseKey setMaxVersion(short v) {
            this.maxVersion = v;
            return this;
        }
        
        @Override
        public void setNext(int v) {
            this.next = v;
        }
        
        @Override
        public void setPrev(int v) {
            this.prev = v;
        }
    }
    
    public static class ApiVersionsResponseKeyCollection extends ImplicitLinkedHashMultiCollection<ApiVersionsResponseKey> {
        public ApiVersionsResponseKeyCollection() {
            super();
        }
        
        public ApiVersionsResponseKeyCollection(int expectedNumElements) {
            super(expectedNumElements);
        }
        
        public ApiVersionsResponseKeyCollection(Iterator<ApiVersionsResponseKey> iterator) {
            super(iterator);
        }
        
        public ApiVersionsResponseKey find(short apiKey) {
            ApiVersionsResponseKey _key = new ApiVersionsResponseKey();
            _key.setApiKey(apiKey);
            return find(_key);
        }
        
        public List<ApiVersionsResponseKey> findAll(short apiKey) {
            ApiVersionsResponseKey _key = new ApiVersionsResponseKey();
            _key.setApiKey(apiKey);
            return findAll(_key);
        }
        
    }
}
