package netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //通过channel获取pipeline
        ChannelPipeline pipeline = ch.pipeline();
        //向pipeline的最后添加HttpServerCodec handler
        //HttpServerCodec是netty自带的编解码器
        pipeline.addLast("MyHttpServerCodec", new HttpServerCodec());
        //向pipeline添加自定义的handler
        pipeline.addLast("HttpServerHandler", new HttpServerHandler());
    }
}
