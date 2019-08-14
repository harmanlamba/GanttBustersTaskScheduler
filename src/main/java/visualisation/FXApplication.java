package visualisation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXApplication extends Application {

    //Reference controller
    //TODO: Get statistics monitor

    @Override
    public void start(Stage primaryStage) throws Exception{

        //Load FXML
        Parent root = FXMLLoader.load(getClass().getResource("/view/main.fxml"));

        //Show stage
        primaryStage.setTitle("Visualization screen");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.show();

        //Run algorithm on own thread
    }

    //TODO: Override stop() method to stop algorithm
}
