package client;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindow implements Controller {
    public static MainWindow load() {
        return App.load("fxml/main_window.fxml");
    }

    @FXML
    private VBox root;

    private Pane active = new Pane();

    private void setActive(Controller controller) {
        ObservableList<Node> c = getRoot().getChildren();
        c.remove(active);
        active = controller.getRoot();
        c.add(active);
    }

    public Pane getRoot() {
        return root;
    }

    public void initialize(URL location, ResourceBundle resources) {
        Menu menu = Menu.load();
        menu.getGameImg().setOnMouseClicked(event -> {
            Game game = Game.load(null);
            game.setIsSoundOn(menu::isSoundOn);
            game.setOnExit(() -> setActive(menu));
            setActive(game);
        });
        menu.getOnlineImg().setOnMouseClicked(event -> {
            Stage s = new Stage();
            Connector connector = Connector.load();
            connector.setOnSuccess(connection -> {
                s.hide();
                Game game = Game.load(connection);
                game.setIsSoundOn(menu::isSoundOn);
                game.setOnExit(() -> setActive(menu));
                setActive(game);
            });
            s.setScene(new Scene(connector.getRoot()));
            s.setTitle("Flappy Bird");
            s.getIcons().add(new Image("img/bird/bird_q.png"));
            s.initModality(Modality.APPLICATION_MODAL);
            s.setResizable(false);
            s.sizeToScene();
            s.showAndWait();
        });
        menu.getAboutImg().setOnMouseClicked(event -> {
            Stage s = new Stage();
            About about = About.load();
            s.setScene(new Scene(about.getRoot()));
            s.setTitle("Flappy Bird");
            s.getIcons().add(new Image("img/bird/bird_q.png"));
            s.initModality(Modality.APPLICATION_MODAL);
            s.setResizable(false);
            s.sizeToScene();
            s.showAndWait();
        });
        menu.getExitImg().setOnMouseClicked(event -> {
            App.stage.close();
        });
        setActive(menu);
    }
}