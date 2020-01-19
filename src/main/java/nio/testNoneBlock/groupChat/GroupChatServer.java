package nio.testNoneBlock.groupChat;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * NIO群聊系统服务端
 * 服务端可以检测客户端上线、离线，并转发消息
 *
 * 这是一种单Reactor单线程模式，Selector就相当于Reactor
 * 单Reactor单线程模式，Reactor跟Handler在一个线程，如果有高并发，肯定会造成阻塞
 */
public class GroupChatServer {

    private ServerSocketChannel serverSocketChannel;

    private Selector selector;

    public static final int PORT = 9898;

    public static void main(String[] args) {
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }

    public GroupChatServer() {

        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
            serverSocketChannel.configureBlocking(false);
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //监听
    public void listen(){
        SocketChannel socketChannel = null;
        System.out.println("监听的线程 " + Thread.currentThread().getName());
        try{
            while (true){
                int select = selector.select(2000);
                if (select > 0){ //有事件处理
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        SelectionKey selectionKey = iterator.next();
                        if (selectionKey.isAcceptable()){
                            socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                            //提示:client上线
                            System.out.println(socketChannel.getRemoteAddress() + " 上线了...");
                        }else if(selectionKey.isReadable()){
                            //专门处理读的操作
                            read(selectionKey);
                        }
                        iterator.remove();                    }
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

    //读取client发来的数据
    private void read(SelectionKey selectionKey){
        SocketChannel socketChannel = null;
        try{
            socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int len;
            StringBuffer stringBuffer = new StringBuffer();
            while ((len = socketChannel.read(byteBuffer)) > 0){
                byteBuffer.flip();
                stringBuffer.append(new String(byteBuffer.array(),0, len));
                byteBuffer.clear();
            }
            System.out.println("来自客户端: " + socketChannel.getRemoteAddress() + "的消息：" + stringBuffer.toString());
            //转发消息给其他client
            sendToOtherClient(stringBuffer.toString(), socketChannel);
        }catch (IOException e){
            try {
                System.out.println(socketChannel.getRemoteAddress() + "已关闭");
                selectionKey.cancel();
                socketChannel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }finally {
            if (null != socketChannel){
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //转发消息给其他客户端
    private void sendToOtherClient(String message, SocketChannel self) throws IOException{
        System.out.println("服务器开始转发消息");
        System.out.println("转发消息的线程： " + Thread.currentThread().getName());
        for (SelectionKey key : selector.keys()) {
            //通过key获取相应的socketChannel
            Channel targetChannel = key.channel();
            if (targetChannel instanceof SocketChannel && targetChannel != self){ //排除自己
                ByteBuffer byteBuffer = ByteBuffer.wrap(message.getBytes());
                ((SocketChannel) targetChannel).write(byteBuffer);
            }
        }
    }
}
