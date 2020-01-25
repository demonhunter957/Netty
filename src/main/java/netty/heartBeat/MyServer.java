package netty.heartBeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class MyServer {

    public static void main(String[] args) {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO)) // 在bossGroup增加一个日志处理器，级别为INFO
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        /**
                         * IdleStateHandler是netty提供的处理空闲状态的处理器
                         * readerIdleTime:表示多长时间没有从客户端读取数据, 就会发送一个心跳检测包检测是否连接
                         * writerIdleTime:表示多长时间没有写数据给客户端
                         * allIdleTime:表示多长时间没有读写
                         *
                         * 当 IdleStateEvent触发后 ,就会传递给管道的下一个 handler去处理，通过调用(触发)下一个handler的userEventTriggered
                         */
                        pipeline.addLast(new IdleStateHandler(3,5,7, TimeUnit.SECONDS));
                        //加入一个对空闲检测进一步处理的 handler(自定义)
                        pipeline.addLast(new MyServerHandler());
                    }
                });
        System.out.println("server is ready");
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(9898).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
