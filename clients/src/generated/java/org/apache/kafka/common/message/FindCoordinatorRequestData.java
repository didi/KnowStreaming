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


public class FindCoordinatorRequestData implements ApiMessage {
    private String key;
    private byte keyType;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("key", Type.STRING, "The coordinator key.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("key", Type.STRING, "The coordinator key."),
            new Field("key_type", Type.INT8, "The coordinator key type.  (Group, transaction, etc.)")
        );
    
    public static final Schema SCHEMA_2 = SCHEMA_1;
    
    public static final Schema SCHEMA_3 =
        new Schema(
            new Field("key", Type.COMPACT_STRING, "The coordinator key."),
            new Field("key_type", Type.INT8, "The coordinator key type.  (Group, transaction, etc.)"),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2,
        SCHEMA_3
    };
    
    public FindCoordinatorRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public FindCoordinatorRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public FindCoordinatorRequestData() {
        this.key = "";
        this.keyType = (byte) 0;
    }
    
    @Override
    public short apiKey() {
        return 10;
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
        {
            int length;
            if (_version >= 3) {
                length = _readable.readUnsignedVarint() - 1;
            } else {
                length = _readable.readShort();
            }
            if (length < 0) {
                throw new RuntimeException("non-nullable field key was serialized as null");
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field key had invalid length " + length);
            } else {
                this.key = _readable.readString(length);
            }
        }
        if (_version >= 1) {
            this.keyType = _readable.readByte();
        } else {
            this.keyType = (byte) 0;
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
        {
            byte[] _stringBytes = _cache.getSerializedValue(key);
            if (_version >= 3) {
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
            } else {
                _writable.writeShort((short) _stringBytes.length);
            }
            _writable.writeByteArray(_stringBytes);
        }
        if (_version >= 1) {
            _writable.writeByte(keyType);
        } else {
            if (keyType != (byte) 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default keyType at version " + _version);
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
        this.key = struct.getString("key");
        if (_version >= 1) {
            this.keyType = struct.getByte("key_type");
        } else {
            this.keyType = (byte) 0;
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
        struct.set("key", this.key);
        if (_version >= 1) {
            struct.set("key_type", this.keyType);
        } else {
            if (keyType != (byte) 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default keyType at version " + _version);
            }
        }
        if (_version >= 3) {
            struct.set("_tagged_fields", _taggedFields);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        {
            byte[] _stringBytes = key.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'key' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(key, _stringBytes);
            if (_version >= 3) {
                _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
            } else {
                _size += _stringBytes.length + 2;
            }
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
        if (!(obj instanceof FindCoordinatorRequestData)) return false;
        FindCoordinatorRequestData other = (FindCoordinatorRequestData) obj;
        if (this.key == null) {
            if (other.key != null) return false;
        } else {
            if (!this.key.equals(other.key)) return false;
        }
        if (keyType != other.keyType) return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (key == null ? 0 : key.hashCode());
        hashCode = 31 * hashCode + keyType;
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "FindCoordinatorRequestData("
            + "key=" + ((key == null) ? "null" : "'" + key.toString() + "'")
            + ", keyType=" + keyType
            + ")";
    }
    
    public String key() {
        return this.key;
    }
    
    public byte keyType() {
        return this.keyType;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public FindCoordinatorRequestData setKey(String v) {
        this.key = v;
        return this;
    }
    
    public FindCoordinatorRequestData setKeyType(byte v) {
        this.keyType = v;
        return this;
    }
}
