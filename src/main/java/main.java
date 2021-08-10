import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("exchange_calculator.fxml")));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.getIcons().add(new Image("currency.png"));
        stage.setTitle("Currency calculator");
        stage.show();
    }
}
