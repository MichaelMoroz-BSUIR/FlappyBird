package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class Game {
    public static boolean isGameContinue = true;
    public static boolean isSoundOn = false;
    public static final int width = 900;
    public static final int height = 600;
    public static final int spaceX = 200;
    public static double score = 0;
    public static int max_score_game = 0;
    public static final Deque<GameObject> columns = new LinkedList<>();
    public static GameObject background1 = new GameObject(new Image("game/background900_600.png"), width, height);
    public static GameObject background2 = new GameObject(new Image("game/background900_600.png"), width, height);
    public static GameObject ground1 = new GameObject(new Image("game/fg.png"), width, 100);
    public static GameObject ground2 = new GameObject(new Image("game/fg.png"), width, 100);
    public static Image[] birds = {new Image("bird/bird0.png"), new Image("bird/bird1.png"),
            new Image("bird/bird2.png"), new Image("bird/bird3.png")};
    public static Bird bird = new Bird(birds, 51, 36);
    public static Image[] numbers = {new Image("numbers/0.png"), new Image("numbers/1.png"), new Image("numbers/2.png"),
            new Image("numbers/3.png"), new Image("numbers/4.png"), new Image("numbers/5.png"),
            new Image("numbers/6.png"), new Image("numbers/7.png"), new Image("numbers/8.png"),
            new Image("numbers/9.png")};
    public Image column1 = new Image("game/pipeNorth.png");
    public Image column2 = new Image("game/pipeSouth.png");
    public static float progress = 0;

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
            if (isSoundOn) {
                Controller.playSound(Controller.score_update_media);
            }
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

    public void drawProgressLine(GraphicsContext graphicsContext, boolean isTimer) {
        if (progress < 900) {
            graphicsContext.setFill(Color.WHITE);
            graphicsContext.fillRect(0, 0, 900, 3);
            graphicsContext.setFill(Color.RED);
            graphicsContext.fillRect(0, 0, progress, 3);
            drawTriangle(graphicsContext);
            if (!isTimer) {
                progress += 0.3;
            }
        }
    }

    private void drawTriangle(GraphicsContext graphicsContext) {
        if (progress < 900) {
            double firstX = progress;
            double secondX = progress + 6;
            double thirdX = progress - 6;

            int firstY = 10;
            int secondY = 25;
            int thirdY = 25;

            graphicsContext.setFill(Color.YELLOW);
            graphicsContext.setStroke(Color.YELLOW);
            graphicsContext.setLineWidth(3);
            /*graphicsContext.fillPolygon(new double[]{firstX, secondX, thirdX},
                    new double[]{firstY, secondY, thirdY}, 3);*/
            graphicsContext.strokePolygon(new double[]{firstX, secondX, thirdX},
                    new double[]{firstY, secondY, thirdY}, 3);
        }
    }

    public void update(GraphicsContext graphicsContext) {
        if (isGameContinue) {
            if (progress < 100) {
                updateBackground();
                updateWalls();
                updateGround();
                updateBird();
                checkCollision();
                draw(graphicsContext);
                drawProgressLine(graphicsContext, false);
            } else {
                reportYouWin(graphicsContext);
            }
        } else {
            reportGameOver(graphicsContext);
        }
    }

    private void reportGameOver(GraphicsContext graphicsContext) {
        isGameContinue = false;
        Image game_over = new Image("game/game_over.png");
        reportGameEnd(graphicsContext, game_over);
        if (isSoundOn) {
            Controller.playSound(Controller.die_media);
        }

    }

    private void reportYouWin(GraphicsContext graphicsContext) {
        isGameContinue = false;
        Image game_over = new Image("game/you_win.png");
        reportGameEnd(graphicsContext, game_over);
    }

    private void reportGameEnd(GraphicsContext graphicsContext, Image game_over) {
        Image top_score = new Image("pause/top.png");
        Image score = new Image("pause/score.png");
        graphicsContext.drawImage(game_over, 285, 195);
        graphicsContext.drawImage(top_score, 380, 440);
        drawMaxScore(graphicsContext);
        graphicsContext.drawImage(score, 350, 485);
        drawScore(graphicsContext, 460, 469);
        saveMaxScore();
        Controller.animationTimer.stop();
    }

    void draw(GraphicsContext graphicsContext) {
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

    public static void saveMaxScore() {
        int max_score_file = readMaxScore();
        if (max_score_game > max_score_file) {
            try {
                PrintWriter printWriter = new PrintWriter("max_score.txt");
                printWriter.print(max_score_game);
                printWriter.close();
            } catch (FileNotFoundException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    public static int readMaxScore() {
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
}
