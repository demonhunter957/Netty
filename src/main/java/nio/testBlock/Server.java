package nio.testBlock;

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
 */
public class Server {

//    public static void main(String[] args) {
//        ServerSocketChannel serverSocketChannel = null;
//        FileChannel outFileChannel = null;
//        SocketChannel socketChannel = null;
//        try {
//            //1.获取通道
//            serverSocketChannel = ServerSocketChannel.open();
//            outFileChannel = FileChannel.open(Paths.get("2.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
//            //2.绑定连接
//            serverSocketChannel.bind(new InetSocketAddress(9898));
//            //3.获取客户端连接的通道
//            socketChannel = serverSocketChannel.accept();
//            //4.分配缓冲区
//            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
//            //5.接受客户端的数据，并保存到本地
//            while (socketChannel.read(byteBuffer) != -1){
//                byteBuffer.flip();
//                outFileChannel.write(byteBuffer);
//                byteBuffer.clear();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            //6.关闭资源
//            if (null != socketChannel){
//                try {
//                    socketChannel.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (null != outFileChannel){
//                try {
//                    outFileChannel.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if(null != serverSocketChannel){
//                try {
//                    serverSocketChannel.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}
