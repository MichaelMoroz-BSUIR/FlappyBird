package sample.launcher;

import sample.server.MiniServer;

public class MiniChatServerLauncher {
    public static void main(String[] args) {
        MiniServer server = new MiniServer();
        server.start(5001);
    }
}
