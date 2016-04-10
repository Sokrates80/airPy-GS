package airpygs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("./resources/stylesheets/airPyGS.fxml"));
        primaryStage.setTitle("airPy Ground Station");
        primaryStage.setScene(new Scene(root, 900, 500));
        //primaryStage.getIcons().add(new Image("file:resources/img/airpy_logo.png"));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
