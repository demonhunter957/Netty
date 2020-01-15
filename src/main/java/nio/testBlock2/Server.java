package nio.testBlock2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 用阻塞NIO完成网络通信的服务端
 * 服务端接收成功后需要反馈给客户端
 */
public class Server {

    public static void main(String[] args) {
        ServerSocketChannel serverSocketChannel = null;
        FileChannel fileChannel = null;
        SocketChannel socketChannel = null;
        try {
            //1.获取通道(serverSocketChannel + FileChannel)
            serverSocketChannel = ServerSocketChannel.open();
            fileChannel = FileChannel.open(Paths.get("2.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            //2.绑定连接
            serverSocketChannel.bind(new InetSocketAddress(9898));
            //3.获取客户端连接通道
            socketChannel = serverSocketChannel.accept();
            //4.分配缓存区
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            //5.接收客户端的数据并保存到本地
            while (socketChannel.read(byteBuffer) != -1){
                byteBuffer.flip();
                fileChannel.write(byteBuffer);
                byteBuffer.clear();
            }
            //6.反馈给客户端
            byteBuffer.put("服务端接收到了。。".getBytes());
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //7.关闭资源
            if(null != socketChannel){
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != fileChannel){
                try {
                    fileChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != serverSocketChannel){
                try {
                    serverSocketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
