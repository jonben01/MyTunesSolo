package dk.easv.Jonas_MyTunesSolo;

//JAVA IMPORTS
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/views/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("MyTunesSOLO");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/fox.png"))));
        stage.show();
        //have not tried making anything resizable yet, so I just wont.
        stage.setResizable(false);


    }

    public static void main(String[] args) {
        launch();
    }
}