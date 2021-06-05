package sample.client;

import sample.Game;
import sample.protocol.Protocol;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class MiniClient implements Disconnector {

    private static final boolean def = false;

    private Socket socket;
    private ObjectOutputStream objectStream = null;
    private Scanner scanner;

    private boolean isAlive;

    public MiniClient() {
        scanner = new Scanner(System.in);
    }

    public void start() {
        connect();
        if (socket != null) {
            runReceiver();
            try {
                play();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void runReceiver() {
        new Thread(new MiniClientReceiver(socket, this)).start();
    }

    private void connect() {
        final String ipAddress;
        final int port;
        if (def) {
            ipAddress = "127.0.0.1";
            port = 5001;
        } else {
            /*System.out.println("Please enter the server address to connect.");
            System.out.print(">>");*/
            ipAddress = "192.168.100.8";
            /*System.out.println("Please enter the port number of the server to connect.");
            System.out.print(">>");*/
            port = 5001;
        }

        System.out.printf("%s:%d Connect to the server.%n", ipAddress, port);

        try {
            socket = new Socket(ipAddress, port);
            System.out.println("Server connection is complete.");

            isAlive = true;
            objectStream = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void play() throws IOException {

        Protocol p = new Protocol();

        System.out.println("Start chatting. Enter \"exit\" to exit");
        while (isAlive) {
            float message = Game.progress;

            p.setMessage(message);

            objectStream.writeObject(p);
            objectStream.reset();
            objectStream.flush();
        }
        System.out.println("Chat input has ended");
    }

    private void close() {
        try {
            objectStream.close();
            System.out.println("ObjectOutputStream close complete");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        close();
    }
}
