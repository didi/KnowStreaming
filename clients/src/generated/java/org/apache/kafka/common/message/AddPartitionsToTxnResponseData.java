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
import java.util.Iterator;
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
import org.apache.kafka.common.utils.ImplicitLinkedHashCollection;
import org.apache.kafka.common.utils.ImplicitLinkedHashMultiCollection;


public class AddPartitionsToTxnResponseData implements ApiMessage {
    private int throttleTimeMs;
    private AddPartitionsToTxnTopicResultCollection results;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "Duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("results", new ArrayOf(AddPartitionsToTxnTopicResult.SCHEMA_0), "The results for each topic.")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1
    };
    
    public AddPartitionsToTxnResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public AddPartitionsToTxnResponseData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public AddPartitionsToTxnResponseData() {
        this.throttleTimeMs = 0;
        this.results = new AddPartitionsToTxnTopicResultCollection(0);
    }
    
    @Override
    public short apiKey() {
        return 24;
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
        this.throttleTimeMs = _readable.readInt();
        {
            int arrayLength;
            arrayLength = _readable.readInt();
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field results was serialized as null");
            } else {
                AddPartitionsToTxnTopicResultCollection newCollection = new AddPartitionsToTxnTopicResultCollection(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new AddPartitionsToTxnTopicResult(_readable, _version));
                }
                this.results = newCollection;
            }
        }
        this._unknownTaggedFields = null;
    }
    
    @Override
    public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        _writable.writeInt(throttleTimeMs);
        _writable.writeInt(results.size());
        for (AddPartitionsToTxnTopicResult resultsElement : results) {
            resultsElement.write(_writable, _cache, _version);
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
        this.throttleTimeMs = struct.getInt("throttle_time_ms");
        {
            Object[] _nestedObjects = struct.getArray("results");
            this.results = new AddPartitionsToTxnTopicResultCollection(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.results.add(new AddPartitionsToTxnTopicResult((Struct) nestedObject, _version));
            }
        }
    }
    
    @Override
    public Struct toStruct(short _version) {
        TreeMap<Integer, Object> _taggedFields = null;
        Struct struct = new Struct(SCHEMAS[_version]);
        struct.set("throttle_time_ms", this.throttleTimeMs);
        {
            Struct[] _nestedObjects = new Struct[results.size()];
            int i = 0;
            for (AddPartitionsToTxnTopicResult element : this.results) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("results", (Object[]) _nestedObjects);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        _size += 4;
        {
            int _arraySize = 0;
            _arraySize += 4;
            for (AddPartitionsToTxnTopicResult resultsElement : results) {
                _arraySize += resultsElement.size(_cache, _version);
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
        if (!(obj instanceof AddPartitionsToTxnResponseData)) return false;
        AddPartitionsToTxnResponseData other = (AddPartitionsToTxnResponseData) obj;
        if (throttleTimeMs != other.throttleTimeMs) return false;
        if (this.results == null) {
            if (other.results != null) return false;
        } else {
            if (!this.results.equals(other.results)) return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + throttleTimeMs;
        hashCode = 31 * hashCode + (results == null ? 0 : results.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "AddPartitionsToTxnResponseData("
            + "throttleTimeMs=" + throttleTimeMs
            + ", results=" + MessageUtil.deepToString(results.iterator())
            + ")";
    }
    
    public int throttleTimeMs() {
        return this.throttleTimeMs;
    }
    
    public AddPartitionsToTxnTopicResultCollection results() {
        return this.results;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public AddPartitionsToTxnResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
    
    public AddPartitionsToTxnResponseData setResults(AddPartitionsToTxnTopicResultCollection v) {
        this.results = v;
        return this;
    }
    
    static public class AddPartitionsToTxnTopicResult implements Message, ImplicitLinkedHashMultiCollection.Element {
        private String name;
        private AddPartitionsToTxnPartitionResultCollection results;
        private List<RawTaggedField> _unknownTaggedFields;
        private int next;
        private int prev;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("results", new ArrayOf(AddPartitionsToTxnPartitionResult.SCHEMA_0), "The results for each partition")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1
        };
        
        public AddPartitionsToTxnTopicResult(Readable _readable, short _version) {
            read(_readable, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public AddPartitionsToTxnTopicResult(Struct struct, short _version) {
            fromStruct(struct, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public AddPartitionsToTxnTopicResult() {
            this.name = "";
            this.results = new AddPartitionsToTxnPartitionResultCollection(0);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
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
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of AddPartitionsToTxnTopicResult");
            }
            {
                int length;
                length = _readable.readShort();
                if (length < 0) {
                    throw new RuntimeException("non-nullable field name was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field name had invalid length " + length);
                } else {
                    this.name = _readable.readString(length);
                }
            }
            {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field results was serialized as null");
                } else {
                    AddPartitionsToTxnPartitionResultCollection newCollection = new AddPartitionsToTxnPartitionResultCollection(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new AddPartitionsToTxnPartitionResult(_readable, _version));
                    }
                    this.results = newCollection;
                }
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of AddPartitionsToTxnTopicResult");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                _writable.writeShort((short) _stringBytes.length);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeInt(results.size());
            for (AddPartitionsToTxnPartitionResult resultsElement : results) {
                resultsElement.write(_writable, _cache, _version);
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
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of AddPartitionsToTxnTopicResult");
            }
            this._unknownTaggedFields = null;
            this.name = struct.getString("name");
            {
                Object[] _nestedObjects = struct.getArray("results");
                this.results = new AddPartitionsToTxnPartitionResultCollection(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.results.add(new AddPartitionsToTxnPartitionResult((Struct) nestedObject, _version));
                }
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of AddPartitionsToTxnTopicResult");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("name", this.name);
            {
                Struct[] _nestedObjects = new Struct[results.size()];
                int i = 0;
                for (AddPartitionsToTxnPartitionResult element : this.results) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("results", (Object[]) _nestedObjects);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of AddPartitionsToTxnTopicResult");
            }
            {
                byte[] _stringBytes = name.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'name' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(name, _stringBytes);
                _size += _stringBytes.length + 2;
            }
            {
                int _arraySize = 0;
                _arraySize += 4;
                for (AddPartitionsToTxnPartitionResult resultsElement : results) {
                    _arraySize += resultsElement.size(_cache, _version);
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
            if (!(obj instanceof AddPartitionsToTxnTopicResult)) return false;
            AddPartitionsToTxnTopicResult other = (AddPartitionsToTxnTopicResult) obj;
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
            return "AddPartitionsToTxnTopicResult("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", results=" + MessageUtil.deepToString(results.iterator())
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public AddPartitionsToTxnPartitionResultCollection results() {
            return this.results;
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
        
        public AddPartitionsToTxnTopicResult setName(String v) {
            this.name = v;
            return this;
        }
        
        public AddPartitionsToTxnTopicResult setResults(AddPartitionsToTxnPartitionResultCollection v) {
            this.results = v;
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
    
    static public class AddPartitionsToTxnPartitionResult implements Message, ImplicitLinkedHashMultiCollection.Element {
        private int partitionIndex;
        private short errorCode;
        private List<RawTaggedField> _unknownTaggedFields;
        private int next;
        private int prev;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition indexes."),
                new Field("error_code", Type.INT16, "The response error code.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1
        };
        
        public AddPartitionsToTxnPartitionResult(Readable _readable, short _version) {
            read(_readable, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public AddPartitionsToTxnPartitionResult(Struct struct, short _version) {
            fromStruct(struct, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public AddPartitionsToTxnPartitionResult() {
            this.partitionIndex = 0;
            this.errorCode = (short) 0;
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
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
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of AddPartitionsToTxnPartitionResult");
            }
            this.partitionIndex = _readable.readInt();
            this.errorCode = _readable.readShort();
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of AddPartitionsToTxnPartitionResult");
            }
            int _numTaggedFields = 0;
            _writable.writeInt(partitionIndex);
            _writable.writeShort(errorCode);
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public void fromStruct(Struct struct, short _version) {
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of AddPartitionsToTxnPartitionResult");
            }
            this._unknownTaggedFields = null;
            this.partitionIndex = struct.getInt("partition_index");
            this.errorCode = struct.getShort("error_code");
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of AddPartitionsToTxnPartitionResult");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("partition_index", this.partitionIndex);
            struct.set("error_code", this.errorCode);
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 1) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of AddPartitionsToTxnPartitionResult");
            }
            _size += 4;
            _size += 2;
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
            if (!(obj instanceof AddPartitionsToTxnPartitionResult)) return false;
            AddPartitionsToTxnPartitionResult other = (AddPartitionsToTxnPartitionResult) obj;
            if (partitionIndex != other.partitionIndex) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + partitionIndex;
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "AddPartitionsToTxnPartitionResult("
                + "partitionIndex=" + partitionIndex
                + ", errorCode=" + errorCode
                + ")";
        }
        
        public int partitionIndex() {
            return this.partitionIndex;
        }
        
        public short errorCode() {
            return this.errorCode;
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
        
        public AddPartitionsToTxnPartitionResult setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
        
        public AddPartitionsToTxnPartitionResult setErrorCode(short v) {
            this.errorCode = v;
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
    
    public static class AddPartitionsToTxnPartitionResultCollection extends ImplicitLinkedHashMultiCollection<AddPartitionsToTxnPartitionResult> {
        public AddPartitionsToTxnPartitionResultCollection() {
            super();
        }
        
        public AddPartitionsToTxnPartitionResultCollection(int expectedNumElements) {
            super(expectedNumElements);
        }
        
        public AddPartitionsToTxnPartitionResultCollection(Iterator<AddPartitionsToTxnPartitionResult> iterator) {
            super(iterator);
        }
        
        public AddPartitionsToTxnPartitionResult find(int partitionIndex) {
            AddPartitionsToTxnPartitionResult _key = new AddPartitionsToTxnPartitionResult();
            _key.setPartitionIndex(partitionIndex);
            return find(_key);
        }
        
        public List<AddPartitionsToTxnPartitionResult> findAll(int partitionIndex) {
            AddPartitionsToTxnPartitionResult _key = new AddPartitionsToTxnPartitionResult();
            _key.setPartitionIndex(partitionIndex);
            return findAll(_key);
        }
        
    }
    
    public static class AddPartitionsToTxnTopicResultCollection extends ImplicitLinkedHashMultiCollection<AddPartitionsToTxnTopicResult> {
        public AddPartitionsToTxnTopicResultCollection() {
            super();
        }
        
        public AddPartitionsToTxnTopicResultCollection(int expectedNumElements) {
            super(expectedNumElements);
        }
        
        public AddPartitionsToTxnTopicResultCollection(Iterator<AddPartitionsToTxnTopicResult> iterator) {
            super(iterator);
        }
        
        public AddPartitionsToTxnTopicResult find(String name) {
            AddPartitionsToTxnTopicResult _key = new AddPartitionsToTxnTopicResult();
            _key.setName(name);
            return find(_key);
        }
        
        public List<AddPartitionsToTxnTopicResult> findAll(String name) {
            AddPartitionsToTxnTopicResult _key = new AddPartitionsToTxnTopicResult();
            _key.setName(name);
            return findAll(_key);
        }
        
    }
}
