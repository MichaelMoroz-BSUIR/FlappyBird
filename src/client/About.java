package client;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class About implements Controller {
    public static About load() {
        return App.load("fxml/about.fxml");
    }

    @FXML
    private Pane root;

    public Pane getRoot() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}