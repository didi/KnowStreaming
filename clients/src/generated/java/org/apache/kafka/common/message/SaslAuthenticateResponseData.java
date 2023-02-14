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
import java.util.Arrays;
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
import org.apache.kafka.common.utils.Bytes;

import static java.util.Map.Entry;
import static org.apache.kafka.common.protocol.types.Field.TaggedFieldsSection;


public class SaslAuthenticateResponseData implements ApiMessage {
    private short errorCode;
    private String errorMessage;
    private byte[] authBytes;
    private long sessionLifetimeMs;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
            new Field("error_message", Type.NULLABLE_STRING, "The error message, or null if there was no error."),
            new Field("auth_bytes", Type.BYTES, "The SASL authentication bytes from the server, as defined by the SASL mechanism.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
            new Field("error_message", Type.NULLABLE_STRING, "The error message, or null if there was no error."),
            new Field("auth_bytes", Type.BYTES, "The SASL authentication bytes from the server, as defined by the SASL mechanism."),
            new Field("session_lifetime_ms", Type.INT64, "The SASL authentication bytes from the server, as defined by the SASL mechanism.")
        );
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
            new Field("error_message", Type.COMPACT_NULLABLE_STRING, "The error message, or null if there was no error."),
            new Field("auth_bytes", Type.COMPACT_BYTES, "The SASL authentication bytes from the server, as defined by the SASL mechanism."),
            new Field("session_lifetime_ms", Type.INT64, "The SASL authentication bytes from the server, as defined by the SASL mechanism."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2
    };
    
    public SaslAuthenticateResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public SaslAuthenticateResponseData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public SaslAuthenticateResponseData() {
        this.errorCode = (short) 0;
        this.errorMessage = "";
        this.authBytes = Bytes.EMPTY;
        this.sessionLifetimeMs = 0L;
    }
    
    @Override
    public short apiKey() {
        return 36;
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
            int length;
            if (_version >= 2) {
                length = _readable.readUnsignedVarint() - 1;
            } else {
                length = _readable.readInt();
            }
            if (length < 0) {
                throw new RuntimeException("non-nullable field authBytes was serialized as null");
            } else {
                byte[] newBytes = new byte[length];
                _readable.readArray(newBytes);
                this.authBytes = newBytes;
            }
        }
        if (_version >= 1) {
            this.sessionLifetimeMs = _readable.readLong();
        } else {
            this.sessionLifetimeMs = 0L;
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
            _writable.writeUnsignedVarint(authBytes.length + 1);
        } else {
            _writable.writeInt(authBytes.length);
        }
        _writable.writeByteArray(authBytes);
        if (_version >= 1) {
            _writable.writeLong(sessionLifetimeMs);
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
        this.errorCode = struct.getShort("error_code");
        this.errorMessage = struct.getString("error_message");
        this.authBytes = struct.getByteArray("auth_bytes");
        if (_version >= 1) {
            this.sessionLifetimeMs = struct.getLong("session_lifetime_ms");
        } else {
            this.sessionLifetimeMs = 0L;
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
        struct.set("error_code", this.errorCode);
        struct.set("error_message", this.errorMessage);
        struct.setByteArray("auth_bytes", this.authBytes);
        if (_version >= 1) {
            struct.set("session_lifetime_ms", this.sessionLifetimeMs);
        }
        if (_version >= 2) {
            struct.set("_tagged_fields", _taggedFields);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
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
            int _bytesSize = authBytes.length;
            if (_version >= 2) {
                _bytesSize += ByteUtils.sizeOfUnsignedVarint(authBytes.length + 1);
            } else {
                _bytesSize += 4;
            }
            _size += _bytesSize;
        }
        if (_version >= 1) {
            _size += 8;
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
        if (!(obj instanceof SaslAuthenticateResponseData)) return false;
        SaslAuthenticateResponseData other = (SaslAuthenticateResponseData) obj;
        if (errorCode != other.errorCode) return false;
        if (this.errorMessage == null) {
            if (other.errorMessage != null) return false;
        } else {
            if (!this.errorMessage.equals(other.errorMessage)) return false;
        }
        if (!Arrays.equals(this.authBytes, other.authBytes)) return false;
        if (sessionLifetimeMs != other.sessionLifetimeMs) return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + errorCode;
        hashCode = 31 * hashCode + (errorMessage == null ? 0 : errorMessage.hashCode());
        hashCode = 31 * hashCode + Arrays.hashCode(authBytes);
        hashCode = 31 * hashCode + ((int) (sessionLifetimeMs >> 32) ^ (int) sessionLifetimeMs);
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "SaslAuthenticateResponseData("
            + "errorCode=" + errorCode
            + ", errorMessage=" + ((errorMessage == null) ? "null" : "'" + errorMessage.toString() + "'")
            + ", authBytes=" + Arrays.toString(authBytes)
            + ", sessionLifetimeMs=" + sessionLifetimeMs
            + ")";
    }
    
    public short errorCode() {
        return this.errorCode;
    }
    
    public String errorMessage() {
        return this.errorMessage;
    }
    
    public byte[] authBytes() {
        return this.authBytes;
    }
    
    public long sessionLifetimeMs() {
        return this.sessionLifetimeMs;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public SaslAuthenticateResponseData setErrorCode(short v) {
        this.errorCode = v;
        return this;
    }
    
    public SaslAuthenticateResponseData setErrorMessage(String v) {
        this.errorMessage = v;
        return this;
    }
    
    public SaslAuthenticateResponseData setAuthBytes(byte[] v) {
        this.authBytes = v;
        return this;
    }
    
    public SaslAuthenticateResponseData setSessionLifetimeMs(long v) {
        this.sessionLifetimeMs = v;
        return this;
    }
}
