package nio.testBlock2;

/**
 * 用阻塞NIO完成网络通信的服务端
 * 服务端接收成功后需要反馈给客户端
 */
public class Server {

    public static void main(String[] args) {
        //1.获取通道(socketChannel + FileChannel)

        //2.绑定连接

        //3.获取客户端连接通道

        //4.分配缓存区

        //5.接收客户端的数据并保存到本地

        //6.接收服务端的反馈
//            byteBuffer.put("服务端接收到了。。".getBytes());
//            byteBuffer.flip();
//            socketChannel.write(byteBuffer);
        //7.关闭资源
    }
}
