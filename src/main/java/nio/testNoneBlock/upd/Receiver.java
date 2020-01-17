package nio.testNoneBlock.upd;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 * NIO模式聊天室服务端
 * UDP
 */
public class Receiver {

    public static void main(String[] args) {
        DatagramChannel datagramChannel = null;
        Selector selector = null;
        try {
            datagramChannel = DatagramChannel.open();
            datagramChannel.configureBlocking(false);
            datagramChannel.bind(new InetSocketAddress(9898));
            selector = Selector.open();
            datagramChannel.register(selector, SelectionKey.OP_READ);
            while (selector.select() > 0){
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isReadable()){
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        datagramChannel.receive(byteBuffer);
                        byteBuffer.flip();
//                        System.out.println(byteBuffer.toString());
                        System.out.println(new String(byteBuffer.array(), 0 , byteBuffer.limit()));
                        byteBuffer.clear();
                    }
                }
                iterator.remove();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != selector){
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != datagramChannel){
                try {
                    datagramChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
