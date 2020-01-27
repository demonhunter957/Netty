package netty.unpackStickPackage.problem;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.UUID;

public class MyServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private int count;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg){
        System.out.println("msg可读取的字节数： " + msg.readableBytes()); //因为拆包粘包的问题，这个值的大小不确定
        byte[] buffer= new byte[msg.readableBytes()];
        msg.readBytes(buffer);

        //将buffer转成字符串
        String message = new String(buffer, CharsetUtil.UTF_8);
        //输出
        /*
        这里服务端接收的message可能被拆成了好几个包。原因是服务端一次读到的字节数是不确定的。
         */
        System.out.println("服务器接收消息" + message );
        System.out.println("count =" + ++count);
        //返回一个随机UUID给客户端
        ByteBuf response = Unpooled.copiedBuffer(UUID.randomUUID().toString() + "\t", CharsetUtil.UTF_8);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
}
