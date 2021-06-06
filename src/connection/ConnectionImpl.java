package connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;

public class ConnectionImpl implements Connection {
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    public ConnectionImpl(ObjectInputStream in, ObjectOutputStream out) {
        this.in = Objects.requireNonNull(in);
        this.out = Objects.requireNonNull(out);
    }

    public Message receive() throws IOException {
        synchronized (in) {
            try {
                return (Message) in.readObject();
            } catch (ClassNotFoundException | ClassCastException e) {
                throw new IOException(e);
            }
        }
    }

    public void send(Message msg) throws IOException {
        synchronized (out) {
            out.writeObject(msg);
            out.flush();
        }
    }

    public void close() throws IOException {
        try {
            in.close();
        } finally {
            out.close();
        }
    }
}

