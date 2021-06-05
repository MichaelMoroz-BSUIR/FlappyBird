package sample.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MiniClientReceiver implements Runnable {

    private Socket socket;

    private BufferedReader br;

    private Disconnector dis;

    public MiniClientReceiver(Socket socket, Disconnector dis) {
        this.socket = socket;
        this.dis = dis;
    }

    @Override
    public void run() {
        createStream();

        String message;
        try {
            while ((message = br.readLine()) != null) {
                if (message.equals("disconnect")) {
                    System.out.println("The connection to the server has been safely terminated");
                    dis.disconnect();
                    break;
                } else {
                    System.out.print(">>");
                    System.out.println(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        close();
        System.out.println("Chat output has ended");
    }

    private void createStream() {
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            br.close();
            System.out.println("BufferedReader close complete");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
