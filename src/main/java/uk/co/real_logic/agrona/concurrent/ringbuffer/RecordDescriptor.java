/*
 * Copyright 2014 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.agrona.concurrent.ringbuffer;

import static uk.co.real_logic.agrona.BitUtil.SIZE_OF_INT;

/**
 * Description of the record structure for message framing in the a {@link RingBuffer}.
 */
public class RecordDescriptor
{
    /**
     * Header length made up of fields for length, type, and then the encoded message.
     * <p>
     * Writing of a positive record length signals the message recording is complete.
     * <pre>
     *   0                   1                   2                   3
     *   0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *  |R|                         Length                              |
     *  +-+-------------------------------------------------------------+
     *  |                            Type                               |
     *  +---------------------------------------------------------------+
     *  |                       Encoded Message                        ...
     * ...                                                              |
     *  +---------------------------------------------------------------+
     * </pre>
     */
    public static final int HEADER_LENGTH = SIZE_OF_INT * 2;

    /**
     * Alignment as a multiple of bytes for each record.
     */
    public static final int ALIGNMENT = HEADER_LENGTH;

    /**
     * The offset from the beginning of a record at which the message length field begins.
     *
     * @param recordOffset beginning index of the record.
     * @return offset from the beginning of a record at which the type field begins.
     */
    public static long lengthOffset(final long recordOffset)
    {
        return recordOffset;
    }

    /**
     * The offset from the beginning of a record at which the message type field begins.
     *
     * @param recordOffset beginning index of the record.
     * @return offset from the beginning of a record at which the type field begins.
     */
    public static long typeOffset(final long recordOffset)
    {
        return recordOffset + SIZE_OF_INT;
    }

    /**
     * The offset from the beginning of a record at which the encoded message begins.
     *
     * @param recordOffset beginning index of the record.
     * @return offset from the beginning of a record at which the encoded message begins.
     */
    public static long encodedMsgOffset(final long recordOffset)
    {
        return recordOffset + HEADER_LENGTH;
    }

    /**
     * Make a 64-bit header from the length and message type id.
     *
     * @param length    length of the record
     * @param msgTypeId of the message stored in the record
     * @return the fields combined into a long.
     */
    public static long makeHeader(final long length, final int msgTypeId)
    {
        return ((msgTypeId & 0xFFFF_FFFFL) << 32) | (length & 0xFFFF_FFFFL);
    }

    /**
     * Extract the record length field from a word representing the header.
     *
     * @param header containing both fields.
     * @return the length field from the header.
     */
    public static int recordLength(final long header)
    {
        return (int)header;
    }

    public static int messageTypeId(final long header)
    {
        return (int)(header >>> 32);
    }

    /**
     * Check that and message id is in the valid range.
     *
     * @param msgTypeId to be checked.
     * @throws IllegalArgumentException if the id is not in the valid range.
     */
    public static void checkTypeId(final int msgTypeId)
    {
        if (msgTypeId < 1)
        {
            final String msg = String.format("Message type id must be greater than zero, msgTypeId=%d", msgTypeId);
            throw new IllegalArgumentException(msg);
        }
    }
}
