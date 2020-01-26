package netty.webSocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;


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
                        //添加加解码器
                        pipeline.addLast("MyHttpServerCodec", new HttpServerCodec());
                        //添加 ChunkedWriteHandler处理器，因为是以块的方式写
                        pipeline.addLast(new ChunkedWriteHandler());
                        /**
                         * http数据在传输过程中是分段传输,这就是为什么浏览器发送大量数据时，会发出多次http请求
                         * HttpObjectAggregator作用就是就是可以将多个请求段聚合
                         */
                        pipeline.addLast(new HttpObjectAggregator(8192));
                        /**
                         * 对于websocket，它的数据是以帧(frame)形式传递;
                         * WebSocketFrame下面有六个子类;
                         * 浏览器发送请求：ws://localhost:9898/hello （表示请求的 uri）;
                         * WebSocketServerProtocolHandler核心功能是将http协议升级为ws协议,保持长连接
                         */
                        pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));
                        //自定义的handler，用来处理业务
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
