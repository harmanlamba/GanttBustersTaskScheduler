package visualisation;

import algorithm.Algorithm;
import algorithm.AlgorithmBuilder;
import algorithm.common.utility.AlgorithmType;
import algorithm.sequential.Sequential;
import app.App;
import fileio.IIO;
import fileio.IO;
import graph.Graph;
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



        //Temporary Algorithm Run
            IIO io = App._mainIO;
            Graph graph = new Graph(io.getNodeMap(), io.getEdgeList()); //create graph from nodes and edges
            AlgorithmBuilder algorithmBuilder = new AlgorithmBuilder(AlgorithmType.SEQUENTIAL, graph,
                    io.getNumberOfProcessorsForTask(), io.getNumberOfProcessorsForParallelAlgorithm());

            Algorithm algorithm = algorithmBuilder.getAlgorithm(); //call algorithm graph
            io.write(algorithm.solve()); //write



        //Run algorithm on own thread
    }

    //TODO: Override stop() method to stop algorithm
}
