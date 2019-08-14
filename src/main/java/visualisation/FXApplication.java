package visualisation;

import algorithm.Algorithm;
import algorithm.sequential.Sequential;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import visualisation.controller.MainController;

public class FXApplication extends Application {

    //Reference controller
    //TODO: Get statistics monitor

    @Override
    public void start(Stage primaryStage) throws Exception{

        //Testing for Algorithm

        //Load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
        MainController controller = new MainController();
        loader.setController(controller);
        Parent root = loader.load();
        //Show stage
        primaryStage.setTitle("Visualization screen");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.show();

        //Run algorithm on own thread
    }

    //TODO: Override stop() method to stop algorithm
}
