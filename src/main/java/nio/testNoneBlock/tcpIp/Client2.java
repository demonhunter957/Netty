package nio.testNoneBlock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client2 {
    public static void main(String[] args) {

        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            InetSocketAddress inetSocketAddress = new InetSocketAddress(9899);
            if (!socketChannel.connect(inetSocketAddress)){//这里表示客户端连接服务端失败
                while (!socketChannel.finishConnect()){
                    System.out.println("表示没有阻塞。。");
                }
            }
            String string = "hi, server";
            ByteBuffer byteBuffer = ByteBuffer.wrap(string.getBytes());
            socketChannel.write(byteBuffer);
            System.in.read(); //让客户端停在这！这行代码非常重要！
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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
