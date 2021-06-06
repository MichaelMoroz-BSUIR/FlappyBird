package client;

import connection.Connection;
import connection.Message;
import connection.SocketConnection;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class Connector implements Controller, Closeable {
    public static Connector load() {
        return App.load("fxml/connector.fxml");
    }

    @FXML
    private Pane root;
    @FXML
    private TextField ipTF, portTF;
    @FXML
    private Button create, submit;
    @FXML
    private Label state;

    private Consumer<Connection> onSuccess;

    public Pane getRoot() {
        return root;
    }

    public void setOnSuccess(Consumer<Connection> consumer) {
        this.onSuccess = consumer;
    }

    public void initialize(URL location, ResourceBundle resources) {
        submit.setOnAction(e -> submit());
        create.setOnAction(e -> create());
    }

    private Task<Void> task;

    private void submit() {
        if (onSuccess == null)
            return;
        try {
            String s = portTF.getText().trim();
            if (s.isEmpty())
                return;
            String i = ipTF.getText().trim();
            if (i.isEmpty())
                return;
            int p = Integer.parseInt(s);
            state.setText("Connecting...");
            close();
            task = new Task<Void>() {
                @Override
                protected Void call() {
                    Platform.runLater(() -> {
                        try {
                            Socket socket = new Socket(i, p);
                            if (isCancelled())
                                return;
                            onSuccess.accept(SocketConnection.client(socket));
                        } catch (IOException e) {
                        }
                    });
                    return null;
                }
            };
            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
        } catch (NumberFormatException e) {
            App.errorAlert("Illegal port characters");
        }
    }

    private void create() {
        try {
            String s = portTF.getText().trim();
            if (s.isEmpty())
                return;
            int p = Integer.parseInt(s);
            ServerSocket serverSocket = new ServerSocket(p, 2, InetAddress.getLocalHost());
            ipTF.setText(serverSocket.getInetAddress().getHostAddress());
            state.setText("Wait client...");
            close();
            task = new Task<Void>() {

                @Override
                public boolean cancel(boolean mayInterruptIfRunning) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                    }
                    return super.cancel(mayInterruptIfRunning);
                }

                @Override
                protected Void call() throws Exception {
                    try {
                        SocketConnection back = SocketConnection.server(serverSocket.accept());
                        Connection connection = new Connection() {
                            public Message receive() throws IOException {
                                return back.receive();
                            }

                            public void send(Message msg) throws IOException {
                                back.send(msg);
                            }

                            public void close() throws IOException {
                                try {
                                    back.close();
                                } finally {
                                    serverSocket.close();
                                }
                            }
                        };
                        Platform.runLater(() -> onSuccess.accept(connection));
                    } catch (IOException e) {
                        App.errorAlert(e.getMessage());
                        serverSocket.close();
                    }
                    return null;
                }
            };
            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
        } catch (NumberFormatException e) {
            App.errorAlert("Illegal port characters");
        } catch (IOException e) {
            App.errorAlert(e.getMessage());
        }
    }

    @Override
    public void close() {
        if (task != null)
            task.cancel();
    }
}