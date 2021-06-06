package connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

public class SocketConnection extends ConnectionImpl {
    public static SocketConnection client(Socket socket) throws IOException {
        Objects.requireNonNull(socket);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        return new SocketConnection(socket, in, out);
    }
    public static SocketConnection server(Socket socket) throws IOException {
        Objects.requireNonNull(socket);
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        return new SocketConnection(socket, in, out);
    }

    private final Socket socket;

    private SocketConnection(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        super(in, out);
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void close() throws IOException {
        try {
            super.close();
        } finally {
            socket.close();
        }
    }
}