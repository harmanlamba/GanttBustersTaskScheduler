package visualisation;

import algorithm.AlgorithmBuilder;
import app.App;
import fileio.IIO;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import visualisation.controller.timer.AlgorithmTimer;
import visualisation.controller.MainController;

public class FXApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        //Load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
        MainController controller = new MainController();
        loader.setController(controller);
        Parent root = loader.load();

        //Show stage
        Scene scene = new Scene(root, 1005, 610);
        scene.getStylesheets().add("https://fonts.googleapis.com/css?family=Space+Mono:400,700&display=swap");
        primaryStage.setScene(scene); //total window size
        primaryStage.setResizable(false);
        primaryStage.setTitle("GanttBuster's Schedule");
        primaryStage.getIcons().add(new Image("/images/logo.png"));
        primaryStage.show();

        //On exit, stop program
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });

        //Runs the algorithm in a new thread
        new Thread(() -> {
            AlgorithmTimer.getAlgorithmTimer().start();
            IIO io = App._mainIO;
            io.write(AlgorithmBuilder.getAlgorithmBuilder().getAlgorithm().solveAlgorithm());
        }).start();

    }

}
