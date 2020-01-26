package netty.webSocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.time.LocalDateTime;

//这里TextWebSocketFrame表示一个文本帧(frame)
public class MyServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {

        System.out.println("服务器收到消息 " + msg.text());
        //返回消息给浏览器
        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器时间" + LocalDateTime.now() + " " + msg.text()));
    }

    //web客户端连接时触发的方法
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        System.out.println("-----handlerAdded() starts---");
        //id 表示channel唯一的值，LongText是唯一的，ShortText有可能重复
        System.out.println("channel的longText Id: " + ctx.channel().id().asLongText());
        System.out.println("channel的shortText Id: " + ctx.channel().id().asShortText());
        System.out.println("-----handlerAdded() ends---");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx){
        System.out.println("-----handlerRemoved() starts---");
        System.out.println("channel的longText Id: " + ctx.channel().id().asLongText());
        System.out.println("-----handlerRemoved() ends---");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("--------channelActive() invoked");
    }
}

