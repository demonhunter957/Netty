package netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;


/**
 * @author Chris
 * @create 2020-01-22 16:27
 */
public class NettyByteBuf02 {

    public static void main(String[] args) {

        //使用Unpooled.copiedBuffer()创建ByteBuf
        //注：UTF-8编码中，1个字母占1个byte，1个符号占1个byte,1个汉字占3个byte,1个汉字标点占3个byte,1个空格占1个byte,一个数字占1byte
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello, world!哟1", CharsetUtil.UTF_8);

        System.out.println(byteBuf.getClass()); //io.netty.buffer.UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeHeapByteBuf

        if (byteBuf.hasArray()){
            byte[] bytes = byteBuf.array();
            System.out.println(new String(bytes, CharsetUtil.UTF_8));
        }
    }
}
