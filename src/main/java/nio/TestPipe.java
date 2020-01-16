package nio;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

/**
 * Java NIO管道是两个线程之间单向数据连接
 * Pipe有一个source和一个sink通道，数据会被写到sink通道，从source通道读取
 */
public class TestPipe {

    public static void main(String[] args) {

        try {
            //获取管道
            Pipe pipe = Pipe.open();
            //分配缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            //开启一个线程，将缓冲区数据写入管道
            new Thread(() -> {
                Pipe.SinkChannel sinkChannel = null;
                try {
                    sinkChannel = pipe.sink();
                    byteBuffer.put("通过单向管道发送数据。。".getBytes());
                    byteBuffer.flip();
                    sinkChannel.write(byteBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (null != sinkChannel){
                        try {
                            sinkChannel.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            //开启另一个线程，读取缓冲区的数据
            new Thread(() -> {
                Pipe.SourceChannel sourceChannel = null;
                try{
                    sourceChannel = pipe.source();
                    byteBuffer.flip();
                    int len = sourceChannel.read(byteBuffer);
                    System.out.println(new String(byteBuffer.array(), 0, len));
                }catch (IOException e){
                    e.printStackTrace();
                }finally {
                    if (null != sourceChannel){
                        try {
                            sourceChannel.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
