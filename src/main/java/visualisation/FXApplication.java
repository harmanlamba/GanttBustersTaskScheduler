package visualisation;

import algorithm.Algorithm;
import algorithm.AlgorithmBuilder;
import app.App;
import fileio.IIO;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import visualisation.controller.timer.AlgorithmTimer;
import visualisation.controller.MainController;

import java.util.concurrent.TimeUnit;

public class FXApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        //Load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));


        //Run algorithm on own thread
        MainController controller = new MainController();
        loader.setController(controller);
        Parent root = loader.load();

        //Show stage
        primaryStage.setTitle("Visualization screen");
        primaryStage.setScene(new Scene(root, 990, 590)); //total window size
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });

        System.out.println("Start");
        AlgorithmTimer.getAlgorithmTimer().start();
        Algorithm algorithm = AlgorithmBuilder.getAlgorithmBuilder().getAlgorithm();
        IIO io = App._mainIO;
        //Runs the algorithm in a new thread
        new Thread(() -> io.write(AlgorithmBuilder.getAlgorithmBuilder().getAlgorithm().solveAlgorithm())).start();
        System.out.println("Finish");




    }

    //TODO: Override stop() method to stop algorithm
}
