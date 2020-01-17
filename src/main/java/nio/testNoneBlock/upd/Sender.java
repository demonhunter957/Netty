package nio.testNoneBlock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

/**
 * NIO模式聊天室服务端
 * UDP
 */
public class Sender {

    public static void main(String[] args) {
        DatagramChannel datagramChannel = null;
        try {
            //1.获取通道
            datagramChannel = DatagramChannel.open();
            //2.把通道切换成非阻塞模式
            datagramChannel.configureBlocking(false);
            //3.分配缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            //4.发送数据
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()){
                String s = scanner.next();
                byteBuffer.put(s.getBytes());
                byteBuffer.flip();
                datagramChannel.send(byteBuffer, new InetSocketAddress("localhost", 9898));
                byteBuffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //5.关闭资源
            if (null != datagramChannel){
                try {
                    datagramChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
