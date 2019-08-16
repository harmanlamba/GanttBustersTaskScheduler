package visualisation;

import algorithm.Algorithm;
import algorithm.AlgorithmBuilder;
import app.App;
import fileio.IIO;
import graph.Graph;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import visualisation.controller.MainController;

public class FXApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        //Load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
        IIO io = App._mainIO;
        Graph graph = new Graph(io.getNodeMap(), io.getEdgeList()); //create graph from nodes and edges
        Algorithm algorithm = AlgorithmBuilder.getAlgorithm(graph, io.getNumberOfProcessorsForTask(), io.getNumberOfProcessorsForParallelAlgorithm());  //call algorithm graph
        io.write(algorithm.solve());

        //Run algorithm on own thread
        MainController controller = new MainController(algorithm, io);
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
    }

    //TODO: Override stop() method to stop algorithm
}
