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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.UUID;
import org.apache.kafka.common.errors.UnsupportedVersionException;
import org.apache.kafka.common.protocol.ApiMessage;
import org.apache.kafka.common.protocol.MessageUtil;
import org.apache.kafka.common.protocol.ObjectSerializationCache;
import org.apache.kafka.common.protocol.Readable;
import org.apache.kafka.common.protocol.Writable;
import org.apache.kafka.common.protocol.types.CompactArrayOf;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.protocol.types.RawTaggedField;
import org.apache.kafka.common.protocol.types.RawTaggedFieldWriter;
import org.apache.kafka.common.protocol.types.Schema;
import org.apache.kafka.common.protocol.types.Struct;
import org.apache.kafka.common.protocol.types.Type;
import org.apache.kafka.common.utils.ByteUtils;
import org.apache.kafka.common.utils.Bytes;

import static java.util.Map.Entry;
import static org.apache.kafka.common.protocol.types.Field.TaggedFieldsSection;


public class SimpleExampleMessageData implements ApiMessage {
    private UUID processId;
    private List<Integer> myTaggedIntArray;
    private String myNullableString;
    private short myInt16;
    private double myFloat64;
    private String myString;
    private byte[] myBytes;
    private UUID taggedUuid;
    private long taggedLong;
    private ByteBuffer zeroCopyByteBuffer;
    private ByteBuffer nullableZeroCopyByteBuffer;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("process_id", Type.UUID, ""),
            new Field("zero_copy_byte_buffer", Type.COMPACT_BYTES, ""),
            new Field("nullable_zero_copy_byte_buffer", Type.COMPACT_NULLABLE_BYTES, ""),
            TaggedFieldsSection.of(
                0, new Field("my_tagged_int_array", new CompactArrayOf(Type.INT32), ""),
                1, new Field("my_nullable_string", Type.COMPACT_NULLABLE_STRING, ""),
                2, new Field("my_int16", Type.INT16, ""),
                3, new Field("my_float64", Type.FLOAT64, ""),
                4, new Field("my_string", Type.COMPACT_STRING, ""),
                5, new Field("my_bytes", Type.COMPACT_NULLABLE_BYTES, ""),
                6, new Field("tagged_uuid", Type.UUID, ""),
                7, new Field("tagged_long", Type.INT64, "")
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1
    };
    
    public SimpleExampleMessageData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public SimpleExampleMessageData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public SimpleExampleMessageData() {
        this.processId = MessageUtil.ZERO_UUID;
        this.myTaggedIntArray = new ArrayList<Integer>();
        this.myNullableString = null;
        this.myInt16 = (short) 123;
        this.myFloat64 = Double.parseDouble("12.34");
        this.myString = "";
        this.myBytes = Bytes.EMPTY;
        this.taggedUuid = UUID.fromString("212d5494-4a8b-4fdf-94b3-88b470beb367");
        this.taggedLong = 0xcafcacafcacafcaL;
        this.zeroCopyByteBuffer = ByteUtils.EMPTY_BUF;
        this.nullableZeroCopyByteBuffer = ByteUtils.EMPTY_BUF;
    }
    
