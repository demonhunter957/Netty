package netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    //读取客户端发送的消息的方法
    /*
    ctx:上下文对象，包含channel，pipeLine，地址等很多信息
    msg:服务端发来的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("ctx = " + ctx);
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("客户端发来的消息是：" + byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端的地址：" + ctx.channel().remoteAddress());
    }

    //读取客户端消息完毕后调用的方法
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //给客户端反馈消息
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端！", CharsetUtil.UTF_8));
    }

    //处理异常
    //一般处理异常的方法是关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
