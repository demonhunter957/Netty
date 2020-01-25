package netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;


/**
 * @author Chris
 * @create 2020-01-22 16:17
 */
public class NettyByteBuf01 {

    public static void main(String[] args) {
        //使用Unpooled.buffer()创建一个ByteBuf
        ByteBuf byteBuf = Unpooled.buffer(10); //这其实就是一个byte[10]
        System.out.println(byteBuf.capacity()); //10
        for (int i = 0; i < byteBuf.capacity(); i++) {
            byteBuf.writeByte(i);
        }
        //Netty的ByteBuf读写转换不需要像ByteBuffer那样用flip()切换
        //原因是ByteBuf维护了一个 readerIndex和writeIndex，再加上capacity，将ByteBuf分成了3个区域
        //0--readIndex,已经读过的区域
        //readerIndex--writeIndex，可读的区域
        //writeIndex--capacity,可写的区域
        // 0 <= readIndex <= writeIndex <= capacity
        for (int i = 0; i < byteBuf.capacity(); i++) {
            System.out.println(byteBuf.readByte()); //每次调用byteBuf.readByte()时，readIndex + 1
        }

    }
}
