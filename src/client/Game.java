package client;

import connection.Connection;
import connection.Message;
import flapy.Bird;
import flapy.GameObject;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Deque;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.function.BooleanSupplier;

public class Game implements Controller {

    public static Game load(Connection connection) {
        Game game = App.load("fxml/game.fxml");
        game.init(connection);
        return game;
    }

    @FXML
    private Pane root;
    @FXML
    private Canvas canvas;

    private Connection connection;

    private BooleanSupplier isSoundOn;
    private Runnable onExit;

    public boolean isGameContinue = true;
    private boolean isPaused = false;
    public final int width = 900;
    public final int height = 600;
    public final int spaceX = 200;
    public double score = 0;
    public int max_score_game = 0;
    public final Deque<GameObject> columns = new LinkedList<>();
    public GameObject background1 = new GameObject(new Image("img/game/background900_600.png"), width, height);
    public GameObject background2 = new GameObject(new Image("img/game/background900_600.png"), width, height);
    public GameObject ground1 = new GameObject(new Image("img/game/fg.png"), width, 100);
    public GameObject ground2 = new GameObject(new Image("img/game/fg.png"), width, 100);
    public Image[] birds = {
            new Image("img/bird/bird0.png"),
            new Image("img/bird/bird1.png"),
            new Image("img/bird/bird2.png"),
            new Image("img/bird/bird3.png")};
    public Bird bird = new Bird(birds, 51, 36);
    public Image[] numbers = {
            new Image("img/numbers/0.png"),
            new Image("img/numbers/1.png"),
            new Image("img/numbers/2.png"),
            new Image("img/numbers/3.png"),
            new Image("img/numbers/4.png"),
            new Image("img/numbers/5.png"),
            new Image("img/numbers/6.png"),
            new Image("img/numbers/7.png"),
            new Image("img/numbers/8.png"),
            new Image("img/numbers/9.png")};
    public Image column1 = new Image("img/game/pipeNorth.png");
    public Image column2 = new Image("img/game/pipeSouth.png");
    public float progress = 0;
    public float enemyProgress = 0;

    public URL jump = MainWindow.class.getClassLoader().getResource("img/sounds/wing.wav");
    public URL score_update = MainWindow.class.getClassLoader().getResource("img/sounds/point.wav");
    public URL die = MainWindow.class.getClassLoader().getResource("img/sounds/hit.wav");
    public Media jump_media = new Media(jump.toExternalForm());
    public Media score_update_media = new Media(score_update.toExternalForm());
    public Media die_media = new Media(die.toExternalForm());

    Image game_over = new Image("img/game/game_over.png");
    Image top_score = new Image("img/pause/top.png");
    Image scoreImg = new Image("img/pause/score.png");
    Image pause = new Image("img/pause/pause.png");

    public AnimationTimer timer;
    private Task<Void> task;

    private void installBackground() {
        background1.x = 0;
        background2.x = width;
    }

    private void updateBackground() {
        int bgSpeedX = 1;
        background1.x -= bgSpeedX;
        background2.x -= bgSpeedX;
        if (background2.x <= 0) {
            installBackground();
        }
    }

    private void installGround() {
        ground1.x = 0;
        ground2.x = width;
        ground1.y = height - ground1.height;
        ground2.y = height - ground2.height;
    }

    private void updateGround() {
        int groundSpeedX = 2;
        ground1.x -= groundSpeedX;
        ground2.x -= groundSpeedX;
        if (ground2.x <= 0) {
            installGround();
        }
    }

    private void fillColumns() {
        columns.clear();
        int x0 = 170;
        for (int i = 0; i < 20; i++) {
            x0 += spaceX;
            createColumns(x0);
        }
    }

    private void createColumns(int x) {
        int y = (int) (Math.random() * 130) - 170;
        int spaceY = 150;
        GameObject top_wall = new GameObject(column1, 70, 300);
        GameObject bottom_wall = new GameObject(column2, 70, 300);
        top_wall.x = x;
        bottom_wall.x = x;
        top_wall.y = y;
        bottom_wall.y = top_wall.height + y + spaceY;
        columns.add(top_wall);
        columns.add(bottom_wall);
    }

