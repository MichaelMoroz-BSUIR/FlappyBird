package sample.server;

import sample.Controller;
import sample.Game;
import sample.protocol.Protocol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class MiniServerReceiver implements Runnable {

    private Broadcast server;
    private Socket socket;

    private PrintWriter printWriter;
    private ObjectInputStream objectInputStream;

    private String nickName = null;


    public MiniServerReceiver(Broadcast server, Socket socket) {
        this.server = server;
        this.socket = socket;
        try {
            printWriter = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void println(float message) {
        printWriter.println(message);
    }

    @Override
    public void run() {
        float message = 0;
        Object obj;
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());

            while ((obj = objectInputStream.readObject()) != null) {
                Protocol p = (Protocol) obj;
                if (Controller.isOnlineGame) {
                    message = p.message;
                }
                /*if (message.equals("exit")) {
                    nickName = p.nickName;
                    socket.close();
                    break;
                } else if (message != 0) {
                    nickName = p.nickName;
                    message = String.format("[%s]: %s", nickName, message);
                    System.out.println(message);
                    server.broadcast(message);
                }*/
                System.out.println(message);
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.broadcast(message);
            System.out.println(message);
            server.notiLeftUser(this);
        }
    }

    private void close() throws IOException {
        printWriter.close();
        System.out.println("PrintWriter close complete");
        objectInputStream.close();
        System.out.println("ObjectInputStream close complete");
    }
}
