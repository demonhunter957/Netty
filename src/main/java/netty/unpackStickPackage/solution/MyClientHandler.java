package netty.unpackStickPackage.solution;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MyClientHandler extends SimpleChannelInboundHandler<MessageProtocol> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        //客户端发送5条消息给服务端
        for (int i = 0; i < 5; i++) {
            String message = "hello I am client";
            byte[] content = message.getBytes();
            MessageProtocol messageProtocol = new MessageProtocol();
            messageProtocol.setContent(content);
            messageProtocol.setLen(content.length);
            ctx.writeAndFlush(messageProtocol);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
