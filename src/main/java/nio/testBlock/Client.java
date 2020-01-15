package nio.testBlock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 用阻塞NIO完成网络通信的客户端
 * 客户端需要收到服务端接收成功的反馈
 */
public class Client {
    public static void main(String[] args) {

        SocketChannel socketChannel = null;
        FileChannel fileChannel = null;
        try {
            //1.获取通道(socketChannel + FileChannel)
            socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
            fileChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
            //2.分配缓存区
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            //3.读取本地文件并发送到服务器
            while (fileChannel.read(byteBuffer) != -1){
                byteBuffer.flip();
                socketChannel.write(byteBuffer);
                byteBuffer.clear();
            }
            socketChannel.shutdownOutput(); //告诉服务端已经发送数据完成

//            //4.接收服务端的反馈
//            int len;
//            while ((len=socketChannel.read(byteBuffer))!= -1){
//                byteBuffer.flip();
//                System.out.println(new String(byteBuffer.array(),0,len));
//                byteBuffer.clear();
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //5.关闭资源
            if (null != fileChannel){
                try {
                    fileChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
