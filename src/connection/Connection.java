package connection;

import java.io.Closeable;
import java.io.IOException;

public interface Connection extends Closeable {
    Message receive() throws IOException;
    void send(Message msg) throws IOException;
}