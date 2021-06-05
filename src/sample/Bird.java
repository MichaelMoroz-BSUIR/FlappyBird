package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Bird extends Bounds {
    public float deltaDown = 0.3F;//ускорение
    public float speedDown;//скорость
    public float angle;//угол наклона
    public double score = 0;

    private final Image[] birds;
    private double index = 0;

    public Bird(Image[] birds, int width, int height) {
        this.birds = birds;
        this.width = width;
        this.height = height;
    }

    public void up() {
        deltaDown = 2F;
        speedDown = -7.0F;
        angle = -5;
    }

    public void update() {
        if (speedDown == -3) {
            deltaDown = 0.3F;
        }
        y += speedDown;
        speedDown += deltaDown;
        angle += 1.0F;//увеличиваем угол
        if (angle < 5) {
            index += 0.35;//next Image
            if (index >= birds.length) {
                index = 0;
            }
        } else {
            index = 0;
        }
    }

    public void draw(GraphicsContext graphicsContext) {
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        int centerX = x + halfWidth;
        int centerY = y + halfHeight;
        graphicsContext.translate(centerX, centerY);
        graphicsContext.rotate(angle);
        graphicsContext.drawImage(birds[(int) index], -halfWidth, -halfHeight, width, height);
        graphicsContext.rotate(-angle);
        graphicsContext.translate(-centerX, -centerY);
    }
}
