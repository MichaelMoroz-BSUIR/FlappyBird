package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    public static Stage stage;
    public static void completeAlert(String message, Alert.AlertType type) {
        alert("Complete!", message, "", type);
    }
    public static void errorAlert(String message) {
        alert("Error", message, "An error occurred during execution.", Alert.AlertType.ERROR);
    }
    public static void alert(String title, String message, String content, Alert.AlertType type) {
        Alert alert = new Alert(type, content);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public static <T extends Controller> T load(String res) {
        try {
            FXMLLoader l = new FXMLLoader();
            l.load(App.class.getClassLoader().getResourceAsStream(res));
            return l.getController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    public void start(Stage s) {
        stage = s;
        MainWindow window = MainWindow.load();
        s.setScene(new Scene(window.getRoot()));
        s.setTitle("Flappy Bird");
        s.getIcons().add(new Image("img/bird/bird_q.png"));
        s.setResizable(false);
        s.sizeToScene();
        s.show();
    }
}