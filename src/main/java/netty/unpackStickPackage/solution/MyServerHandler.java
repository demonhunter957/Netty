package netty.unpackStickPackage.solution;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;


public class MyServerHandler extends SimpleChannelInboundHandler<MessageProtocol> {
    private int count;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg){
        int len = msg.getLen();
        System.out.println("len = " + len);
        byte[] content = msg.getContent();
        String message = new String(content, CharsetUtil.UTF_8);
        System.out.println("服务器接收的消息：" + message);
        System.out.println("count = " + ++count);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
}
