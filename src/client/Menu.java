package client;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class Menu implements Controller {
    public static Menu load() {
        return App.load("fxml/menu.fxml");
    }

    @FXML
    private Pane root;
    @FXML
    private ImageView gameImg, onlineImg, aboutImg, exitImg, songImg;

    public Pane getRoot() {
        return root;
    }
    public ImageView getGameImg() {
        return gameImg;
    }
    public ImageView getOnlineImg() {
        return onlineImg;
    }
    public ImageView getAboutImg() {
        return aboutImg;
    }
    public ImageView getExitImg() {
        return exitImg;
    }
    public ImageView getSongImg() {
        return songImg;
    }

    public boolean isSoundOn() {
        return isSoundOn;
    }

    private boolean isSoundOn;
    public void turnOnSound() {
        songImg.setOnMouseClicked(mouseEvent -> {
            songImg.setImage(isSoundOn ?
                    new Image("img/menu/sound_off.png") :
                    new Image("img/menu/sound_on.png"));
            isSoundOn = !isSoundOn;
        });
    }

    private void playAnimationImageView(ImageView imageView, Image first, Image second) {
        imageView.setOnMouseMoved(mouseEvent -> imageView.setImage(first));
        imageView.setOnMouseExited(mouseEvent -> imageView.setImage(second));
    }

    public void initialize(URL location, ResourceBundle resources) {
        getGameImg().setImage(new Image("img/menu/play148_56.png"));

        playAnimationImageView(gameImg,
                new Image("img/menu/play148_56.png"),
                new Image("img/menu/play148_56nc.png"));
        playAnimationImageView(aboutImg,
                new Image("img/menu/about184_56.png"),
                new Image("img/menu/about184_56nc.png"));
        playAnimationImageView(exitImg,
                new Image("img/menu/exit132_56.png"),
                new Image("img/menu/exit132_56nc.png"));
        playAnimationImageView(onlineImg,
                new Image("img/menu/online192_56.png"),
                new Image("img/menu/online192_56nc.png"));
        songImg.setOnMouseClicked(event -> turnOnSound());
        turnOnSound();
    }
}