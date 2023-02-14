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

package org.apache.kafka.common.protocol;

import org.apache.kafka.common.utils.Utils;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.UUID;

public final class MessageUtil {
    public static final UUID ZERO_UUID = new UUID(0L, 0L);

    /**
     * Copy a byte buffer into an array.  This will not affect the buffer's
     * position or mark.
     */
    public static byte[] byteBufferToArray(ByteBuffer buf) {
        byte[] arr = new byte[buf.remaining()];
        int prevPosition = buf.position();
        try {
            buf.get(arr);
        } finally {
            buf.position(prevPosition);
        }
        return arr;
    }

    public static String deepToString(Iterator<?> iter) {
        StringBuilder bld = new StringBuilder("[");
        String prefix = "";
        while (iter.hasNext()) {
            Object object = iter.next();
            bld.append(prefix);
            bld.append(object.toString());
            prefix = ", ";
        }
        bld.append("]");
        return bld.toString();
    }


    public static ByteBuffer toVersionPrefixedByteBuffer(final short version, final Message message) {
        ObjectSerializationCache cache = new ObjectSerializationCache();
        int messageSize = message.size(cache, version);
        ByteBufferAccessor bytes = new ByteBufferAccessor(ByteBuffer.allocate(messageSize + 2));
        bytes.writeShort(version);
        message.write(bytes, cache, version);
        bytes.flip();
        return bytes.buffer();
    }

    public static byte[] toVersionPrefixedBytes(final short version, final Message message) {
        ByteBuffer buffer = toVersionPrefixedByteBuffer(version, message);
        // take the inner array directly if it is full with data
        if (buffer.hasArray() &&
                buffer.arrayOffset() == 0 &&
                buffer.position() == 0 &&
                buffer.limit() == buffer.array().length) return buffer.array();
        else return Utils.toArray(buffer);
    }
}
