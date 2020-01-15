package nio.testNoneBlock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * NIO模式聊天室服务端
 */
public class Server {

    public static void main(String[] args) {
        ServerSocketChannel serverSocketChannel = null;
        SocketChannel socketChannel = null;
        try {
            //1.获取通道
            serverSocketChannel = ServerSocketChannel.open();
            //2.切换成非阻塞模式
            serverSocketChannel.configureBlocking(false);
            //3.绑定连接
            serverSocketChannel.bind(new InetSocketAddress(9898));
            //4.获取选择器
            Selector selector = Selector.open();
            //5.将通道注册到选择器上。第二个参数为选择器监听通道的监听模式
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            //6.轮询的获取选择器上准备就绪的事件
            while(selector.select() > 0){ //表示选择器上至少有一个准备就绪的事件
                //7.获取selector中所有就绪的监听事件
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                //8.获取准备就绪的事件
                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    //9.判断具体是什么事件准备就绪
                    if (selectionKey.isAcceptable()){ //客户端的连接即为accept事件
                        System.out.println("accept from client...");
                        socketChannel = serverSocketChannel.accept();
                        //将客户端的socketChannel切换成非阻塞模式
                        socketChannel.configureBlocking(false);
                        //将该通道注册到选择器上，监听模式为READ
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        System.out.println("accept end..");
                    }else if (selectionKey.isReadable()){ //如果是可读事件，也获取客户端的socketChannel
                        System.out.println("read from client....");
                        socketChannel = (SocketChannel) selectionKey.channel();
                        //读取数据
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        int len;
                        while ((len = socketChannel.read(byteBuffer)) > 0){
                            byteBuffer.flip();
                            System.out.println(new String(byteBuffer.array(),0, len));
                            byteBuffer.clear();
                        }
                        System.out.println("read end....");
                    }
                    //取消selectionKey
                    iterator.remove();
                }
            }
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
