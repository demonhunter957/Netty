package nio;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.SortedMap;

/**
 * 一、Channel:用于源节点于目标节点的链接。在Java NIO中负责缓冲区数据的传输
 * Channel本身不存储数据，因此要配合缓冲区进行传输
 *
 * 二、通道的主要实现类
 * Java.nio.channel.Channel接口
 *      |--FileChannel
 *      |--SocketChannel
 *      |--ServerSocketChannel
 *      |--DatagramChannel
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
 *
 *  六、字符集Charset
 *  编码：字符串-> 字节数组
 *  解码：字节数组-> 字符串
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
        FileChannel randomAccessFileChannel = null;
        RandomAccessFile randomAccessFile1 = null;
        FileChannel randomAccessFileChannel1 = null;
        try {
            randomAccessFile = new RandomAccessFile("1.txt", "rw");
            //1.获取通道
            randomAccessFileChannel = randomAccessFile.getChannel();

            //2.分配指定大小的缓冲区
            ByteBuffer buffer1 = ByteBuffer.allocate(100);
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

            //4.聚集写入
            randomAccessFile1 = new RandomAccessFile("2.txt", "rw");
            randomAccessFileChannel1 = randomAccessFile1.getChannel();
            randomAccessFileChannel1.write(buffers);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != randomAccessFile1){
                try {
                    randomAccessFile1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if ( null != randomAccessFileChannel1){
                try {
                    randomAccessFileChannel1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != randomAccessFileChannel){
                try {
                    randomAccessFileChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != randomAccessFile){
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Charset
    @Test
    public void test05(){
        SortedMap<String, Charset> map = Charset.availableCharsets();
        for (String s : map.keySet()) {
            System.out.println("key="+ s + " value=" + map.get(s));
        }
    }

    //编码 解码
    @Test
    public void test06(){
        Charset charset = Charset.forName("GBK");
        //获取编码器
        CharsetEncoder charsetEncoder = charset.newEncoder();
        //获取解码器
        CharsetDecoder charsetDecoder = charset.newDecoder();
        //分配char缓冲区
        CharBuffer charBuffer = CharBuffer.allocate(1024);
        charBuffer.put("这是一串中文");
        //切换成读模式
        charBuffer.flip();
        try {
            //编码
            ByteBuffer byteBuffer = charsetEncoder.encode(charBuffer);
            for (int i = 0; i < byteBuffer.limit(); i++) {
                System.out.println(byteBuffer.get());
            }
            //解码
            byteBuffer.flip();
            CharBuffer charBuffer1 = charsetDecoder.decode(byteBuffer);
            System.out.println(charBuffer1.toString());
        } catch (CharacterCodingException e) {
            e.printStackTrace();
        }
    }
}