    @Override
    public short apiKey() {
        return -1;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 1;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        if (_version >= 1) {
            this.processId = _readable.readUUID();
        } else {
            this.processId = MessageUtil.ZERO_UUID;
        }
        {
            this.myTaggedIntArray = new ArrayList<Integer>();
        }
        {
            this.myNullableString = null;
        }
        this.myInt16 = (short) 123;
        this.myFloat64 = Double.parseDouble("12.34");
        {
            this.myString = "";
        }
        {
            this.myBytes = Bytes.EMPTY;
        }
        this.taggedUuid = UUID.fromString("212d5494-4a8b-4fdf-94b3-88b470beb367");
        this.taggedLong = 0xcafcacafcacafcaL;
        if (_version >= 1) {
            int length;
            length = _readable.readUnsignedVarint() - 1;
            if (length < 0) {
                throw new RuntimeException("non-nullable field zeroCopyByteBuffer was serialized as null");
            } else {
                this.zeroCopyByteBuffer = _readable.readByteBuffer(length);
            }
        } else {
            this.zeroCopyByteBuffer = ByteUtils.EMPTY_BUF;
        }
        if (_version >= 1) {
            int length;
            length = _readable.readUnsignedVarint() - 1;
            if (length < 0) {
                this.nullableZeroCopyByteBuffer = null;
            } else {
                this.nullableZeroCopyByteBuffer = _readable.readByteBuffer(length);
            }
        } else {
            this.nullableZeroCopyByteBuffer = ByteUtils.EMPTY_BUF;
        }
        this._unknownTaggedFields = null;
        if (_version >= 1) {
            int _numTaggedFields = _readable.readUnsignedVarint();
            for (int _i = 0; _i < _numTaggedFields; _i++) {
                int _tag = _readable.readUnsignedVarint();
                int _size = _readable.readUnsignedVarint();
                switch (_tag) {
                    case 0: {
                        int arrayLength;
                        arrayLength = _readable.readUnsignedVarint() - 1;
                        if (arrayLength < 0) {
                            throw new RuntimeException("non-nullable field myTaggedIntArray was serialized as null");
                        } else {
                            ArrayList<Integer> newCollection = new ArrayList<Integer>(arrayLength);
                            for (int i = 0; i < arrayLength; i++) {
                                newCollection.add(_readable.readInt());
                            }
                            this.myTaggedIntArray = newCollection;
                        }
                        break;
                    }
                    case 1: {
                        int length;
                        length = _readable.readUnsignedVarint() - 1;
                        if (length < 0) {
                            this.myNullableString = null;
                        } else if (length > 0x7fff) {
                            throw new RuntimeException("string field myNullableString had invalid length " + length);
                        } else {
                            this.myNullableString = _readable.readString(length);
                        }
                        break;
                    }
                    case 2: {
                        this.myInt16 = _readable.readShort();
                        break;
                    }
                    case 3: {
                        this.myFloat64 = _readable.readDouble();
                        break;
                    }
                    case 4: {
                        int length;
                        length = _readable.readUnsignedVarint() - 1;
                        if (length < 0) {
                            throw new RuntimeException("non-nullable field myString was serialized as null");
                        } else if (length > 0x7fff) {
                            throw new RuntimeException("string field myString had invalid length " + length);
                        } else {
                            this.myString = _readable.readString(length);
                        }
                        break;
                    }
                    case 5: {
                        int length;
                        length = _readable.readUnsignedVarint() - 1;
                        if (length < 0) {
                            this.myBytes = null;
                        } else {
                            byte[] newBytes = new byte[length];
                            _readable.readArray(newBytes);
                            this.myBytes = newBytes;
                        }
                        break;
                    }
                    case 6: {
                        this.taggedUuid = _readable.readUUID();
                        break;
                    }
                    case 7: {
                        this.taggedLong = _readable.readLong();
                        break;
                    }
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
            _writable.writeUUID(processId);
        } else {
            if (processId != MessageUtil.ZERO_UUID) {
                throw new UnsupportedVersionException("Attempted to write a non-default processId at version " + _version);
            }
        }
        if (_version >= 1) {
            if (!myTaggedIntArray.isEmpty()) {
                _numTaggedFields++;
            }
        } else {
            if (!myTaggedIntArray.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default myTaggedIntArray at version " + _version);
            }
        }
        if (_version >= 1) {
            if (myNullableString != null) {
                _numTaggedFields++;
            }
        } else {
            if (myNullableString != null) {
                throw new UnsupportedVersionException("Attempted to write a non-default myNullableString at version " + _version);
            }
        }
        if (_version >= 1) {
            if (myInt16 != (short) 123) {
                _numTaggedFields++;
            }
        } else {
            if (myInt16 != (short) 123) {
                throw new UnsupportedVersionException("Attempted to write a non-default myInt16 at version " + _version);
            }
        }
        if (_version >= 1) {
            if (myFloat64 != Double.parseDouble("12.34")) {
                _numTaggedFields++;
            }
        } else {
            if (myFloat64 != Double.parseDouble("12.34")) {
                throw new UnsupportedVersionException("Attempted to write a non-default myFloat64 at version " + _version);
            }
        }
        if (_version >= 1) {
            if (!myString.equals("")) {
                _numTaggedFields++;
            }
        } else {
            if (!myString.equals("")) {
                throw new UnsupportedVersionException("Attempted to write a non-default myString at version " + _version);
            }
        }
        if (_version >= 1) {
            if (myBytes == null || myBytes.length != 0) {
                _numTaggedFields++;
            }
        } else {
            if (myBytes == null || myBytes.length != 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default myBytes at version " + _version);
            }
        }
        if (_version >= 1) {
            if (taggedUuid != UUID.fromString("212d5494-4a8b-4fdf-94b3-88b470beb367")) {
                _numTaggedFields++;
            }
        } else {
            if (taggedUuid != UUID.fromString("212d5494-4a8b-4fdf-94b3-88b470beb367")) {
                throw new UnsupportedVersionException("Attempted to write a non-default taggedUuid at version " + _version);
            }
        }
        if (_version >= 1) {
            if (taggedLong != 0xcafcacafcacafcaL) {
                _numTaggedFields++;
            }
        } else {
            if (taggedLong != 0xcafcacafcacafcaL) {
                throw new UnsupportedVersionException("Attempted to write a non-default taggedLong at version " + _version);
            }
        }
        if (_version >= 1) {
            _writable.writeUnsignedVarint(zeroCopyByteBuffer.remaining() + 1);
            _writable.writeByteBuffer(zeroCopyByteBuffer);
        } else {
            if (zeroCopyByteBuffer.hasRemaining()) {
                throw new UnsupportedVersionException("Attempted to write a non-default zeroCopyByteBuffer at version " + _version);
            }
        }
        if (_version >= 1) {
            if (nullableZeroCopyByteBuffer == null) {
                _writable.writeUnsignedVarint(0);
            } else {
                _writable.writeUnsignedVarint(nullableZeroCopyByteBuffer.remaining() + 1);
                _writable.writeByteBuffer(nullableZeroCopyByteBuffer);
            }
        } else {
            if (nullableZeroCopyByteBuffer == null || nullableZeroCopyByteBuffer.remaining() > 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default nullableZeroCopyByteBuffer at version " + _version);
            }
        }
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        if (_version >= 1) {
            _writable.writeUnsignedVarint(_numTaggedFields);
            {
                if (!myTaggedIntArray.isEmpty()) {
                    _writable.writeUnsignedVarint(0);
                    _writable.writeUnsignedVarint(_cache.getArraySizeInBytes(this.myTaggedIntArray));
                    _writable.writeUnsignedVarint(myTaggedIntArray.size() + 1);
                    for (Integer myTaggedIntArrayElement : myTaggedIntArray) {
                        _writable.writeInt(myTaggedIntArrayElement);
                    }
                }
            }
            if (myNullableString != null) {
                _writable.writeUnsignedVarint(1);
                byte[] _stringBytes = _cache.getSerializedValue(this.myNullableString);
                _writable.writeUnsignedVarint(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
            {
                if (myInt16 != (short) 123) {
                    _writable.writeUnsignedVarint(2);
                    _writable.writeUnsignedVarint(2);
                    _writable.writeShort(myInt16);
                }
            }
            {
                if (myFloat64 != Double.parseDouble("12.34")) {
                    _writable.writeUnsignedVarint(3);
                    _writable.writeUnsignedVarint(8);
                    _writable.writeDouble(myFloat64);
                }
            }
            {
                if (!myString.equals("")) {
                    _writable.writeUnsignedVarint(4);
                    byte[] _stringBytes = _cache.getSerializedValue(this.myString);
                    _writable.writeUnsignedVarint(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                    _writable.writeByteArray(_stringBytes);
                }
            }
            if (myBytes == null) {
                _writable.writeUnsignedVarint(5);
                _writable.writeUnsignedVarint(1);
                _writable.writeUnsignedVarint(0);
            } else {
                if (myBytes.length != 0) {
                    _writable.writeUnsignedVarint(5);
                    _writable.writeUnsignedVarint(this.myBytes.length + ByteUtils.sizeOfUnsignedVarint(this.myBytes.length + 1));
                    _writable.writeUnsignedVarint(this.myBytes.length + 1);
                    _writable.writeByteArray(this.myBytes);
                }
            }
            {
                if (taggedUuid != UUID.fromString("212d5494-4a8b-4fdf-94b3-88b470beb367")) {
                    _writable.writeUnsignedVarint(6);
                    _writable.writeUnsignedVarint(16);
                    _writable.writeUUID(taggedUuid);
                }
            }
            {
                if (taggedLong != 0xcafcacafcacafcaL) {
                    _writable.writeUnsignedVarint(7);
                    _writable.writeUnsignedVarint(8);
                    _writable.writeLong(taggedLong);
                }
            }
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
        if (_version >= 1) {
            _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
        }
        if (_version >= 1) {
            this.processId = struct.getUUID("process_id");
        } else {
            this.processId = MessageUtil.ZERO_UUID;
        }
        if (_version >= 1) {
            if (_taggedFields.containsKey(0)) {
                Object[] _nestedObjects = (Object[]) _taggedFields.remove(0);
                this.myTaggedIntArray = new ArrayList<Integer>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.myTaggedIntArray.add((Integer) nestedObject);
                }
            } else {
                this.myTaggedIntArray = new ArrayList<Integer>();
            }
        } else {
            this.myTaggedIntArray = new ArrayList<Integer>();
        }
        if (_version >= 1) {
            if (_taggedFields.containsKey(1)) {
                this.myNullableString = (String) _taggedFields.remove(1);
            } else {
                this.myNullableString = null;
            }
        } else {
            this.myNullableString = null;
        }
        if (_version >= 1) {
            if (_taggedFields.containsKey(2)) {
                this.myInt16 = (Short) _taggedFields.remove(2);
            } else {
                this.myInt16 = (short) 123;
            }
        } else {
            this.myInt16 = (short) 123;
        }
        if (_version >= 1) {
            if (_taggedFields.containsKey(3)) {
                this.myFloat64 = (Double) _taggedFields.remove(3);
            } else {
                this.myFloat64 = Double.parseDouble("12.34");
            }
        } else {
            this.myFloat64 = Double.parseDouble("12.34");
        }
        if (_version >= 1) {
            if (_taggedFields.containsKey(4)) {
                this.myString = (String) _taggedFields.remove(4);
            } else {
                this.myString = "";
            }
        } else {
            this.myString = "";
        }
        if (_version >= 1) {
            if (_taggedFields.containsKey(5)) {
                this.myBytes = MessageUtil.byteBufferToArray((ByteBuffer) _taggedFields.remove(5));
            } else {
                this.myBytes = Bytes.EMPTY;
            }
        } else {
            this.myBytes = Bytes.EMPTY;
        }
        if (_version >= 1) {
            if (_taggedFields.containsKey(6)) {
                this.taggedUuid = (UUID) _taggedFields.remove(6);
            } else {
                this.taggedUuid = UUID.fromString("212d5494-4a8b-4fdf-94b3-88b470beb367");
            }
        } else {
            this.taggedUuid = UUID.fromString("212d5494-4a8b-4fdf-94b3-88b470beb367");
        }
        if (_version >= 1) {
            if (_taggedFields.containsKey(7)) {
                this.taggedLong = (Long) _taggedFields.remove(7);
            } else {
                this.taggedLong = 0xcafcacafcacafcaL;
            }
        } else {
            this.taggedLong = 0xcafcacafcacafcaL;
        }
        if (_version >= 1) {
            this.zeroCopyByteBuffer = struct.getBytes("zero_copy_byte_buffer");
        } else {
            this.zeroCopyByteBuffer = ByteUtils.EMPTY_BUF;
        }
        if (_version >= 1) {
            this.nullableZeroCopyByteBuffer = struct.getBytes("nullable_zero_copy_byte_buffer");
        } else {
            this.nullableZeroCopyByteBuffer = ByteUtils.EMPTY_BUF;
        }
        if (_version >= 1) {
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
        if (_version >= 1) {
            _taggedFields = new TreeMap<>();
        }
        Struct struct = new Struct(SCHEMAS[_version]);
        if (_version >= 1) {
            struct.set("process_id", this.processId);
        } else {
            if (processId != MessageUtil.ZERO_UUID) {
                throw new UnsupportedVersionException("Attempted to write a non-default processId at version " + _version);
            }
        }
        if (_version >= 1) {
            if (!myTaggedIntArray.isEmpty()) {
                Integer[] _nestedObjects = new Integer[myTaggedIntArray.size()];
                int i = 0;
                for (Integer element : this.myTaggedIntArray) {
                    _nestedObjects[i++] = element;
                }
                _taggedFields.put(0, _nestedObjects);
            }
        } else {
            if (!myTaggedIntArray.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default myTaggedIntArray at version " + _version);
            }
        }
        if (_version >= 1) {
            if (myNullableString != null) {
                _taggedFields.put(1, myNullableString);
            }
        } else {
            if (myNullableString != null) {
                throw new UnsupportedVersionException("Attempted to write a non-default myNullableString at version " + _version);
            }
        }
        if (_version >= 1) {
            if (myInt16 != (short) 123) {
                _taggedFields.put(2, myInt16);
            }
        } else {
            if (myInt16 != (short) 123) {
                throw new UnsupportedVersionException("Attempted to write a non-default myInt16 at version " + _version);
            }
        }
        if (_version >= 1) {
            if (myFloat64 != Double.parseDouble("12.34")) {
                _taggedFields.put(3, myFloat64);
            }
        } else {
            if (myFloat64 != Double.parseDouble("12.34")) {
                throw new UnsupportedVersionException("Attempted to write a non-default myFloat64 at version " + _version);
            }
        }
        if (_version >= 1) {
            if (!myString.equals("")) {
                _taggedFields.put(4, myString);
            }
        } else {
            if (!myString.equals("")) {
                throw new UnsupportedVersionException("Attempted to write a non-default myString at version " + _version);
            }
        }
        if (_version >= 1) {
            if (myBytes == null || myBytes.length != 0) {
                _taggedFields.put(5, (myBytes == null) ? null : ByteBuffer.wrap(myBytes));
            }
        } else {
            if (myBytes == null || myBytes.length != 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default myBytes at version " + _version);
            }
        }
        if (_version >= 1) {
            if (taggedUuid != UUID.fromString("212d5494-4a8b-4fdf-94b3-88b470beb367")) {
                _taggedFields.put(6, taggedUuid);
            }
        } else {
            if (taggedUuid != UUID.fromString("212d5494-4a8b-4fdf-94b3-88b470beb367")) {
                throw new UnsupportedVersionException("Attempted to write a non-default taggedUuid at version " + _version);
            }
        }
        if (_version >= 1) {
            if (taggedLong != 0xcafcacafcacafcaL) {
                _taggedFields.put(7, taggedLong);
            }
        } else {
            if (taggedLong != 0xcafcacafcacafcaL) {
                throw new UnsupportedVersionException("Attempted to write a non-default taggedLong at version " + _version);
            }
        }
        if (_version >= 1) {
            struct.set("zero_copy_byte_buffer", this.zeroCopyByteBuffer);
        } else {
            if (zeroCopyByteBuffer.hasRemaining()) {
                throw new UnsupportedVersionException("Attempted to write a non-default zeroCopyByteBuffer at version " + _version);
            }
        }
        if (_version >= 1) {
            struct.set("nullable_zero_copy_byte_buffer", this.nullableZeroCopyByteBuffer);
        } else {
            if (nullableZeroCopyByteBuffer == null || nullableZeroCopyByteBuffer.remaining() > 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default nullableZeroCopyByteBuffer at version " + _version);
            }
        }
        if (_version >= 1) {
            struct.set("_tagged_fields", _taggedFields);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        if (_version >= 1) {
            _size += 16;
        }
        if (_version >= 1) {
            {
                if (!myTaggedIntArray.isEmpty()) {
                    _numTaggedFields++;
                    _size += 1;
                    int _arraySize = 0;
                    _arraySize += ByteUtils.sizeOfUnsignedVarint(myTaggedIntArray.size() + 1);
                    _arraySize += myTaggedIntArray.size() * 4;
                    _cache.setArraySizeInBytes(myTaggedIntArray, _arraySize);
                    _size += _arraySize + ByteUtils.sizeOfUnsignedVarint(_arraySize);
                }
            }
        }
        if (_version >= 1) {
            if (myNullableString == null) {
            } else {
                _numTaggedFields++;
                _size += 1;
                byte[] _stringBytes = myNullableString.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'myNullableString' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(myNullableString, _stringBytes);
                int _stringPrefixSize = ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                _size += _stringBytes.length + _stringPrefixSize + ByteUtils.sizeOfUnsignedVarint(_stringPrefixSize);
            }
        }
        if (_version >= 1) {
            if (myInt16 != (short) 123) {
                _numTaggedFields++;
                _size += 1;
                _size += 1;
                _size += 2;
            }
        }
        if (_version >= 1) {
            if (myFloat64 != Double.parseDouble("12.34")) {
                _numTaggedFields++;
                _size += 1;
                _size += 1;
                _size += 8;
            }
        }
        if (_version >= 1) {
            {
                if (!myString.equals("")) {
                    _numTaggedFields++;
                    _size += 1;
                    byte[] _stringBytes = myString.getBytes(StandardCharsets.UTF_8);
                    if (_stringBytes.length > 0x7fff) {
                        throw new RuntimeException("'myString' field is too long to be serialized");
                    }
                    _cache.cacheSerializedValue(myString, _stringBytes);
                    int _stringPrefixSize = ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                    _size += _stringBytes.length + _stringPrefixSize + ByteUtils.sizeOfUnsignedVarint(_stringPrefixSize);
                }
            }
        }
        if (_version >= 1) {
            if (myBytes == null) {
                _numTaggedFields++;
                _size += 1;
                _size += 1;
                _size += 1;
            } else {
                if (myBytes.length != 0) {
                    _numTaggedFields++;
                    _size += 1;
                    int _bytesSize = myBytes.length;
                    _bytesSize += ByteUtils.sizeOfUnsignedVarint(myBytes.length + 1);
                    _size += _bytesSize + ByteUtils.sizeOfUnsignedVarint(_bytesSize);
                }
            }
        }
        if (_version >= 1) {
            if (taggedUuid != UUID.fromString("212d5494-4a8b-4fdf-94b3-88b470beb367")) {
                _numTaggedFields++;
                _size += 1;
                _size += 1;
                _size += 16;
            }
        }
        if (_version >= 1) {
            if (taggedLong != 0xcafcacafcacafcaL) {
                _numTaggedFields++;
                _size += 1;
                _size += 1;
                _size += 8;
            }
        }
        if (_version >= 1) {
            {
                int _bytesSize = zeroCopyByteBuffer.remaining();
                _bytesSize += ByteUtils.sizeOfUnsignedVarint(zeroCopyByteBuffer.remaining() + 1);
                _size += _bytesSize;
            }
        }
        if (_version >= 1) {
            if (nullableZeroCopyByteBuffer == null) {
                _size += 1;
            } else {
                int _bytesSize = nullableZeroCopyByteBuffer.remaining();
                _bytesSize += ByteUtils.sizeOfUnsignedVarint(nullableZeroCopyByteBuffer.remaining() + 1);
                _size += _bytesSize;
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
        if (_version >= 1) {
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
        if (!(obj instanceof SimpleExampleMessageData)) return false;
        SimpleExampleMessageData other = (SimpleExampleMessageData) obj;
        if (!this.processId.equals(other.processId)) return false;
        if (this.myTaggedIntArray == null) {
            if (other.myTaggedIntArray != null) return false;
        } else {
            if (!this.myTaggedIntArray.equals(other.myTaggedIntArray)) return false;
        }
        if (this.myNullableString == null) {
            if (other.myNullableString != null) return false;
        } else {
            if (!this.myNullableString.equals(other.myNullableString)) return false;
        }
        if (myInt16 != other.myInt16) return false;
        if (myFloat64 != other.myFloat64) return false;
        if (this.myString == null) {
            if (other.myString != null) return false;
        } else {
            if (!this.myString.equals(other.myString)) return false;
        }
        if (!Arrays.equals(this.myBytes, other.myBytes)) return false;
        if (!this.taggedUuid.equals(other.taggedUuid)) return false;
        if (taggedLong != other.taggedLong) return false;
        if (!Objects.equals(this.zeroCopyByteBuffer, other.zeroCopyByteBuffer)) return false;
        if (!Objects.equals(this.nullableZeroCopyByteBuffer, other.nullableZeroCopyByteBuffer)) return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + processId.hashCode();
        hashCode = 31 * hashCode + (myTaggedIntArray == null ? 0 : myTaggedIntArray.hashCode());
        hashCode = 31 * hashCode + (myNullableString == null ? 0 : myNullableString.hashCode());
        hashCode = 31 * hashCode + myInt16;
        hashCode = 31 * hashCode + Double.hashCode(myFloat64);
        hashCode = 31 * hashCode + (myString == null ? 0 : myString.hashCode());
        hashCode = 31 * hashCode + Arrays.hashCode(myBytes);
        hashCode = 31 * hashCode + taggedUuid.hashCode();
        hashCode = 31 * hashCode + ((int) (taggedLong >> 32) ^ (int) taggedLong);
        hashCode = 31 * hashCode + Objects.hashCode(zeroCopyByteBuffer);
        hashCode = 31 * hashCode + Objects.hashCode(nullableZeroCopyByteBuffer);
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "SimpleExampleMessageData("
            + ", myTaggedIntArray=" + MessageUtil.deepToString(myTaggedIntArray.iterator())
            + ", myNullableString=" + ((myNullableString == null) ? "null" : "'" + myNullableString.toString() + "'")
            + ", myInt16=" + myInt16
            + ", myFloat64=" + myFloat64
            + ", myString=" + ((myString == null) ? "null" : "'" + myString.toString() + "'")
            + ", myBytes=" + Arrays.toString(myBytes)
            + ", taggedLong=" + taggedLong
            + ", zeroCopyByteBuffer=" + zeroCopyByteBuffer
            + ", nullableZeroCopyByteBuffer=" + nullableZeroCopyByteBuffer
            + ")";
    }
    
    public UUID processId() {
        return this.processId;
    }
    
    public List<Integer> myTaggedIntArray() {
        return this.myTaggedIntArray;
    }
    
    public String myNullableString() {
        return this.myNullableString;
    }
    
    public short myInt16() {
        return this.myInt16;
    }
    
    public double myFloat64() {
        return this.myFloat64;
    }
    
    public String myString() {
        return this.myString;
    }
    
    public byte[] myBytes() {
        return this.myBytes;
    }
    
    public UUID taggedUuid() {
        return this.taggedUuid;
    }
    
    public long taggedLong() {
        return this.taggedLong;
    }
    
    public ByteBuffer zeroCopyByteBuffer() {
        return this.zeroCopyByteBuffer;
    }
    
    public ByteBuffer nullableZeroCopyByteBuffer() {
        return this.nullableZeroCopyByteBuffer;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public SimpleExampleMessageData setProcessId(UUID v) {
        this.processId = v;
        return this;
    }
    
    public SimpleExampleMessageData setMyTaggedIntArray(List<Integer> v) {
        this.myTaggedIntArray = v;
        return this;
    }
    
    public SimpleExampleMessageData setMyNullableString(String v) {
        this.myNullableString = v;
        return this;
    }
    
    public SimpleExampleMessageData setMyInt16(short v) {
        this.myInt16 = v;
        return this;
    }
    
    public SimpleExampleMessageData setMyFloat64(double v) {
        this.myFloat64 = v;
        return this;
    }
    
    public SimpleExampleMessageData setMyString(String v) {
        this.myString = v;
        return this;
    }
    
    public SimpleExampleMessageData setMyBytes(byte[] v) {
        this.myBytes = v;
        return this;
    }
    
    public SimpleExampleMessageData setTaggedUuid(UUID v) {
        this.taggedUuid = v;
        return this;
    }
    
    public SimpleExampleMessageData setTaggedLong(long v) {
        this.taggedLong = v;
        return this;
    }
    
    public SimpleExampleMessageData setZeroCopyByteBuffer(ByteBuffer v) {
        this.zeroCopyByteBuffer = v;
        return this;
    }
    
    public SimpleExampleMessageData setNullableZeroCopyByteBuffer(ByteBuffer v) {
        this.nullableZeroCopyByteBuffer = v;
        return this;
    }
}
