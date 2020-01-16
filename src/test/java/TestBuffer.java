import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * 一、缓冲区：在Java NIO中负责数据的存储。缓冲区就是数组。用于存储不同类型数据
 * 根据数据类型的不同（boolean除外）提供了相应的缓冲区（剩下7种基本数据类型）
 * 通过allocate()静态方法获取缓冲区
 *
 * 二、缓冲区的四个核心属性
 * 1. capacity: 表示缓冲区的最大存储数据的容量。一旦声明不能改变。
 * 2. limit: 界限，表示缓冲区中可以操作数据的容量大小。limit后面的数据不能被读写
 * 3. position: 当前操作的位置
 * 4. mark: 记录当前position的位置。可以通过reset()恢复到mark位置
 * 0 <= position <= limit <= capacity
 *
 * 三、直接缓冲区与非直接缓冲区
 * 非直接缓冲区：将缓冲区建立在JVM的内存中。通过allocate()方法分配。
 * 直接缓冲区：将缓冲区建立在OS的物理内存中。通过allocateDirect()方法分配。可以提高效率
 */
public class TestBuffer {

    @Test
    public void test03(){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        System.out.println(byteBuffer.isDirect()); // true
    }

    @Test
    public void test02(){
        String string = "abcde";
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(string.getBytes());
        byteBuffer.flip();

        byte[] dst = new byte[byteBuffer.limit()];
        byteBuffer.get(dst,0, 2);
        System.out.println(byteBuffer.position()); //2
        System.out.println(new String(dst, 0, 2)); //ab

        //mark()标记position的位置
        byteBuffer.mark();
        byteBuffer.get(dst,0, 2);
        System.out.println(byteBuffer.position()); //4
        System.out.println(new String(dst, 0, 2)); //cd

        //reset(),恢复到标记的position位置
        byteBuffer.reset();
        System.out.println(byteBuffer.position()); //2
        byteBuffer.get(dst,0, 2);
        System.out.println(new String(dst, 0, 2)); //cd
    }

    @Test
    public void test01(){
        String string = "abcde";

        //分配一个指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        System.out.println(byteBuffer.capacity()); //1024
        System.out.println(byteBuffer.limit()); //1024
        System.out.println(byteBuffer.position()); //0

        //利用put()存入到缓冲区中
        byteBuffer.put(string.getBytes());
        System.out.println(byteBuffer.capacity()); //1024
        System.out.println(byteBuffer.limit()); //1024
        System.out.println(byteBuffer.position()); //5

        //切换到读取数据的模式
        byteBuffer.flip(); //调用flip(),limit的值为上一个position, position的值变为0
        System.out.println(byteBuffer.capacity()); //1024
        System.out.println(byteBuffer.limit()); //5
        System.out.println(byteBuffer.position()); //0

        //利用get()读取数据
        byte[] dst = new byte[byteBuffer.limit()];
        byteBuffer.get(dst);
        System.out.println(new String(dst, 0, dst.length));
        System.out.println(byteBuffer.capacity()); //1024
        System.out.println(byteBuffer.limit()); //5
        System.out.println(byteBuffer.position()); //5

        //rewind()，可重复读数据
        byteBuffer.rewind(); //调用rewind() position的值被设为0
        System.out.println(byteBuffer.capacity()); //1024
        System.out.println(byteBuffer.limit()); //5
        System.out.println(byteBuffer.position()); //0

        //clear()清空缓冲区，回到最初状态。但是缓冲区里面的数据依然存在，但是处于“被遗忘”状态
        byteBuffer.clear();
        System.out.println(byteBuffer.capacity()); //1024
        System.out.println(byteBuffer.limit()); //1024
        System.out.println(byteBuffer.position()); //0
        System.out.println((char)byteBuffer.get());
    }


}
