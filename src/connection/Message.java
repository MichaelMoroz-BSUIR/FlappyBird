package connection;

import java.io.*;
import java.util.Objects;

public class Message implements Externalizable {
    public enum Type {
        SCORE

    }
    private Type type;
    private Serializable s;

    public Message() {
    }
    public Message(Type type) {
        this(type, null);
    }
    public Message(Type type, Serializable s) {
        this.type = Objects.requireNonNull(type);
        this.s = s;
    }

    public Type getType() {
        return type;
    }
    public Serializable get() {
        return s;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(type);
        out.writeObject(s);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        type = (Type) in.readObject();
        s = (Serializable) in.readObject();
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", s=" + s +
                '}';
    }
}