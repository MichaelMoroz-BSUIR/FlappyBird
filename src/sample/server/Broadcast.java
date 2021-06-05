package sample.server;

public interface Broadcast {
    void broadcast(float message);
    void notiLeftUser(MiniServerReceiver receiver);
    int getUserCount();
}
