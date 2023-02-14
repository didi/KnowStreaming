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


public class LeaveGroupResponseData implements ApiMessage {
    private int throttleTimeMs;
    private short errorCode;
    private List<MemberResponse> members;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error.")
        );
    
    public static final Schema SCHEMA_2 = SCHEMA_1;
    
    public static final Schema SCHEMA_3 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
            new Field("members", new ArrayOf(MemberResponse.SCHEMA_3), "List of leaving member responses.")
        );
    
    public static final Schema SCHEMA_4 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
            new Field("members", new CompactArrayOf(MemberResponse.SCHEMA_4), "List of leaving member responses."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2,
        SCHEMA_3,
        SCHEMA_4
    };
    
    public LeaveGroupResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public LeaveGroupResponseData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public LeaveGroupResponseData() {
        this.throttleTimeMs = 0;
        this.errorCode = (short) 0;
        this.members = new ArrayList<MemberResponse>();
    }
    
    @Override
    public short apiKey() {
        return 13;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 4;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        if (_version >= 1) {
            this.throttleTimeMs = _readable.readInt();
        } else {
            this.throttleTimeMs = 0;
        }
        this.errorCode = _readable.readShort();
        if (_version >= 3) {
            if (_version >= 4) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field members was serialized as null");
                } else {
                    ArrayList<MemberResponse> newCollection = new ArrayList<MemberResponse>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new MemberResponse(_readable, _version));
                    }
                    this.members = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field members was serialized as null");
                } else {
                    ArrayList<MemberResponse> newCollection = new ArrayList<MemberResponse>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new MemberResponse(_readable, _version));
                    }
                    this.members = newCollection;
                }
            }
        } else {
            this.members = new ArrayList<MemberResponse>();
        }
        this._unknownTaggedFields = null;
        if (_version >= 4) {
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
        if (_version >= 1) {
            _writable.writeInt(throttleTimeMs);
        }
        _writable.writeShort(errorCode);
        if (_version >= 3) {
            if (_version >= 4) {
                _writable.writeUnsignedVarint(members.size() + 1);
                for (MemberResponse membersElement : members) {
                    membersElement.write(_writable, _cache, _version);
                }
            } else {
                _writable.writeInt(members.size());
                for (MemberResponse membersElement : members) {
                    membersElement.write(_writable, _cache, _version);
                }
            }
        } else {
            if (!members.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default members at version " + _version);
            }
        }
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        if (_version >= 4) {
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
        if (_version >= 4) {
            _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
        }
        if (_version >= 1) {
            this.throttleTimeMs = struct.getInt("throttle_time_ms");
        } else {
            this.throttleTimeMs = 0;
        }
        this.errorCode = struct.getShort("error_code");
        if (_version >= 3) {
            Object[] _nestedObjects = struct.getArray("members");
            this.members = new ArrayList<MemberResponse>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.members.add(new MemberResponse((Struct) nestedObject, _version));
            }
        } else {
            this.members = new ArrayList<MemberResponse>();
        }
        if (_version >= 4) {
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
        if (_version >= 4) {
            _taggedFields = new TreeMap<>();
        }
        Struct struct = new Struct(SCHEMAS[_version]);
        if (_version >= 1) {
            struct.set("throttle_time_ms", this.throttleTimeMs);
        }
        struct.set("error_code", this.errorCode);
        if (_version >= 3) {
            Struct[] _nestedObjects = new Struct[members.size()];
            int i = 0;
            for (MemberResponse element : this.members) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("members", (Object[]) _nestedObjects);
        } else {
            if (!members.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default members at version " + _version);
            }
        }
        if (_version >= 4) {
            struct.set("_tagged_fields", _taggedFields);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        if (_version >= 1) {
            _size += 4;
        }
        _size += 2;
        if (_version >= 3) {
            {
                int _arraySize = 0;
                if (_version >= 4) {
                    _arraySize += ByteUtils.sizeOfUnsignedVarint(members.size() + 1);
                } else {
                    _arraySize += 4;
                }
                for (MemberResponse membersElement : members) {
                    _arraySize += membersElement.size(_cache, _version);
                }
                _size += _arraySize;
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
        if (_version >= 4) {
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
        if (!(obj instanceof LeaveGroupResponseData)) return false;
        LeaveGroupResponseData other = (LeaveGroupResponseData) obj;
        if (throttleTimeMs != other.throttleTimeMs) return false;
        if (errorCode != other.errorCode) return false;
        if (this.members == null) {
            if (other.members != null) return false;
        } else {
            if (!this.members.equals(other.members)) return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + throttleTimeMs;
        hashCode = 31 * hashCode + errorCode;
        hashCode = 31 * hashCode + (members == null ? 0 : members.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "LeaveGroupResponseData("
            + "throttleTimeMs=" + throttleTimeMs
            + ", errorCode=" + errorCode
            + ", members=" + MessageUtil.deepToString(members.iterator())
            + ")";
    }
    
    public int throttleTimeMs() {
        return this.throttleTimeMs;
    }
    
    public short errorCode() {
        return this.errorCode;
    }
    
    public List<MemberResponse> members() {
        return this.members;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public LeaveGroupResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
    
    public LeaveGroupResponseData setErrorCode(short v) {
        this.errorCode = v;
        return this;
    }
    
    public LeaveGroupResponseData setMembers(List<MemberResponse> v) {
        this.members = v;
        return this;
    }
    
    static public class MemberResponse implements Message {
        private String memberId;
        private String groupInstanceId;
        private short errorCode;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_3 =
            new Schema(
                new Field("member_id", Type.STRING, "The member ID to remove from the group."),
                new Field("group_instance_id", Type.NULLABLE_STRING, "The group instance ID to remove from the group."),
                new Field("error_code", Type.INT16, "The error code, or 0 if there was no error.")
            );
        
        public static final Schema SCHEMA_4 =
            new Schema(
                new Field("member_id", Type.COMPACT_STRING, "The member ID to remove from the group."),
                new Field("group_instance_id", Type.COMPACT_NULLABLE_STRING, "The group instance ID to remove from the group."),
                new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            null,
            null,
            null,
            SCHEMA_3,
            SCHEMA_4
        };
        
        public MemberResponse(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public MemberResponse(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public MemberResponse() {
            this.memberId = "";
            this.groupInstanceId = "";
            this.errorCode = (short) 0;
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 4;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 4) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of MemberResponse");
            }
            {
                int length;
                if (_version >= 4) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field memberId was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field memberId had invalid length " + length);
                } else {
                    this.memberId = _readable.readString(length);
                }
            }
            {
                int length;
                if (_version >= 4) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    this.groupInstanceId = null;
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field groupInstanceId had invalid length " + length);
                } else {
                    this.groupInstanceId = _readable.readString(length);
                }
            }
            this.errorCode = _readable.readShort();
            this._unknownTaggedFields = null;
            if (_version >= 4) {
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
            if (_version > 4) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of MemberResponse");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(memberId);
                if (_version >= 4) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            if (groupInstanceId == null) {
                if (_version >= 4) {
                    _writable.writeUnsignedVarint(0);
                } else {
                    _writable.writeShort((short) -1);
                }
            } else {
                byte[] _stringBytes = _cache.getSerializedValue(groupInstanceId);
                if (_version >= 4) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeShort(errorCode);
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 4) {
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
            if (_version > 4) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of MemberResponse");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 4) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.memberId = struct.getString("member_id");
            this.groupInstanceId = struct.getString("group_instance_id");
            this.errorCode = struct.getShort("error_code");
            if (_version >= 4) {
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
            if (_version > 4) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of MemberResponse");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 4) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("member_id", this.memberId);
            struct.set("group_instance_id", this.groupInstanceId);
            struct.set("error_code", this.errorCode);
            if (_version >= 4) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 4) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of MemberResponse");
            }
            {
                byte[] _stringBytes = memberId.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'memberId' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(memberId, _stringBytes);
                if (_version >= 4) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
            }
            if (groupInstanceId == null) {
                if (_version >= 4) {
                    _size += 1;
                } else {
                    _size += 2;
                }
            } else {
                byte[] _stringBytes = groupInstanceId.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'groupInstanceId' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(groupInstanceId, _stringBytes);
                if (_version >= 4) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
            }
            _size += 2;
            if (_unknownTaggedFields != null) {
                _numTaggedFields += _unknownTaggedFields.size();
                for (RawTaggedField _field : _unknownTaggedFields) {
                    _size += ByteUtils.sizeOfUnsignedVarint(_field.tag());
                    _size += ByteUtils.sizeOfUnsignedVarint(_field.size());
                    _size += _field.size();
                }
            }
            if (_version >= 4) {
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
            if (!(obj instanceof MemberResponse)) return false;
            MemberResponse other = (MemberResponse) obj;
            if (this.memberId == null) {
                if (other.memberId != null) return false;
            } else {
                if (!this.memberId.equals(other.memberId)) return false;
            }
            if (this.groupInstanceId == null) {
                if (other.groupInstanceId != null) return false;
            } else {
                if (!this.groupInstanceId.equals(other.groupInstanceId)) return false;
            }
            if (errorCode != other.errorCode) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (memberId == null ? 0 : memberId.hashCode());
            hashCode = 31 * hashCode + (groupInstanceId == null ? 0 : groupInstanceId.hashCode());
            hashCode = 31 * hashCode + errorCode;
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "MemberResponse("
                + "memberId=" + ((memberId == null) ? "null" : "'" + memberId.toString() + "'")
                + ", groupInstanceId=" + ((groupInstanceId == null) ? "null" : "'" + groupInstanceId.toString() + "'")
                + ", errorCode=" + errorCode
                + ")";
        }
        
        public String memberId() {
            return this.memberId;
        }
        
        public String groupInstanceId() {
            return this.groupInstanceId;
        }
        
        public short errorCode() {
            return this.errorCode;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public MemberResponse setMemberId(String v) {
            this.memberId = v;
            return this;
        }
        
        public MemberResponse setGroupInstanceId(String v) {
            this.groupInstanceId = v;
            return this;
        }
        
        public MemberResponse setErrorCode(short v) {
            this.errorCode = v;
            return this;
        }
    }
}
