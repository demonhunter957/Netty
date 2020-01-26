package netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        System.out.println("-----------------channelRegistered() starts");
        System.out.println(ctx.channel().hashCode());
        System.out.println("-----------------channelRegistered() ends");
    }

    //当通道就绪的时候触发的方法
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("---------------channelActive() starts---------------" + System.currentTimeMillis());
        System.out.println(ctx.channel().hashCode());
        System.out.println("channel的类型：" + ctx.channel().getClass());
        //向通道中发送数据
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello server...", CharsetUtil.UTF_8));
        System.out.println("---------------channelActive() ends---------------");
    }

    //当通道有读取事件时调用的方法
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        System.out.println("-----------------------channelRead() starts-----------");
        System.out.println(ctx.channel().hashCode());
        ByteBuf byteBuf = (ByteBuf)msg;
        System.out.println("服务器回复的消息是： " + byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("服务器的地址是： " + ctx.channel().remoteAddress());
        System.out.println("-----------------------channelRead() ends-----------");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
