package sample.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MiniServerWaiter implements Runnable {

    private SocketAdder server;
    private ServerSocket serverSocket;

    public MiniServerWaiter(SocketAdder server) {
        this.server = server;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {

        while (!serverSocket.isClosed()) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                System.out.println("accept complete");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (socket != null) {
                server.addSocket(socket);
            }
        }
    }
}
