package netty.unpackStickPackage.solution;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MyMessageEncoder extends MessageToByteEncoder<MessageProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageProtocol msg, ByteBuf out) {
        System.out.println("MyMessageEncoder encode() invoked.....");
        out.writeInt(msg.getLen());
        out.writeBytes(msg.getContent());
    }
}
