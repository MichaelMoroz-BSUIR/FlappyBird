package sample;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.client.MiniClient;
import sample.launcher.MiniChatClientLauncher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Objects;

public class Controller {
    public static Game game = new Game();
    public static AnimationTimer animationTimer;
    public static URL jump = Controller.class.getClassLoader().getResource("sounds/wing.wav");
    public static URL score_update = Controller.class.getClassLoader().getResource("sounds/point.wav");
    public static URL die = Controller.class.getClassLoader().getResource("sounds/hit.wav");
    public static Media jump_media = null;
    public static Media score_update_media = null;
    public static Media die_media = null;
    public static Stage about = null;
    public static Stage connect = null;
    public static boolean isAboutOpen = true;
    public static boolean isOnlineGame = true;

    @FXML
    private Canvas canvas;

    @FXML
    private Pane gamePane;

    @FXML
    private Pane menuPane;

    @FXML
    private Pane pausePane;

    @FXML
    private ImageView imageViewPlay;

    @FXML
    private ImageView imageViewAbout;

    @FXML
    private ImageView imageViewExit;

    @FXML
    private ImageView imageViewSound;

    @FXML
    private ImageView imageViewOnline;

    @FXML
    void handlePauseKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            selectPane(gamePane);
            if (Game.isGameContinue) {
                installTimer();
            }
        } else if (event.getCode() == KeyCode.ESCAPE) {
            selectPane(menuPane);
        }
    }

    @FXML
    void startGame() {
        animationTimer.start();
        selectPane(gamePane);
        game.newGame();
        isOnlineGame = true;
    }

    @FXML
    void viewAbout() throws IOException {
        if (isAboutOpen) {
            isAboutOpen = false;
            about = new Stage();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("about.fxml")));
            about.setTitle("Flappy Bird");
            about.getIcons().add(new Image("bird/bird_q.png"));
            about.setScene(new Scene(root));
            about.setResizable(false);
            about.sizeToScene();
            about.show();
            about.setOnCloseRequest(event -> isAboutOpen = true);
        }
    }

    @FXML
    void viewConnectDialog() throws IOException {
        isOnlineGame = false;
        connect = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("connect.fxml"));
        Parent parent = loader.load();
        connect.getIcons().add(new Image("bird/bird_q.png"));
        connect.setScene(new Scene(parent));
        connect.setResizable(false);
        connect.sizeToScene();
        connect.show();
    }

    @FXML
    void exit() {
        Main.stage.close();
        if (about != null) {
            about.close();
        }
    }

    @FXML
    void handleKeyPressGame(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            if (Game.isGameContinue) {
                Game.bird.up();
                if (Game.isSoundOn) {
                    playSound(jump_media);
                }
            }
        } else if (event.getCode() == KeyCode.ESCAPE) {
            if (Game.isGameContinue) {
                selectPane(pausePane);
                gamePane.setVisible(true);
                animationTimer.stop();
                Game.saveMaxScore();
            } else {
                selectPane(menuPane);
            }
        } else if (event.getCode() == KeyCode.ENTER) {
            selectPane(gamePane);

            animationTimer.stop();
            installTimer();
            game.newGame();
        }
    }

    @FXML
    private TextField ipTextField;

    @FXML
    private TextField portTextField;

    @FXML
    private Button connectButton;

    @FXML
    void connect() {
        startGame();
    }

    @FXML
    void initialize() {
        turnOnSound();
        playAnimationImageView(imageViewPlay, new Image("menu/play148_56.png"), new Image("menu/play148_56nc.png"));
        playAnimationImageView(imageViewAbout, new Image("menu/about184_56.png"), new Image("menu/about184_56nc.png"));
        playAnimationImageView(imageViewExit, new Image("menu/exit132_56.png"), new Image("menu/exit132_56nc.png"));
        playAnimationImageView(imageViewOnline, new Image("menu/online192_56.png"), new Image("menu/online192_56nc.png"));
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                game.update(canvas.getGraphicsContext2D());
                /*if (isOnlineGame) {
                    try {
                        send(Game.progress);
                    } catch (IOException e) {
                        animationTimer.stop();
                    }
                }*/
            }
        };
        selectPane(menuPane);
    }

    Socket socket;
    InputStream in;
    OutputStream os;

    void create() {
        try {
            socket = new Socket("192.168.100.8", 5001);
            in = socket.getInputStream();
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double receiveProgress() throws IOException {
        long bits = 0;
        for (int i = 0; i < 8; i++)
            bits = (bits << 8) | (in.read() & 0xFF);
        return Double.longBitsToDouble(bits);
    }

    private void send(double progress) throws IOException {
        long bits = Double.doubleToLongBits(progress);
        for (int i = 0; i < 8; i++)
            os.write((int) ((bits >>> i) & 0xFF));
        os.flush();
    }

    private void installTimer() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
            int i = 3;

            @Override
            public void handle(ActionEvent event) {
                GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
                game.draw(graphicsContext2D);
                graphicsContext2D.drawImage(Game.numbers[i], 435, 250, 30, 60);
                game.drawProgressLine(graphicsContext2D, true);
                i--;
            }
        }));
        timeline.setCycleCount(4);
        timeline.setOnFinished(actionEvent -> animationTimer.start());
        timeline.play();
    }

    private void playAnimationImageView(ImageView imageView, Image first, Image second) {
        imageView.setOnMouseMoved(mouseEvent -> imageView.setImage(first));
        imageView.setOnMouseExited(mouseEvent -> imageView.setImage(second));
    }

    private void turnOnSound() {
        imageViewSound.setOnMouseClicked(mouseEvent -> {
            if (!Game.isSoundOn) {
                imageViewSound.setImage(new Image("menu/sound_on.png"));
                Game.isSoundOn = true;
            } else {
                imageViewSound.setImage(new Image("menu/sound_off.png"));
                Game.isSoundOn = false;
            }
        });
    }

    public static void playSound(Media sound) {
        MediaPlayer player = new MediaPlayer(sound);
        player.play();
    }

    private Pane pane = new Pane();

    private void selectPane(Pane newPane) {
        pane.setDisable(true);
        pane.setVisible(false);

        newPane.setDisable(false);
        newPane.setVisible(true);

        pane = newPane;
    }

    /*private static final boolean def = false;

    private Socket socket;
    private ObjectOutputStream objectStream = null;
    private Scanner scanner;

    private boolean isAlive;
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
            port = 5000;
        } else {
            System.out.println("Please enter the server address to connect.");
            System.out.print(">>");
            ipAddress = scanner.next();
            System.out.println("Please enter the port number of the server to connect.");
            System.out.print(">>");
            port = scanner.nextInt();
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
        String nick = scanner.next();
        Protocol p = new Protocol(nick);
        System.out.println("Start chatting. Enter \"exit\" to exit");
        while (isAlive) {

            p.setMessage();

            objectStream.writeObject(p);
            objectStream.reset();
            objectStream.flush();

            if (message.equals("exit")) {
                isAlive = false;
            }
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
    }*/
}
