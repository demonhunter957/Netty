package netty.groupChat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;

public class NettyGroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    //定义一个channel组，管理所有的channel
    //GlobalEventExecutor.INSTANCE 是全局的事件执行器，是一个单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //客户端连接建立时候执行
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        System.out.println("-----------handlerAdded() started------------");
        Channel channel = ctx.channel();

        //将该客户加入聊天的信息推送给其它在线的客户端
        //该方法会将channelGroup中所有的channel遍历，并发送消息。
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 加入聊天 " + sdf.format(new java.util.Date()) + " \n");
        //将当前channel加入到channelGroup
        channelGroup.add(channel);
        System.out.println(channelGroup.size());
        System.out.println("------------handlerAdded() ended-------------");
    }

    //客户端断开连接的时候执行
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        System.out.println("------------handlerRemoved() started-------------");
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 离开了" + sdf.format(new java.util.Date()) + " \n" );
        System.out.println(channelGroup.size());
        System.out.println("------------handlerRemoved() ended-------------");
    }

    //channel处于活动状态的时候执行
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().remoteAddress() + " 上线了~" + sdf.format(new java.util.Date()) + " \n");
    }

    //channel处于非活动状态的时候执行
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().remoteAddress() + " 下线了。。" + sdf.format(new java.util.Date()) + " \n");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.forEach(ch -> {
            if (ch != channel){ //不是当前channel，转发消息
                ch.writeAndFlush("[客户]" + channel.remoteAddress() + " 发送了消息" + msg + "\n");
            }else {
                ch.writeAndFlush("[自己]发送了消息" + msg + "\n");
            }
        });

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
