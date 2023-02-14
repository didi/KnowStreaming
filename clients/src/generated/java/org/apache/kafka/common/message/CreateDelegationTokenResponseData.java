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


public class CreateDelegationTokenResponseData implements ApiMessage {
    private short errorCode;
    private String principalType;
    private String principalName;
    private long issueTimestampMs;
    private long expiryTimestampMs;
    private long maxTimestampMs;
    private String tokenId;
    private byte[] hmac;
    private int throttleTimeMs;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("error_code", Type.INT16, "The top-level error, or zero if there was no error."),
            new Field("principal_type", Type.STRING, "The principal type of the token owner."),
            new Field("principal_name", Type.STRING, "The name of the token owner."),
            new Field("issue_timestamp_ms", Type.INT64, "When this token was generated."),
            new Field("expiry_timestamp_ms", Type.INT64, "When this token expires."),
            new Field("max_timestamp_ms", Type.INT64, "The maximum lifetime of this token."),
            new Field("token_id", Type.STRING, "The token UUID."),
            new Field("hmac", Type.BYTES, "HMAC of the delegation token."),
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota.")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("error_code", Type.INT16, "The top-level error, or zero if there was no error."),
            new Field("principal_type", Type.COMPACT_STRING, "The principal type of the token owner."),
            new Field("principal_name", Type.COMPACT_STRING, "The name of the token owner."),
            new Field("issue_timestamp_ms", Type.INT64, "When this token was generated."),
            new Field("expiry_timestamp_ms", Type.INT64, "When this token expires."),
            new Field("max_timestamp_ms", Type.INT64, "The maximum lifetime of this token."),
            new Field("token_id", Type.COMPACT_STRING, "The token UUID."),
            new Field("hmac", Type.COMPACT_BYTES, "HMAC of the delegation token."),
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2
    };
    
    public CreateDelegationTokenResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public CreateDelegationTokenResponseData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public CreateDelegationTokenResponseData() {
        this.errorCode = (short) 0;
        this.principalType = "";
        this.principalName = "";
        this.issueTimestampMs = 0L;
        this.expiryTimestampMs = 0L;
        this.maxTimestampMs = 0L;
        this.tokenId = "";
        this.hmac = Bytes.EMPTY;
        this.throttleTimeMs = 0;
    }
    
    @Override
    public short apiKey() {
        return 38;
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
        this.issueTimestampMs = _readable.readLong();
        this.expiryTimestampMs = _readable.readLong();
        this.maxTimestampMs = _readable.readLong();
        {
            int length;
            if (_version >= 2) {
                length = _readable.readUnsignedVarint() - 1;
            } else {
                length = _readable.readShort();
            }
            if (length < 0) {
                throw new RuntimeException("non-nullable field tokenId was serialized as null");
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field tokenId had invalid length " + length);
            } else {
                this.tokenId = _readable.readString(length);
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
                throw new RuntimeException("non-nullable field hmac was serialized as null");
            } else {
                byte[] newBytes = new byte[length];
                _readable.readArray(newBytes);
                this.hmac = newBytes;
            }
        }
        this.throttleTimeMs = _readable.readInt();
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
        _writable.writeLong(issueTimestampMs);
        _writable.writeLong(expiryTimestampMs);
        _writable.writeLong(maxTimestampMs);
        {
            byte[] _stringBytes = _cache.getSerializedValue(tokenId);
            if (_version >= 2) {
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
            } else {
                _writable.writeShort((short) _stringBytes.length);
            }
            _writable.writeByteArray(_stringBytes);
        }
        if (_version >= 2) {
            _writable.writeUnsignedVarint(hmac.length + 1);
        } else {
            _writable.writeInt(hmac.length);
        }
        _writable.writeByteArray(hmac);
        _writable.writeInt(throttleTimeMs);
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
        this.principalType = struct.getString("principal_type");
        this.principalName = struct.getString("principal_name");
        this.issueTimestampMs = struct.getLong("issue_timestamp_ms");
        this.expiryTimestampMs = struct.getLong("expiry_timestamp_ms");
        this.maxTimestampMs = struct.getLong("max_timestamp_ms");
        this.tokenId = struct.getString("token_id");
        this.hmac = struct.getByteArray("hmac");
        this.throttleTimeMs = struct.getInt("throttle_time_ms");
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
        struct.set("principal_type", this.principalType);
        struct.set("principal_name", this.principalName);
        struct.set("issue_timestamp_ms", this.issueTimestampMs);
        struct.set("expiry_timestamp_ms", this.expiryTimestampMs);
        struct.set("max_timestamp_ms", this.maxTimestampMs);
        struct.set("token_id", this.tokenId);
        struct.setByteArray("hmac", this.hmac);
        struct.set("throttle_time_ms", this.throttleTimeMs);
        if (_version >= 2) {
            struct.set("_tagged_fields", _taggedFields);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        _size += 2;
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
        _size += 8;
        _size += 8;
        _size += 8;
        {
            byte[] _stringBytes = tokenId.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'tokenId' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(tokenId, _stringBytes);
            if (_version >= 2) {
                _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
            } else {
                _size += _stringBytes.length + 2;
            }
        }
        {
            int _bytesSize = hmac.length;
            if (_version >= 2) {
                _bytesSize += ByteUtils.sizeOfUnsignedVarint(hmac.length + 1);
            } else {
                _bytesSize += 4;
            }
            _size += _bytesSize;
        }
        _size += 4;
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
        if (!(obj instanceof CreateDelegationTokenResponseData)) return false;
        CreateDelegationTokenResponseData other = (CreateDelegationTokenResponseData) obj;
        if (errorCode != other.errorCode) return false;
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
        if (issueTimestampMs != other.issueTimestampMs) return false;
        if (expiryTimestampMs != other.expiryTimestampMs) return false;
        if (maxTimestampMs != other.maxTimestampMs) return false;
        if (this.tokenId == null) {
            if (other.tokenId != null) return false;
        } else {
            if (!this.tokenId.equals(other.tokenId)) return false;
        }
        if (!Arrays.equals(this.hmac, other.hmac)) return false;
        if (throttleTimeMs != other.throttleTimeMs) return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + errorCode;
        hashCode = 31 * hashCode + (principalType == null ? 0 : principalType.hashCode());
        hashCode = 31 * hashCode + (principalName == null ? 0 : principalName.hashCode());
        hashCode = 31 * hashCode + ((int) (issueTimestampMs >> 32) ^ (int) issueTimestampMs);
        hashCode = 31 * hashCode + ((int) (expiryTimestampMs >> 32) ^ (int) expiryTimestampMs);
        hashCode = 31 * hashCode + ((int) (maxTimestampMs >> 32) ^ (int) maxTimestampMs);
        hashCode = 31 * hashCode + (tokenId == null ? 0 : tokenId.hashCode());
        hashCode = 31 * hashCode + Arrays.hashCode(hmac);
        hashCode = 31 * hashCode + throttleTimeMs;
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "CreateDelegationTokenResponseData("
            + "errorCode=" + errorCode
            + ", principalType=" + ((principalType == null) ? "null" : "'" + principalType.toString() + "'")
            + ", principalName=" + ((principalName == null) ? "null" : "'" + principalName.toString() + "'")
            + ", issueTimestampMs=" + issueTimestampMs
            + ", expiryTimestampMs=" + expiryTimestampMs
            + ", maxTimestampMs=" + maxTimestampMs
            + ", tokenId=" + ((tokenId == null) ? "null" : "'" + tokenId.toString() + "'")
            + ", hmac=" + Arrays.toString(hmac)
            + ", throttleTimeMs=" + throttleTimeMs
            + ")";
    }
    
    public short errorCode() {
        return this.errorCode;
    }
    
    public String principalType() {
        return this.principalType;
    }
    
    public String principalName() {
        return this.principalName;
    }
    
    public long issueTimestampMs() {
        return this.issueTimestampMs;
    }
    
    public long expiryTimestampMs() {
        return this.expiryTimestampMs;
    }
    
    public long maxTimestampMs() {
        return this.maxTimestampMs;
    }
    
    public String tokenId() {
        return this.tokenId;
    }
    
    public byte[] hmac() {
        return this.hmac;
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
    
    public CreateDelegationTokenResponseData setErrorCode(short v) {
        this.errorCode = v;
        return this;
    }
    
    public CreateDelegationTokenResponseData setPrincipalType(String v) {
        this.principalType = v;
        return this;
    }
    
    public CreateDelegationTokenResponseData setPrincipalName(String v) {
        this.principalName = v;
        return this;
    }
    
    public CreateDelegationTokenResponseData setIssueTimestampMs(long v) {
        this.issueTimestampMs = v;
        return this;
    }
    
    public CreateDelegationTokenResponseData setExpiryTimestampMs(long v) {
        this.expiryTimestampMs = v;
        return this;
    }
    
    public CreateDelegationTokenResponseData setMaxTimestampMs(long v) {
        this.maxTimestampMs = v;
        return this;
    }
    
    public CreateDelegationTokenResponseData setTokenId(String v) {
        this.tokenId = v;
        return this;
    }
    
    public CreateDelegationTokenResponseData setHmac(byte[] v) {
        this.hmac = v;
        return this;
    }
    
    public CreateDelegationTokenResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
}
