package netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {


    public static void main(String[] args) {

        //1.创建BossGroup和WorkerGroup线程组
        //BossGroup只处理连接请求，WorkerGroup处理业务
        //bossGroup和workerGroup默认（构造器没有参数）含有的子线程（NioEventLoop）个数为cpu核数*2
        //两个都是无限循环
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //EventLoopGroup bossGroup2 = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        //2.创建服务端的启动对象，并配置参数
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class) //使用NioServerSocketChannel作为服务器通道的实现
                .option(ChannelOption.SO_BACKLOG, 128) //设置线程队列等待连接的个数
                .childOption(ChannelOption.SO_KEEPALIVE, true) //设置保持活动连接状态
                .childHandler(new ChannelInitializer<SocketChannel>() { //创建一个通道初始化对象,childHandler对应workerGroup
                                                                        //handler对应bossGroup
                    //给workerGroup的pipeLine设置处理器
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new NettyServerHandler()); //向管道的最后添加处理器
                    }
                });
        System.out.println("server is ready...");
        try {
            //3.绑定端口（启动服务）并且同步
            ChannelFuture sync = serverBootstrap.bind(9898).sync();
            //注册监听器
            sync.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (sync.isSuccess()){
                        System.out.println("绑定端口成功");
                    }else {
                        System.err.println("绑定端口失败");
                    }
                }
            });
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