    private void updateWalls() {
        int groundSpeedX = 2;
        for (GameObject obs : columns) {
            obs.x -= groundSpeedX;
        }
        GameObject first = columns.peekFirst();
        if (first != null && first.x + first.width <= 0) {
            double count = 0.5;
            score += count;
            bird.score += count;
            playSound(score_update_media);
            columns.removeFirst();
            GameObject last = columns.peekLast();
            if (last != null) {
                createColumns(last.x + spaceX);
            }
        }
    }

    private void installBird() {
        bird.x = 70;
        bird.y = 100;
        bird.speedDown = 0;
        bird.angle = 0;
        bird.deltaDown = 0.3F;
    }

    private void updateBird() {
        bird.x = 70;
        bird.update();
    }

    private void checkCollision() {
        if (bird.y < 0 || bird.intersects(ground1)) {
            bird.y -= bird.speedDown;
            isGameContinue = false;
        } else {
            for (GameObject wall : columns) {
                if (wall.x > bird.x + bird.width) {
                    break;
                }
                if (bird.intersects(wall)) {
                    bird.y -= bird.speedDown;
                    isGameContinue = false;
                }
            }
        }
    }

    public void newGame() {
        max_score_game = readMaxScore();
        score = 0;
        bird.score = 0;
        isGameContinue = true;
        progress = 0;
        installBackground();
        installGround();
        fillColumns();
        installBird();
        saveMaxScore();

    }

    public void drawScore(GraphicsContext graphicsContext, int x, int y) {
        int index_score;
        String number = "" + (int) bird.score;
        for (int i = 0; i < number.length(); i++) {
            index_score = number.charAt(i) - '0';
            graphicsContext.drawImage(numbers[index_score], x + i * 19, y, 18, 36);
        }
    }

    private void drawMaxScore(GraphicsContext graphicsContext) {
        max_score_game = Math.max(max_score_game, (int) bird.score);
        int index_max_score;
        String max_number = "" + max_score_game;
        for (int i = 0; i < max_number.length(); i++) {
            index_max_score = max_number.charAt(i) - '0';
            graphicsContext.drawImage(numbers[index_max_score], 460 + i * 19, 424, 18, 36);
        }
    }

    public void drawProgressLine(GraphicsContext graphicsContext, double progress, Color color, int y) {
        if (progress < 900) {
            graphicsContext.setFill(Color.WHITE);
            graphicsContext.fillRect(0, y, 900, 3);
            graphicsContext.setFill(Color.RED);
            graphicsContext.fillRect(0, y, progress, 3);
            drawTriangle(graphicsContext, progress, color, y);
        }
    }

    private void drawTriangle(GraphicsContext graphicsContext, double progress, Color color, int y) {
        if (progress < 900) {
            double firstX = progress;
            double secondX = progress + 6;
            double thirdX = progress - 6;

            int firstY = 10 + y;
            int secondY = 25 + y;
            int thirdY = 25 + y;

            graphicsContext.setFill(color);
            graphicsContext.setStroke(color);
            graphicsContext.setLineWidth(3);

            graphicsContext.strokePolygon(new double[]{firstX, secondX, thirdX},
                    new double[]{firstY, secondY, thirdY}, 3);
        }
    }

    public void update(GraphicsContext graphicsContext) {
        synchronized (Game.this) {
            if (isGameContinue) {
                if (progress < 100) {
                    updateBackground();
                    updateWalls();
                    updateGround();
                    updateBird();
                    checkCollision();
                    draw(graphicsContext);
                    drawProgressLine(graphicsContext, progress, Color.RED, 0);
                    if (connection != null) {
                        drawProgressLine(graphicsContext, enemyProgress, Color.BLUE, 3);
                    }
                    progress += 0.3;
                } else {
                    reportYouWin(graphicsContext);
                }
            } else {
                reportGameOver(graphicsContext);
            }
        }
    }

    private void reportGameOver(GraphicsContext graphicsContext) {
        isGameContinue = false;
        timer.stop();
        reportGameEnd(graphicsContext, game_over);
        playSound(die_media);
    }

    private void reportYouWin(GraphicsContext graphicsContext) {
        isGameContinue = false;
        timer.stop();

        reportGameEnd(graphicsContext, game_over);
    }

    private void reportGameEnd(GraphicsContext graphicsContext, Image game_over) {

        graphicsContext.drawImage(game_over, 285, 195);
        graphicsContext.drawImage(top_score, 380, 440);
        drawMaxScore(graphicsContext);
        graphicsContext.drawImage(scoreImg, 350, 485);
        drawScore(graphicsContext, 460, 469);
        saveMaxScore();
    }

