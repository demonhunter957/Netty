package nio.testNoneBlock.tcpIp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * NIO模式聊天室服务端的第二种写法
 * TCP/IP
 */
public class Server2 {
    public static void main(String[] args) {
        ServerSocketChannel serverSocketChannel = null;
        SocketChannel socketChannel = null;
        try{
            serverSocketChannel = ServerSocketChannel.open();
            Selector selector = Selector.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(9899));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("注册后selectionKey的数量" + selector.keys().size()); //1
            while (true){
                if (selector.select(1000) == 0){
                    System.out.println("服务器等待了1秒。。");
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isAcceptable()){
                        socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024)); //第三个参数意思是给当前注册到选择器上的socketChannel分配一个缓冲区
                        System.out.println("有客户端连接后selectionKey的数量" + selector.keys().size());
                    }else if(selectionKey.isReadable()){
                        socketChannel = (SocketChannel) selectionKey.channel();
                        //获取到该socketChannel分配的缓冲区
                        ByteBuffer byteBuffer = (ByteBuffer) selectionKey.attachment();
                        int len;
                        while ((len = socketChannel.read(byteBuffer)) > 0){
                            byteBuffer.flip();
                            System.out.println(new String(byteBuffer.array(),0, len));
                            byteBuffer.clear();
                        }
                        System.out.println("read end..");
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e){
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
