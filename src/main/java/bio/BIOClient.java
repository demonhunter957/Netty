package bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;

/**
 * @author Chris
 * @create 2020-01-17 14:14
 */
public class BIOClient {

    public static void main(String[] args) {
        Socket socket = null;
        OutputStream outputStream = null;
        try {
            socket = new Socket("localhost", 6666);
            outputStream = socket.getOutputStream();
            outputStream.write(("hello server " + new Date().toString()).getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != outputStream){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != socket){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
