package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class GameObject extends Bounds {
    public Image image;

    public GameObject(Image image, int width, int height) {
        this.image = image;
        this.width = width;
        this.height = height;
    }

    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.drawImage(image, x, y, width, height);
    }
}
