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
import org.apache.kafka.common.utils.Bytes;

import static java.util.Map.Entry;
import static org.apache.kafka.common.protocol.types.Field.TaggedFieldsSection;


public class JoinGroupResponseData implements ApiMessage {
    private int throttleTimeMs;
    private short errorCode;
    private int generationId;
    private String protocolType;
    private String protocolName;
    private String leader;
    private String memberId;
    private List<JoinGroupResponseMember> members;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
            new Field("generation_id", Type.INT32, "The generation ID of the group."),
            new Field("protocol_name", Type.STRING, "The group protocol selected by the coordinator."),
            new Field("leader", Type.STRING, "The leader of the group."),
            new Field("member_id", Type.STRING, "The member ID assigned by the group coordinator."),
            new Field("members", new ArrayOf(JoinGroupResponseMember.SCHEMA_0), "")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
            new Field("generation_id", Type.INT32, "The generation ID of the group."),
            new Field("protocol_name", Type.STRING, "The group protocol selected by the coordinator."),
            new Field("leader", Type.STRING, "The leader of the group."),
            new Field("member_id", Type.STRING, "The member ID assigned by the group coordinator."),
            new Field("members", new ArrayOf(JoinGroupResponseMember.SCHEMA_0), "")
        );
    
    public static final Schema SCHEMA_3 = SCHEMA_2;
    
    public static final Schema SCHEMA_4 = SCHEMA_3;
    
    public static final Schema SCHEMA_5 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
            new Field("generation_id", Type.INT32, "The generation ID of the group."),
            new Field("protocol_name", Type.STRING, "The group protocol selected by the coordinator."),
            new Field("leader", Type.STRING, "The leader of the group."),
            new Field("member_id", Type.STRING, "The member ID assigned by the group coordinator."),
            new Field("members", new ArrayOf(JoinGroupResponseMember.SCHEMA_5), "")
        );
    
    public static final Schema SCHEMA_6 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
            new Field("generation_id", Type.INT32, "The generation ID of the group."),
            new Field("protocol_name", Type.COMPACT_STRING, "The group protocol selected by the coordinator."),
            new Field("leader", Type.COMPACT_STRING, "The leader of the group."),
            new Field("member_id", Type.COMPACT_STRING, "The member ID assigned by the group coordinator."),
            new Field("members", new CompactArrayOf(JoinGroupResponseMember.SCHEMA_6), ""),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema SCHEMA_7 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
            new Field("generation_id", Type.INT32, "The generation ID of the group."),
            new Field("protocol_type", Type.COMPACT_NULLABLE_STRING, "The group protocol name."),
            new Field("protocol_name", Type.COMPACT_NULLABLE_STRING, "The group protocol selected by the coordinator."),
            new Field("leader", Type.COMPACT_STRING, "The leader of the group."),
            new Field("member_id", Type.COMPACT_STRING, "The member ID assigned by the group coordinator."),
            new Field("members", new CompactArrayOf(JoinGroupResponseMember.SCHEMA_6), ""),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2,
        SCHEMA_3,
        SCHEMA_4,
        SCHEMA_5,
        SCHEMA_6,
        SCHEMA_7
    };
    
    public JoinGroupResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public JoinGroupResponseData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public JoinGroupResponseData() {
        this.throttleTimeMs = 0;
        this.errorCode = (short) 0;
        this.generationId = -1;
        this.protocolType = null;
        this.protocolName = "";
        this.leader = "";
        this.memberId = "";
        this.members = new ArrayList<JoinGroupResponseMember>();
    }
    
    @Override
    public short apiKey() {
        return 11;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 7;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        if (_version >= 2) {
            this.throttleTimeMs = _readable.readInt();
        } else {
            this.throttleTimeMs = 0;
        }
        this.errorCode = _readable.readShort();
        this.generationId = _readable.readInt();
        if (_version >= 7) {
            int length;
            length = _readable.readUnsignedVarint() - 1;
            if (length < 0) {
                this.protocolType = null;
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field protocolType had invalid length " + length);
            } else {
                this.protocolType = _readable.readString(length);
            }
        } else {
            this.protocolType = null;
        }
        {
            int length;
            if (_version >= 6) {
                length = _readable.readUnsignedVarint() - 1;
            } else {
                length = _readable.readShort();
            }
            if (length < 0) {
                if (_version >= 7) {
                    this.protocolName = null;
                } else {
                    throw new RuntimeException("non-nullable field protocolName was serialized as null");
                }
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field protocolName had invalid length " + length);
            } else {
                this.protocolName = _readable.readString(length);
            }
        }
        {
            int length;
            if (_version >= 6) {
                length = _readable.readUnsignedVarint() - 1;
            } else {
                length = _readable.readShort();
            }
            if (length < 0) {
                throw new RuntimeException("non-nullable field leader was serialized as null");
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field leader had invalid length " + length);
            } else {
                this.leader = _readable.readString(length);
            }
        }
        {
            int length;
            if (_version >= 6) {
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
            if (_version >= 6) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field members was serialized as null");
                } else {
                    ArrayList<JoinGroupResponseMember> newCollection = new ArrayList<JoinGroupResponseMember>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new JoinGroupResponseMember(_readable, _version));
                    }
                    this.members = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field members was serialized as null");
                } else {
                    ArrayList<JoinGroupResponseMember> newCollection = new ArrayList<JoinGroupResponseMember>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new JoinGroupResponseMember(_readable, _version));
                    }
                    this.members = newCollection;
                }
            }
        }
        this._unknownTaggedFields = null;
        if (_version >= 6) {
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
            _writable.writeInt(throttleTimeMs);
        }
        _writable.writeShort(errorCode);
        _writable.writeInt(generationId);
        if (_version >= 7) {
            if (protocolType == null) {
                _writable.writeUnsignedVarint(0);
            } else {
                byte[] _stringBytes = _cache.getSerializedValue(protocolType);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
        }
        if (protocolName == null) {
            if (_version >= 7) {
                if (_version >= 6) {
                    _writable.writeUnsignedVarint(0);
                } else {
                    _writable.writeShort((short) -1);
                }
            } else {
                throw new NullPointerException();
            }
        } else {
            byte[] _stringBytes = _cache.getSerializedValue(protocolName);
            if (_version >= 6) {
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
            } else {
                _writable.writeShort((short) _stringBytes.length);
            }
            _writable.writeByteArray(_stringBytes);
        }
        {
            byte[] _stringBytes = _cache.getSerializedValue(leader);
            if (_version >= 6) {
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
            } else {
                _writable.writeShort((short) _stringBytes.length);
            }
            _writable.writeByteArray(_stringBytes);
        }
        {
            byte[] _stringBytes = _cache.getSerializedValue(memberId);
            if (_version >= 6) {
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
            } else {
                _writable.writeShort((short) _stringBytes.length);
            }
            _writable.writeByteArray(_stringBytes);
        }
        if (_version >= 6) {
            _writable.writeUnsignedVarint(members.size() + 1);
            for (JoinGroupResponseMember membersElement : members) {
                membersElement.write(_writable, _cache, _version);
            }
        } else {
            _writable.writeInt(members.size());
            for (JoinGroupResponseMember membersElement : members) {
                membersElement.write(_writable, _cache, _version);
            }
        }
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        if (_version >= 6) {
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
        if (_version >= 6) {
            _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
        }
        if (_version >= 2) {
            this.throttleTimeMs = struct.getInt("throttle_time_ms");
        } else {
            this.throttleTimeMs = 0;
        }
        this.errorCode = struct.getShort("error_code");
        this.generationId = struct.getInt("generation_id");
        if (_version >= 7) {
            this.protocolType = struct.getString("protocol_type");
        } else {
            this.protocolType = null;
        }
        this.protocolName = struct.getString("protocol_name");
        this.leader = struct.getString("leader");
        this.memberId = struct.getString("member_id");
        {
            Object[] _nestedObjects = struct.getArray("members");
            this.members = new ArrayList<JoinGroupResponseMember>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.members.add(new JoinGroupResponseMember((Struct) nestedObject, _version));
            }
        }
        if (_version >= 6) {
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
        if (_version >= 6) {
            _taggedFields = new TreeMap<>();
        }
        Struct struct = new Struct(SCHEMAS[_version]);
        if (_version >= 2) {
            struct.set("throttle_time_ms", this.throttleTimeMs);
        }
        struct.set("error_code", this.errorCode);
        struct.set("generation_id", this.generationId);
        if (_version >= 7) {
            struct.set("protocol_type", this.protocolType);
        }
        struct.set("protocol_name", this.protocolName);
        struct.set("leader", this.leader);
        struct.set("member_id", this.memberId);
        {
            Struct[] _nestedObjects = new Struct[members.size()];
            int i = 0;
            for (JoinGroupResponseMember element : this.members) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("members", (Object[]) _nestedObjects);
        }
        if (_version >= 6) {
            struct.set("_tagged_fields", _taggedFields);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        if (_version >= 2) {
            _size += 4;
        }
        _size += 2;
        _size += 4;
        if (_version >= 7) {
            if (protocolType == null) {
                _size += 1;
            } else {
                byte[] _stringBytes = protocolType.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'protocolType' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(protocolType, _stringBytes);
                _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
            }
        }
        if (protocolName == null) {
            if (_version >= 6) {
                _size += 1;
            } else {
                _size += 2;
            }
        } else {
            byte[] _stringBytes = protocolName.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'protocolName' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(protocolName, _stringBytes);
            if (_version >= 6) {
                _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
            } else {
                _size += _stringBytes.length + 2;
            }
        }
        {
            byte[] _stringBytes = leader.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'leader' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(leader, _stringBytes);
            if (_version >= 6) {
                _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
            } else {
                _size += _stringBytes.length + 2;
            }
        }
        {
            byte[] _stringBytes = memberId.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'memberId' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(memberId, _stringBytes);
            if (_version >= 6) {
                _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
            } else {
                _size += _stringBytes.length + 2;
            }
        }
        {
            int _arraySize = 0;
            if (_version >= 6) {
                _arraySize += ByteUtils.sizeOfUnsignedVarint(members.size() + 1);
            } else {
                _arraySize += 4;
            }
            for (JoinGroupResponseMember membersElement : members) {
                _arraySize += membersElement.size(_cache, _version);
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
        if (_version >= 6) {
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
        if (!(obj instanceof JoinGroupResponseData)) return false;
        JoinGroupResponseData other = (JoinGroupResponseData) obj;
        if (throttleTimeMs != other.throttleTimeMs) return false;
        if (errorCode != other.errorCode) return false;
        if (generationId != other.generationId) return false;
        if (this.protocolType == null) {
            if (other.protocolType != null) return false;
        } else {
            if (!this.protocolType.equals(other.protocolType)) return false;
        }
        if (this.protocolName == null) {
            if (other.protocolName != null) return false;
        } else {
            if (!this.protocolName.equals(other.protocolName)) return false;
        }
        if (this.leader == null) {
            if (other.leader != null) return false;
        } else {
            if (!this.leader.equals(other.leader)) return false;
        }
        if (this.memberId == null) {
            if (other.memberId != null) return false;
        } else {
            if (!this.memberId.equals(other.memberId)) return false;
        }
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
        hashCode = 31 * hashCode + generationId;
        hashCode = 31 * hashCode + (protocolType == null ? 0 : protocolType.hashCode());
        hashCode = 31 * hashCode + (protocolName == null ? 0 : protocolName.hashCode());
        hashCode = 31 * hashCode + (leader == null ? 0 : leader.hashCode());
        hashCode = 31 * hashCode + (memberId == null ? 0 : memberId.hashCode());
        hashCode = 31 * hashCode + (members == null ? 0 : members.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "JoinGroupResponseData("
            + "throttleTimeMs=" + throttleTimeMs
            + ", errorCode=" + errorCode
            + ", generationId=" + generationId
            + ", protocolType=" + ((protocolType == null) ? "null" : "'" + protocolType.toString() + "'")
            + ", protocolName=" + ((protocolName == null) ? "null" : "'" + protocolName.toString() + "'")
            + ", leader=" + ((leader == null) ? "null" : "'" + leader.toString() + "'")
            + ", memberId=" + ((memberId == null) ? "null" : "'" + memberId.toString() + "'")
            + ", members=" + MessageUtil.deepToString(members.iterator())
            + ")";
    }
    
    public int throttleTimeMs() {
        return this.throttleTimeMs;
    }
    
    public short errorCode() {
        return this.errorCode;
    }
    
    public int generationId() {
        return this.generationId;
    }
    
    public String protocolType() {
        return this.protocolType;
    }
    
    public String protocolName() {
        return this.protocolName;
    }
    
    public String leader() {
        return this.leader;
    }
    
    public String memberId() {
        return this.memberId;
    }
    
    public List<JoinGroupResponseMember> members() {
        return this.members;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public JoinGroupResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
    
    public JoinGroupResponseData setErrorCode(short v) {
        this.errorCode = v;
        return this;
    }
    
    public JoinGroupResponseData setGenerationId(int v) {
        this.generationId = v;
        return this;
    }
    
    public JoinGroupResponseData setProtocolType(String v) {
        this.protocolType = v;
        return this;
    }
    
    public JoinGroupResponseData setProtocolName(String v) {
        this.protocolName = v;
        return this;
    }
    
    public JoinGroupResponseData setLeader(String v) {
        this.leader = v;
        return this;
    }
    
    public JoinGroupResponseData setMemberId(String v) {
        this.memberId = v;
        return this;
    }
    
    public JoinGroupResponseData setMembers(List<JoinGroupResponseMember> v) {
        this.members = v;
        return this;
    }
    
    static public class JoinGroupResponseMember implements Message {
        private String memberId;
        private String groupInstanceId;
        private byte[] metadata;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("member_id", Type.STRING, "The group member ID."),
                new Field("metadata", Type.BYTES, "The group member metadata.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 =
            new Schema(
                new Field("member_id", Type.STRING, "The group member ID."),
                new Field("group_instance_id", Type.NULLABLE_STRING, "The unique identifier of the consumer instance provided by end user."),
                new Field("metadata", Type.BYTES, "The group member metadata.")
            );
        
        public static final Schema SCHEMA_6 =
            new Schema(
                new Field("member_id", Type.COMPACT_STRING, "The group member ID."),
                new Field("group_instance_id", Type.COMPACT_NULLABLE_STRING, "The unique identifier of the consumer instance provided by end user."),
                new Field("metadata", Type.COMPACT_BYTES, "The group member metadata."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema SCHEMA_7 = SCHEMA_6;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3,
            SCHEMA_4,
            SCHEMA_5,
            SCHEMA_6,
            SCHEMA_7
        };
        
        public JoinGroupResponseMember(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public JoinGroupResponseMember(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public JoinGroupResponseMember() {
            this.memberId = "";
            this.groupInstanceId = null;
            this.metadata = Bytes.EMPTY;
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 7;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 7) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of JoinGroupResponseMember");
            }
            {
                int length;
                if (_version >= 6) {
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
            if (_version >= 5) {
                int length;
                if (_version >= 6) {
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
            } else {
                this.groupInstanceId = null;
            }
            {
                int length;
                if (_version >= 6) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readInt();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field metadata was serialized as null");
                } else {
                    byte[] newBytes = new byte[length];
                    _readable.readArray(newBytes);
                    this.metadata = newBytes;
                }
            }
            this._unknownTaggedFields = null;
            if (_version >= 6) {
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
            if (_version > 7) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of JoinGroupResponseMember");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(memberId);
                if (_version >= 6) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            if (_version >= 5) {
                if (groupInstanceId == null) {
                    if (_version >= 6) {
                        _writable.writeUnsignedVarint(0);
                    } else {
                        _writable.writeShort((short) -1);
                    }
                } else {
                    byte[] _stringBytes = _cache.getSerializedValue(groupInstanceId);
                    if (_version >= 6) {
                        _writable.writeUnsignedVarint(_stringBytes.length + 1);
                    } else {
                        _writable.writeShort((short) _stringBytes.length);
                    }
                    _writable.writeByteArray(_stringBytes);
                }
            } else {
                if (groupInstanceId != null) {
                    throw new UnsupportedVersionException("Attempted to write a non-default groupInstanceId at version " + _version);
                }
            }
            if (_version >= 6) {
                _writable.writeUnsignedVarint(metadata.length + 1);
            } else {
                _writable.writeInt(metadata.length);
            }
            _writable.writeByteArray(metadata);
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 6) {
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
            if (_version > 7) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of JoinGroupResponseMember");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 6) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.memberId = struct.getString("member_id");
            if (_version >= 5) {
                this.groupInstanceId = struct.getString("group_instance_id");
            } else {
                this.groupInstanceId = null;
            }
            this.metadata = struct.getByteArray("metadata");
            if (_version >= 6) {
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
            if (_version > 7) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of JoinGroupResponseMember");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 6) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("member_id", this.memberId);
            if (_version >= 5) {
                struct.set("group_instance_id", this.groupInstanceId);
            } else {
                if (groupInstanceId != null) {
                    throw new UnsupportedVersionException("Attempted to write a non-default groupInstanceId at version " + _version);
                }
            }
            struct.setByteArray("metadata", this.metadata);
            if (_version >= 6) {
                struct.set("_tagged_fields", _taggedFields);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 7) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of JoinGroupResponseMember");
            }
            {
                byte[] _stringBytes = memberId.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'memberId' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(memberId, _stringBytes);
                if (_version >= 6) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
                }
            }
            if (_version >= 5) {
                if (groupInstanceId == null) {
                    if (_version >= 6) {
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
                    if (_version >= 6) {
                        _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                    } else {
                        _size += _stringBytes.length + 2;
                    }
                }
            }
            {
                int _bytesSize = metadata.length;
                if (_version >= 6) {
                    _bytesSize += ByteUtils.sizeOfUnsignedVarint(metadata.length + 1);
                } else {
                    _bytesSize += 4;
                }
                _size += _bytesSize;
            }
            if (_unknownTaggedFields != null) {
                _numTaggedFields += _unknownTaggedFields.size();
                for (RawTaggedField _field : _unknownTaggedFields) {
                    _size += ByteUtils.sizeOfUnsignedVarint(_field.tag());
                    _size += ByteUtils.sizeOfUnsignedVarint(_field.size());
                    _size += _field.size();
                }
            }
            if (_version >= 6) {
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
            if (!(obj instanceof JoinGroupResponseMember)) return false;
            JoinGroupResponseMember other = (JoinGroupResponseMember) obj;
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
            if (!Arrays.equals(this.metadata, other.metadata)) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (memberId == null ? 0 : memberId.hashCode());
            hashCode = 31 * hashCode + (groupInstanceId == null ? 0 : groupInstanceId.hashCode());
            hashCode = 31 * hashCode + Arrays.hashCode(metadata);
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "JoinGroupResponseMember("
                + "memberId=" + ((memberId == null) ? "null" : "'" + memberId.toString() + "'")
                + ", groupInstanceId=" + ((groupInstanceId == null) ? "null" : "'" + groupInstanceId.toString() + "'")
                + ", metadata=" + Arrays.toString(metadata)
                + ")";
        }
        
        public String memberId() {
            return this.memberId;
        }
        
        public String groupInstanceId() {
            return this.groupInstanceId;
        }
        
        public byte[] metadata() {
            return this.metadata;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public JoinGroupResponseMember setMemberId(String v) {
            this.memberId = v;
            return this;
        }
        
        public JoinGroupResponseMember setGroupInstanceId(String v) {
            this.groupInstanceId = v;
            return this;
        }
        
        public JoinGroupResponseMember setMetadata(byte[] v) {
            this.metadata = v;
            return this;
        }
    }
}