    public void draw(GraphicsContext graphicsContext) {
        background1.draw(graphicsContext);
        background2.draw(graphicsContext);
        for (GameObject wall : columns) {
            wall.draw(graphicsContext);
        }
        ground1.draw(graphicsContext);
        ground2.draw(graphicsContext);

        bird.draw(graphicsContext);
        drawScore(graphicsContext, 50, 30);
    }

    public void saveMaxScore() {
        int max_score_file = readMaxScore();
        if (max_score_game > max_score_file) {
            try {
                PrintWriter printWriter = new PrintWriter("max_score.txt");
                printWriter.print(max_score_game);
                printWriter.close();
            } catch (FileNotFoundException ignored) {}
        }
    }

    public int readMaxScore() {
        int max_score_file = 0;
        try {
            Scanner scanner = new Scanner(new File("max_score.txt"));
            if (scanner.hasNextInt()) {
                max_score_file = scanner.nextInt();
            }
        } catch (FileNotFoundException ignored) {
        }
        return max_score_file;
    }

    public void playSound(Media sound) {
        if (isSoundOn == null || isSoundOn.getAsBoolean()) {
            MediaPlayer player = new MediaPlayer(sound);
            player.play();
        }
    }

    public Pane getRoot() {
        return root;
    }


    public void setIsSoundOn(BooleanSupplier isSoundOn) {
        this.isSoundOn = isSoundOn;
    }

    public void setOnExit(Runnable onExit) {
        this.onExit = onExit;
    }


    public void initialize(URL location, ResourceBundle resources) {
        timer = new AnimationTimer() {
            public void handle(long l) {
                update(canvas.getGraphicsContext2D());
                if (isPaused || !isGameContinue)
                    return;
                if (connection != null) {
                    try {
                        connection.send(new Message(Message.Type.SCORE, progress));
                    } catch (IOException e) {
                        App.errorAlert("Player disconnect");
                        closeConnection();
                    }
                }
            }
        };
        getRoot().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                if (isGameContinue) {
                    bird.up();
                    playSound(jump_media);
                }
            } else if (event.getCode() == KeyCode.ESCAPE) {
                if (isPaused) {
                    exit();
                    return;
                }
                if (isGameContinue) {
                    timer.stop();
                    saveMaxScore();
                    GraphicsContext g = canvas.getGraphicsContext2D();

                    g.drawImage(pause, 285, 195);
                    isPaused = true;
                } else {
                    exit();
                }
            } else if (event.getCode() == KeyCode.ENTER) {
                if (isPaused) {
                    installTimer();
                    isPaused = false;
                }
                if (!isGameContinue) {
                    newGame();
                    timer.stop();
                    installTimer();
                }
            }
        });
        newGame();
        installTimer();
        update(canvas.getGraphicsContext2D());
    }

    private void exit() {
        saveMaxScore();
        closeConnection();
        onExit.run();
    }

    private void init(Connection connection) {
        this.connection = connection;
        if (connection != null) {
            task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while (!isCancelled()) {
                        try {
                            Message msg = connection.receive();
                            if (msg == null)
                                continue;
                            synchronized (Game.this) {
                                switch (msg.getType()) {
                                    case SCORE:
                                        enemyProgress = (float) msg.get();
//                                        drawProgressLine(canvas.getGraphicsContext2D(), (float) msg.get(), Color.BLUE, 3);
                                        break;
                                }
                            }
                        } catch (IOException e) {
                            cancel();
                            break;
                        }
                    }
                    return null;
                }
            };
            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
        }
    }

    private void closeConnection() {
        if (connection != null) {
            task.cancel();
            try {
                connection.close();
            } catch (IOException ignored) {

            }
            connection = null;
        }
    }

    public void installTimer() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
            int i = 3;

            @Override
            public void handle(ActionEvent event) {
                GraphicsContext g = canvas.getGraphicsContext2D();
                draw(g);
                g.drawImage(numbers[i], 435, 250, 30, 60);
                i--;
                drawProgressLine(canvas.getGraphicsContext2D(), progress, Color.RED, 0);
                if (connection != null) {
                    drawProgressLine(canvas.getGraphicsContext2D(), enemyProgress, Color.BLUE, 3);
                }
            }
        }));
        timeline.setCycleCount(4);
        timeline.setOnFinished(actionEvent -> timer.start());
        timeline.play();
    }
}