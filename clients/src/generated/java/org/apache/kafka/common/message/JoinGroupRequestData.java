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
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.common.utils.ImplicitLinkedHashCollection;
import org.apache.kafka.common.utils.ImplicitLinkedHashMultiCollection;

import static java.util.Map.Entry;
import static org.apache.kafka.common.protocol.types.Field.TaggedFieldsSection;


public class JoinGroupRequestData implements ApiMessage {
    private String groupId;
    private int sessionTimeoutMs;
    private int rebalanceTimeoutMs;
    private String memberId;
    private String groupInstanceId;
    private String protocolType;
    private JoinGroupRequestProtocolCollection protocols;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("group_id", Type.STRING, "The group identifier."),
            new Field("session_timeout_ms", Type.INT32, "The coordinator considers the consumer dead if it receives no heartbeat after this timeout in milliseconds."),
            new Field("member_id", Type.STRING, "The member id assigned by the group coordinator."),
            new Field("protocol_type", Type.STRING, "The unique name the for class of protocols implemented by the group we want to join."),
            new Field("protocols", new ArrayOf(JoinGroupRequestProtocol.SCHEMA_0), "The list of protocols that the member supports.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("group_id", Type.STRING, "The group identifier."),
            new Field("session_timeout_ms", Type.INT32, "The coordinator considers the consumer dead if it receives no heartbeat after this timeout in milliseconds."),
            new Field("rebalance_timeout_ms", Type.INT32, "The maximum time in milliseconds that the coordinator will wait for each member to rejoin when rebalancing the group."),
            new Field("member_id", Type.STRING, "The member id assigned by the group coordinator."),
            new Field("protocol_type", Type.STRING, "The unique name the for class of protocols implemented by the group we want to join."),
            new Field("protocols", new ArrayOf(JoinGroupRequestProtocol.SCHEMA_0), "The list of protocols that the member supports.")
        );
    
    public static final Schema SCHEMA_2 = SCHEMA_1;
    
    public static final Schema SCHEMA_3 = SCHEMA_2;
    
    public static final Schema SCHEMA_4 = SCHEMA_3;
    
    public static final Schema SCHEMA_5 =
        new Schema(
            new Field("group_id", Type.STRING, "The group identifier."),
            new Field("session_timeout_ms", Type.INT32, "The coordinator considers the consumer dead if it receives no heartbeat after this timeout in milliseconds."),
            new Field("rebalance_timeout_ms", Type.INT32, "The maximum time in milliseconds that the coordinator will wait for each member to rejoin when rebalancing the group."),
            new Field("member_id", Type.STRING, "The member id assigned by the group coordinator."),
            new Field("group_instance_id", Type.NULLABLE_STRING, "The unique identifier of the consumer instance provided by end user."),
            new Field("protocol_type", Type.STRING, "The unique name the for class of protocols implemented by the group we want to join."),
            new Field("protocols", new ArrayOf(JoinGroupRequestProtocol.SCHEMA_0), "The list of protocols that the member supports.")
        );
    
