package sample.server;

import sample.Bird;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class MiniServer implements Broadcast, SocketAdder {
    private ServerSocket serverSocket;

    private MiniServerWaiter waiter;
    private HashSet<MiniServerReceiver> receivers;

    public MiniServer() {
        waiter = new MiniServerWaiter(this);
        receivers = new HashSet<>();
    }

    public void start(int port) {
        createServerSocket(port);
        new Thread(waiter).start();
    }

    public void start() {
        start(5000);
    }

    @Override
    public void addSocket(Socket socket) {
        MiniServerReceiver receiver = new MiniServerReceiver(this, socket);
        receivers.add(receiver);
        new Thread(receiver).start();
        displayCurrentUserCount();
    }

    public void displayCurrentUserCount() {
        System.out.println(String.format("Current number of users: %d", receivers.size()));
    }

    @Override
    public void broadcast(float message) {
        for (MiniServerReceiver receiver : receivers) {
            if (receiver != null) {
                receiver.println(message);
            }
        }
    }

    @Override
    public void notiLeftUser(MiniServerReceiver receiver) {
        receivers.remove(receiver);
        displayCurrentUserCount();
    }

    @Override
    public int getUserCount() {
        return receivers.size();
    }

    private void createServerSocket(int port) {
        try {
            serverSocket = new ServerSocket(port);
            waiter.setServerSocket(serverSocket);
            System.out.printf("Start the server! (port number: %d)\n", port);
            System.out.println(serverSocket.getLocalSocketAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
