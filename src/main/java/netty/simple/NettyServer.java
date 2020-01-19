package netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 *
 */
public class NettyServer {

    public static void main(String[] args) {

        //1.创建BossGroup和WorkerGroup线程组
        //BossGroup只处理连接请求，WorkerGroup处理业务
        //两个都是无限循环
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        //2.创建服务端的启动对象，并配置参数
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class) //使用NioServerSocketChannel作为服务器通道的实现
                .option(ChannelOption.SO_BACKLOG, 128) //设置线程队列等待连接的个数
                .childOption(ChannelOption.SO_KEEPALIVE, true) //设置保持活动连接状态
                .childHandler(new ChannelInitializer<SocketChannel>() { //创建一个通道初始化对象
                    //给workerGroup的pipeLine设置处理器
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new NettyServerHandler()); //向管道的最后添加处理器
                    }
                });
        System.out.println("server is ready...");
        //3.绑定端口（启动服务）并且同步
        ChannelFuture sync = null;
        try {
            sync = serverBootstrap.bind(9898).sync();
            //4.对关闭的通道进行监听
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
