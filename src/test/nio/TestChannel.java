package nio;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 一、Channel:用于源节点于目标节点的链接。在Java NIO中负责缓冲区数据的传输
 * Channel本身不存储数据，因此要配合缓冲区进行传输
 *
 * 二、通道的主要实现类
 * Java.nio.channel.Channel接口
 *      |--FileChannel
 *      |--SocketChannel
 *      |ServerSocketChannel
 *      |DatagramChannel
 *
 * 三、获取通道
 * 1. Java针对支持通道的类提供了getChannel()方法
 *      本地IO:
 *      FileInputStream/FileOutPutStream/RandomAccessFile
 *      网络IO:
 *      Socket/ServerSocket/DatagramSocket
 * 2. JDK1.7之后 NIO2 针对各个通道提供了静态方法 open()
 * 3. JDK1.7之后 NIO2 的File工具类的newByteChannel()
 *
 * 四、通道之间的传输
 *  transferTo()
 *  transferFrom()
 *
 *  五、分散（Scatter）与聚集（Gather）
 *  分散读取（Scattering Reads）:将通道中的数据分散到多个缓冲区中，依次、按顺序
 *  聚集写入（Gathering Writes）:将多个缓冲区的数据聚集到通道中
 */
public class TestChannel {

    //利用通道完成文件的复制(非直接缓冲区)
    @Test
    public void test01(){
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;

        FileChannel inChannel= null;
        FileChannel outChannel = null;
        try {
            fileInputStream = new FileInputStream("1.txt");
            fileOutputStream = new FileOutputStream("2.txt");

            //1.获取通道
            inChannel = fileInputStream.getChannel();
            outChannel = fileOutputStream.getChannel();

            //2.分配指定大小的缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            //3.将通道中的数据存入缓冲区
            while (inChannel.read(byteBuffer) != -1){
                byteBuffer.flip(); //切换成读数据模式
                //4.将缓冲区中的数据存写入通道
                outChannel.write(byteBuffer);
                byteBuffer.clear(); //清空缓冲区
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != outChannel){
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != inChannel){
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != fileOutputStream){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != fileInputStream){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //使用直接缓冲区完成文件的复制（内存映射文件）
    @Test
    public void test02(){
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try{
            inChannel = FileChannel.open(Paths.get("1.txt"), StandardOpenOption.READ);
            outChannel = FileChannel.open(Paths.get("2.txt"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);

            //得到内存映射文件，道理跟allocateDirect()得到的buffer一样，在OS的屋里内存中。
            MappedByteBuffer inMapBuffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
            MappedByteBuffer outMapBuffer = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

            //直接对缓冲区进行数据的读写操作
            byte[] dst = new byte[inMapBuffer.limit()];
            inMapBuffer.get(dst);
            outMapBuffer.put(dst);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (null != outChannel){
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != inChannel){
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //通道之间的传输（直接缓冲区）
    @Test
    public void test03(){
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = FileChannel.open(Paths.get("1.txt"), StandardOpenOption.READ);
            outChannel = FileChannel.open(Paths.get("2.txt"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);
            //inChannel.transferTo(0, inChannel.size(), outChannel);
            outChannel.transferFrom(inChannel, 0, inChannel.size());
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (null != outChannel){
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != inChannel){
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //分散和聚集
    @Test
    public void test04(){
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile("1.txt", "rw");
            //1.获取通道
            FileChannel randomAccessFileChannel = randomAccessFile.getChannel();

            //2.分配指定大小的缓冲区
            ByteBuffer buffer1 = ByteBuffer.allocate(10);
            ByteBuffer buffer2 = ByteBuffer.allocate(1024);

            //3.分散读取
            ByteBuffer[] buffers = {buffer1, buffer2};
            randomAccessFileChannel.read(buffers);

            for (ByteBuffer buffer : buffers) {
                buffer.flip();
            }
            System.out.println(new String(buffers[0].array(), buffers[0].capacity()));
            System.out.println("----------------------------");
            System.out.println(new String(buffers[1].array(), buffers[1].capacity()));

            /*
               还剩下聚集部分
             */
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }
}
