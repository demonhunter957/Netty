package netty.simple;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author Chris
 * @create 2020-01-20 18:26
 * 任务队列添加Task方式一：
 *  用户程序自定义的普通任务
 *
 * 任务队列添加Task方式二：
 *  用户自定义的普通任务
 */
public class NettyServerHandler2 extends ChannelInboundHandlerAdapter {

    //读取客户端发送的消息的方法
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        //模拟一个耗时很长的读取客户端数据读取，休息10秒
//        //因为在这休息了10秒，客户端跟服务端在这里都被阻塞了
//        TimeUnit.SECONDS.sleep(10);
//        //给客户端返回数据
//        ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端1111", CharsetUtil.UTF_8));
//        System.out.println("server continued...");

        //解决方法：异步执行，提交该channel对应的NioEventLoop的taskQueue中
        //这种解决方式适用于pipeline的handler中有长时间的操作
        ctx.channel().eventLoop().execute(() ->{
            try {
                TimeUnit.SECONDS.sleep(10);
                ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端1111", CharsetUtil.UTF_8));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println("server continued..."); //这句话能马上执行，服务端并没有阻塞

        ctx.channel().eventLoop().execute(() ->{
            try {
                TimeUnit.SECONDS.sleep(20); //注：这里其实是等待了30秒（上面的10 + 这里的20），原因是在一个线程中
                ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端3333", CharsetUtil.UTF_8));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        //用户自定义定时任务，该任务提交到scheduleTaskQueue中
        ctx.channel().eventLoop().schedule(() -> {
            ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端4444", CharsetUtil.UTF_8));
        }, 5, TimeUnit.SECONDS);

        //如果在下一行打断点会发现: ctx.pipeline.channel.eventLoop中的taskQueue的size=2
        System.out.println("reach last..."); //这句话也能马上执行
    }

    //读取客户端数据完毕后调用的方法
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端2222", CharsetUtil.UTF_8));
    }
}
