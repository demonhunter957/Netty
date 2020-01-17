package nio.testNoneBlock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Scanner;

/**
 * NIO模式聊天室的客户端
 * TCP/IP
 */
public class Client {

    public static void main(String[] args) {
        SocketChannel socketChannel = null;
        try {
            //1.获取通道
            socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 9898));
            //2.切换成非阻塞模式
            socketChannel.configureBlocking(false);
            //3.分配缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
            //4.发送数据
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()){
                String s = scanner.next();
                if (s.equals("exit")){
                    break;
                }
                byteBuffer.put((new Date().toString() + "\n" + s).getBytes());
                byteBuffer.flip();
                socketChannel.write(byteBuffer);
                byteBuffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //4.关闭资源
            if (null != socketChannel){
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
