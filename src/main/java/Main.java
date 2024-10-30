import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/WeatherUI.fxml"));
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WeatherUI.fxml"));

            // Set window options
//        primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("Weather Information App");
            primaryStage.setScene(new Scene(root, 1050, 670));
            primaryStage.getScene().getStylesheets().addAll(getClass().getResource("/styles/style.css").toExternalForm());
            primaryStage.setResizable(false);
            primaryStage.sizeToScene();
            primaryStage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
