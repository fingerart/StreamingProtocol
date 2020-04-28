package io.chengguo.streaming.rtsp;

import androidx.annotation.IntRange;

import java.nio.ByteBuffer;
import java.util.Arrays;

import io.chengguo.streaming.transport.IMessage;
import io.chengguo.streaming.utils.Bits;

/**
 *
 */
public class RtspPacket implements IMessage {
    private int magic = 0x24;
    private int channel;
    private long length;
    private IMessage[] iMessages;

    public RtspPacket(@IntRange(from = 0x00, to = 0x01) int channel, IMessage[] iMessages) {
        this.channel = channel;
        this.iMessages = iMessages;
    }

    @Override
    public byte[] toRaw() {

        byte[] messages = new byte[0];
        for (IMessage iMessage : iMessages) {
            byte[] bytes = iMessage.toRaw();
            int oriLength = messages.length;
            messages = Arrays.copyOf(messages, messages.length + bytes.length);
            System.arraycopy(bytes, 0, messages, oriLength, bytes.length);
        }
        length = messages.length;

        ByteBuffer buffer = ByteBuffer.wrap(new byte[(int) (length + 4)]);
        buffer.put((byte) (magic & 0xFF));
        buffer.put((byte) (channel & 0xFF));
        buffer.put(Bits.longToByteArray(length, 2));
        buffer.put(messages);

        return buffer.array();
    }
}
