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


public class WriteTxnMarkersRequestData implements ApiMessage {
    private List<WritableTxnMarker> markers;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("markers", new ArrayOf(WritableTxnMarker.SCHEMA_0), "The transaction markers to be written.")
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0
    };
    
    public WriteTxnMarkersRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public WriteTxnMarkersRequestData(Struct struct, short _version) {
        fromStruct(struct, _version);
    }
    
    public WriteTxnMarkersRequestData() {
        this.markers = new ArrayList<WritableTxnMarker>();
    }
    
    @Override
    public short apiKey() {
        return 27;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 0;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        {
            int arrayLength;
            arrayLength = _readable.readInt();
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field markers was serialized as null");
            } else {
                ArrayList<WritableTxnMarker> newCollection = new ArrayList<WritableTxnMarker>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new WritableTxnMarker(_readable, _version));
                }
                this.markers = newCollection;
            }
        }
        this._unknownTaggedFields = null;
    }
    
    @Override
    public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        _writable.writeInt(markers.size());
        for (WritableTxnMarker markersElement : markers) {
            markersElement.write(_writable, _cache, _version);
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
            Object[] _nestedObjects = struct.getArray("markers");
            this.markers = new ArrayList<WritableTxnMarker>(_nestedObjects.length);
            for (Object nestedObject : _nestedObjects) {
                this.markers.add(new WritableTxnMarker((Struct) nestedObject, _version));
            }
        }
    }
    
    @Override
    public Struct toStruct(short _version) {
        TreeMap<Integer, Object> _taggedFields = null;
        Struct struct = new Struct(SCHEMAS[_version]);
        {
            Struct[] _nestedObjects = new Struct[markers.size()];
            int i = 0;
            for (WritableTxnMarker element : this.markers) {
                _nestedObjects[i++] = element.toStruct(_version);
            }
            struct.set("markers", (Object[]) _nestedObjects);
        }
        return struct;
    }
    
    @Override
    public int size(ObjectSerializationCache _cache, short _version) {
        int _size = 0, _numTaggedFields = 0;
        {
            int _arraySize = 0;
            _arraySize += 4;
            for (WritableTxnMarker markersElement : markers) {
                _arraySize += markersElement.size(_cache, _version);
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
        if (!(obj instanceof WriteTxnMarkersRequestData)) return false;
        WriteTxnMarkersRequestData other = (WriteTxnMarkersRequestData) obj;
        if (this.markers == null) {
            if (other.markers != null) return false;
        } else {
            if (!this.markers.equals(other.markers)) return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (markers == null ? 0 : markers.hashCode());
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "WriteTxnMarkersRequestData("
            + "markers=" + MessageUtil.deepToString(markers.iterator())
            + ")";
    }
    
    public List<WritableTxnMarker> markers() {
        return this.markers;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public WriteTxnMarkersRequestData setMarkers(List<WritableTxnMarker> v) {
        this.markers = v;
        return this;
    }
    
    static public class WritableTxnMarker implements Message {
        private long producerId;
        private short producerEpoch;
        private boolean transactionResult;
        private List<WritableTxnMarkerTopic> topics;
        private int coordinatorEpoch;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("producer_id", Type.INT64, "The current producer ID."),
                new Field("producer_epoch", Type.INT16, "The current epoch associated with the producer ID."),
                new Field("transaction_result", Type.BOOLEAN, "The result of the transaction to write to the partitions (false = ABORT, true = COMMIT)."),
                new Field("topics", new ArrayOf(WritableTxnMarkerTopic.SCHEMA_0), "Each topic that we want to write transaction marker(s) for."),
                new Field("coordinator_epoch", Type.INT32, "Epoch associated with the transaction state partition hosted by this transaction coordinator")
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0
        };
        
        public WritableTxnMarker(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public WritableTxnMarker(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public WritableTxnMarker() {
            this.producerId = 0L;
            this.producerEpoch = (short) 0;
            this.transactionResult = false;
            this.topics = new ArrayList<WritableTxnMarkerTopic>();
            this.coordinatorEpoch = 0;
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 0;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of WritableTxnMarker");
            }
            this.producerId = _readable.readLong();
            this.producerEpoch = _readable.readShort();
            this.transactionResult = _readable.readByte() != 0;
            {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topics was serialized as null");
                } else {
                    ArrayList<WritableTxnMarkerTopic> newCollection = new ArrayList<WritableTxnMarkerTopic>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new WritableTxnMarkerTopic(_readable, _version));
                    }
                    this.topics = newCollection;
                }
            }
            this.coordinatorEpoch = _readable.readInt();
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of WritableTxnMarker");
            }
            int _numTaggedFields = 0;
            _writable.writeLong(producerId);
            _writable.writeShort(producerEpoch);
            _writable.writeByte(transactionResult ? (byte) 1 : (byte) 0);
            _writable.writeInt(topics.size());
            for (WritableTxnMarkerTopic topicsElement : topics) {
                topicsElement.write(_writable, _cache, _version);
            }
            _writable.writeInt(coordinatorEpoch);
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public void fromStruct(Struct struct, short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of WritableTxnMarker");
            }
            this._unknownTaggedFields = null;
            this.producerId = struct.getLong("producer_id");
            this.producerEpoch = struct.getShort("producer_epoch");
            this.transactionResult = struct.getBoolean("transaction_result");
            {
                Object[] _nestedObjects = struct.getArray("topics");
                this.topics = new ArrayList<WritableTxnMarkerTopic>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.topics.add(new WritableTxnMarkerTopic((Struct) nestedObject, _version));
                }
            }
            this.coordinatorEpoch = struct.getInt("coordinator_epoch");
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of WritableTxnMarker");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("producer_id", this.producerId);
            struct.set("producer_epoch", this.producerEpoch);
            struct.set("transaction_result", this.transactionResult);
            {
                Struct[] _nestedObjects = new Struct[topics.size()];
                int i = 0;
                for (WritableTxnMarkerTopic element : this.topics) {
                    _nestedObjects[i++] = element.toStruct(_version);
                }
                struct.set("topics", (Object[]) _nestedObjects);
            }
            struct.set("coordinator_epoch", this.coordinatorEpoch);
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of WritableTxnMarker");
            }
            _size += 8;
            _size += 2;
            _size += 1;
            {
                int _arraySize = 0;
                _arraySize += 4;
                for (WritableTxnMarkerTopic topicsElement : topics) {
                    _arraySize += topicsElement.size(_cache, _version);
                }
                _size += _arraySize;
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
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
            return _size;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof WritableTxnMarker)) return false;
            WritableTxnMarker other = (WritableTxnMarker) obj;
            if (producerId != other.producerId) return false;
            if (producerEpoch != other.producerEpoch) return false;
            if (transactionResult != other.transactionResult) return false;
            if (this.topics == null) {
                if (other.topics != null) return false;
            } else {
                if (!this.topics.equals(other.topics)) return false;
            }
            if (coordinatorEpoch != other.coordinatorEpoch) return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + ((int) (producerId >> 32) ^ (int) producerId);
            hashCode = 31 * hashCode + producerEpoch;
            hashCode = 31 * hashCode + (transactionResult ? 1231 : 1237);
            hashCode = 31 * hashCode + (topics == null ? 0 : topics.hashCode());
            hashCode = 31 * hashCode + coordinatorEpoch;
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "WritableTxnMarker("
                + "producerId=" + producerId
                + ", producerEpoch=" + producerEpoch
                + ", transactionResult=" + (transactionResult ? "true" : "false")
                + ", topics=" + MessageUtil.deepToString(topics.iterator())
                + ", coordinatorEpoch=" + coordinatorEpoch
                + ")";
        }
        
        public long producerId() {
            return this.producerId;
        }
        
        public short producerEpoch() {
            return this.producerEpoch;
        }
        
        public boolean transactionResult() {
            return this.transactionResult;
        }
        
        public List<WritableTxnMarkerTopic> topics() {
            return this.topics;
        }
        
        public int coordinatorEpoch() {
            return this.coordinatorEpoch;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public WritableTxnMarker setProducerId(long v) {
            this.producerId = v;
            return this;
        }
        
        public WritableTxnMarker setProducerEpoch(short v) {
            this.producerEpoch = v;
            return this;
        }
        
        public WritableTxnMarker setTransactionResult(boolean v) {
            this.transactionResult = v;
            return this;
        }
        
        public WritableTxnMarker setTopics(List<WritableTxnMarkerTopic> v) {
            this.topics = v;
            return this;
        }
        
        public WritableTxnMarker setCoordinatorEpoch(int v) {
            this.coordinatorEpoch = v;
            return this;
        }
    }
    
    static public class WritableTxnMarkerTopic implements Message {
        private String name;
        private List<Integer> partitionIndexes;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partition_indexes", new ArrayOf(Type.INT32), "The indexes of the partitions to write transaction markers for.")
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0
        };
        
        public WritableTxnMarkerTopic(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public WritableTxnMarkerTopic(Struct struct, short _version) {
            fromStruct(struct, _version);
        }
        
        public WritableTxnMarkerTopic() {
            this.name = "";
            this.partitionIndexes = new ArrayList<Integer>();
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 0;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of WritableTxnMarkerTopic");
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
                    throw new RuntimeException("non-nullable field partitionIndexes was serialized as null");
                } else {
                    ArrayList<Integer> newCollection = new ArrayList<Integer>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(_readable.readInt());
                    }
                    this.partitionIndexes = newCollection;
                }
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of WritableTxnMarkerTopic");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                _writable.writeShort((short) _stringBytes.length);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeInt(partitionIndexes.size());
            for (Integer partitionIndexesElement : partitionIndexes) {
                _writable.writeInt(partitionIndexesElement);
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
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of WritableTxnMarkerTopic");
            }
            this._unknownTaggedFields = null;
            this.name = struct.getString("name");
            {
                Object[] _nestedObjects = struct.getArray("partition_indexes");
                this.partitionIndexes = new ArrayList<Integer>(_nestedObjects.length);
                for (Object nestedObject : _nestedObjects) {
                    this.partitionIndexes.add((Integer) nestedObject);
                }
            }
        }
        
        @Override
        public Struct toStruct(short _version) {
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of WritableTxnMarkerTopic");
            }
            TreeMap<Integer, Object> _taggedFields = null;
            Struct struct = new Struct(SCHEMAS[_version]);
            struct.set("name", this.name);
            {
                Integer[] _nestedObjects = new Integer[partitionIndexes.size()];
                int i = 0;
                for (Integer element : this.partitionIndexes) {
                    _nestedObjects[i++] = element;
                }
                struct.set("partition_indexes", (Object[]) _nestedObjects);
            }
            return struct;
        }
        
        @Override
        public int size(ObjectSerializationCache _cache, short _version) {
            int _size = 0, _numTaggedFields = 0;
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of WritableTxnMarkerTopic");
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
                _arraySize += partitionIndexes.size() * 4;
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
            if (!(obj instanceof WritableTxnMarkerTopic)) return false;
            WritableTxnMarkerTopic other = (WritableTxnMarkerTopic) obj;
            if (this.name == null) {
                if (other.name != null) return false;
            } else {
                if (!this.name.equals(other.name)) return false;
            }
            if (this.partitionIndexes == null) {
                if (other.partitionIndexes != null) return false;
            } else {
                if (!this.partitionIndexes.equals(other.partitionIndexes)) return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (name == null ? 0 : name.hashCode());
            hashCode = 31 * hashCode + (partitionIndexes == null ? 0 : partitionIndexes.hashCode());
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "WritableTxnMarkerTopic("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", partitionIndexes=" + MessageUtil.deepToString(partitionIndexes.iterator())
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public List<Integer> partitionIndexes() {
            return this.partitionIndexes;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public WritableTxnMarkerTopic setName(String v) {
            this.name = v;
            return this;
        }
        
        public WritableTxnMarkerTopic setPartitionIndexes(List<Integer> v) {
            this.partitionIndexes = v;
            return this;
        }
    }
}
