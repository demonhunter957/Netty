package nio.testNoneBlock.groupChat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * NIO群聊系统客户端
 * 客户端可以发送消息以及接收其他客户端来的消息
 */
public class GroupChatClient {

    public static final String SERVER_IP = "127.0.0.1";

    public static final int PORT = 9898;

    private SocketChannel socketChannel;

    private Selector selector;

    private String userName;

    public static void main(String[] args) {
        GroupChatClient groupChatClient = new GroupChatClient();

        //启动一个线程发数据给服务端
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                groupChatClient.sendInfo(line);
            }
        }).start();

        //启动一个线程循环读取其他服务器发来的数据（每三秒读取一次）
        new Thread(() -> {
            while (true){
                groupChatClient.readInfo();
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public GroupChatClient() {
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open(new InetSocketAddress(SERVER_IP, PORT));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            userName = socketChannel.getLocalAddress().toString().substring(1);
            System.out.println(userName + "is ready");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //向服务器发送消息
    public void sendInfo(String message){
        message = userName + "说：" + message;
        try {
            socketChannel.write(ByteBuffer.wrap(message.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取其他客户端发来的消息
    public void readInfo(){
        try {
            if(selector.select() > 0){
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if (key.isReadable()){
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer bytebuffer = ByteBuffer.allocate(1024);
                        channel.read(bytebuffer);
                        System.out.println(new String(bytebuffer.array()).trim());
                        bytebuffer.clear();
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
