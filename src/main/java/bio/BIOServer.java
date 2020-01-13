package bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 使用BIO模型编写服务端，监听6666端口，当有客户端连接时就启动一个线程与之通讯
 * 使用线程池
 * 客户端用telnet
 */
public class BIOServer {

    public static void main(String[] args) throws Exception{

        //创建一个线程池
        ExecutorService executorService = Executors.newCachedThreadPool();
        //创建一个ServerSocket，监听端口6666
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("服务器启动了");
        while (true){
            System.out.println("线程ID： " + Thread.currentThread().getId() + "  线程名字： " + Thread.currentThread().getName()); //这里是一直监听的主线程
            //监听，等待客户端连接
            System.out.println("等待客户端。。。");
            final Socket socket = serverSocket.accept(); //如果没有客户端连接，这里会产生main线程的阻塞
            System.out.println("连接到客户端");
            //有一个客户端连接，就创建一个线程与之通讯
            executorService.execute(() -> handle(socket));
        }
    }
    //和客户端通讯的方法
    public static void handle(Socket socket){
        try {
            //表明每一个新的客户端连接都启用一个新的线程
            System.out.println("线程ID： " + Thread.currentThread().getId() + "线程名字： " + Thread.currentThread().getName());
            byte[] bytes = new byte[1024];
            //通过socket获取输入流
            InputStream inputStream = socket.getInputStream();
            //循环读取客户端的数据
            while (true){
                System.out.println("read...");
                int read = inputStream.read(bytes); //如果客户端没有发送数据，这里也会产生阻塞
                if ( read != 1){
                    System.out.println(new String(bytes, 0, read)); //输出客户端发送的数据
                }else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //关闭socket
            try {
                System.out.println("关闭socket连接");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
