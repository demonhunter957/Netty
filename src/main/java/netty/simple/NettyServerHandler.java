package netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    //读取客户端发送的消息的方法
    /*
    ctx:上下文对象，包含channel，pipeLine，地址等很多信息
    msg:服务端发来的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("--------------channelRead() starts-----------------");
        System.out.println(ctx.channel().hashCode());
        System.out.println("ctx的类型： " + ctx.getClass()); //io.netty.channel.DefaultChannelHandlerContext
        Channel channel = ctx.channel();
        ChannelPipeline pipeline = ctx.pipeline(); //pipeline本质是个双向链表，负责出栈入栈。
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("客户端发来的消息是：" + byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端的地址：" + channel.remoteAddress());
        System.out.println("--------------channelRead() ends-----------------");
    }

    //读取客户端消息完毕后调用的方法
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        System.out.println("---------------channelReadComplete() starts------------");
        System.out.println(ctx.channel().hashCode());
        //给客户端反馈消息
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端！", CharsetUtil.UTF_8));
        System.out.println("---------------channelReadComplete() ends------------");
    }

    //异常发生时调用的方法，一般处理异常的方法是关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx)  {
        System.out.println("--------channelRegistered() starts------------");
        System.out.println(ctx.channel().hashCode());
        System.out.println(ctx.channel().getClass()); //io.netty.channel.socket.nio.NioSocketChannel
        System.out.println("--------channelRegistered() ends------------");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("---------channelActive() starts------------" + System.currentTimeMillis());
        System.out.println(ctx.channel().hashCode());
        System.out.println(ctx.channel().getClass()); //io.netty.channel.socket.nio.NioSocketChannel
        System.out.println("---------channelActive() ends------------");
    }
}