    public static final Schema SCHEMA_6 =
        new Schema(
            new Field("group_id", Type.COMPACT_STRING, "The group identifier."),
            new Field("session_timeout_ms", Type.INT32, "The coordinator considers the consumer dead if it receives no heartbeat after this timeout in milliseconds."),
            new Field("rebalance_timeout_ms", Type.INT32, "The maximum time in milliseconds that the coordinator will wait for each member to rejoin when rebalancing the group."),
            new Field("member_id", Type.COMPACT_STRING, "The member id assigned by the group coordinator."),
            new Field("group_instance_id", Type.COMPACT_NULLABLE_STRING, "The unique identifier of the consumer instance provided by end user."),
            new Field("protocol_type", Type.COMPACT_STRING, "The unique name the for class of protocols implemented by the group we want to join."),
            new Field("protocols", new CompactArrayOf(JoinGroupRequestProtocol.SCHEMA_6), "The list of protocols that the member supports."),
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
    
    public JoinGroupRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public JoinGroupRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public JoinGroupRequestData() {
        this.groupId = "";
        this.sessionTimeoutMs = 0;
        this.rebalanceTimeoutMs = -1;
        this.memberId = "";
        this.groupInstanceId = null;
        this.protocolType = "";
        this.protocols = new JoinGroupRequestProtocolCollection(0);
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
        {
            int length;
            if (_version >= 6) {
                length = _readable.readUnsignedVarint() - 1;
            } else {
                length = _readable.readShort();
            }
            if (length < 0) {
                throw new RuntimeException("non-nullable field groupId was serialized as null");
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field groupId had invalid length " + length);
            } else {
                this.groupId = _readable.readString(length);
            }
        }
        this.sessionTimeoutMs = _readable.readInt();
        if (_version >= 1) {
            this.rebalanceTimeoutMs = _readable.readInt();
        } else {
            this.rebalanceTimeoutMs = -1;
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
                length = _readable.readShort();
            }
            if (length < 0) {
                throw new RuntimeException("non-nullable field protocolType was serialized as null");
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field protocolType had invalid length " + length);
            } else {
                this.protocolType = _readable.readString(length);
            }
        }
        {
            if (_version >= 6) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field protocols was serialized as null");
                } else {
                    JoinGroupRequestProtocolCollection newCollection = new JoinGroupRequestProtocolCollection(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new JoinGroupRequestProtocol(_readable, _version));
                    }
                    this.protocols = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field protocols was serialized as null");
                } else {
                    JoinGroupRequestProtocolCollection newCollection = new JoinGroupRequestProtocolCollection(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new JoinGroupRequestProtocol(_readable, _version));
                    }
                    this.protocols = newCollection;
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
        {
            byte[] _stringBytes = _cache.getSerializedValue(groupId);
            if (_version >= 6) {
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
            } else {
                _writable.writeShort((short) _stringBytes.length);
            }
            _writable.writeByteArray(_stringBytes);
        }
        _writable.writeInt(sessionTimeoutMs);
        if (_version >= 1) {
            _writable.writeInt(rebalanceTimeoutMs);
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
        {
            byte[] _stringBytes = _cache.getSerializedValue(protocolType);
            if (_version >= 6) {
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
            } else {
                _writable.writeShort((short) _stringBytes.length);
            }
            _writable.writeByteArray(_stringBytes);
        }
        if (_version >= 6) {
            _writable.writeUnsignedVarint(protocols.size() + 1);
            for (JoinGroupRequestProtocol protocolsElement : protocols) {
                protocolsElement.write(_writable, _cache, _version);
            }
        } else {
            _writable.writeInt(protocols.size());
            for (JoinGroupRequestProtocol protocolsElement : protocols) {
                protocolsElement.write(_writable, _cache, _version);
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
        this.groupId = struct.getString("group_id");
        this.sessionTimeoutMs = struct.getInt("session_timeout_ms");
        if (_version >= 1) {
            this.rebalanceTimeoutMs = struct.getInt("rebalance_timeout_ms");
        } else {
            this.rebalanceTimeoutMs = -1;
        }
        this.memberId = struct.getString("member_id");
        if (_version >= 5) {
            this.groupInstanceId = struct.getString("group_instance_id");
        } else {
            this.groupInstanceId = null;
        }
        this.protocolType = struct.getString("protocol_type");
        {
            Object[] _nestedObjects = struct.getArray("protocols");
            this.protocols = new JoinGroupRequestProtocolCollection(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.protocols.add(new JoinGroupRequestProtocol((Struct) nestedObject, _version));
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
        struct.set("group_id", this.groupId);
        struct.set("session_timeout_ms", this.sessionTimeoutMs);
        if (_version >= 1) {
            struct.set("rebalance_timeout_ms", this.rebalanceTimeoutMs);
        }
        struct.set("member_id", this.memberId);
        if (_version >= 5) {
            struct.set("group_instance_id", this.groupInstanceId);
        } else {
            if (groupInstanceId != null) {
                throw new UnsupportedVersionException("Attempted to write a non-default groupInstanceId at version " + _version);
            }
        }
        struct.set("protocol_type", this.protocolType);
        {
            Struct[] _nestedObjects = new Struct[protocols.size()];
            int i = 0;
            for (JoinGroupRequestProtocol element : this.protocols) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("protocols", (Object[]) _nestedObjects);
        }
        if (_version >= 6) {
            struct.set("_tagged_fields", _taggedFields);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        {
            byte[] _stringBytes = groupId.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'groupId' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(groupId, _stringBytes);
            if (_version >= 6) {
                _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
            } else {
                _size += _stringBytes.length + 2;
            }
        }
        _size += 4;
        if (_version >= 1) {
            _size += 4;
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
            byte[] _stringBytes = protocolType.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'protocolType' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(protocolType, _stringBytes);
            if (_version >= 6) {
                _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
            } else {
                _size += _stringBytes.length + 2;
            }
        }
        {
            int _arraySize = 0;
            if (_version >= 6) {
                _arraySize += ByteUtils.sizeOfUnsignedVarint(protocols.size() + 1);
            } else {
                _arraySize += 4;
            }
            for (JoinGroupRequestProtocol protocolsElement : protocols) {
                _arraySize += protocolsElement.size(_cache, _version);
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
        if (!(obj instanceof JoinGroupRequestData)) return false;
        JoinGroupRequestData other = (JoinGroupRequestData) obj;
        if (this.groupId == null) {
            if (other.groupId != null) return false;
        } else {
            if (!this.groupId.equals(other.groupId)) return false;
        }
        if (sessionTimeoutMs != other.sessionTimeoutMs) return false;
        if (rebalanceTimeoutMs != other.rebalanceTimeoutMs) return false;
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
        if (this.protocolType == null) {
            if (other.protocolType != null) return false;
        } else {
            if (!this.protocolType.equals(other.protocolType)) return false;
        }
        if (this.protocols == null) {
            if (other.protocols != null) return false;
        } else {
            if (!this.protocols.equals(other.protocols)) return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (groupId == null ? 0 : groupId.hashCode());
        hashCode = 31 * hashCode + sessionTimeoutMs;
        hashCode = 31 * hashCode + rebalanceTimeoutMs;
        hashCode = 31 * hashCode + (memberId == null ? 0 : memberId.hashCode());
        hashCode = 31 * hashCode + (groupInstanceId == null ? 0 : groupInstanceId.hashCode());
        hashCode = 31 * hashCode + (protocolType == null ? 0 : protocolType.hashCode());
        hashCode = 31 * hashCode + (protocols == null ? 0 : protocols.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "JoinGroupRequestData("
            + "groupId=" + ((groupId == null) ? "null" : "'" + groupId.toString() + "'")
            + ", sessionTimeoutMs=" + sessionTimeoutMs
            + ", rebalanceTimeoutMs=" + rebalanceTimeoutMs
            + ", memberId=" + ((memberId == null) ? "null" : "'" + memberId.toString() + "'")
            + ", groupInstanceId=" + ((groupInstanceId == null) ? "null" : "'" + groupInstanceId.toString() + "'")
            + ", protocolType=" + ((protocolType == null) ? "null" : "'" + protocolType.toString() + "'")
            + ", protocols=" + MessageUtil.deepToString(protocols.iterator())
            + ")";
    }
    
    public String groupId() {
        return this.groupId;
    }
    
    public int sessionTimeoutMs() {
        return this.sessionTimeoutMs;
    }
    
    public int rebalanceTimeoutMs() {
        return this.rebalanceTimeoutMs;
    }
    
    public String memberId() {
        return this.memberId;
    }
    
    public String groupInstanceId() {
        return this.groupInstanceId;
    }
    
    public String protocolType() {
        return this.protocolType;
    }
    
    public JoinGroupRequestProtocolCollection protocols() {
        return this.protocols;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public JoinGroupRequestData setGroupId(String v) {
        this.groupId = v;
        return this;
    }
    
    public JoinGroupRequestData setSessionTimeoutMs(int v) {
        this.sessionTimeoutMs = v;
        return this;
    }
    
    public JoinGroupRequestData setRebalanceTimeoutMs(int v) {
        this.rebalanceTimeoutMs = v;
        return this;
    }
    
    public JoinGroupRequestData setMemberId(String v) {
        this.memberId = v;
        return this;
    }
    
    public JoinGroupRequestData setGroupInstanceId(String v) {
        this.groupInstanceId = v;
        return this;
    }
    
    public JoinGroupRequestData setProtocolType(String v) {
        this.protocolType = v;
        return this;
    }
    
    public JoinGroupRequestData setProtocols(JoinGroupRequestProtocolCollection v) {
        this.protocols = v;
        return this;
    }
    
    static public class JoinGroupRequestProtocol implements Message, ImplicitLinkedHashMultiCollection.Element {
        private String name;
        private byte[] metadata;
        private List<RawTaggedField> _unknownTaggedFields;
        private int next;
        private int prev;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.STRING, "The protocol name."),
                new Field("metadata", Type.BYTES, "The protocol metadata.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 = SCHEMA_4;
        
        public static final Schema SCHEMA_6 =
            new Schema(
                new Field("name", Type.COMPACT_STRING, "The protocol name."),
                new Field("metadata", Type.COMPACT_BYTES, "The protocol metadata."),
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
        
        public JoinGroupRequestProtocol(Readable _readable, short _version) {
            read(_readable, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public JoinGroupRequestProtocol(Struct struct, short _version) {
            fromStruct(struct, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public JoinGroupRequestProtocol() {
            this.name = "";
            this.metadata = Bytes.EMPTY;
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of JoinGroupRequestProtocol");
            }
            {
                int length;
                if (_version >= 6) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field name was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field name had invalid length " + length);
                } else {
                    this.name = _readable.readString(length);
                }
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
                throw new UnsupportedVersionException("Can't write version " + _version + " of JoinGroupRequestProtocol");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                if (_version >= 6) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of JoinGroupRequestProtocol");
            }
            NavigableMap<Integer, Object> _taggedFields = null;
            this._unknownTaggedFields = null;
            if (_version >= 6) {
                _taggedFields = (NavigableMap<Integer, Object>) struct.get("_tagged_fields");
            }
            this.name = struct.getString("name");
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
                throw new UnsupportedVersionException("Can't write version " + _version + " of JoinGroupRequestProtocol");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            if (_version >= 6) {
                _taggedFields = new TreeMap<>();
            }
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("name", this.name);
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
                throw new UnsupportedVersionException("Can't size version " + _version + " of JoinGroupRequestProtocol");
            }
            {
                byte[] _stringBytes = name.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'name' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(name, _stringBytes);
                if (_version >= 6) {
                    _size += _stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _size += _stringBytes.length + 2;
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
            if (!(obj instanceof JoinGroupRequestProtocol)) return false;
            JoinGroupRequestProtocol other = (JoinGroupRequestProtocol) obj;
            if (this.name == null) {
                if (other.name != null) return false;
            } else {
                if (!this.name.equals(other.name)) return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (name == null ? 0 : name.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "JoinGroupRequestProtocol("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", metadata=" + Arrays.toString(metadata)
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public byte[] metadata() {
            return this.metadata;
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
        
        public JoinGroupRequestProtocol setName(String v) {
            this.name = v;
            return this;
        }
        
        public JoinGroupRequestProtocol setMetadata(byte[] v) {
            this.metadata = v;
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
    
    public static class JoinGroupRequestProtocolCollection extends ImplicitLinkedHashMultiCollection<JoinGroupRequestProtocol> {
        public JoinGroupRequestProtocolCollection() {
            super();
        }
        
        public JoinGroupRequestProtocolCollection(int expectedNumElements) {
            super(expectedNumElements);
        }
        
        public JoinGroupRequestProtocolCollection(Iterator<JoinGroupRequestProtocol> iterator) {
            super(iterator);
        }
        
        public JoinGroupRequestProtocol find(String name) {
            JoinGroupRequestProtocol _key = new JoinGroupRequestProtocol();
            _key.setName(name);
            return find(_key);
        }
        
        public List<JoinGroupRequestProtocol> findAll(String name) {
            JoinGroupRequestProtocol _key = new JoinGroupRequestProtocol();
            _key.setName(name);
            return findAll(_key);
        }
        
    }
}
